package io.github.zeroone3010.caffeinegoat.api

import io.github.zeroone3010.caffeinegoat.entity.User
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.*
import org.jboss.resteasy.reactive.RestForm
import java.net.URI
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.random.Random


@Path("/login")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
class LoginResource(private val jwtUtils: JwtUtils) {

    companion object {
        const val LOGGED_IN_TOKEN_NAME = "loggedInToken"
        const val TEMP_USERNAME_PREFIX = "temp+"

        val AFTER_LOGIN_PATH: URI = URI("/")
        val SETUP_USERNAME_PATH: URI = URI("/login/setupUsername")
        val RANDOM: Random = Random.Default
        val LOGIN_TOKEN_VALIDITY_PERIOD: Duration = Duration.of(15L, ChronoUnit.MINUTES)
        val LOGIN_VALIDITY_PERIOD: Duration = Duration.of(24L * 30L, ChronoUnit.HOURS)
    }

    @Inject
    lateinit var login: Template

    @Inject
    lateinit var loginCode: Template

    @Inject
    lateinit var setupUsername: Template

    @Context
    private lateinit var securityContext: SecurityContext

    @GET
    @Transactional
    fun login(): String {
        return login.render()
    }

    @POST
    @Path("/init")
    @Transactional
    fun initLogin(@RestForm email: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(email.trim().lowercase(Locale.ENGLISH).toByteArray(Charsets.UTF_8))
        val emailHash = hashBytes.joinToString("") { "%02x".format(it) }
        val user = User.findByEmailHash(emailHash) ?: User().apply {
            this.name = TEMP_USERNAME_PREFIX + UUID.randomUUID().toString()
            this.emailHash = emailHash
            this.persist()
        }
        user.loginToken = RANDOM.nextInt(100000, 999999).toString()
        user.loginTokenExpiryTime = Instant.now().plus(LOGIN_TOKEN_VALIDITY_PERIOD)
        return loginCode.data("userId", user.id).render()
    }

    @POST
    @Path("/code")
    @Transactional
    fun confirmCode(@RestForm userId: Long, @RestForm code: String): Response {
        User.findById(userId)?.let {
            if (it.loginToken == code && it.loginTokenExpiryTime?.isAfter(Instant.now()) == true) {
                val firstLogin = it.name.startsWith(TEMP_USERNAME_PREFIX)

                it.loginToken = null
                it.loginTokenExpiryTime = null
                it.lastLogin = Instant.now()
                it.persist()

                val cookie = createJwtCookieForUser(it)

                if (firstLogin) {
                    return Response.seeOther(SETUP_USERNAME_PATH).cookie(cookie).build()
                }

                return Response.seeOther(AFTER_LOGIN_PATH).cookie(cookie).build()
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build()
    }

    @POST
    @Path("/setupUsername")
    @Transactional
    fun setupUsername(@RestForm username: String): Response {
        val userPrincipal = securityContext.userPrincipal as UserPrincipal
        User.findById(userPrincipal.userId)?.let {
            it.name = username
            it.persist()
            val cookie = createJwtCookieForUser(it)
            return Response.seeOther(AFTER_LOGIN_PATH).cookie(cookie).build()
        }
        return Response.status(Response.Status.UNAUTHORIZED).build()
    }

    private fun createJwtCookieForUser(user: User): NewCookie {
        val jwt = jwtUtils.generateToken(user)
        return NewCookie.Builder(LOGGED_IN_TOKEN_NAME).value(jwt).path("/")
            .maxAge(LOGIN_VALIDITY_PERIOD.seconds.toInt()).build()
    }

    @GET
    @Path("/setupUsername")
    @Transactional
    fun setupUsername(@Context securityContext: SecurityContext): String {
        return setupUsername.render()
    }
}
