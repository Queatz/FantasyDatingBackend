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

    fun getPeople(id: String, sex: String, minAge: Int, maxAge: Int) = on<Arango>().query(
        AqlQuery.PeopleForPerson,
        graphCollectionParams(
            AqlParam.Person to on<Arango>().ensureId(id),
            AqlParam.Sex to sex,
            AqlParam.Min to minAge,
            AqlParam.Max to maxAge
        ),
        PersonWithLove::class.java
    )

    fun getPersonWithLove(id: String) = on<Arango>().queryOne(
        AqlQuery.PersonForPerson,
        graphParams(
            AqlParam.Person to on<Arango>().ensureId(on<Me>().person.id!!),
            AqlParam.Id to on<Arango>().ensureId(id)
        ),
        PersonWithLove::class.java
    )

    fun getPeopleWhoLoveEachOther(person: String) = on<Arango>().query(
        AqlQuery.PeopleWhoLoveEachOther,
        graphParams(
            AqlParam.Person to on<Arango>().ensureId(person)
        ),
        PersonWithLove::class.java
    )

    fun isPersonHiddenForPerson(id: String, person: String) = on<Arango>().queryOne(
        AqlQuery.IsPersonHiddenForPerson,
        graphParams(
            AqlParam.Id to on<Arango>().ensureId(id),
            AqlParam.Person to on<Arango>().ensureId(person)
        ),
        Boolean::class.java
    )!!

    fun isBothPeopleLoveEachOther(id: String, person: String) = on<Arango>().queryOne(
        AqlQuery.IsBothPeopleLoveEachOther,
        graphParams(
            AqlParam.Id to on<Arango>().ensureId(id),
            AqlParam.Person to on<Arango>().ensureId(person)
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

    fun getApprovals() = on<Arango>().query(
        AqlQuery.Approvals,
        params(),
        Person::class.java
    )

    fun getReports() = on<Arango>().query(
        AqlQuery.Reports,
        params(),
        Report::class.java
    )

    fun getBossPeople() = on<Arango>().query(
        AqlQuery.BossPeople,
        params(),
        Person::class.java
    )

    fun love(from: String, to: String) = on<Arango>().queryOne(
        AqlQuery.AddLove,
        edgeParams(AqlParam.From to on<Arango>().ensureId(from), AqlParam.To to on<Arango>().ensureId(to)),
        Love::class.java
    )

    fun unlove(from: String, to: String) = on<Arango>().queryOne(
        AqlQuery.RemoveLove,
        edgeParams(AqlParam.From to on<Arango>().ensureId(from), AqlParam.To to on<Arango>().ensureId(to)),
        Love::class.java
    )

    fun hide(from: String, to: String) = on<Arango>().queryOne(
        AqlQuery.HidePerson,
        edgeParams(AqlParam.From to on<Arango>().ensureId(from), AqlParam.To to on<Arango>().ensureId(to)),
        Hide::class.java
    )

    fun setPhoneToken(person: String, token: String) = on<Arango>().queryOne(
        AqlQuery.SetPhoneToken,
        params(
            AqlParam.Person to on<Arango>().ensureId(person),
            AqlParam.Token to token
        ),
        Phone::class.java
    )

    fun getPhone(person: String) = on<Arango>().queryOne(
        AqlQuery.GetPhone,
        params(AqlParam.Person to on<Arango>().ensureId(person)),
        Phone::class.java
    )!!

    private fun params(vararg pairs: Pair<String, String>) =
        mutableMapOf<String, Any>(AqlParam.Collection to DB_COLLECTION_ENTITIES).apply { putAll(pairs) }

    private fun edgeParams(vararg pairs: Pair<String, String>) =
        mutableMapOf<String, Any>(AqlParam.Collection to DB_COLLECTION_EDGES).apply { putAll(pairs) }

    private fun graphCollectionParams(vararg pairs: Pair<String, Any>) =
        mutableMapOf<String, Any>(AqlParam.Collection to DB_COLLECTION_ENTITIES, AqlParam.Graph to DB_GRAPH).apply { putAll(pairs) }

    private fun graphParams(vararg pairs: Pair<String, String>) =
        mutableMapOf<String, Any>(AqlParam.Graph to DB_GRAPH).apply { putAll(pairs) }
}
