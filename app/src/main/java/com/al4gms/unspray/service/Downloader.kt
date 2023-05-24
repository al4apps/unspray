package com.al4gms.unspray.service

abstract class Downloader {

    abstract fun downloadFileWithDM(url: String): Long
    abstract suspend fun downloadFile(url: String)
}
