package io.github.zeroone3010.caffeinegoat.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "goat", uniqueConstraints = [
        UniqueConstraint(columnNames = ["name"]),
        UniqueConstraint(columnNames = ["emailHash"])
    ]
)
class User : PanacheEntity() {
    var name: String = ""

    var emailHash: String = ""

    var loginToken: String? = null

    var loginTokenExpiryTime: Instant? = null

    var lastLogin: Instant? = null

    companion object : PanacheCompanion<User> {
        fun findByEmailHash(emailHash: String): User? {
            val userFound = list("emailHash", emailHash)
            if (userFound.isEmpty()) {
                return null
            }
            return userFound.first()
        }
    }
}
