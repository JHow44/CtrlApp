package haoran.btctrlcar.controller.CameraStreamCtrl;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;

/**
 * Created by Administrator on 2015/10/5.
 */
public class MjpegInputStreamThread extends Thread{

    private String url;
    private HttpResponse httpresp;
    public MjpegInputStreamThread(String req_url){
        url = req_url;
    }

    public HttpResponse getHttpresp() {
        return httpresp;
    }

    public void run() {
        try
        {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            URI uri = URI.create(url);
            HttpUriRequest req = new HttpGet(uri);
            httpresp = httpclient.execute(req);

        }
        catch(Exception e)
        {
            System.out.println("<-------Exception------->");
            e.printStackTrace();
        }
    }
}
