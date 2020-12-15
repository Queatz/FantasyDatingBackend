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
                else -> {
                }
            }

        }
    }
}