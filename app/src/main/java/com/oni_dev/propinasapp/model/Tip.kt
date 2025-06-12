package com.example.propinasapp.model

data class Tip(
    val id: Long = 0,
    val amount: Double,
    val date: Long = System.currentTimeMillis()
) 