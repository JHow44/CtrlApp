package haoran.btctrlcar.controller.BtCtrl;

import android.util.Log;
import android.support.v4.app.Fragment;

import haoran.btctrlcar.common.Packet;
import haoran.btctrlcar.common.Constants;


/**
 * Created by Haoran on 3/15/2015.
 * BtCtrl: Bluetooth Ctrl
 * Handle the connection and data transmission of Bluetooth
 */
public class BtCtrl extends Fragment {
    private static final String TAG ="BtCtrl";
    private int packetCount=0;

    private BtConnect btConnect;
    private BtUtil btUtil;
    public BtCtrl(){
        btConnect = new BtConnect(getActivity());
        btUtil = new BtUtil();
    }

    public void connect(){
        System.out.println("*** Connected to Bluetooth car ***");
        btConnect.connect(null, true);
    }
    private void close(){
        System.out.println("*** Closed the Connection to Bluetooth car ***");
    }

    public void send(Packet btPacket){
        byte[] serializedPacket = new byte[Constants.PACKET_SIZE];
        serializedPacket = btPacket.toByteArray();
        if(serializedPacket == null)
        {
            Log.i(TAG,"BT Packet to Byte Array conversion failed");
            return;
        }
        btConnect.send(serializedPacket);
        packetCount++;
        //Log.v("BtCtrl", "*** write packet " + packetCount + " ***");
    }





}
