package com.example.sepend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sepend.data.model.Budget
import com.example.sepend.ui.viewmodel.ExpenseViewModel
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: ExpenseViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Budget")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Budget Advice", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(uiState.budgetAdvice, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall)
                }
            }

            items(uiState.budgets) { budget ->
                BudgetCard(budget)
            }

            if (uiState.budgets.isEmpty()) {
                item {
                    Text("No budgets set. Tap + to create one.", modifier = Modifier.padding(40.dp))
                }
            }
        }
    }

    if (showDialog) {
        BudgetDialog(
            onDismiss = { showDialog = false },
            onSave = { category, limit ->
                viewModel.addBudget(Budget(category = category, limit = limit))
                showDialog = false
            }
        )
    }
}

@Composable
fun BudgetCard(budget: Budget) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(budget.category, fontWeight = FontWeight.Bold)
                Text("₹${budget.spent.toInt()}/${budget.limit.toInt()}", fontWeight = FontWeight.Bold)
            }

            LinearProgressIndicator(
                progress = budget.percentageUsed() / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = if (budget.isExceeded()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            Text(
                "${String.format("%.0f", budget.percentageUsed())}% used • ₹${String.format("%.2f", budget.remaining())} remaining",
                style = MaterialTheme.typography.labelSmall,
                color = if (budget.isExceeded()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BudgetDialog(onDismiss: () -> Unit, onSave: (String, Double) -> Unit) {
    var category by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Budget") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = limit,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) limit = it },
                    label = { Text("Budget Limit") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (category.isNotEmpty() && limit.isNotEmpty()) {
                    onSave(category, limit.toDoubleOrNull() ?: 0.0)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
