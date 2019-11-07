package com.queatz.fantasydating.routes

import com.queatz.fantasydating.Arango
import com.queatz.fantasydating.Json
import com.queatz.fantasydating.MeDiscoveryPreferencesRequest
import com.queatz.fantasydating.util.Me
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

class MeDiscoveryPreferencesRoute constructor(private val on: On) {
    suspend fun get(call: ApplicationCall) {
        call.respond(on<Me>().discoveryPreferences)
    }

    suspend fun post(call: ApplicationCall) {
        val discoveryPreferences = on<Me>().discoveryPreferences

        on<Json>().from(call, MeDiscoveryPreferencesRequest::class).apply {
            who?.let { discoveryPreferences.who = it }
            where?.let { discoveryPreferences.where = it }
            ageMin?.let { discoveryPreferences.ageMin = it }
            ageMax?.let { discoveryPreferences.ageMax = it }
        }

        call.respond(on<Arango>().save(discoveryPreferences) ?: HttpStatusCode.InternalServerError)
    }
}
