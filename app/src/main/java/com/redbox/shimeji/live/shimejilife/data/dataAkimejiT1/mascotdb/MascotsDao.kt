package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.mascotdb

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.*
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.shimejidb.RoomShimeji
import com.redbox.shimeji.live.shimejilife.data.repoT1.Helper
import timber.log.Timber

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
abstract class MascotsDao {

    //private val helper: Helper by inject()

    /*@Transaction
    @Query("Select M.id,M.name,B.bitmap from mascots as M INNER JOIN shimeji as B ON M.id=B.mascot WHERE B.frame=0 ORDER BY lastModifiedTime ASC")
    abstract fun getMascotsRecentlyAdd2(): LiveData<List<Mascots>>*/

    @Query(" SELECT * FROM mascots ORDER BY lastModifiedTime DESC")
    abstract fun getMascotsRecentlyAdd(): LiveData<List<Mascots?>?>?

    //antes de que los frames sean enviados deben convertirse a HelperT2.bitmapToByteArray(frames[i])
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun addMascotToDatabase(mascots: Mascots /*mascotId: Int, name: String*/)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun addShimejiToDatabase(shimeji: RoomShimeji /*frames: List<Bitmap>*/)

    fun addMascotToDatabase2(mascotId: Int, name: String, frames: List<Bitmap>, helper: Helper) {
        Timber.e("DB .... $mascotId | $name | $frames")
        try {
            val megaframe = helper.bitmapToByteArray(frames[0])
            val gardenPlanting = megaframe?.let { Mascots(mascotId, name, it) }
            if (gardenPlanting != null) {
                addMascotToDatabase(gardenPlanting)
            }
            for (i in frames.indices) {
                val roomShimeji2 = helper.bitmapToByteArray(frames[i])?.let {
                    RoomShimeji(
                        null, // auto generado
                        it,
                        Integer.valueOf(i),
                        Integer.valueOf(mascotId)
                    )
                }
                if (roomShimeji2 != null) {
                    addShimejiToDatabase(roomShimeji2)
                }
            }
        } catch (e: Exception) {

        }

    }
}