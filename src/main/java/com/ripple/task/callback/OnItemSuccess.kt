package com.ripple.task.callback

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/6 11:24
 * Email: fanyafeng@live.cn
 * Description: 单项任务成功回调接口
 */
interface OnItemSuccess<T>: Serializable {

    companion object {
        const val CODE_ITEM_SUCCESS = 70
    }
    /**
     * 成功回调接口
     */
    fun onItemSuccess(successResult:T)
}