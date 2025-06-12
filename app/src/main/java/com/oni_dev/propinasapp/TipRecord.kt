package com.oni_dev.propinasapp

import java.util.Date

data class TipRecord(
    val totalAmount: Double,
    val employeeCount: Int,
    val amountPerEmployee: Double,
    val employeeNames: List<String> = emptyList(),
    val absentEmployees: List<String> = emptyList(),
    val date: Date = Date()
) 