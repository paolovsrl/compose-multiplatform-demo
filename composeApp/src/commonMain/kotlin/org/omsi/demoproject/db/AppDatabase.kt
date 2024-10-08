package org.omsi.demoproject.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.component.KoinComponent
import org.lighthousegames.logging.logging
import org.omsi.demoproject.models.Parameter



@Database(entities = [Parameter::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
// @TypeConverters(UserConverter::class) -> Include for complex data class
abstract class AppDatabase : RoomDatabase() {
    abstract fun parameterDao(): ParameterDao
    /*override fun clearAllTables() {
        super.clearAllTables()
    }*/
}


// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

//Common configuration. The builder is retrieved from each platform with Koin
fun getAppDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {

    val log = logging("AppDatabase")
    log.info{"getDatabaseBuilder"}

    return builder
       // .addMigrations(MIGRATIONS)
        .fallbackToDestructiveMigration(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}


