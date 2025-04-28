package com.example.expensemanager.model

import java.util.Date

data class Transaction(
    val transactionType: String,
    val transactionCategory: String,
    val transactionAccount: String,
    val transactionNote: String,

    val transactionId: Int,
    val transactionAmount: Double,
    val transactionDate: Date,
)