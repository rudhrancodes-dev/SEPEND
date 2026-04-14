package com.example.sepend.data.repository

import com.example.sepend.data.model.Expense
import com.example.sepend.data.model.Budget
import com.example.sepend.data.model.RecurringExpense
import com.example.sepend.data.model.SplitBill
import com.example.sepend.data.model.Analytics
import com.example.sepend.data.model.MonthlyData
import com.example.sepend.data.model.CategoryData
import com.example.sepend.service.FirebaseService
import com.example.sepend.service.AiService
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpenseRepository(
    private val firebaseService: FirebaseService,
    private val aiService: AiService = AiService()
) {

    fun getExpensesStream(): Flow<List<Expense>> = firebaseService.getExpensesStream()
    suspend fun addExpense(expense: Expense): Result<String> = firebaseService.addExpense(expense)
    suspend fun updateExpense(expense: Expense): Result<Unit> = firebaseService.updateExpense(expense)
    suspend fun deleteExpense(expenseId: String): Result<Unit> = firebaseService.deleteExpense(expenseId)
    suspend fun getExpenses(): List<Expense> = firebaseService.getExpenses()

    // Budget operations
    suspend fun addBudget(budget: Budget): Result<String> = firebaseService.addBudget(budget)
    suspend fun getBudgets(): List<Budget> = firebaseService.getBudgets()
    suspend fun updateBudget(budget: Budget): Result<Unit> = firebaseService.updateBudget(budget)
    suspend fun deleteBudget(budgetId: String): Result<Unit> = firebaseService.deleteBudget(budgetId)

    // Recurring expenses
    suspend fun addRecurringExpense(expense: RecurringExpense): Result<String> = firebaseService.addRecurringExpense(expense)
    suspend fun getRecurringExpenses(): List<RecurringExpense> = firebaseService.getRecurringExpenses()
    suspend fun updateRecurringExpense(expense: RecurringExpense): Result<Unit> = firebaseService.updateRecurringExpense(expense)

    // Split bills
    suspend fun addSplitBill(splitBill: SplitBill): Result<String> = firebaseService.addSplitBill(splitBill)
    suspend fun getSplitBills(): List<SplitBill> = firebaseService.getSplitBills()

    // Analytics
    fun generateAnalytics(expenses: List<Expense>): Analytics {
        if (expenses.isEmpty()) return Analytics()

        val totalExpenses = expenses.sumOf { it.amount }
        val avgExpense = totalExpenses / expenses.size

        val categoryBreakdown = expenses.groupBy { it.category }
            .mapValues { it.value.sumOf { e -> e.amount } }

        val paymentMethodBreakdown = expenses.groupBy { it.paymentMethod }
            .mapValues { it.value.sumOf { e -> e.amount } }

        val topCategories = categoryBreakdown
            .map { CategoryData(it.key, it.value, (it.value / totalExpenses * 100).toFloat()) }
            .sortedByDescending { it.amount }
            .take(5)

        val topExpenses = expenses.sortedByDescending { it.amount }.take(5)

        val monthlyTrend = generateMonthlyTrend(expenses)

        return Analytics(
            totalExpenses = totalExpenses,
            averageExpense = avgExpense,
            categoryBreakdown = categoryBreakdown,
            paymentMethodBreakdown = paymentMethodBreakdown,
            topCategories = topCategories,
            topExpenses = topExpenses,
            monthlyTrend = monthlyTrend
        )
    }

    private fun generateMonthlyTrend(expenses: List<Expense>): List<MonthlyData> {
        val monthFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val grouped = expenses.groupBy { expense ->
            val calendar = Calendar.getInstance()
            expense.date?.let { timestamp -> calendar.time = timestamp.toDate() }
            monthFormat.format(calendar.time)
        }

        return grouped.map { (month, monthExpenses) ->
            MonthlyData(month, monthExpenses.sumOf { it.amount }, monthExpenses.size)
        }.sortedByDescending { it.month }
    }

    suspend fun getExpenseInsights(expenses: List<Expense>): String = aiService.getExpenseInsights(expenses)

    suspend fun getBudgetAdvice(budgets: List<Budget>, expenses: List<Expense>): String {
        val categoryBudgets = budgets.associate { it.category to it.limit }
        val categorySpending = expenses.groupBy { it.category }
            .mapValues { it.value.sumOf { e -> e.amount } }
        return aiService.getBudgetAdvice(categoryBudgets, categorySpending)
    }

    suspend fun getCategoryAdvice(category: String, amount: Double, expenses: List<Expense>): String =
        aiService.getCategoryRecommendation(category, amount, expenses)

    fun getTotalExpense(expenses: List<Expense>): Double = expenses.sumOf { it.amount }
    fun getCategoryTotal(expenses: List<Expense>, category: String): Double =
        expenses.filter { it.category.equals(category, ignoreCase = true) }.sumOf { it.amount }
    fun getPaymentMethodTotal(expenses: List<Expense>, method: String): Double =
        expenses.filter { it.paymentMethod.equals(method, ignoreCase = true) }.sumOf { it.amount }

    suspend fun signOut() = firebaseService.signOut()
    suspend fun signInWithEmail(email: String, password: String): Result<Unit> = firebaseService.signInWithEmail(email, password)
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> = firebaseService.signUpWithEmail(email, password)
    fun isUserAuthenticated(): Boolean = firebaseService.currentUser != null
    fun getCurrentUserId(): String = firebaseService.currentUserId
}
