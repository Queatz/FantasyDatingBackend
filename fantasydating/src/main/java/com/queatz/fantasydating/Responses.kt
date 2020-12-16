package com.queatz.fantasydating

data class SuccessResponse constructor(
    var success: Boolean = false,
    var message: String? = null
)

class PersonWithLove constructor(
    var youLove: Boolean = false,
    var lovesYou: Boolean = false,
    var styles: List<Style> = listOf(),
    var stylesOrder: List<String> = listOf()
) : Person()

data class BossInfo constructor(
    val approvals: Int,
    val reports: Int
)
