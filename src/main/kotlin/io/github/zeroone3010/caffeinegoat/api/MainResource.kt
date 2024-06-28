package io.github.zeroone3010.caffeinegoat.api

import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.TEXT_HTML)
class MainResource {

    @Inject
    lateinit var home: Template

    @GET
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    fun homeAsHtml(): String {
        return home.render()
    }
}
