package com.queatz.fantasydating

import com.queatz.fantasydating.util.Db
import com.queatz.on.On

class Notify constructor(private val on: On) {
    fun storyChanged(person: Person) {
        on<Db>().getPeopleWhoLoveEachOther(person.id!!).forEach {
            val event = Event()
            event.name = "${person.name} updated ${on<ValueFeature>().referToAs(person.sex)} story"
            event.person = it.id!!
            event.data = on<Json>().to(StoryUpdateEventType(person.id!!))
            on<Arango>().save(event)
        }
    }

    fun accountRemoved(person: Person) {
        on<Db>().getPeopleWhoLoveEachOther(person.id!!).forEach {
            val event = Event()
            event.name = "${person.name}'s account was removed"
            event.person = it.id!!
            event.data = on<Json>().to(AccountRemovedEventType(person.id!!))
            on<Arango>().save(event)
        }
    }
}
