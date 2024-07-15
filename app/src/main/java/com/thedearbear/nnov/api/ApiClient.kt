package com.thedearbear.nnov.api

import android.icu.util.DateInterval
import com.squareup.moshi.Moshi
import com.thedearbear.nnov.api.adapters.DiaryDayAdapter
import com.thedearbear.nnov.api.adapters.RelationGroupsAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

data class ApiClient(
    var devKey: String,
    var server: String,
    var authToken: String? = null,
    var vendor: String = ApiConstants.DEFAULT_VENDOR,
) {
    private val userAgent: String = "DigitalJournal/1.0"
    private var client: OkHttpClient = OkHttpClient()

    val moshi: Moshi = Moshi.Builder()
        .add(DiaryDayAdapter())
        .add(RelationGroupsAdapter())
        .build()

    inline fun <reified T> runRequest(
        call: Call,
        crossinline onSuccess: (T) -> Unit,
        crossinline onAuthFailure: (String) -> Unit,
        crossinline onFailure: (IOException) -> Unit
    ) {
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val adapter = moshi.adapter<ApiAnswer<T>>(typeOf<ApiAnswer<T>>().javaType)

                val parsed: T

                response.use {
                    val apiResponse = adapter.fromJson(response.body!!.source())!!.response

                    if (response.isSuccessful.not()) {
                        onAuthFailure(apiResponse.error!!)
                        return
                    }

                    parsed = apiResponse.result
                }

                onSuccess(parsed)
            }
        })
    }

    fun auth(login: String, password: String): Call {
        val url = createUrlBuilder("apiv3/auth").build()

        val body = FormBody.Builder()
            .add("login", login)
            .add("password", password)
            .build()

        val request = createRequestBuilder(url)
            .post(body)
            .build()

        return client.newCall(request)
    }

    fun getRules(): Call {
        val url = createUrlBuilder("apiv3/getrules").build()

        val request = createRequestBuilder(url)
            .get()
            .build()

        return client.newCall(request)
    }

    fun getUserInfo(id: UInt): Call {
        val url = createUrlBuilder("apiv3/getprofileuserinfo")
            .addQueryParameter("student", id.toString())
            .build()

        val request = createRequestBuilder(url)
            .get()
            .build()

        return client.newCall(request)
    }

    fun getDiary(
        id: UInt?,
        days: DateInterval?,
        rings: Boolean = false
    ): Call {
        val url = createUrlBuilder("apiv3/getdiary")
            .addQueryParameter("rings", rings.toString())
            .also { builder ->
                if (id != null) builder.addQueryParameter("id", id.toString())
            }
            .also { builder ->
                if (days != null) {
                    val startDate = LocalDate.ofEpochDay(days.fromDate)
                        .format(DateTimeFormatter.BASIC_ISO_DATE)
                    val endDate = LocalDate.ofEpochDay(days.toDate)
                        .format(DateTimeFormatter.BASIC_ISO_DATE)

                    builder.addQueryParameter("days", "$startDate-$endDate")
                }
            }
            .build()

        val request = createRequestBuilder(url)
            .get()
            .build()

        return client.newCall(request)
    }

    fun getBoardNotices(): Call {
        val url = createUrlBuilder("apiv3/getboardnotices")
            .build()

        val request = createRequestBuilder(url)
            .get()
            .build()

        return client.newCall(request)
    }

    fun getBoardNoticeInfo(id: Int): Call {
        val url = createUrlBuilder("apiv3/getboardnoticeinfo")
            .addQueryParameter("id", id.toString())
            .build()

        val request = createRequestBuilder(url)
            .get()
            .build()

        return client.newCall(request)
    }

    fun getMessages(folder: String? = null): Call {
        val url = createUrlBuilder("apiv3/getmessages")
            .also {
                if (folder != null) it.addQueryParameter("folder", folder)
            }
            .build()

        val request = createRequestBuilder(url)
            .get()
            .build()

        return client.newCall(request)
    }

    fun getMessageInfo(id: Int): Call {
        val url = createUrlBuilder("apiv3/getmessageinfo")
            .addQueryParameter("id", id.toString())
            .build()

        val request = createRequestBuilder(url)
            .get()
            .build()

        return client.newCall(request)
    }

    private fun createRequestBuilder(url: HttpUrl): Request.Builder {
        return Request.Builder()
            .addHeader("User-Agent", userAgent)
            .url(url)
    }

    private fun createUrlBuilder(path: String): HttpUrl.Builder {
        val builder = HttpUrl.Builder()
            .scheme("https")
            .host(server)
            .addEncodedPathSegments(path)
            .addQueryParameter("vendor", vendor)
            .addQueryParameter("devkey", devKey)
            .addQueryParameter("out_format", "json")

        if (authToken != null) {
            return builder.addQueryParameter("auth_token", authToken)
        }

        return builder
    }

    override fun toString(): String {
        return "ApiClient(authToken='$authToken', devKey='$devKey', vendor='$vendor')"
    }
}