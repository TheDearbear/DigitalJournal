package com.thedearbear.nnov.api.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryResponse(
    val students: Map<String, DiaryResponseStudent> = mapOf()
)

@JsonClass(generateAdapter = true)
data class DiaryResponseStudent(
    val name: String,
    val title: String,
    val days: Map<String, DiaryResponseDay>
)

@JsonClass(generateAdapter = true)
data class DiaryResponseDay(
    val name: String,
    val title: String,
    val alert: String?,
    @Json(name = "holiday_name") val holidayName: String?,
    val items: Map<String, DiaryResponseLesson>?,
    @Json(name = "items_extday") val itemsExtDay: List<DiaryResponseExtendedDayLesson>?
)

@JsonClass(generateAdapter = true)
data class DiaryResponseLesson(
    val assessments: List<DiaryResponseAssessments>?,
    val homework: Map<String, DiaryResponseHomework>,
    val files: List<DiaryResponseFile>,
    val resources: List<*>,
    val name: String,
    @Json(name = "lesson_id") val lessonId: String,
    val num: String,
    val room: String,
    val teacher: String,
    val sort: Int,
    @Json(name = "grp_short") val groupShort: String?,
    @Json(name = "grp") val group: String?,
    @Json(name = "starttime") val startTime: String?,
    @Json(name = "endtime") val endTime: String?
)

@JsonClass(generateAdapter = true)
data class DiaryResponseExtendedDayLesson(
    val name: String,
    val topic: String,
    val teacher: String,
    val sort: Int,
    @Json(name = "grp_short") val groupShort: String?,
    @Json(name = "grp") val group: String?,
    @Json(name = "starttime") val startTime: String?,
    @Json(name = "endtime") val endTime: String?
)

@JsonClass(generateAdapter = true)
data class DiaryResponseAssessments(
    val value: String,
    @Json(name = "countas") val countAs: String,
    @Json(name = "color_hex") val colorHex: String?,
    val count: Boolean,
    val convert: Int,
    @Json(name = "lesson_id") val lessonId: String,
    val date: String,
    val nm: String,
    val comment: String
)

@JsonClass(generateAdapter = true)
data class DiaryResponseHomework(
    val value: String,
    val id: Double,
    val individual: Boolean
)

@JsonClass(generateAdapter = true)
data class DiaryResponseFile(
    @Json(name = "toid") val toId: Double,
    val filename: String,
    val link: String
)
