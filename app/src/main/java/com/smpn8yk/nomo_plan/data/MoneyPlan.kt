package com.smpn8yk.nomo_plan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "MoneyPlan")
data class MoneyPlan(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("total_days")
    val total_days:Int = 0,
    @SerializedName("nominal")
    val nominal: Int = 0,
    @SerializedName("budget")
    val budget: Int = 0,
    @SerializedName("range_dates")
    val range_dates: List<String> = listOf(),
    @SerializedName("status")
    val status: String = MoneyPlanStatus.PENDING.name
)

enum class MoneyPlanStatus {
    PENDING,
    COMPLETE
}
