package com.thedearbear.nnov.api.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RulesResponse(
    val roles: List<String> = listOf(),
    val relations: RulesResponseRelations = RulesResponseRelations(),
    val allowedAds: List<String> = listOf(),
    val allowedSections: List<String> = listOf(),
    val messageSignature: String = "",
    val uploadFileUrl: String = "",
    val supportPhone: String = "",
    val supportEmail: String = "",
    val supportHelpdesk: String = "",
    val supportDescription: String = "",
    val name: String = "",
    val age: Int = 0,
    val id: String = "",
    val vuid: String = "",
    @Json(name = "id_hash") val idHash: String = "",
    val title: String = "",
    val vendor: String = "",
    @Json(name = "vendor_id") val vendorId: Int = 0,
    val guid: String = "",
    val lastname: String = "",
    val firstname: String = "",
    val middlename: String = "",
    val gender: String = "",
    val email: String = "",
    @Json(name = "email_confirmed") val emailConfirmed: Boolean = false,
    val region: String = "",
    val regionCode: String = "",
    val city: String = "",
    @Json(name = "rt_licey_school_end_date") val rtLiceySchoolEndDate: Boolean = false
)

@JsonClass(generateAdapter = true)
data class RulesResponseRelations(
    val students: Map<String, RulesResponseStudentRelations> = mapOf(),
    val groups: Map<String, RulesResponseGroupRelations> = mapOf(),
    val schools: List<RulesResponseSchoolRelations> = listOf()
)

@JsonClass(generateAdapter = true)
data class RulesResponseStudentRelations(
    val rules: List<String>,
    val rel: String,
    val name: String,
    val title: String,
    val lastname: String,
    val firstname: String,
    val gender: String,
    @Json(name = "class") val clazz: String,
    val parallel: Any,
    val city: String
)

@JsonClass(generateAdapter = true)
data class RulesResponseGroupRelations(
    val rules: List<String>,
    val rel: String,
    val name: String,
    val parallel: Int,
    val balls: Int,
    @Json(name = "hometeacher_id") val homeTeacherId: String,
    @Json(name = "hometeacher_name") val homeTeacherName: String,
    @Json(name = "hometeacher_lastname") val homeTeacherLastname: String,
    @Json(name = "hometeacher_firstname") val homeTeacherFirstname: String,
    @Json(name = "hometeacher_middlename") val homeTeacherMiddlename: String
)

@JsonClass(generateAdapter = true)
data class RulesResponseSchoolRelations(
    val number: String,
    val title: String,
    @Json(name = "title_full") val titleFull: String
)
