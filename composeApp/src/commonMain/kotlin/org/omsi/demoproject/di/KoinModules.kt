package org.omsi.demoproject.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.omsi.demoproject.repository.NetworkRepository
import org.omsi.demoproject.viewmodel.MainViewModel

val providehttpClientModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(json = Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
            }
        }
    }
}


/*
In case of dependency in the constructor: get()
e.g.
val provideRepositoryModule = module {
    single<Repository> { Repository(get()) }
}



val provideNetworkRepositoryModule = module {
    single<NetworkRepository> { NetworkRepository() }
}

val viewModelModule = module {
    viewModelOf(::MainViewModel)
}

fun appModule() = listOf(providehttpClientModule, provideNetworkRepositoryModule, viewModelModule)
*/


/*
fun appModule() = module {
    single { CanRepository() }

    viewModelDefinition { MainViewModel() }
}

 */


