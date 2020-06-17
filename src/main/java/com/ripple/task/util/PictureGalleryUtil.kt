package com.ripple.task.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

/**
 * Author: fanyafeng
 * Data: 2020/4/26 17:58
 * Email: fanyafeng@live.cn
 * Description:
 */
object PictureGalleryUtil {
    /**
     * 更新系统相册
     */
    fun updatePictureGallery(context: Context, file: File?) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(file)
        intent.data = contentUri
        context.sendBroadcast(intent)
    }
}