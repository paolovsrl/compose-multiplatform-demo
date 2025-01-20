package org.omsi.demoproject.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.omsi.demoproject.data.DATABASE_PATH
import org.omsi.demoproject.data.db.AppDatabase

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(DATABASE_PATH)
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}