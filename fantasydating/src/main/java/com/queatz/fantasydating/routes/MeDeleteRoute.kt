package com.queatz.fantasydating.routes

import com.queatz.fantasydating.Arango
import com.queatz.fantasydating.SuccessResponse
import com.queatz.fantasydating.util.Me
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

class MeDeleteRoute constructor(private val on: On) {
    suspend fun post(call: ApplicationCall) {
        val me = on<Me>().person
        me.active = false
        call.respond(SuccessResponse(on<Arango>().save(me) != null))
    }
}
