package org.omsi.demoproject.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import org.lighthousegames.logging.logging
import org.omsi.demoproject.data.DATABASE_PATH
import org.omsi.demoproject.data.db.AppDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val log = logging("JVM_specific")
    log.info{"getDatabaseBuilder"}
    val dbFile = File(System.getProperty("java.io.tmpdir"), DATABASE_PATH)
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}
