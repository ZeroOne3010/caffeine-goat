package io.github.zeroone3010.caffeinegoat.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["reviewer_id", "drink_id"])
    ]
)
class Review : PanacheEntity() {
    @ManyToOne
    var drink: Drink = Drink()

    @ManyToOne
    var reviewer: User = User()

    @Column(nullable = false)
    var rating: Int = 0

    @Column(length = 2048)
    var comment: String? = null

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    var createdAt: ZonedDateTime? = null

    @UpdateTimestamp
    @Column(nullable = false, name = "modified_at")
    var modifiedAt: ZonedDateTime? = null

    companion object : PanacheCompanion<Review> {
        fun newestPaged(pageIndex: Int, pageSize: Int): List<Review> {
            return find("order by createdAt desc").page(pageIndex, pageSize).list()
        }

        fun newestPagedByReviewer(reviewerId: Long, pageIndex: Int, pageSize: Int): List<Review> {
            return find("where reviewer.id = ${reviewerId} order by createdAt desc").page(pageIndex, pageSize).list()
        }
    }
}
