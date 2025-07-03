package com.smpn8yk.nomo_plan.data

import kotlinx.coroutines.flow.Flow

interface MoneyPlanRepository {
    suspend fun insertMoneyPlan(moneyPlan: MoneyPlan)

    fun getMoneyPlanWithExpenses(id: Int): Flow<MoneyPlanWithExpenses>?

    fun getALlMoneyPlanWithExpenses(): Flow<List<MoneyPlanWithExpenses>>

    suspend fun insertExpense(expense: Expense)

    fun updateStatus(id: Int, newStatus: String)

    fun updateReportStatus(date: String, newStatus: String)
}