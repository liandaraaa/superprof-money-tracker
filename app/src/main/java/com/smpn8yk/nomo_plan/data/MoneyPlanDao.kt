package com.smpn8yk.nomo_plan.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface MoneyPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(moneyPlan: MoneyPlan)

    @Query("Select * From moneyPlan Where id = :id")
    suspend fun getMoneyPlanById(id: Int): MoneyPlan?

    @Query("DELETE FROM moneyPlan WHERE id = :id")
    suspend fun deleteMoneyPlanById(id: Int)

    @Query("SELECT * FROM moneyPlan")
    suspend fun getAlLMoneyPlans(): List<MoneyPlan>

    @Transaction
    @Query("SELECT * FROM moneyplan Where id = :id")
    fun getMoneyPlanWithExpenses(id: Int): MoneyPlanWithExpenses?

    @Transaction
    @Query("SELECT * FROM moneyplan")
    fun getALlMoneyPlanWithExpenses(): List<MoneyPlanWithExpenses>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("UPDATE moneyplan SET status = :newStatus WHERE id = :id")
    fun updateStatus(id: Int, newStatus: String)

    @Query("UPDATE expense SET report_status = :newStatus WHERE date = :date")
    fun updateReportStatus(date: String, newStatus: String)
}