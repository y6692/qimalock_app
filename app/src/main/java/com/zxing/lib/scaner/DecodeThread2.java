package com.zxing.lib.scaner;

import android.os.Handler;
import android.os.Looper;

import com.qimalocl.manage.activity.MainActivity;
import com.qimalocl.manage.fragment.MaintenanceFragment;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import java.util.concurrent.CountDownLatch;

/**
 * @author Vondear
 * 描述: 解码线程
 */
final class DecodeThread2 extends Thread {

    private final CountDownLatch handlerInitLatch;
	MaintenanceFragment activity;
    private Handler handler;

	DecodeThread2(MaintenanceFragment activity) {
		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);
	}

	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new DecodeHandler2(activity);
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
