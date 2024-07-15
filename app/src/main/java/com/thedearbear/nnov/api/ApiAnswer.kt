package com.thedearbear.nnov.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiAnswer<T>(
    val response: ApiResponse<T>
)
