package com.example.sepend.service

import com.example.sepend.data.model.Expense
import com.example.sepend.data.model.Budget
import com.example.sepend.data.model.RecurringExpense
import com.example.sepend.data.model.SplitBill
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class FirebaseService {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser get() = auth.currentUser
    val currentUserId get() = auth.currentUser?.uid ?: ""

    fun getExpensesStream(): Flow<List<Expense>> = callbackFlow {
        val userId = currentUserId
        if (userId.isEmpty()) {
            close()
            return@callbackFlow
        }

        val subscription = db.collection("users").document(userId)
            .collection(Expense.COLLECTION_NAME)
            .orderBy(Expense.FIELD_DATE, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Expense::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(expenses).isSuccess
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addExpense(expense: Expense): Result<String> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return Result.failure(Exception("User not authenticated"))
            val expenseWithUser = expense.copy(userId = userId, date = expense.date ?: com.google.firebase.Timestamp.now())
            val docRef = db.collection("users").document(userId)
                .collection(Expense.COLLECTION_NAME).add(expenseWithUser).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateExpense(expense: Expense): Result<Unit> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return Result.failure(Exception("User not authenticated"))
            db.collection("users").document(userId)
                .collection(Expense.COLLECTION_NAME)
                .document(expense.id).set(expense.copy(userId = userId)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return Result.failure(Exception("User not authenticated"))
            db.collection("users").document(userId)
                .collection(Expense.COLLECTION_NAME).document(expenseId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpenses(): List<Expense> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return emptyList()
            val snapshot = db.collection("users").document(userId)
                .collection(Expense.COLLECTION_NAME)
                .orderBy(Expense.FIELD_DATE, Query.Direction.DESCENDING).get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Expense::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addBudget(budget: Budget): Result<String> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return Result.failure(Exception("User not authenticated"))
            val budgetWithUser = budget.copy(userId = userId, createdAt = budget.createdAt ?: com.google.firebase.Timestamp.now())
            val docRef = db.collection("users").document(userId)
                .collection(Budget.COLLECTION_NAME).add(budgetWithUser).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBudgets(): List<Budget> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return emptyList()
            val snapshot = db.collection("users").document(userId)
                .collection(Budget.COLLECTION_NAME).get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Budget::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateBudget(budget: Budget): Result<Unit> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return Result.failure(Exception("User not authenticated"))
            db.collection("users").document(userId)
                .collection(Budget.COLLECTION_NAME)
                .document(budget.id).set(budget.copy(userId = userId)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBudget(budgetId: String): Result<Unit> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return Result.failure(Exception("User not authenticated"))
            db.collection("users").document(userId)
                .collection(Budget.COLLECTION_NAME).document(budgetId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addRecurringExpense(expense: RecurringExpense): Result<String> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return Result.failure(Exception("User not authenticated"))
            val expenseWithUser = expense.copy(userId = userId)
            val docRef = db.collection("users").document(userId)
                .collection(RecurringExpense.COLLECTION_NAME).add(expenseWithUser).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecurringExpenses(): List<RecurringExpense> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return emptyList()
            val snapshot = db.collection("users").document(userId)
                .collection(RecurringExpense.COLLECTION_NAME).get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(RecurringExpense::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateRecurringExpense(expense: RecurringExpense): Result<Unit> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return Result.failure(Exception("User not authenticated"))
            db.collection("users").document(userId)
                .collection(RecurringExpense.COLLECTION_NAME)
                .document(expense.id).set(expense.copy(userId = userId)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addSplitBill(splitBill: SplitBill): Result<String> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return Result.failure(Exception("User not authenticated"))
            val docRef = db.collection("users").document(userId)
                .collection("splitBills").add(splitBill.copy(createdAt = splitBill.createdAt ?: com.google.firebase.Timestamp.now())).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSplitBills(): List<SplitBill> {
        return try {
            val userId = currentUserId
            if (userId.isEmpty()) return emptyList()
            val snapshot = db.collection("users").document(userId)
                .collection("splitBills").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(SplitBill::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
