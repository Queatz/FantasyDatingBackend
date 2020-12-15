package com.queatz.fantasydating.routes

import com.queatz.fantasydating.*
import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.fantasydating.util.Validate
import com.queatz.on.On
import io.ktor.application.*
import io.ktor.response.*

class StyleRoute constructor(private val on: On) {
    suspend fun get(call: ApplicationCall) {
        if (on<Validate>().respond(call)) return

        call.request.queryParameters["search"]?.let {
            call.respond(on<Db>().searchStyles(it))
        } ?: run {
            call.respond(on<Db>().getStyles())
        }
    }

    suspend fun post(call: ApplicationCall) {
        if (on<Validate>().respond(call)) return

        on<Json>().from(call, StyleRequest::class).apply {
            val style = Style(
                creator = on<Me>().person.id!!,
                name = name!!,
                about = about!!
            )

            call.respond(SuccessResponse(on<Arango>().save(style)?.also {
                on<Db>().addLink(on<Me>().person.id!!, it.id!!)
            } != null))
        }
    }
}