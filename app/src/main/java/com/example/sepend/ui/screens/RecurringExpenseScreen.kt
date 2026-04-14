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
import com.example.sepend.data.model.RecurringExpense
import com.example.sepend.ui.viewmodel.ExpenseViewModel
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringExpenseScreen(
    viewModel: ExpenseViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recurring Expenses", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Recurring")
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
            items(uiState.recurringExpenses) { expense ->
                RecurringExpenseItem(expense)
            }

            if (uiState.recurringExpenses.isEmpty()) {
                item {
                    Text("No recurring expenses. Tap + to add one.", modifier = Modifier.padding(40.dp))
                }
            }
        }
    }

    if (showDialog) {
        RecurringDialog(
            onDismiss = { showDialog = false },
            onSave = { amount, category, frequency ->
                viewModel.addRecurringExpense(
                    RecurringExpense(
                        amount = amount,
                        category = category,
                        frequency = frequency,
                        paymentMethod = "Auto"
                    )
                )
                showDialog = false
            }
        )
    }
}

@Composable
fun RecurringExpenseItem(expense: RecurringExpense) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(expense.category, fontWeight = FontWeight.Bold)
                Switch(checked = expense.isActive, onCheckedChange = {})
            }
            Text("₹${String.format("%.2f", expense.amount)} • ${expense.frequency}", style = MaterialTheme.typography.labelSmall)
            Text(expense.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringDialog(onDismiss: () -> Unit, onSave: (Double, String, String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("MONTHLY") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Recurring Expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
                    OutlinedTextField(
                        value = frequency,
                        onValueChange = {},
                        label = { Text("Frequency") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (amount.isNotEmpty() && category.isNotEmpty()) {
                    onSave(amount.toDoubleOrNull() ?: 0.0, category, frequency)
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
