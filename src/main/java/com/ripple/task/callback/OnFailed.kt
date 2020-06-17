package com.ripple.task.callback

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/6 11:15
 * Email: fanyafeng@live.cn
 * Description: 全部失败回调接口
 */
interface OnFailed<T> : Serializable {

    companion object {
        const val CODE_FAILED = 90

        const val RESULT_FAILED = "failed_result"
    }

    /**
     * 失败回调接口
     */
    fun onFailed(failedResult: T?)
}