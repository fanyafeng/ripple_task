package com.ripple.task.callback.result

import com.ripple.task.callback.*

/**
 * Author: fanyafeng
 * Data: 2020/6/3 11:27
 * Email: fanyafeng@live.cn
 * Description: 所有任务的回调
 *
 * 同单个任务一样，开发者大多只关心最后的结果
 * 相比之下对过程关心很少，因此将其过程进行空实现
 *
 */
interface OnAllResult<T> : OnStart<T>, OnDoing<T>, OnFailed<T>, OnSuccess<T>, OnFinish<T> {
    override fun onStart() {
    }

    override fun onDoing(doingItem: T?) {
    }

    override fun onFailed(failedResult: T?) {
    }

    override fun onSuccess(successResult: T?) {
    }

    interface OnAllSimpleResult<T> : OnAllResult<T> {
        override fun onFinish(finishResult: T?, unFinishResult: T?) {
        }
    }
}