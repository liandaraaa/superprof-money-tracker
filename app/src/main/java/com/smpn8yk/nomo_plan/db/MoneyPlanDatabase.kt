package com.smpn8yk.nomo_plan.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smpn8yk.nomo_plan.data.Expense
import com.smpn8yk.nomo_plan.data.MoneyPlan
import com.smpn8yk.nomo_plan.data.MoneyPlanDao
import com.smpn8yk.nomo_plan.data.MoneyPlanTypeConverter

@TypeConverters(MoneyPlanTypeConverter::class)
@Database(version = 1, entities = [MoneyPlan::class,Expense::class], exportSchema = false)
abstract class MoneyPlanDatabase : RoomDatabase() {
    abstract fun moneyPlanDao(): MoneyPlanDao
}