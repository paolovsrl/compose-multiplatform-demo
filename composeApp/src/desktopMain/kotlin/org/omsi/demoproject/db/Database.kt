package org.omsi.demoproject.db

import androidx.room.Room
import androidx.room.RoomDatabase
import org.lighthousegames.logging.logging
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val log = logging("JVM_specific")
    log.info{"getDatabaseBuilder"}
    val dbFile = File(System.getProperty("java.io.tmpdir"), "app_data.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}
