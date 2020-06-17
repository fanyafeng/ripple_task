package com.ripple.task.callback

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/6 15:58
 * Email: fanyafeng@live.cn
 * Description: 完成回调接口
 */
interface OnFinish<T>: Serializable {

    companion object {
        const val CODE_FINISH = 92
    }
    /**
     * 任务完成接口
     * @param finishResult 成功任务的回调
     * @param unFinishResult 失败任务的回调
     */
    fun onFinish(finishResult: T?, unFinishResult: T?)
}