package com.queatz.fantasydating.routes

import com.queatz.fantasydating.Arango
import com.queatz.fantasydating.InviteCode
import com.queatz.fantasydating.SuccessResponse
import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.fantasydating.util.Rnd
import com.queatz.fantasydating.util.Validate
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

class InviteRoute constructor(private val on: On) {
    suspend fun post(call: ApplicationCall) {
        call.parameters["id"]?.let {
            val me = on<Me>().person

            if (me.invited) {
                call.respond(SuccessResponse(false, "You've already been invited"))
                return
            }

            val invite = on<Db>().getInviteCode(it)

            if (invite.used) {
                call.respond(SuccessResponse(false, "This invite code has already been used"))
                return
            }

            invite.used = true
            invite.usedBy = me.id!!
            on<Arango>().save(invite)

            me.invited = true

            call.respond(SuccessResponse(on<Arango>().save(me) != null))
        } ?: let {
            if (on<Validate>().respond(call)) return

            val invite = InviteCode(
                code = on<Rnd>().code(),
                person = on<Arango>().ensureId(on<Me>().person.id!!)
            )

            on<Arango>().save(invite)?.id?.let {
                call.respond(on<Db>().getInviteCodeWithPerson(it))
            } ?: let {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}