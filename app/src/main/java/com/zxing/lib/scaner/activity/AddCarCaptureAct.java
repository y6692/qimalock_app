package com.zxing.lib.scaner.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.qimalocl.manage.R;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.vondear.rxtools.RxAnimationTool;
import com.vondear.rxtools.RxBeepTool;
import com.vondear.rxtools.interfaces.OnRxScanerListener;
import com.zxing.lib.scaner.CameraManager;
import com.zxing.lib.scaner.CaptureActivityHandler;
import com.zxing.lib.scaner.decoding.InactivityTimer;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 二维码扫描
 */
@SuppressLint("NewApi")
public class AddCarCaptureAct extends SwipeBackActivity{

	private Context context;

	private Camera mCamera;
//	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private CameraManager mCameraManager;

	private InactivityTimer inactivityTimer;

	private SurfaceView scanPreview;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;

	private Rect mCropRect = null;
	private boolean barcodeScanned = false;
	private boolean previewing = true;
	private ImageScanner mImageScanner = null;

	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private TextView cancle;

	private TextView bikeNunBtn;
	private LinearLayout lightBtn;

	private boolean hasSurface;
	SurfaceHolder surfaceHolder;
	private int mCropWidth = 0;
	private int mCropHeight = 0;

	private CaptureActivityHandler handler;
	private static OnRxScanerListener mScanerListener;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner_location);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		context = this;

		findViewById();
		//权限初始化
		initPermission();
		//扫描动画初始化
		initScanerAnimation();

		CameraManager.init(context);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		mCameraManager = CameraManager.get();
	}

	private void initPermission() {
		//请求Camera权限 与 文件读写 权限
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
				ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
		}
	}

	private void initScanerAnimation() {
		ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
		RxAnimationTool.ScaleUpDowm(mQrLineView);
	}

	private void findViewById() {
		scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);
		bikeNunBtn = (TextView)findViewById(R.id.loca_show_btnBikeNum);
		lightBtn = (LinearLayout)findViewById(R.id.activity_qr_scan_lightBtn);
//		cancle = (TextView) findViewById(R.id.loca_show_btncancle);
//
//		cancle.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				scrollToFinishActivity();
//			}
//		});
		lightBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				light();
			}
		});

//		bikeNunBtn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//				String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//
//				if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
//					Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//					UIHelper.goToAct(context, LoginActivity.class);
//				}else {
//					//关闭相机
//					releaseCamera();
//					mCameraManager.closeDriver();
//
//					bikeNumEdit.setText("");
//
//					WindowManager windowManager = activity.getWindowManager();
//					Display display = windowManager.getDefaultDisplay();
//					WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//					lp.width = (int) (display.getWidth() * 0.8); 								// 设置宽度0.6
//					lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//					dialog.getWindow().setAttributes(lp);
//					dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
//					dialog.show();
//
//					InputMethodManager manager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
//					manager.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
//					manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//				}
//			}
//		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("surface===0", "==="+hasSurface);

		if (!hasSurface) {
			//Camera初始化
//            initCamera(surfaceHolder);
//            resetCamera(surfaceHolder);

			if(surfaceHolder==null){
				surfaceHolder = scanPreview.getHolder();

				surfaceHolder.addCallback(new SurfaceHolder.Callback() {
					@Override
					public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

					}

					@Override
					public void surfaceCreated(SurfaceHolder holder) {
						Log.e("surface===1", "==="+hasSurface);

						if (!hasSurface) {
							hasSurface = true;

							initCamera(holder);
						}
					}

					@Override
					public void surfaceDestroyed(SurfaceHolder holder) {
						Log.e("surface===2", "==="+hasSurface);

						hasSurface = false;

					}
				});
				surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}



		} else {
			initCamera(surfaceHolder);
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			mCameraManager = CameraManager.get();
			mCameraManager.openDriver(surfaceHolder);
			mCamera = mCameraManager.getCamera();
			Point point = mCameraManager.getCameraResolution();
			AtomicInteger width = new AtomicInteger(point.y);
			AtomicInteger height = new AtomicInteger(point.x);
			int cropWidth = scanCropView.getWidth() * width.get() / scanContainer.getWidth();
			int cropHeight = scanCropView.getHeight() * height.get() / scanContainer.getHeight();
//			setCropWidth(cropWidth);
//			setCropHeight(cropHeight);
		} catch (IOException | RuntimeException ioe) {
			return;
		}
		if (handler == null) {
//			handler = new CaptureActivityHandler(this);
		}
	}

	public void handleDecode(Result result) {
		if(!previewing) return;

		inactivityTimer.onActivity();
		//扫描成功之后的振动与声音提示
		RxBeepTool.playBeep(this, vibrate);

//        mCamera.setPreviewCallback(null);
//        mCamera.stopPreview();
//
//        releaseCamera();

		Log.e("===", "====");

		String result1 = result.getText();
		if (mScanerListener == null) {
			initDialogResult(result);
		} else {
			mScanerListener.onSuccess("From to Camera", result);
		}
	}

	private void initDialogResult(Result result) {
//		useBike(result.toString());
//		lockInfo(result.toString());
	}

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Size size = camera.getParameters().getPreviewSize();

			// 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
			byte[] rotatedData = new byte[data.length];
			for (int y = 0; y < size.height; y++) {
				for (int x = 0; x < size.width; x++)
					rotatedData[x * size.height + size.height - y - 1] = data[x + y * size.width];
			}

			// 宽高也要调整
			int tmp = size.width;
			size.width = size.height;
			size.height = tmp;

			initCrop();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(rotatedData);
			barcode.setCrop(mCropRect.left, mCropRect.top, mCropRect.width(), mCropRect.height());

			int result = mImageScanner.scanImage(barcode);
			String resultStr = null;

			if (result != 0) {
				SymbolSet syms = mImageScanner.getResults();
				for (Symbol sym : syms) {
					resultStr = sym.getData();
				}
			}

			if (!TextUtils.isEmpty(resultStr)) {

				inactivityTimer.onActivity();
				playBeepSoundAndVibrate();

				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();

				releaseCamera();
				barcodeScanned = true;

				Intent rIntent = new Intent();
				rIntent.putExtra("QR_CODE", resultStr);
				setResult(RESULT_OK, rIntent);
				scrollToFinishActivity();
			}
		}
	};

//	private void initViews() {
//
//		inactivityTimer = new InactivityTimer(this);
//
//		mImageScanner = new ImageScanner();
//		mImageScanner.setConfig(0, Config.X_DENSITY, 3);
//		mImageScanner.setConfig(0, Config.Y_DENSITY, 3);
//
//		autoFocusHandler = new Handler();
//		mCameraManager = CameraManager.get();
//		try {
//			mCameraManager.openDriver();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		//调整扫描框大小,自适应屏幕
//		Display display = this.getWindowManager().getDefaultDisplay();
//		int width = display.getWidth();
//		int height = display.getHeight();
//
//		RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) scanCropView.getLayoutParams();
//		linearParams.height = (int) (width * 0.65);
//		linearParams.width = (int) (width * 0.65);
//		scanCropView.setLayoutParams(linearParams);
//		//**
//
//		mCamera = mCameraManager.getCamera();
//		mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
//		scanPreview.addView(mPreview);
//
//		TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
//				TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
//				TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
//		mAnimation.setDuration(1500);
//		mAnimation.setRepeatCount(-1);
//		mAnimation.setRepeatMode(Animation.REVERSE);
//		mAnimation.setInterpolator(new LinearInterpolator());
//		scanLine.setAnimation(mAnimation);
//
//	}

	public void onPause() {
		super.onPause();
		releaseCamera();
		mCameraManager.closeDriver();
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}


	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};



	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	/**
	 * 初始化截取的矩形区域
	 */
	private void initCrop() {
		int cameraWidth = mCameraManager.getCameraResolution().y;
		int cameraHeight = mCameraManager.getCameraResolution().x;

		/** 获取布局中扫描框的位置信息 */
		int[] location = new int[2];
		scanCropView.getLocationInWindow(location);

		int cropLeft = location[0];
		int cropTop = location[1] - getStatusBarHeight();

		int cropWidth = scanCropView.getWidth();
		int cropHeight = scanCropView.getHeight();

		/** 获取布局容器的宽高 */
		int containerWidth = scanContainer.getWidth();
		int containerHeight = scanContainer.getHeight();

		/** 计算最终截取的矩形的左上角顶点x坐标 */
		int x = cropLeft * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的左上角顶点y坐标 */
		int y = cropTop * cameraHeight / containerHeight;

		/** 计算最终截取的矩形的宽度 */
		int width = cropWidth * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的高度 */
		int height = cropHeight * cameraHeight / containerHeight;

		/** 生成最终的截取的矩形 */
		mCropRect = new Rect(x, y, width + x, height + y);
	}

	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 打开
			mCameraManager.openLight();
		} else {
			flag = true;
			// 关闭
			mCameraManager.offLight();
		}
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			scrollToFinishActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}