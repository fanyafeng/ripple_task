package com.ripple.task

import com.ripple.log.extend.logD
import com.ripple.task.priority.Priority
import com.ripple.task.priority.PriorityRunnable
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong


/**
 * Author: fanyafeng
 * Data: 2020/7/20 20:01
 * Email: fanyafeng@live.cn
 * Description:
 *
 * 优先级线程池
 * FIFO：先进先出
 */
class PriorityExecutor(corePoolSize: Int = Thread.NORM_PRIORITY, fifo: Boolean = true) : Executor {

    companion object {
        const val maximumPoolSize = 256
        const val keepAliveTime = 1L
        val SEQ_SEED = AtomicLong(0)


        val FIFO_CMP = Comparator<Runnable> { left, right ->
            if (left is PriorityRunnable && right is PriorityRunnable) {
                val result = left.priority.ordinal - right.priority.ordinal
                if (result == 0) ((left.SEQ - right.SEQ).toInt()) else 0
            } else {
                0
            }
        }

        val FILO_CMP = Comparator<Runnable> { lhs, rhs ->
            if (lhs is PriorityRunnable && rhs is PriorityRunnable) {
                val result = lhs.priority.ordinal - rhs.priority.ordinal
                if (result == 0) (rhs.SEQ - lhs.SEQ).toInt() else result
            } else {
                0
            }
        }

        val THREAD_FACTORY = object : ThreadFactory {
            private val mCount = AtomicInteger(1)
            override fun newThread(r: Runnable?): Thread {
                return Thread(r, "Ripple-PriorityExecutor-" + mCount.getAndIncrement())
            }
        }
    }


    private val executor: ThreadPoolExecutor

    init {
        val workQueue =
            PriorityBlockingQueue<Runnable>(maximumPoolSize, if (fifo) FIFO_CMP else FILO_CMP)
        executor = ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            workQueue,
            THREAD_FACTORY
        )
        Runtime.getRuntime().availableProcessors()
    }

    /**
     * 设置核心线程数
     * 如果 size=1 则是串行调用
     * 否则的话是并行调用
     */
    fun setCoreSize(size: Int) {
        if (size > 0) {
            executor.corePoolSize = size
        }
    }

    override fun execute(command: Runnable) {
        if (command is PriorityRunnable) {
            command.SEQ = SEQ_SEED.getAndIncrement()
        }
        executor.execute(command)
    }
}