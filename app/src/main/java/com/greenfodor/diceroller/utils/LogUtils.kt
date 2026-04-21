package com.greenfodor.diceroller.utils

import android.util.Log

inline fun<reified T> T.logD(block: () -> String) {
    Log.d(T::class.simpleName, block())
}