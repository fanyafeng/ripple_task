package com.ripple.task.engine

import java.io.Serializable
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Author: fanyafeng
 * Data: 2020/5/6 17:51
 * Email: fanyafeng@live.cn
 * Description: 使用java自带的任务服务
 */
interface ProcessEngine : Serializable {


    companion object {

        internal var singleExecutorInner = Executors.newSingleThreadExecutor(object :
            ThreadFactory {
            val atomic = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "ripple-task-内部单线程池-" + atomic.getAndIncrement())
            }
        })
        internal val SINGLE_THREAD_EXECUTOR_INNER: ProcessEngine =
            object : ProcessEngine {
                override fun getExecutorService(): ExecutorService {
                    return if (!singleExecutorInner.isShutdown) {
                        singleExecutorInner
                    } else {
                        singleExecutorInner = Executors.newSingleThreadExecutor(object :
                            ThreadFactory {
                            val atomic = AtomicInteger(1)
                            override fun newThread(r: Runnable): Thread {
                                return Thread(r, "ripple-task-内部单线程池-" + atomic.getAndIncrement())
                            }
                        })
                        singleExecutorInner
                    }
                }

                override fun shutdown() {
                    singleExecutorInner.shutdown()
                }

            }

        /**
         * 单线程处理器
         * 处理任务为串行处理
         */
        private var singleExecutor = Executors.newSingleThreadExecutor(object :
            ThreadFactory {
            val atomic = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "ripple-task-内置单线程池-" + atomic.getAndIncrement())
            }
        })
        val SINGLE_THREAD_EXECUTOR: ProcessEngine =
            object : ProcessEngine {
                override fun getExecutorService(): ExecutorService {
                    return if (!singleExecutor.isShutdown) {
                        singleExecutor
                    } else {
                        singleExecutor = Executors.newSingleThreadExecutor(object :
                            ThreadFactory {
                            val atomic = AtomicInteger(1)
                            override fun newThread(r: Runnable): Thread {
                                return Thread(r, "ripple-task-内置单线程池-" + atomic.getAndIncrement())
                            }
                        })
                        singleExecutor
                    }
                }

                override fun shutdown() {
                    singleExecutor.shutdown()
                }

            }

        /**
         * 自带6个线程的处理器
         * 不用纠结个数为什么这么定义，纯属个人喜欢的数字
         * 处理任务为并行处理，并且顺序是打乱的
         */
        private var maxExecutor = Executors.newFixedThreadPool(Thread.MAX_PRIORITY, object :
            ThreadFactory {
            val atomic = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "ripple-task-内置最大线程池-" + atomic.getAndIncrement())
            }
        })
        val MULTI_THREAD_EXECUTOR_MAX: ProcessEngine =
            object : ProcessEngine {
                override fun getExecutorService(): ExecutorService {
                    return if (!maxExecutor.isShutdown) {
                        maxExecutor
                    } else {
                        maxExecutor = Executors.newFixedThreadPool(Thread.MAX_PRIORITY, object :
                            ThreadFactory {
                            val atomic = AtomicInteger(1)
                            override fun newThread(r: Runnable): Thread {
                                return Thread(r, "ripple-task-内置最大线程池-" + atomic.getAndIncrement())
                            }
                        })
                        maxExecutor
                    }
                }

                override fun shutdown() {
                    maxExecutor.shutdown()
                }

            }

        private var normalExecutor = Executors.newFixedThreadPool(Thread.NORM_PRIORITY, object :
            ThreadFactory {
            val atomic = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "ripple-task-内置一般线程池-" + atomic.getAndIncrement())
            }
        })

        val MULTI_THREAD_EXECUTOR_NORMAL: ProcessEngine =
            object : ProcessEngine {

                override fun getExecutorService(): ExecutorService {
                    return if (!normalExecutor.isShutdown) {
                        normalExecutor
                    } else {
                        normalExecutor = Executors.newFixedThreadPool(Thread.NORM_PRIORITY, object :
                            ThreadFactory {
                            val atomic = AtomicInteger(1)
                            override fun newThread(r: Runnable): Thread {
                                return Thread(r, "ripple-task-内置一般线程池-" + atomic.getAndIncrement())
                            }
                        })
                        normalExecutor
                    }
                }

                override fun shutdown() {
                    normalExecutor.shutdown()
                }

            }

        private var minExecutor = Executors.newFixedThreadPool(Thread.MIN_PRIORITY, object :
            ThreadFactory {
            val atomic = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "ripple-task-内置最小线程池-" + atomic.getAndIncrement())
            }
        })

        val MULTI_THREAD_EXECUTOR_MIN: ProcessEngine =
            object : ProcessEngine {

                override fun getExecutorService(): ExecutorService {
                    return if (!minExecutor.isShutdown) {
                        minExecutor
                    } else {
                        minExecutor = Executors.newFixedThreadPool(Thread.MIN_PRIORITY, object :
                            ThreadFactory {
                            val atomic = AtomicInteger(1)
                            override fun newThread(r: Runnable): Thread {
                                return Thread(r, "ripple-task-内置最小线程池-" + atomic.getAndIncrement())
                            }
                        })
                        minExecutor
                    }
                }

                override fun shutdown() {
                    minExecutor.shutdown()
                }

            }
    }

    /**
     * 获取任务处理任务
     *
     * 1、线程池： 提供一个线程队列，队列中保存着所有等待状态的线程。避免了创建与销毁的额外开销，提高了响应的速度。
     *
     * [java.util.concurrent.Executor]
     * [java.util.concurrent.ExecutorService]
     * [java.util.concurrent.ThreadPoolExecutor]
     * [java.util.concurrent.ScheduledExecutorService]
     * [java.util.concurrent.ScheduledThreadPoolExecutor]
     *
     * 2、线程池的体系结构：
     * java.util.concurrent.Executor 负责线程的使用和调度的根接口
     *      |--ExecutorService 子接口： 线程池的主要接口
     *              |--ThreadPoolExecutor 线程池的实现类
     *              |--ScheduledExecutorService 子接口： 负责线程的调度
     *                      |--ScheduledThreadPoolExecutor : 继承ThreadPoolExecutor，实现了ScheduledExecutorService
     *
     * [java.util.concurrent.Executor]
     *
     * 3、工具类 ： Executors
     * ExecutorService newFixedThreadPool() : 创建固定大小的线程池
     * ExecutorService newCachedThreadPool() : 缓存线程池，线程池的数量不固定，可以根据需求自动的更改数量。
     * ExecutorService newSingleThreadExecutor() : 创建单个线程池。 线程池中只有一个线程
     *
     * ScheduledExecutorService newScheduledThreadPool() : 创建固定大小的线程，可以延迟或定时的执行任务
     */
    fun getExecutorService(): ExecutorService

    /**
     * 停止任务
     */
    fun shutdown()

}