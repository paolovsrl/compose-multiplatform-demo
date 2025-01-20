package org.omsi.demoproject.data.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.omsi.demoproject.data.repo.NetworkRepository
import org.omsi.demoproject.ui.viewmodel.MainViewModel

val providehttpClientModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(json = Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
            }
        }
    }
}


//Room DB:
expect fun appDatabaseModule(): Module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
}

val repositoryModule = module {
    singleOf(::NetworkRepository)
}


val getDbModule = { appDatabaseModule() }

