package com.queatz.pushservice

import com.google.gson.GsonBuilder
import com.queatz.on.On
import kotlin.reflect.KClass

class Json constructor(private val on: On) {

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .create()

    fun to(obj: Any) = gson.toJson(obj)!!

    fun <T : Any> from(string: String, klass: KClass<T>): T = gson.fromJson(string, klass.java)
}