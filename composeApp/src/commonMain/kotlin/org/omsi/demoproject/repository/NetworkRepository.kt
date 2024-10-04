package org.omsi.demoproject.repository

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.core.ByteReadPacket
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.readByteArray
import net.sergeych.sprintf.sprintf
import org.lighthousegames.logging.logging
import kotlin.coroutines.CoroutineContext

class NetworkRepository() {

    private val remoteHostIp = "192.168.4.1" //HARDCODED
    private val udpPort = 3333
    private var selectorManager: SelectorManager?= null
    private var socket: BoundDatagramSocket?= null

    private val handler : CoroutineExceptionHandler
    private val jobContext : CoroutineContext
    private val jobScope : CoroutineScope


    init{
        handler =
            CoroutineExceptionHandler { context_, throwable -> println("throwable: $throwable, jobContext: ${context_[Job]}") }
        jobContext = SupervisorJob() + Dispatchers.Default + handler
        jobScope = CoroutineScope(jobContext)
    }

    fun openSocket(callback: (String) -> Unit){



        jobScope.launch(context = Dispatchers.IO) {

            runBlocking {

                selectorManager = SelectorManager(Dispatchers.IO)
                //connect or bind ?
                socket = aSocket(selectorManager!!).udp().bind(InetSocketAddress("0.0.0.0", udpPort))
                log.i{"Connected, waiting for data..."}

                while (true) {
                    //   val receiveChannel = socket!!.openReadChannel()
                    //  val sendChannel = socket!!.(autoFlush = true)

                    val packet = socket!!.receive()
                    // val packet = receiveChannel.readUTF8Line()
                    var data = byte2String(packet.packet.readByteArray())
                    log.i{"Received ${data}"}


                    //  sendChannel.writeStringUtf8("Ciao!")
                     socket!!.send(Datagram(ByteReadPacket("Echo ${packet.packet.readByteArray()}".encodeToByteArray()), InetSocketAddress(remoteHostIp, udpPort)))

                }

            }
        }

    }

    fun closeSocket(){
        if(socket!=null){
            socket!!.close()
            selectorManager!!.close()
            log.i{"socket closed"}
        }
    }


    companion object {
        private val log = logging("NetworkRepository")

        fun byte2String(array: ByteArray): String? {
            var txt = ""
            for (i in array) {

               // txt += stringFormat(i," x%02X")
                txt += " x%02X".sprintf(i)
            }
            return txt
        }
    }
}


