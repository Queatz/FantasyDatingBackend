package com.queatz.fantasydating

import com.queatz.fantasydating.routes.*
import com.queatz.fantasydating.util.InstantTypeConverter
import com.queatz.on.On
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import java.time.Instant

fun Application.main() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(Instant::class.java, InstantTypeConverter())
        }
    }

    routing {
        get("/me") { On()<MeRoute>().get(call) }
        get("/me/discovery-preferences") { On()<MeDiscoveryPreferencesRoute>().get(call) }
        get("/me/feed") { On()<MeFeedRoute>().get(call) }
        get("/me/people") { On()<MePeopleRoute>().get(call) }
        get("/person/{id}") { On()<PersonRoute>().get(call) }
        get("/person/{id}/messages") { On()<PersonMessagesRoute>().get(call) }
        post("/me") { On()<MeRoute>().post(call) }
        post("/me/discovery-preferences") { On()<MeDiscoveryPreferencesRoute>().post(call) }
        post("/person/{id}") { On()<PersonRoute>().post(call) }
        post("/person/{id}/messages") { On()<PersonMessagesRoute>().post(call) }
    }
}