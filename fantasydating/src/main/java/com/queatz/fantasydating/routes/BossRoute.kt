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
            "report" -> report(on<Json>().from(call, BossReportRequest::class))
            "removeProfile" -> removeProfile(on<Json>().from(call, BossRemoveProfileRequest::class))
            else -> SuccessResponse(false)
        })
    }

    private fun removeProfile(action: BossRemoveProfileRequest): SuccessResponse {
        val person = on<Db>().getById(action.person!!, Person::class) ?: return SuccessResponse(false)

        on<Notify>().accountRemoved(person)

        return SuccessResponse(on<Arango>().delete(person))
    }

    private fun report(action: BossReportRequest): SuccessResponse {
        val report = on<Db>().getById(action.report!!, Report::class) ?: return SuccessResponse(false)

        return if (action.resolve) {
            report.resolved = true
            SuccessResponse(on<Arango>().save(report) != null)
        } else {
            SuccessResponse(false)
        }
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

        if (action.approve == true) {
            val event = Event()
            event.name = "Your profile is live"
            event.person = person.id!!
            event.data = on<Json>().to(ProfileLiveEventType(true, action.message ?: ""))
            on<Arango>().save(event)

            on<Notify>().storyChanged(person)
        } else if (action.approve == false) {
            val event = Event()
            event.name = "Your profile is not ready to go live"
            event.person = person.id!!
            event.data = on<Json>().to(ProfileLiveEventType(false, action.message ?: ""))
            on<Arango>().save(event)
        }

        return SuccessResponse(on<Arango>().save(person) != null)
    }
}
