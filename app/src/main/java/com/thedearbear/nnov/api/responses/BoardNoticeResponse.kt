package com.thedearbear.nnov.api.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BoardNoticeResponse(
    val notice: BoardNoticeResponseNotice
)

@JsonClass(generateAdapter = true)
data class BoardNoticeResponseNotice(
    val text: String,
    @Json(name = "short_text") val shortText: String,
    @Json(name = "users_to") val usersTo: String,
    @Json(name = "user_from") val userFrom: BoardNoticeResponseUser,
    @Json(name = "user_from_string") val userFromString: String,
    val id: String,
    val subject: String,
    val date: String,
    @Json(name = "date_actual_before") val dateActualBefore: String,
    val files: List<BoardNoticeResponseFile>?,
    val resources: List<BoardNoticeResponseResource>?
)

@JsonClass(generateAdapter = true)
data class BoardNoticeResponseUser(
    val name: String,
    val lastname: String,
    val firstname: String,
    val middlename: String
)

@JsonClass(generateAdapter = true)
data class BoardNoticeResponseFile(
    val filename: String,
    val link: String
)

@JsonClass(generateAdapter = true)
data class BoardNoticeResponseResource(
    val filename: String,
    val description: String,
    val link: String
)
