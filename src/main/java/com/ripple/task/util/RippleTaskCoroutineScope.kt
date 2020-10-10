package com.ripple.task.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


/**
 * Author: fanyafeng
 * Date: 2020/10/9 18:16
 * Email: fanyafeng@live.cn
 * Description: RippleTask统一的协程对象
 */
class RippleTaskCoroutineScope private constructor() {
    companion object {

        private const val MIN_PRIORITY_THREADS = Thread.MIN_PRIORITY
        private const val NORMAL_PRIORITY_THREADS = Thread.NORM_PRIORITY
        private const val MAX_PRIORITY_THREADS = Thread.MAX_PRIORITY

        @Volatile
        private var instance: CoroutineScope? = null

        @JvmStatic
        fun getInstance(): CoroutineScope {
            if (instance == null) {
                synchronized(RippleTaskCoroutineScope::class) {
                    if (instance == null) {
                        instance = CoroutineScope(
                            ThreadPoolExecutor(
                                NORMAL_PRIORITY_THREADS,
                                MAX_PRIORITY_THREADS,
                                10L,
                                TimeUnit.SECONDS,
                                LinkedBlockingQueue()
                            ).asCoroutineDispatcher()
                        )
                    }
                }
            }
            return instance!!
        }
    }
}