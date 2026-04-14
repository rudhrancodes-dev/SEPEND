package com.example.sepend.data.model

data class Budget(
    val id: String = "",
    val userId: String = "",
    val category: String = "",
    val limit: Double = 0.0,
    val spent: Double = 0.0,
    val period: String = "MONTHLY",
    val createdAt: com.google.firebase.Timestamp? = null
) {
    fun percentageUsed(): Float = if (limit > 0) ((spent / limit) * 100).toFloat() else 0f
    fun isExceeded(): Boolean = spent > limit
    fun remaining(): Double = if (limit > spent) limit - spent else 0.0

    companion object {
        const val COLLECTION_NAME = "budgets"
    }
}
