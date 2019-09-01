package com.qimalocl.manage.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
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
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.fitsleep.sunshinelibrary.utils.ToastUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.MPermissionsActivity;
import com.qimalocl.manage.core.common.AppManager;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.LngLatBean;
import com.qimalocl.manage.model.NearbyBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.utils.JsonUtil;
import com.qimalocl.manage.utils.ToastUtil;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@SuppressLint("NewApi")
public class GetDotActivity extends MPermissionsActivity implements View.OnClickListener, LocationSource,
        AMapLocationListener, AMap.OnCameraChangeListener, AMap.OnMapTouchListener {

    Unbinder unbinder;

    private Context context;
    private View v;

    static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private final static int SCANNIN_GREQUEST_CODE = 1;
//    private LoadingDialog lockLoading;
//    private LoadingDialog loadingDialog;
    private LoadingDialog loadingDialog1;
    public static boolean isForeground = false;

    private ImageView leftBtn;
    private EditText et_name;
    private ImageView myLocationBtn, linkBtn;
    private LinearLayout scanLock, myCommissionLayout, myLocationLayout, linkLayout;
    private ImageView closeBtn;

    protected AMap aMap;
    protected BitmapDescriptor successDescripter;
    protected BitmapDescriptor successDescripter2;
    private MapView mapView;
    //	private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = true;
    private LatLng myLocation = null;
    private Circle mCircle;

    private BitmapDescriptor bikeDescripter;
    private Handler handler = new Handler();
    private Marker centerMarker;
    private Marker dotMarker;
    private boolean isMovingMarker = false;
    private Button authBtn;
    private Button rechargeBtn;
    private int Tag = 0;

    private List<Marker> bikeMarkerList;
    private boolean isUp = false;
    private LinearLayout refreshLayout;
    private Button cartBtn;
    private LinearLayout slideLayout;
    private LinearLayout marqueeLayout;
    private LinearLayout confirmLayout;
    private LinearLayout cancelLayout;
    private LinearLayout submitLayout;
    private int imageWith = 0;
    private ValueAnimator animator = null;

    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    protected OnLocationChangedListener mListener;
//    CustomDialog.Builder customBuilder;
//    private CustomDialog customDialog;
//    private CustomDialog customDialog2;
//    private CustomDialog customDialog3;
//    private CustomDialog customDialog4;
    private boolean isConnect = false;
    private int flag = 0;
    private int flag2 = 0;
    boolean isFrist1 = true;
    private int near = 1;
    public static int tz = 0;
    public static boolean screen = true;
    public static boolean start = false;
    public static boolean change = true;
    private boolean first = true;
    public boolean run = false;
    private long k=0;
    private long p=-1;
    private boolean first3 = true;
//    private boolean isStop = false;

    private Dialog dialog;

    private TextView marquee;
    protected InternalReceiver internalReceiver = null;

    private BluetoothAdapter mBluetoothAdapter;
    LocationManager locationManager;
    String provider = LocationManager.GPS_PROVIDER;
//	String provider = LocationManager.NETWORK_PROVIDER;

    public List<Boolean> isContainsList;
    public List<String> macList;
    public List<String> macList2;
    public List<Polygon> pOptions;

    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private int n=0;
    private float accuracy = 29.0f;

    private boolean isHidden;

    private int carType = 1;

    private Bundle savedIS;

    public double referLatitude = 0.0;
    public double referLongitude = 0.0;

    private List<LngLatBean> list = new ArrayList<LngLatBean>();
    private String json_LngLat;
    private boolean isSubmit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_dot);
        ButterKnife.bind(this);

        context = this;

        WindowManager.LayoutParams winParams = getWindow().getAttributes();
        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        carType = getIntent().getIntExtra("carType", 1);


        isContainsList = new ArrayList<>();
        macList = new ArrayList<>();
        macList2 = new ArrayList<>();
        pOptions = new ArrayList<>();
        bikeMarkerList = new ArrayList<>();
        imageWith = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.8);

        mapView = (MapView) findViewById(R.id.mainUI_map2);
        mapView.onCreate(savedInstanceState);


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                m_myHandler.sendEmptyMessage(1);
//            }
//        }).start();

        initView();

        ToastUtil.showMessage(context, SharedPreferencesUrls.getInstance().getString("userName", "") + "===" + SharedPreferencesUrls.getInstance().getString("uid", "") + "<==>" + SharedPreferencesUrls.getInstance().getString("access_token", ""));

//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setType(1).setTitle("温馨提示").setMessage("当前行程已停止计费，客服正在加紧处理，请稍等\n客服电话：0519—86999222");
//        customDialog = customBuilder.create();
//
//        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setTitle("温馨提示").setMessage("还车须至校内地图红色区域，或打开手机GPS并重启软件再试")
//                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        customDialog3 = customBuilder.create();
//
//        customBuilder = new CustomDialog.Builder(context);
//        customBuilder.setTitle("温馨提示").setMessage("还车须至校内地图红色区域")
//                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        customDialog4 = customBuilder.create();

    }


    private void schoolRange(){
        if(isHidden) return;

        Log.e("main===schoolRange0", "==="+carType);

        RequestParams params = new RequestParams();
        params.put("type", carType);

        HttpHelper.get(context, Urls.schoolRange, params, new TextHttpResponseHandler() {
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
                if(isHidden) return;

                try {
                    Log.e("main===schoolRange1", "==="+carType);

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        JSONArray jsonArray = new JSONArray(result.getData());
                        if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
                            isContainsList.clear();
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            List<LatLng> list = new ArrayList<>();
                            for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){
                                JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);
                                LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")),
                                        Double.parseDouble(jsonObject.getString("longitude")));
                                list.add(latLng);
                            }
                            Polygon polygon = null;
                            PolygonOptions pOption = new PolygonOptions();
                            pOption.addAll(list);

                            if(carType == 1){
                                polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                        .strokeColor(Color.argb(160, 255, 0, 0))
                                        .fillColor(Color.argb(160, 255, 0, 0)));
                            }else{
                                polygon = aMap.addPolygon(pOption.strokeWidth(2)
                                        .strokeColor(Color.argb(160, 0, 255, 0))
                                        .fillColor(Color.argb(160, 0, 255, 0)));
                            }


//                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
//                                    .strokeColor(Color.argb(160, 0, 0, 255))
//                                    .fillColor(Color.argb(160, 0, 0, 255)));

//                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
//                                    .strokeColor(Color.argb(255, 0, 255, 0))
//                                    .fillColor(Color.argb(255, 0, 255, 0)));

                            if(!isHidden){
                                pOptions.add(polygon);

                                isContainsList.add(polygon.contains(myLocation));
                            }else{
                                Log.e("pOptions===Bike", isContainsList.size()+"==="+pOptions.size());
                            }

                        }
                    }else {
                        ToastUtil.showMessageApp(context,result.getMsg());
                    }
                }catch (Exception e){
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }

    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Log.e("main===ChangeFinish_B", isContainsList.contains(true) + "》》》" + cameraPosition.target.latitude + "===" + macList.size()+">>>"+isUp + "===" + cameraPosition.target.latitude);


        if (isUp  && !isHidden){
//            initNearby(cameraPosition.target.latitude, cameraPosition.target.longitude);

            if (centerMarker != null) {
//				animMarker();
                m_myHandler.sendEmptyMessage(4);
            }
        }

        if (macList.size() != 0) {
            macList.clear();
        }

    }



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
                Float value = (Float) animation.getAnimatedValue();
                centerMarker.setPositionByPixels(mapView.getWidth() / 2, Math.round(value));
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                centerMarker.setIcon(successDescripter);
            }
        });
        animator.start();
    }

    private void setMovingMarker() {
        if (isMovingMarker)
            return;

        isMovingMarker = true;
        centerMarker.setPositionByPixels(mapView.getWidth() / 2, mapView.getHeight() / 2);
        centerMarker.setIcon(successDescripter);
    }


    public void onCameraChange(CameraPosition cameraPosition) {
        if (centerMarker != null) {
            setMovingMarker();
        }
    }

    /**附近车接口 */
    private void initNearby(double latitude, double longitude){

        if(isHidden) return;

        Log.e("main===initNearby0", latitude+"==="+longitude);

        RequestParams params = new RequestParams();
        params.put("latitude",latitude);
        params.put("longitude",longitude);
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

                if(isHidden) return;

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
                        }else {
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
    }

    private void initView() {
        openGPSSettings();

        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
                } else {
                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
                }
                return;
            }
        }

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

//        lockLoading = new LoadingDialog(context);
//        lockLoading.setCancelable(false);
//        lockLoading.setCanceledOnTouchOutside(false);

        loadingDialog1 = new LoadingDialog(context);
        loadingDialog1.setCancelable(false);
        loadingDialog1.setCanceledOnTouchOutside(false);

//        dialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
//        View dialogView = LayoutInflater.from(context).inflate(R.layout.ui_frist_view, null);
//        dialog.setContentView(dialogView);
//        dialog.setCanceledOnTouchOutside(false);

        leftBtn =  (ImageView)findViewById(R.id.getDot_leftBtn);
        et_name =  (EditText)findViewById(R.id.getDot_et_name);
        myLocationLayout =  (LinearLayout)findViewById(R.id.mainUI_myLocationLayout);
        confirmLayout = (LinearLayout)findViewById(R.id.getDot_confirmLayout);
        cancelLayout = (LinearLayout)findViewById(R.id.getDot_cancelLayout);
        submitLayout = (LinearLayout)findViewById(R.id.getDot_submitLayout);

        if(aMap==null){
            aMap = mapView.getMap();
            setUpMap();
        }

        aMap.setMapType(AMap.MAP_TYPE_NAVI);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
        aMap.getUiSettings().setLogoBottomMargin(-50);

        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(25f);// 设置缩放监听
        aMap.moveCamera(cameraUpdate);
        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
        successDescripter2 = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed2);
        bikeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);

        aMap.setOnMapTouchListener(this);
        setUpLocationStyle();

        leftBtn.setOnClickListener(this);
        myLocationLayout.setOnClickListener(this);
        confirmLayout.setOnClickListener(this);
        cancelLayout.setOnClickListener(this);
        submitLayout.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        isForeground = true;
        super.onResume();

        Log.e("main===bike", "main====onResume===");

        tz = 0;

        if (flag == 1) {
            flag = 0;
            return;
        }

//        closeBroadcast();
//        getFeedbackStatus();
//
//        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
//        String specialdays = SharedPreferencesUrls.getInstance().getString("specialdays", "");
//        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
//            authBtn.setVisibility(View.VISIBLE);
//            authBtn.setText("您还未登录，点我快速登录");
//            authBtn.setEnabled(true);
//            cartBtn.setVisibility(View.GONE);
//            refreshLayout.setVisibility(View.GONE);
//            rechargeBtn.setVisibility(View.GONE);
//        } else {
//            refreshLayout.setVisibility(View.VISIBLE);
//            if (SharedPreferencesUrls.getInstance().getString("iscert", "") != null && !"".equals(SharedPreferencesUrls.getInstance().getString("iscert", ""))) {
//                switch (Integer.parseInt(SharedPreferencesUrls.getInstance().getString("iscert", ""))) {
//                    case 1:
//                        authBtn.setEnabled(true);
//                        authBtn.setVisibility(View.VISIBLE);
//                        authBtn.setText("您还未认证，点我快速认证");
//                        break;
//                    case 2:
//                        getCurrentorder1(uid, access_token);
//                        break;
//                    case 3:
//                        authBtn.setEnabled(true);
//                        authBtn.setVisibility(View.VISIBLE);
//                        authBtn.setText("认证被驳回，请重新认证");
//                        break;
//                    case 4:
//                        authBtn.setEnabled(false);
//                        authBtn.setVisibility(View.VISIBLE);
//                        authBtn.setText("认证审核中");
//                        break;
//                }
//            } else {
//                authBtn.setVisibility(View.GONE);
//            }
//            if ("0.00".equals(SharedPreferencesUrls.getInstance().getString("money", ""))
//                    || "0".equals(SharedPreferencesUrls.getInstance().getString("money", "")) || SharedPreferencesUrls.getInstance().getString("money", "") == null ||
//                    "".equals(SharedPreferencesUrls.getInstance().getString("money", ""))) {
//                rechargeBtn.setVisibility(View.VISIBLE);
//            } else {
//                rechargeBtn.setVisibility(View.GONE);
//            }
//
//            if (("0".equals(specialdays) || specialdays == null || "".equals(specialdays))
//                    && ("0".equals(specialdays) || specialdays == null || "".equals(specialdays))) {
//                cartBtn.setVisibility(View.GONE);
//            } else {
//                cartBtn.setVisibility(View.VISIBLE);
//                cartBtn.setText("免费" + specialdays + "天,每次前一个小时免费,点击续费");
//            }
//
//        }
    }



    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    animMarker();
                    break;
                case 0x99://搜索超时
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
//        aMap.setLoadOfflineData(true);
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
//	    super.activate(listener);

        mListener = listener;

        Log.e("main===b", isContainsList.contains(true) + "===listener===" + mlocationClient);


        if (mlocationClient != null) {

            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(2 * 1000);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();

//			mListener.onLocationChanged(amapLocation);
        }

//		if (mListener != null) {
//			mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//		}

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

            mLocationOption.setInterval(2 * 1000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        change = true;

        if(isHidden) return;

        if (mListener != null && amapLocation != null) {

            if ((referLatitude == amapLocation.getLatitude()) && (referLongitude == amapLocation.getLongitude())) return;

            Log.e("main===Changed", isContainsList.contains(true) + "》》》" + near + "===" + macList.size() + "===" + amapLocation.getLatitude() );
            ToastUtil.showMessage(context, isContainsList.contains(true) + "》》》" + near + "===" + amapLocation.getLatitude() + "===" + amapLocation.getLongitude());

            if (amapLocation != null && amapLocation.getErrorCode() == 0) {

                if (0.0 != amapLocation.getLatitude() && 0.0 != amapLocation.getLongitude()) {
                    String latitude = SharedPreferencesUrls.getInstance().getString("biking_latitude", "");
                    String longitude = SharedPreferencesUrls.getInstance().getString("biking_longitude", "");
//                    if (latitude != null && !"".equals(latitude) && longitude != null && !"".equals(longitude)) {
//                        if (AMapUtils.calculateLineDistance(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)
//                        ), new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())) > 10) {
//                            SharedPreferencesUrls.getInstance().putString("biking_latitude", "" + amapLocation.getLatitude());
//                            SharedPreferencesUrls.getInstance().putString("biking_longitude", "" + amapLocation.getLongitude());
//                            addMaplocation(amapLocation.getLatitude(), amapLocation.getLongitude());
//                        }
//                    }
                    if (mListener != null) {
                        mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                    }

                    referLatitude = amapLocation.getLatitude();
                    referLongitude = amapLocation.getLongitude();
                    myLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

                    Log.e("main===Changed>>>0", "》》》"+mFirstFix);

                    if (mFirstFix) {

                        Log.e("main===Changed>>>1", "》》》");

                        mFirstFix = false;
                        schoolRange();
//                        initNearby(amapLocation.getLatitude(), amapLocation.getLongitude());
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
                    } else {
                        centerMarker.remove();
                        mCircle.remove();
                        centerMarker=null;

                        if (!isContainsList.isEmpty() || 0 != isContainsList.size()) {
                            isContainsList.clear();
                        }
                        for (int i = 0; i < pOptions.size(); i++) {
                            isContainsList.add(pOptions.get(i).contains(myLocation));
                        }
                    }

                    ToastUtil.showMessage(context, isContainsList.contains(true) + "======" + near);

                    accuracy = amapLocation.getAccuracy();

                    addChooseMarker();
                    addCircle(myLocation, amapLocation.getAccuracy());


                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开位置权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finish();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                        }
                    });
                    customBuilder.create().show();
                }

                //保存经纬度到本地
                SharedPreferencesUrls.getInstance().putString("latitude", "" + amapLocation.getLatitude());
                SharedPreferencesUrls.getInstance().putString("longitude", "" + amapLocation.getLongitude());
            }


        }
    }


    @Override
    public void onStart() {
        super.onStart();
        screen = true;
        start = true;

        Log.e("main===onStart_b", "===="+mlocationClient);

        if (mlocationClient != null) {
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(5 * 1000);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }

//        if (!"".equals(m_nowMac)) {
//            mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                    k++;
//                    Log.e("main===LeScan", device + "====" + rssi + "====" + k);
//
//                    if (!macList.contains(""+device)){
//                        macList.add(""+device);
//                    }
//
//                }
//            };
//        }
    }

    @Override
    public void onPause() {
        isForeground = false;
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
//        if (lockLoading != null && lockLoading.isShowing()) {
//            lockLoading.dismiss();
//        }
        super.onPause();

//		if(mlocationClient!=null) {
//			mlocationClient.stopLocation();//停止定位
//		}

//		if(mapView!=null){
//            mapView.onPause();
//        }

//		deactivate();
//		mFirstFix = false;
        tz = 0;

        ToastUtil.showMessage(context, "main====onPause");
        Log.e("main===", "main====onPause");

//		closeBroadcast();

    }

    @Override
    public void onStop() {
        super.onStop();
        screen = false;
        change = false;

        Log.e("bikeFrag===", "===onStop");

//		closeBroadcast();
//
//		if(mlocationClient!=null) {
//			mlocationClient.stopLocation(); // 停止定位
//		}

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if(mapView!=null){
            mapView.onDestroy();
        }

        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }

        ToastUtil.showMessage(context, "main===onDestroy");

        deactivate();

        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }



    protected void handleReceiver(Context context, Intent intent) {
        // 广播处理
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        String data = intent.getStringExtra("data");

        Log.e("main===", "handleReceiver===" + action + "===" + data);
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
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        locationManager.requestLocationUpdates(provider, 2000, 500, locationListener);

        isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isOpen;
    }


    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
        } else {

            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
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

    @Override
    public void onClick(View view) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (view.getId()){
            case R.id.getDot_leftBtn:
                finishMine();
                break;

            case R.id.mainUI_myLocationLayout:
                if (myLocation != null) {
                    CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
                    aMap.animateCamera(update);
                }
                break;

            case R.id.getDot_confirmLayout:
                Log.e("confirmLayout===", referLatitude+"==="+referLongitude);

                MarkerOptions centerMarkerOption = new MarkerOptions().position(new LatLng(centerMarker.getPosition().latitude, centerMarker.getPosition().longitude)).icon(successDescripter2);
                dotMarker = aMap.addMarker(centerMarkerOption);

                LngLatBean bean = new LngLatBean();
                bean.setLng(centerMarker.getPosition().longitude);
                bean.setLat(centerMarker.getPosition().latitude);

                list.add(bean);

                try {
                    json_LngLat = JsonUtil.objectToJson(list);

                    Log.e("confirmLayout===2", "==="+json_LngLat);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){
//                JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);
//                JSONObject json = JSONObject.fromObject(stu);
//                JSONArray array = JSONArray.

                break;

            case R.id.getDot_cancelLayout:

//                Log.e("cancelLayout===", centerMarker.getPosition()+"==="+centerMarker.getPosition().latitude+"==="+centerMarker.getPosition().longitude+"==="+referLongitude+"==="+referLatitude);

                if(dotMarker!=null){
                    dotMarker.remove();
                }

                if(list!=null && list.size()>=1){
                    list.remove(list.size()-1);
                }

                break;

            case R.id.getDot_submitLayout:

                if("".equals(et_name.getText().toString())){
                    ToastUtil.showMessageApp(context, "名称不能为空！");
                    return;
                }

                if(isSubmit){
                    isSubmit = false;

                    submit();
                }else{
                    ToastUtil.showMessageApp(context, "不能重复提交！");
                }

                break;

            default:
                break;
        }
    }

    protected void submit(){
        Log.e("main===submit",json_LngLat+"==="+referLatitude+"==="+referLongitude);

        RequestParams params = new RequestParams();
        params.put("lng_lat", json_LngLat);
        params.put("area_name", et_name.getText().toString());
        params.put("type", carType);
        HttpHelper.post(context, Urls.addSchoolRange, params, new TextHttpResponseHandler() {
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
                Log.e("submit===","==="+responseString);
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                        List<LatLng> list2 = new ArrayList<>();

//                        json_LngLat = JsonUtil.jsonToObject().objectToJson(list);

                        for (int j = 0; j < list.size(); j ++){
                            LatLng latLng = new LatLng(list.get(j).getLat(), list.get(j).getLng());
                            list2.add(latLng);
                        }
//                        for (int j = 0; j < json_LngLat.getJSONArray(i).length(); j ++){
//                            JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);
//                            LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")),
//                                    Double.parseDouble(jsonObject.getString("longitude")));
//                            list2.add(latLng);
//                        }
//                        Polygon polygon = null;
                        PolygonOptions pOption = new PolygonOptions();
                        pOption.addAll(list2);

                        aMap.addPolygon(pOption.strokeWidth(2)
                                .strokeColor(Color.argb(255, 0, 0, 255))
                                .fillColor(Color.argb(255, 0, 0, 255)));

                        ToastUtil.showMessageApp(context, "提交成功");


//                        submitLayout.setEnabled(false);

                    }else {
                        ToastUtil.showMessageApp(context, result.getMsg());
                    }
                }catch (Exception e){

                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
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


//    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.e("broadcastReceiver===1", "==="+intent);
//
//            getCurrentorder1(SharedPreferencesUrls.getInstance().getString("uid", ""), SharedPreferencesUrls.getInstance().getString("access_token", ""));
//            getFeedbackStatus();
//        }
//    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        ToastUtil.showMessage(context, resultCode + "====" + requestCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 188:
                    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                    mBluetoothAdapter = bluetoothManager.getAdapter();

                    if (mBluetoothAdapter == null) {
                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
                        finish();
                        return;
                    }
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 188);
                    }else{
                    }


                    break;

                default:
                    break;

            }
        } else {
            switch (requestCode) {
                case PRIVATE_CODE:
                    openGPSSettings();
                    break;

                case 188:
                    ToastUtil.showMessageApp(context, "需要打开蓝牙");
                    AppManager.getAppManager().AppExit(context);
                    break;
                default:
                    break;
            }
        }
    }

    private void addChooseMarker() {
        // 加入自定义标签

        if(centerMarker == null){
            MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation).icon(successDescripter);
            centerMarker = aMap.addMarker(centerMarkerOption);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(myLocation));
                    aMap.animateCamera(update, 1000, new AMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            aMap.setOnCameraChangeListener(GetDotActivity.this);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            }, 1000);
        }

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


    public void onTouch(MotionEvent motionEvent) {
        Log.e("main===onTouch", "===" + motionEvent.getAction());


        if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE
                || motionEvent.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
            isUp = true;
        }else {
            isUp = false;
        }
    }


    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.setTitle("正在刷新");
                loadingDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
            ToastUtil.showMessage(context, "刷新成功");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    private void setUpLocationStyle() {
        // 自定义系统定位蓝点

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);
    }




//    private void schoolrangeList(){
//        RequestParams params = new RequestParams();
//        HttpHelper.get(context, schoolrangeList, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在加载");
//                    loadingDialog.show();
//                }
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//                UIHelper.ToastError(context, throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().equals("Success")) {
//                        JSONArray jsonArray = new JSONArray(result.getData());
//                        if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
//                            isContainsList.clear();
//                        }
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            List<LatLng> list = new ArrayList<>();
//                            for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){
//                                JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);
//                                LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")),
//                                        Double.parseDouble(jsonObject.getString("longitude")));
//                                list.add(latLng);
//                            }
//                            Polygon polygon = null;
//                            PolygonOptions pOption = new PolygonOptions();
//                            pOption.addAll(list);
////                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
////                                    .strokeColor(Color.argb(160, 255, 0, 0))
////                                    .fillColor(Color.argb(160, 255, 0, 0)));
//
////                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
////                                    .strokeColor(Color.argb(160, 0, 0, 255))
////                                    .fillColor(Color.argb(160, 0, 0, 255)));
//
//                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
//                                    .strokeColor(Color.argb(255, 0, 255, 0))
//                                    .fillColor(Color.argb(255, 0, 255, 0)));
//
//                            pOptions.add(polygon);
//                            isContainsList.add(polygon.contains(myLocation));
//                        }
//                    }else {
//                        ToastUtil.showMessageApp(context,result.getMsg());
//                    }
//                }catch (Exception e){
//                }
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//            }
//        });
//    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                break;
            case 100:
                break;
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PERMISSION_GRANTED) {
                    initView();
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
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

    protected void registerReceiver(IntentFilter intentfilter) {
        if (internalReceiver == null) {
            internalReceiver = new InternalReceiver();
        }
        registerReceiver(internalReceiver, intentfilter);
    }

    protected class InternalReceiver extends BroadcastReceiver {

        //	protected BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            handleReceiver(context, intent);

        }
    };
}
