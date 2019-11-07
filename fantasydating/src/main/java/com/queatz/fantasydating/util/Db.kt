package com.queatz.fantasydating.util

import com.queatz.fantasydating.*
import com.queatz.on.On

class Db constructor(private val on: On) {
    fun getPerson(token: String) = on<Arango>().queryOne(
            AQL_UPSERT_PERSON,
            params(AQL_PARAM_TOKEN to token),
            Person::class.java
        )!!

    fun getDiscoveryPreferences(id: String) = on<Arango>().queryOne(
        AQL_DISCOVERY_PREFERENCES_FOR_PERSON,
        params(AQL_PARAM_PERSON to on<Arango>().ensureKey(id)),
        DiscoveryPreferences::class.java
    )

    private fun params(vararg pairs: Pair<String, String>) =
        mutableMapOf<String, Any>(AQL_PARAM_ENTITIES to "entities").apply { putAll(pairs) }
}
