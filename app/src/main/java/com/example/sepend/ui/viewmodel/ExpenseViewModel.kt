package com.example.sepend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sepend.data.model.Expense
import com.example.sepend.data.model.Budget
import com.example.sepend.data.model.RecurringExpense
import com.example.sepend.data.model.SplitBill
import com.example.sepend.data.model.Analytics
import com.example.sepend.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExpenseUiState(
    val expenses: List<Expense> = emptyList(),
    val budgets: List<Budget> = emptyList(),
    val recurringExpenses: List<RecurringExpense> = emptyList(),
    val splitBills: List<SplitBill> = emptyList(),
    val analytics: Analytics = Analytics(),
    val aiInsights: String = "Loading insights...",
    val budgetAdvice: String = "",
    val totalExpense: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getExpensesStream().collect { expenses ->
                val analytics = repository.generateAnalytics(expenses)
                val total = repository.getTotalExpense(expenses)

                _uiState.value = _uiState.value.copy(
                    expenses = expenses,
                    totalExpense = total,
                    analytics = analytics
                )

                loadAiInsights(expenses)
            }
        }
        loadBudgets()
        loadRecurringExpenses()
        loadSplitBills()
    }

    private fun loadAiInsights(expenses: List<Expense>) {
        viewModelScope.launch {
            val insights = repository.getExpenseInsights(expenses)
            _uiState.value = _uiState.value.copy(aiInsights = insights)
        }
    }

    private fun loadBudgets() {
        viewModelScope.launch {
            val budgets = repository.getBudgets()
            _uiState.value = _uiState.value.copy(budgets = budgets)
            loadBudgetAdvice(budgets)
        }
    }

    private fun loadBudgetAdvice(budgets: List<Budget>) {
        viewModelScope.launch {
            val advice = repository.getBudgetAdvice(budgets, _uiState.value.expenses)
            _uiState.value = _uiState.value.copy(budgetAdvice = advice)
        }
    }

    private fun loadRecurringExpenses() {
        viewModelScope.launch {
            val recurring = repository.getRecurringExpenses()
            _uiState.value = _uiState.value.copy(recurringExpenses = recurring)
        }
    }

    private fun loadSplitBills() {
        viewModelScope.launch {
            val splits = repository.getSplitBills()
            _uiState.value = _uiState.value.copy(splitBills = splits)
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.addExpense(expense)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to add expense"
                )
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            val result = repository.deleteExpense(expenseId)
            result.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Failed to delete expense"
                )
            }
        }
    }

    fun addBudget(budget: Budget) {
        viewModelScope.launch {
            val result = repository.addBudget(budget)
            result.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Failed to add budget"
                )
            }
        }
    }

    fun addRecurringExpense(expense: RecurringExpense) {
        viewModelScope.launch {
            val result = repository.addRecurringExpense(expense)
            result.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Failed to add recurring expense"
                )
            }
        }
    }

    fun addSplitBill(splitBill: SplitBill) {
        viewModelScope.launch {
            val result = repository.addSplitBill(splitBill)
            result.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Failed to add split bill"
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _authState.value = AuthState.SignedOut
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signInWithEmail(email, password)
            result.onSuccess {
                _authState.value = AuthState.SignedIn
                loadData()
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Sign in failed")
            }
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUpWithEmail(email, password)
            result.onSuccess {
                _authState.value = AuthState.SignedIn
                loadData()
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Sign up failed")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun isUserAuthenticated(): Boolean = repository.isUserAuthenticated()
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object SignedIn : AuthState()
    object SignedOut : AuthState()
    data class Error(val message: String) : AuthState()
}

class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
