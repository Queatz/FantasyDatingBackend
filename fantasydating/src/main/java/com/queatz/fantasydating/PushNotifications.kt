package com.queatz.fantasydating

open class PushNotification constructor(val action: String)

data class MessagePushNotification constructor(
    val name: String,
    val id: String,
    val message: String
) : PushNotification("message")

data class BossPushNotification constructor(
    val reports: Int,
    val approvals: Int
) : PushNotification("boss")
