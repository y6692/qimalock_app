package com.qimalocl.manage.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.fastjson.JSON;
import com.qimalocl.manage.core.common.AppManager;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;

import org.apache.http.Header;

import java.util.Set;

import butterknife.ButterKnife;

public class BaseActivity extends BaseFragmentActivity {

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		BaseApplication.context = context;
//		ButterKnife.bind(this);

//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

	//字体适配解决方案
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.fontScale != 1)//非默认值
			getResources();

		Log.e("ba===1", "===");

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

//		Log.e("ba===2", res+"==="+res.getConfiguration().fontScale);

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
