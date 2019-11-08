package com.queatz.fantasydating.routes

import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

class MeEventsRoute constructor(private val on: On) {
    suspend fun get(call: ApplicationCall) {
        call.respond(on<Db>().getEvents(on<Me>().person.id!!))
    }
}
