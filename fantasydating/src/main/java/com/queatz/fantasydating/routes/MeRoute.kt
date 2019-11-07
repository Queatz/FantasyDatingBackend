package com.queatz.fantasydating.routes

import com.queatz.fantasydating.Arango
import com.queatz.fantasydating.Json
import com.queatz.fantasydating.MeRequest
import com.queatz.fantasydating.util.Me
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

class MeRoute constructor(private val on: On) {
    suspend fun get(call: ApplicationCall) {
        call.respond(on<Me>().person)
    }

    suspend fun post(call: ApplicationCall) {
        val person = on<Me>().person

        on<Json>().from(call, MeRequest::class).apply {
            name?.let { person.name = it }
            sex?.let { person.sex = it }
            age?.let { person.age = it }
            fantasy?.let { person.fantasy = it }
            stories?.let { person.stories = it }
            active?.let { person.active = it }
        }

        call.respond(on<Arango>().save(person) ?: HttpStatusCode.InternalServerError)
    }
}