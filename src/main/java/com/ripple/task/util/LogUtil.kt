package com.ripple.task.util

import android.util.Log
import com.ripple.log.extend.logD
import com.ripple.task.BuildConfig

/**
 * Author: fanyafeng
 * Data: 2020/4/21 17:02
 * Email: fanyafeng@live.cn
 * Description:
 */
object LogUtil {

    @JvmOverloads
    @JvmStatic
    fun d(tag: String = "logger d:", msg: String) {
        if (BuildConfig.DEBUG) {
            logD(msg, tag)
        }
    }


}