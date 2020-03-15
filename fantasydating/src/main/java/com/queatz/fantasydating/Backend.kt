package com.queatz.fantasydating

import com.queatz.fantasydating.routes.*
import com.queatz.fantasydating.util.InstantTypeConverter
import com.queatz.fantasydating.util.Me
import com.queatz.on.On
import io.ktor.application.*
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
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.pipeline.PipelineContext
import java.time.Instant

val OnAttributeKey = AttributeKey<On>("On")

@KtorExperimentalAPI
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
        get("/me") { on<MeRoute>().get(call) }
        get("/me/discovery-preferences") { on<MeDiscoveryPreferencesRoute>().get(call) }
        get("/me/events") { on<MeEventsRoute>().get(call) }
        get("/me/people") { on<MePeopleRoute>().get(call) }
        get("/person/{id}") { on<PersonRoute>().get(call) }
        get("/person/{id}/messages") { on<PersonMessagesRoute>().get(call) }
        get("/boss/{action}") { on<BossRoute>().get(call) }
        post("/me") { on<MeRoute>().post(call) }
        post("/me/discovery-preferences") { on<MeDiscoveryPreferencesRoute>().post(call) }
        post("/me/delete") { on<MeDeleteRoute>().post(call) }
        post("/person/{id}") { on<PersonRoute>().post(call) }
        post("/person/{id}/messages") { on<PersonMessagesRoute>().post(call) }
        post("/bootystrap") { on<BootystrapRoute>().get(call) }
        post("/boss/{action}") { on<BossRoute>().post(call) }
        post("/phone") { on<PhoneRoute>().post(call) }
        get("/invite") { on<InviteRoute>().post(call) }
        get("/invite/{id}") { on<InviteRoute>().post(call) }
    }
}

private inline fun <reified T : Any> PipelineContext<*, ApplicationCall>.on() = call.attributes[OnAttributeKey]<T>()