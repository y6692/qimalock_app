package com.qimalocl.manage.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
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
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.flyco.tablayout.CommonTabLayout;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.BikeLocationActivity;
import com.qimalocl.manage.activity.DeviceList2Activity;
import com.qimalocl.manage.activity.DeviceListActivity;
import com.qimalocl.manage.activity.DeviceSelectActivity;
import com.qimalocl.manage.activity.DotSelectActivity;
import com.qimalocl.manage.activity.GetDotActivity;
import com.qimalocl.manage.activity.HistorysRecordActivity;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.activity.Main2Activity;
import com.qimalocl.manage.activity.MainActivity;
import com.qimalocl.manage.activity.TestXiaoanActivity;
import com.qimalocl.manage.base.BaseActivity;
import com.qimalocl.manage.base.BaseFragment;
import com.qimalocl.manage.core.common.AppManager;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.NearbyBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.utils.UtilAnim;
import com.qimalocl.manage.utils.UtilBitmap;
import com.qimalocl.manage.utils.UtilScreenCapture;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.sofi.blelocker.library.utils.BluetoothUtils.unregisterReceiver;

@SuppressLint("NewApi")
public class ScanFragment extends BaseFragment implements View.OnClickListener,LocationSource,
        AMapLocationListener,AMap.OnCameraChangeListener,AMap.OnMapTouchListener{

    Unbinder unbinder;

    private Context context;
    private Activity activity;

    private LoadingDialog loadingDialog;
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
    private Handler handler = new Handler();
    private Marker centerMarker;
    private boolean isMovingMarker = false;

    private List<Marker> bikeMarkerList;
    private boolean isUp = false;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private int isLock = 0;
    private View v;

    private String codenum;
    private String quantity;
    Marker curMarker;

    LocationManager locationManager;
    String provider = LocationManager.GPS_PROVIDER;
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

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

        mapView = v.findViewById(R.id.mainUI_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        bikeMarkerList = new ArrayList<>();
        initView();

//        Toast.makeText(context,"发送寻车指令成功", Toast.LENGTH_SHORT).show();
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

        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
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
    }


    public void initmPopupWindowView(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_menu, null, false);
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
        final PopupWindow popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);
        /**
         * 设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
         */
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        TextView tv_codenum = (TextView)customView.findViewById(R.id.pop_menu_codenum);
        TextView tv_quantity = (TextView)customView.findViewById(R.id.pop_menu_quantity);
        LinearLayout findBikeLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_findBike);
        LinearLayout openPowerLockLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_openPowerLock);
        TextView cancleBtn = (TextView)customView.findViewById(R.id.pop_menu_cancleBtn);

        Log.e("initmPopup===", codenum+"==="+quantity);

        tv_codenum.setText(codenum);
        tv_quantity.setText("电量："+quantity);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.pop_menu_findBike:
                        ddSearch();
                        break;
                    case R.id.pop_menu_openPowerLock:
                        battery_unlock();
                        break;
                    case R.id.pop_menu_cancleBtn:
                        popupwindow.dismiss();
                        break;
                }

            }
        };

        findBikeLayout.setOnClickListener(listener);
        openPowerLockLayout.setOnClickListener(listener);
        cancleBtn.setOnClickListener(listener);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

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

    private void initNearby(double latitude, double longitude){

        RequestParams params = new RequestParams();
        params.put("latitude",latitude);
        params.put("longitude",longitude);
        HttpHelper.get(context, Urls.nearbyEbike, params, new TextHttpResponseHandler() {
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
                            Toast.makeText(context,"附近没有自行车",Toast.LENGTH_SHORT).show();
                        }else {
                            for (int i = 0; i < array.length(); i++){
                                NearbyBean bean = JSON.parseObject(array.getJSONObject(i).toString(), NearbyBean.class);
                                // 加入自定义标签
                                MarkerOptions bikeMarkerOption = new MarkerOptions().title(bean.getCodenum()+"-"+bean.getQuantity()+"%").position(new LatLng(
                                        Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
                                        .icon("1".equals(bean.getQuantity_level())?bikeDescripter_green:"2".equals(bean.getQuantity_level())?bikeDescripter_yellow:"3".equals(bean.getQuantity_level())?bikeDescripter_red:"4".equals(bean.getQuantity_level())?bikeDescripter_blue:bikeDescripter_brown);
                                Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
                                bikeMarkerList.add(bikeMarker);

                                if("80001651".equals(bean.getCodenum())){
                                    Log.e("initNearby===", bean.getQuantity()+"==="+bean.getQuantity_level());
                                }

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

    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume===Scan", "===");

        mapView.onResume();

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)){
            rightBtn.setText("退出登录");
        }else {
            rightBtn.setText("登录");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        mapView.onPause();
        deactivate();
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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (mapView != null) mapView.onDestroy();


    }


    private void addChooseMarker() {
        // 加入自定义标签
        MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation).icon(successDescripter);
        centerMarker = aMap.addMarker(centerMarkerOption);
        centerMarker.setPositionByPixels(mapView.getWidth() / 2, mapView.getHeight() / 2);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CameraUpdate update = CameraUpdateFactory.zoomTo(18f);
                aMap.animateCamera(update, 1000, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
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
        centerMarker.setIcon(successDescripter);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (centerMarker != null) {
            setMovingMarker();
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (isUp){
            initNearby(cameraPosition.target.latitude,cameraPosition.target.longitude);
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

            case R.id.mainUI_myLocationLayout:
                if (myLocation != null) {
                    CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
                    aMap.animateCamera(update);
                }
                break;

            case R.id.mainUI_getDotLayout:
                UIHelper.goToAct(context, DotSelectActivity.class);
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


            default:
                break;
        }
    }


    private void setUpLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
        myLocationStyle.strokeWidth(0);
        myLocationStyle.strokeColor(R.color.main_theme_color);
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        aMap.setMyLocationStyle(myLocationStyle);
    }
    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                if (mListener != null) {
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                }
                myLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                latitude = amapLocation.getLatitude();
                longitude = amapLocation.getLongitude();
                //保存位置到本地
                SharedPreferencesUrls.getInstance().putString("latitude",""+latitude);
                SharedPreferencesUrls.getInstance().putString("longitude",""+longitude);
                if (mFirstFix){
                    initNearby(amapLocation.getLatitude(),amapLocation.getLongitude());
                    mFirstFix = false;
                    addChooseMarker();
                } else {
                    centerMarker.remove();
                    mCircle.remove();
                    addChooseMarker();
                }
                addCircle(myLocation, amapLocation.getAccuracy());//添加定位精度圆
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10f));
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(context);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(1000 * 1000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("requestCode===", "==="+requestCode);

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

            default:
                break;

        }


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

    /**
     *
     * 附近车接口
     *
     * */

}
