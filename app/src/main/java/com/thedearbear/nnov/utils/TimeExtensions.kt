package com.thedearbear.nnov.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun LocalDate.toDisplayString(
    locale: Locale = Locale.getDefault(),
    hideYear: Boolean = false
): String {
    val dayOfWeek = dayOfWeek.getDisplayName(TextStyle.FULL, locale)
        .replaceFirstChar {
            if (it.isLowerCase()) {
                it.titlecase(locale)
            } else {
                it.toString()
            }
        }

    return "%s, %02d.%02d".format(
        dayOfWeek,
        monthValue,
        dayOfMonth
    ).let { date ->
        if (hideYear.not() && LocalDate.now().year != year) {
            "$date.$year"
        } else {
            date
        }
    }
}

fun LocalDateTime.toDisplayString(
    locale: Locale = Locale.getDefault(),
    hideYear: Boolean = false
): String {
    val dayOfWeek = dayOfWeek.getDisplayName(TextStyle.FULL, locale)
        .replaceFirstChar {
            if (it.isLowerCase()) {
                it.titlecase(locale)
            } else {
                it.toString()
            }
        }

    return "%s, %02d.%02d".format(
        dayOfWeek,
        monthValue,
        dayOfMonth
    ).let { date ->
        if (hideYear.not() && LocalDate.now().year != year) {
            "$date.$year"
        } else {
            date
        }
    }.let { date -> "$date ${format(DateTimeFormatter.ISO_LOCAL_TIME)}" }
}
