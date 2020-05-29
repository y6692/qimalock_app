
package com.qimalocl.manage.swipebacklayout.app;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.qimalocl.manage.base.BaseFragmentActivity;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.swipebacklayout.SwipeBackLayout;
import com.sunshine.blelibrary.config.Config;


public class SwipeBackActivity extends BaseFragmentActivity implements SwipeBackActivityBase {

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

	private static final int VIBRATE_DURATION = 10;
	private SwipeBackActivityHelper mHelper;
	private SwipeBackLayout mSwipeBackLayout;

//	protected LoadingDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
//		mHelper = new SwipeBackActivityHelper(this);
//		mHelper.onActivityCreate();
//		// 修改状态栏颜色，4.4+生效
//		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//			// 透明状态栏
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			// 透明导航栏
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}
//
//		mSwipeBackLayout = getSwipeBackLayout();
//		mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
//
//		getSwipeBackLayout().addSwipeListener(new SwipeBackLayout.SwipeListener() {
//			@Override
//			public void onScrollStateChange(int state, float scrollPercent) {
//
//			}
//
//			@Override
//			public void onEdgeTouch(int edgeFlag) {
//				vibrate(VIBRATE_DURATION);
//			}
//
//			@Override
//			public void onScrollOverThreshold() {
//				vibrate(VIBRATE_DURATION);
//			}
//		});
	}

	public static byte[] hexStringToByteArray(String str) {
		if(str == null || str.trim().equals("")) {
			return new byte[0];
		}

		byte[] bytes = new byte[str.length() / 2];
		for(int i = 0; i < str.length() / 2; i++) {
			String subStr = str.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(subStr, 16);
		}

		Log.e("StringToByte===1", bytes+"==="+bytes[0]);

//		Config.key = bytes;

		return bytes;
	}

	public void oncall(){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
//		mHelper.onPostCreate();
	}

	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v == null && mHelper != null)
			return mHelper.findViewById(id);
		return v;
	}

	@Override
	public SwipeBackLayout getSwipeBackLayout() {
		return mHelper.getSwipeBackLayout();
	}

	@Override
	public void setSwipeBackEnable(boolean enable) {
		getSwipeBackLayout().setEnableGesture(enable);
	}

	@Override
	public void scrollToFinishActivity() {
		
		finishMine();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		AppManager.getAppManager().finishActivity(this);
		finishMine();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.e("sba===onTouchEvent", "==="+event.getAction());

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			} catch (Exception e) {
			}
		}
		return super.onTouchEvent(event);
	}

	private void vibrate(long duration) {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 0, duration };
		vibrator.vibrate(pattern, -1);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		RefreshLogin();
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

	public void onFailureCommon(final String title, final String s) {
		m_myHandler.post(new Runnable() {
			@Override
			public void run() {
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}

				Log.e("onFailureCommon===2", title+"==="+s);

				UIHelper.ToastError(context, s);
			}
		});
	}

//	// 用户已经登录过没有退出刷新登录
//	public void RefreshLogin() {
//		String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
//		String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
//		if (access_token == null || "".equals(access_token) || uid == null || "".equals(uid)) {
//			setAlias("");
//		} else {
//			RequestParams params = new RequestParams();
//			params.add("uid", uid);
//			Log.e("Test", access_token);
//			params.add("access_token", access_token);
//			HttpHelper.post(AppManager.getAppManager().currentActivity(), Urls.accesslogin, params,
//					new TextHttpResponseHandler() {
//						@Override
//						public void onSuccess(int statusCode, Header[] headers, String responseString) {
//							try {
//								ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//								if (result.getFlag().equals("Success")) {
//									UserMsgBean bean = JSON.parseObject(result.getData(), UserMsgBean.class);
//									// 极光标记别名
//									setAlias(bean.getUid());
//									SharedPreferencesUrls.getInstance().putString("uid", bean.getUid());
//									SharedPreferencesUrls.getInstance().putString("access_token",
//											bean.getAccess_token());
//									SharedPreferencesUrls.getInstance().putString("nickname", bean.getNickname());
//									SharedPreferencesUrls.getInstance().putString("realname", bean.getRealname());
//									SharedPreferencesUrls.getInstance().putString("sex", bean.getSex());
//									SharedPreferencesUrls.getInstance().putString("headimg", bean.getHeadimg());
//									SharedPreferencesUrls.getInstance().putString("points", bean.getPoints());
//									SharedPreferencesUrls.getInstance().putString("money", bean.getMoney());
//									SharedPreferencesUrls.getInstance().putString("bikenum", bean.getBikenum());
//
//									SharedPreferencesUrls.getInstance().putString("iscert", bean.getIscert());
//								} else {
//									setAlias("");
//									SharedPreferencesUrls.getInstance().putString("uid", "");
//									SharedPreferencesUrls.getInstance().putString("access_token","");
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//
//						}
//
//						@Override
//						public void onFailure(int statusCode, Header[] headers, String responseString,
//											  Throwable throwable) {
//						}
//					});
//
//		}
//	}

}
