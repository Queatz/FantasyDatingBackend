package com.queatz.fantasydating.util

import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

class Validate constructor(private val on: On) {
    suspend fun respond(call: ApplicationCall) = when {
        on<Me>().person.invited.not() -> {
            call.respond(HttpStatusCode.Unauthorized)
            true
        }
        else -> false
    }
}