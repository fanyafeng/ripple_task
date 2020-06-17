package com.ripple.task.callback

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/6 11:32
 * Email: fanyafeng@live.cn
 * Description: 单项任务被打断回调
 */
interface OnItemInterrupted<T>: Serializable {

    companion object {
        const val CODE_ITEM_INTERRUPTED = 68
    }

    /**
     * 被打断任务回调
     */
    fun onItemInterrupted(interruptedResult: T)
}