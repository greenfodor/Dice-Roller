package com.greenfodor.diceroller.utils

@PublishedApi
internal actual fun logDebug(tag: String?, message: String) {
    println("D/$tag: $message")
}
