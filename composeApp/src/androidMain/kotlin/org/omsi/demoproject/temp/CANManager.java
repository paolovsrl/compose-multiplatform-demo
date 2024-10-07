package org.omsi.demoproject.temp;

import static com.omsi.cpdevicelib.Utils.isCPVXSupported;
import static com.omsi.cpdevicelib.Utils.isDeviceSupported;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cpdevice.cpcomm.proto.Protocol;
import com.omsi.cpdevicelib.CPDevices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class CANManager {

    private final String TAG = "CANManager";
    ApolloCanHelper apolloCanHelper;
    SpringCanHelper springCanHelper;
    ApolloXCanHelper apolloXCanHelper;
    Apollo2CanHelper apollo2CanHelper;
    WifiTcpCanHelper wifiTcpCanHelper;
    WifiUdpCanHelper wifiUdpCanHelper;
    CPVXCanHelper cpvxCanHelper;

    private static final long TP_CM_mask = 0x00EC0000;
    private static final long TP_TD_mask = 0x00EB0000;
    private Boolean useWifi = false;

    private CanSender canSender;
    private CanListener exposedCanListener;
    private final CanListener localCanListener = new CanListener() {
        @Override
        public void OnStatusChange(Boolean connected) {
            canInitialized = connected;
        }

        @Override
        public void OnReceivedCanMsg(CANPacket packet) {
            if(exposedCanListener != null && (canChannel==0 || packet.channel==canChannel)) {
                long masked = packet.id & 0x00FF0000L;
                if(masked == 0x00EC0000 || masked == 0x00EB0000){
                    handleBamData(masked, packet);
                }
                //Deliver the data in any case
                exposedCanListener.OnReceivedCanMsg(packet);
            }
        }

        @Override
        public void OnReceivedBamData(BAMPacket packet) {
            if(exposedCanListener!=null)
                exposedCanListener.OnReceivedBamData(packet);
        }
    };

    private List<CANPacket> cyclicMessagesList = new ArrayList<>();
    private Handler msgHandler;
    private boolean isCycleActive = false, sendFeedback = false;
    private int canChannel = 0; //1 or 2. Then 99 means "use wifi-tcp", 98->UDP.
    private int canSpeed = Protocol.CAN_BAUD_250K;
    private ArrayList<Integer> supportedSpeeds = new ArrayList<Integer>(Arrays.asList(Protocol.CAN_BAUD_125K, Protocol.CAN_BAUD_250K, Protocol.CAN_BAUD_500K));
    private int TIME_INTERVAL = 200; //ms
    private boolean canInitialized = false; //TODO check it even in addCyclicMsg() ?

    //TODO set baud rate?

    /**
     * Default implementation: all available can channel are considered
     * Remember to initializeCanInterface() and setCanListener() afterwards
     */
    public CANManager(){

    }


    /**
     * Specify the can channel to ignore the data on the others
     * @param canChannel = 0: all (1-2), 1, 2, 99: Wifi TCP-IP (Deprecated), 98 Wifi UDP
     * Remember to initializeCanInterface() and setCanListener() afterwards
     */
    public CANManager(int canChannel){
        if(canChannel>90){
            useWifi=true;
        }
        this.canChannel = canChannel;
    }

    public void setCanSpeed(int baud){
        if(supportedSpeeds.contains(baud)) {
            this.canSpeed = baud;
            Log.d(TAG, "CAN speed set to "+String.valueOf(baud));
        }
    }

    /**
     * Initialize the CAN interface in base of the device.
     * @param context to bound services and call Native functions
     */
    public void initializeCanInterface(Context context){
        msgHandler = new Handler(Looper.getMainLooper());

        Log.d(TAG, "Device manufacturer "+ Build.MANUFACTURER+", Model: "+Build.MODEL);

        if(canInitialized){
            Log.d(TAG, "CAN already initialized");
            removeAllCyclicMessages();
            invalidate();
        }

        if(useWifi){
            //TODO 500 kbps speed? --> Must be done on the device settings.
            if(canChannel==99) {
                wifiTcpCanHelper = new WifiTcpCanHelper();
                wifiTcpCanHelper.initRepo();
                canSender = wifiTcpCanHelper.getSender();
                wifiTcpCanHelper.setCanListener(this.localCanListener);
                canInitialized = true;
            }
            else if(canChannel==98){
                //TODO UDP
                wifiUdpCanHelper = new WifiUdpCanHelper();
                wifiUdpCanHelper.initRepo();
                canSender = wifiUdpCanHelper.getSender();
                wifiUdpCanHelper.setCanListener(this.localCanListener);
                canInitialized = true;
            }
        }else if(isDeviceSupported() && isCPVXSupported()){
            //After 2024 all OS version implement CPVX
            cpvxCanHelper= new CPVXCanHelper(canSpeed);
            canSender = cpvxCanHelper.getSender();
            cpvxCanHelper.setCanListener(this.localCanListener);
            Log.d(TAG, "CPVX interface loaded.");
            canInitialized = true;

        } else
            switch (new CPDevices().getDeviceFamily()) {
                case APOLLO:{
                    if (canChannel == 0) {
                        Log.e(TAG, "APOLLO can work only with one channel at time. Setting to CAN1");
                        canChannel = 1;
                    }
                    apolloCanHelper = new ApolloCanHelper(context, canChannel, canSpeed);
                    canSender = apolloCanHelper.getSender();
                    apolloCanHelper.setCanListener(this.localCanListener);
                    Log.d(TAG, "APOLLO SPI loaded.");
                    canInitialized = true;
                    break;
                }
                case APOLLO_PRO:{
                    //Note for Apollo10pro. After 20231101 version the cpp files must be excluded. Lib is already included in the OS. This is now default.
                    //APOLLO X has only one channel
                    apolloXCanHelper = new ApolloXCanHelper(canSpeed);
                    canSender = apolloXCanHelper.getSender();
                    apolloXCanHelper.setCanListener(this.localCanListener);
                    canInitialized = true;
                    Log.d(TAG, "APOLLO_PRO SPI loaded.");
                    break;
                }
                case SPRING:{
                    springCanHelper = new SpringCanHelper(canSpeed);
                    canSender = springCanHelper.getSender();
                    springCanHelper.setCanListener(this.localCanListener);
                    Log.d(TAG, "SPRING SPI loaded.");
                    canInitialized = true;
                    break;
                }
                case SPRING2:
                case APOLLO2:{
                    apollo2CanHelper = new Apollo2CanHelper(canSpeed);
                    canSender = apollo2CanHelper.getSender();
                    apollo2CanHelper.setCanListener(this.localCanListener);
                    Log.d(TAG, "APOLLO2 SPI loaded.");
                    canInitialized = true;
                    break;
                }
                case UNKNOWN:{
                    Log.e(TAG, "Utils.isDeviceSupported(..) has not been updated. Edit CpDevice Lib and republish.");
                }
            }

    }

    public void setCanListener(CanListener canListener){
        this.exposedCanListener = canListener;
    }


    /**
     * @param packet address and data to send. It is possible to specify the CAN channel
     */
    public void send(CANPacket packet){
        if(canInitialized && canSender!=null){
            if(canChannel!=0)
                packet.channel=canChannel;
            canSender.SendCanMsg(packet);
        }
    }


    /**
     * @param packets List of messages to send via CAN. It is possible to specify the CAN channel
     */
    public void sendMultiplePackets(List<CANPacket> packets){
        if(canInitialized && canSender!=null && packets!=null){
            msgHandler.post(new Runnable() {
                @Override
                public void run() {
                    for(CANPacket packet : packets){
                        try {
                            send(packet);
                            Thread.sleep(TIME_INTERVAL);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (cyclicMessagesList != null && cyclicMessagesList.size()>0 && canSender!=null && canInitialized) {
                for(CANPacket message : cyclicMessagesList) {
                    send(message);
                    if (sendFeedback && localCanListener != null)
                        localCanListener.OnReceivedCanMsg(message);
                }
            }
            msgHandler.postDelayed(this, TIME_INTERVAL);
        }
    };


    /**
     * Close the communication.
     */
    public void invalidate(){
        Log.d(TAG, "Invalidate SPI Instance");
        removeAllCyclicMessages();
        exposedCanListener = null;

        if(canInitialized) {
            if (useWifi) {
                if(canChannel==99)
                    wifiTcpCanHelper.disconnect();
                else if (canChannel==98){
                    wifiUdpCanHelper.stop();
                }
            } else if(isCPVXSupported()){
                cpvxCanHelper.invalidate();
            }else{
                switch (new CPDevices().getDeviceFamily()) {
                    case APOLLO:{
                        apolloCanHelper.stopCommunication();
                        break;
                    }
                    case APOLLO_PRO:{
                        apolloXCanHelper.destroy();
                        break;
                    }
                    case SPRING:{
                        springCanHelper.stopCommunication();
                        break;
                    }
                    case SPRING2:
                    case APOLLO2:{
                        apollo2CanHelper.stopCommunication();
                        break;
                    }
                    case UNKNOWN:{
                        Log.d(TAG, "CAN interface not supported.");
                    }

                }
            }
            canInitialized = false;
        }
    }


    /**
     * Set a list of messages to cyclically send
     * @param messages list of CANPacket to send
     * @param feedback put the message on the incoming queue once sent
     * @param reset clear the list before add the messages
     */
    public void addCyclicMessages(List<CANPacket> messages, boolean feedback, boolean reset){
        this.sendFeedback = feedback;
        if(!isCycleActive){
            msgHandler.postDelayed(runnable, TIME_INTERVAL);
            isCycleActive = true;
        }
        if(reset){
            this.cyclicMessagesList.clear();
        }
        this.cyclicMessagesList.addAll(messages);
    }

    /**
     * Set a list of messages to cyclically send
     * @param message CANPacket to send
     * @param feedback put the message on the incoming queue once sent
     * @param reset clear the list before add the messages
     */
    public void addCyclicMessage(CANPacket message, boolean feedback, boolean reset){
        if(!canInitialized)
            return;
        this.sendFeedback = feedback;
        if(!isCycleActive){
            isCycleActive = true;
            msgHandler.postDelayed(runnable, TIME_INTERVAL);
        }
        if(reset){
            this.cyclicMessagesList.clear();
        } else{
            //Remove any existent msg with the same id
            ArrayList<CANPacket> list = new ArrayList<>();
            for(CANPacket pkt : cyclicMessagesList) {
                if(pkt.id!= message.id){
                    list.add(pkt);
                }
            }
            this.cyclicMessagesList.clear();
            this.cyclicMessagesList.addAll(list);
        }
        this.cyclicMessagesList.add(message);
    }

    /**
     * Default time interval is 200ms
     * @param msInterval of the cyclic messages
     */
    public void setTimeInterval(int msInterval){
        if(msInterval>50){
            this.TIME_INTERVAL= msInterval;
        }
    }

    /**
     * Stop to send the cyclic messages and clear the list
     */
    public void removeAllCyclicMessages(){
        if(!canInitialized)
            return;
        this.sendFeedback = false;
        this.cyclicMessagesList.clear();
        msgHandler.removeCallbacksAndMessages(null);
        isCycleActive = false;
        Log.d(TAG, "Cyclic message removed");
    }

    public void removeCyclicMessage(long id){
        ArrayList<CANPacket> list = new ArrayList<>();
        for(CANPacket pkt : cyclicMessagesList) {
            if(pkt.id!= id){
                list.add(pkt);
            }
        }
        this.cyclicMessagesList.clear();
        this.cyclicMessagesList.addAll(list);
    }

    public void removeCyclicMessages(long[] ids){
        ArrayList<CANPacket> list = new ArrayList<>();
        for(CANPacket pkt : cyclicMessagesList) {
            for(long id :ids)
                if(pkt.id!= id){
                    list.add(pkt);
                    break;
                }
        }
        this.cyclicMessagesList.clear();
        this.cyclicMessagesList.addAll(list);
    }











    /*************** BAM DATA handling ************************************************************/



    /**
     * @param data String to convert. Max length:1500
     * @param TP_CM Transport Protocol Connection Management message ID
     * @param TP_DT Transport Protocol Data Transfer message ID
     * @return List of CANPacket containing the String + final CRC byte
     *
     *  Data Bytes of TP.CM:
     *            1   - Control Byte = 32 (0x20)
     *            2,3 - Message size (LSB first)
     *            4   - Total number of packets
     *            5   - Reserved (0xFF)
     *            6-8 - Parameter Group Number of the multi-packet message (6=LSB, 8=MSB)
     *
     *            PGN definition:
     *            0x01 Job data
     *            0x02 .. 0xffff JobEntry data
     *
     */
    public static List<CANPacket> generateBAMPackets(byte[] data, long TP_CM, long TP_DT, long PGN, int canPort){
        List<CANPacket> list = new ArrayList<>();
        byte[] cmData = new byte[8];
        int dataLength = data.length;
        byte[] bamData;

        if(dataLength>1500){
            dataLength=1500;
        }
        bamData = new byte[dataLength];
        System.arraycopy(data, 0, bamData, 0, dataLength);

        cmData[0] = 0x20;
        cmData[1] = (byte) dataLength;
        cmData[2] = (byte) (dataLength>>8);
        cmData[3] = (byte) Math.ceil(dataLength/7f);
        cmData[4] = (byte) 0xff;
        cmData[5] = (byte) (PGN);
        cmData[6] = (byte) (PGN>>8);
        cmData[7] = (byte) (PGN>>16);

        list.add( new CANPacket(canPort, TP_CM, cmData));

        for( int i = 0; i< Math.ceil(bamData.length/7f); i++){
            byte[] filledData = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
            filledData[0] = (byte) (i+1);
            System.arraycopy(bamData, 7*i, filledData, 1, Math.min(7,(bamData.length-7*i)));
            list.add(new CANPacket(canPort, TP_DT, filledData));
        }

        return list;
    }


    HashMap<Integer, BAMPacket> tempBamData = new HashMap<>();

    private void handleBamData(long maskedId, CANPacket packet){
        int currentSourceID = packet.getSourceAddressChannel();

        //TP.Communication Manager flow control
        if (maskedId==TP_CM_mask){
            //reset if already present
            tempBamData.remove(currentSourceID);
            int dataLength = (0xff&packet.data[1]) | (0xff&packet.data[2])<<8;
            int currentPgn = (0xff&packet.data[5]) | (0xff&packet.data[6])<<8 | (0xff&packet.data[7])<<16;
            tempBamData.put(currentSourceID, new BAMPacket(packet.channel, currentPgn, packet.getSourceAddress(), new byte[dataLength]));
            // bamCounter=0;
        }

        //data message
        BAMPacket bPkt = tempBamData.get(currentSourceID);
        if (maskedId == TP_TD_mask && bPkt!=null) {
            if (bPkt.bamCounter<bPkt.data.length && bPkt.data.length>0){
                int dataLength = Math.min(7, bPkt.data.length-bPkt.bamCounter);
                System.arraycopy(packet.data,1,bPkt.data, bPkt.bamCounter,dataLength);
                bPkt.bamCounter+=dataLength;

                if (bPkt.bamCounter==bPkt.data.length) {
                    //data complete: post value and remove from memory
                    localCanListener.OnReceivedBamData(bPkt);
                    tempBamData.remove(currentSourceID);
                }
            }
        }
    }


}
