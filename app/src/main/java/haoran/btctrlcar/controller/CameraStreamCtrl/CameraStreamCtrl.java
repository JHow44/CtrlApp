package haoran.btctrlcar.controller.CameraStreamCtrl;

import org.apache.http.HttpResponse;

import haoran.btctrlcar.controller.mMessageHandler;

/**
 * Created by Administrator on 2015/10/11.
 */
public class CameraStreamCtrl {
    //global message handler from mainctrl, works for communication between this task and mainctrl
    private mMessageHandler mMessageHandler = null;

    public CameraStreamCtrl(mMessageHandler messageHandler){
        mMessageHandler = messageHandler;
    }
    public MjpegInputStream readStream(String videoUrl){
        HttpResponse httpresp = null;
        MjpegInputStream source = null;
        MjpegInputStreamThread mjpegInputStreamThread = new MjpegInputStreamThread(videoUrl);
        if (mjpegInputStreamThread != null)
        {
            mjpegInputStreamThread.start();
            try{
                mjpegInputStreamThread.join();
                httpresp= mjpegInputStreamThread.getHttpresp();
            }
            catch(Exception e){
                System.out.println("<-------Exception------->");
                e.printStackTrace();
                return null;
            }
        }
        if(httpresp != null) {
            try {
                source = new MjpegInputStream(httpresp.getEntity().getContent());
            }
            catch(Exception e){
                System.out.println("<-------Exception------->");
                e.printStackTrace();
                return null;
            }
        }
        return source;
    }
}
