package io.github.zeroone3010.caffeinegoat.api

import io.github.zeroone3010.caffeinegoat.entity.Review
import io.github.zeroone3010.caffeinegoat.entity.User
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class UserResource {

    @Inject
    lateinit var user: Template

    @Inject
    lateinit var users: Template

    @GET
    @Transactional
    fun listAll() = User.listAll()

    @GET
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    fun listAllAsHtml(): String {
        return users.data("users", User.listAll()).render()
    }

    @POST
    @Transactional
    fun add(entity: User): Response {
        entity.persist()
        return Response.status(Response.Status.CREATED).entity(entity).build()
    }

    @GET
    @Transactional
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long) = User.findById(id)

    @GET
    @Transactional
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    fun getByIdAsHtml(@PathParam("id") userId: Long): String {
        return user.data("user", User.findById(userId), "reviews", Review.newestPagedByReviewer(userId, 0, 5))
            .render()
    }

    @PUT
    @Transactional
    @Path("/{id}")
    fun update(@PathParam("id") id: Long, entity: User): Response {
        return Response.serverError().entity("Not implemented").build()
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long): Response {
        val entity = User.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        entity.delete()
        return Response.ok().build()
    }
}
