package com.queatz.fantasydating.routes

import com.queatz.fantasydating.*
import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.fantasydating.util.Rnd
import com.queatz.fantasydating.util.Validate
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

class InviteCodeRoute constructor(private val on: On) {

    private val areSelfInvitesSupported = true

    suspend fun post(call: ApplicationCall) {
        call.parameters["id"]?.let {
            val me = on<Me>().person

            if (me.invited) {
                call.respond(SuccessResponse(false, "You've already been invited"))
                return
            }

            if (it == "self") {
                if (!areSelfInvitesSupported) {
                    call.respond(SuccessResponse(false, "You\'ll need an invite code from someone else to join at this time."))
                    return
                }
            } else {
                val invite = on<Db>().getInviteCode(it)

                if (invite == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return
                }

                if (invite.used) {
                    call.respond(SuccessResponse(false, "That invite code has already been used"))
                    return
                }

                invite.used = true
                invite.usedBy = me.id!!
                on<Arango>().save(invite)

                on<Db>().getById(invite.person, Person::class)?.let { inviter ->
                    val event = Event()
                    event.name = "You were invited by ${inviter.name}"
                    event.person = me.id!!
                    event.data = on<Json>().to(InvitedEventType(inviter.id!!))
                    on<Arango>().save(event)
                }
            }

            me.invited = true

            call.respond(SuccessResponse(on<Arango>().save(me) != null))
        } ?: let {
            if (on<Validate>().respond(call)) return

            val invite = InviteCode(
                code = on<Rnd>().code(),
                person = on<Arango>().ensureId(on<Me>().person.id!!)
            )

            on<Arango>().save(invite)?.code?.let {
                call.respond(on<Db>().getInviteCodeWithPerson(it) ?: HttpStatusCode.NotFound)
            } ?: let {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}