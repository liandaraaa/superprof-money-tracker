package com.smpn8yk.nomo_plan.ui.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.smpn8yk.nomo_plan.data.local.entity.Expense
import com.smpn8yk.nomo_plan.data.local.entity.ExpenseReportStatus
import com.smpn8yk.nomo_plan.data.local.entity.MoneyPlan
import com.smpn8yk.nomo_plan.data.local.entity.MoneyPlanStatus
import com.smpn8yk.nomo_plan.data.local.entity.MoneyPlanWithExpenses
import com.smpn8yk.nomo_plan.domain.MoneyPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyTrackerExpenseViewModel @Inject constructor(
    private val repository: MoneyPlanRepository,
    application: Application
) : BaseViewModel(application) {

    private val _currentMoneyPlanId = MutableStateFlow(0)
    val currentMoneyPlanId: StateFlow<Int> = _currentMoneyPlanId

    private val _saveExpenseUiState = MutableStateFlow("IDLE")
    val saveExpenseUiState: StateFlow<String> = _saveExpenseUiState

    private val _updateStatusExpenseUiState = MutableStateFlow("IDLE")
    val updateStatusExpenseUiState: StateFlow<String> = _updateStatusExpenseUiState

    private val _currentMoneyPlanExpenses = MutableStateFlow(MoneyPlanWithExpenses())
    val currentMoneyPlanExpenses: StateFlow<MoneyPlanWithExpenses> = _currentMoneyPlanExpenses

    init {
        viewModelScope.launch {
            repository.getMoneyPlanWithExpenses(currentMoneyPlanId.value)
                ?.collect { data ->
                    _currentMoneyPlanExpenses.tryEmit(data)
                }
        }
    }

    fun setCurrentPlanId(id: Int) {
        _currentMoneyPlanId.value = id
    }

    fun saveExpense(expense: Expense) {
        _saveExpenseUiState.value = "LOADING"
        viewModelScope.launch {
            repository.insertExpense(expense)
            _saveExpenseUiState.value = "COMPLETED"
        }
    }

    fun updatePlanStatus(selectedDate: String, reportStatus: ExpenseReportStatus, planId:Int, status: MoneyPlanStatus){
        _updateStatusExpenseUiState.value = "LOADING"
        viewModelScope.launch {
            repository.updateReportStatus(selectedDate,reportStatus.name)
            repository.updateStatus(planId,status.name)
            _updateStatusExpenseUiState.value = "COMPLETED"
        }
    }
}