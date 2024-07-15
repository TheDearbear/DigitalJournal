package com.thedearbear.nnov.ui.state

import java.time.LocalDateTime

data class AccountEditState(
    var token: String = "",
    var expire: LocalDateTime? = null,

    var server: String = "",
    var devKey: String = "",
    var vendor: String = ""
)