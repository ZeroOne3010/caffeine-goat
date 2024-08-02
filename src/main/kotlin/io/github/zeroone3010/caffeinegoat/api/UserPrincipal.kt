package io.github.zeroone3010.caffeinegoat.api

import java.security.Principal

data class UserPrincipal(val userId: Long, val username: String, val roles: Set<String>, val emailHash: String) :
    Principal {
    override fun getName(): String = username
}
