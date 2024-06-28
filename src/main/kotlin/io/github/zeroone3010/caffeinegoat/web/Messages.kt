package io.github.zeroone3010.caffeinegoat.web

import io.quarkus.qute.i18n.Message
import io.quarkus.qute.i18n.MessageBundle

@MessageBundle
interface Messages {
    @Message
    fun home(): String

    // DRINKS

    @Message
    fun drinks_title(): String

    @Message
    fun drinks_no_drinks(): String

    @Message
    fun drinks_caffeine(): String

    // REVIEWS

    @Message
    fun reviews_title(): String

    @Message
    fun latest_reviews(): String

    // USERS

    @Message
    fun users_title(): String

    @Message
    fun users_no_users(): String

    // BRANDS

    @Message
    fun brands_title(): String

    @Message
    fun brands_no_brands(): String
}
