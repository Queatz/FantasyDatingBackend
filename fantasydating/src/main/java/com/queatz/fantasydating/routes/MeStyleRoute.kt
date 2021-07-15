package com.queatz.fantasydating.routes

import com.queatz.fantasydating.Json
import com.queatz.fantasydating.MeStyleRequest
import com.queatz.fantasydating.SuccessResponse
import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.on.On
import io.ktor.application.*
import io.ktor.response.*

class MeStyleRoute constructor(private val on: On) {
    suspend fun post(call: ApplicationCall) {
        on<Json>().from(call, MeStyleRequest::class).apply {
            when {
                link != null -> {
                    val success = on<Db>().addLink(on<Me>().person.id!!, link!!) != null

                    call.respond(SuccessResponse(success))
                }
                unlink != null -> {
                    val success = on<Db>().removeLink(on<Me>().person.id!!, unlink!!) != null

                    call.respond(SuccessResponse(success))
                }
                promote != null -> {
                    val success = on<Db>().promoteStyle(on<Me>().person.id!!, promote!!) != null

                    call.respond(SuccessResponse(success))
                }
                demote != null -> {
                    val success = on<Db>().demoteStyle(on<Me>().person.id!!, demote!!) != null

                    call.respond(SuccessResponse(success))
                }
                dismiss != null -> {
                    val success = on<Db>().dismissStyle(on<Me>().person.id!!, dismiss!!) != null

                    call.respond(SuccessResponse(success))
                }
                undismiss != null -> {
                    val success = on<Db>().undismissStyle(on<Me>().person.id!!, undismiss!!) != null

                    call.respond(SuccessResponse(success))
                }
                else -> {
                    call.respond(SuccessResponse(false))
                }
            }
        }
    }
}