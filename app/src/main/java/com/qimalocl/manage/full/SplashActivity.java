package com.qimalocl.manage.full;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.activity.MainActivity;
import com.qimalocl.manage.base.AppStatusManager;
import com.qimalocl.manage.base.Base2Activity;
import com.qimalocl.manage.base.BaseActivity;
import com.qimalocl.manage.core.common.AppManager;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.Md5Helper;
import com.qimalocl.manage.core.common.NetworkUtils;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.model.AppStatus;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.utils.LogUtil;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.UUID;

@SuppressLint("NewApi")
public class SplashActivity extends Base2Activity {

	private Context context = this;
	public static boolean isForeground = false;

	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();

	private ImageView loadingImage;
	private String imageUrl;
	private String ad_link;
	private String app_type;
	private String app_id;

	LocationManager locationManager;
	String provider = LocationManager.GPS_PROVIDER;
	private static final int PRIVATE_CODE = 1315;
	static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;


	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.main_enter);
//		setContentLayout(R.layout.main_enter);
		loadingImage = (ImageView) findViewById(R.id.plash_loading_main);
//		initHttp();

		init();
	}

	private void init() {
		LogUtil.e("init===1", "===");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int checkPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
			if (checkPermission != PackageManager.PERMISSION_GRANTED) {
				if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
					requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
				} else {
					requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
				}
				return;
			}
		}

		LogUtil.e("init===2", "===");


		/**
		 *
		 * 读写手机状态和身份
		 *
		 */
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int checkPermission = this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
			if (checkPermission != PackageManager.PERMISSION_GRANTED) {
				if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
					requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
				} else {
					requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
				}
				return;
			}
		}
		// <!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据 -->
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int checkPermission = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
			if (checkPermission != PackageManager.PERMISSION_GRANTED) {
				if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
					requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
				} else {
					requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
				}
				return;
			}
		}

		if (Build.VERSION.SDK_INT >= 23) {
			int checkPermission = context.checkSelfPermission(Manifest.permission.CAMERA);
			if (checkPermission != PackageManager.PERMISSION_GRANTED) {
				if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
					requestPermissions(new String[]{Manifest.permission.CAMERA}, 102);
				} else {
					requestPermissions(new String[]{Manifest.permission.CAMERA}, 102);
				}
				return;
			}
		}

//		if (Build.VERSION.SDK_INT >= 23) {
//			int checkPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//			if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//				if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//					requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
//				} else {
//					requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
//				}
//				return;
//			}
//		}

		String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

		AppStatusManager.getInstance().setAppStatus(AppStatus.STATUS_NORMAL);
		if("".equals(access_token)){
			UIHelper.goToAct(context, LoginActivity.class);
		}else{
			UIHelper.goToAct(context, MainActivity.class);
		}

		finishMine();

//        initLocation();
	}

	private boolean checkGPSIsOpen() {
		boolean isOpen;
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
		provider = locationManager.getBestProvider(criteria, true);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return false;
		}
		locationManager.requestLocationUpdates(provider, 2000, 500, locationListener2);

		isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
		return isOpen;
	}

	private final LocationListener locationListener2 = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {

		}

		@Override
		public void onProviderDisabled(String arg0) {

		}

		@Override
		public void onProviderEnabled(String arg0) {

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

		}

	};


	/**
	 * 获取启动页图广告
	 * */
	private void initHttp() {

		if (NetworkUtils.getNetWorkType(context) == NetworkUtils.NONETWORK) {
			loadingImage.setBackgroundResource(R.drawable.enter_bg);
			Toast.makeText(context, "暂无网络连接，请连接网络", Toast.LENGTH_SHORT).show();
		} else {
			RequestParams params = new RequestParams();
			params.put("adsid", "10");
			HttpHelper.get(context, Urls.getIndexAd, params, new TextHttpResponseHandler() {
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					loadingImage.setBackgroundResource(R.drawable.enter_bg);
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					try {
						ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
						if (result.getFlag().equals("Success")) {
							JSONArray array = new JSONArray(result.getData());
							for (int i = 0; i < array.length(); i++) {
								imageUrl = array.getJSONObject(i).getString("ad_file");
								ad_link = array.getJSONObject(i).getString("ad_link");
								app_type = array.getJSONObject(i).getString("app_type");
								app_id = array.getJSONObject(i).getString("app_id");

								SharedPreferencesUrls.getInstance().putString("ad_link", ad_link);
								SharedPreferencesUrls.getInstance().putString("app_type", app_type);
								SharedPreferencesUrls.getInstance().putString("app_id", app_id);
							}
							if (imageUrl == null || "".equals(imageUrl)) {
								loadingImage.setBackgroundResource(R.drawable.enter_bg);
							} else {
								// 加载图片
								Glide.with(context).load(imageUrl).into(loadingImage);
							}
						}
					} catch (Exception e) {

					}
				}
			});
		}
	}

	/**
	 * 初始化定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void initLocation() {
		if (NetworkUtils.getNetWorkType(context) != NetworkUtils.NONETWORK) {
			//初始化client
			locationClient = new AMapLocationClient(this.getApplicationContext());
			//设置定位参数
			locationClient.setLocationOption(getDefaultOption());
			// 设置定位监听
			locationClient.setLocationListener(locationListener);
			startLocation();
		} else {
			Toast.makeText(context, "暂无网络连接，请连接网络", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	/**
	 * 默认的定位参数
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private AMapLocationClientOption getDefaultOption() {
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(20 * 1000);//可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
		mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
		mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
		AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
		mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
		mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
		mOption.setLocationCacheEnable(false); //可选，设置是否使用缓存定位，默认为true
		return mOption;
	}

	/**
	 * 定位监听
	 */
	AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation loc) {

			if (null != loc) {
				if (0.0 != loc.getLongitude() && 0.0 != loc.getLongitude()) {
//					PostDeviceInfo(loc.getLatitude(), loc.getLongitude());
					stopLocation();
				} else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(SplashActivity.this);
					customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开定位权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									finishMine();
								}
							}).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent localIntent = new Intent();
							localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							localIntent.setData(Uri.fromParts("package", getPackageName(), null));
							startActivity(localIntent);
							finishMine();
						}
					});
					customBuilder.create().show();
					return;


//                    if (Build.VERSION.SDK_INT >= 23) {
//                        int checkPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                                requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
//                                        REQUEST_CODE_ASK_PERMISSIONS);
//                            } else {
//                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                                customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开位置权限！")
//                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.cancel();
//                                            }
//                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                        requestPermissions(
//                                                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
//                                                REQUEST_CODE_ASK_PERMISSIONS);
//                                    }
//                                });
//                                customBuilder.create().show();
//                            }
//                            return;
//                        }
//                    }


				}
			} else {
				Toast.makeText(context, "定位失败", Toast.LENGTH_SHORT).show();
				finishMine();
			}
		}
	};

	/**
	 * 开始定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void startLocation() {
		// 设置定位参数
		locationClient.setLocationOption(locationOption);
		// 启动定位
		locationClient.startLocation();
	}

	/**
	 * 停止定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void stopLocation() {
		// 停止定位
		locationClient.stopLocation();
	}

	/**
	 * 销毁定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void destroyLocation() {
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}

	public Thread mThread = new Thread() {
		public void run() {
			try {
				mThread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			UIHelper.goToAct(context, MainActivity.class);
			finishMine();
		}

		;
	};

	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		destroyLocation();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AppManager.getAppManager().AppExit(context);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 获取版本号
	 *
	 * @return 当前应用的版本号
	 */
	public int getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			int version = info.versionCode;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	// 提交设备信息到appinfo
	private void PostDeviceInfo(double latitude, double longitude) {
		if (NetworkUtils.getNetWorkType(context) != NetworkUtils.NONETWORK) {
			try {
				TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
					return;
				}
//				String UUID = tm.getDeviceId();
				String uuid = UUID.randomUUID().toString();
				String system_version = Build.VERSION.RELEASE;
				String device_model = new Build().MODEL;
				RequestParams params = new RequestParams();
				Md5Helper Md5Helper = new Md5Helper();
				String verify = Md5Helper.encode("7mateapp" + uuid);
				params.put("verify", verify);
				params.put("system_name", "Android");
				params.put("system_version", system_version);
				params.put("device_model", device_model);
				params.put("device_user", new Build().MANUFACTURER + device_model);
				params.put("UUID", uuid);
				params.put("longitude", longitude);
				params.put("latitude", latitude);
				HttpHelper.post(context, Urls.DevicePostUrl, params, new TextHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, String responseString) {
						try {
							ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
							if (result.getFlag().toString().equals("Success")) {

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

					}
				});
				mThread.start();
			} catch (Exception e) {
				showDialog();
				return;
			}
		}else{
			Toast.makeText(context,"暂无网络连接，请连接网络",Toast.LENGTH_SHORT).show();
			return;
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 0:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					init();
				} else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开存储空间权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									finishMine();
								}
							}).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent localIntent = new Intent();
							localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							localIntent.setData(Uri.fromParts("package", getPackageName(), null));
							startActivity(localIntent);
							finishMine();
						}
					});
					customBuilder.create().show();
				}
				return;
			case 100:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					init();
				} else {
//					showDialog();

					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许设备信息权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									finishMine();
								}
							}).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent localIntent = new Intent();
							localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							localIntent.setData(Uri.fromParts("package", getPackageName(), null));
							startActivity(localIntent);
							finishMine();
						}
					});
					customBuilder.create().show();

				}
				return;
			case 101:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					init();
				}else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
					customBuilder.setTitle("温馨提示").setMessage("您需要在设置里定位权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									finishMine();
								}
							}).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent localIntent = new Intent();
							localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							localIntent.setData(Uri.fromParts("package", getPackageName(), null));
							startActivity(localIntent);
							finishMine();
						}
					});
					customBuilder.create().show();
				}
				return;

			case 102:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					init();
				}else {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
					customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									finishMine();
								}
							}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
//							requestPermissions(new String[] { Manifest.permission.CAMERA },100);

							Intent localIntent = new Intent();
							localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							localIntent.setData(Uri.fromParts("package", getPackageName(), null));
							startActivity(localIntent);
							finishMine();
						}
					});
					customBuilder.create().show();

				}
				return;

			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void showDialog() {
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
		customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许设备信息权限！")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						finishMine();
					}
				}).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				Intent localIntent = new Intent();
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
				localIntent.setData(Uri.fromParts("package", getPackageName(), null));
				startActivity(localIntent);
				finishMine();
			}
		});
		customBuilder.create().show();
		return;
	}
}
