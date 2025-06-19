package com.smpn8yk.nomo_plan.data

import java.time.YearMonth

data class MoneyPlan(
    val totalDays:Int,
    val nominal: Int,
    val budget: Int,
    val rangeDates: List<YearMonth>
)
