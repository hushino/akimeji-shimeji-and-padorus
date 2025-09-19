package   com.redbox.shimeji.live.shimejilife.data.repoT1

import android.graphics.Bitmap
import com.redbox.shimeji.live.shimejilife.ShimejiListing
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.mascotdb.MascotRepository
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.shimejidb.ShimejiRepository
import com.redbox.shimeji.live.shimejilife.di.ServiceLocator
import kotlinx.coroutines.runBlocking
import timber.log.Timber

open class TeamListingService() : ArrayList<ShimejiListing?>() {

    private val db: ShimejiRepository = ServiceLocator.shimejiRepository
    private val helper: Helper = ServiceLocator.helper
    private val provideMascotRepository: MascotRepository = ServiceLocator.mascotRepository

    internal var mascotLimit: Int = 2
    private var nextInsertPosition: Int = 0

    companion object {

        var cachedThumbs: ArrayList<ShimejiListing> = ArrayList()

    }

    init {
        runBlocking {
            cachedThumbs = db.mascotThumbnails()
        }
        Timber.e("cachedThumbs.size: ${cachedThumbs.size}")
        val ids = helper.getActiveTeamMembers()
        Timber.e("Active ids at startup: %s", ids)
        for (id in ids) {
            Timber.e("id: $id")
            getThumbById(id)?.let { super.add(it) }
        }
        this.nextInsertPosition = this.mascotLimit
    }

    private val allMascotIds: List<Int?>
        get() {
            val list = ArrayList<Int>()
            for (m in cachedThumbs) {
                m.id.let { Integer.valueOf(it) }?.let { list.add(it) }
            }
            return list
        }

    val getAllThumbs: MutableList<ShimejiListing>
        get() = cachedThumbs

    fun getThumbById(id: Int): ShimejiListing? {
        Timber.e("getThumbById: $id")
        Timber.e("getThumbById cachedThumbs.size: ${cachedThumbs.size}")
        for (m: ShimejiListing in cachedThumbs) {
            Timber.e("1")
            Timber.e("${m.name}")
            if (m.id == id) {
                Timber.e("2")
                return m
            }
        }
        Timber.e("return null: $id")
        return null
    }

    fun hasTeamMember(id: Int): Boolean {
        for (m in cachedThumbs) {
            if (m.id == id) {
                Timber.e("hasTeamMember true")
                return true
            }
        }
        Timber.e("hasTeamMember false")
        return false
    }

    fun addMascot(listing: ShimejiListing, frames: List<Bitmap>) {
        try {
            val asde = helper.bitmapToByteArray(frames[0])
            if (asde != null && asde.isNotEmpty()) {
                listing.thumbnailShimeji2 = asde
            }
            cachedThumbs.add(listing)

            provideMascotRepository.addMascotToDatabase(
                listing.id,
                listing.name!!,
                frames
            )
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

    override fun add(element: ShimejiListing?): Boolean {
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

    override fun get(index: Int): ShimejiListing? {
        if (index >= size) {
            return null
        }
        return super.get(index)

    }

    internal fun remove(position: Int): ShimejiListing? {
        if (position >= size) {
            return ShimejiListing()
        }
        this.nextInsertPosition = position
        return super.removeAt(position)
    }


}
