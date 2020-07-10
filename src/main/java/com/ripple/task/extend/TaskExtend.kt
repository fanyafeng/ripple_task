package com.ripple.task.extend

import com.ripple.task.callback.result.OnAllResult
import com.ripple.task.callback.result.OnItemResult
import com.ripple.task.config.ProcessModel
import com.ripple.task.engine.ProcessEngine
import com.ripple.task.task.impl.ProcessTaskImpl
import com.ripple.tool.kttypelians.PairLambda
import com.ripple.tool.kttypelians.SuccessLambda

/**
 * Author: fanyafeng
 * Data: 2020/6/4 15:24
 * Email: fanyafeng@live.cn
 * Description:
 */

/**
 * 内部维持一个单线程队列
 * 会跟随进程存活在后台
 * 如需任务处理直接交给其处理即可
 */
@JvmOverloads
fun <S, T> serialBackgroundTask(
    process: ProcessModel<S, T>,
    lambda: (HandleTaskExtra<S, T>.() -> Unit)? = null
) {
    handleTaskList(listOf(process), ProcessEngine.SINGLE_THREAD_EXECUTOR_INNER, lambda)
}

/**
 * 默认为并行多线程任务
 */
@JvmOverloads
fun <S, T> handleTask(
    process: ProcessModel<S, T>,
    processEngine: ProcessEngine = ProcessEngine.MULTI_THREAD_EXECUTOR_MAX,
    lambda: (HandleTaskExtra<S, T>.() -> Unit)? = null
) {
    handleTaskList(listOf(process), processEngine, lambda)
}

/**
 * 默认为并行多线程任务
 */
@JvmOverloads
fun <S, T> handleTaskList(
    processList: List<ProcessModel<S, T>>,
    processEngine: ProcessEngine = ProcessEngine.MULTI_THREAD_EXECUTOR_NORMAL,
    lambda: (HandleTaskExtra<S, T>.() -> Unit?)? = null
) {
    val handleTaskExtra = HandleTaskExtra(processEngine, processList)
    lambda?.let {
        handleTaskExtra.apply {
            it()
        }
    }
}

class HandleTaskExtra<S, T>(processEngine: ProcessEngine, processList: List<ProcessModel<S, T>>) {

    private var itemStartLambda: SuccessLambda<ProcessModel<S, T>> = null
    private var itemDoingLambda: SuccessLambda<ProcessModel<S, T>> = null
    private var itemInterruptedLambda: SuccessLambda<ProcessModel<S, T>> = null
    private var itemFailedLambda: SuccessLambda<ProcessModel<S, T>> = null
    private var itemSuccessLambda: SuccessLambda<ProcessModel<S, T>> = null
    private var itemFinishLambda: SuccessLambda<ProcessModel<S, T>> = null
    private var startLambda: SuccessLambda<Unit> = null
    private var doingLambda: SuccessLambda<List<ProcessModel<S, T>>?> = null
    private var failedLambda: SuccessLambda<List<ProcessModel<S, T>>?> = null
    private var successLambda: SuccessLambda<List<ProcessModel<S, T>>?> = null
    private var finishLambda: PairLambda<List<ProcessModel<S, T>>?, List<ProcessModel<S, T>>?> =
        null

    private val engine = ProcessTaskImpl<S, T>(processEngine)


    init {
        engine.handleTaskList(processList)
        engine.onItemResult = object : OnItemResult<ProcessModel<S, T>> {
            override fun onItemStart(startResult: ProcessModel<S, T>) {
                super.onItemStart(startResult)
                itemStartLambda?.invoke(startResult)
            }

            override fun onItemDoing(doingResult: ProcessModel<S, T>) {
                super.onItemDoing(doingResult)
                itemDoingLambda?.invoke(doingResult)
            }

            override fun onItemInterrupted(interruptedResult: ProcessModel<S, T>) {
                super.onItemInterrupted(interruptedResult)
                itemInterruptedLambda?.invoke(interruptedResult)
            }

            override fun onItemFailed(failedResult: ProcessModel<S, T>) {
                super.onItemFailed(failedResult)
                itemFailedLambda?.invoke(failedResult)
            }

            override fun onItemSuccess(successResult: ProcessModel<S, T>) {
                super.onItemSuccess(successResult)
                itemSuccessLambda?.invoke(successResult)
            }

            override fun onItemFinish(finishResult: ProcessModel<S, T>) {
                itemFinishLambda?.invoke(finishResult)
            }
        }
        engine.onAllResult = object : OnAllResult<List<ProcessModel<S, T>>> {
            override fun onStart() {
                super.onStart()
                startLambda?.invoke(Unit)
            }

            override fun onDoing(doingItem: List<ProcessModel<S, T>>?) {
                super.onDoing(doingItem)
                doingLambda?.invoke(doingItem)
            }

            override fun onFailed(failedResult: List<ProcessModel<S, T>>?) {
                super.onFailed(failedResult)
                failedLambda?.invoke(failedResult)
            }

            override fun onSuccess(successResult: List<ProcessModel<S, T>>?) {
                super.onSuccess(successResult)
                successLambda?.invoke(successResult)
            }

            override fun onFinish(
                finishResult: List<ProcessModel<S, T>>?,
                unFinishResult: List<ProcessModel<S, T>>?
            ) {
                finishLambda?.invoke(finishResult, unFinishResult)
            }
        }
    }


    fun onItemStart(itemStartLambda: SuccessLambda<ProcessModel<S, T>>) {
        this.itemStartLambda = itemStartLambda
    }

    fun onItemDoing(itemDoingLambda: SuccessLambda<ProcessModel<S, T>>) {
        this.itemDoingLambda = itemDoingLambda
    }

    fun onItemInterrupted(itemInterruptedLambda: SuccessLambda<ProcessModel<S, T>>) {
        this.itemInterruptedLambda = itemInterruptedLambda
    }

    fun onItemFailed(itemFailedLambda: SuccessLambda<ProcessModel<S, T>>) {
        this.itemFailedLambda = itemFailedLambda
    }

    fun onItemSuccess(itemSuccessLambda: SuccessLambda<ProcessModel<S, T>>) {
        this.itemSuccessLambda = itemSuccessLambda
    }

    fun onItemFinish(itemFinishLambda: SuccessLambda<ProcessModel<S, T>>) {
        this.itemFinishLambda = itemFinishLambda
    }

    fun onStart(startLambda: SuccessLambda<Unit>) {
        this.startLambda = startLambda
    }

    fun onDoing(doingLambda: SuccessLambda<List<ProcessModel<S, T>>?>) {
        this.doingLambda = doingLambda
    }

    fun onFailed(failedLambda: SuccessLambda<List<ProcessModel<S, T>>?>) {
        this.failedLambda = failedLambda
    }

    fun onSuccess(successLambda: SuccessLambda<List<ProcessModel<S, T>>?>) {
        this.successLambda = successLambda
    }

    fun onFinish(
        finishLambda: PairLambda<List<ProcessModel<S, T>>?, List<ProcessModel<S, T>>?>
    ) {
        this.finishLambda = finishLambda
    }

}