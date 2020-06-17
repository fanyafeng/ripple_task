package com.ripple.task.util

import java.io.File

/**
 * Author: fanyafeng
 * Data: 2020/5/8 09:23
 * Email: fanyafeng@live.cn
 * Description:
 */
object HandleFileUtil {

    fun delFile(file: File?) {
        file?.deleteOnExit()
    }

    fun delFileList(fileList: List<File?>?) {
        fileList?.forEachIndexed { _, file ->
            delFile(file)
        }

    }
}