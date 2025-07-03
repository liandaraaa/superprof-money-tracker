package com.smpn8yk.nomo_plan.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MoneyPlanTypeConverter {
    private val gson : Gson by lazy {
        Gson()
    }

    @TypeConverter
    fun fromRangeDateList(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun toRangeDateList(value: String): List<String> =
        gson.fromJson(value, object : TypeToken<List<String>>() {}.type)
}