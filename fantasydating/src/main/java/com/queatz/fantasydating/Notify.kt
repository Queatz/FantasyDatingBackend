package com.queatz.fantasydating

import com.queatz.fantasydating.util.Db
import com.queatz.on.On

class Notify constructor(private val on: On) {
    fun storyChanged(person: Person) {
        on<Db>().getPeopleWhoLoveEachOther(person.id!!).forEach { person ->
            val event = Event()
            event.name = "${person.name} updated ${on<ValueFeature>().referToAs(person.sex)} story"
            event.person = person.id!!
            event.data = on<Json>().to(StoryUpdateEventType(person.id!!))
            on<Arango>().save(event)
        }
    }

}
