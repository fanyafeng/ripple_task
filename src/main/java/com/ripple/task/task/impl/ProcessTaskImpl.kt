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
import com.ripple.task.task.ProcessAllResultTask
import com.ripple.task.task.ProcessItemResultTask
import com.ripple.task.task.ProcessTask
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


    private val handler: ProcessTaskImplHandler<S, T> = ProcessTaskImplHandler(this)

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
                        Message.obtain(handler, OnStart.CODE_START, Bundle()).sendToTarget()
                    }

                    override fun onDoing(doingItem: List<ProcessModel<S, T>>?) {
                        super.onDoing(doingItem)
                    }

                    override fun onFailed(failedResult: List<ProcessModel<S, T>>?) {
                        super.onFailed(failedResult)

                        val bundle = Bundle()
                        bundle.putSerializable(
                            OnFailed.RESULT_FAILED,
                            failedResultList
                        )
                        Message.obtain(handler, OnFailed.CODE_FAILED, bundle).sendToTarget()
                    }

                    override fun onSuccess(successResult: List<ProcessModel<S, T>>?) {
                        super.onSuccess(successResult)
                        val bundle = Bundle()
                        bundle.putSerializable(
                            OnSuccess.RESULT_SUCCESS,
                            successResultList
                        )
                        Message.obtain(handler, OnSuccess.CODE_SUCCESS, bundle).sendToTarget()
                    }

                    override fun onFinish(
                        finishResult: List<ProcessModel<S, T>>?,
                        unFinishResult: List<ProcessModel<S, T>>?
                    ) {
                        val bundle = Bundle()
                        bundle.putSerializable(
                            OnSuccess.RESULT_SUCCESS,
                            successResultList
                        )
                        bundle.putSerializable(
                            OnFailed.RESULT_FAILED,
                            failedResultList
                        )
                        Message.obtain(handler, OnFinish.CODE_FINISH, bundle).sendToTarget()
                        executorServiceInner?.shutdown()
                    }
                }
            }
        }
//        val allFuture: Future<*> = service.submit(processAll)
        executorServiceInner = Executors.newSingleThreadExecutor()
        executorServiceInner?.execute(processAll)

        processList.forEachIndexed { _, processModel ->
            val bundle = Bundle()

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
                            bundle.putSerializable(ProcessModel.PROCESS_ITEM, startResult)
                            Message.obtain(handler, OnItemStart.CODE_ITEM_START, bundle)
                                .sendToTarget()
                        }

                        override fun onItemDoing(doingResult: ProcessModel<S, T>) {
                            super.onItemDoing(doingResult)
                            bundle.putSerializable(ProcessModel.PROCESS_ITEM, doingResult)
                            Message.obtain(handler, OnItemDoing.CODE_ITEM_DOING, bundle)
                                .sendToTarget()

                            bundle.putSerializable(
                                OnDoing.RESULT_DOING,
                                doingResult
                            )
                            Message.obtain(handler, OnDoing.CODE_DOING, bundle).sendToTarget()
                        }

                        override fun onItemInterrupted(interruptedResult: ProcessModel<S, T>) {
                            super.onItemInterrupted(interruptedResult)
                            bundle.putSerializable(ProcessModel.PROCESS_ITEM, interruptedResult)
                            Message.obtain(handler, OnItemInterrupted.CODE_ITEM_INTERRUPTED, bundle)
                                .sendToTarget()
                        }

                        override fun onItemFailed(failedResult: ProcessModel<S, T>) {
                            super.onItemFailed(failedResult)
                            failedResultList.add(failedResult)
                            bundle.putSerializable(ProcessModel.PROCESS_ITEM, failedResult)
                            Message.obtain(handler, OnItemFailed.CODE_ITEM_FAILED, bundle)
                                .sendToTarget()
                        }

                        override fun onItemSuccess(successResult: ProcessModel<S, T>) {
                            super.onItemSuccess(successResult)
                            successResultList.add(successResult)
                            bundle.putSerializable(ProcessModel.PROCESS_ITEM, successResult)
                            Message.obtain(handler, OnItemSuccess.CODE_ITEM_SUCCESS, bundle)
                                .sendToTarget()
                        }

                        override fun onItemFinish(finishResult: ProcessModel<S, T>) {
                            bundle.putSerializable(ProcessModel.PROCESS_ITEM, finishResult)
                            Message.obtain(handler, OnItemFinish.CODE_ITEM_FINISH, bundle)
                                .sendToTarget()
                        }
                    }
                }
            }
            val itemFuture = service.submit(processItem)
        }
    }

    /**
     * 以下为所有任务回调
     *
     * 所有任务开始
     */
    private fun onStart() {

    }

    /**
     * 所有任务结束后
     * 失败任务回调
     */
    private fun onFailed(failedResult: List<ProcessModel<S, T>>?) {

    }

    /**
     * 所有任务结束
     * 成功任务回调
     */
    private fun onSuccess(successResult: List<ProcessModel<S, T>>?) {

    }

    /**
     * 所有任务结束
     * 成功和失败任务回调
     */
    private fun onFinish(
        finishResult: List<ProcessModel<S, T>>?,
        unFinishResult: List<ProcessModel<S, T>>?
    ) {

    }

    /**
     * 以下为单个的回调
     *
     * 单个任务开始回调
     */
    private fun onItemStart(startResult: ProcessModel<S, T>) {

    }

    /**
     * 单个任务进行中回调
     */
    private fun onItemDoing(doingResult: ProcessModel<S, T>) {

    }

    /**
     * 单个任务被打断回调
     */
    private fun onItemInterrupted(interruptedResult: ProcessModel<S, T>) {

    }

    /**
     * 单个任务失败回调
     */
    private fun onItemFailed(failedResult: ProcessModel<S, T>) {

    }

    /**
     * 单个任务成功回调
     */
    private fun onItemSuccess(successResult: ProcessModel<S, T>) {

    }

    /**
     * 单个任务结束回调
     */
    private fun onItemFinish(finishResult: ProcessModel<S, T>) {

    }

    class ProcessTaskImplHandler<S, T>(processTask: ProcessTask<S, T>) :
        Handler(Looper.getMainLooper()) {

        private val weakReference: WeakReference<ProcessTask<S, T>> = WeakReference(processTask)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val task = weakReference.get()
            task?.let { taskEngine ->
                val bundle = msg.obj as Bundle
                val processItem: ProcessModel<S, T>
                val successResult: List<ProcessModel<S, T>>
                val failedResult: List<ProcessModel<S, T>>
                when (msg.what) {
                    OnItemStart.CODE_ITEM_START -> {
                        bundle.getSerializable(ProcessModel.PROCESS_ITEM)?.let {
                            processItem =
                                it as ProcessModel<S, T>
                            taskEngine.getItemResult()?.onItemStart(processItem)
                            Log.d("CODE_ITEM_START ITEM", processItem.getSource().toString())
                        }

                    }
                    OnItemDoing.CODE_ITEM_DOING -> {
                        processItem =
                            bundle.getSerializable(ProcessModel.PROCESS_ITEM) as ProcessModel<S, T>
                        taskEngine.getItemResult()?.onItemDoing(processItem)
                    }
                    OnItemInterrupted.CODE_ITEM_INTERRUPTED -> {
                        processItem =
                            bundle.getSerializable(ProcessModel.PROCESS_ITEM) as ProcessModel<S, T>
                        taskEngine.getItemResult()?.onItemInterrupted(processItem)
                    }
                    OnItemFailed.CODE_ITEM_FAILED -> {
                        processItem =
                            bundle.getSerializable(ProcessModel.PROCESS_ITEM) as ProcessModel<S, T>
                        taskEngine.getItemResult()?.onItemFailed(processItem)
                    }

                    OnItemSuccess.CODE_ITEM_SUCCESS -> {
                        processItem =
                            bundle.getSerializable(ProcessModel.PROCESS_ITEM) as ProcessModel<S, T>
                        taskEngine.getItemResult()?.onItemSuccess(processItem)
                    }

                    OnItemFinish.CODE_ITEM_FINISH -> {
                        processItem =
                            bundle.getSerializable(ProcessModel.PROCESS_ITEM) as ProcessModel<S, T>
                        taskEngine.getItemResult()?.onItemFinish(processItem)
                    }

                    OnStart.CODE_START -> {
                        taskEngine.getAllResult()?.onStart()
                    }

                    OnDoing.CODE_DOING -> {
                        processItem =
                            bundle.getSerializable(OnDoing.RESULT_DOING) as ProcessModel<S, T>
                        taskEngine.getAllResult()?.onDoing(listOf(processItem))
                    }
                    OnFailed.CODE_FAILED -> {
                        failedResult =
                            bundle.getSerializable(OnFailed.RESULT_FAILED) as List<ProcessModel<S, T>>
                        taskEngine.getAllResult()?.onFailed(failedResult)
                    }
                    OnSuccess.CODE_SUCCESS -> {
                        successResult =
                            bundle.getSerializable(OnSuccess.RESULT_SUCCESS) as List<ProcessModel<S, T>>
                        taskEngine.getAllResult()?.onSuccess(successResult)
                    }
                    OnFinish.CODE_FINISH -> {
                        failedResult =
                            bundle.getSerializable(OnFailed.RESULT_FAILED) as List<ProcessModel<S, T>>
                        successResult =
                            bundle.getSerializable(OnSuccess.RESULT_SUCCESS) as List<ProcessModel<S, T>>
                        taskEngine.getAllResult()?.onFinish(successResult, failedResult)
                    }
                    else -> {

                    }
                }

            }
        }

    }


}