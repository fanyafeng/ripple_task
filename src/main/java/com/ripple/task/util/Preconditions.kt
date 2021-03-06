package com.ripple.task.util

import com.ripple.tool.judge.checkNotNullRipple

/**
 * Author: fanyafeng
 * Data: 2020/3/6 18:10
 * Email: fanyafeng@live.cn
 * Description: 用来做判空，有返回值
 */
object Preconditions {

    /**
     * 判断对象是否为空
     */
    @JvmStatic
    @JvmOverloads
    fun <T> checkNotNull(reference: T?, errorMessage: Any = "引用对象为空"): T =
        checkNotNullRipple(reference, errorMessage)

}