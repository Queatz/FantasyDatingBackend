package com.queatz.fantasydating.routes

import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.fantasydating.util.Validate
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

class MePeopleRoute constructor(private val on: On) {
    suspend fun get(call: ApplicationCall) {
        if (on<Validate>().respond(call)) return

        val discoveryPreferences = on<Me>().discoveryPreferences
        call.respond(on<Db>().getPeople(
            on<Me>().person.id!!,
            discoveryPreferences.who,
            discoveryPreferences.ageMin,
            discoveryPreferences.ageMax
        ))
    }
}
