package com.smpn8yk.nomo_plan.data

import com.smpn8yk.nomo_plan.utils.getDates
import java.time.YearMonth

data class CalendarUiState(
    var yearMonth: YearMonth,
    var dates: List<Date>
) {
    companion object {
        val Init = CalendarUiState(
            yearMonth = YearMonth.now(),
            dates = getDates(YearMonth.now(), listOf())
        )
    }
    data class Date(
        val dayOfMonth: String,
        val isSelected: Boolean,
        val dateFormat: String,
        val status: String
    ) {
        companion object {
            val Empty = Date("", false, "", "")
        }
    }
}