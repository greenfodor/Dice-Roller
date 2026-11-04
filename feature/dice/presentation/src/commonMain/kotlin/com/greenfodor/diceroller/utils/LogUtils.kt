package com.greenfodor.diceroller.utils

inline fun <reified T> T.logD(block: () -> String) {
    logDebug(T::class.simpleName, block())
}

/** Writes [message] to the platform's debug log under [tag]. */
@PublishedApi
internal expect fun logDebug(tag: String?, message: String)
