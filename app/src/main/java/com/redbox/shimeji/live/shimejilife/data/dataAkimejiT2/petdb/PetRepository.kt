package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.petdb

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PetRepository(private val petDao: PetsDao) {

    suspend fun addMascotToDatabase(id: Int, name: String, frames: List<Bitmap>) =
        withContext(Dispatchers.IO) { petDao.addMascotToDatabase2(id, name, frames) }

    fun getLiveDataOfMascotsInDb() = petDao.getMascotsRecentlyAdd()

}