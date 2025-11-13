package com.example.eventhubkampus // Pastikan ini package-mu

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// --- UBAH VERSI MENJADI 2 ---
@Database(entities = [Acara::class], version = 2, exportSchema = false)
abstract class AcaraDatabase : RoomDatabase() {

    abstract fun acaraDao(): AcaraDao

    companion object {
        @Volatile
        private var INSTANCE: AcaraDatabase? = null

        fun getDatabase(context: Context): AcaraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AcaraDatabase::class.java,
                    "acara_database"
                )
                    // --- INI YANG KITA TAMBAHKAN ---
                    // Ini akan menghapus dan membuat ulang database
                    // setiap kali kamu menaikkan nomor 'version'
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}