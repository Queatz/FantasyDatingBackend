package com.queatz.fantasydating

import com.arangodb.entity.DocumentField
import com.google.gson.annotations.SerializedName
import java.time.Instant

open class BaseModel constructor(val kind: String) {
    @DocumentField(DocumentField.Type.KEY)
    @SerializedName(value = "id", alternate = ["_key"])
    var id: String? = null
    var created: Instant = Instant.EPOCH
    var updated: Instant = Instant.EPOCH
}

open class EdgeBaseModel constructor(kind: String) : BaseModel(kind) {
    @DocumentField(DocumentField.Type.FROM)
    @SerializedName(value = "from", alternate = ["_from"])
    var from: String = ""
    @DocumentField(DocumentField.Type.TO)
    @SerializedName(value = "to", alternate = ["_to"])
    var to: String = ""
}

class DiscoveryPreferences constructor(
    var person: String = "",
    var who: String = "",
    var where: String = "",
    var ageMin: Int = 0,
    var ageMax: Int = 0
) : BaseModel("discovery-preferences")

class InviteCode constructor(
    var person: String = "",
    var code: String = "",
    var used: Boolean = false,
    var usedBy: String? = null,
    var inviter: Person? = null
) : BaseModel("invite-code")

open class Person constructor(
    var sex: String = "",
    var name: String = "",
    var age: Int = 0,
    var seen: Instant = Instant.EPOCH,
    var approved: Boolean = false,
    var active: Boolean = false,
    var invited: Boolean = false,
    var fantasy: String = "",
    var stories: List<PersonStory> = listOf(),
    var boss: Boolean = false
) : BaseModel("person")

open class Style constructor(
    var creator: String = "",
    var name: String = "",
    var about: String = ""
) : BaseModel("style")

class PersonStory constructor(
    var story: String = "",
    var photo: String = "",
    var x: Float = 0f,
    var y: Float = 0f
)

class Event constructor(
    var person: String = "",
    var name: String = "",
    var data: String = ""
) : BaseModel("event")

class Phone constructor(
    var person: String = "",
    var token: String = ""
) : BaseModel("phone")

class Message constructor(
    var from: String = "",
    var to: String = "",
    var message: String? = null,
    var attachment: String? = null
) : BaseModel("message")

class Love : EdgeBaseModel("love")

class Hide : EdgeBaseModel("hide")

class Link : EdgeBaseModel("link")

class StylePreference : EdgeBaseModel("style-preference") {
    var favor: Float = 0f
    var dismissed: Boolean? = null
}

open class Report constructor(
    var person: String = "",
    var reporter: String = "",
    var report: String = "",
    var resolved: Boolean = false
) : BaseModel("report")
