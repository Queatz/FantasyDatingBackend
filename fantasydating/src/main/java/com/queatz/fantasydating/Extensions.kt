package com.queatz.fantasydating

inline infix fun Boolean.then(function: () -> Unit): Boolean {
    if (this) {
        function.invoke()
    }

    return this
}
