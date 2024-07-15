package com.thedearbear.nnov.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val state: Int = 0,
    val error: String? = null,
    val result: T
)
