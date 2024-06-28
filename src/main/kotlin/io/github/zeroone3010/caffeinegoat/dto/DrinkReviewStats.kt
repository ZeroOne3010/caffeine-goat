package io.github.zeroone3010.caffeinegoat.dto

data class DrinkReviewStats(
    val drinkId: Long,
    val drinkName: String,
    val averageRating: Int,
    val numberOfReviews: Long
)
