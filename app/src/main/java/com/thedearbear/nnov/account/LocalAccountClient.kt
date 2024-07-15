package com.thedearbear.nnov.account

import com.thedearbear.nnov.api.ApiClient
import java.time.ZonedDateTime

class LocalAccountClient(
    val account: LocalAccount,
    val client: ApiClient
) {
    constructor(account: LocalAccount) : this(account, ApiClient(
        account.key,
        account.baseAddress,
        account.token,
        account.vendor
    ))

    fun isValid(): Boolean {
        return account.id != -1
    }

    fun updateToken(token: String, expires: ZonedDateTime?) {
        account.token = token
        account.expires = expires

        client.authToken = token
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is LocalAccountClient &&
                other.account.id == account.id
    }

    override fun hashCode(): Int {
        return 31 * account.hashCode() + client.hashCode()
    }

    companion object {
        val default: LocalAccountClient
            get() = LocalAccountClient(
                LocalAccount(-1, "", null, "", "", "", ""),
                ApiClient("", "")
            )
    }
}