package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.redbox.shimeji.live.shimejilife.R
import com.redbox.shimeji.live.shimejilife.common.constants.AppConstants
import java.io.ByteArrayInputStream
import java.util.concurrent.atomic.AtomicBoolean

class AkimejiListing() {

    //private val downloadInProgress = AtomicBoolean(false)
    var downloadInProgress = AtomicBoolean(false)
    var id: Int = 0
    var idOriginal: Int = 0
    var name: String? = ""
    var author: String? = ""
    var nick: String? = ""
    var status: Int = R.string.tap_to_download
    var progressBarUpdate: Int = 0
    var isMarkedForDownload: Boolean = false
    var thumbnailShimeji2: ByteArray? = null
    var isFromServer: Boolean = false

    var setStatuss: Int
        get() = this.status
        set(value) {
            status = value
        }

    val isDownloadInProgress: Boolean
        get() = this.downloadInProgress.get()

    val url: String
        get() = AppConstants.SERVER_BASE_PATH + "akimejiT2/thumb/" + this.id

    fun getThumbnail2(): Bitmap {
        val loco = this.thumbnailShimeji2?.let { byteArrayToBitmap(it) }
        return requireNotNull(loco, { "Listing Thumbnail bitmap was not set 2 " })
    }

    internal fun startDownload() {
        this.downloadInProgress.set(true)
    }

    internal fun notifyDownloadFinished() {
        this.downloadInProgress.set(false)
    }


    fun byteArrayToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeStream(ByteArrayInputStream(bytes))
    }
}