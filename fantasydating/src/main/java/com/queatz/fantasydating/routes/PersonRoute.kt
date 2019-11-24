package com.queatz.fantasydating.routes

import com.queatz.fantasydating.*
import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

class PersonRoute constructor(private val on: On) {

    suspend fun get(call: ApplicationCall) {
        val person = call.parameters["id"]!!

        if (on<Db>().isPersonHiddenForPerson(on<Me>().person.id!!, person)) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        call.respond(on<Db>().getById(person, Person::class) ?: HttpStatusCode.NotFound)
    }

    suspend fun post(call: ApplicationCall) {
        if (on<Db>().isPersonHiddenForPerson(on<Me>().person.id!!, call.parameters["id"]!!)) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        val person = on<Db>().getById(call.parameters["id"]!!, Person::class)

        if (person == null) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        on<Json>().from(call, PersonRequest::class).apply {
            when {
                love != null -> {
                    if (love == true) {
                        call.respond(SuccessResponse(on<Db>().love(on<Me>().person.id!!, person.id!!) != null))
                    } else {
                        call.respond(SuccessResponse(false))
                    }
                }
                report != null -> {
                    if (report == true) {
                        val report = Report(
                            person = person.id!!,
                            reporter = on<Me>().person.id!!,
                            report = message ?: "Generic report"
                        )

                        call.respond(SuccessResponse(on<Arango>().save(report) != null))
                    } else {
                        call.respond(SuccessResponse(false))
                    }
                }
                hide != null -> {
                    if (hide == true) {
                        call.respond(SuccessResponse(on<Db>().hide(on<Me>().person.id!!, person.id!!) != null))
                    } else {
                        call.respond(SuccessResponse(false))
                    }
                }
            }
        }
    }
}
