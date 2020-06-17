package com.ripple.task.callback

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/6/3 11:41
 * Email: fanyafeng@live.cn
 * Description: 任务开始回调
 */
interface OnStart<T> : Serializable {

    companion object {
        const val CODE_START= 88
    }

    /**
     * 任务开始
     */
    fun onStart()
}