package com.queatz.fantasydating.routes

import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

class MeFeedRoute constructor(private val on: On) {
    suspend fun get(call: ApplicationCall) {
        call.respond("feed")
    }

}