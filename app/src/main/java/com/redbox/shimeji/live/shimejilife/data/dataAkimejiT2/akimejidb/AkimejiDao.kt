package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.akimejidb

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.AkimejiListing
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.petdb.PetsWithAkimeji
import timber.log.Timber
import java.io.ByteArrayInputStream


@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
abstract class AkimejiDao {


    @Query("SELECT bitmap FROM akimeji WHERE mascot = :id ORDER BY frame ASC")
    abstract fun getMascotAssets(id: Int): Cursor

//    @Query("SELECT bitmap FROM akimeji WHERE mascot = :id ORDER BY frame ASC")
//    abstract fun getMascotAssets2(id: Int): LiveData<List<RoomAkimejit2>>
//
//    @Transaction
//    @Query("Select M.id,M.name,B.bitmap from pets as M INNER JOIN akimeji as B ON M.id=B.mascot WHERE B.frame=0")
//    abstract fun getmascotThumbnails(): List<PetsWithAkimeji>

    @Transaction
    @Query("Select M.id,M.name,B.bitmap from pets as M INNER JOIN akimeji as B ON M.id=B.mascot WHERE B.frame=0")
    abstract fun getmascotThumbnails2(): Cursor

    fun getmascotThumbnails22(): ArrayList<AkimejiListing> {
        val c = getmascotThumbnails2()
        val list = ArrayList<AkimejiListing>()
        if (c.moveToFirst()) {
            while (!c.isAfterLast) {
                val thumb = AkimejiListing()
                thumb.id = c.getInt(c.getColumnIndexOrThrow("id"))
                // tal vez deba comentarlo
                thumb.name = c.getString(c.getColumnIndexOrThrow("name"))
                thumb.thumbnailShimeji2 = c.getBlob(c.getColumnIndexOrThrow("bitmap"))
                /*Timber.e(
                    "getmascotThumbnails22 transaction: $c  id: ${thumb.id} name: ${thumb.name} bitmap: ${thumb.thumbnailShimeji2}"
                )*/
                list.add(thumb)
                c.moveToNext()
            }
        }
        c.close()
        return list
    }


    suspend fun getMascotAssets22(id: Int, usedFrames: HashSet<Int>): HashMap<Int, Bitmap> {
        val map = HashMap<Int, Bitmap>(100)
        val c = getMascotAssets(id)
        var position: Int? = 0
        if (c.moveToFirst()) {
            while (!c.isAfterLast) {
                if (usedFrames.contains(position)) {
                    map.put(
                        position!!,
                        byteArrayToBitmap(c.getBlob(c.getColumnIndexOrThrow("bitmap")))
                    )
                }
                position = position!! + 1
                c.moveToNext()
            }
        }
        c.close()
        return map
    }

    fun byteArrayToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeStream(ByteArrayInputStream(bytes))
    }
}