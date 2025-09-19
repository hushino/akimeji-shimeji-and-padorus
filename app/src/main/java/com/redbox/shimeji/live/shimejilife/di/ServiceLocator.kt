package com.redbox.shimeji.live.shimejilife.di

import android.content.Context
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.Akimejit2Database
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.akimejidb.AkimejiDao
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.akimejidb.AkimejiRepository
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.petdb.PetRepository
import com.redbox.shimeji.live.shimejilife.data.repoT2.HelperT2
import com.redbox.shimeji.live.shimejilife.data.repoT2.SpritesServiceAkimejiT2

import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.AppDatabase
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.shimejidb.ShimejiRepository
import com.redbox.shimeji.live.shimejilife.data.repoT1.Helper
import com.redbox.shimeji.live.shimejilife.data.repoT1.SpritesService
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.mascotdb.MascotRepository

// Retrofit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.redbox.shimeji.live.shimejilife.network.ApiService
import com.redbox.shimeji.live.shimejilife.common.constants.AkimejiT2Constants
import com.redbox.shimeji.live.shimejilife.ShimejiListing
/**
 * Manual Service Locator used to provide dependencies after removing Hilt.
 * Initialize with ServiceLocator.init(applicationContext) from Application.onCreate().
 */
object ServiceLocator {
    lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // Retrofit + Api
    private val retrofitShimeji: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://akimeji.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val retrofitAkimeji: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AkimejiT2Constants.SERVER_BASE_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiServiceShimeji: ApiService by lazy { retrofitShimeji.create(ApiService::class.java) }
    val apiServiceAkimeji: ApiService by lazy { retrofitAkimeji.create(ApiService::class.java) }

    // T2 Database and related
    val akimejit2Database: Akimejit2Database by lazy { Akimejit2Database.getInstance(appContext) }
    val akimejiDao: AkimejiDao by lazy { akimejit2Database.akimejiDao() }

    val helperT2: HelperT2 by lazy { HelperT2(appContext) }
    val akimejiRepository: AkimejiRepository by lazy { AkimejiRepository(akimejiDao) }
    val petRepository: PetRepository by lazy { PetRepository(akimejit2Database.petsDao()) }
    val spritesServiceAkimejiT2: SpritesServiceAkimejiT2 by lazy { SpritesServiceAkimejiT2(akimejiRepository, helperT2) }

    // T1 Database and related
    val appDatabase: AppDatabase by lazy { AppDatabase.getInstance(appContext) }
    val shimejiDao by lazy { appDatabase.shimejiDao() }
    val mascotsDao by lazy { appDatabase.mascotsDao() }

    val helper: Helper by lazy { Helper(appContext) }
    val shimejiListing: ShimejiListing by lazy { ShimejiListing() }
    val shimejiRepository: ShimejiRepository by lazy { ShimejiRepository(shimejiDao, shimejiListing) }
    val mascotRepository: MascotRepository by lazy { MascotRepository(mascotsDao, appContext) }
    val spritesService: SpritesService by lazy { SpritesService(helper, shimejiRepository) }
}
