package com.zxing.lib.scaner.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.google.zxing.Result;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.ChangeKeyLockManageActivity;
import com.qimalocl.manage.activity.DeviceDetailAlertActivity;
import com.qimalocl.manage.activity.LockManageActivity;
import com.qimalocl.manage.activity.LockManageAlterActivity;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.activity.Main2Activity;
import com.qimalocl.manage.activity.Main3Activity;
import com.qimalocl.manage.activity.MainActivity;
import com.qimalocl.manage.activity.MainXiaoanActivity;
import com.qimalocl.manage.activity.TestXiaoanActivity;
import com.qimalocl.manage.base.BaseApplication;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.fragment.MaintenanceFragment;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.qimalocl.manage.utils.ToastUtil;
import com.vondear.rxtools.RxAnimationTool;
import com.vondear.rxtools.RxBeepTool;
import com.vondear.rxtools.interfaces.OnRxScanerListener;
import com.zxing.lib.scaner.CameraManager;
import com.zxing.lib.scaner.CaptureActivityHandler;
import com.zxing.lib.scaner.decoding.InactivityTimer;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 二维码扫描
 */
@SuppressLint("NewApi")
public class ActivityScanerCode extends SwipeBackActivity implements View.OnClickListener{

	private Context context;
	private Activity mActivity = this;

	BluetoothAdapter mBluetoothAdapter;

	private Camera mCamera;
//	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private CameraManager mCameraManager;

	private InactivityTimer inactivityTimer;

	private SurfaceView scanPreview;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;
	private ImageView ivHand, ivLight;

	private Rect mCropRect = null;
	private boolean barcodeScanned = false;
	private boolean previewing = true;
	private ImageScanner mImageScanner = null;

	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private ImageView cancle;
	private LinearLayout bikeNunBtn;

	private RelativeLayout mCropLayout, mCropLayout2;
	private LinearLayout ll_input;
//	private LoadingDialog loadingDialog;

	private String codenum = "";
	// 输入法
	private Dialog dialog;
	private EditText bikeNumEdit;
	private Button positiveButton,negativeButton;
	private LinearLayout lightBtn;
	private int Tag = 0;
	private boolean isChangeKey = false;
	private boolean isAdd = false;

	private boolean hasSurface;
	SurfaceHolder surfaceHolder;
	private int mCropWidth = 0;
	private int mCropHeight = 0;

	private CaptureActivityHandler handler;
	private static OnRxScanerListener mScanerListener;

	SurfaceHolder.Callback sf;

	private boolean isNext = false;
	private String deviceuuid;

	private List<String> macList = new ArrayList<String>();
	private int n = 0;

	private boolean isHide = false;
	private boolean isHand = false;
	private boolean isLight = false;

	private boolean isPermission = false;

	private InputMethodManager inputMethodManager;

//	static {
//		System.loadLibrary("iconv");
//	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner);

//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//		FragmentManager fm = getSupportFragmentManager();
//		Fragment fragment = fm.findFragmentById(R.id.fr);

		context = this;

		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		isChangeKey = getIntent().getExtras().getBoolean("isChangeKey");
		isAdd = getIntent().getExtras().getBoolean("isAdd", false);
		findViewById();
		//权限初始化
		initPermission();
		//扫描动画初始化
		initScanerAnimation();

//		initViews();
//		playBeep = true;
//		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
//		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//			playBeep = false;
//		}
//		initBeepSound();
//		vibrate = true;

		bikeNumEdit.setText("");

		CameraManager.init(context);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		mCameraManager = CameraManager.get();

		Log.e("ActivityScanerCode===", "===");

//		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//			Toast.makeText(ActivityScanerCode.this, "您的设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
//			scrollToFinishActivity();
//		}
//		//蓝牙锁
//		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//		mBluetoothAdapter = bluetoothManager.getAdapter();
//
//		if (mBluetoothAdapter == null) {
//			Toast.makeText(ActivityScanerCode.this, "获取蓝牙失败", Toast.LENGTH_SHORT).show();
//			scrollToFinishActivity();
//			return;
//		}
//		if (!mBluetoothAdapter.isEnabled()) {
//			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableBtIntent, 188);
//		}else{
//			;
//		}

		BleManager.getInstance().init(getApplication());
//		BleManager.getInstance()
//				.enableLog(true)
//				.setReConnectCount(1, 5000)
//				.setConnectOverTime(20000)
//				.setOperateTimeout(5000);
		BleManager.getInstance()
				.enableLog(true)
				.setReConnectCount(10, 5000)
				.setConnectOverTime(20000)
				.setOperateTimeout(10000);

		setScanRule();
		scan();

	}


	@Override
	protected void onResume() {
		super.onResume();

		Log.e("surface===0", surfaceHolder+"==="+hasSurface);

		if(!isPermission){
			if (!hasSurface) {

				if(surfaceHolder==null){
					surfaceHolder = scanPreview.getHolder();

					Log.e("surface===0_1", isPermission+"==="+hasSurface+"==="+surfaceHolder);

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

								if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
									ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
								}
								//蓝牙锁
								if (mBluetoothAdapter == null) {
									BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
									mBluetoothAdapter = bluetoothManager.getAdapter();
								}

								if (mBluetoothAdapter == null) {
									ToastUtil.showMessageApp(context, "获取蓝牙失败");
									return;
								}

								if (!mBluetoothAdapter.isEnabled()) {
									isPermission = true;

									previewing = false;
									mCropLayout2.setVisibility(View.GONE);

									Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
									startActivityForResult(enableBtIntent, 188);
								} else {

									BleManager.getInstance().init(getApplication());
									BleManager.getInstance()
											.enableLog(true)
											.setReConnectCount(10, 5000)
											.setConnectOverTime(20000)
											.setOperateTimeout(10000);

									setScanRule();
									scan();

									Log.e("asc===onCreate", "==="+mCamera);

									if (mCamera!=null){

									}


								}


							}
						}

						@Override
						public void surfaceDestroyed(SurfaceHolder holder) {
							Log.e("surface===2", "==="+hasSurface);

							hasSurface = false;

							if (loadingDialog != null && loadingDialog.isShowing()) {
								loadingDialog.dismiss();
							}
						}
					});
					surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				}

			} else {
				Log.e("surface===3", "==="+hasSurface);

				initCamera(surfaceHolder);
			}

		}

//		if (!hasSurface) {
//			//Camera初始化
////            initCamera(surfaceHolder);
////            resetCamera(surfaceHolder);
//
//			if(surfaceHolder==null){
//				surfaceHolder = scanPreview.getHolder();
//
//				sf = new SurfaceHolder.Callback() {
//					@Override
//					public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//					}
//
//					@Override
//					public void surfaceCreated(SurfaceHolder holder) {
//						Log.e("surface===1", "==="+hasSurface);
//
//						if (!hasSurface) {
//							hasSurface = true;
//							initCamera(holder);
//						}
//					}
//
//					@Override
//					public void surfaceDestroyed(SurfaceHolder holder) {
//						Log.e("surface===2", "==="+hasSurface);
//
//						hasSurface = false;
//					}
//				};
//
//				surfaceHolder.addCallback(sf);
//				surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//			}
//
//		} else {
//			initCamera(surfaceHolder);
//		}


	}

	private void findViewById() {

		loadingDialog = new LoadingDialog(ActivityScanerCode.this);
		loadingDialog.setCancelable(false);
		loadingDialog.setCanceledOnTouchOutside(false);

//		scanPreview =  (FrameLayout) findViewById(R.id.capture_preview);
		scanPreview =  (SurfaceView) findViewById(R.id.capture_preview);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);
		ivHand = (ImageView) findViewById(R.id.iv_hand);
		ivLight = (ImageView) findViewById(R.id.iv_light);
		cancle = (ImageView)findViewById(R.id.iv_cancle);
		bikeNunBtn = (LinearLayout)findViewById(R.id.loca_show_btnBikeNum);
		lightBtn = (LinearLayout)findViewById(R.id.activity_qr_scan_lightBtn);

		mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
		mCropLayout2 = (RelativeLayout) findViewById(R.id.capture_crop_layout2);
		ll_input = (LinearLayout) findViewById(R.id.ll_input);

		dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
		View dialogView = LayoutInflater.from(this).inflate(R.layout.pop_circles_menu, null);
		dialog.setContentView(dialogView);
		dialog.setCanceledOnTouchOutside(false);

		bikeNumEdit = (EditText)findViewById(R.id.pop_circlesMenu_bikeNumEdit);
		positiveButton = (Button)dialogView.findViewById(R.id.pop_circlesMenu_positiveButton);
		negativeButton = (Button)dialogView.findViewById(R.id.pop_circlesMenu_negativeButton);

		cancle.setOnClickListener(this);
		positiveButton.setOnClickListener(this);
		negativeButton.setOnClickListener(this);
		bikeNunBtn.setOnClickListener(this);
		lightBtn.setOnClickListener(this);
		findViewById(R.id.top_back).setOnClickListener(this);
		findViewById(R.id.top_mask_bcg).setOnClickListener(this);
		findViewById(R.id.left_mask).setOnClickListener(this);
		findViewById(R.id.right_mask).setOnClickListener(this);
		findViewById(R.id.bottom_mask).setOnClickListener(this);

//        initViews();
		playBeep = true;
		AudioManager audioService = (AudioManager) context.getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	public void onClick(View v) {
		String uid = SharedPreferencesUrls.getInstance().getString("uid","");
		String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
		switch (v.getId()){
//			case R.id.iv_cancle:
//				scrollToFinishActivity();
//				break;

			case R.id.top_back:
				inputMethodManager.hideSoftInputFromWindow(bikeNumEdit.getWindowToken(), 0);
				scrollToFinishActivity();
				break;

			case R.id.top_mask_bcg:
			case R.id.left_mask:
			case R.id.right_mask:
			case R.id.bottom_mask:
				Log.e("onClick===1", "===");
				isHand = false;
				ivHand.setImageResource(R.drawable.top_hand);
//				btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand), null, null);

				previewing = true;
				initCamera(surfaceHolder);

				mCropLayout2.setVisibility(View.VISIBLE);
				ll_input.setVisibility(View.GONE);

				inputMethodManager.hideSoftInputFromWindow(bikeNumEdit.getWindowToken(), 0);
				break;

			case R.id.loca_show_btnBikeNum:
				if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
					Toast.makeText(ActivityScanerCode.this,"请先登录账号",Toast.LENGTH_SHORT).show();
					UIHelper.goToAct(ActivityScanerCode.this, LoginActivity.class);
				}else {
					//关闭相机
//					releaseCamera();
//					mCameraManager.closeDriver();

					Log.e("asc===btnBikeNum", isHand+"==="+mCamera);

					if(isHand){
						isHand = false;
						ivHand.setImageResource(R.drawable.top_hand);
//						btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand), null, null);

						previewing = true;
						initCamera(surfaceHolder);
//                		mCamera.startPreview();

						mCropLayout2.setVisibility(View.VISIBLE);
						ll_input.setVisibility(View.GONE);

						inputMethodManager.hideSoftInputFromWindow(bikeNumEdit.getWindowToken(), 0);
					}else{
						isHand = true;
						ivHand.setImageResource(R.drawable.top_hand2);
//						btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand2), null, null);

						bikeNumEdit.setText("");

						if (mCamera!=null){
							previewing = false;
						}

						isHide = true;

						mCropLayout2.setVisibility(View.GONE);
						ll_input.setVisibility(View.VISIBLE);


						bikeNumEdit.requestFocus();

						inputMethodManager.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
						inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

//						InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//						manager.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
//						manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


					}

//					bikeNumEdit.setText("");
//
//					WindowManager windowManager = getWindowManager();
//					Display display = windowManager.getDefaultDisplay();
//					WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//					lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
//					lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//					dialog.getWindow().setAttributes(lp);
//					dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
//					dialog.show();
//
//					InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//					manager.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
//					manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
				}
				break;

			case R.id.pop_circlesMenu_positiveButton:
				String bikeNum = bikeNumEdit.getText().toString().trim();
				if (bikeNum == null || "".equals(bikeNum)){
					Toast.makeText(this,"请输入单车编号",Toast.LENGTH_SHORT).show();
					return;
				}
				InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				Tag = 1;
				lockInfo(bikeNum);
				break;

			case R.id.pop_circlesMenu_negativeButton:
				InputMethodManager manager1= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				manager1.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
				if (dialog.isShowing()) {
					dialog.dismiss();
				}

				initCamera(surfaceHolder);

				break;

			case R.id.activity_qr_scan_lightBtn:

				if (isLight) {
					isLight = false;
					// 打开
					ivLight.setImageResource(R.drawable.top_light2);

					mCameraManager.openLight();
				} else {
					isLight = true;
					// 关闭
					ivLight.setImageResource(R.drawable.top_light);
					mCameraManager.offLight();
				}


				break;

			default:
				break;
		}
	}

	private void setScanRule() {
//        String[] uuids;
//        String str_uuid = et_uuid.getText().toString();
//        if (TextUtils.isEmpty(str_uuid)) {
//            uuids = null;
//        } else {
//            uuids = str_uuid.split(",");
//        }
//        UUID[] serviceUuids = null;
//        if (uuids != null && uuids.length > 0) {
//            serviceUuids = new UUID[uuids.length];
//            for (int i = 0; i < uuids.length; i++) {
//                String name = uuids[i];
//                String[] components = name.split("-");
//                if (components.length != 5) {
//                    serviceUuids[i] = null;
//                } else {
//                    serviceUuids[i] = UUID.fromString(uuids[i]);
//                }
//            }
//        }

//        String[] names;
//        String str_name = et_name.getText().toString();
//        if (TextUtils.isEmpty(str_name)) {
//            names = null;
//        } else {
//            names = str_name.split(",");
//        }
//
//        String mac = et_mac.getText().toString();
//
//        boolean isAutoConnect = sw_auto.isChecked();

		BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
//				.setDeviceMac(address)                  // 只扫描指定mac的设备，可选
//                .setAutoConnect(true)                 // 连接时的autoConnect参数，可选，默认false
//                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
				.build();
		BleManager.getInstance().initScanRule(scanRuleConfig);
	}

	void scan(){
//        loadingDialog = DialogUtils.getLoadingDialog(context, "正在搜索...");
//		loadingDialog.setTitle("正在搜索");
//		loadingDialog.show();

		BleManager.getInstance().scan(new BleScanCallback() {
			@Override
			public void onScanStarted(boolean success) {
//                mDeviceAdapter.clearScanDevice();
//                mDeviceAdapter.notifyDataSetChanged();
//                img_loading.startAnimation(operatingAnim);
//                img_loading.setVisibility(View.VISIBLE);
//                btn_scan.setText(getString(R.string.stop_scan));
				Log.e("asc===onScanStarted", "==="+success);

			}

			@Override
			public void onLeScan(BleDevice bleDevice) {
				super.onLeScan(bleDevice);

				Log.e("asc===onLeScan", bleDevice+"==="+bleDevice.getMac());
			}

			@Override
			public void onScanning(final BleDevice bleDevice) {
//                mDeviceAdapter.addDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();

				Log.e("asc===onScanning", bleDevice+"==="+bleDevice.getMac());

				macList.add(bleDevice.getMac());

//				m_myHandler.post(new Runnable() {
//					@Override
//					public void run() {
//						if(address.equals(bleDevice.getMac())){
//							//                            if (loadingDialog != null && loadingDialog.isShowing()) {
////                                loadingDialog.dismiss();
////                            }
//
//							BleManager.getInstance().cancelScan();
//
//							Log.e("onScanning===2", isConnect+"==="+bleDevice+"==="+bleDevice.getMac());
//
//							Toast.makeText(context, "搜索成功", Toast.LENGTH_LONG).show();
//
//							connect();
//
////                            m_myHandler.postDelayed(new Runnable() {
////                                @Override
////                                public void run() {
////                                    if(!isConnect)
////                                        connect();
////                                }
////                            }, 5 * 1000);
//						}
//					}
//				});

			}

			@Override
			public void onScanFinished(List<BleDevice> scanResultList) {
//                img_loading.clearAnimation();
//                img_loading.setVisibility(View.INVISIBLE);
//                btn_scan.setText(getString(R.string.start_scan));

				Log.e("asc===onScanFinished", scanResultList+"==="+scanResultList.size());
			}
		});
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

	private void initCamera(SurfaceHolder surfaceHolder) {
		Log.e("initCamera===", "===="+handler);

//		previewing = true;

		try {
			mCameraManager = CameraManager.get();
			mCameraManager.openDriver(surfaceHolder);
			mCamera = mCameraManager.getCamera();
			Point point = mCameraManager.getCameraResolution();
			AtomicInteger width = new AtomicInteger(point.y);
			AtomicInteger height = new AtomicInteger(point.x);
			int cropWidth = scanCropView.getWidth() * width.get() / scanContainer.getWidth();
			int cropHeight = scanCropView.getHeight() * height.get() / scanContainer.getHeight();
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);

//			mCamera.startPreview();
//			mCamera.autoFocus(autoFocusCB);

//			mCamera.autoFocus(new Camera.AutoFocusCallback() {
//				@Override
//				public void onAutoFocus(boolean success, Camera camera) {
////					camera.cancelAutoFocus();
//				}
//			});
//			mCamera.autoFocus(autoFocusCB);
//			mCamera.startPreview();

//			if (handler == null) {
//				handler = new CaptureActivityHandler(this);
//			}

			handler = new CaptureActivityHandler(this);
		} catch (Exception ioe) {
			Log.e("initCamera===e", "===="+ioe);
//			return;
		}

	}

	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};


	public void onPause() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}

//        hasSurface = false;

		Log.e("ASC===onPause", surfaceHolder+"==="+hasSurface+"==="+handler);


		//		surfaceHolder.removeCallback(sf);

		if (handler != null) {
			handler.quitSynchronously();
//			handler = null;
		}
////		inactivityTimer.onPause();
//
		mCameraManager.closeDriver();

		if (!hasSurface) {
//			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
//			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(sf);
		}

//		surfaceHolder.removeCallback(sf);

		Log.e("ASC===onPause2", "==="+hasSurface);

		super.onPause();
	}

//	@Override
//	protected void onDestroy() {
//		Log.e("onDestroy===ASC1", "===="+inactivityTimer);
//
//		inactivityTimer.shutdown();
////		mScanerListener = null;
//		super.onDestroy();
//
//        BleManager.getInstance().cancelScan();
//
//		Log.e("onDestroy===ASC2", "===="+inactivityTimer);
//
////		previewing = false;
////
////		if (handler != null) {
////			handler.quitSynchronously();
////			handler = null;
////		}
////
////		Log.e("onDestroy===ASC2", "===="+mCameraManager);
////
////		releaseCamera();
////		mCameraManager.closeDriver();
//
//
//
////		m_myHandler.removeCallbacksAndMessages(null);
////		resetCamera();
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try{
			if(isPermission){
				inactivityTimer.shutdown();
				mScanerListener = null;
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}

				Log.e("scan===onDestroy", "===");

				inputMethodManager.hideSoftInputFromWindow(bikeNumEdit.getWindowToken(), 0);

				m_myHandler.removeCallbacksAndMessages(null);
			}

		}catch (Exception e){
//            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
		}



	}


	public void handleDecode(final Result result) {
		m_myHandler.post(new Runnable() {
			@Override
			public void run() {
				try{
					Log.e("handleDecode===", "==="+previewing);

					if(!previewing) return;

					if(previewing)
						inactivityTimer.onActivity();

					//扫描成功之后的振动与声音提示
					if(previewing)
						RxBeepTool.playBeep(mActivity, vibrate);

					Log.e("===", "====");

					String result1 = result.getText();
					if (mScanerListener == null) {
						initDialogResult(result);
					} else {
						mScanerListener.onSuccess("From to Camera", result);
					}

				} catch (Exception e) {
					ToastUtil.showMessageApp(context, "扫码异常，请重试");
					scrollToFinishActivity();
				}

			}
		});


	}

	private void initDialogResult(final Result result) {
//		useBike(result.toString());

		Log.e("add_xiaoan_car===", SharedPreferencesUrls.getInstance().getString("type", "")+"==="+result+"==="+result.toString().contains("IMEI:"));

		if("7".equals(SharedPreferencesUrls.getInstance().getString("type", ""))){

            if(isAdd){
                if(isNext){

                    if(!result.toString().contains("7mate.cn")){
//					Toast.makeText(ActivityScanerCode.this, "不是7MA电单车，请重试!", Toast.LENGTH_SHORT).show();

                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                        customBuilder.setTitle("温馨提示").setMessage("不是7MA电单车，请重试!")
                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                        isNext = true;
                                        initCamera(surfaceHolder);
                                    }
                                });
                        customBuilder.create().show();


                    }else{
                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                        customBuilder.setTitle("温馨提示").setMessage("扫码成功，是否入库?")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                        isNext = true;

                                        initCamera(surfaceHolder);
                                    }
                                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                isNext = true;

                                add_xiaoan_car(deviceuuid, result.toString());

                            }
                        });
                        customBuilder.create().show();
                    }


                }else{
                    if(!result.toString().contains("IMEI:")){
//					Toast.makeText(ActivityScanerCode.this, "不是小安中控，请重试!", Toast.LENGTH_SHORT).show();

                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                        customBuilder.setTitle("温馨提示").setMessage("请先扫描中控二维码，谢谢!")
                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                        isNext = false;
                                        initCamera(surfaceHolder);

                                    }
                                });
                        customBuilder.create().show();


                    }else{
                        deviceuuid = result.toString().split(" ")[0].split(":")[1];

                        Log.e("initDialogResult===", "==="+deviceuuid);

                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                        customBuilder.setTitle("温馨提示").setMessage("imei:"+deviceuuid+"\n是否继续?")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                        isNext = false;

                                        initCamera(surfaceHolder);
                                    }
                                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();

										isNext = true;

										initCamera(surfaceHolder);

										Toast.makeText(ActivityScanerCode.this, "请继续扫描绑定车辆二维码", Toast.LENGTH_LONG).show();
									}
								});
                        customBuilder.create().show();
                    }

                }


            }else{
                if(!result.toString().contains("IMEI:")){
//					Toast.makeText(ActivityScanerCode.this, "不是小安中控，请重试!", Toast.LENGTH_SHORT).show();

                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("不是小安中控，请重试!")
                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    isNext = false;
                                    initCamera(surfaceHolder);
                                }
                            });
                    customBuilder.create().show();
                }else{
                    deviceuuid = result.toString().split(" ")[0].split(":")[1];

                    Log.e("initDialogResult===2", "==="+deviceuuid);

                    Intent rIntent = new Intent();
                    rIntent.putExtra("QR_CODE", deviceuuid);
                    setResult(RESULT_OK, rIntent);
                    scrollToFinishActivity();

//                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                    customBuilder.setTitle("温馨提示").setMessage("imei:"+deviceuuid+"\n是否继续?")
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//
//                                    isNext = false;
//
//                                    initCamera(surfaceHolder);
//                                }
//                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//
//                            isNext = true;
//
//                            initCamera(surfaceHolder);
//
//                        }
//                    });
//                    customBuilder.create().show();
                }
            }

		}else{
			if(isAdd){
				Intent rIntent = new Intent();
				rIntent.putExtra("QR_CODE", result.toString());
				setResult(RESULT_OK, rIntent);
				scrollToFinishActivity();
			}else{
				lockInfo(result.toString());
			}
		}

	}

	private void add_xiaoan_car(String deviceuuid, String result){
		RequestParams params = new RequestParams();
		params.put("deviceuuid", deviceuuid);
		params.put("tokencode", result);
		HttpHelper.post(ActivityScanerCode.this, Urls.add_xiaoan_car, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				if (loadingDialog != null && !loadingDialog.isShowing()) {
					loadingDialog.setTitle("正在提交");
					loadingDialog.show();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				UIHelper.ToastError(ActivityScanerCode.this, throwable.toString());
			}


			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				try {
					ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
					if (result.getFlag().equals("Success")) {
						Toast.makeText(ActivityScanerCode.this, result.getMsg(), Toast.LENGTH_SHORT).show();

						JSONObject jsonObject = new JSONObject(result.getData());

						Log.e("add_xiaoan_car===", responseString+"===");
//						Log.e("Scan===", responseString+"==="+jsonObject.getString("deviceuuid")+"==="+jsonObject.getString("deviceuuid")+"==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));


					} else {
						Toast.makeText(ActivityScanerCode.this,result.getMsg(),Toast.LENGTH_SHORT).show();
						if (loadingDialog != null && loadingDialog.isShowing()){
							loadingDialog.dismiss();
						}
						scrollToFinishActivity();
					}
				} catch (Exception e) {
					Log.e("Test","异常"+e);
				}
				if (loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
			}
		});

//		String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//		String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//		if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
//			Toast.makeText(ActivityScanerCode.this,"请先登录账号",Toast.LENGTH_SHORT).show();
//			UIHelper.goToAct(ActivityScanerCode.this, LoginActivity.class);
//		}else {
//
//		}
	}

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(final byte[] data, final Camera camera) {

			m_myHandler.post(new Runnable() {
				@Override
				public void run() {
					Size size = camera.getParameters().getPreviewSize();

					Log.e("previewCb===", "==="+data.length);

					// 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
					byte[] rotatedData = new byte[data.length];
					for (int y = 0; y < size.height; y++) {
						for (int x = 0; x < size.width; x++)
							rotatedData[x * size.height + size.height - y - 1] = data[x
									+ y * size.width];
					}

					// 宽高也要调整
					int tmp = size.width;
					size.width = size.height;
					size.height = tmp;

					initCrop();

					Image barcode = new Image(size.width, size.height, "Y800");
					barcode.setData(rotatedData);
					barcode.setCrop(mCropRect.left, mCropRect.top, mCropRect.width(),
							mCropRect.height());

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
						Tag = 0;
						lockInfo(resultStr);
					}
				}
			});

		}
	};




	public void setCropWidth(int cropWidth) {
		mCropWidth = cropWidth;
		CameraManager.FRAME_WIDTH = mCropWidth;

	}


	public void setCropHeight(int cropHeight) {
		this.mCropHeight = cropHeight;
		CameraManager.FRAME_HEIGHT = mCropHeight;
	}





//	private void initViews() {
//
//		mImageScanner = new ImageScanner();
//		mImageScanner.setConfig(0, Config.X_DENSITY, 3);
//		mImageScanner.setConfig(0, Config.Y_DENSITY, 3);
//
//		autoFocusHandler = new Handler();
//		mCameraManager = new CameraManager(context);
//		try {
//			mCameraManager.openDriver();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		//调整扫描框大小,自适应屏幕
//		Display display = this.getWindowManager().getDefaultDisplay();
//		int width = display.getWidth();
//	    int height = display.getHeight();
//
//		RelativeLayout.LayoutParams linearParams =  (RelativeLayout.LayoutParams)scanCropView.getLayoutParams();
//		linearParams.height = (int) (width*0.65);
//		linearParams.width = (int) (width*0.65);
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
//	}



	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();

			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}



	public Handler getHandler() {
		return handler;
	}

	Handler m_myHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message mes) {
			switch (mes.what) {
				default:
					break;
			}
			return false;
		}
	});

//	private void resetCamera(){
//		mCameraManager = new CameraManager(this);
//		try {
//			mCameraManager.openDriver();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		mCamera = mCameraManager.getCamera();
//		mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
//		scanPreview.addView(mPreview);
//
//		previewing = true;
//	}

	void macLoop(final JSONObject jsonObject){


		try {

			final String macinfo = jsonObject.getString("macinfo");
			boolean is = macList.contains(macinfo);

			if(n<5){
				n++;

				Log.e("macLoop===", is+"==="+n);

				if(is){
					BleManager.getInstance().cancelScan();

					Intent intent = new Intent(ActivityScanerCode.this, LockManageAlterActivity.class);
					intent.putExtra("name", "NokeLock");
					intent.putExtra("codenum",codenum);
					intent.putExtra("pdk",jsonObject.getString("pdk"));
					intent.putExtra("pwd",jsonObject.getString("pwd"));
					intent.putExtra("address", macinfo);
					intent.putExtra("isMac",true);
					startActivity(intent);
					scrollToFinishActivity();
				}else{
					m_myHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							Log.e("macLoop===1", "==="+n);

							macLoop(jsonObject);

						}
					}, 1 * 1000);
				}

			}else{
				Log.e("macLoop===finish", "===");
				BleManager.getInstance().cancelScan();

				Intent intent = new Intent(ActivityScanerCode.this, LockManageAlterActivity.class);
				intent.putExtra("name", "NokeLock");
				intent.putExtra("codenum",codenum);
				intent.putExtra("pdk",jsonObject.getString("pdk"));
				intent.putExtra("pwd",jsonObject.getString("pwd"));
				intent.putExtra("address", macinfo);
				intent.putExtra("isMac",is);
				startActivity(intent);
				scrollToFinishActivity();
			}


		}catch (Exception e){

		}
	}

	private void info(final JSONObject jsonObject){
		try {


			if ("1".equals(jsonObject.getString("type"))){
				//机械锁
				scrollToFinishActivity();
			}else if ("4".equals(jsonObject.getString("type"))){    //行运兔

				Log.e("Scan===4", jsonObject.getString("macinfo")+"==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));

				Intent intent = new Intent(ActivityScanerCode.this, Main2Activity.class);
				intent.putExtra("bleid", jsonObject.getString("bleid"));
				intent.putExtra("codenum", jsonObject.getString("codenum"));
				intent.putExtra("address", jsonObject.getString("macinfo"));
				startActivity(intent);
//
//				Intent intent = new Intent();
//				intent.setClass(context, ActivityScanerCode.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.putExtra("isChangeKey",false);
//				startActivityForResult(intent, 101);

				intent.putExtra("sx", "1");
				setResult(RESULT_OK, intent);

				scrollToFinishActivity();
			}else if ("5".equals(jsonObject.getString("type")) || "6".equals(jsonObject.getString("type"))){    //3合1锁

				Log.e("Scan===5", "==="+jsonObject.getString("lock_no")+"==="+jsonObject.getString("type"));

				if("5".equals(jsonObject.getString("type"))){
					SharedPreferencesUrls.getInstance().putString("type", "5");
				}else{
					SharedPreferencesUrls.getInstance().putString("type", "6");
				}


				Intent intent = new Intent(ActivityScanerCode.this, DeviceDetailAlertActivity.class);
				//									intent.putExtra("lock_no", jsonObject.getString("lock_no"));
				//									intent.putExtra("codenum", jsonObject.getString("codenum"));
				//									intent.putExtra("address", jsonObject.getString("macinfo"));
				intent.putExtra("mac", jsonObject.getString("macinfo"));
				intent.putExtra("name", jsonObject.getString("lock_no"));
				startActivity(intent);
			}else if ("7".equals(jsonObject.getString("type"))){    //3合1锁

				Log.e("Scan===7", jsonObject.getString("deviceuuid")+"==="+jsonObject.getString("type"));


				Intent intent = new Intent(ActivityScanerCode.this, MainXiaoanActivity.class);
				intent.putExtra("codenum", jsonObject.getString("codenum"));
				intent.putExtra("address", jsonObject.getString("macinfo"));
				intent.putExtra("deviceuuid", jsonObject.getString("deviceuuid"));
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
//				startActivityForResult(intent, 101);


//				Intent intent2 = new Intent();
				intent.putExtra("sx", "1");
				setResult(RESULT_OK, intent);

				scrollToFinishActivity();
			}else {


				if (!isChangeKey){
					if ("2".equals(jsonObject.getString("pdk"))){
						BaseApplication.getInstance().getIBLE().setChangKey(true);
					}else {
						BaseApplication.getInstance().getIBLE().setChangKey(false);
					}
					if ("2".equals(jsonObject.getString("pwd"))){
						BaseApplication.getInstance().getIBLE().setChangPsd(true);
					}else {
						BaseApplication.getInstance().getIBLE().setChangPsd(false);
					}
				}else {
					BaseApplication.getInstance().getIBLE().setChangKey(false);
					BaseApplication.getInstance().getIBLE().setChangPsd(false);
				}

				if ("2".equals(jsonObject.getString("type"))){
					if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
						Toast.makeText(ActivityScanerCode.this, "您的设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
						scrollToFinishActivity();
					}
					//蓝牙锁
					BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
					mBluetoothAdapter = bluetoothManager.getAdapter();

					if (mBluetoothAdapter == null) {
						Toast.makeText(ActivityScanerCode.this, "获取蓝牙失败", Toast.LENGTH_SHORT).show();
						scrollToFinishActivity();
						return;
					}
					if (!mBluetoothAdapter.isEnabled()) {
						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent, 188);
					}else{
						if (!isChangeKey){
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
								Log.e("Scan===2_1", macList.contains(jsonObject.getString("macinfo"))+"==="+jsonObject.getString("bleid")+"==="+isChangeKey+"==="+jsonObject.getString("pwd")+"==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));

								n=0;
								macLoop(jsonObject);

//								boolean is = macList.contains(macinfo);
//								int n = is?0:1;
//								m_myHandler.postDelayed(new Runnable() {
//									@Override
//									public void run() {
//										boolean is = macList.contains(macinfo);
//										int n = is?0:1;
//										m_myHandler.postDelayed(new Runnable() {
//											@Override
//											public void run() {
//												try {
//													Intent intent = new Intent(ActivityScanerCode.this, LockManageAlterActivity.class);
//													intent.putExtra("name", "NokeLock");
//													intent.putExtra("codenum",codenum);
//													intent.putExtra("pdk",jsonObject.getString("pdk"));
//													intent.putExtra("pwd",jsonObject.getString("pwd"));
//													intent.putExtra("address", macinfo);
//													intent.putExtra("isMac", macList.contains(jsonObject.getString("macinfo")));
//													startActivity(intent);
//													scrollToFinishActivity();
//												}catch (Exception e){
//
//												}
//											}
//										}, n * 1000);
//									}
//								}, n * 1000);


							}else {
								Log.e("Scan===2_2", jsonObject.getString("bleid")+"==="+isChangeKey+"==="+jsonObject.getString("pwd")+"==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));

								n=0;
								macLoop(jsonObject);

//								Intent intent = new Intent(ActivityScanerCode.this, LockManageActivity.class);
//								intent.putExtra("name", "NokeLock");
//								intent.putExtra("codenum",codenum);
//								intent.putExtra("pdk",jsonObject.getString("pdk"));
//								intent.putExtra("pwd",jsonObject.getString("pwd"));
//								intent.putExtra("address", jsonObject.getString("macinfo"));
//								startActivity(intent);
//								scrollToFinishActivity();
							}
						}else {
							Log.e("Scan===2_3", jsonObject.getString("bleid")+"==="+isChangeKey+"==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));

							Intent intent = new Intent(ActivityScanerCode.this, ChangeKeyLockManageActivity.class);
							intent.putExtra("name", "NokeLock");
							intent.putExtra("codenum",codenum);
							intent.putExtra("address", jsonObject.getString("macinfo"));
							startActivity(intent);
							scrollToFinishActivity();
						}

					}
				}else if ("3".equals(jsonObject.getString("type"))){    //3合1锁

					Log.e("Scan===3", "==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));

//					Intent intent = new Intent(ActivityScanerCode.this, Main3Activity.class);
//					intent.putExtra("codenum", jsonObject.getString("codenum"));
//					intent.putExtra("address", jsonObject.getString("macinfo"));
//					startActivity(intent);
//					scrollToFinishActivity();

					if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
						Toast.makeText(ActivityScanerCode.this, "您的设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
						scrollToFinishActivity();
					}
					BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
					mBluetoothAdapter = bluetoothManager.getAdapter();

					if (mBluetoothAdapter == null) {
						Toast.makeText(ActivityScanerCode.this, "获取蓝牙失败", Toast.LENGTH_SHORT).show();
						scrollToFinishActivity();
						return;
					}
					if (!mBluetoothAdapter.isEnabled()) {
						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent, 188);
					}else{
						if (!isChangeKey){
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){

								Log.e("Scan===3_1", macList.contains(jsonObject.getString("macinfo"))+"==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));

								Intent intent = new Intent(ActivityScanerCode.this, LockManageAlterActivity.class);
								intent.putExtra("name", "NokeLock");
								intent.putExtra("codenum",codenum);
								intent.putExtra("pdk",jsonObject.getString("pdk"));
								intent.putExtra("pwd",jsonObject.getString("pwd"));
								intent.putExtra("address", jsonObject.getString("macinfo"));
								intent.putExtra("isMac", macList.contains(jsonObject.getString("macinfo")));
								startActivity(intent);
							}else {
								Intent intent = new Intent(ActivityScanerCode.this, LockManageActivity.class);
								intent.putExtra("name", "NokeLock");
								intent.putExtra("codenum",codenum);
								intent.putExtra("pdk",jsonObject.getString("pdk"));
								intent.putExtra("pwd",jsonObject.getString("pwd"));
								intent.putExtra("address", jsonObject.getString("macinfo"));
								startActivity(intent);
							}
						}else {
							Intent intent = new Intent(ActivityScanerCode.this, ChangeKeyLockManageActivity.class);
							intent.putExtra("name", "NokeLock");
							intent.putExtra("codenum",codenum);
							intent.putExtra("address", jsonObject.getString("macinfo"));
							startActivity(intent);
						}
						scrollToFinishActivity();
					}

				}
			}
		} catch (Exception e) {
			Log.e("Test","异常"+e);
		}
	}

	private void lockInfo(String result){

		String uid = SharedPreferencesUrls.getInstance().getString("uid","");
		String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
		if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
			Toast.makeText(ActivityScanerCode.this,"请先登录账号",Toast.LENGTH_SHORT).show();
			UIHelper.goToAct(ActivityScanerCode.this, LoginActivity.class);
		}else {
			RequestParams params = new RequestParams();
			params.put("uid",uid);
			params.put("access_token",access_token);
			params.put("tokencode",result);
			HttpHelper.post(ActivityScanerCode.this, Urls.lockInfo, params, new TextHttpResponseHandler() {
				@Override
				public void onStart() {
					if (loadingDialog != null && !loadingDialog.isShowing()) {
						loadingDialog.setTitle("正在提交");
						loadingDialog.show();
					}
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					if (loadingDialog != null && loadingDialog.isShowing()){
						loadingDialog.dismiss();
					}
					UIHelper.ToastError(ActivityScanerCode.this, throwable.toString());
				}
				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					try {
						ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

						Log.e("Scan===00", "==="+responseString);

						if (result.getFlag().equals("Success")) {
							final JSONObject jsonObject = new JSONObject(result.getData());

							codenum = jsonObject.getString("codenum");

							Log.e("Scan===", codenum+"==="+jsonObject.getString("bleid")+"==="+responseString+"==="+isChangeKey+"==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));

							if (result.getInfo().equals("2")) {
								CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
								customBuilder.setTitle("温馨提示").setMessage("该车被设为坏车，用户无法使用，请及时回收维修")
										.setPositiveButton("去维护", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												dialog.cancel();

												MaintenanceFragment.bikeNum=codenum;
												MaintenanceFragment.ff=1;

												Intent rIntent = new Intent();
												rIntent.putExtra("tz", "1");
												setResult(RESULT_OK, rIntent);
//												setResult(RESULT_OK);

												scrollToFinishActivity();
//												MainActivity.changeTab(4);

												if (loadingDialog != null && loadingDialog.isShowing()){
													loadingDialog.dismiss();
												}
											}
										})
										.setNegativeButton("开锁", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												dialog.cancel();

												info(jsonObject);

											}
										});
								customBuilder.create().show();
							}else{
								info(jsonObject);
							}

						} else {
							Log.e("lockinfo===error","==="+result.getInfo());

//							if (result.getInfo().equals("1")) {
//
//							}else{
//
//							}

							Toast.makeText(ActivityScanerCode.this,result.getMsg(),Toast.LENGTH_SHORT).show();
							if (loadingDialog != null && loadingDialog.isShowing()){
								loadingDialog.dismiss();
							}
							scrollToFinishActivity();


						}
					} catch (Exception e) {

						if (loadingDialog != null && loadingDialog.isShowing()){
							loadingDialog.dismiss();
						}
						Log.e("Test","异常"+e);
					}

				}
			});
		}
	}



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


	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.e("188===", isPermission+"==="+requestCode+"==="+resultCode);

		if(resultCode == RESULT_OK)
		{
			switch (requestCode) {
				case 288:{
					break;
				}
				case 188:{
					if (null != loadingDialog && loadingDialog.isShowing()) {
						loadingDialog.dismiss();
					}

					isPermission = true;

					Log.e("188===1", deviceuuid+"==="+hasSurface+"==="+surfaceHolder);

					previewing = true;
					initCamera(surfaceHolder);
					mCropLayout2.setVisibility(View.VISIBLE);

					BleManager.getInstance().init(getApplication());
					BleManager.getInstance()
							.enableLog(true)
							.setReConnectCount(10, 5000)
							.setConnectOverTime(20000)
							.setOperateTimeout(10000);

					setScanRule();
					scan();

					break;
				}
				default:{
					break;
				}
			}
		}else if( requestCode == 188){
			ToastUtil.showMessageApp(this, "需要打开蓝牙");

			scrollToFinishActivity();

//			if("".equals(oid)){
////                setResult(RESULT_OK);
//				scrollToFinishActivity();
//			}else{
//				if(!isFinishing()){
//					tzEnd();
//				}
//			}

		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Log.e("onKeyDown===", "==="+keyCode);

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			inputMethodManager.hideSoftInputFromWindow(bikeNumEdit.getWindowToken(), 0);

			if(isHide){
				isHide = false;

				isHand = false;
				ivHand.setImageResource(R.drawable.top_hand);
//				btnBikeNum.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.top_hand), null, null);

				previewing = true;
				initCamera(surfaceHolder);

				mCropLayout2.setVisibility(View.VISIBLE);
				ll_input.setVisibility(View.GONE);


			}else{
				finishMine();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
