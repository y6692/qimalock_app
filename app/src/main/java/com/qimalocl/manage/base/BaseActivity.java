package com.qimalocl.manage.base;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.BindSchoolActivity;
import com.qimalocl.manage.activity.DeviceSelectActivity;
import com.qimalocl.manage.activity.GetDotActivity;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.activity.MainActivity;
import com.qimalocl.manage.activity.MerchantAddressMapActivity;
import com.qimalocl.manage.activity.TestXiaoanActivity;
import com.qimalocl.manage.core.common.AppManager;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.MyScrollView;
import com.qimalocl.manage.fragment.ScanFragment;
import com.qimalocl.manage.fragment.ScanFragmentPermissionsDispatcher;
import com.qimalocl.manage.full.SplashActivity;
import com.qimalocl.manage.model.AppStatus;
import com.qimalocl.manage.utils.LogUtil;
import com.qimalocl.manage.utils.ToastUtil;
import com.xiaoantech.sdk.XiaoanBleApiClient;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import org.apache.http.Header;

import java.util.Set;

import butterknife.ButterKnife;
import okhttp3.Response;

public class BaseActivity extends BaseFragmentActivity  implements View.OnClickListener{

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

	private View contentView;
	RelativeLayout rl_base;

	private LinearLayout ll_test;
	private RelativeLayout rl_test;
	public static MyScrollView sv_test;
	public static TextView tv_test0;
	public static TextView tv_test;
	private Button btn_clear;
	private Button btn_log;
	public static String testLog = "";
	private boolean isLog;
	private boolean isNetSuc;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		LogUtil.e("ba===onCreate", "==="+AppStatusManager.getInstance().getAppStatus());

		//判断app状态
		if (AppStatusManager.getInstance().getAppStatus() == AppStatus.STATUS_RECYCLE){
			//被回收，跳转到启动页面
			Intent intent = new Intent(this, SplashActivity.class);
			startActivity(intent);
			finish();
			return;
		}

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

	@Override
	protected void onResume() {
		super.onResume();

		LogUtil.e("ba===onResume", "==="+AppStatusManager.getInstance().getAppStatus());

		if(!"https://newmapi.7mate.cn/api".equals(Urls.host2)){

			if(tv_test!=null) return;

			LogUtil.e("ba===onResume_1", "==="+AppStatusManager.getInstance().getAppStatus());

			ll_test = findViewById(R.id.ll_test);
			rl_test = findViewById(R.id.rl_test);
			sv_test = findViewById(R.id.sv_test);
			tv_test0 = findViewById(R.id.tv_test0);
			tv_test = findViewById(R.id.tv_test);
			btn_clear = findViewById(R.id.btn_clear);
			btn_log = findViewById(R.id.btn_log);

			btn_clear.setOnClickListener(this);
			btn_log.setOnClickListener(this);

			btn_clear.setVisibility(View.VISIBLE);
			btn_log.setVisibility(View.VISIBLE);
			ll_test.setVisibility(View.VISIBLE);
			rl_test.setVisibility(View.VISIBLE);
			sv_test.setVisibility(View.GONE);

//          testLog+="log:\n";
//          tv_test.setText(testLog);
//			LogUtil.e("log", "");

		}

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){

			case R.id.btn_clear:
				testLog="";
				tv_test.setText("");
				break;

			case R.id.btn_log:

				if(!isLog){
					isLog = true;
					sv_test.setVisibility(View.VISIBLE);

					scrollToBottom(sv_test, tv_test);
				}else{
					isLog = false;
					sv_test.setVisibility(View.GONE);
				}

				break;

			default:
				break;
		}

	}

	public static void scrollToBottom(final View scroll, final View inner) {

		Handler mHandler = new Handler();

		mHandler.post(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}
				int offset = inner.getMeasuredHeight() - scroll.getHeight();
				if (offset < 0) {
					offset = 0;
				}

				scroll.scrollTo(0, offset);
			}
		});
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void setContentLayout(int resId) {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(resId, null);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
		contentView.setLayoutParams(layoutParams);
		contentView.setBackgroundDrawable(null);

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
