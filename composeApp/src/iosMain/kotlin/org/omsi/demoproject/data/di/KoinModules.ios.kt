package org.omsi.demoproject.data.di


import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.omsi.demoproject.data.DATABASE_NAME
import org.omsi.demoproject.data.db.AppDatabase
import org.omsi.demoproject.data.db.getAppDatabase
import org.omsi.demoproject.data.db.getDatabaseBuilder

/*
actual fun platformModule() = module {
    single<AppDatabase> { getDatabaseBuilder() }
}*/



actual fun appDatabaseModule(): Module {
    return module{single<AppDatabase> (named(DATABASE_NAME)){ getAppDatabase(getDatabaseBuilder()) }}
}