package com.queatz.fantasydating.routes

import com.queatz.fantasydating.*
import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.fantasydating.util.Validate
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

class PersonRoute constructor(private val on: On) {

    suspend fun get(call: ApplicationCall) {
        if (on<Validate>().respond(call)) return

        val person = call.parameters["id"]!!

        if (on<Db>().isPersonHiddenForPerson(on<Me>().person.id!!, person) && on<Me>().person.boss.not()) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        call.respond(on<Db>().getPersonWithLove(person) ?: HttpStatusCode.NotFound)
    }

    suspend fun post(call: ApplicationCall) {
        if (on<Validate>().respond(call)) return

        if (on<Db>().isPersonHiddenForPerson(on<Me>().person.id!!, call.parameters["id"]!!)) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        val person = on<Db>().getPersonWithLove(call.parameters["id"]!!)

        if (person == null) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        on<Json>().from(call, PersonRequest::class).apply {
            when {
                love != null -> {
                    var success = if (love == true) {
                        on<Db>().love(on<Me>().person.id!!, person.id!!) != null
                    } else {
                        on<Db>().unlove(on<Me>().person.id!!, person.id!!) != null
                    }

                    if (love == true && success && person.lovesYou) {
                        run {
                            val event = Event()
                            event.name = "You and ${person.name} love each other"
                            event.person = on<Me>().person.id!!
                            event.data = on<Json>().to(LoveEventType(person.id!!))
                            on<Arango>().save(event)
                        }

                        run {
                            val event = Event()
                            event.name = "You and ${on<Me>().person.name} love each other"
                            event.person = person.id!!
                            event.data = on<Json>().to(LoveEventType(on<Me>().person.id!!))
                            on<Arango>().save(event)
                        }
                    }

                    call.respond(SuccessResponse(success))
                }
                report != null -> {
                    if (report == true) {
                        val report = Report(
                            person = person.id!!,
                            reporter = on<Me>().person.id!!,
                            report = message ?: "Generic report"
                        )

                        call.respond(SuccessResponse(on<Arango>().save(report) != null))

                        on<Boss>().newStuff()
                    } else {
                        call.respond(SuccessResponse(false))
                    }
                }
                hide != null -> {
                    if (hide == true) {
                        on<Db>().unlove(on<Me>().person.id!!, person.id!!)

                        if (person.lovesYou && person.youLove) {
                            val event = Event()
                            event.name = "${on<Me>().person.name} no longer loves you"
                            event.person = person.id!!
                            event.data = on<Json>().to(UnloveEventType(on<Me>().person.id!!))
                            on<Arango>().save(event)
                        }

                        call.respond(SuccessResponse(on<Db>().hide(on<Me>().person.id!!, person.id!!) != null))
                    } else {
                        call.respond(SuccessResponse(false))
                    }
                }
            }
        }
    }
}
