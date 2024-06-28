package io.github.zeroone3010.caffeinegoat.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "goat")
class User : PanacheEntity() {
    var name: String = ""

    companion object: PanacheCompanion<User>
}
