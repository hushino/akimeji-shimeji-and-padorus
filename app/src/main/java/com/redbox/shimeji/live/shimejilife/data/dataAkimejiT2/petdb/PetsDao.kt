package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.petdb

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.*
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.akimejidb.RoomAkimejit2
import timber.log.Timber
import java.io.ByteArrayOutputStream

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
abstract class PetsDao {


    @Query("SELECT * FROM pets ORDER BY lastModifiedTime DESC")
    abstract fun getMascotsRecentlyAdd(): LiveData<List<Pets>>


    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun addMascotToDatabase(mascots: Pets /*mascotId: Int, name: String*/)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun addShimejiToDatabase(shimeji: RoomAkimejit2 /*frames: List<Bitmap>*/)

    fun addMascotToDatabase2(mascotId: Int, name: String, frames: List<Bitmap>) {
        Timber.e("DB Akimejit2 .... $mascotId | $name | $frames")
        try {
            val megaframe = bitmapToByteArray(frames[0])
            val gardenPlanting = megaframe?.let { Pets(mascotId, name, it) }
            if (gardenPlanting != null) {
                addMascotToDatabase(gardenPlanting)
            }
            for (i in frames.indices) {
                val roomShimeji2 = bitmapToByteArray(frames[i])?.let {
                    RoomAkimejit2(
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
            Timber.e(e)
        }

    }

    private fun bitmapToByteArray(b: Bitmap): ByteArray? {
        val output: ByteArrayOutputStream
        try {
            output = ByteArrayOutputStream()
            //quality no work for .png
            b.compress(Bitmap.CompressFormat.PNG, 90, output)
        } catch (e: Exception) {
            return byteArrayOf()
        }
        return output.toByteArray()
    }
}