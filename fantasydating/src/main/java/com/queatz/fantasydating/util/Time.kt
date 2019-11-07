package com.queatz.fantasydating.util

import com.queatz.on.On
import java.time.Instant
import java.util.*

class Time constructor(private val on: On) {

    fun now(): Instant = Instant.now()

    init {
        System.setProperty("user.timezone", "UTC")
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }
}
