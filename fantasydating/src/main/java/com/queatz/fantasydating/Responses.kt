package com.queatz.fantasydating

data class SuccessResponse constructor(
    var success: Boolean = false
)

class PersonWithLove constructor(
    var youLove: Boolean = false,
    var lovesYou: Boolean = false
) : Person()

data class BossInfo constructor(
    val approvals: Int,
    val reports: Int
)