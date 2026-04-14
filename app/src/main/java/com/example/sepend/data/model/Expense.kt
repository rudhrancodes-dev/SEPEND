package com.example.sepend.data.model

import com.google.firebase.Timestamp

data class Expense(
    val id: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val paymentMethod: String = "",
    val description: String = "",
    val date: Timestamp? = null,
    val userId: String = ""
) {
    companion object {
        const val COLLECTION_NAME = "expenses"
        const val FIELD_AMOUNT = "amount"
        const val FIELD_CATEGORY = "category"
        const val FIELD_PAYMENT_METHOD = "paymentMethod"
        const val FIELD_DATE = "date"
        const val FIELD_USER_ID = "userId"
    }
}
