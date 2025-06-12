package com.oni_dev.propinasapp

import androidx.room.*
import java.util.Date

@Dao
interface TipRecordDao {
    @Query("SELECT * FROM tip_records ORDER BY date DESC")
    suspend fun getAllRecords(): List<TipRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: TipRecordEntity)

    @Delete
    suspend fun delete(record: TipRecordEntity)

    @Query("DELETE FROM tip_records WHERE date = :date")
    suspend fun deleteByDate(date: Date)

    @Query("DELETE FROM tip_records")
    suspend fun deleteAll()
} 