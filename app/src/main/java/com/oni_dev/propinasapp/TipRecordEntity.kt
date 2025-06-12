package com.oni_dev.propinasapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "tip_records")
@TypeConverters(Converters::class)
data class TipRecordEntity(
    @PrimaryKey
    val date: Date,
    val totalAmount: Double,
    val employeeCount: Int,
    val amountPerEmployee: Double,
    val employeeNames: List<String>
) 