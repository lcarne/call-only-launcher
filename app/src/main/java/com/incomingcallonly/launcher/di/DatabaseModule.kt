package com.incomingcallonly.launcher.di

import android.content.Context
import androidx.room.Room
import com.incomingcallonly.launcher.data.local.AppDatabase
import com.incomingcallonly.launcher.data.local.CallLogDao
import com.incomingcallonly.launcher.data.local.ContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "incomingcallonly_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideContactDao(database: AppDatabase): ContactDao {
        return database.contactDao()
    }

    @Provides
    fun provideCallLogDao(database: AppDatabase): CallLogDao {
        return database.callLogDao()
    }
}
