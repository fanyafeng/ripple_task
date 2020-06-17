package com.ripple.task.task

import com.ripple.task.engine.ScheduledProcessEngine

/**
 * Author: fanyafeng
 * Data: 2020/6/11 15:12
 * Email: fanyafeng@live.cn
 * Description:
 */
interface ScheduledProcessTask {
    fun getScheduledProcessEngine(): ScheduledProcessEngine
}