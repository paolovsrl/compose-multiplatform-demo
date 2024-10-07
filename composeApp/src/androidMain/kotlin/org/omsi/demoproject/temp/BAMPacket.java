package org.omsi.demoproject.temp;

public class BAMPacket {
    public int sourceAddress;
    public int pgn;
    public byte[] data;
    public int channel;
    public int bamCounter; //used only while collecting the data from the bus





    /**
     * @param channel specify where to send the message
     * @param pgn Parameter Group Number of the multi-packet message (18bits)
     * @param sourceAddress last byte of Source address CAN ID
     * @param data 8 data received
     */
    public BAMPacket(int channel, int pgn, int sourceAddress, byte[] data){
        this.channel = channel;
        this.pgn=pgn;
        this.sourceAddress = sourceAddress;
        this.data = data.clone();
        bamCounter = 0;
        //System.arraycopy(data, 0, this.data, 0, data.length);
    }
}
