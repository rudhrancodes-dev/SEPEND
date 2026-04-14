package com.example.sepend.data.model

import com.google.firebase.Timestamp

data class RecurringExpense(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val paymentMethod: String = "",
    val frequency: String = "MONTHLY",
    val description: String = "",
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val isActive: Boolean = true,
    val lastExecuted: Timestamp? = null
) {
    companion object {
        const val COLLECTION_NAME = "recurringExpenses"
        const val FREQUENCY_DAILY = "DAILY"
        const val FREQUENCY_WEEKLY = "WEEKLY"
        const val FREQUENCY_MONTHLY = "MONTHLY"
        const val FREQUENCY_YEARLY = "YEARLY"
    }
}
