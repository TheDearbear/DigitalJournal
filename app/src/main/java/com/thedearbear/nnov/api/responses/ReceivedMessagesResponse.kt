package com.thedearbear.nnov.api.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReceivedMessagesResponse(
    val total: String,
    @Json(name = "total_unread") val totalUnread: Int?,
    val count: Int,
    val messages: List<ReceivedMessagesResponseMessage>?
)

@JsonClass(generateAdapter = true)
data class ReceivedMessagesResponseMessage(
    @Json(name = "short_text") val shortText: String,
    @Json(name = "user_from") val userFrom: ReceivedMessagesResponseUser,
    val id: String,
    val subject: String,
    val date: String,
    val unread: Boolean,
    @Json(name = "with_files") val withFiles: Boolean,
    @Json(name = "with_resources") val withResources: Boolean
)

@JsonClass(generateAdapter = true)
data class ReceivedMessagesResponseUser(
    val name: String,
    val lastname: String,
    val firstname: String,
    val middlename: String
)
