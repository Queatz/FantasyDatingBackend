package com.queatz.fantasydating.util

import com.queatz.on.On
import java.util.*
import kotlin.math.abs


class Rnd constructor(private val on: On) {
    fun code(length: Int = 24): String {
        val random = Random()
        val str = StringBuilder()
        for (i in 0 until length) {
            val x = abs(random.nextInt()) % 36
            if (x < 26) {
                str.append((x + 'a'.code).toChar())
            } else {
                str.append(x - 26)
            }
        }
        return str.toString()
    }
}
