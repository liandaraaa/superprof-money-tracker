package com.smpn8yk.nomo_plan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "MoneyPlan")
data class MoneyPlan(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int?,
    @SerializedName("total_days")
    val total_days:Int,
    @SerializedName("nominal")
    val nominal: Int,
    @SerializedName("budget")
    val budget: Int,
    @SerializedName("start_date")
    val start_date: String,
    @SerializedName("end_date")
    val end_date: String
)
