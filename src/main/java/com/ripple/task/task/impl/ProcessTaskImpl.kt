package com.ripple.task.task.impl

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.ripple.task.callback.*
import com.ripple.task.callback.result.OnAllResult
import com.ripple.task.callback.result.OnItemResult
import com.ripple.task.config.ProcessModel
import com.ripple.task.engine.ProcessEngine
import com.ripple.task.extend.rippleTaskCoroutineScope
import com.ripple.task.task.ProcessAllResultTask
import com.ripple.task.task.ProcessItemResultTask
import com.ripple.task.task.ProcessTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Author: fanyafeng
 * Data: 2020/6/3 19:40
 * Email: fanyafeng@live.cn
 * Description:
 */
class ProcessTaskImpl<S, T> @JvmOverloads constructor(
    /**
     * 多线程任务请求
     */
    private var handleProcessEngine: ProcessEngine = ProcessEngine.MULTI_THREAD_EXECUTOR_NORMAL
) : ProcessTask<S, T> {

    /**
     * 所有任务回调
     */
    var onAllResult: OnAllResult<List<ProcessModel<S, T>>>? = null

    /**
     * 单个任务回调
     */
    var onItemResult: OnItemResult<ProcessModel<S, T>>? = null

    private var countDownLatch: CountDownLatch? = null

    private val failedResultList = arrayListOf<ProcessModel<S, T>>()

    private val successResultList = arrayListOf<ProcessModel<S, T>>()

    init {
        failedResultList.clear()
        successResultList.clear()
    }


    override fun getAllResult(): OnAllResult<List<ProcessModel<S, T>>>? {
        return onAllResult
    }

    override fun getItemResult(): OnItemResult<ProcessModel<S, T>>? {
        return onItemResult
    }

    override fun getProcessEngine(): ProcessEngine {
        return handleProcessEngine
    }

    fun handleTask(process: ProcessModel<S, T>) {
        handleTaskList(listOf(process))
    }

    private var executorServiceInner: ExecutorService? = null

    fun handleTaskList(processList: List<ProcessModel<S, T>>) {
        var service = getProcessEngine().getExecutorService()
        if (service.isShutdown) {
            handleProcessEngine = ProcessEngine.MULTI_THREAD_EXECUTOR_NORMAL
            service = handleProcessEngine.getExecutorService()
        }

        countDownLatch = CountDownLatch(processList.size)

        val processAll = object : ProcessAllResultTask<List<ProcessModel<S, T>>, S, T> {
            override fun getCountDownLatch(): CountDownLatch {
                return countDownLatch!!
            }

            override fun getAllResult(): OnAllResult<List<ProcessModel<S, T>>> {
                return object : OnAllResult<List<ProcessModel<S, T>>> {

                    override fun onStart() {
                        super.onStart()
                        rippleTaskCoroutineScope().launch(Dispatchers.Main) {
                            this@ProcessTaskImpl.getAllResult()?.onStart()
                        }
                    }

                    override fun onDoing(doingItem: List<ProcessModel<S, T>>?) {
                        super.onDoing(doingItem)
                    }

                    override fun onFailed(failedResult: List<ProcessModel<S, T>>?) {
                        super.onFailed(failedResult)
                        rippleTaskCoroutineScope().launch(Dispatchers.Main) {
                            this@ProcessTaskImpl.getAllResult()?.onFailed(failedResultList)
                        }
                    }

                    override fun onSuccess(successResult: List<ProcessModel<S, T>>?) {
                        super.onSuccess(successResult)
                        rippleTaskCoroutineScope().launch(Dispatchers.Main) {
                            this@ProcessTaskImpl.getAllResult()?.onSuccess(successResultList)
                        }
                    }

                    override fun onFinish(
                        finishResult: List<ProcessModel<S, T>>?,
                        unFinishResult: List<ProcessModel<S, T>>?
                    ) {
                        rippleTaskCoroutineScope().launch(Dispatchers.Main) {
                            this@ProcessTaskImpl.getAllResult()?.onFinish(successResultList,failedResultList)
                        }
                        executorServiceInner?.shutdown()
                    }
                }
            }
        }
//        val allFuture: Future<*> = service.submit(processAll)
        executorServiceInner = Executors.newSingleThreadExecutor()
        executorServiceInner?.execute(processAll)

        processList.forEachIndexed { _, processModel ->
            val processItem = object : ProcessItemResultTask<ProcessModel<S, T>, S, T> {

                override fun getProcessModel(): ProcessModel<S, T> {
                    return processModel
                }

                override fun getCountDownLatch(): CountDownLatch {
                    return countDownLatch!!
                }

                override fun getItemResult(): OnItemResult<ProcessModel<S, T>>? {
                    return object : OnItemResult<ProcessModel<S, T>> {

                        override fun onItemStart(startResult: ProcessModel<S, T>) {
                            super.onItemStart(startResult)

                            rippleTaskCoroutineScope().launch(Dispatchers.Main) {
                                this@ProcessTaskImpl.getItemResult()?.onItemStart(startResult)
                            }
                        }

                        override fun onItemDoing(doingResult: ProcessModel<S, T>) {
                            super.onItemDoing(doingResult)

                            rippleTaskCoroutineScope().launch(Dispatchers.Main) {
                                this@ProcessTaskImpl.getItemResult()?.onItemDoing(doingResult)
                                this@ProcessTaskImpl.getAllResult()?.onDoing(listOf(doingResult))
                            }
                        }

                        override fun onItemInterrupted(interruptedResult: ProcessModel<S, T>) {
                            super.onItemInterrupted(interruptedResult)

                            rippleTaskCoroutineScope().launch(Dispatchers.Main) {
                                this@ProcessTaskImpl.getItemResult()?.onItemInterrupted(interruptedResult)
                            }
                        }

                        override fun onItemFailed(failedResult: ProcessModel<S, T>) {
                            super.onItemFailed(failedResult)

                            failedResultList.add(failedResult)
                            rippleTaskCoroutineScope().launch(Dispatchers.Main) {
                                this@ProcessTaskImpl.getItemResult()?.onItemFailed(failedResult)
                            }
                        }

                        override fun onItemSuccess(successResult: ProcessModel<S, T>) {
                            super.onItemSuccess(successResult)

                            successResultList.add(successResult)
                            rippleTaskCoroutineScope().launch(Dispatchers.Main) {
                                this@ProcessTaskImpl.getItemResult()?.onItemSuccess(successResult)
                            }
                        }

                        override fun onItemFinish(finishResult: ProcessModel<S, T>) {

                            rippleTaskCoroutineScope().launch(Dispatchers.Main) {
                                this@ProcessTaskImpl.getItemResult()?.onItemFinish(finishResult)
                            }
                        }
                    }
                }
            }
            val itemFuture = service.submit(processItem)
        }
    }
}