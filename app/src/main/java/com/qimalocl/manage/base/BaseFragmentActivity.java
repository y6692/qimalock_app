package com.qimalocl.manage.base;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.qimalocl.manage.core.common.AppManager;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.widget.LoadingDialog;

import org.apache.http.Header;

import java.lang.ref.WeakReference;
import java.util.Set;

public class BaseFragmentActivity extends FragmentActivity {
	protected Context context;
	protected LoadingDialog loadingDialog;

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

	private TelephonyManager tm;

	protected boolean isRefresh;

	protected Handler m_myHandler = new MainHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		context = this;

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

	public void onStartCommon(final String title) {
		m_myHandler.post(new Runnable() {
			@Override
			public void run() {
				if (loadingDialog != null && !loadingDialog.isShowing()) {
					loadingDialog.setTitle(title);
					loadingDialog.show();
				}
			}
		});

	}

	public void onFailureCommon(final String s) {
		m_myHandler.post(new Runnable() {
			@Override
			public void run() {
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				UIHelper.ToastError(context, s);
			}
		});

	}

	class MainHandler extends Handler {
		WeakReference<BaseFragmentActivity> softReference;

		public MainHandler(BaseFragmentActivity activity) {
			softReference = new WeakReference<BaseFragmentActivity>(activity);
		}

		@Override
		public void handleMessage(Message mes) {
			BaseFragmentActivity baseFragmentActivity = softReference.get();

			switch (mes.what) {
				case 0:
//					if (!BaseApplication.getInstance().getIBLE().isEnable()){
//
//						break;
//					}
//					BaseApplication.getInstance().getIBLE().connect(m_nowMac, baseFragmentActivity);
					break;
				case 1:

					break;
				case 2:
					break;
				case 3:
					break;
				case 9:
					break;
				case 0x99://搜索超时
//					BaseApplication.getInstance().getIBLE().connect(m_nowMac, baseFragmentActivity);
					break;
				default:
					break;
			}
//			return false;
		}
	}
}
