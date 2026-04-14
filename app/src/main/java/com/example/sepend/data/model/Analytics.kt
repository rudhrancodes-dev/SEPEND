package com.example.sepend.data.model

data class Analytics(
    val totalExpenses: Double = 0.0,
    val averageExpense: Double = 0.0,
    val categoryBreakdown: Map<String, Double> = emptyMap(),
    val paymentMethodBreakdown: Map<String, Double> = emptyMap(),
    val dailyExpenses: Map<String, Double> = emptyMap(),
    val monthlyTrend: List<MonthlyData> = emptyList(),
    val topCategories: List<CategoryData> = emptyList(),
    val topExpenses: List<Expense> = emptyList()
)

data class MonthlyData(
    val month: String = "",
    val amount: Double = 0.0,
    val count: Int = 0
)

data class CategoryData(
    val category: String = "",
    val amount: Double = 0.0,
    val percentage: Float = 0f,
    val count: Int = 0
)
