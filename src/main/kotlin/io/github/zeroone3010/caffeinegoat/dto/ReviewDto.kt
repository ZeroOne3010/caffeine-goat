package io.github.zeroone3010.caffeinegoat.dto

import io.github.zeroone3010.caffeinegoat.entity.Review

data class ReviewDto(
    val id: Long?,
    val drinkId: Long,
    val reviewerId: Long,
    val rating: Int,
    val comment: String?
) {

    companion object {
        fun from(review: Review?): ReviewDto? {
            if (review == null) {
                return null
            }
            return ReviewDto(
                id = review.id!!,
                reviewerId = review.reviewer.id!!,
                drinkId = review.drink.id!!,
                rating = review.rating,
                comment = review.comment
            )
        }
    }
}
