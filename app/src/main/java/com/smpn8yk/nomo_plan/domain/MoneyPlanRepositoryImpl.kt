package com.smpn8yk.nomo_plan.domain

import com.smpn8yk.nomo_plan.data.local.MoneyPlanDao
import com.smpn8yk.nomo_plan.data.local.entity.Expense
import com.smpn8yk.nomo_plan.data.local.entity.MoneyPlan
import com.smpn8yk.nomo_plan.data.local.entity.MoneyPlanWithExpenses
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MoneyPlanRepositoryImpl @Inject constructor(
    private val moneyPlanDao: MoneyPlanDao
) : MoneyPlanRepository {
    override suspend fun insertMoneyPlan(moneyPlan: MoneyPlan) {
        return moneyPlanDao.insert(moneyPlan)
    }

    override fun getMoneyPlanWithExpenses(id: Int): MoneyPlanWithExpenses? {
       return moneyPlanDao.getMoneyPlanWithExpenses(id)
    }

    override fun getALlMoneyPlanWithExpenses(): Flow<List<MoneyPlanWithExpenses>> {
       return moneyPlanDao.getALlMoneyPlanWithExpenses()
    }

    override suspend fun insertExpense(expense: Expense) {
        return moneyPlanDao.insertExpense(expense)
    }

    override fun updateStatus(id: Int, newStatus: String) {
        return moneyPlanDao.updateStatus(id,newStatus)
    }

    override fun updateReportStatus(date: String, newStatus: String) {
       return moneyPlanDao.updateReportStatus(date,newStatus)
    }
}