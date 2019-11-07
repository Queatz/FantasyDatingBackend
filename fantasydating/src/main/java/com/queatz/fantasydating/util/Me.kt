package com.queatz.fantasydating.util

import com.queatz.fantasydating.DiscoveryPreferences
import com.queatz.fantasydating.Person
import com.queatz.on.On

class Me constructor(private val on: On) {

    lateinit var token: String

    val person: Person get() = on<Db>().getPerson(token)
    val discoveryPreferences get() = on<Db>().getDiscoveryPreferences(person.id!!) ?: DiscoveryPreferences(person = person.id!!)
}