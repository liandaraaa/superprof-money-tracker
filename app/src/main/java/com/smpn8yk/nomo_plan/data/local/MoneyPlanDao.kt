package com.smpn8yk.nomo_plan.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.smpn8yk.nomo_plan.data.local.entity.Expense
import com.smpn8yk.nomo_plan.data.local.entity.MoneyPlan
import com.smpn8yk.nomo_plan.data.local.entity.MoneyPlanWithExpenses
import kotlinx.coroutines.flow.Flow

@Dao
interface MoneyPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(moneyPlan: MoneyPlan)

    @Transaction
    @Query("SELECT * FROM moneyplan Where id = :id")
    fun getMoneyPlanWithExpenses(id: Int): Flow<MoneyPlanWithExpenses>?

    @Transaction
    @Query("SELECT * FROM moneyplan")
    fun getALlMoneyPlanWithExpenses(): Flow<List<MoneyPlanWithExpenses>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("UPDATE moneyplan SET status = :newStatus WHERE id = :id")
    fun updateStatus(id: Int, newStatus: String)

    @Query("UPDATE expense SET report_status = :newStatus WHERE date = :date")
    fun updateReportStatus(date: String, newStatus: String)
}