package com.queatz.fantasydating

import com.queatz.fantasydating.util.Db
import com.queatz.on.On
import com.queatz.pushservice.PushService

class Boss constructor(private val on: On) {
    suspend fun newStuff() {
        val message = on<Json>().toJsonTree(BossPushNotification(
            on<Db>().getReports().size,
            on<Db>().getApprovals().size
        ))

        on<Db>().getBossPeople().map {
            on<Db>().getPhone(it.id!!).token
        }.forEach { token -> on<PushService>().send(token, message) }
    }
}
