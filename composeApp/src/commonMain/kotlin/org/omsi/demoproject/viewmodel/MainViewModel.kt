package org.omsi.demoproject.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.lighthousegames.logging.logging
import org.omsi.demoproject.db.AppDatabase
import org.omsi.demoproject.models.Parameter
import org.omsi.demoproject.repository.NetworkRepository

open class MainViewModel : ViewModel(), KoinComponent {

    var status by mutableStateOf(false)
    val networkRepository: NetworkRepository by inject()
    val database: AppDatabase by inject(named("app-db"))

    var number = 1

    init {
        log.verbose { "Init MainViewModel" }
        number = (1..100).random()
    }

    fun addEntry(){
        viewModelScope.launch {
            log.info{"Adding entry"}
            val count = database.parameterDao().getAllParameters().count()
            log.info{"Total entries: $count"}
            database.parameterDao().insert(Parameter(name = "Parameter${count}"))
            log.info{"Entry added."}
        }
    }

    fun connect(){
        viewModelScope.launch {
            networkRepository.openSocket {
                log.info{"message: $it"}
            }
        }
        log.info{"connected"}
    }

    fun disconnect() {
        networkRepository.closeSocket()
        log.info{"disconnected"}
    }

    fun printNumber(){
        log.info{"number: $number"}
    }


    // the class from where logging() was called will be used as the tag in the logs
    companion object {
        private val log = logging("MainViewModel")
    }
}