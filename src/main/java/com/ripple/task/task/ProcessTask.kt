package com.ripple.task.task

import com.ripple.task.callback.result.OnAllResult
import com.ripple.task.callback.result.OnItemResult
import com.ripple.task.config.ProcessModel
import com.ripple.task.engine.ProcessEngine

/**
 * Author: fanyafeng
 * Data: 2020/6/3 19:39
 * Email: fanyafeng@live.cn
 * Description:
 */
interface ProcessTask<S, T> {

    /**
     * 所有单个任务回调，任务回调包含以下所有回调，但是为了简化使用
     * 除去单项任务完成回调必须重写其余进行了空实现
     *
     * 单项任务开始回调
     * [com.ripple.task.callback.OnItemStart]
     *
     * 单项任务进行回调
     * [com.ripple.task.callback.OnItemDoing]
     *
     * 单项任务打断回调
     * [com.ripple.task.callback.OnItemInterrupted]
     *
     * 单项任务失败回调
     * [com.ripple.task.callback.OnItemFailed]
     *
     * 单项任务成功回调
     * [com.ripple.task.callback.OnItemSuccess]
     *
     * 单项任务完成回调
     * [com.ripple.task.callback.OnItemFinish]
     */
    fun getItemResult(): OnItemResult<ProcessModel<S, T>>?

    /**
     * 所有任务回调，基本同上除去具体回调
     *
     * 所有任务开始回调
     * [com.ripple.task.callback.OnStart]
     *
     * 所有任务进行回调
     * [com.ripple.task.callback.OnDoing]
     *
     * 所有任务失败回调
     * [com.ripple.task.callback.OnFailed]
     *
     * 所有任务成功回调
     * [com.ripple.task.callback.OnSuccess]
     *
     * 所有任务完成结束回调
     * [com.ripple.task.callback.OnFinish]
     */
    fun getAllResult(): OnAllResult<List<ProcessModel<S, T>>>?

    /**
     * 获取任务处理器引擎
     * [com.ripple.task.engine.ProcessEngine]
     */
    fun getProcessEngine(): ProcessEngine
}