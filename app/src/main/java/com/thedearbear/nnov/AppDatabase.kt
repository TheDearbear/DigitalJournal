package com.thedearbear.nnov

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thedearbear.nnov.account.LocalAccount
import com.thedearbear.nnov.account.LocalAccountDao
import com.thedearbear.nnov.utils.ZonedDateTimeConverter

@Database(entities = [LocalAccount::class], version = 1)
@TypeConverters(ZonedDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): LocalAccountDao
}