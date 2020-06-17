package com.ripple.task.callback.result

import com.ripple.task.callback.*

/**
 * Author: fanyafeng
 * Data: 2020/6/3 11:26
 * Email: fanyafeng@live.cn
 * Description: 单项任务所有回调结果
 *
 * 此为所有回调处理结果，
 * 除了任务完成的状态外，其他所有回调进行了简单的空实现
 * 其目的是因为人们一般只关注最后的结果不关心过程
 */
interface OnItemResult<T> : OnItemStart<T>, OnItemDoing<T>, OnItemInterrupted<T>,
    OnItemSuccess<T>, OnItemFailed<T>, OnItemFinish<T> {

    override fun onItemStart(startResult: T) {
    }

    override fun onItemDoing(doingResult: T) {
    }

    override fun onItemInterrupted(interruptedResult: T) {
    }

    override fun onItemFailed(failedResult: T) {
    }

    override fun onItemSuccess(successResult: T) {
    }

    interface OnItemSimpleResult<T> : OnItemResult<T> {
        override fun onItemFinish(finishResult: T) {
        }
    }
}