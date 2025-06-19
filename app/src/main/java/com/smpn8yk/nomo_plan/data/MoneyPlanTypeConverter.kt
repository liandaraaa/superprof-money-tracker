package com.smpn8yk.nomo_plan.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MoneyPlanTypeConverter {
    private val gson : Gson by lazy {
        Gson()
    }

    @TypeConverter
    fun fromMoneyPlanList(value: List<MoneyPlan>): String = gson.toJson(value)

    @TypeConverter
    fun toMoneyPlanList(value: String): List<MoneyPlan> =
        gson.fromJson(value, object : TypeToken<List<MoneyPlan>>() {}.type)
}