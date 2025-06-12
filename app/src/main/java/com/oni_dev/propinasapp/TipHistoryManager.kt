package com.oni_dev.propinasapp

import android.content.Context
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class TipHistoryManager private constructor(private val context: Context) {
    private val database: TipDatabase
    private val tipRecordDao: TipRecordDao
    private var initialBudget: Double = 10000.0 // Presupuesto inicial de 10,000
    private var currentBudget: Double = initialBudget
    private val tipPercentage: Double = 0.05 // 5% de propina

    init {
        database = Room.databaseBuilder(
            context.applicationContext,
            TipDatabase::class.java,
            "tip_database"
        ).build()
        tipRecordDao = database.tipRecordDao()
    }

    suspend fun getCurrentBudget(): Double = withContext(Dispatchers.IO) {
        try {
            currentBudget
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener el presupuesto actual", e)
            initialBudget
        }
    }

    suspend fun addRecord(record: TipRecord): Boolean = withContext(Dispatchers.IO) {
        try {
            if (record.totalAmount <= currentBudget) {
                val entity = TipRecordEntity(
                    date = record.date,
                    totalAmount = record.totalAmount,
                    employeeCount = record.employeeCount,
                    amountPerEmployee = record.amountPerEmployee,
                    employeeNames = record.employeeNames
                )
                tipRecordDao.insert(entity)
                currentBudget -= record.totalAmount
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al agregar registro", e)
            false
        }
    }

    suspend fun getHistory(): List<TipRecord> = withContext(Dispatchers.IO) {
        try {
            tipRecordDao.getAllRecords().map { entity ->
                TipRecord(
                    date = entity.date,
                    totalAmount = entity.totalAmount,
                    employeeCount = entity.employeeCount,
                    amountPerEmployee = entity.amountPerEmployee,
                    employeeNames = entity.employeeNames
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener historial", e)
            emptyList()
        }
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        try {
            tipRecordDao.deleteAll()
            currentBudget = initialBudget
        } catch (e: Exception) {
            Log.e(TAG, "Error al limpiar historial", e)
        }
    }

    suspend fun removeRecord(record: TipRecord) = withContext(Dispatchers.IO) {
        try {
            tipRecordDao.deleteByDate(record.date)
            currentBudget += record.totalAmount
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar registro", e)
        }
    }

    suspend fun calculateTipAmount(baseAmount: Double): Double {
        return baseAmount * tipPercentage
    }

    suspend fun getTotalTips(): Double = withContext(Dispatchers.IO) {
        try {
            tipRecordDao.getAllRecords().sumOf { it.totalAmount }
        } catch (e: Exception) {
            Log.e(TAG, "Error al calcular total de propinas", e)
            0.0
        }
    }

    suspend fun getRemainingBudget(): Double = withContext(Dispatchers.IO) {
        try {
            currentBudget
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener presupuesto restante", e)
            0.0
        }
    }

    companion object {
        private const val TAG = "TipHistoryManager"
        @Volatile
        private var INSTANCE: TipHistoryManager? = null

        fun getInstance(context: Context): TipHistoryManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TipHistoryManager(context).also { INSTANCE = it }
            }
        }
    }
}
