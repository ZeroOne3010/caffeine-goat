package io.github.zeroone3010.caffeinegoat.api

import io.github.zeroone3010.caffeinegoat.dto.ReviewDto
import io.github.zeroone3010.caffeinegoat.entity.Drink
import io.github.zeroone3010.caffeinegoat.entity.Review
import io.github.zeroone3010.caffeinegoat.entity.User
import io.quarkus.qute.Template
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/review")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ReviewResource {

    @Inject
    lateinit var reviews: Template

    @GET
    @Transactional
    fun listAll() = Review.listAll().map { ReviewDto.from(it) }

    @GET
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    fun listLatestAsHtml(@HeaderParam("hx-fragment") isFragment: Boolean): String {
        val template = if (isFragment) reviews.getFragment("reviews") else reviews
        return template.data("reviews", Review.newestPaged(0, 5)).render()
    }

    @POST
    @Transactional
    fun addReview(reviewDto: ReviewDto): Response {
        val user = User.findById(reviewDto.reviewerId)
            ?: return Response.status(Response.Status.BAD_REQUEST).entity("User not found.").build()

        val product = Drink.findById(reviewDto.drinkId)
            ?: return Response.status(Response.Status.BAD_REQUEST).entity("Drink not found.").build()

        val review = Review().apply {
            reviewer = user
            drink = product
            rating = reviewDto.rating
            comment = reviewDto.comment
        }
        review.persist()

        return Response.status(Response.Status.CREATED).entity(ReviewDto.from(review)).build()
    }

    @GET
    @Transactional
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long) = ReviewDto.from(Review.findById(id))

    @PUT
    @Transactional
    @Path("/{id}")
    fun update(@PathParam("id") id: Long, entity: Review): Response {
        return Response.serverError().entity("Not implemented").build()
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long): Response {
        val entity = Review.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        entity.delete()
        return Response.noContent().build()
    }
}
