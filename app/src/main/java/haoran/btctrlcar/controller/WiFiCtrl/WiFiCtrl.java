package haoran.btctrlcar.controller.WiFiCtrl;
import haoran.btctrlcar.MainActivity;
import haoran.btctrlcar.common.Constants;
import haoran.btctrlcar.common.Packet;
import haoran.btctrlcar.controller.mMessageHandler;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by Administrator on 2015/10/11.
 */
public class WiFiCtrl {
    private static final String TAG = "WiFiCtrl";
    private Constants.wifictrl_status curr_status;
    private Socket socket;
    private static int SERVERPORT = 5000;
    private TextView serverString;
//    private NotificationHandler notificationHandler = null;


    //The dynamic allocated IP address of the car(server)
    private String SERVER_IP = "192.168.1.140";
    private boolean clientRun = false;
    ConnectThread connectThread;
    ConnectedThread connectedThread;
    //global message handler from mainctrl, works for communication between this task and mainctrl
    private mMessageHandler mMessageHandler = null;
    // message from the server
    private String mServerMessage;
    // used to send messages
    private DataOutputStream dOut;
    // used to read messages from the server
//    private DataInputStream dIn;
    private BufferedInputStream dIn;
//    private ObjectInputStream dIn;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public WiFiCtrl(mMessageHandler messageHandler) {
//        notificationHandler = nHandler;
        mMessageHandler = messageHandler;
        curr_status = Constants.wifictrl_status.INITIALIZED;
    }

    public String getSERVER_IP() {
        return SERVER_IP;
    }

    public boolean connect() {
        connectThread = new ConnectThread();
        connectThread.start();
        if (connectThread == null) {
            curr_status = Constants.wifictrl_status.CONNECTION_LOST;
            return false;
        }
        try{
            connectThread.join();
        }
        catch (Exception e) {
            Log.e(TAG, "Exception during connect", e);
        }
        if (dOut == null || dIn == null){
            return false;
        }
        curr_status = Constants.wifictrl_status.CONNECTED;
        connectedThread = new ConnectedThread();
        connectedThread.start();
        Log.v("Notice", "connectedThread just started");
        return true;
    }

//    static int count = 0;
    public boolean send(Packet packet) {
        byte[] serializedPacket = new byte[Constants.PACKET_SIZE];
        serializedPacket = packet.toByteArray();
        if (getCurr_status() != Constants.wifictrl_status.CONNECTED
                || connectedThread == null) {
            return false;
        }
        connectedThread.send(serializedPacket);

        return true;
    }

    public boolean recv(String str){
        if (getCurr_status() != Constants.wifictrl_status.CONNECTED
                || connectedThread == null) {
            return false;
        }
        connectedThread.recv(str);

        return true;
    }

    public Constants.wifictrl_status getCurr_status() {
        return curr_status;
    }

    class ConnectThread extends Thread implements Runnable {

        @Override
        public void run() {
            clientRun = true;
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                Log.i(TAG, "TCP Connecting...");
                try{
                    socket = new Socket(serverAddr, SERVERPORT);
                }
                catch (Exception e){
                    e.printStackTrace();
                    socket.close();
                    return;
                }

                dIn = new BufferedInputStream(socket.getInputStream());
//                dIn = new ObjectInputStream(socket.getInputStream());
                dOut= new DataOutputStream(socket.getOutputStream());


            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    class ConnectedThread extends Thread implements Runnable {
        public void send(byte[] message) {
            try {
                if (dOut != null) {
                    //////dOut.writeInt(message.length);
                    dOut.write(message);
                    Log.d(TAG, "Sending message (length is " + message.length  +"): " + Arrays.toString(message));
                } else {
                    clientRun = false;
                    Log.i(TAG, "dOut is null, sendThread is closed now");
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void recv(String str){
            byte[] mServerMessage = new byte[512];
            int bytesRead = 0;
            try {
                if (dIn != null) {
                    //////dOut.writeInt(message.length);
                    bytesRead = dIn.read(mServerMessage);
                    str = new String(mServerMessage, 0, bytesRead);
                    Log.d(TAG, "Sending message (length is " + bytesRead  +"): " +str);
                } else {
                    clientRun = false;
                    Log.i(TAG, "dOut is null, sendThread is closed now");
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception during write", e);
            }
        }


        @Override
        public void run() {

            Log.v("Client stat", "Client is running");
            try {
                while (clientRun) {
//                    Log.v("Notice", "Just entered the loop");
                    byte[] buffer = new byte[50];
                    int bytesRead = 0;
                    bytesRead = dIn.read(buffer);
//                    if((bytesRead = dIn.read(buffer)) != -1){
//                        String str = new String(buffer, 0, bytesRead);
//                        Log.v("RESPONSE FROM SERVER", "S: Received Message: '" + "AHAHAHHAHA" + str);
//                    }

                    if (bytesRead != -1) {
                        byte[] mServerMessage = new byte[512];
                        try {
                            Log.v("Notice", "Just wait here");
                            String str = new String(buffer, 0, bytesRead);
                            Log.v("RESPONSE FROM SERVER", "S: Received Message: '" + str);
                            if(str.length() > 13){
                                MainActivity.motion = true;
                            }else {
                                MainActivity.motion = false;
                            }
//                            serverString.setText("" + str);


                        } catch (Exception e) {
                            Log.e(TAG, "Error: socket closed in ConnectedThread");
                            clientRun = false;
                            socket.close();
                            e.printStackTrace();
                        }
//                        Log.v("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");
                    }
                }


                Log.i(TAG, "socket closed in ConnectedThread");
                clientRun = false;
                socket.close();

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        //run() above



    }

}
