package com.queatz.fantasydating

infix fun Boolean.then(function: () -> Unit): Boolean {
    if (this) {
        function.invoke()
    }

    return this
}
