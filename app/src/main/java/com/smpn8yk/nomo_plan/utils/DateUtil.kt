package com.smpn8yk.nomo_plan.utils

import com.smpn8yk.nomo_plan.data.CalendarUiState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

object DateUtil {

    val daysOfWeek: Array<String>
        get() {
            val daysOfWeek = Array(7) { "" }

            for (dayOfWeek in DayOfWeek.entries) {
                val localizedDayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                daysOfWeek[dayOfWeek.value - 1] = localizedDayName
            }

            return daysOfWeek
        }
}

fun getDates(yearMonth: YearMonth): List<CalendarUiState.Date> {
    return yearMonth.getDayOfMonthStartingFromMonday()
        .map { date ->
            CalendarUiState.Date(
                dayOfMonth = if (date.monthValue == yearMonth.monthValue) {
                    "${date.dayOfMonth}"
                } else {
                    "" // Fill with empty string for days outside the current month
                },
                isSelected = date.isEqual(LocalDate.now()) && date.monthValue == yearMonth.monthValue
            )
        }
}

fun YearMonth.getDayOfMonthStartingFromMonday(): List<LocalDate> {
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val firstMondayOfMonth = firstDayOfMonth.with(DayOfWeek.MONDAY)
    val firstDayOfNextMonth = firstDayOfMonth.plusMonths(1)

    return generateSequence(firstMondayOfMonth) { it.plusDays(1) }
        .takeWhile { it.isBefore(firstDayOfNextMonth) }
        .toList()
}

fun YearMonth.getDisplayName(): String {
    return "${month.getDisplayName(TextStyle.FULL, Locale.getDefault())} $year"
}
