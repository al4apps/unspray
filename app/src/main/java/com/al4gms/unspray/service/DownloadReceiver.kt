package com.al4gms.unspray.service

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.al4gms.unspray.BuildConfig
import com.al4gms.unspray.utils.haveM
import java.io.File

class DownloadReceiver(private val downloadId: Long) : BroadcastReceiver() {

    private val _intentLiveData = MutableLiveData<Intent>()
    val intentLiveData: LiveData<Intent>
        get() = _intentLiveData

    override fun onReceive(context: Context?, intent: Intent?) {
        val downloadManager = if (haveM()) {
            context?.getSystemService(DownloadManager::class.java)
        } else {
            context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        }
        if (context != null && intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE &&
            downloadManager != null
        ) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id == downloadId) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val downloadStatus =
                        cursor.getIntOrNull(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val downloadLocalUri =
                        cursor.getStringOrNull(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    val downloadMimeType =
                        cursor.getStringOrNull(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
                    if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL &&
                        downloadLocalUri != null &&
                        downloadMimeType != null
                    ) {
                        getFileIntent(context, Uri.parse(downloadLocalUri), downloadMimeType)?.let {
                            _intentLiveData.postValue(it)
                        }
                    }
                }
                cursor.close()
            }
        }
    }

    private fun getFileIntent(context: Context, fileUri: Uri, mimeType: String): Intent? {
        return if (ContentResolver.SCHEME_FILE == fileUri.scheme) {
            val file = File(fileUri.path)
            val newUri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                file,
            )
            val fileIntent = Intent(Intent.ACTION_VIEW)
            fileIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.setDataAndType(newUri, mimeType)
            fileIntent
        } else {
            null
        }
    }
}
