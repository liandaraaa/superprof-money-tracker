package com.smpn8yk.nomo_plan.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoneyPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(moneyPlan: MoneyPlan)

    @Query("Select * From moneyPlan Where id = :id")
    suspend fun getMoneyPlanById(id: Int): MoneyPlan?

    @Query("DELETE FROM moneyPlan WHERE id = :id")
    suspend fun deleteMoneyPlanById(id: Int)

    @Query("SELECT * FROM moneyPlan")
    suspend fun getAlLMoneyPlans(): List<MoneyPlan?>
}