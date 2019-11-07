package com.queatz.fantasydating

import com.arangodb.entity.DocumentField
import com.google.gson.annotations.SerializedName
import java.time.Instant

open class BaseModel {
    @DocumentField(DocumentField.Type.KEY)
    @SerializedName(value = "id", alternate = ["_key"])
    var id: String? = null
    var created: Instant = Instant.EPOCH
    var updated: Instant = Instant.EPOCH
}

class DiscoveryPreferences constructor(
    var kind: String = "discovery-preferences",
    var person: String = "",
    var who: String = "",
    var where: String = "",
    var ageMin: Int = 0,
    var ageMax: Int = 0
) : BaseModel()

class Person constructor(
    var kind: String = "person",
    var sex: String = "",
    var name: String = "",
    var age: Int = 0,
    var approved: Boolean = false,
    var active: Boolean = false,
    var fantasy: String = "",
    var stories: List<String> = listOf()
) : BaseModel()
