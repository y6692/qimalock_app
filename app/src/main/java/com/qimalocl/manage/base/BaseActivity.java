package com.qimalocl.manage.base;

import android.content.Intent;
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

public class BaseActivity extends FragmentActivity {

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		AppManager.getAppManager().addActivity(this);
		// 修改状态栏颜色，4.4+生效
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
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
