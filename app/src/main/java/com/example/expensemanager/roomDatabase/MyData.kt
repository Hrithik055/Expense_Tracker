package com.example.expensemanager.roomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "myData")
data class MyData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val transactionType: String?,
    val transactionCategory: String?,
    val transactionAccount: String?,
    val transactionNote: String?,
    val transactionAmount: Double,
    val transactionDate: Long
)
