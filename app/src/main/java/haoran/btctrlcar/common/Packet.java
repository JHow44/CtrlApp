package haoran.btctrlcar.common;

import android.util.Log;

import java.io.Serializable;

import haoran.btctrlcar.common.Constants;

/**
 * Created by Administrator on 2015/7/19.
 */
public class Packet implements Serializable {
    /*  READ before coding!
        Remember to update the header offset if adding any more header for the packet!
    */
    private static final String TAG ="Packet";
    public static final int headerOffset = 1;
    private byte packetType;
    private byte data[] ;

    public Packet(){
        this.packetType = Constants.PACKET_INVALID;
        this.data = null;
    }
    public Packet(int x, int y, int offset) // offset option is used to choose the car movement package or camera movement package: 0 for car, 1 for camera
    {
        byte data[] = new byte[2];
        switch (offset){
            case 0:
                this.packetType = Constants.PACKET_ENGINE_MOVE;
                data[0] = (byte)x;
                data[1] = (byte)y;
                setData(data);
                break;
            case 1:
                this.packetType = Constants.PACKET_CAMERA_MOVE;
                data[0] = (byte)x;
                data[1] = (byte)y;
                setData(data);
                break;
        }

    }
    public void setPacketType(byte packetType) {
        this.packetType = packetType;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte getPacketType() {
        return packetType;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] toByteArray() {
        if(this.packetType == Constants.PACKET_INVALID ){
            Log.e(TAG, "BT Packet is invalid for Byte Array conversion");
            return null;
        }
        byte[] output = null;
        output = new byte[headerOffset + this.data.length];
        output[0] = this.packetType;
        for(int i = headerOffset; i<output.length; i++)
        {
            output[i] = this.data[i-headerOffset];
        }
        return output;
    }


}
