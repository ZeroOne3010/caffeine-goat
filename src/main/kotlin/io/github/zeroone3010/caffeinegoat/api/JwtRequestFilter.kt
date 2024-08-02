package io.github.zeroone3010.caffeinegoat.api

import io.github.zeroone3010.caffeinegoat.api.LoginResource.Companion.LOGGED_IN_TOKEN_NAME
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.PreMatching
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import jakarta.ws.rs.ext.Provider
import java.security.Principal

@Provider
@PreMatching
class JwtRequestFilter(private val jwtUtils: JwtUtils) :
    ContainerRequestFilter {

    override fun filter(requestContext: ContainerRequestContext) {
        val method = requestContext.method
        val cookies = requestContext.cookies
        val token = cookies[LOGGED_IN_TOKEN_NAME]?.value

        val isValid = jwtUtils.validateToken(token)
        if (isValid) {
            val userPrincipal = jwtUtils.getUserPrincipalFromToken(token!!)
            requestContext.securityContext = object : SecurityContext {
                override fun getUserPrincipal(): Principal = userPrincipal
                override fun isUserInRole(role: String): Boolean = userPrincipal.roles.contains(role)
                override fun isSecure(): Boolean = requestContext.securityContext.isSecure
                override fun getAuthenticationScheme(): String = "Bearer"
            }
        }
        // Check if the endpoint is public (e.g., annotated with @PermitAll)
        val isPublic = requestContext.uriInfo.path.startsWith("/login")
        if (!isPublic && method != "GET") {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build())
        }
    }
}
