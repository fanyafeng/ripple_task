package com.ripple.task.callback

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/6 11:23
 * Email: fanyafeng@live.cn
 * Description: 单项任务进行中回调
 */
interface OnItemDoing<T> : Serializable {

    companion object {
        const val CODE_ITEM_DOING = 67

        const val CODE_ITEM_DOING_START = 0L

        const val CODE_ITEM_DOING_FAILED = -1L

        const val CODE_ITEM_DOING_FINISH = 100L
    }

    /**
     * 进行中回调接口
     */
    fun onItemDoing(doingResult: T)
}