package com.qimalocl.manage.base;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.qimalocl.manage.core.common.AppManager;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.utils.LogUtil;

public class BaseFragment extends Fragment {
	protected LoadingDialog loadingDialog;
	protected Context context;

	private String STATE_SAVE_IS_HIDDEN = "dasdasdasd";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LogUtil.e("bf===onC", savedInstanceState+"==="+isHidden());

//		if (savedInstanceState != null) {
//			boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
//
//			LogUtil.e("bf===onC_1", isSupportHidden+"==="+isHidden());
//
//			FragmentTransaction ft = getFragmentManager().beginTransaction();
//
//			LogUtil.e("bf===onC_2", isSupportHidden+"==="+isHidden());
//
//			if (isSupportHidden) {
//				ft.hide(this);
//			} else {
//				ft.show(this);
//			}
//			ft.commit();
//		}

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		loadingDialog = new LoadingDialog(getActivity());
//		loadingDialog.setMessageText("数据加载...");
//		imageManager = new ImageManager(getActivity());
//		autoHelper = new LibraryHelper(getActivity());

		LogUtil.e("bf===onAC", savedInstanceState+"==="+isHidden());

		context = getActivity();
		BaseApplication.context = context;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		LogUtil.e("bf===onSIS", outState+"==="+isHidden());

		outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
		super.onSaveInstanceState(outState);
		;


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

		LogUtil.e("StringToByte===1", bytes+"==="+bytes[0]);


		return bytes;
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

	Handler m_myHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message mes) {
			switch (mes.what) {
				case 0:
					break;
				default:
					break;
			}
			return false;
		}
	});

	public void finishMine() {
		AppManager.getAppManager().finishActivity(getActivity());
	}
}
