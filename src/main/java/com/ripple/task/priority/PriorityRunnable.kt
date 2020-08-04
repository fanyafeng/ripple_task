package com.ripple.task.priority


/**
 * Author: fanyafeng
 * Data: 2020/7/21 09:19
 * Email: fanyafeng@live.cn
 * Description:
 */
internal class PriorityRunnable(priority: Priority?, private val runnable: Runnable) : Runnable {

    var SEQ: Long = 0L
    val priority: Priority = priority ?: Priority.DEFAULT

    override fun run() {
        this.runnable.run()
    }
}