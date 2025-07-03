package com.smpn8yk.nomo_plan.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smpn8yk.nomo_plan.data.local.entity.Expense
import com.smpn8yk.nomo_plan.data.local.entity.MoneyPlan
import com.smpn8yk.nomo_plan.data.local.MoneyPlanDao
import com.smpn8yk.nomo_plan.data.local.MoneyPlanTypeConverter

@TypeConverters(MoneyPlanTypeConverter::class)
@Database(version = 1, entities = [MoneyPlan::class, Expense::class], exportSchema = false)
abstract class MoneyPlanDatabase : RoomDatabase() {
    abstract fun moneyPlanDao(): MoneyPlanDao
}