package com.callonly.launcher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.callonly.launcher.data.model.Contact

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}
