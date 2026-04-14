package com.example.sepend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sepend.data.repository.ExpenseRepository
import com.example.sepend.service.FirebaseService
import com.example.sepend.ui.screens.AddExpenseScreen
import com.example.sepend.ui.screens.DashboardScreen
import com.example.sepend.ui.screens.HistoryScreen
import com.example.sepend.ui.screens.LoginScreen
import com.example.sepend.ui.screens.BudgetScreen
import com.example.sepend.ui.screens.AnalyticsScreen
import com.example.sepend.ui.screens.RecurringExpenseScreen
import com.example.sepend.ui.screens.SplitBillScreen
import com.example.sepend.ui.theme.SEPENDTheme
import com.example.sepend.ui.viewmodel.ExpenseViewModel
import com.example.sepend.ui.viewmodel.ExpenseViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SEPENDTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

                val firebaseService = remember { FirebaseService() }
                val repository = remember { ExpenseRepository(firebaseService) }
                val factory = remember { ExpenseViewModelFactory(repository) }
                val viewModel: ExpenseViewModel = viewModel(factory = factory)

                val isUserAuthenticated = remember { viewModel.isUserAuthenticated() }

                if (isUserAuthenticated) {
                    currentScreen = Screen.Dashboard
                }

                when (currentScreen) {
                    Screen.Login -> LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { currentScreen = Screen.Dashboard }
                    )
                    Screen.Dashboard -> DashboardScreen(
                        viewModel = viewModel,
                        onAddExpenseClick = { currentScreen = Screen.AddExpense },
                        onSeeAllClick = { currentScreen = Screen.History },
                        onBudgetClick = { currentScreen = Screen.Budget },
                        onAnalyticsClick = { currentScreen = Screen.Analytics },
                        onRecurringClick = { currentScreen = Screen.Recurring },
                        onSplitClick = { currentScreen = Screen.SplitBill },
                        onLogout = {
                            viewModel.signOut()
                            currentScreen = Screen.Login
                        }
                    )
                    Screen.AddExpense -> AddExpenseScreen(
                        viewModel = viewModel,
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                    Screen.History -> HistoryScreen(
                        viewModel = viewModel,
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                    Screen.Budget -> BudgetScreen(
                        viewModel = viewModel,
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                    Screen.Analytics -> AnalyticsScreen(
                        viewModel = viewModel,
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                    Screen.Recurring -> RecurringExpenseScreen(
                        viewModel = viewModel,
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                    Screen.SplitBill -> SplitBillScreen(
                        viewModel = viewModel,
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                }
            }
        }
    }
}

sealed class Screen {
    object Login : Screen()
    object Dashboard : Screen()
    object AddExpense : Screen()
    object History : Screen()
    object Budget : Screen()
    object Analytics : Screen()
    object Recurring : Screen()
    object SplitBill : Screen()
}
