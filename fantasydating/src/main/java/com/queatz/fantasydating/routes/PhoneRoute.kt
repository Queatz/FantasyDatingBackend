package com.queatz.fantasydating.routes

import com.queatz.fantasydating.Json
import com.queatz.fantasydating.PhoneRequest
import com.queatz.fantasydating.SuccessResponse
import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

class PhoneRoute constructor(private val on: On) {
    suspend fun post(call: ApplicationCall) {
        val person = on<Me>().person

        on<Json>().from(call, PhoneRequest::class).apply {
            call.respond(SuccessResponse(on<Db>().setPhoneToken(person.id!!, token ?: "") != null))
        }
    }
}