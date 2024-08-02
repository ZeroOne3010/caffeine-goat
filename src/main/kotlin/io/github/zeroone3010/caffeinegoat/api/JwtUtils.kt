package io.github.zeroone3010.caffeinegoat.api

import io.github.zeroone3010.caffeinegoat.api.LoginResource.Companion.LOGIN_VALIDITY_PERIOD
import io.github.zeroone3010.caffeinegoat.entity.User
import io.smallrye.jwt.auth.principal.DefaultJWTParser
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo
import io.smallrye.jwt.auth.principal.JWTParser
import io.smallrye.jwt.auth.principal.ParseException
import io.smallrye.jwt.build.Jwt
import io.smallrye.jwt.util.KeyUtils.readPublicKey
import jakarta.enterprise.context.ApplicationScoped
import jakarta.json.JsonNumber
import org.bouncycastle.util.io.pem.PemReader
import org.eclipse.microprofile.jwt.Claims
import java.io.InputStreamReader
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Instant

private const val TOKEN_ISSUER = "CaffeineGoat"

const val REGISTERED_USER_ROLE = "registeredUser"

@ApplicationScoped
class JwtUtils {

    private val privateKey: PrivateKey = readPrivateKey("/testPrivateKey.pem")
    private val publicKey: PublicKey = readPublicKey("/testPublicKey.pem")
    private val jwtAuthContextInfo: JWTAuthContextInfo = JWTAuthContextInfo(publicKey, TOKEN_ISSUER)
    private val jwtParser: JWTParser = DefaultJWTParser(jwtAuthContextInfo)

    fun generateToken(user: User): String {
        return Jwt.claims()
            .claim(Claims.groups.name, REGISTERED_USER_ROLE)
            .claim("userId", user.id)
            .claim("emailHash", user.emailHash)
            .claim(Claims.preferred_username.name, user.name)
            .issuer(TOKEN_ISSUER)
            .expiresAt(Instant.now().plus(LOGIN_VALIDITY_PERIOD))
            .jws()
            .sign(privateKey)
    }

    fun validateToken(token: String?): Boolean {
        if (token == null) {
            return false
        }

        try {
            val jwt = jwtParser.parse(token)
            val userId = jwt.getClaim<JsonNumber>("userId").longValue()
            val expirationTime = jwt.expirationTime
            // We could verify that the user exists in the database, but that seems to lead down a complex rabbit hole
            // of asynchronous operations and trouble in the downstream, so let's not do that right now.
            return expirationTime > Instant.now().epochSecond && userId > 0
        } catch (e: ParseException) {
            return false
        }
    }

    fun getUserPrincipalFromToken(token: String): UserPrincipal {
        val jwtContext = jwtParser.parse(token, jwtAuthContextInfo)
        val userId = jwtContext.getClaim<JsonNumber>("userId").longValue()
        val username = jwtContext.getClaim(Claims.preferred_username.name) as String?
        val emailHash = jwtContext.getClaim("emailHash") as String?
        val roles = jwtContext.getClaim(Claims.groups.name) as Set<String>
        return UserPrincipal(userId, username ?: "newbie", roles, emailHash ?: "")
    }

    private fun readPrivateKey(resourcePath: String): PrivateKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val keyContent =
            PemReader(InputStreamReader(this::class.java.getResourceAsStream(resourcePath))).use { it.readPemObject().content }
        val keySpec = PKCS8EncodedKeySpec(keyContent)
        return keyFactory.generatePrivate(keySpec)
    }
}
