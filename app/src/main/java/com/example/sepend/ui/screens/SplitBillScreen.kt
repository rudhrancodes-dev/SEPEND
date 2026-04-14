package com.example.sepend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sepend.data.model.SplitBill
import com.example.sepend.data.model.Participant
import com.example.sepend.ui.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillScreen(
    viewModel: ExpenseViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Split Bills", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Split Bill")
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
            items(uiState.splitBills) { split ->
                SplitBillCard(split)
            }

            if (uiState.splitBills.isEmpty()) {
                item {
                    Text("No split bills. Tap + to create one.", modifier = Modifier.padding(40.dp))
                }
            }
        }
    }

    if (showDialog) {
        SplitDialog(
            onDismiss = { showDialog = false },
            onSave = { amount, participants ->
                viewModel.addSplitBill(
                    SplitBill(
                        amount = amount,
                        participants = participants
                    )
                )
                showDialog = false
            }
        )
    }
}

@Composable
fun SplitBillCard(split: SplitBill) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("₹${String.format("%.2f", split.amount)}", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineSmall)
            Text("${split.participants.size} participants", style = MaterialTheme.typography.labelSmall)

            split.participants.forEach { participant ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(participant.name)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("₹${String.format("%.2f", participant.share)}")
                        if (participant.paid) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Paid",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplitDialog(onDismiss: () -> Unit, onSave: (Double, List<Participant>) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var participants by remember { mutableStateOf(listOf<Participant>()) }
    var participantName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Split Bill") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amount = it },
                    label = { Text("Total Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = participantName,
                    onValueChange = { participantName = it },
                    label = { Text("Participant Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (participantName.isNotEmpty() && amount.isNotEmpty()) {
                            val share = (amount.toDoubleOrNull() ?: 0.0) / (participants.size + 1)
                            participants = participants + Participant(
                                name = participantName,
                                share = share
                            )
                            participantName = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Participant")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (amount.isNotEmpty() && participants.isNotEmpty()) {
                    onSave(amount.toDoubleOrNull() ?: 0.0, participants)
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
