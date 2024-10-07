package org.omsi.demoproject.temp;


public class CANPacket{
    //ID: 3b priority | 1b reserved | 1b datapage | 8b PDU format | 8b PDU specific | 8B source address
    public long id;
    public byte[] data = new byte[8];
    public int channel;
    public boolean extendedFrame = true;


    /**
     * @param id extended CAN id
     * @param data 8 bytes
     */
    public CANPacket(long id, byte[] data){
        this.id=id;
        this.data=data;
        //System.arraycopy(data, 0, this.data, 0, data.length);
    }

    /**
     * @param channel specify where to send the message
     * @param id extended CAN id
     * @param data 8 bytes
     */
    public CANPacket(int channel, long id, byte[] data){
        this.channel = channel;
        this.id=id;
        this.data=data;
        //System.arraycopy(data, 0, this.data, 0, data.length);
    }

    /**
     * @param channel specify where to send the message
     * @param id extended CAN id
     * @param data 8 bytes
     */
    public CANPacket(int channel, long id, boolean extended,  byte[] data){
        this.channel = channel;
        this.id=id;
        this.extendedFrame = extended;
        this.data=data;
        //System.arraycopy(data, 0, this.data, 0, data.length);
    }

    /**
     *
     * @return last 2 bytes of the CAN id
     */
    public int getSourceAddress(){
        return (int) (id&0xFF);
    }

    /**
     * To avoid conflict in handling BAM data from multiple CAN buses
     * @return 0xAABB where AA is the source and BB is can channel.
     */
    public int getSourceAddressChannel(){
        return (int) ((id&0xFF)<<2 | channel);
    }
}
