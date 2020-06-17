package com.ripple.task.config

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/6/3 19:07
 * Email: fanyafeng@live.cn
 * Description:
 */
interface ProcessConfig : Serializable {
    /**
     * 目标路径
     */
    fun getTargetPath(): String?

    /**
     * 处理后的目标路径不能为空
     */
    fun setTargetPath(target: String)

    /**
     * 解析处理sourcePath到targetPath
     */
    fun getProcessOption(): ProcessOption
}