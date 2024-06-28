package io.github.zeroone3010.caffeinegoat.api

import io.github.zeroone3010.caffeinegoat.dto.DrinkReviewStats
import io.github.zeroone3010.caffeinegoat.entity.Drink
import io.github.zeroone3010.caffeinegoat.repository.DrinkReviewStatsRepository
import io.quarkus.panache.common.Sort
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/drink")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class DrinkResource {

    @Inject
    lateinit var drinkReviewStatsRepository: DrinkReviewStatsRepository

    @Inject
    lateinit var drink: Template

    @Inject
    lateinit var drinks: Template

    @GET
    @Transactional
    fun listAll() = Drink.listAll()

    @GET
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    fun listAllAsHtml(): String {
        return drinks.data("drinks", Drink.listAll(Sort.by("name"))).render()
    }

    @POST
    @Transactional
    fun add(entity: Drink): Response {
        entity.persist()
        return Response.status(Response.Status.CREATED).entity(entity).build()
    }

    @GET
    @Transactional
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long) = Drink.findById(id)

    @GET
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    @Path("/{id}")
    fun getByIdAsHtml(@PathParam("id") id: Long): String {
        return drink.data("drink", Drink.findById(id)).render()
    }

    @PUT
    @Transactional
    @Path("/{id}")
    fun update(@PathParam("id") id: Long, updated: Drink): Response {

        val entity = Drink.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        entity.name = updated.name
        entity.brand = updated.brand
        entity.caffeineContentMgPerLitre = updated.caffeineContentMgPerLitre
        entity.persist()
        return Response.ok(entity).build()
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long): Response {
        val entity = Drink.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        entity.delete()
        return Response.ok().build()
    }

    @GET
    @Transactional
    @Path("/top-rated")
    fun getTopRatedDrinks(): List<DrinkReviewStats> {
        return drinkReviewStatsRepository.findHighestRatedDrinks()
    }
}
