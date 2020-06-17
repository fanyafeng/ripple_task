package com.ripple.task.task.impl

import com.ripple.task.engine.ScheduledProcessEngine
import com.ripple.task.task.ScheduledProcessTask
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Author: fanyafeng
 * Data: 2020/6/11 15:04
 * Email: fanyafeng@live.cn
 * Description:
 */

class ScheduledProcessTaskImpl @JvmOverloads constructor(private var scheduledProcessEngine: ScheduledProcessEngine = ScheduledProcessEngine.SINGLE_THREAD_EXECUTOR) :
    ScheduledProcessTask {
    override fun getScheduledProcessEngine(): ScheduledProcessEngine {
        return scheduledProcessEngine
    }

    fun scheduleAtFixedRate(
        command: Runnable,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ): ScheduledProcessEngine {
        scheduledProcessEngine.getScheduledProcessService().scheduleAtFixedRate(command, initialDelay, period, unit)
        return scheduledProcessEngine
    }

    fun scheduleAtFixedRate(
        command: (() -> Unit),
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ): ScheduledProcessEngine {
        scheduledProcessEngine.getScheduledProcessService().scheduleAtFixedRate(command, initialDelay, period, unit)
        return scheduledProcessEngine
    }

    /**
     * 主要针对kt的封装
     * 直接传入方法
     */
    fun schedule(command: (() -> Unit), delay: Long, unit: TimeUnit): ScheduledProcessEngine {
        val task = getScheduledProcessEngine()
        task.getScheduledProcessService().schedule(command, delay, unit)
        return scheduledProcessEngine
    }

    fun schedule(command: Runnable, delay: Long, unit: TimeUnit): ScheduledProcessEngine {
        val task = getScheduledProcessEngine()
        task.getScheduledProcessService().schedule(command, delay, unit)
        return scheduledProcessEngine
    }

    fun scheduleWithFixedDelay(
        command: Runnable,
        initialDelay: Long,
        delay: Long,
        unit: TimeUnit
    ): ScheduledProcessEngine {
        val task = getScheduledProcessEngine()
        task.getScheduledProcessService().scheduleWithFixedDelay(command, initialDelay, delay, unit)
        return scheduledProcessEngine
    }

    fun scheduleWithFixedDelay(
        command: (() -> Unit),
        initialDelay: Long,
        delay: Long,
        unit: TimeUnit
    ): ScheduledProcessEngine {
        val task = getScheduledProcessEngine()
        task.getScheduledProcessService().scheduleWithFixedDelay(command, initialDelay, delay, unit)
        return scheduledProcessEngine
    }

}