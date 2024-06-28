package io.github.zeroone3010.caffeinegoat.repository

import io.github.zeroone3010.caffeinegoat.dto.DrinkReviewStats
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class DrinkReviewStatsRepository : PanacheRepository<DrinkReviewStats> {
    @Transactional
    fun findHighestRatedDrinks(): List<DrinkReviewStats> {
        val query = getEntityManager().createQuery("""
            SELECT new io.github.zeroone3010.caffeinegoat.dto.DrinkReviewStats(
                d.id, 
                d.name, 
                CAST(AVG(r.rating) AS INTEGER), 
                COUNT(r))
            FROM Drink d 
            JOIN Review r ON r.drink = d
            GROUP BY d 
            ORDER BY AVG(r.rating) DESC
        """, DrinkReviewStats::class.java)
        return query.resultList.toList()
    }
}
