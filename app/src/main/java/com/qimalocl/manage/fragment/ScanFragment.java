package com.qimalocl.manage.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.fitsleep.sunshinelibrary.utils.ConvertUtils;
import com.fitsleep.sunshinelibrary.utils.EncryptUtils;
import com.fitsleep.sunshinelibrary.utils.ToastUtils;
import com.http.rdata.RRent;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.ClientManager;
import com.qimalocl.manage.activity.DeviceSelectActivity;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.activity.MainActivity;
import com.qimalocl.manage.activity.TestXiaoanActivity;
import com.qimalocl.manage.base.BaseFragment;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.KeyBean;
import com.qimalocl.manage.model.NearbyBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.UserIndexBean;
import com.qimalocl.manage.utils.ToastUtil;
import com.qimalocl.manage.utils.UtilAnim;
import com.qimalocl.manage.utils.UtilBitmap;
import com.qimalocl.manage.utils.UtilScreenCapture;
import com.sofi.blelocker.library.Code;
import com.sofi.blelocker.library.connect.listener.BleConnectStatusListener;
import com.sofi.blelocker.library.connect.options.BleConnectOptions;
import com.sofi.blelocker.library.model.BleGattProfile;
import com.sofi.blelocker.library.protocol.ICloseListener;
import com.sofi.blelocker.library.protocol.IConnectResponse;
import com.sofi.blelocker.library.protocol.IEmptyResponse;
import com.sofi.blelocker.library.protocol.IGetRecordResponse;
import com.sofi.blelocker.library.protocol.IGetStatusResponse;
import com.sofi.blelocker.library.search.SearchRequest;
import com.sofi.blelocker.library.search.SearchResult;
import com.sofi.blelocker.library.search.response.SearchResponse;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.mode.GetLockStatusTxOrder;
import com.sunshine.blelibrary.mode.GetTokenTxOrder;
import com.sunshine.blelibrary.mode.OpenLockTxOrder;
import com.sunshine.blelibrary.mode.XinbiaoTxOrder;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.xiaoantech.sdk.XiaoanBleApiClient;
import com.xiaoantech.sdk.ble.model.Response;
import com.xiaoantech.sdk.ble.scanner.ScanResult;
import com.xiaoantech.sdk.listeners.BleCallback;
import com.xiaoantech.sdk.listeners.BleStateChangeListener;
import com.xiaoantech.sdk.listeners.ScanResultCallback;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import permissions.dispatcher.NeedsPermission;

import static android.app.Activity.RESULT_OK;
import static com.fitsleep.sunshinelibrary.utils.EncryptUtils.Encrypt;
import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

@SuppressLint("NewApi")
public class ScanFragment extends BaseFragment implements View.OnClickListener, LocationSource
        , BleStateChangeListener, ScanResultCallback
        , AMapLocationListener,AMap.OnCameraChangeListener,AMap.OnMapTouchListener{

    Unbinder unbinder;

    private Context context;
    private Activity activity;

//    private LoadingDialog loadingDialog;
    @BindView(R.id.msg) TextView tvMsg;
    @BindView(R.id.mainUI_msg) LinearLayout llMsg;
    @BindView(R.id.mainUI_rightBtn) TextView rightBtn;
    @BindView(R.id.mainUI_scanCode_lock) LinearLayout scanCodeBtn;
    @BindView(R.id.mainUI_leftBtn) TextView leftBtn;
    @BindView(R.id.mainUI_scanCode_lookRecordBtn) Button lookBtn;
    @BindView(R.id.mainUI_lookLocationBtn) LinearLayout lookLocationBtn;
    @BindView(R.id.mainUI_storageLayout) LinearLayout storeageLayout;
    @BindView(R.id.mainUI_scanCode_changeKeyBtn) Button changeKeyBtn;
    @BindView(R.id.mainUI_lockLayout) LinearLayout lockLayout;
    @BindView(R.id.mainUI_unLockLayout) LinearLayout unLockLayout;
    @BindView(R.id.mainUI_scanCode_endLayout) LinearLayout endLayout;
    @BindView(R.id.mainUI_myLocationLayout) LinearLayout myLocationLayout;
    @BindView(R.id.mainUI_getDotLayout) LinearLayout getDotLayout;
    @BindView(R.id.mainUI_testXALayout) LinearLayout testXALayout;
    @BindView(R.id.mainUI_yellow_xyt) TextView tvYellow_xyt;
    @BindView(R.id.mainUI_red_xyt) TextView tvRed_xyt;
    @BindView(R.id.mainUI_yellow_xa) TextView tvYellow_xa;
    @BindView(R.id.mainUI_red_xa) TextView tvRed_xa;
    @BindView(R.id.switcher) Switch switcher;
    @BindView(R.id.switcher_bad) Switch switcher_bad;
    @BindView(R.id.switcher_type) Switch switcher_type;

    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = true;
    private LatLng myLocation = null;
    private Circle mCircle;
    private BitmapDescriptor successDescripter;
    private BitmapDescriptor bikeDescripter;
    private BitmapDescriptor bikeDescripter_red;
    private BitmapDescriptor bikeDescripter_yellow;
    private BitmapDescriptor bikeDescripter_green;
    private BitmapDescriptor bikeDescripter_blue;
    private BitmapDescriptor bikeDescripter_brown;
    private BitmapDescriptor bikeDescripter_xa_red;
    private BitmapDescriptor bikeDescripter_xa_yellow;
    private BitmapDescriptor bikeDescripter_xa_green;
    private BitmapDescriptor bikeDescripter_xa_blue;
    private BitmapDescriptor bikeDescripter_xa_brown;
    private Handler handler = new Handler();
    private Marker centerMarker;
    private boolean isMovingMarker = false;

    private List<Marker> bikeMarkerList;
    private boolean isUp = false;

    private static double latitude = 0.0;
    private static double longitude = 0.0;
    private int isLock = 0;
    private View v;


    private String quantity;
    Marker curMarker;

    LocationManager locationManager;
    String provider = LocationManager.GPS_PROVIDER;
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    public String codenum = "";
    private String m_nowMac = "";
    private int carmodel_id = 1;
    private String type = "";
    private String lock_no = "";
    private String bleid = "";
    private String deviceuuid = "";
    private String electricity = "";
    private String mileage = "";
    private float accuracy = 29.0f;
    float leveltemp = 18f;

    private String keySource = "";
    //密钥索引
    int encryptionKey= 0;
    //开锁密钥
    String keys = null;
    //服务器时间戳，精确到秒，用于锁同步时间
    long serverTime;
    private String tel = "13188888888";

    private BleDevice bleDevice;
    private XiaoanBleApiClient apiClient;
    private String token;

    PopupWindow popupwindow;

    BluetoothAdapter mBluetoothAdapter;

    private boolean isPermission = true;

    private int timeout = 10000;

    private boolean isConnect = false;
    private boolean isStop = false;
    private boolean isOpen = false;
    private boolean isFinish = false;
    private int n = 0;
    private int cn = 0;
    private int force_backcar = 0;
    private boolean isTwo = false;
    private boolean first3 = true;
    private boolean isEndBtn = false;
    public static int flagm = 0;
    boolean isFrist1 = true;
    boolean stopScan = false;
    private int clickCount = 0;
    private int tz = 0;
    private String transtype = "";
    private int major = 0;
    private int minor = 0;
    private boolean isGPS_Lo = false;
    private boolean scan = false;
    private boolean isTemp = false;
    private boolean isLookPsdBtn = false;
    private boolean isAgain = false;
    private String backType = "";
    private boolean isOpenLock = false;
    private int order_type = 0;
    private boolean isWaitEbikeInfo = true;
    private Thread ebikeInfoThread;
    public String oid = "";
    private int notice_code = 0;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_scan, null);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        activity = getActivity();

//        registerReceiver(new String[] { LibraryConstants.BROADCAST_UPDATE_USER_INFO });

		IntentFilter filter = new IntentFilter("data.broadcast.action");
		context.registerReceiver(mReceiver, filter);

//        filter = new IntentFilter("data.broadcast.action");
//        activity.registerReceiver(broadcastReceiver, filter);

        mapView = activity.findViewById(R.id.mainUI_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        bikeMarkerList = new ArrayList<>();
        initView();

        initHttp();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){

                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    m_myHandler.sendEmptyMessage(1);
                }

            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume===Scan", latitude + "===" + longitude);

//        initNearby(latitude, longitude);

//        mapView.onResume();

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)){
            rightBtn.setText("退出登录");
        }else {
            rightBtn.setText("登录");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Log.e("onHiddenChanged===Scan", mapView+"==="+hidden);

        if(hidden){
            //pause
            mapView.setVisibility(View.GONE);

//            mapView.onPause();
//            deactivate();
        }else{
            //resume
            mapView.setVisibility(View.VISIBLE);

//            mapView.onResume();
//
//            if (aMap != null) {
//                setUpMap();
//            }

//            aMap.clear();
//            aMap.reloadMap();
//
//            setUpMap();
//
////            m_myHandler.sendEmptyMessage(4);
//
//            aMap.setOnMapTouchListener(this);
//            aMap.setOnCameraChangeListener(this);
//
//
//            if(centerMarker!=null){
//                centerMarker.remove();
//                centerMarker=null;
//            }
//            if(mCircle!=null){
//                mCircle.remove();
//                mCircle = null;
//            }
//
//
//            if(latitude!=0 && longitude!=0){
//                myLocation = new LatLng(latitude, longitude);
//
//                initNearby(latitude, longitude);
//
//                addChooseMarker();
//                addCircle(myLocation, accuracy);
//            }


        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();

            Log.e("scan===mReceiver", "==="+action);

            if ("data.broadcast.action".equals(action)) {
                if(tvMsg!=null){
                    if(intent.getIntExtra("count", 0)!=0){
                        tvMsg.setText(intent.getStringExtra("codenum")+" 需要回收，请及时完成工作");
                    }else{
                        tvMsg.setText("");
                    }

                }

            }
        }
    };

    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
        } else {
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
            customBuilder.setTitle("温馨提示").setMessage("请在手机设置打开应用的位置权限并选择最精准的定位模式")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            activity.finish();
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

    private boolean checkGPSIsOpen() {
        boolean isOpen;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        locationManager.requestLocationUpdates(provider, 2000, 500, locationListener);

        isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Log.e("checkGPSIsOpen==","==="+isOpen);

        return isOpen;
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



    private void initView(){
        openGPSSettings();

        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },101);
                } else {
                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },101);
                }
                return;
            }
        }



        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        aMap.setMapType(AMap.MAP_TYPE_NAVI);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18f);// 设置缩放监听
        aMap.moveCamera(cameraUpdate);

        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
        bikeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);
        bikeDescripter_red = BitmapDescriptorFactory.fromResource(R.drawable.ebike_red_icon);
        bikeDescripter_yellow = BitmapDescriptorFactory.fromResource(R.drawable.ebike_yellow_icon);
        bikeDescripter_green = BitmapDescriptorFactory.fromResource(R.drawable.ebike_green_icon);
        bikeDescripter_blue = BitmapDescriptorFactory.fromResource(R.drawable.ebike_blue_icon);
        bikeDescripter_brown = BitmapDescriptorFactory.fromResource(R.drawable.ebike_brown_icon);

        bikeDescripter_xa_red = BitmapDescriptorFactory.fromResource(R.drawable.ebike_xa_red_icon);
        bikeDescripter_xa_yellow = BitmapDescriptorFactory.fromResource(R.drawable.ebike_xa_yellow_icon);
        bikeDescripter_xa_green = BitmapDescriptorFactory.fromResource(R.drawable.ebike_xa_green_icon);
        bikeDescripter_xa_blue = BitmapDescriptorFactory.fromResource(R.drawable.ebike_xa_blue_icon);
        bikeDescripter_xa_brown = BitmapDescriptorFactory.fromResource(R.drawable.ebike_xa_brown_icon);

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                curMarker = marker;

                marker.setTitle(marker.getTitle());

                Log.e("onMarkerClick===", marker.getTitle()+"==="+marker.getTitle().split("-")[0]);

                codenum = marker.getTitle().split("-")[0];
                quantity = marker.getTitle().split("-")[1];

                initmPopupWindowView();
                return true;
            }
        });


        aMap.setOnMapTouchListener(ScanFragment.this);
        setUpLocationStyle();

//        Log.e("zoom==", "=="+aMap.getCameraPosition().zoom);

        llMsg.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        lookBtn.setOnClickListener(this);
        scanCodeBtn.setOnClickListener(this);
        lookLocationBtn.setOnClickListener(this);
        changeKeyBtn.setOnClickListener(this);
        storeageLayout.setOnClickListener(this);
        lockLayout.setOnClickListener(this);
        unLockLayout.setOnClickListener(this);
        endLayout.setOnClickListener(this);
        myLocationLayout.setOnClickListener(this);
        getDotLayout.setOnClickListener(this);
        testXALayout.setOnClickListener(this);
        switcher.setOnClickListener(this);
        switcher_bad.setOnClickListener(this);
        switcher_type.setOnClickListener(this);

        BleManager.getInstance().init(activity.getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(10, 5000)
                .setConnectOverTime(timeout)
                .setOperateTimeout(10000);

        setScanRule();
        scan();
    }

    private void setScanRule() {

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
//				.setDeviceMac(address)                  // 只扫描指定mac的设备，可选
//                .setAutoConnect(true)                 // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(timeout)              // 扫描超时时间，可选，默认10秒
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
                Log.e("mf===onScanStarted", "==="+success);

            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);

                Log.e("mf===onLeScan", bleDevice+"==="+bleDevice.getMac());
            }

            @Override
            public void onScanning(final BleDevice bleDevice) {
//                mDeviceAdapter.addDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();

                Log.e("mf===onScanning", bleDevice+"==="+bleDevice.getMac());


            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
//                img_loading.clearAnimation();
//                img_loading.setVisibility(View.INVISIBLE);
//                btn_scan.setText(getString(R.string.start_scan));

                Log.e("mf===onScanFinished", scanResultList+"==="+scanResultList.size());
            }
        });
    }


    public void initmPopupRentWindowView(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_rent_bike, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_rent_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_rent_back);
//        ImageView iv_rent_cancelBtn = (ImageView) customView.findViewById(R.id.iv_rent_cancelBtn);
//        TextView tv_codenum = (TextView) customView.findViewById(R.id.tv_codenum);
//        TextView tv_carmodel_name = (TextView) customView.findViewById(R.id.tv_carmodel_name);
//        TextView tv_each_free_time = (TextView) customView.findViewById(R.id.tv_each_free_time);
//        TextView tv_first_price = (TextView) customView.findViewById(R.id.tv_first_price);
//        TextView tv_first_time = (TextView) customView.findViewById(R.id.tv_first_time);
//        TextView tv_continued_price = (TextView) customView.findViewById(R.id.tv_continued_price);
//        TextView tv_continued_time = (TextView) customView.findViewById(R.id.tv_continued_time);
//        TextView tv_electricity = (TextView) customView.findViewById(R.id.tv_electricity);
//        TextView tv_mileage= (TextView) customView.findViewById(R.id.tv_mileage);
//        LinearLayout ll_ebike = (LinearLayout) customView.findViewById(R.id.ll_ebike);
//        LinearLayout ll_change_car = (LinearLayout) customView.findViewById(R.id.ll_change_car);
//        LinearLayout ll_rent = (LinearLayout) customView.findViewById(R.id.ll_rent);

        LinearLayout ll_open_lock = (LinearLayout) customView.findViewById(R.id.ll_open_lock);

//        if(carmodel_id==2){
//            ll_ebike.setVisibility(View.VISIBLE);
//
//            tv_electricity.setText(electricity);
//            tv_mileage.setText(mileage);
//        }else{
//            ll_ebike.setVisibility(View.GONE);
//        }
//
//        tv_codenum.setText(codenum);
//        tv_carmodel_name.setText(carmodel_name);
//        tv_first_price.setText(first_price+"元");
//        tv_first_time.setText("/"+first_time+"分钟");
//        tv_continued_price.setText(continued_price+"元");
//        tv_continued_time.setText("/"+continued_time+"分钟");
//
//        if("0".equals(each_free_time)){
//            tv_each_free_time.setVisibility(View.GONE);
//        }else{
//            tv_each_free_time.setVisibility(View.VISIBLE);
//            tv_each_free_time.setText(each_free_time+"分钟免费");
//        }


//        iv_rent_cancelBtn.setOnClickListener(this);
//        ll_change_car.setOnClickListener(this);
//        ll_rent.setOnClickListener(this);

        ll_open_lock.setOnClickListener(this);

        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(activity);
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
        // 创建PopupWindow宽度和高度
        popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        //设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        Log.e("initmPopup===", "===");

//        if("5".equals(type)  || "6".equals(type)){
//            if(!SharedPreferencesUrls.getInstance().getBoolean("isKnow0", false)){
//                WindowManager windowManager = activity.getWindowManager();
//                Display display = windowManager.getDefaultDisplay();
//                WindowManager.LayoutParams lp = advDialog0.getWindow().getAttributes();
//                lp.width = (int) (display.getWidth() * 1);
//                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                advDialog0.getWindow().setBackgroundDrawableResource(R.color.transparent);
//                advDialog0.getWindow().setAttributes(lp);
//                advDialog0.show();
//            }
//        }
    }

    public void initmPopupRentWindowView2(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_rent_ebike, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_rent_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_rent_back);
//        ImageView iv_rent_cancelBtn = (ImageView) customView.findViewById(R.id.iv_rent_cancelBtn);
//        TextView tv_codenum = (TextView) customView.findViewById(R.id.tv_codenum);
//        TextView tv_carmodel_name = (TextView) customView.findViewById(R.id.tv_carmodel_name);
//        TextView tv_each_free_time = (TextView) customView.findViewById(R.id.tv_each_free_time);
//        TextView tv_first_price = (TextView) customView.findViewById(R.id.tv_first_price);
//        TextView tv_first_time = (TextView) customView.findViewById(R.id.tv_first_time);
//        TextView tv_continued_price = (TextView) customView.findViewById(R.id.tv_continued_price);
//        TextView tv_continued_time = (TextView) customView.findViewById(R.id.tv_continued_time);
//        TextView tv_electricity = (TextView) customView.findViewById(R.id.tv_electricity);
//        TextView tv_mileage= (TextView) customView.findViewById(R.id.tv_mileage);
//        LinearLayout ll_ebike = (LinearLayout) customView.findViewById(R.id.ll_ebike);
//        LinearLayout ll_change_car = (LinearLayout) customView.findViewById(R.id.ll_change_car);
//        LinearLayout ll_rent = (LinearLayout) customView.findViewById(R.id.ll_rent);

        LinearLayout ll_open_lock = (LinearLayout) customView.findViewById(R.id.ll_open_lock);

//        if(carmodel_id==2){
//            ll_ebike.setVisibility(View.VISIBLE);
//
//            tv_electricity.setText(electricity);
//            tv_mileage.setText(mileage);
//        }else{
//            ll_ebike.setVisibility(View.GONE);
//        }
//
//        tv_codenum.setText(codenum);
//        tv_carmodel_name.setText(carmodel_name);
//        tv_first_price.setText(first_price+"元");
//        tv_first_time.setText("/"+first_time+"分钟");
//        tv_continued_price.setText(continued_price+"元");
//        tv_continued_time.setText("/"+continued_time+"分钟");
//
//        if("0".equals(each_free_time)){
//            tv_each_free_time.setVisibility(View.GONE);
//        }else{
//            tv_each_free_time.setVisibility(View.VISIBLE);
//            tv_each_free_time.setText(each_free_time+"分钟免费");
//        }


//        iv_rent_cancelBtn.setOnClickListener(this);
//        ll_change_car.setOnClickListener(this);
//        ll_rent.setOnClickListener(this);

        ll_open_lock.setOnClickListener(this);

        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(activity);
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
        // 创建PopupWindow宽度和高度
        popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        //设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        Log.e("initmPopup===", "===");

    }

    public void initmPopupWindowView(){

        // 获取自定义布局文件的视图
//        View customView = getLayoutInflater().inflate(R.layout.pop_menu, null, false);

        View customView = LayoutInflater.from(getContext()).inflate(R.layout.pop_menu, null, false);

        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_win_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_back);
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(activity);
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
        // 创建PopupWindow宽度和高度
//        popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        /**
         * 设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
         */
        popupwindow.setContentView(customView);
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setFocusable(true);
        popupwindow.setOutsideTouchable(true);
        popupwindow.setBackgroundDrawable(new BitmapDrawable());

//        customView.setFocusable(true);
//        customView.setFocusableInTouchMode(true);

//        TextView tv_codenum = (TextView)customView.findViewById(R.id.pop_menu_codenum);
//        TextView tv_quantity = (TextView)customView.findViewById(R.id.pop_menu_quantity);
//        LinearLayout findBikeLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_findBike);
//        LinearLayout openPowerLockLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_openPowerLock);
//        TextView cancleBtn = (TextView)customView.findViewById(R.id.pop_menu_cancleBtn);
//
//        Log.e("initmPopup===", codenum+"==="+quantity);
//
//        tv_codenum.setText(codenum);
//        tv_quantity.setText("电量："+quantity);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
//                    case R.id.pop_menu_findBike:
//                        ddSearch();
//                        break;
//                    case R.id.pop_menu_openPowerLock:
//                        battery_unlock();
//                        break;
//                    case R.id.pop_menu_cancleBtn:
//                        popupwindow.dismiss();
//
//                        initNearby(latitude, longitude);
//
//                        break;
                }

            }
        };

//        findBikeLayout.setOnClickListener(listener);
//        openPowerLockLayout.setOnClickListener(listener);
//        cancleBtn.setOnClickListener(listener);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 改变显示的按钮图片为正常状态
                Log.e("onDismiss===", "===");

                initNearby(latitude, longitude);
            }
        });

    }

//    @Override
//    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (popupWindow != null && popupWindow .isShowing()) {
//                popupWindow.dismiss();
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public void onBackPressed() {
//        bleService.artifClose();
//        super.onBackPressed();
//        //Toast.makeText(FDQControlAct.this, "onBackPessed", Toast.LENGTH_SHORT).show();
//    }

    private void ddSearch(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("tokencode",codenum);
            HttpHelper.post(context, Urls.ddSearch, params, new TextHttpResponseHandler() {
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
                    UIHelper.ToastError(context, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"发送寻车指令成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

    void battery_unlock() {

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("tokencode",codenum);
            HttpHelper.post(context, Urls.battery_unlock, params, new TextHttpResponseHandler() {
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
                    UIHelper.ToastError(context, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            curMarker.setIcon(bikeDescripter_blue);

                            Toast.makeText(context,"已发送指令",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

    private void initHttp(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)){
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            HttpHelper.get(context, Urls.userIndex, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在加载");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onFailureCommon(throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                                if (result.getFlag().equals("Success")) {

                                    UserIndexBean bean = JSON.parseObject(result.getData(), UserIndexBean.class);
//                            nameEdit.setText(bean.getRealname());
//                            phoneNum.setText(bean.getTelphone());
//                            nickNameEdit.setText(bean.getNickname());
//                            sexText.setText(bean.getSex());
//                            schoolText.setText(bean.getSchool());
//                            classEdit.setText(bean.getGrade());
//                            stuNumEdit.setText(bean.getStunum());
//
//                            sex = bean.getSex();
                                    String school = bean.getSchool();

                                    Log.e("initHttp===", "==="+school);

                                    if("江苏理工学院".equals(school) || "泰山医学院".equals(school) || "中国矿业大学（南湖校区）".equals(school) || "河南财经政法大学".equals(school)){
                                        carmodel_id = 2;
                                        switcher_type.setChecked(true);
                                    }else{
                                        carmodel_id = 1;
                                        switcher_type.setChecked(false);
                                    }

                                    initNearby(latitude, longitude);

                                } else {
                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        }else {
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
        }
    }

    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    break;

                case 1:
                    if (popupwindow == null || (popupwindow != null && !popupwindow.isShowing())) {
                        initNearby(latitude, longitude);
                    }

                    break;

                case 0x98://搜索超时

                    Log.e("0x98===", isLookPsdBtn+"==="+isAgain+"==="+isOpenLock+"==="+isEndBtn);

//                    ClientManager.getClient().stopSearch();
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//                    ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//                    ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);

                    if(!isLookPsdBtn){
                        SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
                                .searchBluetoothLeDevice(0)
                                .build();


                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Log.e("usecar===1", "===");

                            break;
                        }


                        ClientManager.getClient().search(request, mSearchResponse);

                        connectDeviceLP();
                        ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                        ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
                    }else{
                        getStateLP(m_nowMac);
                    }

//                    m_myHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!isStop){
//                                closeLoadingDialog();
//
//                                Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！",Toast.LENGTH_LONG).show();
//
//                                ClientManager.getClient().stopSearch();
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().disconnect(m_nowMac);
//                                ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//
//                                if(popupwindow!=null){
//                                    popupwindow.dismiss();
//                                }
//
//                                car_notification(isOpenLock?1:isAgain?2:isEndBtn?3:0, 2, 0);
//
//                            }
//                        }
//                    }, 10 * 1000);
                    break;

                default:
                    break;
            }
            return false;
        }
    });


    private void initNearby(double latitude, double longitude){
        RequestParams params = new RequestParams();
        params.put("latitude",latitude);
        params.put("longitude",longitude);

        Log.e("initNearby===", latitude+"==="+carmodel_id);

        if(carmodel_id==1){
            params.put("type", 1);
            HttpHelper.get(context, Urls.nearby, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在加载");
                        loadingDialog.show();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            JSONArray array = new JSONArray(result.getData());

                            Log.e("initNearby===Bike", "==="+array.length());

                            for (Marker marker : bikeMarkerList){
                                if (marker != null){
                                    marker.remove();
                                }
                            }
                            if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
                                bikeMarkerList.clear();
                            }
                            if (0 == array.length()){
                                ToastUtils.showMessage("附近没有单车");
                            } else {
                                for (int i = 0; i < array.length(); i++){
                                    NearbyBean bean = JSON.parseObject(array.getJSONObject(i).toString(), NearbyBean.class);
                                    // 加入自定义标签
                                    MarkerOptions bikeMarkerOption = new MarkerOptions().position(new LatLng(
                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude()))).icon(bikeDescripter);
                                    Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
                                    bikeMarkerList.add(bikeMarker);
                                }
                            }
                        } else {
                            ToastUtils.showMessage(result.getMsg());
                        }
                    } catch (Exception e) {

                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }else{
            String uid = SharedPreferencesUrls.getInstance().getString("uid","");
            String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
            if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                UIHelper.goToAct(context, LoginActivity.class);
            }else {
                params.put("uid",uid);
                params.put("access_token",access_token);

                HttpHelper.get(context, Urls.nearbyEbikeScool, params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingDialog != null && !loadingDialog.isShowing()) {
                                    loadingDialog.setTitle("正在加载");
                                    loadingDialog.show();
                                }
                            }
                        });
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {

                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                                UIHelper.ToastError(context, throwable.toString());
                            }
                        });
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                    Log.e("nearbyEbikeScool===", "==="+responseString);

                                    if (result.getFlag().equals("Success")) {
                                        JSONArray array = new JSONArray(result.getData());

                                        if (curMarker != null){
                                            curMarker.remove();
                                        }

                                        for (Marker marker : bikeMarkerList){
                                            if (marker != null){
                                                marker.remove();
                                            }
                                        }
                                        if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
                                            bikeMarkerList.clear();
                                        }
                                        if (0 == array.length()){
                                            Toast.makeText(context,"附近没有电单车",Toast.LENGTH_SHORT).show();
                                        }else {
                                            for (int i = 0; i < array.length(); i++){
                                                NearbyBean bean = JSON.parseObject(array.getJSONObject(i).toString(), NearbyBean.class);
                                                // 加入自定义标签

                                                MarkerOptions bikeMarkerOption = null;
                                                if("4".equals(bean.getType())){
                                                    bikeMarkerOption = new MarkerOptions().title(bean.getCodenum()+"-"+bean.getQuantity()+"%").position(new LatLng(
                                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
                                                            .icon("1".equals(bean.getQuantity_level())?bikeDescripter_green:"2".equals(bean.getQuantity_level())?bikeDescripter_yellow:"3".equals(bean.getQuantity_level())?bikeDescripter_red:"4".equals(bean.getQuantity_level())?bikeDescripter_blue:bikeDescripter_brown);

                                                }else{
                                                    bikeMarkerOption = new MarkerOptions().title(bean.getCodenum()+"-"+bean.getQuantity()+"%").position(new LatLng(
                                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
                                                            .icon("1".equals(bean.getQuantity_level())?bikeDescripter_xa_green:"2".equals(bean.getQuantity_level())?bikeDescripter_xa_yellow:"3".equals(bean.getQuantity_level())?bikeDescripter_xa_red:"4".equals(bean.getQuantity_level())?bikeDescripter_xa_blue:bikeDescripter_xa_brown);
                                                }

                                                if(switcher.isChecked() || switcher_bad.isChecked()){
                                                    if(switcher.isChecked()){
                                                        if("2".equals(bean.getQuantity_level()) || "3".equals(bean.getQuantity_level()) || "4".equals(bean.getQuantity_level())){
                                                            Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
                                                            bikeMarkerList.add(bikeMarker);
                                                        }
                                                    }

                                                    if(switcher_bad.isChecked()){
                                                        if("6".equals(bean.getQuantity_level())){
                                                            Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
                                                            bikeMarkerList.add(bikeMarker);
                                                        }
                                                    }
                                                }else{

                                                    Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
                                                    bikeMarkerList.add(bikeMarker);
                                                }

                                                if(bean.getQuantity_level_2_count_xyt() != null){
                                                    tvYellow_xyt.setText("黄色："+bean.getQuantity_level_2_count_xyt());
                                                }
                                                if(bean.getQuantity_level_3_count_xyt() != null){
                                                    tvRed_xyt.setText("红色："+bean.getQuantity_level_3_count_xyt());
                                                }

                                                if(bean.getQuantity_level_2_count_xa() != null){
                                                    tvYellow_xa.setText("黄色："+bean.getQuantity_level_2_count_xa());
                                                }
                                                if(bean.getQuantity_level_3_count_xa() != null){
                                                    tvRed_xa.setText("红色："+bean.getQuantity_level_3_count_xa());
                                                }

//                                                Log.e("nearbyEbike===", bean.getCodenum()+"==="+bean.getType()+"==="+bean.getQuantity()+"==="+bean.getQuantity_level());

//                                    if("80001651".equals(bean.getCodenum())){
//                                        Log.e("initNearby===", bean.getQuantity()+"==="+bean.getQuantity_level());
//                                    }

                                            }
                                        }
                                    } else {
                                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                }
                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            }
                        });

                    }
                });
            }

        }
    }

    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }





    @Override
    public void onPause() {
        super.onPause();
//        mapView.onPause();
//        deactivate();
//		mFirstFix = false;
    }
    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }

        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        deactivate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (mapView != null) mapView.onDestroy();
    }


    private void addChooseMarker() {
        // 加入自定义标签
        Log.e("addChooseMarker===", mapView+"==="+myLocation);

//        MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation).icon(successDescripter);
//        centerMarker = aMap.addMarker(centerMarkerOption);
//        centerMarker.setPositionByPixels(mapView.getWidth() / 2, mapView.getHeight() / 2);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

//                CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
//                aMap.animateCamera(update);

//                CameraUpdate update = CameraUpdateFactory.zoomTo(18f);
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, aMap.getCameraPosition().zoom));
                CameraUpdate update = CameraUpdateFactory.zoomTo(leveltemp);
                aMap.animateCamera(update, 1000, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        Log.e("animateCamera===", "===");
                        aMap.setOnCameraChangeListener(ScanFragment.this);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        }, 1000);
    }

    private void setMovingMarker() {
        if (isMovingMarker)
            return;

        isMovingMarker = true;
//        centerMarker.setPositionByPixels(mapView.getWidth() / 2, mapView.getHeight() / 2);
//        centerMarker.setIcon(successDescripter);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (centerMarker != null) {
            setMovingMarker();
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        leveltemp = aMap.getCameraPosition().zoom;

        Log.e("onCameraChangeF===", isUp+"==="+cameraPosition.target.latitude);
        if (isUp){

            initNearby(cameraPosition.target.latitude, cameraPosition.target.longitude);
            if (centerMarker != null) {
                animMarker();
            }
        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE
                || motionEvent.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
            isUp = true;
        }else {
            isUp = false;
        }
    }


//    private void showTab(int idx){
//         for(int i = 0; i < fragments.size(); i++){
//                 Fragment fragment = fragments.get(i);
//                 FragmentTransaction ft = obtainFragmentTransaction(idx);
//
//                 if(idx == i){
//                         ft.show(fragment);
//                     }else{
//                         ft.hide(fragment);
//                     }
//                 ft.commit();
//             }
//         currentTab = idx; // 更新目标tab为当前tab
//     }
//
//    private FragmentTransaction obtainFragmentTransaction(int index){
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        // 设置切换动画
//        if(index > currentTab){
//             ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
//         }else{
//             ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
//         }
//        return ft;
//    }


    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.mainUI_msg:
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    UIHelper.goToAct(activity, LoginActivity.class);
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                }else {
                    ((MainActivity)getActivity()).changeTab(1);

//                    MainActivity.changeTab(4);
                }


                break;

            case R.id.mainUI_leftBtn:
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    UIHelper.goToAct(context,LoginActivity.class);
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    return;
                }
                UIHelper.goToAct(context, DeviceSelectActivity.class);
                break;

            case R.id.mainUI_rightBtn:
                if ("登录".equals(rightBtn.getText().toString().trim())){
                    UIHelper.goToAct(context, LoginActivity.class);
                }else {
                    SharedPreferencesUrls.getInstance().putString("uid","");
                    SharedPreferencesUrls.getInstance().putString("access_token","");
                    Toast.makeText(context,"登出登录成功",Toast.LENGTH_SHORT).show();
                    rightBtn.setText("登录");
                }
                break;

            case R.id.switcher:
//                SharedPreferencesUrls.getInstance().putBoolean("switcher", switcher.isChecked());

                if(switcher.isChecked()){
                    Log.e("biking===switcher1", "onClick==="+switcher.isChecked());
                }else{
                    Log.e("biking===switcher2", "onClick==="+switcher.isChecked());
                }

                initNearby(latitude, longitude);

                break;

            case R.id.switcher_bad:
//                SharedPreferencesUrls.getInstance().putBoolean("switcher", switcher.isChecked());

                if(switcher_bad.isChecked()){
                    Log.e("biking===switcher_bad1", "onClick==="+switcher_bad.isChecked());
                }else{
                    Log.e("biking===switcher_bad2", "onClick==="+switcher_bad.isChecked());
                }

                initNearby(latitude, longitude);

                break;

            case R.id.switcher_type:
//                SharedPreferencesUrls.getInstance().putBoolean("switcher", switcher.isChecked());

                if(switcher_type.isChecked()){
                    Log.e("biking===switcher_type1", "onClick==="+switcher_type.isChecked());

                    carmodel_id = 2;
                }else{
                    Log.e("biking===switcher_type2", "onClick==="+switcher_type.isChecked());

                    carmodel_id = 1;
                }

                initNearby(latitude, longitude);

                break;

            case R.id.mainUI_myLocationLayout:
                if (myLocation != null) {
                    CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
                    aMap.animateCamera(update);
                }
//
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, aMap.getCameraPosition().zoom));

//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18f));

//                aMap.moveCamera(CameraUpdateFactory.zoomTo(18f));


                initmPopupRentWindowView();
//                initmPopupRentWindowView2();
//                initmPopupWindowView();

                break;

            case R.id.mainUI_getDotLayout:
//                UIHelper.goToAct(context, DotSelectActivity.class);
                break;

            case R.id.mainUI_testXALayout:
                UIHelper.goToAct(context, TestXiaoanActivity.class);
                break;

            case R.id.mainUI_scanCode_lock:
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    UIHelper.goToAct(context,LoginActivity.class);
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        int checkPermission = context.checkSelfPermission(Manifest.permission.CAMERA);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
                            } else {
                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        ScanFragment.this.requestPermissions(
                                                new String[] { Manifest.permission.CAMERA },
                                                100);
                                    }
                                });
                                customBuilder.create().show();
                            }
                            return;
                        }
                    }
                    try {
                        SharedPreferencesUrls.getInstance().putString("type", "");

                        Intent intent = new Intent();
                        intent.setClass(context, ActivityScanerCode.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("isChangeKey",false);
                        startActivityForResult(intent, 101);
                    } catch (Exception e) {
                        UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
                    }
                }
                break;

            case R.id.ll_open_lock:



                open_lock();


                break;

            default:
                break;
        }
    }

    private void open_lock(){
        type = "2";
        m_nowMac = "C8:FD:19:68:2F:90";     //40004690

//        type = "3";
//        m_nowMac = "DF:FF:96:62:68:BB";     //60009090

//        type = "6";
////                m_nowMac = "3C:A3:08:AF:02:C3";     //30005053
////                lock_no = "LPKMIrwLD";
//        m_nowMac = "A4:34:F1:7B:BF:9A";     //30005060
//        lock_no = "GpDTxe7<a";

        if ("1".equals(type)) {          //单车机械锁
//                    UIHelper.goToAct(context, CurRoadStartActivity.class);
//                    popupwindow.dismiss();
        } else if ("2".equals(type) || "3".equals(type)) {    //单车蓝牙锁

            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                popupwindow.dismiss();
            }
            //蓝牙锁
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null) {
                ToastUtil.showMessageApp(context, "获取蓝牙失败");
                popupwindow.dismiss();
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                isPermission = false;
                closeLoadingDialog2();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);
            } else {

                Log.e("order===2",  "===" + type);

//                        if (!TextUtils.isEmpty(m_nowMac)) {
//                            isOpenLock = true;
//
//                            if(isMac){
//                                connect();
//                            }else{
//                                setScanRule();
//                                scan2();
//                            }
//                        }

                BleManager.getInstance().init(activity.getApplication());
                BleManager.getInstance()
                        .enableLog(true)
                        .setReConnectCount(10, 5000)
                        .setConnectOverTime(timeout)
                        .setOperateTimeout(10000);

                connect();
            }
        }else if ("4".equals(type)) {

            unlock();

            //TODO  2
//                                if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                                    ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                                    popupwindow.dismiss();
//                                }
//                                BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//                                mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                                BLEService.bluetoothAdapter = mBluetoothAdapter;
//
//                                bleService.view = context;
//                                bleService.showValue = true;
//
//                                if (mBluetoothAdapter == null) {
//                                    ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                                    popupwindow.dismiss();
//                                    return;
//                                }
//                                if (!mBluetoothAdapter.isEnabled()) {
//                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                                    startActivityForResult(enableBtIntent, 188);
//                                } else {
//                                    Log.e("mf===4_1", bleid + "==="+m_nowMac);
//
//                                    bleService.connect(m_nowMac);
//
//                                    checkConnect();
//                                }

        }else if ("5".equals(type) || "6".equals(type)) {      //泺平单车蓝牙锁

            Log.e("mf===5_1", deviceuuid + "==="+m_nowMac);

//                    if(BaseApplication.getInstance().isTest()){
//                        if("40001101".equals(codenum)){
////                                                  m_nowMac = "3C:A3:08:AE:BE:24";
//                            m_nowMac = "3C:A3:08:CD:9F:47";
//                        }else if("50007528".equals(codenum)){
//
//                        }else{
//                            type = "6";
//                            m_nowMac = "A4:34:F1:7B:BF:9A";
//                        }
//                    }


            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                popupwindow.dismiss();
            }
            //蓝牙锁
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null) {
                ToastUtil.showMessageApp(context, "获取蓝牙失败");
                popupwindow.dismiss();
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                isPermission = false;
                closeLoadingDialog2();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);
            } else {
//                      iv_help.setVisibility(View.VISIBLE);

                m_myHandler.sendEmptyMessage(0x98);

            }
        }else if ("7".equals(type)) {
            Log.e("mf===7_1", deviceuuid + "==="+m_nowMac);

//                                unlock();

            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
                popupwindow.dismiss();
            }
            //蓝牙锁
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null) {
                ToastUtil.showMessageApp(context, "获取蓝牙失败");
                popupwindow.dismiss();
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                isPermission = false;
                closeLoadingDialog2();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 188);
            } else {
                XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                builder.setBleStateChangeListener(ScanFragment.this);
                builder.setScanResultCallback(ScanFragment.this);
                apiClient = builder.build();

                ScanFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(ScanFragment.this, deviceuuid);

                isConnect = false;
                m_myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isConnect){
                            closeLoadingDialog();

                            Log.e("mf===7==timeout", isConnect + "==="+activity.isFinishing());

                            unlock();
                        }
                    }
                }, timeout);
            }
        }
    }

    private void connect(){
//        loadingDialog = DialogUtils.getLoadingDialog(this, "正在连接...");
//        loadingDialog.setTitle("正在连接");
//        loadingDialog.show();

        Log.e("connect===", carmodel_id+"==="+type+"==="+isLookPsdBtn);

        BleManager.getInstance().cancelScan();

        BleManager.getInstance().connect(m_nowMac, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.e("onStartConnect===", "===");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Log.e("onConnectFail===", bleDevice.getMac()+"==="+exception);

                isLookPsdBtn = false;

                closeLoadingDialog2();

                if (!isLookPsdBtn){
//                    BaseApplication.getInstance().getIBLE().stopScan();
//                    BaseApplication.getInstance().getIBLE().refreshCache();
//                    BaseApplication.getInstance().getIBLE().close();
//                    BaseApplication.getInstance().getIBLE().disconnect();

                    Log.e("0x99===timeout", isLookPsdBtn+"==="+isStop+"==="+type);

                    if("3".equals(type)){

//                        unlock();     //TODO
                    }else{
                        Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！",Toast.LENGTH_LONG).show();
//                        car_notification(isOpenLock?1:isAgain?2:isEndBtn?3:0, 2, 0);      //TODO
                        if(popupwindow!=null){
                            popupwindow.dismiss();
                        }
                    }

                }

            }

            @Override
            public void onConnectSuccess(BleDevice device, BluetoothGatt gatt, int status) {
//                if (loadingDialog != null && loadingDialog.isShowing()) {
//                    loadingDialog.dismiss();
//                }

                isLookPsdBtn = true;
                bleDevice = device;

//                BleManager.getInstance().cancelScan();

                Log.e("onConnectSuccess===", bleDevice.getMac()+"===");
//                Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();


                m_myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        getBleToken();

                    }
                }, 500);

                BleManager.getInstance().notify(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f6-0000-1000-8000-00805f9b34fb", new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Log.e("onNotifySuccess===", "===");
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Log.e("onNotifyFailure===", "===");
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
//                            byte[] values = characteristic.getValue();

                        Log.e("onCharacteristicChanged", "===0");


                        byte[] x = new byte[16];
                        System.arraycopy(data, 0, x, 0, 16);

                        byte[] mingwen = EncryptUtils.Decrypt(x, Config.newKey);    //060207FE02433001010606D41FC9553C  FE024330 01 01 06

                        Log.e("onCharacteristicChanged", x.length+"==="+ ConvertUtils.bytes2HexString(data)+"==="+ConvertUtils.bytes2HexString(mingwen));

                        String s1 = ConvertUtils.bytes2HexString(mingwen);

                        if(s1.startsWith("0602")){      //获取token

                            token = s1.substring(6, 14);    //0602070C0580E001010406C8D6DC1949
                            GlobalParameterUtils.getInstance().setToken(ConvertUtils.hexString2Bytes(token));

//                            String tvAgain = tv_againBtn.getText().toString().trim();

                            Log.e("token===", token+"==="+s1);

                            openLock();

                        }else if(s1.startsWith("0502")){    //开锁
                            Log.e("openLock===", "==="+s1);

                            Toast.makeText(context, "开锁成功", Toast.LENGTH_LONG).show();

                            closeLoadingDialog();
//                            m_myHandler.sendEmptyMessage(7);
                        }else if(s1.startsWith("0508")){   //关锁==050801RET：RET取值如下：0x00，锁关闭成功。0x01，锁关闭失败。0x02，锁关闭异常。
                            if ("00".equals(s1.substring(6, 8))) {
                                ToastUtil.showMessageApp(context,"关锁成功");
                                Log.e("closeLock===suc", "===");

                            } else {
                                ToastUtil.showMessageApp(context,"关锁失败");
                                Log.e("closeLock===fail", "===");
                            }
                        }else if(s1.startsWith("050F")){   //锁状态
                            Log.e("lockState===0", "==="+s1);


                            isStop = true;
                            isLookPsdBtn = true;

                            closeLoadingDialog();

//                            查询锁开关状态==050F:0x00表示开启状态；0x01表示关闭状态。
                            if ("01".equals(s1.substring(6, 8))) {
                                ToastUtil.showMessageApp(context,"锁已关闭");
                                Log.e("closeLock===1", "锁已关闭==="+first3);

                            } else {
                                //锁已开启
                                ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");

//                                car_notification(3, 5, 0);    //TODO

                                isEndBtn = false;
                            }

                        }else if(s1.startsWith("058502")){

                            Log.e("xinbiao===", "当前操作：搜索信标成功"+s1.substring(2*10, 2*10+2)+"==="+s1.substring(2*11, 2*11+2)+"==="+s1);

                            if("000000000000".equals(s1.substring(2*4, 2*10))){
                                major = 0;
                            }else{
                                major = 1;
                            }

                        }


//                        EventBus.getDefault().post(BleNotifyEvent(decode));

//                        else if(s1.startsWith("050F")){
//                            Log.e("closeLock===2", "==="+s1);        //050F0101017A0020782400200F690300
//
////                            if("01".equals(s1.substring(6, 8))){
////                                Toast.makeText(context, "锁已关闭", Toast.LENGTH_LONG).show();
////                            }else{
////                                Toast.makeText(context, "锁已打开", Toast.LENGTH_LONG).show();
////                            }
//
//                            isStop = true;
//                            isLookPsdBtn = true;
//
//                            closeLoadingDialog2();
//
//                            if ("01".equals(s1.substring(6, 8))) {
//                                ToastUtil.showMessageApp(context,"锁已关闭");
//                                Log.e("biking===", "biking===锁已关闭==="+first3);
//
//                                if(!isEndBtn) return;
//
//                                m_myHandler.sendEmptyMessage(6);
//                            } else {
//                                //锁已开启
//                                ToastUtil.showMessageApp(context,"车锁未关，请手动关锁");
//                            }
//                        }

                    }
                });
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {

                isLookPsdBtn = false;
                Log.e("connect=onDisConnected", "==="+isActiveDisConnected);

//                    if (isActiveDisConnected) {
//                        Toast.makeText(MainActivity.this, getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(MainActivity.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
//                        ObserverManager.getInstance().notifyObserver(bleDevice);
//                    }

            }
        });
    }

    private void getBleToken(){
        String s = new GetTokenTxOrder().generateString();  //06010101490E602E46311640422E5238

        Log.e("getBleToken===1", "==="+s);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("getBleToken==onWriteSuc", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("getBleToken=onWriteFail", "==="+exception);
            }
        });
    }

    private void getLockStatus(){
        String s = new GetLockStatusTxOrder().generateString();  //06010101490E602E46311640422E5238

        Log.e("getLockStatus===1", "==="+isLookPsdBtn);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("getLockStatus==onWriteS", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("getLockStatus=onWriteFa", "==="+exception);
            }
        });
    }

    private void getXinbiao(){
        String s = new XinbiaoTxOrder().generateString();  //06010101490E602E46311640422E5238

        Log.e("getXinbiao===1", "==="+s);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("getXinbiao==onWriteS", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("getXinbiao=onWriteFa", "==="+exception);
            }
        });
    }

    private void openLock() {
        String s = new OpenLockTxOrder().generateString();

//        s= s.substring(0, 18) + token + s.substring(26, 32);

        Log.e("onWriteSuccess===1", token+"==="+s);     //989C064A===050106323031373135989C064A750217

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("onWriteSuccess===a", current+"==="+total+"==="+justWrite);
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("onWriteFailure===a", "==="+exception);
            }
        });
    }

    //泺平===连接设备
    private void connectDeviceLP() {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(0)
                .setConnectTimeout(timeout)
                .setServiceDiscoverRetry(1)
                .setServiceDiscoverTimeout(10000)
                .setEnableNotifyRetry(1)
                .setEnableNotifyTimeout(10000)
                .build();

        ClientManager.getClient().connect(m_nowMac, options, new IConnectResponse() {
            @Override
            public void onResponseFail(int code) {
                isStop = false;
                isLookPsdBtn = false;

                Log.e("connectDeviceLP===", "Fail==="+Code.toString(code));
//                ToastUtil.showMessageApp(context, Code.toString(code));

//                closeLoadingDialog();
//                if (loadingDialogWithHelp != null && loadingDialogWithHelp.isShowing()){
//                    loadingDialogWithHelp.dismiss();
//                }

                if(popupwindow!=null){
                    popupwindow.dismiss();
                }

                Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！",Toast.LENGTH_LONG).show();
//                car_notification(isOpenLock?1:isAgain?2:isEndBtn?3:0, 2, 0);  //TODO

            }

            @Override
            public void onResponseSuccess(BleGattProfile profile) {
//                BluetoothLog.v(String.format("profile:\n%s", profile));
//                refreshData(true);

                isStop = true;
                isLookPsdBtn = true;

                Log.e("connectDeviceLP===", "Success==="+profile);


                getStateLP(m_nowMac);

            }
        });
    }



    //泺平
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            Log.e("scan===","DeviceListActivity.onSearchStarted");
//            mDevices.clear();
//            mAdapter.notifyDataChanged();
        }

        @Override
        public void onDeviceFounded(final SearchResult device) {

            Log.e("scan===onDeviceFounded",device.device.getName() + "===" + device.device.getAddress());

//            bike:GpDTxe8DGN412
//            bike:LUPFKsrUyR405
//            bike:LUPFKsrUyK405
//            bike:L6OsRAiviK289===E8:EB:11:02:2B:E2

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(m_nowMac.equals(device.device.getAddress())){

                        Log.e("scan===stop",device.device.getName() + "===" + device.device.getAddress());

                        ClientManager.getClient().stopSearch();

//                        connectDeviceLP();
//
//                        ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                    }
                }
            });

//            if (!mDevices.contains(device)) {
//                mDevices.add(device);
//                mAdapter.notifyItemInserted(mDevices.size());
//            } else {
//                int index = mDevices.indexOf(device);
//                mDevices.set(index, device);
//
//                if (StringUtils.checkBikeTag(device.getName())) {
//                    mAdapter.notifyDataChanged();
//                } else {
//                    mAdapter.notifyItemChanged(index);
//                }
//            }
        }

        @Override
        public void onSearchStopped() {
            Log.e("scan===","DeviceListActivity.onSearchStopped");

        }

        @Override
        public void onSearchCanceled() {
            Log.e("scan===","DeviceListActivity.onSearchCanceled");

        }
    };

    ////泺平===监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(final String mac, final int status) {

//            Log.e("ConnectStatus===", mac+"===="+(status == STATUS_CONNECTED));

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("ConnectStatus===biking", isLookPsdBtn+"==="+mac+"==="+(status == STATUS_CONNECTED)+"==="+m_nowMac);

                    if(status == STATUS_CONNECTED){
                        isLookPsdBtn = true;

//                        ToastUtil.showMessageApp(context,"设备连接成功");
                    }else{
                        isLookPsdBtn = false;

//                        ToastUtil.showMessageApp(context,"设备断开连接");
                    }

//                    connectDeviceIfNeeded();
                }
            });

            if(status != STATUS_CONNECTED){
                return;
            }

            ClientManager.getClient().stopSearch();

//            getStateLP(mac);


//            Globals.isBleConnected = mConnected = (status == STATUS_CONNECTED);
//            refreshData(mConnected);
//            connectDeviceIfNeeded();
        }
    };

    //监听锁关闭事件
    private final ICloseListener mCloseListener = new ICloseListener() {
        @Override
        public void onNotifyClose() {

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("onNotifyClose===", "====");

                    ToastUtil.showMessageApp(context,"锁已关闭");

//                    if("6".equals(type)){
//                        lookPsdBtn.setText("再次开锁");
//                        SharedPreferencesUrls.getInstance().putString("tempStat","1");
//                    }

                    getBleRecord2();

//                    ClientManager.getClient().disconnect(m_nowMac);
//                    ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//                    ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
                }
            });
        }
    };

    //与设备，获取记录
    private void getBleRecord2() {
        ClientManager.getClient().getRecord(m_nowMac, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, final String transType, final String mackey, String index, final int Major, final int Minor, String vol) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===###", transType+"==Major:"+ Major +"---Minor:"+Minor+"---mackey:"+mackey);

//                        if(BaseApplication.getInstance().isTestLog()){
////                            macText.setText(Major+"==="+Minor+"==="+macList);
//                        }

//                      ToastUtil.showMessageApp(context, "Major:"+ Major +"---Minor:"+Minor);

                        transtype = transType;
                        major = Major;
                        minor = Minor;

                        SharedPreferencesUrls.getInstance().putInt("major", major);

//                      m_myHandler.sendEmptyMessage(9);

                        deleteBleRecord2(bikeTradeNo);
                    }
                });


            }

            @Override
            public void onResponseSuccessEmpty() {
//                ToastUtil.showMessageApp(context, "record empty");
                Log.e("getBleRecord===", "Success===Empty");

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===fail", Code.toString(code));
                    }
                });
            }
        });
    }


    //与设备，删除记录
    private void deleteBleRecord2(String tradeNo) {
        ClientManager.getClient().deleteRecord(m_nowMac, tradeNo, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, final String transType, String mackey, String index, final int Major, final int Minor, String vol) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("deleteBleRecord2===", transType+"==Major:"+ Major +"---Minor:"+Minor);

                        transtype = transType;
                        major = Major;
                        minor = Minor;

                        deleteBleRecord2(bikeTradeNo);
                    }
                });
            }

            @Override
            public void onResponseSuccessEmpty() {
                Log.e("biking=deleteBleRecord2", "Success===Empty");

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("biking=deleteBleRecord2", Code.toString(code));
                    }
                });

            }
        });
    }

    private void getStateLP(String mac){
        ClientManager.getClient().getStatus(mac, new IGetStatusResponse() {
            @Override
            public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
//                    quantity = vol+"";

                Log.e("getStatus===", "===="+macKey);
                keySource = keySerial;

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("mf===getStatus1", "==="+isEndBtn);

//                                    getBleRecord();

                        rent();


                        Log.e("scan===", "scan===="+loadingDialog);

                    }
                });
            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getStatus===", Code.toString(code));
//                            ToastUtil.showMessageApp(context, Code.toString(code));
                    }
                });
            }

        });
    }

    //泺平_开锁
    protected void rent(){

        Log.e("rent===000",lock_no+"==="+m_nowMac+"==="+keySource);

        RequestParams params = new RequestParams();
        params.put("lock_no", lock_no);
        params.put("keySource", keySource);
        HttpHelper.get(context, Urls.rent, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                onStartCommon("正在提交");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("rent===","==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            KeyBean bean = JSON.parseObject(result.getData(), KeyBean.class);

//                            if (loadingDialog != null && loadingDialog.isShowing()){
//                                loadingDialog.dismiss();
//                            }

                            encryptionKey = bean.getEncryptionKey();
                            keys = bean.getKeys();
                            serverTime = bean.getServerTime();

                            Log.e("rent===", m_nowMac+"==="+encryptionKey+"==="+keys);

//                                getBleRecord();

//                            iv_help.setVisibility(View.GONE);

                            openBleLock(null);

                        }catch (Exception e){
                            closeLoadingDialog();
                        }


                    }
                });



            }
        });
    }

    //泺平===与设备，开锁
    private void openBleLock(RRent.ResultBean resultBean) {
        Log.e("mf===openBleLock", serverTime+"==="+keys+"==="+encryptionKey);

        ClientManager.getClient().openLock(m_nowMac,"000000000000", (int) serverTime, keys, encryptionKey, new IEmptyResponse(){
            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("openLock===Fail", m_nowMac+"==="+Code.toString(code));

                        getBleRecord();

//                        car_notification(1, 3, 0);    //TODO

                    }
                });

            }

            @Override
            public void onResponseSuccess() {
                Log.e("openLock===Success", "===");

                getBleRecord();

                closeLoadingDialog();

                isFinish = true;

                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

//                car_notification(1, 1, 0);      //TODO


            }
        });
    }

    //泺平===与设备，获取记录
    private void getBleRecord() {

        Log.e("getBleRecord===", "###==="+m_nowMac);

        ClientManager.getClient().getRecord(m_nowMac, new IGetRecordResponse() {

            @Override
            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, int Major, int Minor, String vol) {
                Log.e("getBleRecord===0", transType + "==Major:"+ Major +"---Minor:"+Minor+"==="+bikeTradeNo);
                deleteBleRecord(bikeTradeNo);

//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//
//                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
//
//                SharedPreferencesUrls.getInstance().putBoolean("isStop",false);
//                SharedPreferencesUrls.getInstance().putString("m_nowMac", m_nowMac);
//                SharedPreferencesUrls.getInstance().putBoolean("switcher", false);
//                SharedPreferencesUrls.getInstance().putString("type", type);
//                SharedPreferencesUrls.getInstance().putString("tempStat","0");
//                SharedPreferencesUrls.getInstance().putString("bleid",bleid);
//
//                UIHelper.goToAct(context, CurRoadBikingActivity.class);
//                scrollToFinishActivity();
            }

            @Override
            public void onResponseSuccessEmpty() {
//                ToastUtil.showMessageApp(context, "record empty");
                Log.e("getBleRecord===1", "Success===Empty");
            }

            @Override
            public void onResponseFail(int code) {
                Log.e("getBleRecord===2", Code.toString(code));
//                ToastUtil.showMessageApp(context, Code.toString(code));
            }
        });
    }

    //泺平===与设备，删除记录
    private void deleteBleRecord(String tradeNo) {
//        UIHelper.showProgress(this, R.string.delete_bike_record);
        ClientManager.getClient().deleteRecord(m_nowMac, tradeNo, new IGetRecordResponse() {
//            @Override
//            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, String cap, String vol) {
//                Log.e("scan===deleteBleRecord", "Success===");
//                deleteBleRecord(bikeTradeNo);
//            }

            @Override
            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, int Major, int Minor, String vol) {
                Log.e("biking=deleteBleRecord", "Major:"+ Major +"---Minor:"+Minor);
                deleteBleRecord(bikeTradeNo);
            }

            @Override
            public void onResponseSuccessEmpty() {
//                UIHelper.dismiss();
                Log.e("scan===deleteBleRecord", "Success===Empty");

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoadingDialog();

                        isFinish = true;

                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

//                        car_notification(1, 1, 0);        //TODO
                    }
                });

            }

            @Override
            public void onResponseFail(int code) {
                Log.e("scan===deleteBleRecord", Code.toString(code));
//                ToastUtil.showMessageApp(context, Code.toString(code));
                popupwindow.dismiss();
            }
        });
    }


    //助力车关锁
    private void lock() {
        Log.e("mf===lock", isAgain+"===");


        RequestParams params = new RequestParams();
        params.put("action_type", isAgain?1:2); //操作类型 1临时上锁 2还车
//        if(!isAgain){
//            params.put("parking", parking());    //电子围栏json字符串 操作类型为还车时必传
//        }
//        params.put("longitude", referLongitude);   //0代表成功 1连接不上蓝牙 2蓝牙开锁超时 3网络开锁请求失败(接口无响应或异常) 4网络开锁超时（接口有响应但返回超时码） 5网络开锁失败
//        params.put("latitude", referLatitude);
//
//        //还车类型 操作类型为还车时必传 1手机gps在电子围栏 2锁gps在电子围栏 3信标 4锁与信标
//        if(!isAgain){
//            if(major!=0){
//                Log.e("mf===lock1", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
//                params.put("back_type", "4");     // 4锁与信标
//            }else if(isGPS_Lo){
//                Log.e("mf===lock2", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
//                params.put("back_type", "2");     // 2锁gps在电子围栏
//            }else if(macList.size() > 0){
//                Log.e("mf===lock3", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
//                params.put("back_type", "3");     // 3信标
//            }else if(force_backcar==1 && isTwo){
//                Log.e("mf===lock4", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
//                params.put("back_type", "5");     // 没锁第二次强制还车
//            }else{
////                  }else if(isContainsList.contains(true)){
//                Log.e("mf===lock5", major+"==="+macList+"==="+macList2+"==="+isContainsList.contains(true)+"==="+uid+"==="+access_token);
//                params.put("back_type", "1");     // 1手机gps在电子围栏
//            }
//        }

        HttpHelper.post(context, Urls.lock, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if(isAgain){

                }
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Log.e("mf===lock_fail", responseString + "===" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("mf===lock_1", carmodel_id + "===" + responseString + "===" + result.data);

                            ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");

//                            n=0;
//                            carLoopClose();

                        } catch (Exception e) {
                            closeLoadingDialog();
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });
    }

    //助力车开锁
    private void unlock() {
        Log.e("mf===unlock", "===");

        isOpenLock = false;

        HttpHelper.post(context, Urls.unlock, null, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("mf===unlock1", carmodel_id+ "===" + type + "===" + codenum + "===" + responseString + "===" + result.data);


                            popupwindow.dismiss();

//                            n=0;
//                            carLoopOpen();



                        } catch (Exception e) {
                            closeLoadingDialog();
//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                    }
                });
            }
        });
    }

    //助力车开锁_轮询
//    private void carLoopOpen() {
//        Log.e("mf===carLoopOpen", order_id2+"===" +order_id+"===" + "===" + codenum);
//
//        HttpHelper.get(context, Urls.order_detail+order_id2, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
////                onStartCommon("正在加载");
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.e("mf===carLoopOpen_fail", responseString + "===" + throwable.toString());
//                onFailureCommon(throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
//                m_myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//
//                            Log.e("mf===carLoopOpen1", responseString + "===" + result.data);
//
//                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);
//
//                            if(20 != bean.getOrder_state()){
//                                queryCarStatusOpen();
//                            }else{
////                                isConnect = true;
//
//                                closeLoadingDialog();
//
//                                ll_top_navi.setVisibility(View.GONE);
//                                ll_top.setVisibility(View.VISIBLE);
//                                rl_ad.setVisibility(View.GONE);
//                                ll_top_biking.setVisibility(View.VISIBLE);
//
//                                cyclingThread();
//                            }
//
//                        } catch (Exception e) {
////                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
//
//                            closeLoadingDialog();
//                        }
//
//                    }
//                });
//            }
//        });
//    }

    //助力车开锁_轮询
//    private void queryCarStatusOpen() {
//        if(n<5){
//            n++;
//
//            m_myHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e("mf===queryCarStatusOpen", "===");
//
//                    carLoopOpen();
//                }
//            }, 1 * 1000);
//        }else{
//            ToastUtil.showMessageApp(context, "开锁超时");
//
////            car_notification(1, 4, 0);w
//
//            closeLoadingDialog();
//        }
//    }

    //助力车关锁_轮询
//    private void carLoopClose() {
//        Log.e("mf===carLoopClose", order_id2+"===" +order_id+"===" + isAgain+"===" + codenum);
//
//        HttpHelper.get(context, Urls.order_detail+order_id2, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                if(isAgain){
//
//                }
//                onStartCommon("正在加载");
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.e("mf===carLoopClose_fail", responseString + "===" + throwable.toString());
//                onFailureCommon(throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
//                m_myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//
//                            Log.e("mf===carLoopClose1", responseString + "===" + result.data);
//
//                            OrderBean bean = JSON.parseObject(result.getData(), OrderBean.class);
//
//                            if(isAgain){
//                                closeLoadingDialog();
//                            }else{
//                                if(bean.getOrder_state() < 30){
//                                    queryCarStatusClose();
//                                }else{
////                                isConnect = true;
//
//                                    order_type = 1;
//                                    end();
//                                }
//                            }
//
//
//
//
//                        } catch (Exception e) {
////                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
//
//                            closeLoadingDialog();
//                        }
//
//                    }
//                });
//            }
//        });
//    }

    //助力车关锁_轮询2
//    private void queryCarStatusClose() {
//        if(n<5){
//            n++;
//
//            m_myHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e("mf=queryCarStatusClose", "===");
//
//                    carLoopClose();
//
//                }
//            }, 1 * 1000);
//        }else{
//            ToastUtil.showMessageApp(context, "关锁超时");
//
////            car_notification(3, 4, 0);
//
//            closeLoadingDialog();
//        }
//    }

    //小安
    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH})
    public void connectDevice(String imei) {
        if (apiClient != null) {
            apiClient.connectToIMEI(imei);

            Log.e("connectDevice===", "==="+imei);
        }
    }

    //小安
    @Override
    public void onConnect(BluetoothDevice bluetoothDevice) {
        Log.e("mf===Xiaoan", "Connect==="+isConnect);

        isConnect = true;
        m_myHandler.removeCallbacksAndMessages(null);

        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

//                String tvAgain = tv_againBtn.getText().toString().trim();

                Log.e("mf===Xiaoan1", isAgain+"==="+isEndBtn+"==="+isOpenLock);

                xiaoanOpen_blue();

            }
        }, 2 * 1000);

    }



    //小安
    @Override
    public void onDisConnect(BluetoothDevice bluetoothDevice) {
        Log.e("mf===Xiaoan", "DisConnect==="+isConnect);


        if(isConnect){
            isConnect = false;

            Log.e("mf===Xiaoan2", "DisConnect==="+isConnect);
            return;
        }

        if (apiClient != null) {
            apiClient.onDestroy();
        }

        isConnect = false;


    }

    public void xiaoanOpen_blue() {
        apiClient.setDefend(false, new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("xiaoanOpen===", response.toString());

                        if(response.code==0){
                            isFinish = true;

                            ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

//                            car_notification(1, 1, 0);    //TODO

                        }else{
                            unlock();
                        }

                    }
                });

            }
        });
    }

    public void xiaoanClose_blue() {
        apiClient.setDefend(true, new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("xiaoanClose===", type+"==="+deviceuuid+"==="+response.toString());

                        if(response.code==0){
                            ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");

//                            macList2 = new ArrayList<> (macList);
//
//                            car_notification(isAgain?2:3, 1, isAgain?0:1);    //TODO

                        }else if(response.code==6){
                            ToastUtil.showMessageApp(context,"车辆未停止，请停止后再试");

                            closeLoadingDialog();
                        }else{

                            lock();

//                            if("108".equals(info)){       //TODO  2
//                                Log.e("biking_defend===1", "====");
//
//                            }else{
//                                Log.e("biking_defend===2", "====");
//
//                                ToastUtil.showMessageApp(context,"关锁失败，请重试");
//                            }
                        }


                    }
                });
            }
        });
    }

    @Override
    public void onDeviceReady(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void onReadRemoteRssi(int i) {

    }

    @Override
    public void onError(BluetoothDevice bluetoothDevice, String s, int i) {

    }

    @Override
    public void onBleAdapterStateChanged(int i) {

    }

    @Override
    public void onResult(ScanResult scanResult) {

    }

    void closeLoadingDialog(){
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }

    }

    void closeLoadingDialog2(){
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
//        if (loadingDialog2 != null && loadingDialog2.isShowing()){
//            loadingDialog2.dismiss();
//        }
    }

    private void setUpLocationStyle() {
        // 自定义系统定位蓝点
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
////        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
//
//        myLocationStyle.interval(2000);
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
//        myLocationStyle.strokeWidth(0);
//        myLocationStyle.strokeColor(R.color.main_theme_color);
//        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
//
//        aMap.setMyLocationStyle(myLocationStyle);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        Log.e("onLocationChanged=0", mListener+"==="+amapLocation);

        if (mListener != null && amapLocation != null) {

            if ((latitude == amapLocation.getLatitude()) && (longitude == amapLocation.getLongitude())) return;

            if (amapLocation != null && amapLocation.getErrorCode() == 0) {


                if (0.0 != amapLocation.getLatitude() && 0.0 != amapLocation.getLongitude()) {
                    if (mListener != null) {
                        mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                    }
                    myLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    latitude = amapLocation.getLatitude();
                    longitude = amapLocation.getLongitude();
                    //保存位置到本地
                    SharedPreferencesUrls.getInstance().putString("latitude",""+latitude);
                    SharedPreferencesUrls.getInstance().putString("longitude",""+longitude);

                    Log.e("onLocationChanged=", mFirstFix+"==="+myLocation);

//                    Toast.makeText(context,"==="+myLocation, Toast.LENGTH_SHORT).show();

                    if (mFirstFix){
                        initNearby(amapLocation.getLatitude(),amapLocation.getLongitude());
                        mFirstFix = false;

//                        Toast.makeText(context,"==="+leveltemp, Toast.LENGTH_SHORT).show();

                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, leveltemp));

//                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18f));
                    } else {
//                        centerMarker.remove();
                        mCircle.remove();
                    }

                    accuracy = amapLocation.getAccuracy();

                    addChooseMarker();
                    addCircle(myLocation, amapLocation.getAccuracy());//添加定位精度圆
                }else{
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开位置权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    activity.finish();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                        }
                    });
                    customBuilder.create().show();
                }
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;

        if (mlocationClient != null) {

            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(2 * 1000);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();

//			mListener.onLocationChanged(amapLocation);
        }

        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(context);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);

            // 关闭缓存机制
            mLocationOption.setLocationCacheEnable(false);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);

//            mLocationOption.setSensorEnable(true);

            mLocationOption.setInterval(2 * 1000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();

//            mAMapLocationManager = LocationManagerProxy.getInstance(this);
//            mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 60*1000, 10, this);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 添加Circle
     * @param latlng  坐标
     * @param radius  半径
     */
    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private ValueAnimator animator = null;

    private void animMarker() {
        isMovingMarker = false;
        if (animator != null) {
            animator.start();
            return;
        }
        animator = ValueAnimator.ofFloat(mapView.getHeight() / 2, mapView.getHeight() / 2 - 30);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(150);
        animator.setRepeatCount(1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                Float value = (Float) animation.getAnimatedValue();
//                centerMarker.setPositionByPixels(mapView.getWidth() / 2, Math.round(value));
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                centerMarker.setIcon(successDescripter);
            }
        });
        animator.start();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e("sf===requestCode", requestCode+"==="+resultCode+"==="+data);

                switch (requestCode) {

                    case 1:
                        if (resultCode == RESULT_OK) {
                            String result = data.getStringExtra("QR_CODE");
//                    upcarmap(result);
                            lock(result);
                        } else {
                            Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                        }

                        Log.e("requestCode===1", "==="+resultCode);
                        break;

                    case 2:
                        if (resultCode == RESULT_OK) {
                            String result = data.getStringExtra("QR_CODE");
                            switch (isLock){
                                case 1:
                                    Log.e("requestCode===2_1", "==="+resultCode);
                                    lock(result);
                                    break;
                                case 2:
                                    Log.e("requestCode===2_2", "==="+resultCode);
                                    unLock(result);
                                    break;
                                case 3:
                                    Log.e("requestCode===2_3", "==="+resultCode);
                                    endCar(result);
                                    break;
                                case 4:
                                    Log.e("requestCode===2_4", "==="+resultCode);
                                    recycle(result);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                        }

                        Log.e("requestCode===2", "==="+resultCode);
                        break;

                    case PRIVATE_CODE:
                        openGPSSettings();
                        break;

                    case 101:

                        Log.e("requestCode===101", requestCode+"==="+data.getStringExtra("address"));

                        if (resultCode == RESULT_OK) {
                            String tz = data.getStringExtra("tz");
                            if(tz!=null && "1".equals(tz)){
                                ((MainActivity)getActivity()).changeTab(4);
                            }

                            String sx = data.getStringExtra("sx");
                            if(sx!=null && "1".equals(sx)){
                                initNearby(latitude, longitude);
                            }

                        }

                        break;

                    case 188:

                        if (resultCode == RESULT_OK) {
//                            closeLoadingDialog();

                            isPermission = true;

                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("正在唤醒车锁");
                                loadingDialog.show();
                            }

                            Log.e("188===", isAgain+"==="+isConnect+"==="+isLookPsdBtn+"==="+oid+"==="+m_nowMac+"==="+type+">>>"+isOpenLock+"==="+isEndBtn);


                            isStop = false;
                            isOpen = false;
                            isFinish = false;
                            n = 0;
                            cn = 0;
                            force_backcar = 0;
                            isTwo = false;
                            first3 = true;
                            flagm = 0;
                            isFrist1 = true;
                            stopScan = false;
                            clickCount = 0;
                            tz = 0;
                            transtype = "";
                            major = 0;
                            minor = 0;
                            isGPS_Lo = false;
                            scan = false;
                            isTemp = false;
                            backType = "";
                            isOpenLock = false;
                            isConnect = false;
                            isLookPsdBtn = false;
                            isEndBtn = false;
                            isAgain = false;
                            order_type = 0;
                            isWaitEbikeInfo = true;
                            ebikeInfoThread = null;
                            oid = "";

//                            if ("2".equals(type) || "3".equals(type)){
//
//                                Log.e("mf===requestCode2", codenum+"==="+type);
//
////                                      closeBroadcast();     //TODO    3
////                                      activity.registerReceiver(broadcastReceiver, Config.initFilter());
////                                      GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
//
//                                BleManager.getInstance().init(activity.getApplication());
//                                BleManager.getInstance()
//                                        .enableLog(true)
//                                        .setReConnectCount(10, 5000)
//                                        .setConnectOverTime(timeout)
//                                        .setOperateTimeout(10000);
//
//                            }else if("4".equals(type)){
//
////                                      BLEService.bluetoothAdapter = mBluetoothAdapter;
////                                      bleService.view = context;
////                                      bleService.showValue = true;
//                            }else if ("5".equals(type)  || "6".equals(type)) {
//                                Log.e("initView===5", "==="+isLookPsdBtn);
//
////                                      ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
////                                      ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);
//                            }else if ("7".equals(type)) {
//                            }

//                                    SharedPreferencesUrls.getInstance().putString("tempStat", "0");
//                                    if (carmodel_id==2) {
//                                        tv_againBtn.setText("临时上锁");
//
//                                    }else{
//                                        tv_againBtn.setText("再次开锁");
//                                    }
//
//                                    refreshLayout.setVisibility(View.VISIBLE);

                            Log.e("188===order", isAgain+"==="+isLookPsdBtn+"==="+oid+"==="+m_nowMac+"==="+type+">>>"+isOpenLock+"==="+isEndBtn);


                            open_lock();

                        }else{
                            ToastUtil.showMessageApp(context, "需要打开蓝牙");

                            Log.e("188===fail", oid+"===");

                            if(popupwindow!=null){
                                popupwindow.dismiss();
                            }

                            closeLoadingDialog2();

                        }
                        break;

                    case 189:
                        Log.e("189===", oid+"===");

                        BleManager.getInstance().init(activity.getApplication());
                        BleManager.getInstance()
                                .enableLog(true)
                                .setReConnectCount(10, 5000)
                                .setConnectOverTime(timeout)
                                .setOperateTimeout(10000);

                        setScanRule();
                        scan();
                        break;

                    default:

                        break;

                }
            }
        });




//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case 188:
//
//                    BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//                    mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                    if (mBluetoothAdapter == null) {
//                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                        activity.finish();
//                        return;
//                    }
//                    if (!mBluetoothAdapter.isEnabled()) {
//                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableBtIntent, 188);
//                    }else{
//                    }
//
//
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
//                case 188:
//                    ToastUtil.showMessageApp(context, "需要打开蓝牙");
//                    AppManager.getAppManager().AppExit(context);
//                    break;
//                default:
//                    break;
//            }
//        }


    }

    private void upcarmap(String result){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("tokencode",result);
            params.put("latitude",latitude);
            params.put("longitude",longitude);
            HttpHelper.post(context, Urls.upcarmap, params, new TextHttpResponseHandler() {
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
                    UIHelper.ToastError(context, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"恭喜您，提交成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

    private void recycle(String result){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("codenum",result);
            HttpHelper.post(context, Urls.recycle, params, new TextHttpResponseHandler() {
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
                    UIHelper.ToastError(context, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"恭喜您，回收成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

    private void lock(String result){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("codenum",result);
            HttpHelper.post(context, Urls.lock, params, new TextHttpResponseHandler() {
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
                    UIHelper.ToastError(context, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"恭喜您，锁定成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }
    private void unLock(String result){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("codenum",result);
            HttpHelper.post(context, Urls.unLock, params, new TextHttpResponseHandler() {
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
                    UIHelper.ToastError(context, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"恭喜您，解锁成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }
    private void endCar(String result){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("tokencode",result);
            HttpHelper.post(context, Urls.endCar, params, new TextHttpResponseHandler() {
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
                    UIHelper.ToastError(context, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"恭喜您，结束用车成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            try{
//                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                customBuilder.setTitle("温馨提示").setMessage("确认退出吗?")
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        AppManager.getAppManager().AppExit(context);
//                    }
//                });
//                customBuilder.create().show();
//                return true;
//            }catch (Exception e){
//
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case 100:
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.CAMERA)){
                        try {
                            SharedPreferencesUrls.getInstance().putString("type", "");

                            Intent intent = new Intent();
                            intent.setClass(context, ActivityScanerCode.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("isChangeKey",false);
                            startActivityForResult(intent, 101);
                        } catch (Exception e) {
                            UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
                        }
                    }
                }else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取相机权限！")
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
                            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                            startActivity(localIntent);
//                            finishMine();
                        }
                    });
                    customBuilder.create().show();
                }
                break;
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                        if (aMap == null) {
//                            aMap = mapView.getMap();
//                            setUpMap();
//                        }
//                        aMap.getUiSettings().setZoomControlsEnabled(false);
//                        aMap.getUiSettings().setMyLocationButtonEnabled(false);
//                        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
//                        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(15);// 设置缩放监听
//                        aMap.moveCamera(cameraUpdate);
//                        setUpLocationStyle();
//                    }

                    initView();
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取定位权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
//                                    context.finishMine();
                                }
                            }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                            startActivity(localIntent);
//                            context.finishMine();
                        }
                    });
                    customBuilder.create().show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



}
