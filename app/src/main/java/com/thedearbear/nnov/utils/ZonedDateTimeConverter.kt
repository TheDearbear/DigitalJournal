package com.thedearbear.nnov.utils

import androidx.room.TypeConverter
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

class ZonedDateTimeConverter {
    @TypeConverter
    fun fromString(value: String?): ZonedDateTime? {
        return try {
            if (value != null) {
                ZonedDateTime.parse(value)
            } else {
                null
            }
        } catch (e: DateTimeParseException) {
            null
        }
    }

    @TypeConverter
    fun fromZonedDateTime(value: ZonedDateTime?): String? {
        if (value == null) {
            return null
        }

        return value.toString()
    }
}