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
import android.widget.Toast;

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
import com.qimalocl.manage.model.ParkingBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.SchoolListBean;
import com.qimalocl.manage.model.UserBean;
import com.qimalocl.manage.utils.JsonUtil;
import com.qimalocl.manage.utils.LogUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jock.pickerview.view.view.OptionsPickerView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@SuppressLint("NewApi")
public class GetDotActivity extends MPermissionsActivity implements View.OnClickListener, LocationSource,
        AMapLocationListener, AMap.OnCameraChangeListener, AMap.OnMapTouchListener
        , AMap.OnMapClickListener, AMap.OnMapLongClickListener{

    int mode = 0;

    Unbinder unbinder;

    private Context context;
    private View v;

    static private final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private final static int SCANNIN_GREQUEST_CODE = 1;
//    private LoadingDialog lockLoading;
//    private LoadingDialog loadingDialog;
    private LoadingDialog loadingDialog1;
    public static boolean isForeground = false;

    private LinearLayout leftBtn;
    private LinearLayout rightBtn;
    private LinearLayout ll_top;
    private EditText et_dot_name;
    private LinearLayout ll_school_name;
    private TextView tv_school_name;
    private EditText et_name;
    private ImageView myLocationBtn, linkBtn;
    private LinearLayout scanLock, myCommissionLayout, myLocationLayout, linkLayout;
    private ImageView closeBtn;

    String dot_name;
    private int school_id;

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
    public List<ParkingBean> parkingList = new ArrayList<>();
    private Map<Integer, Polygon> parking_map = new HashMap<>();
    private Map<Integer, Polygon> polygon_map = new HashMap<>();

    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private int n=0;
    private float accuracy = 29.0f;

//    private boolean isHidden;

    private int carType = 1;

    private Bundle savedIS;

    public double referLatitude = 0.0;
    public double referLongitude = 0.0;

//    private List<LngLatBean> list = new ArrayList<LngLatBean>();
    private List<LatLng> list = new ArrayList<LatLng>();
    private List<Marker> listMarker = new ArrayList<Marker>();
    private int pid;
    private String json_LngLat = "";
    private boolean isSubmit = true;

    Polygon polygon;
    Polygon del_polygon;


    CustomDialog.Builder customBuilder;
    private CustomDialog customDialog;
    private CustomDialog customDialog2;

    Marker delMarker;

    private OptionsPickerView pvOptions;
    private ArrayList<String> item = new ArrayList<>();
    private ArrayList<SchoolListBean> data = new ArrayList<>();

    private boolean isUpdate;
    private boolean isAdd;

    ParkingBean parkingBean;

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


        initView();

        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("是否删除该点位？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                        list.add(centerMarker.getPosition());
//                        listMarker.add(dotMarker);

                        list.remove(delMarker.getPosition());
                        listMarker.remove(delMarker);
                        delMarker.remove();

                        try {
                            json_LngLat = JsonUtil.objectToJson(list);

                            LogUtil.e("del===2", list.size()+"==="+listMarker.size()+"==="+json_LngLat);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(listMarker.size()>=3){
                            List<LatLng> list2 = new ArrayList<>();

                            for (int j = 0; j < listMarker.size(); j ++){
                                list2.add(listMarker.get(j).getPosition());
                            }

                            PolygonOptions pOption = new PolygonOptions();
                            pOption.addAll(list2);

                            if(polygon!=null){
                                polygon.remove();
                            }

                            polygon = aMap.addPolygon(pOption.strokeWidth(3)
                                    .strokeColor(Color.argb(128, 255, 167, 243))    //#FFA7F3   50%
                                    .fillColor(Color.argb(76, 0, 128, 255)));    //#0080FF   30%
                        }else{
                            polygon.remove();
                        }

                        dialog.cancel();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        customDialog = customBuilder.create();

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

        loadingDialog1 = new LoadingDialog(context);
        loadingDialog1.setCancelable(false);
        loadingDialog1.setCanceledOnTouchOutside(false);

        leftBtn =  (LinearLayout)findViewById(R.id.ll_backBtn);
        rightBtn =  (LinearLayout)findViewById(R.id.ll_rightBtn);
        ll_top =  (LinearLayout)findViewById(R.id.ll_top);
        et_dot_name =  (EditText)findViewById(R.id.getDot_et_dot_name);
        ll_school_name =  (LinearLayout)findViewById(R.id.ll_school_name);
        tv_school_name =  (TextView)findViewById(R.id.getDot_tv_school_name);
        myLocationLayout =  (LinearLayout)findViewById(R.id.mainUI_myLocationLayout);
        confirmLayout = (LinearLayout)findViewById(R.id.getDot_confirmLayout);
        cancelLayout = (LinearLayout)findViewById(R.id.getDot_cancelLayout);
        submitLayout = (LinearLayout)findViewById(R.id.getDot_submitLayout);

        ll_top.setVisibility(View.GONE);
//        myLocationLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.GONE);
        isUpdate = false;
        isAdd = false;

        pvOptions = new OptionsPickerView(context,false);
        pvOptions.setTitle("选择学校");


        pvOptions.setPicker(item);
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);

        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                school_id = data.get(options1).getId();

//                order_type = options1;
                tv_school_name.setText(data.get(options1).getName());

            }
        });

        getSchoolList();


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
        successDescripter2 = BitmapDescriptorFactory.fromResource(R.drawable.pin_icon);
        bikeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);

        aMap.setOnMapClickListener(this);
        aMap.setOnMapLongClickListener(this);

        aMap.setOnMapTouchListener(this);
        setUpLocationStyle();

        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        ll_school_name.setOnClickListener(this);
        myLocationLayout.setOnClickListener(this);
        confirmLayout.setOnClickListener(this);
        cancelLayout.setOnClickListener(this);
        submitLayout.setOnClickListener(this);

        rightBtn.setEnabled(true);

        aMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                LogUtil.e("onMarkerDragStart===", marker+"==="+marker.getPosition()+"==="+list.contains(marker.getPosition()));

//                listMarker.remove(marker);

//                marker.setPosition(myLocation);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                LogUtil.e("onMarkerDrag===", marker+"==="+marker.getPosition());
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LogUtil.e("onMarkerDragEnd===", listMarker.size()+"==="+marker+"==="+marker.getPosition()+"==="+polygon);

//                listMarker.add(marker);

                if(listMarker.size()>=3){
                    List<LatLng> list2 = new ArrayList<>();

                    for (int j = 0; j < listMarker.size(); j ++){
                        list2.add(listMarker.get(j).getPosition());
                    }

                    list = list2;
                    json_LngLat = JsonUtil.objectToJson(list2);

                    PolygonOptions pOption = new PolygonOptions();
                    pOption.addAll(list2);

                    if(polygon!=null){
                        polygon.remove();
                    }

//                    ParkingBean parkingBean2 = parkingBean;
//                    parkingList.remove(parkingBean);

                    polygon = aMap.addPolygon(pOption.strokeWidth(3)
                            .strokeColor(Color.argb(128, 255, 167, 243))    //#FFA7F3   50%
                            .fillColor(Color.argb(76, 0, 128, 255)));    //#0080FF   30%

//                    parkingBean2.setPolygon(polygon);
//                    parkingList.add(parkingBean2);


                    parkingBean.setPolygon(polygon);
                    polygon_map.put(pid, polygon);


//                    parkingList
//
//                    int id = jsonObject.getInt("id");
//                    String name = jsonObject.getString("name");
//                    int school_id = jsonObject.getInt("school_id");
//                    String school_name = jsonObject.getString("school_name");
//
//                    ParkingBean parkingBean = new ParkingBean();
//                    parkingBean.setId(id);
//                    parkingBean.setName(name);
//                    parkingBean.setSchool_id(school_id);
//                    parkingBean.setSchool_name(school_name);
//                    parkingBean.setPolygon(polygon);
//
//                    parkingList.add(parkingBean);



                    LogUtil.e("onMarkerDragEnd===1", polygon_map.size()+"==="+polygon_map);
                }

            }
        });

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LogUtil.e("onMarkerClick===", referLatitude+"==="+referLongitude+"==="+marker.getPosition().latitude+"==="+marker.getPosition().longitude);

                if(marker.getTitle()!=null && !"".equals(marker.getTitle())){
//                    ll_top.setVisibility(View.GONE);
//                    ll_top_navi.setVisibility(View.VISIBLE);
//                    isNavi = true;
//
//                    LogUtil.e("onMarkerClick===1", ll_top_navi.isShown()+"==="+marker.getTitle()+"==="+marker.getTitle().split("-")[0]);
//
//                    markerPosition = marker.getPosition();
//                    tv_navi_name.setText(marker.getTitle());
                }

                delMarker = marker;
                customDialog.show();

                return true;
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng point) {
        LogUtil.e("onMapLongClick===", listMarker.size() + "===" + point.latitude+"===" + point.longitude+"===" + point);

        if(isAdd || isUpdate){
            return;
        }

        for (int i = 0; i < parkingList.size(); i ++){

            parkingBean = parkingList.get(i);

            if(parkingBean.getPolygon().contains(point)){
//                LogUtil.e("onMapClick===2", key+"==="+parking_map.get(key).getPoints());

                for (int j = 0; j < listMarker.size(); j ++){
                    listMarker.get(j).remove();
                }

                if(listMarker.size()>0){
                    listMarker.clear();
                }

                pid = parkingBean.getId();
                dot_name = parkingBean.getName();
                et_dot_name.setText(parkingBean.getName());
                school_id = parkingBean.getSchool_id();
                tv_school_name.setText(parkingBean.getSchool_name());
                del_polygon = parkingBean.getPolygon();
                json_LngLat = JsonUtil.objectToJson(del_polygon.getPoints());

                LogUtil.e("onMapLongClick===3", pid+"==="+dot_name+"==="+json_LngLat);

                customBuilder = new CustomDialog.Builder(context);
                customBuilder.setTitle("温馨提示").setMessage("是否删除"+dot_name+"电子围栏？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                delete_parking();

                                dialog.cancel();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                customDialog2 = customBuilder.create();
                customDialog2.show();

            }
        }

//        for (int key : parking_map.keySet()) {
//        }

    }

    @Override
    public void onMapClick(LatLng point) {
//        LogUtil.e("onMapClick===", ll_top.isShown()+"===" + routeOverLay+"===" + ll_top_navi+"===" + point.latitude+"===" + point.longitude+"===" + point);
        LogUtil.e("onMapClick===", listMarker.size() + "===" + point.latitude+"===" + point.longitude+"===" + point);

//        if(polygon!=null){
//            polygon.remove();
//        }

        if(isAdd && !isUpdate){
            return;
        }


        for (int i = 0; i < parkingList.size(); i ++){

//            parkingBean = parkingList.get(i);

            if(parkingList.get(i).getPolygon().contains(point)){
//                LogUtil.e("onMapClick===2", key+"==="+parking_map.get(key).getPoints());

                parkingBean = parkingList.get(i);

                for (int j = 0; j < listMarker.size(); j ++){
                    listMarker.get(j).remove();
                }

                if(listMarker.size()>0){
                    listMarker.clear();
                }

                pid = parkingBean.getId();
                et_dot_name.setText(parkingBean.getName());
                school_id = parkingBean.getSchool_id();
                tv_school_name.setText(parkingBean.getSchool_name());
                polygon = parkingBean.getPolygon();
                list = polygon.getPoints();

                list.remove(list.size()-1);

                json_LngLat = JsonUtil.objectToJson(list);

                LogUtil.e("onMapClick===4", list.size()+"==="+parkingBean.getName()+"==="+json_LngLat);

                for (int j = 0; j < list.size(); j ++){
                    MarkerOptions centerMarkerOption = new MarkerOptions().position(list.get(j)).icon(successDescripter2);
                    dotMarker = aMap.addMarker(centerMarkerOption);
                    dotMarker.setDraggable(true);

                    listMarker.add(dotMarker);
                }

                rightBtn.setEnabled(false);
                ll_top.setVisibility(View.VISIBLE);
//                myLocationLayout.setVisibility(View.VISIBLE);
                confirmLayout.setVisibility(View.VISIBLE);

                isUpdate = true;
                isAdd = false;

            }
        }

//        for (int key : parking_map.keySet()) {
//
////            LogUtil.e("onMapClick===1", key + "===" + parking_map.get(key));
//
//            if(parking_map.get(key).contains(point)){
////                LogUtil.e("onMapClick===2", key+"==="+parking_map.get(key).getPoints());
//                LogUtil.e("onMapClick===3", key+"==="+JsonUtil.objectToJson(parking_map.get(key).getPoints()));
//
//                pid = key;
//
//            }
//        }

//        for ( int i = 0; i < pOptions.size(); i++){
//
////            isContainsList.add(pOptions.get(i).contains(new LatLng(Double.parseDouble(bean.getLatitude()), Double.parseDouble(bean.getLongitude()))));
//
//            if(pOptions.get(i).contains(point)){
//                LogUtil.e("onMapClick===2", "==="+pOptions.get(i).getPoints());
//            }
//
////            LogUtil.e("onMapClick===1", pOptions.get(i)+"==="+pOptions.get(i).contains(point));
////            LogUtil.e("onMapClick===1", pOptions.get(i).contains(point)+"===");
//
////            isContainsList.add(pOptions.get(i).contains(point));
//
//        }


    }

    @Override
    public void onClick(View view) {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (view.getId()){
            case R.id.ll_backBtn:
                finishMine();
                break;

            case R.id.ll_rightBtn:
                rightBtn.setEnabled(false);
                ll_top.setVisibility(View.VISIBLE);
//                myLocationLayout.setVisibility(View.VISIBLE);
                confirmLayout.setVisibility(View.VISIBLE);

                isUpdate = false;
                isAdd = true;

                et_dot_name.setText("");
                json_LngLat = "";
                getSchoolList();

                break;

            case R.id.ll_school_name:
                pvOptions.show();
                break;

            case R.id.mainUI_myLocationLayout:
                if (myLocation != null) {
                    CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
                    aMap.animateCamera(update);
                }
                break;

            case R.id.getDot_confirmLayout:
                LogUtil.e("confirmLayout===", referLatitude+"==="+referLongitude);

//                MarkerOptions centerMarkerOption = new MarkerOptions().position(new LatLng(centerMarker.getPosition().latitude, centerMarker.getPosition().longitude)).icon(successDescripter2);

                MarkerOptions centerMarkerOption;
                if(mode==1){
                    centerMarkerOption = new MarkerOptions().position(centerMarker.getPosition()).icon(successDescripter2);
                }else{
                    centerMarkerOption = new MarkerOptions().position(myLocation).icon(successDescripter2);
                }

                dotMarker = aMap.addMarker(centerMarkerOption);
                dotMarker.setDraggable(true);

                if(mode==1){
                    list.add(centerMarker.getPosition());
                }else{
                    list.add(myLocation);
                }


                listMarker.add(dotMarker);

                try {
                    json_LngLat = JsonUtil.objectToJson(list);

                    LogUtil.e("confirmLayout===2", list.size()+"==="+listMarker.size()+"==="+json_LngLat);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(list.size()>=3){
                    List<LatLng> list2 = new ArrayList<>();


                    for (int j = 0; j < listMarker.size(); j ++){
//                        LatLng latLng = new LatLng(listMarker.get(j).getPosition(), list.get(j).getLng());
                        list2.add(listMarker.get(j).getPosition());
                    }

                    PolygonOptions pOption = new PolygonOptions();
                    pOption.addAll(list2);

                    if(polygon!=null){
                        polygon.remove();
                    }

                    polygon = aMap.addPolygon(pOption.strokeWidth(3)
                            .strokeColor(Color.argb(128, 255, 167, 243))    //#FFA7F3   50%
                            .fillColor(Color.argb(76, 0, 128, 255)));    //#0080FF   30%
                }


                break;

            case R.id.getDot_cancelLayout:

//                LogUtil.e("cancelLayout===", centerMarker.getPosition()+"==="+centerMarker.getPosition().latitude+"==="+centerMarker.getPosition().longitude+"==="+referLongitude+"==="+referLatitude);

//                if(dotMarker!=null){
//                    dotMarker.remove();
//                }
//
//                if(list!=null && list.size()>=1){
//                    list.remove(list.size()-1);
//                }

                refresh();

                break;

            case R.id.getDot_submitLayout:

//                if("".equals(et_name.getText().toString())){
//                    ToastUtil.showMessageApp(context, "名称不能为空！");
//                    return;
//                }

                if(isSubmit){
                    isSubmit = false;

                    if(isUpdate && !isAdd){
                        update_parking();
                    }else if(!isUpdate && isAdd){
                        add_parking();
                    }


//                    submit();
                }else{
                    ToastUtil.showMessageApp(context, "不能重复提交！");
                }

                break;

            default:
                break;
        }
    }

    private void getSchoolList(){

        LogUtil.e("getSchoolList===", "===");

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token != null && !"".equals(access_token)) {
            HttpHelper.get(context, Urls.user, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在加载");
                        loadingDialog.show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        LogUtil.e("getSchoolList===1", "==="+responseString);

                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        UserBean bean = JSON.parseObject(result.getData(), UserBean.class);

                        String[] schools = bean.getSchools();

                        LogUtil.e("getSchoolList===2", schools+"===");

//                        if (schoolList.size() != 0 || !schoolList.isEmpty()){
//                            schoolList.clear();
//                        }
                        if (item.size() != 0 || !item.isEmpty()){
                            item.clear();
                        }

                        for (int i = 0; i < schools.length;i++){
                            SchoolListBean bean2 = JSON.parseObject(schools[i], SchoolListBean.class);

                            LogUtil.e("getSchoolList===3", bean2.getId()+"==="+bean2.getName());

//                            schoolList.add(bean2);
                            data.add(bean2);
                            item.add(bean2.getName());


                            if(i==0){
                                school_id = bean2.getId();
                                tv_school_name.setText(bean2.getName());
                            }
                        }

//                        setFooterType(2);
//
//                        LogUtil.e("getSchoolList===3", datas.size()+"==="+schoolList.size());
//
//                        myAdapter.notifyDataSetChanged();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }

    }

    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LogUtil.e("main===ChangeFinish_B", isContainsList.contains(true) + "》》》" + cameraPosition.target.latitude + "===" + macList.size()+">>>"+isUp + "===" + cameraPosition.target.latitude);


        if (isUp){
//            initNearby(cameraPosition.target.latitude, cameraPosition.target.longitude);

            if (centerMarker != null) {
//				animMarker();
//                m_myHandler.sendEmptyMessage(4);
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

//    /**附近车接口 */
//    private void initNearby(double latitude, double longitude){
//
////        if(isHidden) return;
//
//        LogUtil.e("main===initNearby0", latitude+"==="+longitude);
//
//        RequestParams params = new RequestParams();
//        params.put("latitude",latitude);
//        params.put("longitude",longitude);
//        params.put("type", 1);
//        HttpHelper.get(context, Urls.nearby, params, new TextHttpResponseHandler() {
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
//
////                if(isHidden) return;
//
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().equals("Success")) {
//                        JSONArray array = new JSONArray(result.getData());
//
//                        LogUtil.e("initNearby===Bike", "==="+array.length());
//
//                        for (Marker marker : bikeMarkerList){
//                            if (marker != null){
//                                marker.remove();
//                            }
//                        }
//                        if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
//                            bikeMarkerList.clear();
//                        }
//                        if (0 == array.length()){
//                            ToastUtils.showMessage("附近没有单车");
//                        }else {
//                            for (int i = 0; i < array.length(); i++){
//                                NearbyBean bean = JSON.parseObject(array.getJSONObject(i).toString(), NearbyBean.class);
//                                // 加入自定义标签
//                                MarkerOptions bikeMarkerOption = new MarkerOptions().position(new LatLng(
//                                        Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude()))).icon(bikeDescripter);
//                                Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
//                                bikeMarkerList.add(bikeMarker);
//                            }
//                        }
//                    } else {
//                        ToastUtils.showMessage(result.getMsg());
//                    }
//                } catch (Exception e) {
//
//                }
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//            }
//        });
//    }

//    public void initNearby(final double latitude, final double longitude){
    public void parking(){

//        LogUtil.e("gda===parking", latitude+"==="+longitude+"==="+SharedPreferencesUrls.getInstance().getString("access_token",""));
//
//        if(latitude==0.0 || longitude==0.0) return;
//
//        RequestParams params = new RequestParams();
//        params.put("latitude", latitude);
//        params.put("longitude", longitude);


        LogUtil.e("gda===parking", "==="+SharedPreferencesUrls.getInstance().getString("access_token",""));

        HttpHelper.get(context, Urls.parking, new TextHttpResponseHandler() {
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
                            LogUtil.e("gda===parking1", "==="+responseString);

                            final ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            m_myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (1==1 || result.getFlag().equals("Success")) {
                                            final JSONArray jsonArray = new JSONArray(result.getData());

                                            LogUtil.e("gda===parking2", jsonArray.length()+"==="+jsonArray);

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try{

//                                                        for (Marker marker : bikeMarkerList){
//                                                            if (marker != null){
//                                                                marker.remove();
//                                                            }
//                                                        }
//                                                        if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
//                                                            bikeMarkerList.clear();
//                                                        }

                                                        for (int key : polygon_map.keySet()) {
                                                            polygon_map.get(key).remove();
                                                        }

                                                        if(polygon!=null){
                                                            polygon.remove();
                                                        }

                                                        for ( int i = 0; i < parkingList.size(); i++){
                                                            parkingList.get(i).getPolygon().remove();
                                                        }
//
                                                        if (!parkingList.isEmpty() || 0 != parkingList.size()){
                                                            parkingList.clear();
                                                        }

                                                        if (0 == jsonArray.length()){
                                                            ToastUtil.showMessageApp(context, "没有电子围栏");
                                                        }else {
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                List<LatLng> list = new ArrayList<>();
                                                                List<LatLng> list2 = new ArrayList<>();
                                                                int flag=0;


                                                                JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(i).getString("parking"));
                                                                JSONArray jsonArray2 = new JSONArray(jsonObject.getString("ranges"));;

                                                                LogUtil.e("gda===parking3", jsonObject.length()+"==="+jsonObject);
                                                                LogUtil.e("gda===parking4", jsonArray2.length()+"==="+jsonArray2);

                                                                for (int j = 0; j < jsonArray2.length(); j++) {
                                                                    LatLng latLng = new LatLng(Double.parseDouble(jsonArray2.getJSONObject(j).getString("latitude")), Double.parseDouble(jsonArray2.getJSONObject(j).getString("longitude")));

                                                                    flag=0;
                                                                    list.add(latLng);
                                                                }

//                                                                if("江理工中德宿舍".equals(jsonObject.getString("name"))){
//                                                                    LogUtil.e("gda===parking5", list.size()+"==="+list);
//                                                                }

                                                                Polygon polygon = null;
                                                                PolygonOptions pOption = new PolygonOptions();

                                                                pOption.addAll(list);

                                                                polygon = aMap.addPolygon(pOption.strokeWidth(3)
                                                                        .strokeColor(Color.argb(128, 255, 167, 243))    //#FFA7F3   50%
                                                                        .fillColor(Color.argb(76, 0, 128, 255)));    //#0080FF   30%


                                                                int id = jsonObject.getInt("id");
                                                                String name = jsonObject.getString("name");
                                                                int school_id = jsonObject.getInt("school_id");
                                                                String school_name = jsonObject.getString("school_name");
                                                                String school_area = jsonObject.getString("school_area");
                                                                if(!"".equals(school_area)){
                                                                    school_area = "("+school_area+")";
                                                                }

                                                                ParkingBean parkingBean = new ParkingBean();
                                                                parkingBean.setId(id);
                                                                parkingBean.setName(name);
                                                                parkingBean.setSchool_id(school_id);
                                                                parkingBean.setSchool_name(school_name+school_area);
                                                                parkingBean.setPolygon(polygon);

                                                                parkingList.add(parkingBean);

//                                                                pOptions.add(polygon);
//                                                                parking_map.put(id, polygon);

                                                            }


                                                        }

                                                        LogUtil.e("main_b===parking_r5", isContainsList.size()+"==="+isContainsList.contains(true)+"==="+pOptions.size()+"==="+pOptions);

                                                        if (loadingDialog != null && loadingDialog.isShowing()){
                                                            loadingDialog.dismiss();
                                                        }
                                                    }catch (Exception e){
                                                        if (loadingDialog != null && loadingDialog.isShowing()){
                                                            loadingDialog.dismiss();
                                                        }
                                                    }
                                                }
                                            }).start();

                                        }else {
                                            if (loadingDialog != null && loadingDialog.isShowing()){
                                                loadingDialog.dismiss();
                                            }
                                            ToastUtil.showMessageApp(context,result.getMsg());
                                        }
                                    }catch (Exception e){

                                        if (loadingDialog != null && loadingDialog.isShowing()){
                                            loadingDialog.dismiss();
                                        }
                                    }

//                                  isInitNearby = false;
                                }
                            });

                        } catch (Exception e) {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }

                    }
                });
            }
        });

    }

    public void delete_parking(){

        LogUtil.e("gda===delete_parking", pid+"==="+SharedPreferencesUrls.getInstance().getString("access_token",""));

        HttpHelper.delete(context, Urls.edit_parking+pid, new TextHttpResponseHandler() {
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
                            LogUtil.e("gda===delete_parking1", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            ToastUtil.showMessageApp(context, result.getMessage());

                            if(result.getStatus_code()==200){
                                del_polygon.remove();
                                parking();
                            }else{
                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            }



                        } catch (Exception e) {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    }
                });
            }
        });

    }

    public void update_parking(){

        LogUtil.e("gda===update_parking", school_id+"==="+json_LngLat+"==="+SharedPreferencesUrls.getInstance().getString("access_token",""));

        RequestParams params = new RequestParams();
        params.put("name", et_dot_name.getText().toString());
        params.put("school_id", school_id);
        params.put("ranges", json_LngLat);   // [{"longitude":119.912488,"latitude":31.756865},{"longitude":119.912402,"latitude":31.755952}]

        HttpHelper.put(context, Urls.edit_parking+pid, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());

                isSubmit = true;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.e("gda===update_parking1", "==="+responseString);

                            final ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            ToastUtil.showMessageApp(context, result.getMessage());

                            if(result.getStatus_code()==200){
                                refresh();
                            }else{
                                isSubmit = true;

                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            }
                        } catch (Exception e) {
                            isSubmit = true;

                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    }
                });
            }
        });

    }

    public void add_parking(){

        LogUtil.e("gda===add_parking", "==="+SharedPreferencesUrls.getInstance().getString("access_token",""));

        RequestParams params = new RequestParams();
        params.put("name", et_dot_name.getText().toString());
        params.put("school_id", school_id);
        params.put("ranges", json_LngLat);   // [{"longitude":119.912488,"latitude":31.756865},{"longitude":119.912402,"latitude":31.755952}]

        HttpHelper.post(context, Urls.parking, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());

                isSubmit = true;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.e("gda===add_parking1", "==="+responseString);

                            final ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            ToastUtil.showMessageApp(context, result.getMessage());

                            if(result.getStatus_code()==200){
                                refresh();
                            }else{
                                isSubmit = true;

                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            }

                        } catch (Exception e) {
                            isSubmit = true;

                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }

                    }
                });
            }
        });

    }

    void refresh(){

        for (int i = 0; i < listMarker.size(); i ++){
            listMarker.get(i).remove();
        }

        list.clear();
        listMarker.clear();

        rightBtn.setEnabled(true);
        ll_top.setVisibility(View.GONE);
//        myLocationLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.GONE);

        isUpdate = false;
        isAdd = false;

        isSubmit = true;

        et_dot_name.setText("");

        parking();
    }

    @Override
    public void onResume() {
        isForeground = true;
        super.onResume();

        LogUtil.e("main===bike", "main====onResume===");

        tz = 0;

        if (flag == 1) {
            flag = 0;
            return;
        }

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
//        aMap.setMyLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
//        aMap.setLoadOfflineData(true);
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
//	    super.activate(listener);

        mListener = listener;

        LogUtil.e("main===b", isContainsList.contains(true) + "===listener===" + mlocationClient);


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

//        if(isHidden) return;

        if (mListener != null && amapLocation != null) {

            if ((referLatitude == amapLocation.getLatitude()) && (referLongitude == amapLocation.getLongitude())) return;

            LogUtil.e("main===Changed", isContainsList.contains(true) + "》》》" + near + "===" + macList.size() + "===" + amapLocation.getLatitude() );
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

                    LogUtil.e("main===Changed>>>0", "》》》"+mFirstFix);

                    if (mFirstFix) {

                        LogUtil.e("main===Changed>>>1", "》》》");

                        mFirstFix = false;
//                        schoolRange();
//                        initNearby(amapLocation.getLatitude(), amapLocation.getLongitude());
                        parking();
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));

                        if(mode==1){
                            addChooseMarker();
                        }
//

                    } else {
//                        centerMarker.remove();
                        mCircle.remove();
//                        centerMarker=null;

//                        if (!isContainsList.isEmpty() || 0 != isContainsList.size()) {
//                            isContainsList.clear();
//                        }
//                        for (int i = 0; i < pOptions.size(); i++) {
//                            isContainsList.add(pOptions.get(i).contains(myLocation));
//                        }
                    }

                    ToastUtil.showMessage(context, isContainsList.contains(true) + "======" + near);

                    accuracy = amapLocation.getAccuracy();

//                    addChooseMarker();
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

        LogUtil.e("main===onStart_b", "===="+mlocationClient);

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
//                    LogUtil.e("main===LeScan", device + "====" + rssi + "====" + k);
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
        LogUtil.e("main===", "main====onPause");

//		closeBroadcast();

    }

    @Override
    public void onStop() {
        super.onStop();
        screen = false;
        change = false;

        LogUtil.e("bikeFrag===", "===onStop");

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

        LogUtil.e("main===", "handleReceiver===" + action + "===" + data);
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
        LogUtil.e("main===onTouch", "===" + motionEvent.getAction());


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
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);
    }



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
