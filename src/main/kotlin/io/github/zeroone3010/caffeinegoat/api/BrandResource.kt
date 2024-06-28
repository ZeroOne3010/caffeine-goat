package io.github.zeroone3010.caffeinegoat.api

import io.github.zeroone3010.caffeinegoat.entity.Brand
import io.github.zeroone3010.caffeinegoat.entity.Drink
import io.quarkus.panache.common.Sort
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/brand")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class BrandResource {

    @Inject
    lateinit var brands: Template

    @Inject
    lateinit var brand: Template

    @GET
    @Transactional
    fun listAll() = Drink.listAll()

    @GET
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    fun listAllAsHtml(@HeaderParam("hx-fragment") isFragment: Boolean): String {
        val template = if (isFragment) brands.getFragment("brands") else brands
        return template.data("brands", Brand.listAll(Sort.by("name"))).render()
    }

    @POST
    @Transactional
    fun add(entity: Brand): Response {
        entity.persist()
        return Response.status(Response.Status.CREATED).entity(entity).build()
    }

    @GET
    @Transactional
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    fun getByIdAsHtml(@PathParam("id") id: Long): String {
        return brand.data("brand", Brand.findById(id), "drinks", Drink.findByBrand(id)).render()
    }
}
