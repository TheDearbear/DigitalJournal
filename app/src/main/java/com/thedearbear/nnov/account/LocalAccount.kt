package com.thedearbear.nnov.account

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity
data class LocalAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var token: String,
    var expires: ZonedDateTime?,
    var baseAddress: String,
    var key: String,
    var vendor: String,
    var name: String
)
