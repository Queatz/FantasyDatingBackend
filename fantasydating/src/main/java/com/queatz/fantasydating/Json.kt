package com.queatz.fantasydating

import com.google.gson.GsonBuilder
import com.queatz.fantasydating.util.InstantTypeConverter
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.request.receiveText
import java.time.Instant
import kotlin.reflect.KClass

class Json constructor(private val on: On) {

    private val gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantTypeConverter())
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .create()

    fun to(obj: Any) = gson.toJson(obj)!!

    fun <T : Any> from(string: String, klass: KClass<T>): T = gson.fromJson(string, klass.java)

    suspend fun <T : Any> from(call: ApplicationCall, klass: KClass<T>): T = from(call.receiveText(), klass)
}