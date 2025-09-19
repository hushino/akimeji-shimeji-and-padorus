package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.shimejidb

import android.graphics.Bitmap
import com.redbox.shimeji.live.shimejilife.ShimejiListing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShimejiRepository(private val shimejiDao: ShimejiDao, private val shimejiListing: ShimejiListing) {

    suspend fun getShimejis(id: Int, usedFrames: HashSet<Int>): HashMap<Int, Bitmap> {
        return withContext(Dispatchers.IO) { shimejiDao.getMascotAssets22(id, usedFrames) }
    }


    suspend fun mascotThumbnails(): ArrayList<ShimejiListing> {
        return withContext(Dispatchers.IO) {
            shimejiDao.getmascotThumbnails22(shimejiListing)
        }
    }
}