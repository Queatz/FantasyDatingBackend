package com.queatz.pushservice

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.queatz.on.On
import io.ktor.http.HttpStatusCode
import java.util.logging.Logger

class PushService constructor(private val on: On) {

    companion object {
        private const val FCM_KEY_DATA = "data"
        private const val FCM_KEY_TO = "to"
        private const val FCM_KEY_PRIORITY = "priority"
        private const val FCM_PRIORITY_HIGH = "high"
        private const val FCM_ENDPOINT = "https://fcm.googleapis.com/fcm/send"
    }

    var onReplaceToken = { oldToken: String, newToken: String -> }
    var onInvalidToken = { invalidToken: String -> }

    suspend fun send(token: String, message: JsonElement) {
        if (token.isBlank()) {
            return
        }

        val push = JsonObject()
        push.add(FCM_KEY_DATA, message)
        push.add(FCM_KEY_TO, JsonPrimitive(token))
        push.add(FCM_KEY_PRIORITY, JsonPrimitive(FCM_PRIORITY_HIGH))

        on<Http>().post(
            FCM_ENDPOINT, on<Json>().to(push)) {
            if (it.status != HttpStatusCode.OK) {
                Logger.getGlobal().warning("FCM Response: ${it.content}")
                return@post
            }

            val jsonResponse: JsonElement = on<Json>().from(
                it.content,
                JsonElement::class
            )

            if (!jsonResponse.isJsonObject) {
                Logger.getGlobal().warning("FCM Response: ${it.content}")
                return@post
            }

            val results = jsonResponse.asJsonObject
            if (results.has("results") && results.getAsJsonArray("results").size() > 0) {
                val result = results.getAsJsonArray("results")[0].asJsonObject
                if (result.has("registration_id")) {
                    onReplaceToken(token, result["registration_id"].asString)
                }
                if (result.has("error")) {
                    if ("MismatchSenderId" == result["error"].asString || "NotRegistered" == result["error"].asString
                    ) {
                        if (result.has("registration_id")) {
                            onInvalidToken(result["registration_id"].asString)
                        }
                    }
                }
            }
        }
    }

}