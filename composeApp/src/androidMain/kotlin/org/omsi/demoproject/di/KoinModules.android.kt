package org.omsi.demoproject.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import io.kamel.core.applicationContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.get
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.omsi.demoproject.db.AppDatabase
import org.omsi.demoproject.db.getAppDatabase
import org.omsi.demoproject.db.getDatabaseBuilder

actual fun appDatabaseModule() = module {
    single<AppDatabase> (named("app-db")){ getAppDatabase(getDatabaseBuilder(get())) } //getting context with koin
}
