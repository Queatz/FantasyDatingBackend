package com.queatz.fantasydating.routes

import com.queatz.fantasydating.SuccessResponse
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

class MeDeleteRoute constructor(private val on: On) {
    suspend fun post(call: ApplicationCall) {
        call.respond(SuccessResponse(true))
    }
}
