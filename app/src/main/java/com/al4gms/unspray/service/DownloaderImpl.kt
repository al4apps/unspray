package com.al4gms.unspray.service

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.al4gms.unspray.data.AccessToken
import com.al4gms.unspray.utils.haveM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import javax.inject.Inject

class DownloaderImpl @Inject constructor(
    private val context: Context,
) : Downloader() {

    private val downloadManager = if (haveM()) {
        context.getSystemService(DownloadManager::class.java)
    } else {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    override fun downloadFileWithDM(url: String): Long {
        val filename = (url.substringAfterLast("/")).substringBefore("?") + ".jpg"
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("image/jpeg")
            .addRequestHeader("Authorization", "Bearer ${AccessToken.accessToken}")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(filename)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
        return downloadManager.enqueue(request)
    }

    override suspend fun downloadFile(url: String) {
        (Dispatchers.IO) {
        }
    }
}
