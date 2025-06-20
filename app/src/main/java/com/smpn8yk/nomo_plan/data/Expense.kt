package com.smpn8yk.nomo_plan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Expense")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("expense_id")
    val expense_id: Int?,
    @SerializedName("plan_id")
    val plan_id: Int?,
    @SerializedName("item")
    val item:String,
    @SerializedName("price")
    val price: Int
)
