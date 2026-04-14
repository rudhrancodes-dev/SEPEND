package com.example.sepend.data.model

import com.google.firebase.Timestamp

data class SplitBill(
    val id: String = "",
    val expenseId: String = "",
    val amount: Double = 0.0,
    val participants: List<Participant> = emptyList(),
    val createdBy: String = "",
    val createdAt: Timestamp? = null
)

data class Participant(
    val userId: String = "",
    val name: String = "",
    val share: Double = 0.0,
    val paid: Boolean = false,
    val paidAt: Timestamp? = null
)
