package com.ripple.task.lambda

/**
 * Author: fanyafeng
 * Data: 2020/5/6 18:18
 * Email: fanyafeng@live.cn
 * Description:
 */
typealias SuccessLambda<T> = ((result: T) -> Unit)?

typealias PairLambda<T> = ((finishResult: T?, unFinishResult: T?) -> Unit)?