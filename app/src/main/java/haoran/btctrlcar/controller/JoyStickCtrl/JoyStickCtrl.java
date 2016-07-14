package haoran.btctrlcar.controller.JoyStickCtrl;

import haoran.btctrlcar.MainActivity;
import haoran.btctrlcar.controller.mMessageHandler;

/**
 * Created by Haoran on 3/15/2015.
 */

public class JoyStickCtrl {
    //global message handler from mainctrl, works for communication between this task and mainctrl
    private mMessageHandler mMessageHandler = null;
    public JoyStickCtrl(mMessageHandler messageHandler){
        mMessageHandler = messageHandler;
    }

    public void moveNotify(float x, float y){
        mMessageHandler.onMoveNotify(x,y);
    }
}
