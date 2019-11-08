package com.queatz.fantasydating.util


import com.queatz.fantasydating.*
import com.queatz.fantasydating.Arango.Companion.DB_COLLECTION_EDGES
import com.queatz.fantasydating.Arango.Companion.DB_COLLECTION_ENTITIES
import com.queatz.fantasydating.Arango.Companion.DB_GRAPH
import com.queatz.on.On
import kotlin.reflect.KClass

class Db constructor(private val on: On) {
    fun getPerson(token: String) = on<Arango>().queryOne(
            AqlQuery.UpsertPerson,
            params(AqlParam.Token to token),
            Person::class.java
        )!!

    fun <T : Any> getById(id: String, klass: KClass<T>) = on<Arango>().queryOne(
            AqlQuery.ById,
            params(AqlParam.Id to on<Arango>().ensureKey(id)),
            klass.java
        )

    fun getDiscoveryPreferences(id: String) = on<Arango>().queryOne(
        AqlQuery.DiscoveryPreferencesForPerson,
        params(AqlParam.Person to on<Arango>().ensureKey(id)),
        DiscoveryPreferences::class.java
    )

    fun getEvents(id: String) = on<Arango>().query(
        AqlQuery.EventsForPerson,
        params(AqlParam.Person to on<Arango>().ensureKey(id)),
        Event::class.java
    )

    fun getPeople(id: String) = on<Arango>().query(
        AqlQuery.PeopleForPerson,
        graphCollectionParams(AqlParam.Person to on<Arango>().ensureKey(id)),
        Person::class.java
    )

    fun isPersonHiddenForPerson(id: String, person: String) = on<Arango>().queryOne(
        AqlQuery.IsPersonHiddenForPerson,
        graphParams(
            AqlParam.Id to on<Arango>().ensureId(id),
            AqlParam.Person to on<Arango>().ensureKey(person)
        ),
        Boolean::class.java
    )!!

    fun getMessages(person: String, personOther: String) = on<Arango>().query(
        AqlQuery.MessagesBetweenPeople,
        params(
            AqlParam.Person to on<Arango>().ensureKey(person),
            AqlParam.Id to on<Arango>().ensureKey(personOther)
        ),
        Message::class.java
    )

    fun love(from: String, to: String) = on<Arango>().queryOne(
        AqlQuery.AddLove,
        edgeParams(AqlParam.From to from, AqlParam.To to to),
        Love::class.java
    )

    fun hide(from: String, to: String) = on<Arango>().queryOne(
        AqlQuery.HidePerson,
        edgeParams(AqlParam.From to from, AqlParam.To to to),
        Hide::class.java
    )

    private fun params(vararg pairs: Pair<String, String>) =
        mutableMapOf<String, Any>(AqlParam.Collection to DB_COLLECTION_ENTITIES).apply { putAll(pairs) }

    private fun edgeParams(vararg pairs: Pair<String, String>) =
        mutableMapOf<String, Any>(AqlParam.Collection to DB_COLLECTION_EDGES).apply { putAll(pairs) }

    private fun graphCollectionParams(vararg pairs: Pair<String, String>) =
        mutableMapOf<String, Any>(AqlParam.Collection to DB_COLLECTION_ENTITIES, AqlParam.Graph to DB_GRAPH).apply { putAll(pairs) }

    private fun graphParams(vararg pairs: Pair<String, String>) =
        mutableMapOf<String, Any>(AqlParam.Graph to DB_GRAPH).apply { putAll(pairs) }
}
