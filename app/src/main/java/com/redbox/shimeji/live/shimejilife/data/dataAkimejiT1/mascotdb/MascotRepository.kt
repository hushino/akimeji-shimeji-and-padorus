package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.mascotdb

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.redbox.shimeji.live.shimejilife.di.ServiceLocator
import timber.log.Timber

/**
 * Repository module for handling data operations.
 */
class MascotRepository(private val mascotsDao: MascotsDao, context: Context) {

    private val helper = ServiceLocator.helper
    private val shimejiListing = ServiceLocator.shimejiListing

    fun addMascotToDatabase(id: Int, name: String, frames: List<Bitmap>) = mascotsDao.addMascotToDatabase2(id, name, frames, helper)

    fun getLiveDataOfMascotsInDb(): LiveData<List<Mascots?>>? {
        return try {
            mascotsDao.getMascotsRecentlyAdd()
        } catch (e: Exception) {
            Timber.e(e)
            return null
        } as LiveData<List<Mascots?>>
    }

}