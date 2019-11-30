package com.queatz.fantasydating

open class EventType constructor(
    val type: String
)

data class ProfileLiveEventType constructor(
    val live: Boolean,
    val message: String
) : EventType("live")

data class LoveEventType constructor(
    val person: String
) : EventType("love")

data class UnloveEventType constructor(
    val person: String
) : EventType("unlove")

data class StoryUpdateEventType constructor(
    val person: String
) : EventType("story")

data class AccountRemovedEventType constructor(
    val person: String
) : EventType("accountRemoved")
