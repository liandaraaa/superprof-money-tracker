package com.smpn8yk.nomo_plan.ui.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.smpn8yk.nomo_plan.data.local.entity.MoneyPlan
import com.smpn8yk.nomo_plan.domain.MoneyPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageMoneyViewModel @Inject constructor(
    private val repository: MoneyPlanRepository,
    application: Application
) : BaseViewModel(application) {

    private val _manageMoneyUiState = MutableStateFlow("IDLE")
    val manageMoneyUiState:StateFlow<String> = _manageMoneyUiState

    fun savePlan(moneyPlan: MoneyPlan) {
        _manageMoneyUiState.value = "LOADING"
        viewModelScope.launch {
           repository.insertMoneyPlan(moneyPlan)
            _manageMoneyUiState.value = "COMPLETED"
        }
    }
}