package haoran.btctrlcar;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import haoran.btctrlcar.controller.MainCtrl;
import haoran.btctrlcar.controller.mMessageHandler;
import haoran.btctrlcar.fragment.MjpegView;
import haoran.btctrlcar.fragment.MyJoyStickView;

public class MainActivity extends ActionBarActivity{
    public static boolean motion = false;

    private static final String TAG ="MainCtrl";
    public static MainCtrl mainCtrl;
    private MjpegView mjpegView;
    private MyJoyStickView myJoyStickView;
    private MyJoyStickView myCamStickView;  // for camera
    final String PREFS_NAME = "MyPrefsFile";
    public ImageView Motion;


    public NotificationCompat.Builder nBuilder;
    public Notification notification;
    public NotificationManager nm;
    Bitmap alert;
    Thread th;

    //SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
    //settings Options:
    //first_launch      first time launch the app or not
    //car_ip_address    IP address of the car
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean result;
        super.onCreate(savedInstanceState);
        mainCtrl = new MainCtrl();
        setContentView(R.layout.activity_main);
        mjpegView = (MjpegView) findViewById(R.id.mjpegViewID);
        Motion = (ImageView) findViewById(R.id.imageView);
        alert = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_alert);

        nBuilder = new NotificationCompat.Builder(getApplicationContext());
        nBuilder.setContentTitle("Waring!");
        nBuilder.setContentText("Possible intruder!");
        nBuilder.setSmallIcon(R.mipmap.ic_alert_small);
        nBuilder.setLargeIcon(alert);
        nBuilder.setColor(Color.argb(255,255,0,0));
        notification = nBuilder.build();
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Log.v("tag", "all good");
        if(mjpegView == null){
        finish();
        }

        Motion.setImageResource(R.mipmap.ic_ball);

        result = mainCtrl.connect();
        if(!result)
        {
            Log.e(TAG, "mainCtrl TCP connection failure!");
            //more work: Handling connection failure
            //finish();
        }
        //mainCtrl.authorizeController(myJoyStickView);
        result = mainCtrl.cameraStreamOn(mjpegView);
        if(!result)
        {
            Log.e(TAG, "mainCtrl cameraStreamOn failure!");
            //more work: Handling connection failure
            //finish();
        }
        Log.v("NOTICE", "This is where is broken");
//        th.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        mjpegView.stopPlayback();
    }


    public void Start(View view){

        th =new Thread(new Runnable() {
            public void run()
            {
                int i = 0;
                while(true){
                    if(motion){
                        i++;
                        if(i > 1) {
                            Log.v("Tag", "Motion detected try to notify" + motion);
                            nm.notify(1, notification);
                        }
                        if(i > 10){
                            i = 8;
                        }
                    }
                    motion = false;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();
    }

//    public class motionCheck implements Runnable{
//        public NotificationCompat.Builder nBuilder;
//        public Notification notification;
//        public NotificationManager nm;
//
//        public motionCheck(){
//            nBuilder = new NotificationCompat.Builder(getApplicationContext());
//            nBuilder.setContentTitle("Waring!");
//            nBuilder.setContentText("Possible intruder!");
//            nBuilder.setSmallIcon(R.mipmap.ic_alert);
//
//            notification = nBuilder.build();
//            nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//
//        }
//        @Override
//        public void run() {
//            while(true){
//                if(motion){
//                    nm.notify(1, notification);
//                    Motion.setImageResource(R.mipmap.ic_alert);
//                }
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }


}
