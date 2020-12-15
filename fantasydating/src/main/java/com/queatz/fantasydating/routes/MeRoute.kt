package com.queatz.fantasydating.routes

import com.queatz.fantasydating.*
import com.queatz.fantasydating.util.Db
import com.queatz.fantasydating.util.Me
import com.queatz.fantasydating.util.Time
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import kotlin.math.max

class MeRoute constructor(private val on: On) {
    suspend fun get(call: ApplicationCall) {
        val me = on<Me>().person
        me.seen = on<Time>().now()
        on<Arango>().save(me)
        me.styles = on<Db>().getPersonStyles(me.id!!)
        call.respond(me)
    }

    suspend fun post(call: ApplicationCall) {
        val person = on<Me>().person

        var needsReview = false

        on<Json>().from(call, MeRequest::class).apply {
            name?.let { person.name = it }
            sex?.let { person.sex = it }
            age?.let { person.age = max(17, it) }
            fantasy?.let {
                if (person.fantasy != it) {
                    person.fantasy = it
                    needsReview = true
                }
            }
            stories?.let {
                if (person.stories.size != it.size || person.stories.zip(it).any {
                    it.first.story != it.second.story ||
                    it.first.photo != it.second.photo
                }) {
                    needsReview = true
                }

                person.stories = it
            }
            active?.let { person.active = it }
        }

        if (needsReview && person.approved) {
            person.approved = false

            val event = Event()
            event.name = "Your profile change is in review"
            event.person = person.id!!
            event.data = on<Json>().to(ProfileLiveEventType(false, "Profile changes are always reviewed"))
            on<Arango>().save(event)

            on<Boss>().newStuff()
        }

        call.respond(on<Arango>().save(person) ?: HttpStatusCode.InternalServerError)
    }
}