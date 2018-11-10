package com.qimalocl.manage.base;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.qimalocl.manage.core.common.AppManager;

import org.apache.http.Header;

import java.util.Set;

public class BaseFragmentActivity extends FragmentActivity {

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

	private TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		// 添加Activity到堆栈
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
		// 结束Activity从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

	public void finishMine() {
		AppManager.getAppManager().finishActivity(this);
	}
}
