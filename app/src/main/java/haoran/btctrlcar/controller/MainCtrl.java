package haoran.btctrlcar.controller;

import android.util.Log;

import haoran.btctrlcar.MainActivity;
import haoran.btctrlcar.common.Constants;
import haoran.btctrlcar.common.Packet;
import haoran.btctrlcar.controller.CameraStreamCtrl.CameraStreamCtrl;
import haoran.btctrlcar.controller.CameraStreamCtrl.MjpegInputStream;
import haoran.btctrlcar.controller.JoyStickCtrl.JoyStickCtrl;
import haoran.btctrlcar.controller.WiFiCtrl.WiFiCtrl;
import haoran.btctrlcar.fragment.MjpegView;

/**
 * Created by Haoran on 3/15/2015.
 */
public class MainCtrl {
    private static final String TAG ="MainCtrl";

    //The list of sub-ctrl managed by mainCtrl
    private WiFiCtrl wiFiCtrl;
    private CameraStreamCtrl cameraStreamCtrl;
    public JoyStickCtrl joyStickCtrl;
    public JoyStickCtrl camStickCtrl;

    //Message handler
    private mMessageHandler mMessageHandler = null;
    private mMessageHandler camMesgHandle = null;
//    private NotificationHandler notiHandler = null;

    //status of the sub-ctrls

    public MainCtrl(){
        initSetup();
    }

    public void initSetup(){
       // btctrl= new BtCtrl();
        //Message handler definition for handling message from other tasks
//        this.notiHandler = notificationHandler;
        mMessageHandler = new mMessageHandler() {
            @Override
            public void onMoveNotify(float x, float y) {
                carMovement(x, y);
            }
        };

        camMesgHandle = new mMessageHandler() {
            @Override
            public void onMoveNotify(float x, float y) {
                cameraMovement(x, y);
            }
        };


        wiFiCtrl = new WiFiCtrl(mMessageHandler);
        cameraStreamCtrl = new CameraStreamCtrl(mMessageHandler);
        joyStickCtrl = new JoyStickCtrl(mMessageHandler);
        camStickCtrl = new JoyStickCtrl(camMesgHandle);
    }

    public boolean connect(){
        boolean result = false;
        result = wiFiCtrl.connect();
        return result;
    }

    public void carMovement(float x, float y){
        Log.v("mainCtrl", "*** drive car in (" + x + "," + y + ") power ***");
        int powerX=(int)x;
        int powerY=(int) y;
        Packet movePacket = new Packet(powerX, powerY, 0);
        movePacket.setPacketType(Constants.PACKET_ENGINE_MOVE);
        wiFiCtrl.send(movePacket);
    }

    public void cameraMovement(float x, float y){
        Log.v("mainCtrl", "====== Camera move to (" + x + "," + y + ") direction ======");
        int camX=(int)x;
        int camY=(int)y;
        Packet camPacket = new Packet(camX, camY, 1);
        camPacket.setPacketType(Constants.PACKET_CAMERA_MOVE);
        wiFiCtrl.send(camPacket);
    }

    public boolean cameraStreamOn(MjpegView mjpegView){
        //String videoUrl="http://trackfield.webcam.oregonstate.edu/axis-cgi/mjpg/video.cgi";
        //String videoUrl="http://192.168.1.8:8080";
        //String videoUrl="http://192.168.2.3:9090?action=stream";
        String videoUrl="http://"+wiFiCtrl.getSERVER_IP()+":8080?action=stream";
        //[?get robot's camera status before read the stream]
        MjpegInputStream source = cameraStreamCtrl.readStream(videoUrl);
        if(source == null){
            return false;
        }
        mjpegView.setSource(source);
        mjpegView.setDisplayMode(MjpegView.SIZE_BEST_FIT);
        mjpegView.showFps(false);
        return true;
    }
}
