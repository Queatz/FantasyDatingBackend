package com.queatz.fantasydating

import com.queatz.fantasydating.routes.*
import com.queatz.fantasydating.util.InstantTypeConverter
import com.queatz.fantasydating.util.Me
import com.queatz.on.On
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.request.authorization
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.AttributeKey
import java.time.Instant

val OnAttributeKey = AttributeKey<On>("On")

fun Application.main() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(Instant::class.java, InstantTypeConverter())
        }
    }

    intercept(ApplicationCallPipeline.Call) {
        if (call.request.authorization().isNullOrBlank()) {
            call.respondText("Missing 'Authorization' header")
            finish()
        }

        call.attributes.put(OnAttributeKey, On().apply {
            this<Me>().token = call.request.authorization() ?: ""
        })

        proceed()
    }

    routing {
        get("/me") { call.attributes[OnAttributeKey]<MeRoute>().get(call) }
        get("/me/discovery-preferences") { call.attributes[OnAttributeKey]<MeDiscoveryPreferencesRoute>().get(call) }
        get("/me/feed") { call.attributes[OnAttributeKey]<MeFeedRoute>().get(call) }
        get("/me/people") { call.attributes[OnAttributeKey]<MePeopleRoute>().get(call) }
        get("/person/{id}") { call.attributes[OnAttributeKey]<PersonRoute>().get(call) }
        get("/person/{id}/messages") { call.attributes[OnAttributeKey]<PersonMessagesRoute>().get(call) }
        post("/me") { call.attributes[OnAttributeKey]<MeRoute>().post(call) }
        post("/me/discovery-preferences") { call.attributes[OnAttributeKey]<MeDiscoveryPreferencesRoute>().post(call) }
        post("/person/{id}") { call.attributes[OnAttributeKey]<PersonRoute>().post(call) }
        post("/person/{id}/messages") { call.attributes[OnAttributeKey]<PersonMessagesRoute>().post(call) }
    }
}
