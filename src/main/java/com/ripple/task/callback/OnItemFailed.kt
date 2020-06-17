package com.ripple.task.callback

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/6 11:25
 * Email: fanyafeng@live.cn
 * Description: 单项任务失败回调接口
 */
interface OnItemFailed<T>: Serializable {

    companion object {
        const val CODE_ITEM_FAILED= 69
    }

    /**
     * 失败回调接口
     */
    fun onItemFailed(failedResult: T)
}