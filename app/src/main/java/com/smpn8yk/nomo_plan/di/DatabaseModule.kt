package com.smpn8yk.nomo_plan.di

import android.content.Context
import androidx.room.Room.databaseBuilder
import com.smpn8yk.nomo_plan.data.db.MoneyPlanDatabase
import com.smpn8yk.nomo_plan.data.local.MoneyPlanDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideMoneyPlanDao(moneyPlanDatabase:MoneyPlanDatabase):MoneyPlanDao{
        return moneyPlanDatabase.moneyPlanDao()
    }

    @Provides
    @Singleton
    fun provideMoneyPlanDatabase(@ApplicationContext context: Context):MoneyPlanDatabase{
        return databaseBuilder(
            context,
            MoneyPlanDatabase::class.java,
            "moneyplan-database"
        )
            .allowMainThreadQueries()
            .build()
    }
}