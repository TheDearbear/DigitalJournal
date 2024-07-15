package com.thedearbear.nnov.api.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageResponse(
    val message: MessageResponseMessage
)

@JsonClass(generateAdapter = true)
data class MessageResponseMessage(
    val text: String,
    @Json(name = "short_text") val shortText: String,
    @Json(name = "user_from") val userFrom: MessageResponseUser,
    @Json(name = "user_to") val userTo: List<MessageResponseReceiverUser>,
    val id: String,
    val subject: String,
    val date: String,
    val files: List<MessageResponseFile>?,
    val resources: List<MessageResponseResource>?
)

@JsonClass(generateAdapter = true)
data class MessageResponseReceiverUser(
    val name: String,
    val lastname: String,
    val firstname: String,
    val middlename: String,
    val unread: Boolean?
)

@JsonClass(generateAdapter = true)
data class MessageResponseUser(
    val name: String,
    val lastname: String,
    val firstname: String,
    val middlename: String
)

@JsonClass(generateAdapter = true)
data class MessageResponseFile(
    val filename: String,
    val link: String
)

@JsonClass(generateAdapter = true)
data class MessageResponseResource(
    val filename: String,
    val description: String,
    val link: String
)
