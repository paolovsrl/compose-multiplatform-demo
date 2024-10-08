package org.omsi.demoproject.di


import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.omsi.demoproject.db.AppDatabase
import org.omsi.demoproject.db.getAppDatabase
import org.omsi.demoproject.db.getDatabaseBuilder

/*
actual fun platformModule() = module {
    single<AppDatabase> { getDatabaseBuilder() }
}*/



actual fun appDatabaseModule(): Module {
    return module{single<AppDatabase> (named("app-db")){ getAppDatabase(getDatabaseBuilder())}}
}