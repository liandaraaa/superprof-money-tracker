package com.smpn8yk.nomo_plan.di

import com.smpn8yk.nomo_plan.domain.MoneyPlanRepository
import com.smpn8yk.nomo_plan.domain.MoneyPlanRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindMoneyPlanRepository(repositoryImpl: MoneyPlanRepositoryImpl):MoneyPlanRepository
}