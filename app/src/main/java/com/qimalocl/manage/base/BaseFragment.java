package com.qimalocl.manage.base;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.qimalocl.manage.core.common.AppManager;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.widget.LoadingDialog;

public class BaseFragment extends Fragment {
	protected LoadingDialog loadingDialog;
	protected Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		loadingDialog = new LoadingDialog(getActivity());
//		loadingDialog.setMessageText("数据加载...");
//		imageManager = new ImageManager(getActivity());
//		autoHelper = new LibraryHelper(getActivity());

		context = getActivity();
		BaseApplication.context = context;
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
