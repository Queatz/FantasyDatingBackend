package com.queatz.fantasydating

import com.arangodb.entity.DocumentField
import com.google.gson.annotations.SerializedName

open class BaseModel {
    @DocumentField(DocumentField.Type.KEY)
    @SerializedName(value = "id", alternate = ["_key"])
    var id: String = ""
}

data class DiscoveryPreferences constructor(
    var who: String = "",
    var where: String = "",
    var ageMin: Int = 0,
    var ageMax: Int = 0
) : BaseModel()

data class WalkthroughStep constructor(
    var step: String = "",
    var shown: Boolean = false
) : BaseModel()

data class Person constructor(
    var sex: String = "",
    var name: String = "",
    var age: Int = 0,
    var approved: Boolean = false,
    var active: Boolean = false,
    var fantasy: String = "",
    var stories: List<String> = listOf()
) : BaseModel()
