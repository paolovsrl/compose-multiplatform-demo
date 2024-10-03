package org.omsi.demoproject.viewmodel

import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import org.omsi.demoproject.repository.CanRepository

open class MainViewModel : ViewModel(), KoinComponent {

    val canRepository: CanRepository by inject()

    var number = 1

    init {
        log.verbose { "Init MainViewModel" }
        number = (1..100).random()
    }


    fun connect(){
        log.info{"connect"}
        canRepository.openSocket {
            log.info{"message: $it"}
        }
    }

    fun disconnect() {
        log.info{"disconnect"}
        //TODO("Not yet implemented")
    }

    fun printNumber(){
        log.info{"number: $number"}
    }


    // the class from where logging() was called will be used as the tag in the logs
    companion object {
        private val log = logging("MainViewModel")
    }
}