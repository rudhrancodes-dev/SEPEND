package com.example.sepend.service

import com.example.sepend.data.model.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

class AiService {

    suspend fun getExpenseInsights(expenses: List<Expense>): String = withContext(Dispatchers.Default) {
        if (expenses.isEmpty()) return@withContext "No expenses to analyze yet."

        val totalSpent = expenses.sumOf { it.amount }
        val avgExpense = totalSpent / expenses.size
        val topCategory = expenses.groupBy { it.category }
            .maxByOrNull { it.value.sumOf { e -> e.amount } }?.key ?: "Unknown"

        return@withContext buildString {
            append("📊 Spending Analysis:\n\n")
            append("💰 Your top spending category is $topCategory. Consider setting a limit to reduce expenses in this area.\n\n")

            if (avgExpense > 1000) {
                append("⚠️ Average expense (₹${String.format("%.0f", avgExpense)}) is high. Try to reduce it by 10-15% by cutting non-essential items.\n\n")
            } else {
                append("✅ Your average expense (₹${String.format("%.0f", avgExpense)}) is reasonable. Continue tracking carefully.")
            }

            append("💡 Tip: Review your spending weekly to identify patterns and opportunities to save.")
        }
    }

    suspend fun getBudgetAdvice(categoryBudgets: Map<String, Double>, spending: Map<String, Double>): String =
        withContext(Dispatchers.Default) {
            val recommendations = mutableListOf<String>()

            spending.forEach { (category, spent) ->
                val budget = categoryBudgets[category] ?: 0.0
                if (budget > 0) {
                    val percentUsed = (spent / budget) * 100
                    when {
                        percentUsed > 90 -> recommendations.add("🔴 $category is at ${percentUsed.toInt()}% of budget. Reduce spending immediately.")
                        percentUsed > 75 -> recommendations.add("🟡 $category is at ${percentUsed.toInt()}% of budget. Be careful with further spending.")
                        else -> recommendations.add("✅ $category spending is under control at ${percentUsed.toInt()}% of budget.")
                    }
                }
            }

            return@withContext if (recommendations.isNotEmpty()) {
                recommendations.take(3).joinToString("\n\n")
            } else {
                "No budget data available yet. Set budgets to get personalized advice."
            }
        }

    suspend fun getCategoryRecommendation(category: String, amount: Double, history: List<Expense>): String =
        withContext(Dispatchers.Default) {
            val categoryExpenses = history.filter { it.category.equals(category, ignoreCase = true) }
            val avgForCategory = if (categoryExpenses.isNotEmpty()) {
                categoryExpenses.map { it.amount }.average()
            } else {
                0.0
            }

            return@withContext buildString {
                append("💡 $category Insight:\n\n")
                if (avgForCategory > 0) {
                    val difference = amount - avgForCategory
                    if (difference > 100) {
                        append("⚠️ This expense (₹${String.format("%.0f", amount)}) is ₹${String.format("%.0f", abs(difference))} higher than your average (₹${String.format("%.0f", avgForCategory)}).\n\n")
                    } else if (difference < -100) {
                        append("✅ This expense (₹${String.format("%.0f", amount)}) is ₹${String.format("%.0f", abs(difference))} lower than your average. Good control!\n\n")
                    } else {
                        append("Your expense is close to your usual spending for this category.\n\n")
                    }
                }
                append("📌 Tip: Try to keep $category expenses consistent. Track similar items together for better analysis.")
            }
        }
}
