package com.queatz.fantasydating.routes

import com.queatz.fantasydating.*
import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

class PersonMessagesRoute constructor(private val on: On) {
    suspend fun get(call: ApplicationCall) {
        val person = call.parameters["id"]!!

        call.respond(on<Db>().getMessages(on<Me>().person.id!!, person))
    }

    suspend fun post(call: ApplicationCall) {
        val person = call.parameters["id"]!!
        if (on<Db>().isPersonHiddenForPerson(on<Me>().person.id!!, person)) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        if (on<Db>().isBothPeopleLoveEachOther(on<Me>().person.id!!, person).not()) {
            call.respond(SuccessResponse(false))
            return
        }

        on<Json>().from(call, MessageRequest::class).apply {
            val message = Message(
                from = on<Me>().person.id!!,
                to = person,
                message = message,
                attachment = attachment
            )

            call.respond(SuccessResponse(on<Arango>().save(message) != null))
        }
    }
}
