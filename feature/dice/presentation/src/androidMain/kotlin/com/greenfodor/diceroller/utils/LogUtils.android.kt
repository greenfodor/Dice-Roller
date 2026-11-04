package com.greenfodor.diceroller.utils

import android.util.Log

@PublishedApi
internal actual fun logDebug(tag: String?, message: String) {
    Log.d(tag, message)
}
