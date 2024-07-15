package com.thedearbear.nnov

import com.thedearbear.nnov.account.LocalAccountClient

object Singleton {
    var selectedAccount = LocalAccountClient.default
}