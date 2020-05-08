package com.qimalocl.manage.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.fitsleep.sunshinelibrary.utils.SharedPreferencesUtils;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.http.OkHttpClientManager;
import com.http.ResultCallback;
import com.http.rdata.RUserLogin;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseActivity;
import com.qimalocl.manage.core.common.AppManager;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.fragment.AlarmFragment;
import com.qimalocl.manage.fragment.MaintenanceFragment;
import com.qimalocl.manage.fragment.MineFragment;
import com.qimalocl.manage.fragment.MissionFragment;
import com.qimalocl.manage.fragment.QueryFragment;
import com.qimalocl.manage.fragment.ScanFragment;
import com.qimalocl.manage.model.NearbyBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.TabEntity;
import com.qimalocl.manage.utils.Globals;
import com.zxing.lib.scaner.CaptureActivityHandler2;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Request;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String INTENT_MSG_COUNT = "INTENT_MSG_COUNT";
    public final static String MESSAGE_RECEIVED_ACTION = "io.yunba.example.msg_received_action";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private static final int PRIVATE_CODE = 1315;//开启GPS权限
    static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;

    @BindView(R.id.fl_change) FrameLayout flChange;
    @BindView(R.id.tab) CommonTabLayout tab;
    @BindView(R.id.ll_tab) LinearLayout llTab;

    private Context mContext;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
//    private String[] mTitles = { "用车", "任务", "报警", "查询", "维护" };
//    private int[] mIconUnselectIds = {
//            R.mipmap.scan, R.mipmap.mission, R.mipmap.alarm, R.mipmap.query,R.mipmap.maintenance
//    };
//    private int[] mIconSelectIds = {
//            R.mipmap.scan2, R.mipmap.mission2, R.mipmap.alarm2, R.mipmap.query2,R.mipmap.maintenance2
//    };
    private String[] mTitles = { "首页", "任务", "查询", "我的" };
    private int[] mIconUnselectIds = {
            R.mipmap.home, R.mipmap.mission, R.mipmap.query, R.mipmap.mine
    };
    private int[] mIconSelectIds = {
            R.mipmap.home2, R.mipmap.mission2, R.mipmap.query2, R.mipmap.mine2
    };

    private ScanFragment scanFragment;
    private MissionFragment missionFragment;
    private QueryFragment queryFragment;
    private MineFragment mineFragment;

    private AlarmFragment alarmFragment;

    private MaintenanceFragment maintenanceFragment;

    LocationManager locationManager;
    String provider = LocationManager.GPS_PROVIDER;

    private CaptureActivityHandler2 handler;

    MainActivity activity;
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ui_main);
        ButterKnife.bind(this);

        activity = this;
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(mReceiver, filter);

        initData();
        initView();
        initListener();
//        initLocation();
//        AppApplication.getApp().scan();

//        OkHttpClientManager.getInstance().UserLogin("99920170623", "123456", new ResultCallback<RUserLogin>() {
//            @Override
//            public void onError(Request request, Exception e) {
//                Log.e("UserLogin===", "====Error");
////                UIHelper.showToast(this, e.getMessage());
////                failLogin();
//            }
//
//            @Override
//            public void onResponse(RUserLogin rUserLogin) {
//                if (rUserLogin.getResult() < 0) {
////                    failLogin();
//                    Log.e("UserLogin===", "====fail");
//                }
//                else {
//                    Globals.USERNAME = "99920170623";
////                    Globals.BLE_NAME = "GpDTxe7<a";
////                    successLogin();
//                    Log.e("UserLogin===", "====success");
//                }
//            }
//        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//      must store the new intent unless getIntent() will return the old one
        setIntent(intent);

        Log.e("ma===onNewIntent", SharedPreferencesUrls.getInstance().getString("access_token", "") + "===");

    }

    @Override
    public void onResume() {
        super.onResume();

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");

        boolean flag = getIntent().getBooleanExtra("flag", false);

        Log.e("ma===onResume",  loadingDialog+ "===" + flag + "===" + access_token);

//        if (loadingDialog != null && !loadingDialog.isShowing()) {
//            loadingDialog.setTitle("请稍等");
//            loadingDialog.show();
//        }

//        if (loadingDialog != null && loadingDialog.isShowing()){
//            loadingDialog.dismiss();
//        }

        if("".equals(access_token)){
            tab.setCurrentTab(0);
        }

        if(flag){
//            purseFragment.user();
            scanFragment.cars(true);
            missionFragment.initHttp();
            mineFragment.initHttp();
        }



//        mainFragment.show
//        tab.setCurrentTab(0);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();

            if ("data.broadcast.action".equals(action)) {
                int count = intent.getIntExtra("count", 0);

                Log.e("main===mReceiver", "==="+count);

                if (count > 0) {
                    tab.showMsg(1, count);
                    tab.setMsgMargin(1, -8, 5);
                } else {
                    tab.hideMsg(1);
                }
            }
        }
    };

    private void initData() {
        mContext = this;
        scanFragment = new ScanFragment();
        missionFragment = new MissionFragment();
        queryFragment = new QueryFragment();
        mineFragment = new MineFragment();

//        alarmFragment = new AlarmFragment();
//        maintenanceFragment = new MaintenanceFragment();

        mFragments.add(scanFragment);
        mFragments.add(missionFragment);
        mFragments.add(queryFragment);
        mFragments.add(mineFragment);

//        mFragments.get(1).set


//        mFragments.add(alarmFragment);
//        mFragments.add(maintenanceFragment);

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));

        }
    }



    private final LocationListener locationListener = new LocationListener() {

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
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        locationManager.requestLocationUpdates(provider, 2000, 500, locationListener);

        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    private void openGPSSettings() {

        if (checkGPSIsOpen()) {
        } else {

            CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
            customBuilder.setTitle("温馨提示").setMessage("请在手机设置打开应用的位置权限并选择最精准的定位模式")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    })
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, PRIVATE_CODE);
                        }
                    });
            customBuilder.create().show();

        }
    }

    private void initView() {

        openGPSSettings();

        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        tab = findViewById(R.id.tab);

        tab.setTabData(mTabEntities, MainActivity.this, R.id.fl_change, mFragments);


//        TabLayout.Tab tab1 = tab.getChildAt(1);

//        tab.getIconView(1).setClickable(false);
//        tab.getChildAt(1).setClickable(false);

        tab.setCurrentTab(0);

        Log.e("ma===initView", tab.getChildAt(0)+"==="+tab.getChildAt(1));

//        tab.getChildAt(0).setClickable(false);
//        tab.onT

//        LinearLayout tabStrip = (LinearLayout) tab.getChildAt(0);
//
//        tabStrip.getChildAt(1).setClickable(false);
//        tabStrip.getChildAt(2).setClickable(false);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        Log.e("main==result", requestCode+"==="+resultCode);
//
//        if (resultCode == RESULT_OK) {
//
//
//
//            switch (requestCode) {
//                case 101:
//                    changeTab(4);
//                    break;
//
//                default:
//                    break;
//
//            }
//        } else {
//            switch (requestCode) {
//                case PRIVATE_CODE:
//                    openGPSSettings();
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.CALL_PHONE)){
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + "0519-86999222"));
                        startActivity(intent);
                    }
                }else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开电话权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(localIntent);
                            finish();
                        }
                    });
                    customBuilder.create().show();
                }
                break;

            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PERMISSION_GRANTED) {

                    initView();
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取定位权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finish();
                                }
                            }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(localIntent);
                            finish();
                        }
                    });
                    customBuilder.create().show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void changeTab(int index) {
        tab.setCurrentTab(index);
    }

    private void initListener() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("main==onDestroy", "===");

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("main==onBackPressed", "===");
        super.onBackPressed();
        //Toast.makeText(FDQControlAct.this, "onBackPessed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try{
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                customBuilder.setTitle("温馨提示").setMessage("您将退出7MA调度。")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        AppManager.getAppManager().AppExit(context);
                    }
                });
                customBuilder.create().show();


                return true;
            }catch (Exception e){

            }
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    protected void handleReceiver(Context context, Intent intent) {
//        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
//            return;
//        }
//        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
//        if (Constants.SHOW_MSG_NUM.equals(intent.getAction())) {
//            int count = intent.getIntExtra(INTENT_MSG_COUNT, 0);
//            if (count > 0) {
//                tab.showMsg(0, count);
//                tab.setMsgMargin(0, -8, 5);
//                msgFragment.updateMsgList();
//            } else {
//                tab.hideMsg(0);
//            }
//        } else if (Constants.BROADCAST_UPDATE_MONEY.equals(intent.getAction())) {
//            //getUserInfo();
//            sendBroadcast(new Intent(LibraryConstants.BROADCAST_GET_PARK_USER_INFO));
//        } else if (Constants.BROADCAST_TURN_TO_LOCK_LIST.equals(intent.getAction())) {
//            if(tab.getCurrentTab() == 1){
//                Intent i = new Intent(mContext, LockListActivity.class);
//                i.putExtra(LockListActivity.INTENT_LOT_ID, intent.getStringExtra(LockListActivity.INTENT_LOT_ID));
//                startActivity(i);
//            }
//        }
//        else if(Constants.BROADCAST_SHARE_AUTO_CANCEL.equals(intent.getAction())){
//            CommonDialog.showYesDialog(mContext, "当前车位共享时间已结束，请预约其他车位", null, null);
//        }
//    }


//    private void initLocation() {
//        boolean bLocationPermission = AndPermission.hasPermission(mContext, Permission.LOCATION);
//        if (!bLocationPermission) {
//            requestLocationPermission();
//        }
//    }
//
//    private void requestLocationPermission() {
//        AndPermission.with(this).requestCode(101).permission(Permission.LOCATION).callback(this)
//                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
//                // 这样避免用户勾选不再提示，导致以后无法申请权限。
//                // 你也可以不设置。
//                .rationale(new RationaleListener() {
//                    @Override
//                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
//                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
//                        AndPermission.rationaleDialog(mContext, rationale).show();
//                    }
//                }).start();
//    }
//
//    @PermissionYes(101) private void getLocationYes(@NonNull List<String> grantedPermissions) {
//        Log.i(TAG, "getLocationYes");
//    }
//
//    @PermissionNo(101) private void getLocationNo(@NonNull List<String> grantedPermissions) {
//        ToastUtil.showMessage("为了app能正常使用，请打开定位权限");
//        Log.i(TAG, "getLocationNo");
//    }
}
