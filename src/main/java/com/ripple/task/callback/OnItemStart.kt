package com.ripple.task.callback

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/6 11:21
 * Email: fanyafeng@live.cn
 * Description: 单项任务开始回调接口
 */
interface OnItemStart<T> : Serializable {

    companion object {
        const val CODE_ITEM_START = 66
    }

    /**
     * 开始回调接口
     */
    fun onItemStart(startResult: T)
}