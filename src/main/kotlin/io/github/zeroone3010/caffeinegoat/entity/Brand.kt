package io.github.zeroone3010.caffeinegoat.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["name"], name = "unique_brand_name")
    ]
)
class Brand : PanacheEntity() {
    @Column(nullable = false)
    lateinit var name: String

    companion object : PanacheCompanion<Brand>
}
