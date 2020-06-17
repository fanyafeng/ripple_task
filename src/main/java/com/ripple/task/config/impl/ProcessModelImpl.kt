package com.ripple.task.config.impl

import com.ripple.task.config.ProcessModel

/**
 * Author: fanyafeng
 * Data: 2020/6/4 16:55
 * Email: fanyafeng@live.cn
 * Description:
 */
class ProcessModelImpl : ProcessModel<String, String> {
    override fun getSource(): String {
        return ""
    }

    override fun getTarget(): String? {
        return ""
    }

    override fun setTarget(target: String) {
    }

    override fun parse(source: String, target: String?): String {
        return ""
    }

}