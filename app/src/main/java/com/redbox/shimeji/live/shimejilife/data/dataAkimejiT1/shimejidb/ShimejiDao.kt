package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.shimejidb

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.redbox.shimeji.live.shimejilife.ShimejiListing
import timber.log.Timber
import java.io.ByteArrayInputStream

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
abstract class ShimejiDao {

    //private val helper: Helper by inject()


    // bitmap is the projection part and return only bitmap
    // luego de obtener usar el usedFrames: HashSet<Int> para filtrar los bitmap
    @Query("SELECT bitmap FROM shimeji WHERE mascot = :id ORDER BY frame ASC")
    abstract fun getMascotAssets(id: Int): Cursor


//    @Query("SELECT bitmap FROM shimeji WHERE mascot = :id ORDER BY frame ASC")
//    abstract fun getMascotAssets2(id: Int): LiveData<List<RoomShimeji>>
//
//    @Transaction
//    @Query("Select M.id,M.name,B.bitmap from mascots as M INNER JOIN shimeji as B ON M.id=B.mascot WHERE B.frame=0")
//    abstract fun getmascotThumbnails(): List<MascotsWithShimeji>

    //abstract fun _getmascotThumbnails(): LiveData<List<MascotsWithShimeji>>
    @Transaction
    @Query("Select M.id,M.name,B.bitmap from mascots as M INNER JOIN shimeji as B ON M.id=B.mascot WHERE B.frame=0")
    abstract fun getmascotThumbnails2(): Cursor


    suspend fun getmascotThumbnails22(shimejiListing: ShimejiListing): ArrayList<ShimejiListing> {
        val c = getmascotThumbnails2()
        val list = ArrayList<ShimejiListing>()
        if (c.moveToFirst()) {
            while (!c.isAfterLast) {
                val idCol = c.getColumnIndexOrThrow("id")
                val nameCol = c.getColumnIndexOrThrow("name")
                val bmpCol = c.getColumnIndexOrThrow("bitmap")
                // Skip if blob is null to avoid NPEs later
                if (c.isNull(bmpCol)) {
                    Timber.e("Thumbnail blob was null for mascot id=%s", c.getInt(idCol))
                } else {
                    val thumb = ShimejiListing()
                    thumb.id = c.getInt(idCol)
                    thumb.name = c.getString(nameCol)
                    thumb.thumbnailShimeji2 = c.getBlob(bmpCol)
                    Timber.e("getmascotThumbnails22 built thumb: id=%s name=%s bytes=%s", thumb.id, thumb.name, thumb.thumbnailShimeji2?.size)
                    list.add(thumb)
                }
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
                        //helper.byteArrayToBitmap(c.getBlob(c.getColumnIndexOrThrow("bitmap")))
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