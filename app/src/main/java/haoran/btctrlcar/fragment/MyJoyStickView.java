package haoran.btctrlcar.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

import haoran.btctrlcar.MainActivity;
import haoran.btctrlcar.controller.JoyStickCtrl.JoyStickCtrl;

public class MyJoyStickView extends SurfaceView implements Callback, Runnable {
	private static final String TAG ="MyJoyStickView";
    private JoyStickCtrl jsctrl;
	private Thread th;
	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint paint;
	private boolean flag;
	private int width;
	private int height;
    private static float SMALL_ROCKER_DEFAULT_X = 200;
    private static float SMALL_ROCKER_DEFAULT_Y = 200;
    //More:Joystick's location should be align with screen.
	private float RockerCircleX = SMALL_ROCKER_DEFAULT_X;
	private float RockerCircleY = SMALL_ROCKER_DEFAULT_Y;
	private int RockerCircleR = 200;
	private float SmallRockerCircleR = 80;
	private float SmallRockerCircleX = SMALL_ROCKER_DEFAULT_X;
	private float SmallRockerCircleY = SMALL_ROCKER_DEFAULT_Y;
	private static float RockerCircleOriginalX = SMALL_ROCKER_DEFAULT_X;
	private static float RockerCircleOriginalY = SMALL_ROCKER_DEFAULT_Y;
	private static int count = 0;


	public MyJoyStickView(Context context, AttributeSet attrs) {
		super(context);
		//Log.v("Joystick", "MySurfaceView");
		this.setKeepScreenOn(true);
		//this.setZOrderOnTop(true);
        holder = this.getHolder();
		holder.setFormat(PixelFormat.TRANSLUCENT);
        holder.addCallback(this);
		paint = new Paint();
		paint.setAntiAlias(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		//[?] Here is a module design problem:
		//Every subctrl(such as jsctrl, wireless) should be managed by mainctrl
		//And View should be designed independently from the corresponding ctrl(controller)
		//MyJoyStickView is an exception, it owns the jsctrl by directly use MainActivity.mainctrl
		//This is not recommended because it's hard to trace.  (Reason is: findViewById for MyJoyStickView will return null...)
		if(!this.isInEditMode()){
			if(this.count == 0){
				jsctrl = MainActivity.mainCtrl.joyStickCtrl;
			}else{
				jsctrl = MainActivity.mainCtrl.camStickCtrl;
			}
			//add this check only for rendering problem in activity_main.xml
		}
		count++;
		Log.v("JoyStickView Count:", "is " + count);
	}

	public void setControler(JoyStickCtrl mjsctrl){
		jsctrl = mjsctrl;
	}
	public void surfaceCreated(SurfaceHolder holder) {
		th = new Thread(this);
		flag = true;
		th.start();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width=getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
		height=getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec);
		setMeasuredDimension(width, height);
		int screenWidth = getWidth();
		int screenHeight = getHeight();
		SMALL_ROCKER_DEFAULT_X = (float)screenWidth/2;
		SMALL_ROCKER_DEFAULT_Y =  screenHeight - RockerCircleR;
		SmallRockerCircleX = SMALL_ROCKER_DEFAULT_X;
		SmallRockerCircleY = SMALL_ROCKER_DEFAULT_Y;
		RockerCircleX = SMALL_ROCKER_DEFAULT_X;
		RockerCircleY = SMALL_ROCKER_DEFAULT_Y;
		RockerCircleOriginalX = SMALL_ROCKER_DEFAULT_X;
		RockerCircleOriginalY = SMALL_ROCKER_DEFAULT_Y;
	}
	/***
	 *
	 */
	public double getRad(float px1, float py1, float px2, float py2) {
		float x = px2 - px1;
		float y = py1 - py2;
		float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		float cosAngle = x / xie;
		float rad = (float) Math.acos(cosAngle);
		if (py2 < py1) {
			rad = -rad;
		}
		return rad;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				// When ACTION_DOWN the big circle should also move
				RockerCircleX = event.getX();
				RockerCircleY = event.getY();

				if (Math.sqrt(Math.pow((RockerCircleX - (int) event.getX()), 2) + Math.pow((RockerCircleY - (int) event.getY()), 2)) >= RockerCircleR) {
					double tempRad = getRad(RockerCircleX, RockerCircleY, event.getX(), event.getY());
					getXY(RockerCircleX, RockerCircleY, RockerCircleR, tempRad);
				}
				else {
					SmallRockerCircleX = (int) event.getX();
					SmallRockerCircleY = (int) event.getY();
				}
				jsctrl.moveNotify(
						(SmallRockerCircleX - RockerCircleX)*100/RockerCircleR,
						(SmallRockerCircleY - RockerCircleY)*100/RockerCircleR );
				break;
			case MotionEvent.ACTION_MOVE:
				if (Math.sqrt(Math.pow((RockerCircleX - (int) event.getX()), 2) + Math.pow((RockerCircleY - (int) event.getY()), 2)) >= RockerCircleR) {
					double tempRad = getRad(RockerCircleX, RockerCircleY, event.getX(), event.getY());
					getXY(RockerCircleX, RockerCircleY, RockerCircleR, tempRad);
				}
				else {
					SmallRockerCircleX = (int) event.getX();
					SmallRockerCircleY = (int) event.getY();
				}
				//We use the percentage of movement range from 0% to 100%), and send the parameter to JoyStickCtrl(MainCtrl)
				jsctrl.moveNotify(
						(SmallRockerCircleX - RockerCircleX)*100/RockerCircleR,
						(SmallRockerCircleY - RockerCircleY)*100/RockerCircleR );
				break;
			case MotionEvent.ACTION_UP:
				//Log.v("Notice: ", "Touch event just ended");
				jsctrl.moveNotify(0,0);
				SmallRockerCircleX = SMALL_ROCKER_DEFAULT_X;
				SmallRockerCircleY = SMALL_ROCKER_DEFAULT_Y;
				RockerCircleX = RockerCircleOriginalX;
				RockerCircleY = RockerCircleOriginalY;
				break;
		}
		return true;
	}

	/**
	 * 
	 * @param R
	 *            The big circle's radius
	 * @param centerX
	 *            The big circle's x axis
	 * @param centerY
	 *             The big circle's y axis
	 * @param rad
	 *             The angle of between the line that connects with touch point and center point and the horizontal line
	 */
	public void getXY(float centerX, float centerY, float R, double rad) {
		SmallRockerCircleX = (float) (R * Math.cos(rad)) + centerX;
		SmallRockerCircleY = (float) (R * Math.sin(rad)) + centerY;
	}

	public void draw() {
		try {
			canvas = holder.lockCanvas();
//			Log.v("Joystick", "Drawing");
            //[?]More:Joystick's color and background picture
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			paint.setColor(0x7f0ff000);
			canvas.drawCircle(RockerCircleX, RockerCircleY, RockerCircleR, paint);
			paint.setColor(0x70fffd00);
			canvas.drawCircle(SmallRockerCircleX, SmallRockerCircleY, SmallRockerCircleR, paint);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
			} catch (Exception e2) {

			}
		}
	}

	public void run() {
		// TODO Auto-generated method stub
		while (flag) {
			draw();
			try {
				Thread.sleep(10);
			} catch (Exception ex) {
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v("joystick", "surfaceChanged");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
		Log.v("Joystick", "surfaceDestroyed");
	}
}