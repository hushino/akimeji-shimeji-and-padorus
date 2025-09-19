package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2

import android.graphics.Bitmap
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.akimejidb.AkimejiRepository
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.petdb.PetRepository
import com.redbox.shimeji.live.shimejilife.data.repoT2.HelperT2
import com.redbox.shimeji.live.shimejilife.di.ServiceLocator
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class AkimejiT2TeamListingService() : ArrayList<AkimejiListing?>() {

    private val db: AkimejiRepository = ServiceLocator.akimejiRepository
    private val db2: PetRepository = ServiceLocator.petRepository
    private val helper: HelperT2 = ServiceLocator.helperT2

    internal var mascotLimit: Int = 10
    internal var nextInsertPosition: Int = 0

    public fun traercache() {
        runBlocking {
            try {
                //cachedThumbs.clear()
                cachedThumbs = db.mascotThumbnails()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    companion object {
        //internal var instance: AkimejiT2TeamListingService? = null
        var cachedThumbs: ArrayList<AkimejiListing> = ArrayList()
        //var cachedThumbs2: ArrayList<AkimejiListing> = ArrayList()

        /*fun getInstance(): AkimejiT2TeamListingService {
            if (instance == null) {
                instance =
                    AkimejiT2TeamListingService()
            }
            return instance as AkimejiT2TeamListingService
        }*/
    }

    init {
        //val db2 = db
        runBlocking {
            try {
                cachedThumbs = db.mascotThumbnails()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        //Timber.e("cachedThumbs.size T2 init: ${cachedThumbs.size}")
        val ids = helper.getActiveTeamMembers()
        for (id in ids) {
            //Timber.e("getThumbById T2 init: $id")
            try {
                getThumbById(id)?.let { super.add(it) }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        this.nextInsertPosition = this.mascotLimit
    }

    private val allMascotIds: List<Int?>
        get() {
            val list = ArrayList<Int>()
            for (m in cachedThumbs) {
                m?.id?.let { Integer.valueOf(it) }?.let { list.add(it) }
            }
            return list
        }

    val getAllThumbs: MutableList<AkimejiListing>
        get() = cachedThumbs

    fun getThumbById(id: Int?): AkimejiListing? {
        //Timber.e("getThumbById T2: $id")
        //Timber.e("getThumbById cachedThumbs.size T2: ${cachedThumbs.size}, getThumbById T2: $id")
        if (id != null) {
            for (m: AkimejiListing? in cachedThumbs) {
                // Timber.e("getThumbById T2: ${m.id}  ${m.name}")
                if (m?.id == id) {
                    return m
                }
            }
        }
        Timber.e("return null T2: $id")
        return null
    }



    /*fun getThumbByIdOnline(id: Int): AkimejiListing? {
        for (m: AkimejiListing in cachedThumbs2) {
            if (m.id == id) {
                return m
            }
        }
        Timber.e("return null T2 Online: $id")
        return cachedThumbs2[id]
    }*/

    fun hasTeamMember(id: Int): Boolean {
        for (m in cachedThumbs) {
            if (m?.id == id) {
                Timber.e("hasTeamMember true")
                return true
            }
        }
        Timber.e("hasTeamMember false")
        return false
    }

    suspend fun addMascot(listing: AkimejiListing, frames: List<Bitmap>) {
        try {
            val asde = helper.bitmapToByteArray(frames[0])
            if (asde != null && asde.isNotEmpty()) {
                listing.thumbnailShimeji2 = asde
            }
            cachedThumbs.add(listing)
            //traercache()
            //listing.name?.let {
            db2.addMascotToDatabase(
                listing.id,
                listing.name!!,
                frames
            )
            //}
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    internal val mascotIDs: List<Int>
        get() {
            val list = ArrayList<Int>(10)
            var i = 0
            while (i < size && i < this.mascotLimit) {
                get(i)?.id?.let { Integer.valueOf(it) }?.let { list.add(it) }
                i++
            }
            return list
        }

    //lo llama el main activity o lo q sea si la compra fue exitosa
    fun setMascotLimit(limit: Int) {
        this.mascotLimit = limit
        this.nextInsertPosition = size
    }

    fun mascotExistsAt(position: Int): Boolean {
        return get(position) != null
    }

    internal fun isOutOfBounds(position: Int): Boolean {
        return position >= this.mascotLimit
    }

    override fun add(element: AkimejiListing?): Boolean {
        if (size < this.mascotLimit) {
            this.nextInsertPosition++
            return super.add(element)
        }
        if (this.nextInsertPosition >= this.mascotLimit) {
            this.nextInsertPosition = 0
        }
        set(this.nextInsertPosition, element)
        this.nextInsertPosition++
        return true
    }

    override fun get(index: Int): AkimejiListing? {
        if (index >= size) {
            return null
        }
        return super.get(index)

    }

    internal fun remove(position: Int): AkimejiListing? {
        if (position >= size) {
            return AkimejiListing()
        }
        this.nextInsertPosition = position
        return super.removeAt(position)
    }
}