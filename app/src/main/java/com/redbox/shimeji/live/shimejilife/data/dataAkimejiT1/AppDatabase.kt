package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.mascotdb.Mascots
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.mascotdb.MascotsDao
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.shimejidb.RoomShimeji
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT1.shimejidb.ShimejiDao
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * The Room database for this app
 */
@Database(
    entities = [RoomShimeji::class, Mascots::class
    ], version = 5, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shimejiDao(): ShimejiDao
    abstract fun mascotsDao(): MascotsDao

    companion object {
        internal val executor: ExecutorService = Executors.newCachedThreadPool()

        // For Singleton instantiation
        @Volatile
        public var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            Timber.e("getInstance")
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        internal fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "RoomShimeji.db")
                .enableMultiInstanceInvalidation()
                .setTransactionExecutor(executor)
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }
}