package com.ripple.task.callback

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/6/3 11:42
 * Email: fanyafeng@live.cn
 * Description: 所有任务进行中回调
 */
interface OnDoing<T> : Serializable {

    companion object {
        const val CODE_DOING = 89
        const val RESULT_DOING = "doing_result"
    }

    /**
     * 某项任务在进行中状态
     *
     */
    fun onDoing(doingItem: T?)
}