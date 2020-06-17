package com.ripple.task.callback

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/6 11:12
 * Email: fanyafeng@live.cn
 * Description: 全部成功回调接口
 */
interface OnSuccess<T> : Serializable {

    companion object {
        const val CODE_SUCCESS = 91

        const val RESULT_SUCCESS = "success_result"
    }

    /**
     * 成功回调接口
     */
    fun onSuccess(successResult: T?)
}