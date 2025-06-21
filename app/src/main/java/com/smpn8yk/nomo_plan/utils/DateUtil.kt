package com.smpn8yk.nomo_plan.utils

import android.util.Log
import com.smpn8yk.nomo_plan.data.CalendarUiState
import com.smpn8yk.nomo_plan.data.ExpenseReportStatus
import com.smpn8yk.nomo_plan.data.MoneyPlanWithExpenses
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
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

fun getDates(yearMonth: YearMonth,moneyPLanWithExpenses:List<MoneyPlanWithExpenses>): List<CalendarUiState.Date> {
    return yearMonth.getDayOfMonthStartingFromMonday()
        .map { date ->
            val dateFormat =
                if (date.monthValue == yearMonth.monthValue) {
                    yearMonth.getDateFormat(date.dayOfMonth)
                } else {
                    "" // Fill with empty string for days outside the current month
                }
            val currentMoneyPlan = moneyPLanWithExpenses.map { it.plan}.find { it.range_dates.contains(dateFormat) }
            val reportStatus = moneyPLanWithExpenses.find { it.plan == currentMoneyPlan }?.expenses?.find { it.date.equals(dateFormat) }?.report_status
            Log.d("TEST_PROGRAM","get date status : $moneyPLanWithExpenses and $reportStatus")
            CalendarUiState.Date(
                dayOfMonth = if (date.monthValue == yearMonth.monthValue) {
                    "${date.dayOfMonth}"
                } else {
                    "" // Fill with empty string for days outside the current month
                },

                isSelected = date.isEqual(LocalDate.now()) && date.monthValue == yearMonth.monthValue,
                dateFormat = dateFormat,
                status = if(currentMoneyPlan?.range_dates?.contains(dateFormat) == true && reportStatus == null){
                    ExpenseReportStatus.EMPTY.name
                }else if(currentMoneyPlan?.range_dates?.contains(dateFormat) == true && reportStatus != null){
                    reportStatus
                }else{
                    ExpenseReportStatus.NONE.name
                }
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

fun YearMonth.getDateFormat(day:Int): String {
    val date = atDay(day).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    Log.d("TEST_PROGRAM","get date fomat : $date")
    return date
}
