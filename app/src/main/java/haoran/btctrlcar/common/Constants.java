
package haoran.btctrlcar.common;

import haoran.btctrlcar.controller.BtCtrl.BtCtrl;

/**
 * Defines several constants used between {@link BtCtrl} and the UI.
 */
public interface Constants {

    public static final int PACKET_SIZE = 1024;
    // Defined Packet types
    // Remember to update the same constants in btsimulator (java simulator app)
    public static final byte PACKET_INVALID = 0;
//    public static final byte PACKET_CONTROL = 1;
    public static final byte PACKET_ENGINE_MOVE = 1;
    public static final byte PACKET_CAMERA_SWITCH = 2;
    public static final byte PACKET_CAMERA_MOVE = 3;

    //
    public enum wifictrl_status {
        INITIALIZED,
        CONNECTED,
        CONNECTION_LOST,
        UNKNOWN_ERROR
    }

}
