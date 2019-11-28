package com.queatz.fantasydating.routes

import com.queatz.fantasydating.*
import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

class BossRoute constructor(private val on: On) {
    suspend fun get(call: ApplicationCall) {
        val action = call.parameters["action"]!!

        if (on<Me>().person.boss.not()) {
             call.respond(SuccessResponse(false))
             return
         }

        call.respond(when (action) {
            "info" -> BossInfo(
                on<Db>().getApprovals().size,
                on<Db>().getReports().size
            )
            "approvals" -> on<Db>().getApprovals()
            "reports" -> on<Db>().getReports()
            else -> SuccessResponse(false)
        })
    }

    suspend fun post(call: ApplicationCall) {
        val action = call.parameters["action"]!!

        if (on<Me>().person.boss.not() && action != "me") {
            call.respond(SuccessResponse(false))
            return
        }

        call.respond(when (action) {
            "me" -> upgrade(on<Json>().from(call, WhoIsTheBossRequest::class))
            "approve" -> approve(on<Json>().from(call, BossApproveRequest::class))
            else -> SuccessResponse(false)
        })
    }

    private fun upgrade(action: WhoIsTheBossRequest): SuccessResponse {
        val me = on<Me>().person

        if (me.boss.not() && action.whoIsTheBoss == "I am the boss") {
            me.boss = true
            return SuccessResponse(on<Arango>().save(me) != null)
        }

        return SuccessResponse(false)
    }

    private fun approve(action: BossApproveRequest): SuccessResponse {
        if (action.person == null || action.approve == null) {
            return SuccessResponse(false)
        }

        val person = on<Db>().getById(action.person!!, Person::class) ?: return SuccessResponse(false)

        person.approved = action.approve!!

        if (action.message != null) {
            // TODO send a message to that person from admin
        }

        return SuccessResponse(on<Arango>().save(person) != null)
    }
}
