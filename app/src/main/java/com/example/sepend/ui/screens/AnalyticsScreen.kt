package com.example.sepend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sepend.ui.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: ExpenseViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics & Insights", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Total Spending", style = MaterialTheme.typography.labelMedium)
                        Text("₹${String.format("%.2f", uiState.totalExpense)}", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineSmall)
                        Text("Average: ₹${String.format("%.2f", uiState.analytics.averageExpense)}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            item {
                Text("AI Insights", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))) {
                    Text(uiState.aiInsights, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall)
                }
            }

            item {
                Text("Top Categories", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            items(uiState.analytics.topCategories) { category ->
                CategoryAnalyticItem(category.category, category.amount, category.percentage)
            }

            item {
                Text("Monthly Trend", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            items(uiState.analytics.monthlyTrend.take(6)) { monthData ->
                MonthlyTrendItem(monthData.month, monthData.amount, monthData.count)
            }
        }
    }
}

@Composable
fun CategoryAnalyticItem(category: String, amount: Double, percentage: Float) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(category, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(
                    progress = percentage / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )
            }
            Text("₹${amount.toInt()} (${String.format("%.0f", percentage)}%)", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MonthlyTrendItem(month: String, amount: Double, count: Int) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(month, fontWeight = FontWeight.Bold)
            Column(horizontalAlignment = Alignment.End) {
                Text("₹${amount.toInt()}", fontWeight = FontWeight.Bold)
                Text("$count transactions", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
