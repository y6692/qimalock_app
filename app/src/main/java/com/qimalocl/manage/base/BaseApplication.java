package com.qimalocl.manage.base;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.support.multidex.MultiDex;

import com.fitsleep.sunshinelibrary.utils.Logger;
import com.fitsleep.sunshinelibrary.utils.ToastUtils;
import com.sunshine.blelibrary.inter.IBLE;
import com.sunshine.blelibrary.service.BLEService;

import java.io.File;

/**
 * 自定义Application
 * 
 * @author wutao
 *
 */
public class BaseApplication extends Application {

	private static BaseApplication app;
	private BLEService mBleService;

	private PackageInfo packageInfo;

	public BaseApplication() {
		app = this;
	}

	public static synchronized BaseApplication getInstance() {
		if (app == null) {
			app = new BaseApplication();
		}
		return app;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ToastUtils.init(this);
		initBle();
	}

	// 注册App异常崩溃处理器
	private void registerUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

	private void initBle() {
		Intent intent = new Intent(this, BLEService.class);
		boolean bindService = bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
		if (bindService){
		}
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			ToastUtils.showMessage("不支持BLE");
			return;
		}
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter ==null){
			ToastUtils.showMessage("不支持BLE");
			return;
		}
		if (!mBluetoothAdapter.isEnabled()){
			mBluetoothAdapter.enable();
		}
	}

	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBleService = ((BLEService.LocalBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBleService = null;
		}
	};

	public IBLE getIBLE(){
		return mBleService.getIBLE();
	}
}
