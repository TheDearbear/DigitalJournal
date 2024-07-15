package com.thedearbear.nnov.api.responses

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val token: String,
    val expires: String
)
