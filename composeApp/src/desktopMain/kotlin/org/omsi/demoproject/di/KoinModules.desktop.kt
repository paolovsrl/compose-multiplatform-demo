package org.omsi.demoproject.di

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.omsi.demoproject.db.AppDatabase
import org.omsi.demoproject.db.getAppDatabase
import org.omsi.demoproject.db.getDatabaseBuilder
import java.io.File

/*
actual fun platformModule() = module {
    single<AppDatabase> { getDatabaseBuilder() }
}*/


actual fun appDatabaseModule()= module {
    single<AppDatabase> (named("app-db")){ getAppDatabase(getDatabaseBuilder()) }}

