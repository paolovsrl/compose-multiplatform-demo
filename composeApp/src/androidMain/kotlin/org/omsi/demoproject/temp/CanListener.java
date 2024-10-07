package org.omsi.demoproject.temp;

public interface CanListener {
    void OnStatusChange(Boolean connected);
    void OnReceivedCanMsg(CANPacket packet);
    void OnReceivedBamData(BAMPacket packet);
}
