package com.thedearbear.nnov.api.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BoardNoticesResponse(
    val total: Any,
    @Json(name = "total_unread") val totalUnread: Int?,
    val count: Int,
    val notices: List<BoardNoticesResponseNotice>?
)

@JsonClass(generateAdapter = true)
data class BoardNoticesResponseNotice(
    @Json(name = "short_text") val shortText: String,
    @Json(name = "users_to") val usersTo: String,
    @Json(name = "user_from") val userFrom: BoardNoticesResponseFrom,
    @Json(name = "user_from_string") val userFromString: String,
    val id: String,
    val subject: String,
    val date: String,
    @Json(name = "date_actual_before") val dateActualBefore: String,
    val unread: Boolean,
    @Json(name = "with_files") val withFiles: Boolean,
    @Json(name = "with_resources") val withResources: Boolean
)

@JsonClass(generateAdapter = true)
data class BoardNoticesResponseFrom(
    val name: String,
    val lastname: String,
    val firstname: String,
    val middlename: String
)
