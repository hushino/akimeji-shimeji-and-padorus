package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.akimejidb.AkimejiDao
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.akimejidb.RoomAkimejit2
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.petdb.Pets
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.petdb.PetsDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(
    entities = [RoomAkimejit2::class, Pets::class], version = 1, exportSchema = false
)
abstract class Akimejit2Database : RoomDatabase() {
    abstract fun akimejiDao():AkimejiDao
    abstract fun petsDao():PetsDao

    companion object {
        private val executor: ExecutorService = Executors.newCachedThreadPool()

        // For Singleton instantiation
        @Volatile
        internal var instance: Akimejit2Database? = null

        fun getInstance(context: Context): Akimejit2Database {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        fun buildDatabase(context: Context): Akimejit2Database {
            return Room.databaseBuilder(context, Akimejit2Database::class.java, "RoomAkimejit2.db")
                .enableMultiInstanceInvalidation()
                .setTransactionExecutor(executor)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}