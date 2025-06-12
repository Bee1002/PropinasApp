package com.oni_dev.propinasapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TipRecordEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class TipDatabase : RoomDatabase() {
    abstract fun tipRecordDao(): TipRecordDao

    companion object {
        @Volatile
        private var INSTANCE: TipDatabase? = null

        fun getDatabase(context: Context): TipDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TipDatabase::class.java,
                    "tip_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 