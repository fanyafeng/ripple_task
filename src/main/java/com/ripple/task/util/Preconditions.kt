package com.ripple.task.util

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
    fun <T> checkNotNull(reference: T?, errorMessage: Any = "引用对象为空"): T {
        if (reference == null) {
            throw NullPointerException(errorMessage.toString())
        }
        return reference
    }


    @JvmStatic
    fun <T> checkArgument(expression: Boolean, errorMessage: Any) {
        if (!expression) {
            throw IllegalArgumentException(errorMessage.toString())
        }
    }

}