package org.omsi.demoproject.temp

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.closeQuietly
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.LinkedList


class WifiUdpCanHelper {
    //Remember to add android:usesCleartextTraffic="true" to Manifest Application{ -- }


    private val TAG = "UDP_CAN"
    private val CAN_PORT = 98
    private val remoteHostIp = "192.168.4.1" //HARDCODED
    private val udpPort = 3333
    private var receiveSocket : DatagramSocket? = null

    @Volatile
    var udpEnable = false
    var receivingJob = kotlinx.coroutines.Job()
    var sendingJob = kotlinx.coroutines.Job()

    val sendList = LinkedList<CANPacket>()


    val receiveList = LinkedList<DatagramPacket>()

    fun initRepo(){
        // inside try-catch: val receiveSocket = DatagramSocket(udpPort, InetAddress.getByName(remoteHostIp))
        Log.d(TAG, "Initializing UDP-CAN interface...")

        udpEnable = true
        runBlocking(Dispatchers.IO){
            receivingJob = kotlinx.coroutines.Job()
            receivingJob.invokeOnCompletion {
                Log.d(TAG, "Completed")
            }

            launch (Dispatchers.IO + receivingJob) {
                                      //Receive UDP and manage received data:
                    val buffer = ByteArray(2600) //check .c code for having the same buffer size
                    try {
                        //Keep a socket open to listen to all the UDP traffic that is destined for this port
                        Log.d(TAG, "Opening socket")
                        receiveSocket = DatagramSocket(udpPort)
                        Log.d(TAG, "Socket open.")
                        receiveSocket?.broadcast = false//true

                        while(udpEnable) {
                            //Log.d(TAG, "Waiting for UDP data...")
                            val udpPacket = DatagramPacket(buffer, buffer.size)
                            receiveSocket?.receive(udpPacket)
                            //Log.d(TAG, "UDP packet received = " + Utils.Hex2String(udpPacket.data))
                            receiveList.add(udpPacket)

                            //Sending a confirmation,
                            val sendPacket: DatagramPacket = DatagramPacket(
                                byteArrayOf(0),
                                1,
                                InetAddress.getByName(remoteHostIp),
                                udpPort
                            )
                            receiveSocket?.send(sendPacket)
                        }

                    } catch (e: Exception) {
                        Log.e(TAG, "Receive UDP catch exception." + e.toString())
                        e.printStackTrace()
                    } finally {
                        receiveSocket?.close()
                        Log.d(TAG, "Closing receiving socket")
                    }

                 Log.d(TAG, "Terminating receiving thread...")
            }

            //Handling received obj
            launch (Dispatchers.Default + receivingJob) {
                while(udpEnable) {
                    if(receiveList.isNotEmpty()){
                        // Log.d(TAG, "ReceiveList size: ${receiveList.size}")
                        decodePackets()
                    }
                }
                //Log.d(TAG, "Terminating sending thread...")
            }

            //Sending
            launch (Dispatchers.IO + sendingJob) {
                while(udpEnable) {
                    if(sendList.isNotEmpty()){
                        val pk = sendList.poll()
                        if(pk!=null)
                            sendUDP(pk)
                    }
                }
                //Log.d(TAG, "Terminating sending thread...")
            }
        }




    }

    private fun decodePackets(){

        val rawData = receiveList.poll() ?: return
        // byte[0] and byte[1] contain the number of packets
        // byte[index] contains the checksum
        //Log.d(TAG, "Processing: "+ Utils.Hex2String(rawData.data))
       // Log.d(TAG, "Processing msg of size: ${rawData.length}")

        val lastDataIndex = (rawData.data[0].toInt() and 0xff) or ((rawData.data[1].toInt() and 0xff).shl(8))

        if(lastDataIndex>rawData.length) {
            Log.e(TAG, "Wrong lastDataIndex value!")
            return
        }
      //  Log.d(TAG, "Last data index: $lastDataIndex contains ${rawData.data[lastDataIndex].toUByte()}")

        //Checksum
        var sum = 0
        for ( i in 0 until lastDataIndex){
            sum = (sum + (rawData.data[i].toInt() and 0xFF))
        }
       var sumb = sum.toUByte()
        if(sumb != (rawData.data[lastDataIndex].toUByte())){
            Log.e(TAG, "Wrong checksum of UDP received packet! Calculated: ${sumb}, expected: ${(rawData.data[lastDataIndex].toUByte())}, Length:${rawData.length}")
            return
        }

        val numberOfPackets = (rawData.data[2].toInt() and 0xff)
        //Log.d(TAG, "Number of received packet is $numberOfPackets")
        if( numberOfPackets == 0){
            return
        }


        var pointer = 1+2 //b2 is first valid data
        for(i in 0 until numberOfPackets){
            val expectedLength = if(rawData.data[pointer]>0) 13 else 11
            val tempUdp = ByteArray(expectedLength)
            System.arraycopy(rawData.data, pointer, tempUdp, 0, expectedLength)
            val pk = decodeUdpPacket(tempUdp)
            if(pk!=null) {
                canListener?.OnReceivedCanMsg(pk)
            }
            pointer += expectedLength
        }




    }


    private fun decodeUdpPacket(data: ByteArray):CANPacket? {
        if(data.size<10) {
            Log.e(TAG, "Packet size is too short!")
            return null
        }

        //Handle received packet
        val extended = data[0]>0
        //Checking length
        val expectedLength = if(extended) 13 else 11
        if(expectedLength != data.size){
            Log.e(TAG, "Wrong length of UDP packet!")
            return null
        }
       /* //Checksum
        var sum = 0
        for ( i in 0 until expectedLength-1){
            sum = (sum + (udpPacket.data[i].toInt()) and 0xFF)
        }
        if((sum and 0xff).toByte() != (udpPacket.data[expectedLength-1])){
            Log.e(TAG, "Wrong checksum of UDP received packet!")
            return null
        }*/

        var id: Long = (data[1].toLong() and 0xff) or
                (data[2].toLong() and 0xff).shl(8)

        var offset = 3

        if(extended){
            id = id or
                    (data[3].toLong() and 0xff).shl(16) or
                    (data[4].toLong() and 0xff).shl(24)
            offset = 5
        }

        var canPacket = CANPacket(CAN_PORT,id,extended, byteArrayOf(0,0,0,0,0,0,0,0))
        for(i in 0 .. 7){
            canPacket.data[i] = data[i+offset]
        }
        return canPacket
    }

    /*
    private fun putInReceiveQueue(packet: CANPacket){
        itemsReceived[keyRecRef] = packet
        keyRecRef++
        if(keyRecRef==Double.MAX_VALUE) {
            keyRecRef = 0.0
            itemsReceived.clear()
            Log.e(TAG, "Received queue full, clear() performed.")
        }
    }
*/

    fun stop(){
        udpEnable = false
        Log.d(TAG, "Closing UDP Socket...")
            receiveSocket?.closeQuietly()
            /*receiveSocket?.disconnect()
            receiveSocket?.close()*/
        receivingJob.cancel()
        Log.d(TAG, "UDP Job cancelled.")
    }

    fun send(packet: CANPacket){
        sendList.add(packet)
    }



    private fun sendUDP(packet: CANPacket?){
        if(packet==null)
            return
        // Log.d(TAG, "Sending CAN packet as UDP")

        //Same structure for sending multiple packets!
        var length = 17
        var bytes: ByteArray = ByteArray(length)

        bytes[2]=1  //1 packet only
        var offset = 8
        if(packet.extendedFrame) {
            //BigEndian
            bytes[3]=1 //extended ID
            bytes[4] = (packet.id and 0xff).toByte()
            bytes[5] = (packet.id.shr(8) and 0xff).toByte()
            bytes[6] = (packet.id.shr(16) and 0xff).toByte()
            bytes[7] = (packet.id.shr(24) and 0xff).toByte()
        } else{
            length = 15
            offset = 6
            bytes[3]=0 //standard ID
            bytes[4] = (packet.id and 0xff).toByte()
            bytes[5] = (packet.id.shr(8) and 0xff).toByte()
        }
        for (i in 0..7) {
            bytes[i + offset] = packet.data[i]
        }

        bytes[0]= (length and 0xFF).toByte()
        bytes[1]= (length.shr(8) and 0xFF).toByte()

        var sum = 0
        for(i in 0 until length){
            sum=(sum+bytes[i]) and 0xFF
        }
        bytes[length-1] = sum.toByte()

        try {
            val sendSocket = DatagramSocket()
            val sendPacket: DatagramPacket = DatagramPacket(bytes, length, InetAddress.getByName(remoteHostIp), udpPort)
            sendSocket.send(sendPacket)
            //  Log.d(TAG, "fun sendBroadcast: packet sent to: " + InetAddress.getByName(remoteHostIp) + ":" + udpPort)
        } catch (e:Exception){
            e.printStackTrace()
        }

    }







    private val sender = CanSender { pkg -> send(pkg) }
    var canListener: CanListener? = null

    fun getSender(): CanSender {
        return sender
    }
}







