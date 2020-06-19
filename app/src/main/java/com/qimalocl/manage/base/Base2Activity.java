package com.qimalocl.manage.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.MainActivity;
import com.qimalocl.manage.core.common.AppManager;
import com.qimalocl.manage.model.AppStatus;
import com.qimalocl.manage.utils.LogUtil;

public class Base2Activity extends BaseFragmentActivity {

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

	private View contentView;
	RelativeLayout rl_base;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		LogUtil.e("ba2===onCreate", "==="+AppStatusManager.getInstance().getAppStatus());

//		//判断app状态
//		if (AppStatusManager.getInstance().getAppStatus() == AppStatus.STATUS_RECYCLE){
//			//被回收，跳转到启动页面
//			Intent intent = new Intent(this, MainActivity.class);
//			startActivity(intent);
//			finish();
//			return;
//		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		BaseApplication.context = context;
//		ButterKnife.bind(this);

//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


		setContentView(R.layout.activity_base);
		rl_base = findViewById(R.id.rl_base);

		AppManager.getAppManager().addActivity(this);
		// 修改状态栏颜色，4.4+生效
//		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//			// 透明状态栏
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			// 透明导航栏
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}

//		Configuration cfg = getResources().getConfiguration();
//		cfg.fontScale=1;
	}

	public void setContentLayout(int resId) {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(resId, null);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
		contentView.setLayoutParams(layoutParams);
		contentView.setBackgroundDrawable(null);

		rl_base.addView(contentView);

//		contentView.add
//
//		if (null != ly_content) {
//			ly_content.addView(contentView);
//		}

	}

	//字体适配解决方案
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.fontScale != 1)//非默认值
			getResources();

		LogUtil.e("ba===1", "===");

		super.onConfigurationChanged(newConfig);
	}

	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		if (res.getConfiguration().fontScale != 1) {//非默认值
			Configuration newConfig = new Configuration();
			newConfig.setToDefaults();//设置默认
			res.updateConfiguration(newConfig, res.getDisplayMetrics());
		}

//		LogUtil.e("ba===2", res+"==="+res.getConfiguration().fontScale);

		return res;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			} catch (Exception e) {
			}
		}
		return super.onTouchEvent(event);
	}

	public void gotoAct(Class<?> clazz) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
	}

	public void finishMine() {
		AppManager.getAppManager().finishActivity(this);
	}
}
