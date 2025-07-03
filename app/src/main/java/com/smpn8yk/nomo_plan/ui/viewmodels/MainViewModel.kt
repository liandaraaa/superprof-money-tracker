package com.smpn8yk.nomo_plan.ui.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.smpn8yk.nomo_plan.data.CalendarUiState
import com.smpn8yk.nomo_plan.data.local.MoneyPlanUiState
import com.smpn8yk.nomo_plan.domain.MoneyPlanRepository
import com.smpn8yk.nomo_plan.utils.getDates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MoneyPlanRepository,
    application: Application
) : BaseViewModel(application) {

    private val _moneyPlanWithExpanses = MutableStateFlow(MoneyPlanUiState(listOf()))
    val moneyPlanUiState: StateFlow<MoneyPlanUiState> = _moneyPlanWithExpanses

    private val _calendarUiState = MutableStateFlow(CalendarUiState.Init)
    val calendarUiState: StateFlow<CalendarUiState> = _calendarUiState

    init {
        viewModelScope.launch {
            repository.getALlMoneyPlanWithExpenses()
                .collect { data ->
                    _moneyPlanWithExpanses.tryEmit(MoneyPlanUiState(data))
                    _calendarUiState.tryEmit(
                        CalendarUiState(
                            yearMonth = YearMonth.now(),
                            dates = getDates(YearMonth.now(), data)
                        )
                    )
                }
        }
    }

    fun toSelectedMonth(currentMonth: YearMonth) {
        viewModelScope.launch {
            _calendarUiState.update {
                CalendarUiState(
                    yearMonth = currentMonth,
                    dates = getDates(currentMonth, moneyPlanUiState.value.plans)
                )
            }
        }
    }

    companion object {
        private const val MILLIS = 5_000L
    }


}