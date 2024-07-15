package com.thedearbear.nnov.ui.state

data class LoginState(
    var login: String = "",
    var password: String = "",

    var token: String = "",
    var useToken: Boolean = false,

    var server: String = "",
    var devKey: String = "",
    var vendor: String = "",

    var state: String = ""
)