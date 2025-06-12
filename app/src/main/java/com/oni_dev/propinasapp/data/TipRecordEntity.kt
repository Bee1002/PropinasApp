package com.oni_dev.propinasapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "tip_records")
@TypeConverters(Converters::class)
data class TipRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val totalAmount: Double,
    val employeeCount: Int,
    val amountPerEmployee: Double,
    val employeeNames: List<String>,
    val date: Date
) 