package io.github.zeroone3010.caffeinegoat.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import io.quarkus.panache.common.Sort
import jakarta.persistence.*

@Entity
class Drink : PanacheEntity() {
    @Column
    lateinit var name: String

    @ManyToOne(optional = false)
    var brand: Brand = Brand()

    @Column(nullable = true)
    var caffeineContentMgPerLitre: Int? = null

    fun caffeineContentMgPer100ml(): Int? {
        return caffeineContentMgPerLitre?.div(10)
    }

    companion object : PanacheCompanion<Drink> {
        fun findByBrand(brandId: Long): List<Drink> {
            return list("brand.id", Sort.by("name"), brandId)
        }
    }
}
