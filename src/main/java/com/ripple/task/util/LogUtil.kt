package com.ripple.task.util

import android.util.Log

/**
 * Author: fanyafeng
 * Data: 2020/4/21 17:02
 * Email: fanyafeng@live.cn
 * Description:
 */
object LogUtil {

    private const val isDebug = true

    @JvmOverloads
    @JvmStatic
    fun d(tag: String = "logger d:", msg: String) {
        if (isDebug) {
            Log.d(tag, msg)
        }
    }


}