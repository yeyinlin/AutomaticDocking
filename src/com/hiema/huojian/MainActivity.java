package com.hiema.huojian;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private WindowManager wm;
	private ImageView view;
	private ActivityManager am;

	private WindowManager.LayoutParams params;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		view = new ImageView(this);
		view.setBackgroundResource(R.drawable.rocket);// 设置这个view视图的背景资源,这里设置的是一个动画。
		view.setOnTouchListener(new OnTouchListener() {

			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();// 得到第一按下的位置

					break;

				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();// 得到移动到的新位置
					int newY = (int) event.getRawY();

					int dx = newX - startX;// 得到位移了多少距离
					int dy = newY - startY;

					params.x += dx;// 重新赋值位移的位置
					params.y += dy;

					wm.updateViewLayout(view, params);// 更新界面

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();// 重新计算当前的位置次按下的位置
					break;

				case MotionEvent.ACTION_UP:

					if (params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth()) / 2) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								while (params.x != wm.getDefaultDisplay().getWidth() - view.getWidth()) {
									SystemClock.sleep(1);
									handler.post(new Runnable() {

										@Override
										public void run() {
											wm.updateViewLayout(view, params);// 更新界面
										}
									});
									params.x++;
								}
							}
						}).start();
					} else {
						new Thread(new Runnable() {
							@Override
							public void run() {
								while (params.x != 0) {
									SystemClock.sleep(1);
									handler.post(new Runnable() {

										@Override
										public void run() {
											wm.updateViewLayout(view, params);// 更新界面
										}
									});
									params.x--;
								}
							}
						}).start();
					}
					break;
				}
				return true;
			}
		});

		params = new LayoutParams();
		params.gravity = Gravity.LEFT + Gravity.TOP;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;// 半透明
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

		wm.addView(view, params);// 把这个土司添加到窗体

	}

	@Override
	public void onDestroy() {
		wm.removeView(view);// 当界面销毁就释放资源
		view = null;
		System.out.println("onDestroy");
		super.onDestroy();
	}
}
