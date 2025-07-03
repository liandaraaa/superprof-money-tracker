package com.smpn8yk.nomo_plan.data

import androidx.room.Embedded
import androidx.room.Relation

data class MoneyPlanWithExpenses(
    @Embedded val plan: MoneyPlan = MoneyPlan(),
    @Relation(
        parentColumn = "id",
        entityColumn = "plan_id"
    )
    val expenses: List<Expense> = listOf()
)