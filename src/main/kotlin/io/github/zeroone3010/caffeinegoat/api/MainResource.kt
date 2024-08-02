package io.github.zeroone3010.caffeinegoat.api

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext

@Path("/")
@Produces(MediaType.TEXT_HTML)
class MainResource {

    @Inject
    lateinit var home: Template

    @Inject
    @Location("partials/loginLink.html")
    lateinit var loginLink: Template

    @Context
    private lateinit var securityContext: SecurityContext

    @GET
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    fun homeAsHtml(): String {
        return home.render()
    }

    @GET
    @Path("/loginLink")
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    fun loginLinkAsHtml(): String {
        val userPrincipal = securityContext.userPrincipal as? UserPrincipal
        return loginLink.data(
            "username", userPrincipal?.username,
            "emailHash", userPrincipal?.emailHash
        ).render()
    }
}
