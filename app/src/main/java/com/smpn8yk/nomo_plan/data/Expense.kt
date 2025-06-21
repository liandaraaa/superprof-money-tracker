package com.smpn8yk.nomo_plan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Expense")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("expense_id")
    val expense_id: Int? = null,
    @SerializedName("plan_id")
    val plan_id: Int? = null,
    @SerializedName("date")
    val date: String = "",
    @SerializedName("item")
    val item:String = "",
    @SerializedName("price")
    val price: Int = 0,
    @SerializedName("report_status")
    val report_status: String = ExpenseReportStatus.NONE.name
)

enum class ExpenseReportStatus {
    NONE,
    EMPTY,
    SUCCESS,
    FAILED
}
