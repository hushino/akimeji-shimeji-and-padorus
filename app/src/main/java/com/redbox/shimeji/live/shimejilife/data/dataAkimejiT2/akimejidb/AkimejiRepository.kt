package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.akimejidb

import android.graphics.Bitmap
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.AkimejiListing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AkimejiRepository(private val akimejit2: AkimejiDao) {

    suspend fun getShimejis(id: Int, usedFrames: HashSet<Int>): HashMap<Int, Bitmap> {
        return withContext(Dispatchers.IO) { akimejit2.getMascotAssets22(id, usedFrames) }
    }


    suspend fun mascotThumbnails(): ArrayList<AkimejiListing> {
        return withContext(Dispatchers.IO) {
            akimejit2.getmascotThumbnails22()
        }
    }
}