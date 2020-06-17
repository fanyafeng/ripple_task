package com.ripple.task.callback

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/7 09:34
 * Email: fanyafeng@live.cn
 * Description: 单项任务完成回调
 */
interface OnItemFinish<T> : Serializable {

    companion object {
        const val CODE_ITEM_FINISH = 71
    }

    /**
     * 完成后执行回调结果
     */
    fun onItemFinish(finishResult: T)
}