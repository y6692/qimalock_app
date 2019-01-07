package com.qimalocl.manage.base;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
	
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
	}
}
