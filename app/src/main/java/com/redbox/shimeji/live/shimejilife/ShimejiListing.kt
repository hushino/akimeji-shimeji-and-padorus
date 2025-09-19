package com.redbox.shimeji.live.shimejilife

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
class ShimejiListing() {

    private val downloadInProgress = AtomicBoolean(false)
    var id: Int = 0
    var name: String? = ""
    var author: String? = ""
    var nick: String? = ""
    var category: String? = ""
    var visibility: Boolean? = true
    var status: Int = R.string.tap_to_download
    var progressBarUpdate: Int = 0
    var isMarkedForDownload: Boolean = false
    var thumbnailShimeji2: ByteArray? = null
    var isFromServer: Boolean = false

    var thumb: String = ""
    var shimejiGif: String = ""

    var setStatuss: Int
        get() = this.status
        set(value) {
            status = value
        }

//    val isDownloadInProgress: Boolean
//        get() = this.downloadInProgress.get()
//
//    fun getThumbnail2(): Bitmap {
//        return byteArrayToBitmap(this.thumbnailShimeji2!!)
//    }

    fun byteArrayToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeStream(ByteArrayInputStream(bytes))
    }

  /*  internal fun startDownload() {
        this.downloadInProgress.set(true)
    }

    internal fun notifyDownloadFinished() {
        this.downloadInProgress.set(false)
    }*/


}