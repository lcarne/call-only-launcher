package com.incomingcallonly.launcher.di

import com.incomingcallonly.launcher.data.repository.CallLogRepository
import com.incomingcallonly.launcher.data.repository.CallLogRepositoryImpl
import com.incomingcallonly.launcher.data.repository.ContactRepository
import com.incomingcallonly.launcher.data.repository.ContactRepositoryImpl
import com.incomingcallonly.launcher.data.repository.SettingsRepository
import com.incomingcallonly.launcher.data.repository.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindContactRepository(
        contactRepositoryImpl: ContactRepositoryImpl
    ): ContactRepository

    @Binds
    @Singleton
    abstract fun bindCallLogRepository(
        callLogRepositoryImpl: CallLogRepositoryImpl
    ): CallLogRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}
