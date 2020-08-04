package com.ripple.task

import com.ripple.task.callback.result.OnItemResult


/**
 * Author: fanyafeng
 * Data: 2020/7/21 17:42
 * Email: fanyafeng@live.cn
 * Description:
 */
interface ITaskExecutor {

    fun <T> start(task: OnItemResult<T>): OnItemResult<T>
}