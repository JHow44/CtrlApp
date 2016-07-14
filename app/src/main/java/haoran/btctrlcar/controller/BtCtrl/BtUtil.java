package haoran.btctrlcar.controller.BtCtrl;


import android.util.Log;

import java.io.Serializable;

import haoran.btctrlcar.common.Packet;
import haoran.btctrlcar.common.Constants;

/**
 * Created by Administrator on 2015/7/19.
 */
public class BtUtil implements Serializable{
    private static final String TAG ="BtUtil";

    public static Packet toBtPacket(byte[] bytes) {
        if(bytes[0] == Constants.PACKET_INVALID ){
            Log.e(TAG, "input bytes is invalid for Packet conversion");
            return null;
        }
        Packet packet = new Packet();
        packet.setPacketType(bytes[0]);
        byte[] data = null;
        System.arraycopy(bytes, Packet.headerOffset, data, 0, bytes.length - Packet.headerOffset);
        packet.setData(data);
        return packet;
    }

}
