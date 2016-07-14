package haoran.btctrlcar.controller;

/**
 * Created by Administrator on 2015/10/12.
 */
public interface mMessageHandler {
    //req from JoyStickCtrl, when user move the joystick to move the robot
    public void onMoveNotify(float x, float y);
}
