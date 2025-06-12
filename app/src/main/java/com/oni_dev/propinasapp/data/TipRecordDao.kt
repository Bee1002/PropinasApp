package com.oni_dev.propinasapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TipRecordDao {
    @Query("SELECT * FROM tip_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<TipRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: TipRecordEntity)

    @Delete
    suspend fun delete(record: TipRecordEntity)

    @Query("DELETE FROM tip_records")
    suspend fun deleteAll()
} 