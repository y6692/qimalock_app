package com.qimalocl.manage.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.bumptech.glide.Glide;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.cangyigou.marqueeviewdemo.MarqueeView;
import com.fitsleep.sunshinelibrary.utils.ConvertUtils;
import com.fitsleep.sunshinelibrary.utils.EncryptUtils;
import com.fitsleep.sunshinelibrary.utils.ToastUtils;
import com.google.gson.Gson;
import com.http.rdata.RRent;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.BindSchoolActivity;
import com.qimalocl.manage.activity.ClientManager;
import com.qimalocl.manage.activity.DeviceSelectActivity;
import com.qimalocl.manage.activity.DotSelectActivity;
import com.qimalocl.manage.activity.ExchangePowerDetailActivity;
import com.qimalocl.manage.activity.ExchangePowerRecordActivity;
import com.qimalocl.manage.activity.GetDotActivity;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.activity.MainActivity;
import com.qimalocl.manage.activity.MerchantAddressMapActivity;
import com.qimalocl.manage.activity.QueryActivity;
import com.qimalocl.manage.activity.SecretProtocolAdapter;
import com.qimalocl.manage.activity.TestXiaoanActivity;
import com.qimalocl.manage.base.BaseApplication;
import com.qimalocl.manage.base.BaseFragment;
import com.qimalocl.manage.base.BaseViewAdapter;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.core.widget.VerticalScrollTextView;
import com.qimalocl.manage.model.BadCarBean;
import com.qimalocl.manage.model.CarBean;
import com.qimalocl.manage.model.CarsBean;
import com.qimalocl.manage.model.GlobalConfig;
import com.qimalocl.manage.model.KeyBean;
import com.qimalocl.manage.model.LowPowerBean;
import com.qimalocl.manage.model.LowPowerDataBean;
import com.qimalocl.manage.model.NearbyBean;
import com.qimalocl.manage.model.PowerExchangeBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.SchoolListBean;
import com.qimalocl.manage.model.Sentence;
import com.qimalocl.manage.model.UserBean;
import com.qimalocl.manage.model.UserIndexBean;
import com.qimalocl.manage.utils.LogUtil;
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
import com.sofi.blelocker.library.protocol.IQueryOpenStateResponse;
import com.sofi.blelocker.library.search.SearchRequest;
import com.sofi.blelocker.library.search.SearchResult;
import com.sofi.blelocker.library.search.response.SearchResponse;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.mode.Battery2TxOrder;
import com.sunshine.blelibrary.mode.BatteryTxOrder;
import com.sunshine.blelibrary.mode.GetLockStatusTxOrder;
import com.sunshine.blelibrary.mode.GetTokenTxOrder;
import com.sunshine.blelibrary.mode.OpenLockTxOrder;
import com.sunshine.blelibrary.mode.XinbiaoTxOrder;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.tbit.tbitblesdk.Bike.TbitBle;
import com.tbit.tbitblesdk.Bike.model.BikeState;
import com.tbit.tbitblesdk.Bike.services.command.callback.StateCallback;
import com.tbit.tbitblesdk.protocol.Packet;
import com.tbit.tbitblesdk.protocol.callback.PacketCallback;
import com.tbit.tbitblesdk.protocol.callback.ResultCallback;
import com.tbit.tbitblesdk.user.entity.W206State;
import com.xiaoantech.sdk.XiaoanBleApiClient;
import com.xiaoantech.sdk.ble.model.Response;
import com.xiaoantech.sdk.ble.scanner.ScanResult;
import com.xiaoantech.sdk.listeners.BleCallback;
import com.xiaoantech.sdk.listeners.BleStateChangeListener;
import com.xiaoantech.sdk.listeners.ScanResultCallback;
import com.xiaosu.DataSetAdapter;
import com.xiaosu.VerticalRollingTextView;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import permissions.dispatcher.NeedsPermission;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.fitsleep.sunshinelibrary.utils.EncryptUtils.Encrypt;
import static com.qimalocl.manage.base.BaseApplication.school_carmodel_ids;
import static com.qimalocl.manage.base.BaseApplication.school_id;
import static com.qimalocl.manage.base.BaseApplication.school_id2;
import static com.qimalocl.manage.base.BaseApplication.school_latitude;
import static com.qimalocl.manage.base.BaseApplication.school_longitude;
import static com.qimalocl.manage.base.BaseApplication.school_name;
import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;
import static com.sofi.blelocker.library.utils.BluetoothUtils.unregisterReceiver;

@SuppressLint("NewApi")
public class ScanFragment extends BaseFragment implements View.OnClickListener, LocationSource
        , BleStateChangeListener, ScanResultCallback
        , AMapLocationListener,AMap.OnCameraChangeListener,AMap.OnMapTouchListener{

    Unbinder unbinder;

    private Context context;
    private Activity activity;

//    private LoadingDialog loadingDialog;
//    @BindView(R.id.msg) VerticalRollingTextView tvMsg;
    @BindView(R.id.marqueeView) MarqueeView marqueeView;
//    @BindView(R.id.lv_msg) ListView lv_msg;
    @BindView(R.id.mainUI_msg) LinearLayout llMsg;
    @BindView(R.id.mainUI_rightBtn) TextView rightBtn;

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
    @BindView(R.id.mainUI_bindSchoolLayout) LinearLayout bindSchoolLayout;
    @BindView(R.id.mainUI_inStorageLayout) LinearLayout inStorageLayout;
    @BindView(R.id.mainUI_refreshLayout) LinearLayout refreshLayout;
    @BindView(R.id.mainUI_yellow_xyt) TextView tvYellow_xyt;
    @BindView(R.id.mainUI_red_xyt) TextView tvRed_xyt;
    @BindView(R.id.mainUI_yellow_xa) TextView tvYellow_xa;
    @BindView(R.id.mainUI_red_xa) TextView tvRed_xa;
    @BindView(R.id.switcher) Switch switcher;
    @BindView(R.id.switcher_bad) Switch switcher_bad;
    @BindView(R.id.switcher_over_area) Switch switcher_over_area;
    @BindView(R.id.switcher_bike) Switch switcher_bike;
    @BindView(R.id.switcher_ebike) Switch switcher_ebike;
    @BindView(R.id.mainUI_bikeLayout) LinearLayout bikeLayout;
    @BindView(R.id.mainUI_ebikeLayout) LinearLayout ebikeLayout;
    @BindView(R.id.mainUI_conditionLayout) LinearLayout conditionLayout;
    @BindView(R.id.ll_lowpowerLayout) LinearLayout ll_lowpowerLayout;
    @BindView(R.id.ll_slowpowerLayout) LinearLayout ll_slowpowerLayout;
    @BindView(R.id.tv_lowpower) TextView tv_lowpower;
    @BindView(R.id.tv_slowpower) TextView tv_slowpower;
//    @BindView(R.id.mainUI_scanCode_lock) LinearLayout scanCodeBtn;

    @BindView(R.id.mainUI_refresh) ImageView iv_refresh;

    private LinearLayout scanCodeBtn;

    private TextView tv_electricity;
    private TextView tv_lock_status;
    private Switch lock_switcher;

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
    private BitmapDescriptor bikeDescripter_bad;
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
    private BitmapDescriptor bikeDescripter_tbtd_red;
    private BitmapDescriptor bikeDescripter_tbtd_yellow;
    private BitmapDescriptor bikeDescripter_tbtd_green;
    private BitmapDescriptor bikeDescripter_tbtd_blue;
    private BitmapDescriptor bikeDescripter_tbtd_brown;
    private BitmapDescriptor bikeDescripter_tbtf_red;
    private BitmapDescriptor bikeDescripter_tbtf_yellow;
    private BitmapDescriptor bikeDescripter_tbtf_green;
    private BitmapDescriptor bikeDescripter_tbtf_blue;
    private BitmapDescriptor bikeDescripter_tbtf_brown;
    private Handler handler = new Handler();
    private Marker centerMarker;
    private boolean isMovingMarker = false;

    private List<Marker> bikeMarkerList;
    private boolean isUp = false;

    public static double latitude = 0.0;
    public static double longitude = 0.0;
    private int isLock = 0;
    private View v;


    private String quantity;
    Marker curMarker;

    LocationManager locationManager;
    String provider = LocationManager.GPS_PROVIDER;
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    public String codenum = "";
    public String lock_name = "";
    public String lock_title = "";
    private String m_nowMac = "";
    private int carmodel_id = 1;
    private String type = "";
    private String lock_no = "";
    private String bleid = "";
    private String deviceuuid = "";
    private String electricity = "";
    private String carmodel_name = "";
    private String bad_reason = "";
    private String battery_name = "";
    private int lock_status;
    private int status;
    private int can_finish_order;
    private boolean isSearch = false;
    private float accuracy = 29.0f;
    float leveltemp = 18f;

    private Boolean isMac = false;
    private Boolean isFind = false;

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

    private PopupWindow popupwindow;

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
    private boolean isNetSuc;

    private boolean threadScan = true;

    private Dialog dialogReason;
    private EditText reasonEdit;
    private Button positiveButton;
    private Button negativeButton;
    public String reason;

    private int switchType;
    private int is_area;

    List list2 = new ArrayList();

    private boolean isForeground = true;

    private float tbt_battery;

    Timer autoUpdate;

    MyAdapter myAdapter;
    int index;
    public static boolean isMsgThread = false;

    private Handler m_myHandler2 = new Handler();

    private View customView;
    private RelativeLayout pop_win_bg;
    private ImageView iv_popup_window_cond_back;
    private PopupWindow popupwindowCond;

    private TextView tv_condition_cancel;
    private TextView tv_condition_confirm;
    private CheckBox cb_1;
    private CheckBox cb_2;
    private CheckBox cb_3;
    private CheckBox cb_4;
    private CheckBox cb_5;
    private CheckBox cb_6;

    private boolean first = true;
    private boolean isLowpowerLayout;

    public int[] school_carmodel_ids2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_scan, null);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        activity = getActivity();

//        registerReceiver(new String[] { LibraryConstants.BROADCAST_UPDATE_USER_INFO });

//        filter = new IntentFilter("data.broadcast.action");
//        activity.registerReceiver(broadcastReceiver, filter);

        mapView = activity.findViewById(R.id.mainUI_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        bikeMarkerList = new ArrayList<>();
        initView();

//        initHttp(false);


//        BleManager.getInstance().init(activity.getApplication());
//        BleManager.getInstance()
//                .enableLog(true)
//                .setReConnectCount(10, 5000)
//                .setConnectOverTime(timeout)
//                .setOperateTimeout(10000);
//
//        setScanRule();
//        scan();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//
//                    try {
//                        Thread.sleep(60 * 1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
////                    LogUtil.e("sf===thread", threadScan + "===");
//
//                    if(threadScan){
//                        m_myHandler.sendEmptyMessage(1);
//                    }
//
//                }
//            }
//        }).start();
    }


    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;

//        LogUtil.e("sf===onResume", isHidden()+ "===" + latitude + "===" + longitude);

        if(!isHidden()){
            threadScan = true;
//            if(!tvMsg.isRunning() && list2.size()>1){
//                tvMsg.run();
//            }
        }else{
            threadScan = false;
//            if(tvMsg.isRunning()){
//                tvMsg.stop();
//            }
//            if(list2.size()>0){
//                list2.clear();
//            }
        }

        closeLoadingDialog();

        boolean flag = activity.getIntent().getBooleanExtra("flag", false);


        LogUtil.e("sf===onResume", isPermission+"==="+flag+"==="+SharedPreferencesUrls.getInstance().getString("access_token", "")+"==="+type);

        mapView.onResume();

        if(flag){
            first = true;
            activity.getIntent().putExtra("flag", false);
        }

        if(isPermission){
//            if(!isNavi){
//                banner();
//                car_authority();
//            }
        }

        IntentFilter filter = new IntentFilter("data.broadcast.action");
        context.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        threadScan = false;
//        if(tvMsg.isRunning()){
//            tvMsg.stop();
//        }
//        if(list2.size()>0){
//            list2.clear();
//        }

//        isForeground = false;

        LogUtil.e("sf===onPause", isPermission+"==="+SharedPreferencesUrls.getInstance().getString("access_token", "")+"==="+type);



//      mapView.onPause();
//      deactivate();
//		mFirstFix = false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        LogUtil.e("onHiddenChanged===Scan", mFirstFix+"==="+first+"==="+mapView+"==="+hidden);

        if(hidden){
            //pause
            threadScan = false;
//            if(tvMsg.isRunning()){
//                tvMsg.stop();
//            }
//            if(list2.size()>0){
//                list2.clear();
//            }

            if(mapView!=null){
                mapView.setVisibility(View.GONE);
            }

            end2();

//            mapView.onPause();
//            deactivate();
        }else{
            //resume
            threadScan = true;
//            if(!tvMsg.isRunning() && list2.size()>1){
//                tvMsg.run();
//            }

            if(mapView!=null){
                mapView.setVisibility(View.VISIBLE);

//                if("".equals(jsonObject.getString("latitude"))){
//                    myLocation = new LatLng(31.76446, 119.920594);
//                }else{
//                    myLocation = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));
//                }

                //TODO
//                LatLng myLocation = new LatLng(32.76446, 119.920594);
//                addChooseMarker();
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, leveltemp));
            }

            if (!mFirstFix && !first){
                initHttp(false);
            }


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

//    List list2 = new ArrayList();
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private String action = null;

        @Override
        public void onReceive(Context context, final Intent intent) {


            LogUtil.e("scan===mReceiver0", "==="+intent);

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    action = intent.getAction();

                    LogUtil.e("scan===mReceiver", "==="+action);

                    if ("data.broadcast.action".equals(action)) {

                        boolean updateData = intent.getBooleanExtra("updateData", false);

                        if(!updateData) return;



//                        myAdapter.setDatas(null);
//                        myAdapter.notifyDataSetChanged();
//                        marqueeView.setVisibility(View.GONE);
//                        marqueeView.startWithList(null);

//                        isMsgThread = false;

                        List<BadCarBean> list = (List<BadCarBean>) intent.getSerializableExtra("datas");

                        LogUtil.e("scan===mReceiver1", list.size()+"==="+list);




//                        List list2 = new ArrayList<Sentence>();
//
//                        for(int i=0; i<list.size(); i++){
//                            Sentence sen=new Sentence(i,list.get(i).getNumber()+" 需要回收，请及时完成工作");
//                            list2.add(i, sen);
//                        }

                        List listTemp = new ArrayList();

                        for(int i=0; i<list.size(); i++){
                            listTemp.add(list.get(i).getNumber()+" 需要回收，请及时完成工作");
                        }


                        LogUtil.e("scan===mReceiver2", isEquals(listTemp, list2)+"==="+list2+"==="+listTemp);

                        if(isEquals(listTemp, list2)){

                            if(listTemp.size()>0){
                                llMsg.setVisibility(View.VISIBLE);
                            }else{
                                llMsg.setVisibility(View.GONE);
                            }

                            return;
                        }

                        list2 = listTemp;


                        LogUtil.e("scan===mReceiver3", isEquals(listTemp, list2)+"==="+list2+"==="+listTemp);


                        if (marqueeView.start()) {
                            marqueeView.stopFlipping();
                        } else {
                            marqueeView.startFlipping();
                        }


                        if(list2.size()>0){
                            llMsg.setVisibility(View.VISIBLE);
                            marqueeView.setVisibility(View.VISIBLE);
                            marqueeView.startWithList(list2);
                        }else{
                            llMsg.setVisibility(View.GONE);
                        }



//                        LogUtil.e("scan===mReceiver2", list2.size()+"==="+myAdapter.getCount()+"==="+list2);
//
//
//                        if(list2.size()!=myAdapter.getCount()){
//
//                            LogUtil.e("scan===mReceiver3", list2.size()+"==="+myAdapter.getCount());
//
//                            myAdapter.setDatas(list2);
//                            myAdapter.notifyDataSetChanged();
//
//
//                            if(list2.size()>1){
//                                isMsgThread = true;
//
//                            }
//
//                        }



//

//                                try{
//
//                                    if(tvMsg.isRunning()){
//
//                                        tvMsg.stop();
//                                        tvMsg.animationEnd();
//                                        tvMsg.setEnabled(false);
////                                        tvMsg.animate()
//
//                                    }
//
////                                  dataSetAdapter.setData(list2);
//
//                                    dataSetAdapter = new DataSetAdapter<Sentence>(list2) {
//                                        @Override
//                                        protected String text(Sentence sentence) {
////                                          LogUtil.e("scan===mReceiver3", sentence+"==="+sentence.getName());
//
//                                            if(sentence.getName()==null){
//                                                return "";
//                                            }else{
//                                                return sentence.getName();
//                                            }
//                                        }
//                                    };
//
//                                    tvMsg.setDataSetAdapter(dataSetAdapter);
//                                    tvMsg.setEnabled(true);
//
//                                    LogUtil.e("scan===mReceiver4", list2.size()+"==="+tvMsg.isRunning());
//
//                                    if(list2.size()>1){
//                                        llMsg.setVisibility(View.VISIBLE);
//
//                                        if(!tvMsg.isRunning()){
//                                            tvMsg.run();
//                                        }
//
//                                    }else{
//                                        if(tvMsg.isRunning()){
//                                            tvMsg.stop();
//                                        }
//
//                                        if(list2.size()==1){
//                                            llMsg.setVisibility(View.VISIBLE);
//                                        }else if(list2.size()==0){
//                                            llMsg.setVisibility(View.GONE);
//                                        }
//
//                                    }
//                                }catch (Exception e){
//
//                                }

                    }
                }
            });

        }
    };

    public static boolean isEquals(List<String> list1,List<String> list2){
        if(null != list1 && null != list2){
            if(list1.containsAll(list2) && list2.containsAll(list1)){
                return true;
            }
            return false;
        }
        return true;
    }

//    void loop(){
//
//        LogUtil.e("loop===", "==="+index);
//
//        index++;
//
//        if(index>=lv_msg.getCount()){
//            index=0;
//        }
//
//        lv_msg.smoothScrollToPositionFromTop(index, 0, 1000);
//        lv_msg.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                lv_msg.setSelection(index);
//                loop();
//            }
//        }, 1000);
//
//        lv_msg.smoothScrollToPositionFromTop(index, 0, 1000);
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    DataSetAdapter<Sentence> dataSetAdapter;
//    DataSetAdapter<Sentence> dataSetAdapter = new DataSetAdapter<Sentence>(list2) {
//        @Override
//        protected String text(Sentence sentence) {
////            LogUtil.e("scan===mReceiver3", sentence+"==="+sentence.getName());
//
//            if(sentence.getName()==null){
//                return "";
//            }else{
//                return sentence.getName();
//            }
//        }
//    };

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

        LogUtil.e("checkGPSIsOpen==","==="+isOpen);

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
//        openGPSSettings();
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 101);
//                return;
//            }
//        }

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);



        customView = getLayoutInflater().inflate(R.layout.pop_main_condition, null, false);
        // 创建PopupWindow宽度和高度
        pop_win_bg = customView.findViewById(R.id.pop_low_power_bg);
        iv_popup_window_cond_back = customView.findViewById(R.id.popupWindowCond_back);

        tv_condition_cancel = customView.findViewById(R.id.tv_condition_cancel);
        tv_condition_confirm = customView.findViewById(R.id.tv_condition_confirm);
        cb_1 = customView.findViewById(R.id.cb_1);
        cb_2 = customView.findViewById(R.id.cb_2);
        cb_3 = customView.findViewById(R.id.cb_3);
        cb_4 = customView.findViewById(R.id.cb_4);
        cb_5 = customView.findViewById(R.id.cb_5);
        cb_6 = customView.findViewById(R.id.cb_6);

//        cb_2.setText("只看坏车");
//        cb_3.setVisibility(View.GONE);
//        LogUtil.e("cb_2====", "==="+cb_2.getText());

        iv_popup_window_cond_back.setOnClickListener(this);

        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(activity);
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_cond_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_cond_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_cond_back.setBackgroundColor(0x77000000);
        }
//        // 打开弹窗
//        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back_low_power);
        // 创建PopupWindow宽度和高度
        popupwindowCond = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        //设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
        popupwindowCond.setAnimationStyle(R.style.PopupAnimation);
        popupwindowCond.setOutsideTouchable(false);

//        customView.setFocusable(true);
//        customView.setFocusableInTouchMode(true);
        customView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                LogUtil.e("popup===onKey", "==="+keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    dismissPopupWindow();
                    return true;
                }
                return false;
            }
        });

        iv_popup_window_cond_back.setOnClickListener(this);


//        customBuilder = new CustomDialog.Builder(context);

        dialogReason = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.pop_scrapped, null);
        dialogReason.setContentView(dialogView);
        dialogReason.setCanceledOnTouchOutside(false);
        dialogReason.getWindow().setBackgroundDrawableResource(R.drawable.block_dialog_bcg);

        reasonEdit = (EditText)dialogView.findViewById(R.id.pop_reasonEdit);
        positiveButton = (Button)dialogView.findViewById(R.id.positiveButton);
        negativeButton = (Button)dialogView.findViewById(R.id.negativeButton);


        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = reasonEdit.getText().toString().trim();
                if (reason == null || "".equals(reason)){
                    Toast.makeText(context,"请输入维修部位",Toast.LENGTH_SHORT).show();
                    return;
                }

                InputMethodManager manager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏

                if (dialogReason.isShowing()) {
                    dialogReason.dismiss();
                }

                LogUtil.e("sf===onC", "==="+type);

                carbadaction(2);

//                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                customBuilder.setTitle("温馨提示").setMessage("是否确定提交?")
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//
//                            }
//                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//
//                        LogUtil.e("sf===onC", "==="+type);
//
//                        carbadaction(2);
//
//                    }
//                });
//                customBuilder.create().show();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager manager1= (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                manager1.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
                if (dialogReason.isShowing()) {
                    dialogReason.dismiss();
                }
            }
        });

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        aMap.setMapType(AMap.MAP_TYPE_NAVI);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
        aMap.getUiSettings().setLogoBottomMargin(-100);
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18f);// 设置缩放监听
        aMap.moveCamera(cameraUpdate);

        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
        bikeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);
        bikeDescripter_bad = BitmapDescriptorFactory.fromResource(R.drawable.bike_bad_icon);

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

        bikeDescripter_tbtd_red = BitmapDescriptorFactory.fromResource(R.drawable.ebike_tbt_red_icon);
        bikeDescripter_tbtd_yellow = BitmapDescriptorFactory.fromResource(R.drawable.ebike_tbt_yellow_icon);
        bikeDescripter_tbtd_green = BitmapDescriptorFactory.fromResource(R.drawable.ebike_tbt_green_icon);
        bikeDescripter_tbtd_blue = BitmapDescriptorFactory.fromResource(R.drawable.ebike_tbt_blue_icon);
        bikeDescripter_tbtd_brown = BitmapDescriptorFactory.fromResource(R.drawable.ebike_tbt_brown_icon);

        bikeDescripter_tbtf_red = BitmapDescriptorFactory.fromResource(R.drawable.ebike_tbtf_red_icon);
        bikeDescripter_tbtf_yellow = BitmapDescriptorFactory.fromResource(R.drawable.ebike_tbtf_yellow_icon);
        bikeDescripter_tbtf_green = BitmapDescriptorFactory.fromResource(R.drawable.ebike_tbtf_green_icon);
        bikeDescripter_tbtf_blue = BitmapDescriptorFactory.fromResource(R.drawable.ebike_tbtf_blue_icon);
        bikeDescripter_tbtf_brown = BitmapDescriptorFactory.fromResource(R.drawable.ebike_tbtf_brown_icon);


        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                curMarker = marker;

                marker.setTitle(marker.getTitle());

                LogUtil.e("onMarkerClick===", marker.getTitle()+"==="+marker.getTitle().split("-")[0]);

//                codenum = marker.getTitle().split("-")[0];
//                quantity = marker.getTitle().split("-")[1];

                codenum = marker.getTitle();

                lockInfo();

                return true;
            }
        });


        aMap.setOnMapTouchListener(ScanFragment.this);
        setUpLocationStyle();

//        LogUtil.e("zoom==", "=="+aMap.getCameraPosition().zoom);


//        mSampleView = (VerticalScrollTextView) findViewById(R.id.sampleView1);
//        List lst=new ArrayList<Sentence>();
//        for(int i=0;i<30;i++){
//            if(i%2==0){
//                Sentence sen=new Sentence(i,i+"、金球奖三甲揭晓 C罗梅西哈维入围 ");
//                lst.add(i, sen);
//            }else{
//                Sentence sen=new Sentence(i,i+"、公牛欲用三大主力换魔兽？？？？");
//                lst.add(i, sen);
//            }
//        }
//        //给View传递数据
//        tvMsg.setList(lst);
//        //更新View
//        tvMsg.updateUI();

        scanCodeBtn = activity.findViewById(R.id.mainUI_scanCode_lock);


        llMsg.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        lookBtn.setOnClickListener(this);

        lookLocationBtn.setOnClickListener(this);
        changeKeyBtn.setOnClickListener(this);
        storeageLayout.setOnClickListener(this);
        lockLayout.setOnClickListener(this);
        unLockLayout.setOnClickListener(this);
        endLayout.setOnClickListener(this);
        myLocationLayout.setOnClickListener(this);
        getDotLayout.setOnClickListener(this);
        testXALayout.setOnClickListener(this);
        bindSchoolLayout.setOnClickListener(this);
        inStorageLayout.setOnClickListener(this);
        refreshLayout.setOnClickListener(this);
        switcher.setOnClickListener(this);
        switcher_bad.setOnClickListener(this);
        switcher_over_area.setOnClickListener(this);
        switcher_bike.setOnClickListener(this);
        switcher_ebike.setOnClickListener(this);
        tv_condition_cancel.setOnClickListener(this);
        tv_condition_confirm.setOnClickListener(this);
        cb_1.setOnClickListener(this);
        cb_2.setOnClickListener(this);
        cb_3.setOnClickListener(this);
        cb_4.setOnClickListener(this);
        cb_5.setOnClickListener(this);
        cb_6.setOnClickListener(this);
        conditionLayout.setOnClickListener(this);
        scanCodeBtn.setOnClickListener(this);

        switcher_bike.setChecked(true);
        switcher_ebike.setChecked(true);
        cb_2.setChecked(true);
        cb_3.setChecked(true);

//        lv_msg.setOnItemClickListener(this);

//        if(data.isEmpty()){
//            initHttp();
//        }

//        myAdapter = new MyAdapter(context);
//        myAdapter.setDatas(null);
//        lv_msg.setAdapter(myAdapter);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                while (true){
//                    LogUtil.e("sf===thread", "==="+isMsgThread);
//
//                    if(isMsgThread){
//                        LogUtil.e("sf===1", "===");
////                        m_myHandler.sendEmptyMessage(0);
//                    }
//
//                    try {
//                        Thread.sleep(2*1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        }).start();

        marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
//                Toast.makeText(context, String.valueOf(marqueeView.getPosition()) + ". " + textView.getText(), Toast.LENGTH_SHORT).show();

                String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
                if (access_token == null || "".equals(access_token)){
                    UIHelper.goToAct(activity, LoginActivity.class);
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                }else {
                    ((MainActivity)getActivity()).changeTab(1);

//                    MainActivity.changeTab(4);
                }
            }
        });

//        autoUpdate = new Timer();
//        autoUpdate.schedule(new TimerTask(){
//            @Override
//            public void run(){
//
//                m_myHandler.post(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//
//                    }
//                });
//
//            }
//        }, 0,2000);

//        BleManager.getInstance().init(activity.getApplication());
//        BleManager.getInstance()
//                .enableLog(true)
//                .setReConnectCount(10, 5000)
//                .setConnectOverTime(timeout)
//                .setOperateTimeout(10000);
//
//        setScanRule();
//        scan();
    }

    private class MyAdapter extends BaseViewAdapter<Sentence> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_msg, null);
            }

            TextView tv_msg = BaseViewHolder.get(convertView, R.id.tv_msg);

            Sentence sen = getDatas().get(position);
            tv_msg.setText(sen.getName());
//
//            final PowerExchangeBean bean = getDatas().get(position);
//            date.setText(bean.getDate());
//            car_course.setText(bean.getCar_course());
//            car_valid.setText(bean.getCar_valid());
//            car_invalid.setText(bean.getCar_invalid());



            return convertView;
        }
    }

    private void lockInfo(){

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
//			RequestParams params = new RequestParams();
//			params.put("tokencode",result);
            HttpHelper.get(context, Urls.car+codenum, new TextHttpResponseHandler() {
                @Override
                public void onStart() {

//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在加载");
//                        loadingDialog.show();
//                    }
                    onStartCommon("正在加载");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//                    UIHelper.ToastError(context, throwable.toString());
                    onFailureCommon(throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                LogUtil.e("sf===lockInfo", "==="+responseString);

                                CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                                codenum = bean.getNumber();
                                type = ""+bean.getLock_id();
                                lock_name = bean.getLock_name();	//车锁名称(英文)
                                lock_title = bean.getLock_title();	//车锁名称(中文)
                                deviceuuid = bean.getVendor_lock_id();
                                lock_status = bean.getLock_status();	//0未知 1已上锁 2已开锁 3离线
                                lock_no = bean.getLock_no();
                                m_nowMac = bean.getLock_mac();
                                bleid = bean.getLock_secretkey();
                                electricity = bean.getElectricity();
                                carmodel_id = bean.getCarmodel_id();
                                carmodel_name = bean.getCarmodel_name();
                                status = bean.getStatus();
                                can_finish_order = bean.getCan_finish_order();	//可否结束订单（有无进行中行程）1有 0无
                                bad_reason = bean.getBad_reason();
                                battery_name = bean.getBattery_name();

                                String lock_secretkey = bean.getLock_secretkey();
                                String lock_password = bean.getLock_password();

                                if("9".equals(type) || "10".equals(type) || "12".equals(type)){
                                    Config.newKey = hexStringToByteArray(lock_secretkey);
                                    Config.passwordnew = hexStringToByteArray(lock_password);
                                }else if("2".equals(type) || "3".equals(type)){
                                    Config.newKey = Config.newKey2;
                                    Config.passwordnew = Config.passwordnew2;
                                }

                                LogUtil.e("sf===lockInfo1", codenum+"==="+type+"==="+carmodel_id+"==="+m_nowMac+"==="+lock_status+"==="+can_finish_order);

                                end2();

                                if(carmodel_id==1){
                                    initmPopupWindowView();
                                }else if(carmodel_id==2){
                                    initmPopupWindowView2();
                                }


                            } catch (Exception e) {


                                LogUtil.e("Test","异常"+e);
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

    private void lockInfo2(){

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
//			RequestParams params = new RequestParams();
//			params.put("tokencode",result);
            HttpHelper.get(context, Urls.car+codenum, new TextHttpResponseHandler() {
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

                                LogUtil.e("sf===lockInfo", "==="+responseString);

                                CarBean bean = JSON.parseObject(result.getData(), CarBean.class);

                                codenum = bean.getNumber();
                                type = ""+bean.getLock_id();
                                lock_name = bean.getLock_name();	//车锁名称(英文)
                                lock_title = bean.getLock_title();	//车锁名称(中文)
                                deviceuuid = bean.getVendor_lock_id();
                                lock_status = bean.getLock_status();	//0未知 1已上锁 2已开锁 3离线
                                lock_no = bean.getLock_no();
                                m_nowMac = bean.getLock_mac();
                                bleid = bean.getLock_secretkey();
                                electricity = bean.getElectricity();
                                carmodel_id = bean.getCarmodel_id();
                                carmodel_name = bean.getCarmodel_name();
                                status = bean.getStatus();
                                can_finish_order = bean.getCan_finish_order();	//可否结束订单（有无进行中行程）1有 0无
                                bad_reason = bean.getBad_reason();

                                String lock_secretkey = bean.getLock_secretkey();
                                String lock_password = bean.getLock_password();

                                if("9".equals(type) || "10".equals(type) || "12".equals(type)){
                                    Config.newKey = hexStringToByteArray(lock_secretkey);
                                    Config.passwordnew = hexStringToByteArray(lock_password);
                                }else if("2".equals(type) || "3".equals(type)){
                                    Config.newKey = Config.newKey2;
                                    Config.passwordnew = Config.passwordnew2;
                                }

                                LogUtil.e("sf===lockInfo1", codenum+"==="+type+"==="+carmodel_id+"==="+m_nowMac+"==="+lock_status+"==="+can_finish_order);

                                if(carmodel_id==1){
                                    initmPopupRentWindowView();
                                }else if(carmodel_id==2){
                                    initmPopupRentWindowView2();
                                }


                            } catch (Exception e) {


                                LogUtil.e("Test","异常"+e);
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
                LogUtil.e("mf===onScanStarted", "==="+success);

            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);

                LogUtil.e("mf===onLeScan", bleDevice+"==="+bleDevice.getMac());
            }

            @Override
            public void onScanning(final BleDevice bleDevice) {
//                mDeviceAdapter.addDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();

                LogUtil.e("mf===onScanning", bleDevice+"==="+bleDevice.getMac());


            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
//                img_loading.clearAnimation();
//                img_loading.setVisibility(View.INVISIBLE);
//                btn_scan.setText(getString(R.string.start_scan));

                LogUtil.e("mf===onScanFinished", scanResultList+"==="+scanResultList.size());
            }
        });
    }


    public void initmPopupRentWindowView(){
        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_rent_bike, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = customView.findViewById(R.id.pop_rent_bg);
        ImageView iv_popup_window_back = customView.findViewById(R.id.popupWindow_back);

        TextView tv_number = customView.findViewById(R.id.tv_number);
        TextView tv_lock_title = customView.findViewById(R.id.tv_lock_title);
        TextView tv_lock_mac = customView.findViewById(R.id.tv_lock_mac);
        tv_electricity = customView.findViewById(R.id.tv_electricity);
        tv_lock_status = customView.findViewById(R.id.tv_lock_status);
        LinearLayout ll_open_lock = customView.findViewById(R.id.ll_open_lock);
        LinearLayout ll_get_state = customView.findViewById(R.id.ll_get_state);
        LinearLayout ll_end_order = customView.findViewById(R.id.ll_end_order);

        LinearLayout ll_bad = customView.findViewById(R.id.ll_bad);
        LinearLayout ll_bad2 = customView.findViewById(R.id.ll_bad2);
        TextView tv_bad_reason = customView.findViewById(R.id.tv_bad_reason);
        TextView tv_bad_reason2 = customView.findViewById(R.id.tv_bad_reason2);
        LinearLayout ll_set_recovered = customView.findViewById(R.id.ll_set_recovered);
        LinearLayout ll_set_ok = customView.findViewById(R.id.ll_set_ok);
        TextView tv_set_useless = customView.findViewById(R.id.tv_set_useless);
        LinearLayout ll_query = customView.findViewById(R.id.ll_query);

        LogUtil.e("PopupRent===", codenum+"==="+electricity);

        tv_number.setText(codenum);
        tv_lock_title.setText(lock_name);
        tv_lock_mac.setText(m_nowMac);
        tv_electricity.setText("");
        tv_lock_status.setText("");
        tv_bad_reason.setText("损坏部位："+bad_reason);      //TODO 损坏部位没有使用后台该车最新的坏车原因
        tv_bad_reason2.setText("损坏部位："+bad_reason);


        if(can_finish_order==1){
            ll_end_order.setVisibility(View.VISIBLE);
        }else{
            ll_end_order.setVisibility(View.GONE);
        }

        if(status==3 || status==4){    //车辆状态 0待投放 1正常 2锁定 3确认为坏车 4坏车已回收 5调运中 6报废
            ll_bad.setVisibility(View.VISIBLE);

            if(isSearch){
                ll_bad.setVisibility(View.GONE);
                ll_bad2.setVisibility(View.VISIBLE);
            }else{
                ll_bad.setVisibility(View.VISIBLE);
                ll_bad2.setVisibility(View.GONE);
            }
        }else{
            ll_bad.setVisibility(View.GONE);
            ll_bad2.setVisibility(View.GONE);
        }

        LogUtil.e("initmPopup===", can_finish_order+"==="+status);

        ll_open_lock.setOnClickListener(this);
        ll_end_order.setOnClickListener(this);
        ll_get_state.setOnClickListener(this);
        ll_set_recovered.setOnClickListener(this);
        ll_set_ok.setOnClickListener(this);
        tv_set_useless.setOnClickListener(this);
        iv_popup_window_back.setOnClickListener(this);
        ll_query.setOnClickListener(this);

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

//        ll_get_state.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });



//        if ("2".equals(type) || "3".equals(type)) {    //单车蓝牙锁
//
//            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                popupwindow.dismiss();
//            }
//            //蓝牙锁
//            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//
//            mBluetoothAdapter = bluetoothManager.getAdapter();
//
//            if (mBluetoothAdapter == null) {
//                ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                popupwindow.dismiss();
//                return;
//            }
//            if (!mBluetoothAdapter.isEnabled()) {
//                isPermission = false;
//                closeLoadingDialog2();
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, 188);
//            } else {
//
//                LogUtil.e("order===2",  "===" + type);
//
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在连接");
//                    loadingDialog.show();
//                }
//
//                BleManager.getInstance().init(activity.getApplication());
//                BleManager.getInstance()
//                        .enableLog(true)
//                        .setReConnectCount(10, 5000)
//                        .setConnectOverTime(timeout)
//                        .setOperateTimeout(10000);
//
//                isOpenLock = false;
//                connect();
//            }
//        }else
        if("5".equals(type) || "6".equals(type)){
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

                LogUtil.e("order===2",  "===" + type);

//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在连接");
//                    loadingDialog.show();
//                }
//
//                isOpenLock = false;
//                m_myHandler.sendEmptyMessage(0x98);
            }

        }

    }

    public void initmPopupRentWindowView2(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_rent_ebike, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = customView.findViewById(R.id.pop_rent_bg);
        ImageView iv_popup_window_back = customView.findViewById(R.id.popupWindow_back);


        TextView tv_number = customView.findViewById(R.id.tv_number);
        TextView tv_lock_title = customView.findViewById(R.id.tv_lock_title);
        TextView tv_deviceuuid = customView.findViewById(R.id.tv_deviceuuid);
        tv_lock_status = customView.findViewById(R.id.tv_lock_status);
        TextView tv_lock_mac = customView.findViewById(R.id.tv_lock_mac);
        tv_electricity = customView.findViewById(R.id.tv_electricity);
        TextView tv_battery_name = customView.findViewById(R.id.tv_battery_name);

//        lock_switcher = customView.findViewById(R.id.lock_switcher);
        LinearLayout ll_open_lock = customView.findViewById(R.id.ll_open_lock);
        LinearLayout ll_close_lock = customView.findViewById(R.id.ll_close_lock);
        LinearLayout ll_car_search = customView.findViewById(R.id.ll_car_search);
        LinearLayout ll_power_exchange = customView.findViewById(R.id.ll_power_exchange);
        LinearLayout ll_end_order = customView.findViewById(R.id.ll_end_order);

        LinearLayout ll_bad = customView.findViewById(R.id.ll_bad);
        LinearLayout ll_bad2 = customView.findViewById(R.id.ll_bad2);
        TextView tv_bad_reason = customView.findViewById(R.id.tv_bad_reason);
        TextView tv_bad_reason2 = customView.findViewById(R.id.tv_bad_reason2);
        LinearLayout ll_set_recovered = customView.findViewById(R.id.ll_set_recovered);
        LinearLayout ll_set_ok = customView.findViewById(R.id.ll_set_ok);
        TextView tv_set_useless = customView.findViewById(R.id.tv_set_useless);
        LinearLayout ll_query = customView.findViewById(R.id.ll_query);

        tv_number.setText(codenum);
        tv_lock_title.setText(lock_name);
        tv_deviceuuid.setText(deviceuuid);
        if(!"7".equals(type) && !"11".equals(type)){    //状态不准，所以不要显示
            tv_lock_status.setText(lock_status==0?"未知":lock_status==1?"已上锁":lock_status==2?"已开锁":lock_status==3?"离线":"正常");
        }

        tv_lock_mac.setText(m_nowMac);
        tv_electricity.setText(electricity);
        tv_battery_name.setText(battery_name);
        tv_bad_reason.setText("损坏部位："+bad_reason);
        tv_bad_reason2.setText("损坏部位："+bad_reason);

        if(isSearch){
            ll_car_search.setVisibility(View.VISIBLE);
        }else{
            ll_car_search.setVisibility(View.GONE);
        }

        if(can_finish_order==1){
            ll_end_order.setVisibility(View.VISIBLE);
        }else{
            ll_end_order.setVisibility(View.GONE);
        }

        if(status==3 || status==4){    //车辆状态 0待投放 1正常 2锁定 3确认为坏车 4坏车已回收 5调运中 6报废
            ll_bad.setVisibility(View.VISIBLE);

            if(isSearch){
                ll_bad.setVisibility(View.GONE);
                ll_bad2.setVisibility(View.VISIBLE);
            }else{
                ll_bad.setVisibility(View.VISIBLE);
                ll_bad2.setVisibility(View.GONE);
            }
        }else{
            ll_bad.setVisibility(View.GONE);
            ll_bad2.setVisibility(View.GONE);
        }

//        if(lock_status==2){
//            lock_switcher.setChecked(true);
//        }else{
//            lock_switcher.setChecked(false);
//        }


        LogUtil.e("initmPopup===2", lock_status+"==="+can_finish_order+"==="+status);



//        lock_switcher.setOnClickListener(this);     //TODO 行运兔电单车的弹窗开关锁按钮无效
        ll_open_lock.setOnClickListener(this);
        ll_close_lock.setOnClickListener(this);
        ll_power_exchange.setOnClickListener(this);
        ll_car_search.setOnClickListener(this);
        ll_end_order.setOnClickListener(this);
        tv_set_useless.setOnClickListener(this);
        ll_set_recovered.setOnClickListener(this);
        ll_set_ok.setOnClickListener(this);
        iv_popup_window_back.setOnClickListener(this);
        ll_query.setOnClickListener(this);

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

    }

    public void initmPopupWindowView(){
        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_menu, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = customView.findViewById(R.id.pop_rent_bg);
        ImageView iv_popup_window_back = customView.findViewById(R.id.popupWindow_back);

        TextView tv_number = customView.findViewById(R.id.tv_number);
        TextView tv_lock_title = customView.findViewById(R.id.tv_lock_title);
        TextView tv_lock_mac = customView.findViewById(R.id.tv_lock_mac);
        tv_electricity = customView.findViewById(R.id.tv_electricity);
        tv_lock_status = customView.findViewById(R.id.tv_lock_status);
        LinearLayout ll_open_lock = customView.findViewById(R.id.ll_open_lock);
        LinearLayout ll_end_order = customView.findViewById(R.id.ll_end_order);
        LinearLayout ll_get_state = customView.findViewById(R.id.ll_get_state);

        LinearLayout ll_bad = customView.findViewById(R.id.ll_bad);
        TextView tv_bad_reason = customView.findViewById(R.id.tv_bad_reason);
        LinearLayout ll_set_recovered = customView.findViewById(R.id.ll_set_recovered);
        LinearLayout ll_set_ok = customView.findViewById(R.id.ll_set_ok);
        TextView tv_set_useless = customView.findViewById(R.id.tv_set_useless);
        LinearLayout ll_query = customView.findViewById(R.id.ll_query);

        LogUtil.e("PopupRent===", codenum+"==="+electricity);

        tv_number.setText(codenum);
        tv_lock_title.setText(lock_name);
        tv_lock_mac.setText(m_nowMac);
        tv_electricity.setText("");
        tv_lock_status.setText("");
        tv_bad_reason.setText("损坏部位："+bad_reason);      //TODO 损坏部位没有使用后台该车最新的坏车原因
        if(can_finish_order==1){
            ll_end_order.setVisibility(View.VISIBLE);
        }else{
            ll_end_order.setVisibility(View.GONE);
        }

        if(status==3 || status==4){    //车辆状态 0待投放 1正常 2锁定 3确认为坏车 4坏车已回收 5调运中 6报废
            ll_bad.setVisibility(View.VISIBLE);
        }else{
            ll_bad.setVisibility(View.GONE);
        }

        LogUtil.e("initmPopup===", can_finish_order+"==="+status);

        ll_open_lock.setOnClickListener(this);
        ll_end_order.setOnClickListener(this);
        ll_get_state.setOnClickListener(this);
        ll_set_recovered.setOnClickListener(this);
        ll_set_ok.setOnClickListener(this);
        tv_set_useless.setOnClickListener(this);
        iv_popup_window_back.setOnClickListener(this);
        ll_query.setOnClickListener(this);

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



//        if ("2".equals(type) || "3".equals(type)) {    //单车蓝牙锁
//
//            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                popupwindow.dismiss();
//            }
//            //蓝牙锁
//            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//
//            mBluetoothAdapter = bluetoothManager.getAdapter();
//
//            if (mBluetoothAdapter == null) {
//                ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                popupwindow.dismiss();
//                return;
//            }
//            if (!mBluetoothAdapter.isEnabled()) {
//                isPermission = false;
//                closeLoadingDialog2();
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, 188);
//            } else {
//
//                LogUtil.e("order===2",  type+"===" +isMac );
//
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在连接");
//                    loadingDialog.show();
//                }
//
//                BleManager.getInstance().init(activity.getApplication());
////                BleManager.getInstance()
////                        .enableLog(true)
////                        .setReConnectCount(10, 5000)
////                        .setConnectOverTime(timeout)
////                        .setOperateTimeout(10000);
//
//                isOpenLock = false;
//
//
//                if(isMac){
//                    connect();
//                }else{
//                    setScanRule();
//                    scan2();
//                }
//            }
//        }else if("5".equals(type) || "6".equals(type)){
//            if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                popupwindow.dismiss();
//            }
//            //蓝牙锁
//            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//
//            mBluetoothAdapter = bluetoothManager.getAdapter();
//
//            if (mBluetoothAdapter == null) {
//                ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                popupwindow.dismiss();
//                return;
//            }
//            if (!mBluetoothAdapter.isEnabled()) {
//                isPermission = false;
//                closeLoadingDialog2();
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, 188);
//            } else {
//
//                LogUtil.e("order===2",  "===" + type);
//
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在连接");
//                    loadingDialog.show();
//                }
//
//                isOpenLock = false;
//                m_myHandler.sendEmptyMessage(0x98);
//            }
//
//        }

    }

    public void initmPopupWindowView2(){

        // 获取自定义布局文件的视图
//        View customView = getLayoutInflater().inflate(R.layout.pop_menu, null, false);

        View customView = LayoutInflater.from(getContext()).inflate(R.layout.pop_menu2, null, false);

        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = customView.findViewById(R.id.pop_win_bg);
        ImageView iv_popup_window_back =  customView.findViewById(R.id.popupWindow_back);

        TextView tv_number = customView.findViewById(R.id.tv_number);
        TextView tv_lock_title = customView.findViewById(R.id.tv_lock_title);
        TextView tv_deviceuuid = customView.findViewById(R.id.tv_deviceuuid);
        tv_lock_status = customView.findViewById(R.id.tv_lock_status);
        TextView tv_lock_mac = customView.findViewById(R.id.tv_lock_mac);
        TextView tv_electricity = customView.findViewById(R.id.tv_electricity);
        TextView tv_battery_name = customView.findViewById(R.id.tv_battery_name);

//        lock_switcher = customView.findViewById(R.id.lock_switcher);
        LinearLayout ll_open_lock = customView.findViewById(R.id.ll_open_lock);
        LinearLayout ll_close_lock = customView.findViewById(R.id.ll_close_lock);
        LinearLayout ll_car_search = customView.findViewById(R.id.ll_car_search);
        LinearLayout ll_power_exchange = customView.findViewById(R.id.ll_power_exchange);
        LinearLayout ll_end_order = customView.findViewById(R.id.ll_end_order);
        LinearLayout ll_query = customView.findViewById(R.id.ll_query);

        LinearLayout ll_bad = customView.findViewById(R.id.ll_bad);
        TextView tv_bad_reason = customView.findViewById(R.id.tv_bad_reason);
        LinearLayout ll_set_recovered = customView.findViewById(R.id.ll_set_recovered);
        LinearLayout ll_set_ok = customView.findViewById(R.id.ll_set_ok);
        TextView tv_set_useless = customView.findViewById(R.id.tv_set_useless);

        tv_number.setText(codenum);
        tv_lock_title.setText(lock_name);
        tv_deviceuuid.setText(deviceuuid);
        if(!"7".equals(type) && !"11".equals(type)){
            tv_lock_status.setText(lock_status==0?"未知":lock_status==1?"已上锁":lock_status==2?"已开锁":lock_status==3?"离线":"正常");
        }

        tv_lock_mac.setText(m_nowMac);
        tv_electricity.setText(electricity);
        tv_battery_name.setText(battery_name);
        tv_bad_reason.setText("损坏部位："+bad_reason);

        if(can_finish_order==1){
            ll_end_order.setVisibility(View.VISIBLE);
        }else{
            ll_end_order.setVisibility(View.GONE);
        }

        if(status==3 || status==4){    //车辆状态 0待投放 1正常 2锁定 3确认为坏车 4坏车已回收 5调运中 6报废
            ll_bad.setVisibility(View.VISIBLE);
        }else{
            ll_bad.setVisibility(View.GONE);
        }

//        if(lock_status==2){
//            lock_switcher.setChecked(true);
//        }else{
//            lock_switcher.setChecked(false);
//        }

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


//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()){
//
//
////                    case R.id.pop_menu_findBike:
////                        ddSearch();
////                        break;
////                    case R.id.pop_menu_openPowerLock:
////                        battery_unlock();
////                        break;
////                    case R.id.pop_menu_cancleBtn:
////                        popupwindow.dismiss();
////
////                        initNearby(latitude, longitude);
////
////                        break;
//                }
//
//            }
//        };

//        lock_switcher.setOnClickListener(this);
        ll_open_lock.setOnClickListener(this);
        ll_close_lock.setOnClickListener(this);
        ll_power_exchange.setOnClickListener(this);
        ll_car_search.setOnClickListener(this);
        ll_end_order.setOnClickListener(this);
        tv_set_useless.setOnClickListener(this);
        ll_set_recovered.setOnClickListener(this);
        ll_set_ok.setOnClickListener(this);
        iv_popup_window_back.setOnClickListener(this);
        ll_query.setOnClickListener(this);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

//        popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                // 改变显示的按钮图片为正常状态
//                LogUtil.e("onDismiss===", "===");
//
////                initNearby(latitude, longitude);
//                cars();
//            }
//        });

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
        LogUtil.e("ddSearch===", "==="+codenum);

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
//            RequestParams params = new RequestParams();
//            params.put("tokencode",codenum);
            HttpHelper.post(context, Urls.search+codenum, null, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在提交");
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
                                LogUtil.e("ddSearch===1", "==="+responseString);

                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

//                        if(result.getStatus_code()==200){
//                            curMarker.setIcon(bikeDescripter_blue);
//                        }else{
//
//                        }

                                Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();

//                        if (result.getFlag().equals("Success")) {
//                            Toast.makeText(context,"发送寻车指令成功",Toast.LENGTH_SHORT).show();
//                        }else {
//                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                        }
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

    void battery_unlock() {

        LogUtil.e("battery_unlock===", "==="+codenum);

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
//            RequestParams params = new RequestParams();
//            params.put("number", codenum);
            HttpHelper.post(context, Urls.battery_unlock+codenum, null, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在提交");
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

                                LogUtil.e("battery_unlock===1", type+"==="+responseString);

                                if(result.getStatus_code()==200){
//                                  m_myHandler.sendEmptyMessage(2);
                                    if(curMarker!=null){
                                        if("4".equals(type)){
                                            curMarker.setIcon(bikeDescripter_blue);
                                        }else if("7".equals(type)){
                                            curMarker.setIcon(bikeDescripter_xa_blue);
                                        }else if("8".equals(type)){
                                            curMarker.setIcon(bikeDescripter_tbtd_blue);
                                        }else if("11".equals(type)){
                                            curMarker.setIcon(bikeDescripter_tbtf_blue);
                                        }
                                    }

                                    closeLoadingDialog();
                                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                                }else if(result.getStatus_code()==503){

                                    if("11".equals(type)){
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
//                                            if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                                loadingDialog.setTitle("正在唤醒车锁");
//                                                loadingDialog.show();
//                                            }

                                            LogUtil.e("batteryBle_unlock===11", TbitBle.hasInitialized() + "===");

                                            if(!TbitBle.hasInitialized()){
                                                TbitBle.initialize(context, new SecretProtocolAdapter());
                                            }

                                            if(TbitBle.getBleConnectionState()==0){
                                                tbtble_connect_battery();

                                                isConnect = false;
//                                                m_myHandler.postDelayed(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        if (!isConnect){
////                                                          closeLoadingDialog();
//
//                                                            LogUtil.e("batteryBle_unlock===11==timeout", isConnect + "==="+activity.isFinishing());
//
////                                                          battery_unlock();
//
//                                                            ToastUtil.showMessageApp(context, "蓝牙连接失败");
//                                                        }
//                                                    }
//                                                }, timeout);
                                            }else{
                                                batteryBle_unlock();
                                            }

                                        }
                                    }else {
                                        closeLoadingDialog();
                                        Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }else {
                                    closeLoadingDialog();
                                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                closeLoadingDialog();
                            }
                        }
                    });

                }
            });
        }
    }

    void battery_report() {

        LogUtil.e("battery_report===", codenum+"==="+tbt_battery);

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("car_number", codenum);
            params.put("voltage", tbt_battery);
            HttpHelper.post(context, Urls.battery_report, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在提交");
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

                                LogUtil.e("battery_report===1", type+"==="+responseString);

                                if(result.getStatus_code()==200){
                                    ToastUtil.showMessageApp(context,"打开电池锁成功");
                                }else{
                                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void recycletask() {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
//            Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
//            UIHelper.goToAct(context, LoginActivity.class);
            return;
        }

        RequestParams params = new RequestParams();
        params.put("type", 1);
        params.put("school_id", school_id);
        params.put("page", 1);
        params.put("pagesize", GlobalConfig.PAGE_SIZE);

        LogUtil.e("sf===recycletask0", "==="+codenum);

        HttpHelper.get(context, Urls.recycletask, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {
                onFailureCommon(throwable.toString());

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        LogUtil.e("sf===recycletask1", "==="+responseString);

                        JSONArray array = new JSONArray(result.getData());

                        JSONObject jsonObject = new JSONObject(result.getMeta());
                        JSONObject json = new JSONObject(jsonObject.getString("pagination"));
                        LogUtil.e("sf===recycletask2", "==="+array);

                        int totalnum = json.getInt("count");

                        marqueeView.startWithList(null);

                        List<BadCarBean> list = new ArrayList<>();
                        for (int i = 0; i < array.length();i++){
                            BadCarBean bean = JSON.parseObject(array.getJSONObject(i).toString(), BadCarBean.class);

                            list.add(bean);
                        }

                        LogUtil.e("sf===recycletask3", list.size()+"==="+codenum);


                        Intent intent = new Intent("data.broadcast.action");
//                            intent.putExtra("datas", (Serializable)datas);
                        intent.putExtra("updateData", false);
                        intent.putExtra("count", totalnum);
                        context.sendBroadcast(intent);

//                            List<BadCarBean> list = (List<BadCarBean>) intent.getSerializableExtra("datas");

                        LogUtil.e("sf===recycletask4", list.size()+"==="+list);

//                        final List list2 = new ArrayList<Sentence>();
//
//                        for(int i=0; i<list.size(); i++){
//                            Sentence sen=new Sentence(i,list.get(i).getNumber()+" 需要回收，请及时完成工作");
//                            list2.add(i, sen);
//                        }
//
//                        LogUtil.e("sf===recycletask5", list2.size()+"==="+list2);

                        List listTemp = new ArrayList();

                        for(int i=0; i<list.size(); i++){
                            listTemp.add(list.get(i).getNumber()+" 需要回收，请及时完成工作");
                        }



                        LogUtil.e("scan===mReceiver2", isEquals(listTemp, list2)+"==="+list2+"==="+listTemp);

                        if(isEquals(listTemp, list2)){
                            return;
                        }

                        list2 = listTemp;


                        LogUtil.e("scan===mReceiver3", isEquals(listTemp, list2)+"==="+list2+"==="+listTemp);


                        if (marqueeView.start()) {
                            marqueeView.stopFlipping();
                        } else {
                            marqueeView.startFlipping();
                        }


                        if(list2.size()>0){
                            llMsg.setVisibility(View.VISIBLE);
                            marqueeView.startWithList(list2);
                        }else{
                            llMsg.setVisibility(View.GONE);
                        }


//                        try{
//
//                            if(tvMsg.isRunning()){
//                                tvMsg.stop();
//                                tvMsg.animationEnd();
//                                tvMsg.setEnabled(false);
//                            }
//
//                            LogUtil.e("sf===recycletask6", list2.size()+"==="+tvMsg.isRunning());
//
//
//                            dataSetAdapter = new DataSetAdapter<Sentence>(list2) {
//                                @Override
//                                protected String text(Sentence sentence) {
////                                    LogUtil.e("scan===mReceiver3", sentence+"==="+sentence.getName());
//
//                                    if(sentence.getName()==null){
//                                        return "";
//                                    }else{
//                                        return sentence.getName();
//                                    }
//
//                                }
//
//                            };
//
////                                    dataSetAdapter.setData(list2);
//                            tvMsg.setDataSetAdapter(dataSetAdapter);
//                            tvMsg.setEnabled(true);
//
//
//                            if(list2.size()>1){
//                                llMsg.setVisibility(View.VISIBLE);
//
//                                if(!tvMsg.isRunning()){
//                                    tvMsg.run();
//                                }
//
//                            }else{
//
//                                LogUtil.e("sf===recycletask7", list2.size()+"==="+tvMsg.isRunning());
//
////                                tvMsg.stop();
//
//                                if(list2.size()==1){
//                                    tvMsg.stop();
//                                    tvMsg.animationEnd();
//                                    llMsg.setVisibility(View.VISIBLE);
//                                }else if(list2.size()==0){
//                                    llMsg.setVisibility(View.GONE);
//                                }
//                            }
//
////                            m_myHandler.postDelayed(new Runnable() {
////                                @Override
////                                public void run() {
////
////
////                                }
////                            }, 5 * 1000);
//
//
//                        }catch (Exception e){
//
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    }
                });

            }
        });
    }

    public void initHttp(final boolean isFresh){

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token != null && !"".equals(access_token)){
            LogUtil.e("sf===user0", "==="+school_id);

            HttpHelper.get(context, Urls.user, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if(isFresh){
                        onStartCommon("正在加载");
                    }
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

                                LogUtil.e("sf===user1", "==="+responseString);

                                UserBean bean = JSON.parseObject(result.getData(), UserBean.class);
                                String[] schools = bean.getSchools();

                                if(schools.length==0){
                                    if(polygon!=null){
                                        polygon.remove();
                                    }
                                    school_id=0;
                                    school_id2=0;
                                    SharedPreferencesUrls.getInstance().putInt("school_id", 0);

                                    if (myLocation != null) {
                                        leveltemp = 18f;
                                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, leveltemp));
                                    }

                                    for (Marker marker : bikeMarkerList){
                                        if (marker != null){
                                            marker.remove();
                                        }
                                    }

                                    if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
                                        bikeMarkerList.clear();
                                    }
                                }else{
                                    boolean flag=false;
                                    for (int i = 0; i < schools.length;i++){
                                        SchoolListBean bean2 = JSON.parseObject(schools[i], SchoolListBean.class);

                                        LogUtil.e("sf===user2", isFresh+"==="+first+"==="+school_id+"==="+school_id2+"==="+bean2.getId()+"==="+SharedPreferencesUrls.getInstance().getInt("school_id", 0));

                                        if((SharedPreferencesUrls.getInstance().getInt("school_id", 0)==0 && i==0) || (SharedPreferencesUrls.getInstance().getInt("school_id", 0)!=0 && bean2.getId()==SharedPreferencesUrls.getInstance().getInt("school_id", 0))){

                                            if(school_id2 == bean2.getId()){
                                                flag=true;
                                            }

                                            if(isFresh || first || school_id2 != bean2.getId()){

                                                LogUtil.e("sf===user3_0", isFresh+"==="+school_id2+"==="+bean2.getId());

                                                school_id = bean2.getId();

                                                if(school_id2 != bean2.getId()){
                                                    operationarea();

                                                    LogUtil.e("sf===user3_1", isFresh+"==="+first+"==="+bean2.getLatitude()+"==="+bean2.getLongitude());    //32.11277===118.922955

                                                    LatLng myLocation = new LatLng(Double.parseDouble(bean2.getLatitude()), Double.parseDouble(bean2.getLongitude()));
                                                    leveltemp = 18f;
                                                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, leveltemp));
                                                }

                                                if(isFresh || first){
                                                    LatLng myLocation = new LatLng(Double.parseDouble(bean2.getLatitude()), Double.parseDouble(bean2.getLongitude()));
                                                    leveltemp = 18f;
                                                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, leveltemp));
                                                }

                                                SharedPreferencesUrls.getInstance().putInt("school_id", bean2.getId());

                                                school_id2 = school_id;

                                                LogUtil.e("sf===user3", first+"==="+school_id+"==="+bean2.getId());

                                                first = false;
                                            }

                                            school_id = bean2.getId();
                                            school_id2 = school_id;
                                            school_name = bean2.getName();
                                            school_latitude = bean2.getLatitude();
                                            school_longitude = bean2.getLongitude();

                                            LogUtil.e("sf===user3_2", school_carmodel_ids+"==="+school_carmodel_ids2+"==="+bean2.getCarmodel_ids()+"==="+first+"==="+school_id+"==="+bean2.getId());


                                            if(school_carmodel_ids2!=null){
                                                LogUtil.e("sf===user3_3", Arrays.equals(school_carmodel_ids2, bean2.getCarmodel_ids())+"==="+school_carmodel_ids.length+"==="+school_carmodel_ids2.length+"==="+bean2.getCarmodel_ids().length+"==="+first+"==="+school_id+"==="+bean2.getId());
                                            }

                                            if(school_carmodel_ids2!=null && flag && Arrays.equals(school_carmodel_ids2, bean2.getCarmodel_ids())){

                                            }else{
                                                school_carmodel_ids = bean2.getCarmodel_ids();
                                                school_carmodel_ids2 = bean2.getCarmodel_ids();

                                                ll_lowpowerLayout.setVisibility(View.GONE);
                                                ll_slowpowerLayout.setVisibility(View.GONE);
                                                cb_1.setVisibility(View.GONE);
                                                cb_2.setVisibility(View.GONE);
                                                cb_3.setVisibility(View.GONE);
                                                cb_4.setVisibility(View.GONE);
                                                cb_5.setVisibility(View.GONE);
                                                cb_6.setVisibility(View.GONE);
                                                cb_1.setChecked(false);
                                                cb_2.setChecked(false);
                                                cb_3.setChecked(false);
                                                cb_4.setChecked(false);
                                                cb_5.setChecked(false);
                                                cb_6.setChecked(false);
                                                cb_1.setText("");
                                                cb_2.setText("");
                                                cb_3.setText("");
                                                cb_4.setText("");
                                                cb_5.setText("");
                                                cb_6.setText("");
                                                is_bike = false;
                                                is_ebike = false;
                                                is_bad = false;
                                                is_area = 0;
                                                is_lowpower = false;
                                                is_slowpower = false;

                                                int[] carmodels = school_carmodel_ids;
                                                LogUtil.e("sf===user4", "==="+carmodels);

                                                if(carmodels==null || carmodels.length==0){
                                                    cb_1.setVisibility(View.VISIBLE);
                                                    cb_2.setVisibility(View.VISIBLE);
                                                    cb_1.setChecked(false);
                                                    cb_2.setChecked(false);
                                                    cb_1.setText("只看超区");
                                                    cb_2.setText("只看坏车");

                                                    isLowpowerLayout = false;
                                                    ll_lowpowerLayout.setVisibility(View.GONE);
                                                    ll_slowpowerLayout.setVisibility(View.GONE);

                                                    is_bike = false;
                                                    is_ebike = false;
                                                }else if(carmodels!=null && carmodels.length>0){

                                                    LogUtil.e("sf===user5", carmodels.length+"==="+carmodels[0]);

                                                    if(carmodels.length==1){

                                                        if(carmodels[0]==1){
                                                            cb_1.setVisibility(View.VISIBLE);
                                                            cb_2.setVisibility(View.VISIBLE);
                                                            cb_1.setChecked(false);
                                                            cb_2.setChecked(false);
                                                            cb_1.setText("只看超区");
                                                            cb_2.setText("只看坏车");

                                                            isLowpowerLayout = false;
                                                            ll_lowpowerLayout.setVisibility(View.GONE);
                                                            ll_slowpowerLayout.setVisibility(View.GONE);

                                                            is_bike = true;
                                                            is_ebike = false;
                                                        }else if(carmodels[0]==2){
                                                            cb_1.setVisibility(View.VISIBLE);
                                                            cb_2.setVisibility(View.VISIBLE);
                                                            cb_3.setVisibility(View.VISIBLE);
                                                            cb_4.setVisibility(View.VISIBLE);
                                                            cb_1.setChecked(false);
                                                            cb_2.setChecked(false);
                                                            cb_3.setChecked(false);
                                                            cb_4.setChecked(false);
                                                            cb_1.setText("只看超区");
                                                            cb_2.setText("只看坏车");
                                                            cb_3.setText("只看低电");
                                                            cb_4.setText("只看超低电");

                                                            isLowpowerLayout = true;
                                                            ll_lowpowerLayout.setVisibility(View.VISIBLE);
                                                            ll_slowpowerLayout.setVisibility(View.VISIBLE);

                                                            carbatteryaction_lowpower();

                                                            is_bike = false;
                                                            is_ebike = true;
                                                        }
                                                    }else if(carmodels.length==2){
                                                        cb_1.setVisibility(View.VISIBLE);
                                                        cb_2.setVisibility(View.VISIBLE);
                                                        cb_3.setVisibility(View.VISIBLE);
                                                        cb_4.setVisibility(View.VISIBLE);
                                                        cb_5.setVisibility(View.VISIBLE);
                                                        cb_6.setVisibility(View.VISIBLE);
                                                        cb_1.setChecked(false);
                                                        cb_2.setChecked(true);
                                                        cb_3.setChecked(true);
                                                        cb_4.setChecked(false);
                                                        cb_5.setChecked(false);
                                                        cb_6.setChecked(false);
                                                        cb_1.setText("只看超区");
                                                        cb_2.setText("只看单车");
                                                        cb_3.setText("只看电单车");
                                                        cb_4.setText("只看坏车");
                                                        cb_5.setText("只看低电");
                                                        cb_6.setText("只看超低电");

                                                        isLowpowerLayout = true;
                                                        ll_lowpowerLayout.setVisibility(View.VISIBLE);
                                                        ll_slowpowerLayout.setVisibility(View.VISIBLE);

                                                        carbatteryaction_lowpower();

                                                        is_bike = true;
                                                        is_ebike = true;
                                                    }

                                                }
                                            }
                                        }
                                    }

                                    cars(false);
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

    private void carbatteryaction_lowpower(){
        if(!isLowpowerLayout) return;

        LogUtil.e("sf===lowpower", school_id+"===");

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token != null && !"".equals(access_token)){
            RequestParams params = new RequestParams();
            params.put("school_id", school_id);
            HttpHelper.get(context, Urls.carbatteryaction_lowpower, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
//                    onStartCommon("正在加载");
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

                                LogUtil.e("sf===lowpower1", "==="+responseString);

                                LowPowerBean lowPowerBean = JSON.parseObject(result.getData(), LowPowerBean.class);

                                tv_lowpower.setText(""+lowPowerBean.getLow_total());
                                tv_slowpower.setText(""+lowPowerBean.getUltra_low_total());

                            } catch (Exception e) {
                                e.printStackTrace();

                                LogUtil.e("lowpower===e", "==="+e);
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


    Handler m_myHandlerState = new Handler();
    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    int n = myAdapter.getCount();

                    LogUtil.e("autoUpdate===", index+"==="+n);


//                    if((index%n)>0 && (index%n)<n){
////                        lv_msg.smoothScrollToPositionFromTop(index, 0);
//                        lv_msg.smoothScrollToPositionFromTop(index, 0, 2000);
//                    }else{
//                        lv_msg.smoothScrollToPosition(index);
//                    }

//                    lv_msg.smoothScrollToPositionFromTop(index, 0, 100);
//                    lv_msg.smoothScrollToPositionFromTop(index, 0);

                    index ++;

                    if(index >= n) {
                        index = 0;
                    }

//                    lv_msg.smoothScrollToPositionFromTop(index, 0, 1000);
//                    lv_msg.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            lv_msg.setSelection(index);
////                            loop();
//                        }
//                    }, 1000);

//                    lv_msg.post(new Runnable() {
//                        @Override
//                        public void run() {
////                            lv_msg.smoothScrollToPosition(index);
//                            lv_msg.smoothScrollToPositionFromTop(index, 0);
//                        }
//                    });

//                    lv_msg.postDelayed(new Runnable() {
//                           @Override
//                           public void run() {
////                               lv_msg.setSelection(index);
//                               lv_msg.smoothScrollToPosition(index);
//                           }
//                       }
//                    ,1000);

//                    lv_msg.smoothScrollToPosition(index);
//                    lv_msg.setSelection(index);
                    break;

                case 1:
                    if (popupwindow == null || (popupwindow != null && !popupwindow.isShowing())) {
//                        initNearby(latitude, longitude);
                        cars(false);
                    }

                    break;

                case 2:

                    break;

                case 0x98://搜索超时

                    LogUtil.e("0x98===", isLookPsdBtn+"==="+isAgain+"==="+isOpenLock+"==="+isEndBtn+"==="+lock_no);

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
//                        SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
//                                .searchBluetoothLeDevice(0)
//                                .build();
//
//
//                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            LogUtil.e("usecar===1", "===");
//
//                            break;
//                        }
//
//
//                        ClientManager.getClient().search(request, mSearchResponse);

                        connectDeviceLP();
                        ClientManager.getClient().registerConnectStatusListener(m_nowMac, mConnectStatusListener);
                        ClientManager.getClient().notifyClose(m_nowMac, mCloseListener);

//                        ClientManager.getClient().registerConnectStatusListener(lock_no, mConnectStatusListener);
//                        ClientManager.getClient().notifyClose(lock_no, mCloseListener);
                    }else{
//                        getStateLP();

                        if(isOpenLock){
                            getStateLP();
                        }else{
                            queryOpenState();
                        }
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

    public Polygon polygon = null;
    private void operationarea(){
        LogUtil.e("sf===operationarea0", isHidden()+"===");

//        if(isHidden()) return;



        RequestParams params = new RequestParams();
        params.put("school_id", school_id);

        HttpHelper.get(context, Urls.operationarea, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                if(!isHidden()){
//                    onStartCommon("正在加载");
//                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("sf===operationarea_f", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                LogUtil.e("sf===operationarea", "==="+responseString);

                final ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (1==1 || result.getFlag().equals("Success")) {
                                JSONArray jsonArray = new JSONArray(result.getData());

                                LogUtil.e("sf===operationarea1", isHidden()+"==="+jsonArray);

//                                if(isHidden()) return;

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    List<LatLng> list = new ArrayList<>();
                                    int flag=0;

                                    JSONArray jsonArray2 = new JSONArray(jsonArray.getJSONObject(i).getString("ranges"));

                                    for (int j = 0; j < jsonArray2.length(); j ++){

                                        JSONObject jsonObject = jsonArray2.getJSONObject(j);

                                        LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));

                                        LogUtil.e("sf===operationarea2", "==="+latLng);

                                        flag=0;
                                        list.add(latLng);

                                    }

                                    LogUtil.e("sf===operationarea3", "==="+list.size());


                                    PolygonOptions pOption = new PolygonOptions();

                                    pOption.addAll(list);

//                                    if(isHidden()) return;

                                    if(polygon!=null){
                                        polygon.remove();
                                    }

                                    polygon = aMap.addPolygon(pOption.strokeWidth(3)
//                                            .strokeDashStyle()
                                            .strokeColor(Color.argb(255, 0, 135, 255))
//                                            .fillColor(Color.argb(0, 0, 0, 0)));
                                            .fillColor(Color.argb(76, 0, 173, 255)));

                                    LogUtil.e("sf===operationarea4", "==="+polygon);


//                                    getMaxPoint(list);
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
        });
    }

    int carType = 0;
    public void cars(final boolean isFresh){

        RequestParams params = new RequestParams();


//        if(switcher_bike.isChecked()){
//            carType = 1;
//        }
//
//        if(switcher_ebike.isChecked()){
//            if(switcher_bike.isChecked()){
//                carType = 0;
//            }else{
//                carType = 2;
//            }
//        }
//
//
//        switchType = 0;
//        if(switcher_bad.isChecked()){
//            switchType = 1;
//        }
//
//        if(switcher.isChecked()){
//            if(switcher_bad.isChecked()){
//                switchType = 3;
//            }else{
//                switchType = 2;
//            }
//        }
//
//        is_area = 0;
//        if(switcher_over_area.isChecked()){
//            is_area = 1;
//        }


        carType = 3;
        if(is_bike){
            carType = 1;
        }

        if(is_ebike){
            if(is_bike){
                carType = 0;
            }else{
                carType = 2;
            }
        }

//                1、坏车 2、低电 3、超低电 4、坏车+低电 5、坏车+超低电 6、低电+超低电 7、坏车+低电+超低电
        switchType = 0;
        if(is_bad && !is_lowpower && !is_slowpower){
            switchType = 1;
        }else if(!is_bad && is_lowpower && !is_slowpower){
            switchType = 2;
        }else if(!is_bad && !is_lowpower && is_slowpower){
            switchType = 3;
        }else if(is_bad && is_lowpower && !is_slowpower){
            switchType = 4;
        }else if(is_bad && !is_lowpower && is_slowpower){
            switchType = 5;
        }else if(!is_bad && is_lowpower && is_slowpower){
            switchType = 6;
        }else if(is_bad && is_lowpower && is_slowpower){
            switchType = 7;
        }



        params.put("carmodel_type", carType);  //0单车+助力车 1只看单车 2只看助力车 3空类型 (必传)
        params.put("car_type", switchType);  //0全部 1只看坏车 2只看低电 3只看坏车+只看低电
        params.put("is_area", is_area);  //0默认 1查看超区
        params.put("school_id", school_id);

        LogUtil.e("cars===", school_id+"==="+carType+"==="+switchType+"==="+is_area+"==="+carmodel_id);

//        Looper.prepare();
        HttpHelper.get(context, Urls.cars, params, new TextHttpResponseHandler() {     //TODO
            @Override
            public void onStart() {
                if(isFresh){
                    onStartCommon("正在加载");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());

                LogUtil.e("cars===fail", "==="+throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("cars===0", "==="+responseString);

                        final ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final JSONArray array = new JSONArray(result.getData());

                                    LogUtil.e("cars===1", array.length()+"==="+bikeMarkerList+"==="+responseString);

                                    for (Marker marker : bikeMarkerList){
                                        if (marker != null){
                                            marker.remove();
                                        }
                                    }

                                    if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
                                        bikeMarkerList.clear();
                                    }

                                    if (0 == array.length()){   //1、坏车 2、低电 3、超低电 4、坏车+低电 5、坏车+超低电 6、低电+超低电 7、坏车+低电+超低电
                                        ToastUtils.showMessage("暂无数据");

                                        //TODO
//                                        if(switchType==0){
//                                            ToastUtils.showMessage("附近没有车辆");
//                                        }else if(switchType==1){
//                                            ToastUtils.showMessage("附近没有损坏车辆");
//                                        }else if(switchType==2){
//                                            ToastUtils.showMessage("附近没有低电车辆");
//                                        }else if(switchType==3){
//                                            ToastUtils.showMessage("附近没有低电和损坏车辆");
//                                        }

                                    } else {
                                        for (int i = 0; i < array.length(); i++){

                                            CarsBean bean = JSON.parseObject(array.getJSONObject(i).toString(), CarsBean.class);

//                                          LogUtil.e("cars===2", bean.getNumber()+"==="+array.getJSONObject(i).toString());

                                            // 加入自定义标签
                                            MarkerOptions bikeMarkerOption = null;

                                            int lock_id = bean.getLock_id();

//                                            LogUtil.e("cars===2", bikeMarkerList.size()+"==="+lock_id+"==="+bean.getNumber()+"==="+bean.getLevel()+"==="+bean.getLatitude()+"==="+bean.getLongitude());   //31.751411657279===119.94790856569   31.762293594349===119.92528159784

//                                            level：图标级别 3红色 2黄色 1绿色 4换电中 5维护中
                                            if(!"".equals(bean.getLatitude()) && !"".equals(bean.getLongitude())){
                                                if(lock_id==4){

                                                    bikeMarkerOption = new MarkerOptions().title(bean.getNumber()).position(new LatLng(
                                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
                                                            .icon(bean.getLevel()==1?bikeDescripter_green:bean.getLevel()==2?bikeDescripter_yellow:bean.getLevel()==3?bikeDescripter_red:bean.getLevel()==4?bikeDescripter_blue:bikeDescripter_brown);

                                                }else if(lock_id==7){
                                                    bikeMarkerOption = new MarkerOptions().title(bean.getNumber()).position(new LatLng(
                                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
                                                            .icon(bean.getLevel()==1?bikeDescripter_xa_green:bean.getLevel()==2?bikeDescripter_xa_yellow:bean.getLevel()==3?bikeDescripter_xa_red:bean.getLevel()==4?bikeDescripter_xa_blue:bikeDescripter_xa_brown);

                                                }else if(lock_id==8){
                                                    bikeMarkerOption = new MarkerOptions().title(bean.getNumber()).position(new LatLng(
                                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
                                                            .icon(bean.getLevel()==1?bikeDescripter_tbtd_green:bean.getLevel()==2?bikeDescripter_tbtd_yellow:bean.getLevel()==3?bikeDescripter_tbtd_red:bean.getLevel()==4?bikeDescripter_tbtd_blue:bikeDescripter_tbtd_brown);

                                                }else if(lock_id==11){
                                                    bikeMarkerOption = new MarkerOptions().title(bean.getNumber()).position(new LatLng(
                                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
                                                            .icon(bean.getLevel()==1?bikeDescripter_tbtf_green:bean.getLevel()==2?bikeDescripter_tbtf_yellow:bean.getLevel()==3?bikeDescripter_tbtf_red:bean.getLevel()==4?bikeDescripter_tbtf_blue:bikeDescripter_tbtf_brown);

                                                }else{
                                                    bikeMarkerOption = new MarkerOptions().title(bean.getNumber()).position(new LatLng(
                                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
                                                            .icon(bean.getLevel()==0?bikeDescripter:bikeDescripter_bad);

                                                }

                                                Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
                                                bikeMarkerList.add(bikeMarker);
                                            }

                                        }
                                    }
                                } catch (Exception e) {
                                    LogUtil.e("cars===e", "==="+e);
                                }

                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            }
                        }).start();



                    }
                });


            }
        });
//        Looper.loop();

    }





    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
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

        if("5".equals(type)  || "6".equals(type)){
            ClientManager.getClient().stopSearch();

            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);

            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
        }else if("4".equals(type) || "8".equals(type)){
        }else if("7".equals(type)){
            if (apiClient != null) {
                apiClient.onDestroy();
                apiClient = null;
            }
        }else if("11".equals(type)){
            if(TbitBle.hasInitialized()){
                TbitBle.destroy();
            }

        }else if("2".equals(type) || "3".equals(type) || "9".equals(type) || "10".equals(type) || "12".equals(type)){
            BleManager.getInstance().disconnectAllDevice();
            BleManager.getInstance().destroy();
        }

        super.onDestroy();
        if(mapView!=null){
            mapView.onDestroy();
        }

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
        LogUtil.e("addChooseMarker===", mapView+"==="+myLocation);

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
                        LogUtil.e("animateCamera===", "===");
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

        LogUtil.e("onCameraChangeF===", isUp+"==="+cameraPosition.target.latitude);
        if (isUp){

//            initNearby(cameraPosition.target.latitude, cameraPosition.target.longitude);
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
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){

            case R.id.mainUI_msg:
                if (access_token == null || "".equals(access_token)){
                    UIHelper.goToAct(activity, LoginActivity.class);
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                }else {
                    ((MainActivity)getActivity()).changeTab(1);

//                    MainActivity.changeTab(4);
                }

                break;

            case R.id.mainUI_leftBtn:
                if (access_token == null || "".equals(access_token)){
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
                    SharedPreferencesUrls.getInstance().putString("access_token","");
                    Toast.makeText(context,"登出登录成功",Toast.LENGTH_SHORT).show();
                    rightBtn.setText("登录");
                }
                break;

            case R.id.switcher:
//                SharedPreferencesUrls.getInstance().putBoolean("switcher", switcher.isChecked());

                if(switcher.isChecked()){
                    LogUtil.e("biking===switcher1", "onClick==="+switcher.isChecked());
                }else{
                    LogUtil.e("biking===switcher2", "onClick==="+switcher.isChecked());
                }

//                initNearby(latitude, longitude);
                cars(true);

                break;

            case R.id.switcher_bad:
//                SharedPreferencesUrls.getInstance().putBoolean("switcher", switcher.isChecked());

                if(switcher_bad.isChecked()){
                    LogUtil.e("biking===switcher_bad1", "onClick==="+switcher_bad.isChecked());
                }else{
                    LogUtil.e("biking===switcher_bad2", "onClick==="+switcher_bad.isChecked());
                }

//                initNearby(latitude, longitude);
                cars(true);

                break;

            case R.id.switcher_over_area:
//                SharedPreferencesUrls.getInstance().putBoolean("switcher", switcher.isChecked());

                if(switcher_over_area.isChecked()){
                    LogUtil.e("biking===switcher_over_area1", "onClick==="+switcher_over_area.isChecked());
                }else{
                    LogUtil.e("biking===switcher_over_area2", "onClick==="+switcher_over_area.isChecked());
                }

//                initNearby(latitude, longitude);
                cars(true);

                break;

            case R.id.switcher_bike:
//                SharedPreferencesUrls.getInstance().putBoolean("switcher", switcher.isChecked());

                if(switcher_bike.isChecked()){
                    LogUtil.e("biking===switcher_bike1", "onClick==="+switcher_bike.isChecked());
                }else{
                    LogUtil.e("biking===switcher_bike2", "onClick==="+switcher_bike.isChecked());
                }

//                initNearby(latitude, longitude);
                cars(true);
                break;

            case R.id.switcher_ebike:
//                SharedPreferencesUrls.getInstance().putBoolean("switcher", switcher.isChecked());

                if(switcher_ebike.isChecked()){
                    LogUtil.e("biking==switcher_ebike1", "onClick==="+switcher_ebike.isChecked());
                }else{
                    LogUtil.e("biking==switcher_ebike2", "onClick==="+switcher_ebike.isChecked());
                }

//                initNearby(latitude, longitude);
                cars(true);
                break;

            case R.id.tv_condition_cancel:
                LogUtil.e("tv_condition_cancel===", "==="+popupwindowCond);

                popupwindowCond.dismiss();
                break;

            case R.id.tv_condition_confirm:
                LogUtil.e("tv_condition_confirm===", "==="+popupwindowCond);

                carbatteryaction_lowpower();
                cars(true);


                popupwindowCond.dismiss();
                break;

            case R.id.cb_1:
//                if(switcher_ebike.isChecked()){
//                    LogUtil.e("biking==switcher_ebike1", "onClick==="+switcher_ebike.isChecked());
//                }else{
//                    LogUtil.e("biking==switcher_ebike2", "onClick==="+switcher_ebike.isChecked());
//                }
                LogUtil.e("biking==cb_1", cb_1.getText()+"==="+cb_1.isChecked());

                checkCondition(cb_1);

//                cars(true);
                break;

            case R.id.cb_2:
//                if(switcher_ebike.isChecked()){
//                    LogUtil.e("biking==switcher_ebike1", "onClick==="+switcher_ebike.isChecked());
//                }else{
//                    LogUtil.e("biking==switcher_ebike2", "onClick==="+switcher_ebike.isChecked());
//                }
                LogUtil.e("biking==cb_2", cb_2.getText()+"==="+cb_2.isChecked());

                checkCondition(cb_2);

//                cars(true);
                break;

            case R.id.cb_3:
//                if(switcher_ebike.isChecked()){
//                    LogUtil.e("biking==switcher_ebike1", "onClick==="+switcher_ebike.isChecked());
//                }else{
//                    LogUtil.e("biking==switcher_ebike2", "onClick==="+switcher_ebike.isChecked());
//                }
                LogUtil.e("biking==cb_3", cb_3.getText()+"==="+cb_3.isChecked());

                checkCondition(cb_3);

//                cars(true);
                break;

            case R.id.cb_4:
//                if(switcher_ebike.isChecked()){
//                    LogUtil.e("biking==switcher_ebike1", "onClick==="+switcher_ebike.isChecked());
//                }else{
//                    LogUtil.e("biking==switcher_ebike2", "onClick==="+switcher_ebike.isChecked());
//                }
                LogUtil.e("biking==cb_4", cb_4.getText()+"==="+cb_4.isChecked());

                checkCondition(cb_4);

//                cars(true);
                break;

            case R.id.cb_5:
//                if(switcher_ebike.isChecked()){
//                    LogUtil.e("biking==switcher_ebike1", "onClick==="+switcher_ebike.isChecked());
//                }else{
//                    LogUtil.e("biking==switcher_ebike2", "onClick==="+switcher_ebike.isChecked());
//                }
                LogUtil.e("biking==cb_3", cb_5.getText()+"==="+cb_5.isChecked());

                checkCondition(cb_5);

//                cars(true);
                break;

            case R.id.cb_6:
//                if(switcher_ebike.isChecked()){
//                    LogUtil.e("biking==switcher_ebike1", "onClick==="+switcher_ebike.isChecked());
//                }else{
//                    LogUtil.e("biking==switcher_ebike2", "onClick==="+switcher_ebike.isChecked());
//                }
                LogUtil.e("biking==cb_3", cb_6.getText()+"==="+cb_6.isChecked());

                checkCondition(cb_6);

//                cars(true);
                break;

            case R.id.mainUI_myLocationLayout:
                if (myLocation != null) {

//                    CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
//                    aMap.animateCamera(update);

                    leveltemp = 18f;
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, leveltemp));
                }
//
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, aMap.getCameraPosition().zoom));

//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18f));

//                aMap.moveCamera(CameraUpdateFactory.zoomTo(18f));


//                initmPopupRentWindowView();
//                initmPopupRentWindowView2();
//                initmPopupWindowView();

                break;

            case R.id.mainUI_getDotLayout:
//                UIHelper.goToAct(context, DotSelectActivity.class);
                UIHelper.goToAct(context, GetDotActivity.class);
                break;

            case R.id.mainUI_testXALayout:
                UIHelper.goToAct(context, TestXiaoanActivity.class);
                break;

            case R.id.mainUI_bindSchoolLayout:
                UIHelper.goToAct(context, BindSchoolActivity.class);
                break;

            case R.id.mainUI_inStorageLayout:
                end2();
                UIHelper.goToAct(context, DeviceSelectActivity.class);
                break;

            case R.id.mainUI_refreshLayout:
                initHttp(true);
                carbatteryaction_lowpower();
//                cars(true);
                break;

            case R.id.mainUI_scanCode_lock:
                if (access_token == null || "".equals(access_token)){
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

                        end2();

                        Intent intent = new Intent();
                        intent.setClass(context, ActivityScanerCode.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("isChangeKey",false);
                        startActivityForResult(intent, 101);
                    } catch (Exception e) {
                        LogUtil.e("scanCode_lock===e", "==="+e);
                        UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
                    }
                }
                break;

            case R.id.ll_open_lock:
                isOpenLock = true;

                if(carmodel_id==1){
                    open_lock();
                }else{

                    if ("7".equals(type)) {

                        LogUtil.e("open===7_1", deviceuuid + "==="+m_nowMac);

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
                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("正在唤醒车锁");
                                loadingDialog.show();
                            }

                            if( apiClient==null){
                                XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                                builder.setBleStateChangeListener(ScanFragment.this);
                                builder.setScanResultCallback(ScanFragment.this);
                                apiClient = builder.build();
                            }

                            if(!isConnect){
                                ScanFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(ScanFragment.this, deviceuuid);

                                isConnect = false;
                                m_myHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isConnect){
//                                          closeLoadingDialog();

                                            LogUtil.e("mf===7==timeout", isConnect + "==="+activity.isFinishing());

                                            if (apiClient != null) {
                                                apiClient.disConnect();
                                                apiClient.onDestroy();
                                                apiClient=null;
                                            }

                                            unlock();
                                        }
                                    }
                                }, timeout);
                            }else{
                                xiaoanOpen_blue();
                            }
                        }
                    }else if ("11".equals(type)) { //TODO
                        LogUtil.e("open===11_1", deviceuuid + "==="+ bleid + "==="+ m_nowMac);

                        unlock();


                    }else{
                        unlock();
                    }

                }


                break;

            case R.id.ll_close_lock:
                isOpenLock = false;

                if("7".equals(type)){
                    LogUtil.e("close===7_1", deviceuuid + "==="+ bleid + "==="+ m_nowMac);

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
                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                            loadingDialog.setTitle("正在唤醒车锁");
                            loadingDialog.show();
                        }

                        LogUtil.e("close===onClick_7", "上锁===" + isConnect + "===" + deviceuuid + "===" + apiClient);

                        if(apiClient==null){
                            XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                            builder.setBleStateChangeListener(this);
                            builder.setScanResultCallback(this);
                            apiClient = builder.build();
                        }

                        if(!isConnect){
                            ScanFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(this, deviceuuid);

                            isConnect = false;
                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isConnect){

                                        LogUtil.e("close===7==timeout", "上锁==="+isConnect + "==="+activity.isFinishing());

                                        if (apiClient != null) {
                                            apiClient.disConnect();
                                            apiClient.onDestroy();
                                            apiClient=null;
                                        }

                                        lock();
                                    }
                                }
                            }, timeout);
                        }else{
                            xiaoanClose_blue();
                        }
                    }
                }else if ("11".equals(type)) { //TODO
                    LogUtil.e("close===11_1", deviceuuid + "==="+ bleid + "==="+ m_nowMac);

                    lock();


                }else{
                    lock();
                }

                break;

            case R.id.ll_end_order:
                order_finish();
                break;

            case R.id.ll_get_state:
                get_state();
                break;

            case R.id.popupWindow_back:
                popupwindow.dismiss();
                break;

            case R.id.ll_set_recovered:
                carbadaction(1);
                break;
            case R.id.ll_set_ok:
//                carbadaction(2);
                dialogReason.show();
                break;

            case R.id.ll_query:
//                Intent intent = new Intent(context, MerchantAddressMapActivity.class);
                Intent intent = new Intent(context, QueryActivity.class);
                intent.putExtra("carmodel_id", carmodel_id);
                intent.putExtra("codenum", codenum);
                startActivity(intent);

//                UIHelper.goToAct(context, MerchantAddressMapActivity.class);
                break;

            case R.id.tv_set_useless:
                dialogReason.show();
//                carbadaction(3);
                break;

            case R.id.lock_switcher:

                if(lock_switcher.isChecked()){
                    LogUtil.e("pop===switcher1", "onClick==="+lock_switcher.isChecked());
                    unlock();
                }else{
                    LogUtil.e("pop===switcher2", "onClick==="+lock_switcher.isChecked());
                    lock();
                }
                break;

            case R.id.ll_car_search:
                ddSearch();
                break;

            case R.id.ll_power_exchange:

                LogUtil.e("power_exchange===", deviceuuid + "==="+ bleid + "==="+ m_nowMac);

//                        unlock();

                battery_unlock();
                break;

            case R.id.mainUI_conditionLayout:

                LogUtil.e("_lowPowerLayout===", "==="+popupwindowCond);

                if(popupwindowCond==null || !popupwindowCond.isShowing()){
                    initmPopupWindowViewCond();
                }

                break;

            case R.id.popupWindowCond_back:
                popupwindowCond.dismiss();
                break;

            default:
                break;
        }
    }

    boolean is_bike;
    boolean is_ebike;
    boolean is_bad;
    boolean is_lowpower;
    boolean is_slowpower;
    void checkCondition(CheckBox cb){
//        int carType = 3;
//        if(switcher_bike.isChecked()){
//            carType = 1;
//        }
//
//        if(switcher_ebike.isChecked()){
//            if(switcher_bike.isChecked()){
//                carType = 0;
//            }else{
//                carType = 2;
//            }
//        }
//
//
//        switchType = 0;
//        if(switcher_bad.isChecked()){
//            switchType = 1;
//        }
//
//        if(switcher.isChecked()){
//            if(switcher_bad.isChecked()){
//                switchType = 3;
//            }else{
//                switchType = 2;
//            }
//        }
//
//        is_area = 0;
//        if(switcher_over_area.isChecked()){
//            is_area = 1;
//        }
//
//        params.put("carmodel_type", carType);  //0单车+助力车 1只看单车 2只看助力车 3空类型 (必传)
//        params.put("car_type", switchType);  //1、坏车 2、低电 3、超低电 4、坏车+低电 5、坏车+超低电 6、低电+超低电 7、坏车+低电+超低电
//        params.put("is_area", is_area);  //0默认 1查看超区

        if("只看超区".equals(cb.getText())){
            if(cb.isChecked()){
                is_area = 1;
            }else{
                is_area = 0;
            }
        }else if("只看坏车".equals(cb.getText())){
            if(cb.isChecked()){
                is_bad = true;
            }else{
                is_bad = false;
            }

        }else if("只看单车".equals(cb.getText())){
            if(cb.isChecked()){
                is_bike = true;
            }else{
                is_bike = false;
            }

//            carType = 3;
//            if(is_bike){
//                carType = 1;
//            }
//
//            if(is_ebike){
//                if(is_bike){
//                    carType = 0;
//                }else{
//                    carType = 2;
//                }
//            }

        }else if("只看电单车".equals(cb.getText())){
            if(cb.isChecked()){
                is_ebike = true;
            }else{
                is_ebike = false;
            }

//            carType = 3;
//            if(is_bike){
//                carType = 1;
//            }
//
//            if(is_ebike){
//                if(is_bike){
//                    carType = 0;
//                }else{
//                    carType = 2;
//                }
//            }

        }else if("只看低电".equals(cb.getText())){
            if(cb.isChecked()){
                is_lowpower = true;
            }else{
                is_lowpower = false;
            }

        }else if("只看超低电".equals(cb.getText())){
            if(cb.isChecked()){
                is_slowpower = true;
            }else{
                is_slowpower = false;
            }
        }
    }

    public void initmPopupWindowViewCond(){

        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_cond_back);

        popupwindowCond.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//        isLowPowerLayout = false;


        LogUtil.e("initmPopup===", "===");

    }

    //泰比特连接
    private void tbtble_connect() {
        LogUtil.e("tbtble_connect===", deviceuuid+"==="+bleid);

        TbitBle.connect(deviceuuid, bleid, new ResultCallback() {
            @Override
            public void onResult(final int resultCode) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try{
//                            if(isNetSuc){
//                                return;
//                            }

                            // 连接回应
//                            final String tvAgain = tv_againBtn.getText().toString().trim();
                            LogUtil.e("tbtble_connect===1", resultCode+"==="+isOpenLock+"==="+isEndBtn+"==="+isAgain);
//                            m_myHandler2.removeCallbacksAndMessages(null);

                            if(resultCode==0){
//                                if(isAgain){
//                                    if("再次开锁".equals(tvAgain)){
//                                        tbtble_unlock_temp();
//                                    }else{
//                                        tbtble_lock_temp();
//                                    }
//                                }else{
//                                    if(isEndBtn){
//                                        tbtble_lock();
//                                    }else{
//                                        tbtble_unlock();
//                                    }
//                                }

                                isConnect = true;

                                if(isOpenLock){
                                    tbtble_unlock();
                                }else{
                                    tbtble_lock();
                                }



                            }else{

//                                if(isAgain){
//                                    if("再次开锁".equals(tvAgain)){
//                                        car_notification(4, 2, 0, type+"===tbtble_connect_f==="+resultCode);
//                                    }else{
//                                        car_notification(2, 2, 0, type+"===tbtble_connect_f===\""+resultCode);
//                                    }
//
//
//                                }else{
//                                    if(isEndBtn){
//                                        car_notification(3, 2, 0, type+"===tbtble_connect_f===\""+resultCode);
//                                    }else{
//                                        car_notification(1, 2, 0, type+"===tbtble_connect_f===\""+resultCode);
//                                    }
//                                }

//                                closeLoadingDialog();

                                isConnect = false;
                                LogUtil.e("tbtble_connect_f", "==="+resultCode);
                                ToastUtil.showMessageApp(context, "蓝牙连接失败");


//                                if(isOpenLock){
//                                    unlock();
//                                }else{
//                                    lock();
//                                }
                            }
                        }catch(Exception e){
                            closeLoadingDialog();
                            LogUtil.e("tbtble_connect===e", "==="+e);
                        }

                    }
                });



            }
        }, new StateCallback() {
            @Override
            public void onStateUpdated(BikeState bikeState) {

                // 连接成功状态更新
                // 通过车辆状态获取设备特定信息
                W206State w206State = (W206State) TbitBle.getConfig().getResolver().resolveCustomState(bikeState);

                LogUtil.e("tbtble_connect=onStateU", bikeState+"==="+bikeState.getBattery());
            }
        });
    }

    //泰比特连接
    private void tbtble_connect_battery() {
        LogUtil.e("tbtble_connect===", deviceuuid+"==="+bleid);

        TbitBle.connect(deviceuuid, bleid, new ResultCallback() {
            @Override
            public void onResult(final int resultCode) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            LogUtil.e("tbtble_connect_battery===1", resultCode+"==="+isOpenLock+"==="+isEndBtn+"==="+isAgain);

                            if(resultCode==0){
                                isConnect = true;

                                batteryBle_unlock();

                            }else{

                                isConnect = false;
                                LogUtil.e("tbtble_connect_battery_f", "==="+resultCode);

                                closeLoadingDialog();
                                ToastUtil.showMessageApp(context, "蓝牙连接失败");
//                                battery_unlock();
                            }
                        }catch(Exception e){
                            closeLoadingDialog();
                            LogUtil.e("tbtble_connect_battery===e", "==="+e);
                        }

                    }
                });



            }
        }, new StateCallback() {
            @Override
            public void onStateUpdated(BikeState bikeState) {

                // 连接成功状态更新
                // 通过车辆状态获取设备特定信息
                W206State w206State = (W206State) TbitBle.getConfig().getResolver().resolveCustomState(bikeState);

                tbt_battery = bikeState.getBattery();
                LogUtil.e("tbtble_connect_battery=onStateU", bikeState.getLocation()+"==="+tbt_battery);
            }
        });
    }

    // 泰比特209D蓝牙开锁
    private void tbtble_unlock() {
        TbitBle.unlock(new ResultCallback() {
            @Override
            public void onResult(final int resultCode) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            // 解锁回应
                            LogUtil.e("tbtble_unlock===", resultCode+"==="+isEndBtn);

                            if(resultCode==0){
//                              isOpened = true;
//
//                                if(isNetSuc){
//                                    return;
//                                }
//                                m_myHandler.sendEmptyMessage(7);

                                closeLoadingDialog();
                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                            }else{

//                                String tvAgain = tv_againBtn.getText().toString().trim();
//                                int action_type;
//
//                                if(isAgain){
//                                    if("再次开锁".equals(tvAgain)){
//                                        action_type=4;
//                                    }else{
//                                        action_type=2;
//                                    }
//                                }else{
//                                    if(isEndBtn){
//                                        action_type=3;
//                                    }else{
//                                        action_type=1;
//                                    }
//                                }


                                closeLoadingDialog();
                                LogUtil.e("tbtble_unlock_f", "==="+resultCode);
//                                car_notification(action_type, 3, 0, type+"===tbtble_unlock_f==="+resultCode);

//                                unlock();
                                ToastUtil.showMessageApp(context, "开锁失败");
                            }

                        }catch(Exception e){
                            closeLoadingDialog();
                            LogUtil.e("tbtble_unlock===e", "==="+e);
                        }

                    }
                });



            }
        });
    }

    // 泰比特209D蓝牙上锁
    private void tbtble_lock() {
        TbitBle.lock(new ResultCallback() {
            @Override
            public void onResult(final int resultCode) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            // 上锁回应
                            LogUtil.e("tbtble_lock===", resultCode+"==="+isEndBtn+"==="+isFinish);

                            if(resultCode==0){
//                                if(isEndBtn){
//                                    if(!isFinish){
//                                        isFinish = true;
//                                        car_notification(3, 1, 1, "");
//                                    }
//                                }

                                ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");
                                closeLoadingDialog();
                            }else{
//                                String tvAgain = tv_againBtn.getText().toString().trim();
//                                int action_type;
//
//                                if(isAgain){
//                                    if("再次开锁".equals(tvAgain)){
//                                        action_type=4;
//                                    }else{
//                                        action_type=2;
//                                    }
//                                }else{
//                                    if(isEndBtn){
//                                        action_type=3;
//                                    }else{
//                                        action_type=1;
//                                    }
//                                }

                                closeLoadingDialog();
                                LogUtil.e("tbtble_lock_f", "==="+resultCode);
//                                car_notification(action_type, 3, 0, type+"===tbtble_lock_f==="+resultCode);

//                                lock();
                                ToastUtil.showMessageApp(context, "关锁失败");

//                                if(isEndBtn){
//                                    lock();
//                                }
                            }
                        }catch(Exception e){
                            closeLoadingDialog();
                            LogUtil.e("tbtble_lock===e", "==="+e);
                        }

                    }
                });
            }
        });
    }

    // 泰比特209D蓝牙电池锁开锁
    private void batteryBle_unlock(){
        TbitBle.commonCommand((byte)0x03, (byte)0x05, new Byte[]{0x01},
                new ResultCallback() {
                    @Override
                    public void onResult(final int resultCode) {
                        // 发送状态回复

                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                try{
                                    LogUtil.e("batteryBle_unlock", isAgain+"==="+resultCode);

                                    if(resultCode==0){

                                        closeLoadingDialog();

                                        battery_report();

                                    }else{
                                        LogUtil.e("batteryBle_unlock_f", "==="+resultCode);
//                                        battery_unlock();

                                        closeLoadingDialog();
                                        ToastUtil.showMessageApp(context,"打开电池锁失败");
                                    }
                                }catch(Exception e){
                                    closeLoadingDialog();
                                    LogUtil.e("batteryBle_unlock_e", "==="+e);
                                }

                            }
                        });


                    }
                }, new PacketCallback() {
                    @Override
                    public void onPacketReceived(Packet packet) {
                        // 收到packet回复
                    }
                });
    }

    void carbadaction(int type) {   //

        LogUtil.e("carbadaction===", type+"==="+codenum);

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("type", type);  //类型 1已回收 2已修好 3报废
            if(type==2){
                params.put("setgood_reason", reason);
            }else if(type==3){
                params.put("scrapped_reason", reason);
            }

            HttpHelper.post(context, Urls.carbadaction_operation+codenum, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在提交");
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

                                LogUtil.e("carbadaction===1", "==="+responseString);

//                        if(result.getStatus_code()==200){
//                            curMarker.setIcon(bikeDescripter_blue);
//                        }else{
//
//                        }

                                Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();

                                if(popupwindow!=null){
                                    popupwindow.dismiss();
                                }

                                reasonEdit.setText("");

                                lockInfo2(); //TODO

////                        popupwindow.dismiss();
//                        if(carmodel_id==1){
//                            initmPopupRentWindowView();     //TODO
//                        }else{
//                            initmPopupRentWindowView2();
//                        }

                                recycletask();

//                        if (result.getFlag().equals("Success")) {
//                            curMarker.setIcon(bikeDescripter_blue);
//
//                            Toast.makeText(context,"已发送指令",Toast.LENGTH_SHORT).show();
//                        }else {
//                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                        }
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

    //泰比特连接
    private void tbtble_connect2() {
        TbitBle.connect(deviceuuid, bleid, new ResultCallback() {
            @Override
            public void onResult(int resultCode) {
                // 连接回应
//                final String tvAgain = tv_againBtn.getText().toString().trim();
                LogUtil.e("tbtble_connect2===", resultCode+"==="+isOpenLock+"==="+isEndBtn+"==="+isAgain);


            }
        }, new StateCallback() {
            @Override
            public void onStateUpdated(BikeState bikeState) {
                LogUtil.e("tbtble_connect2=onStateU", bikeState+"===");

                // 连接成功状态更新
                // 通过车辆状态获取设备特定信息
                W206State w206State = (W206State) TbitBle.getConfig().getResolver().resolveCustomState(bikeState);
            }
        });
    }

    private void get_state(){
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.setTitle("正在唤醒车锁");
            loadingDialog.show();
        }

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

            LogUtil.e("ll_get_state===2",  isLookPsdBtn + "===" + type + "===" + isMac + "===" + token);

            isOpenLock = false;

//                    if("10".equals(type)){
//                        BleManager.getInstance().init(activity.getApplication());
//                        BleManager.getInstance()
//                                .enableLog(true)
//                                .setReConnectCount(0, 5000)
////                            .setOperateTimeout(10000)
//                                .setConnectOverTime(timeout);
//
//                        connect();
//                    }else{
//
//                    }

            if ("2".equals(type) || "3".equals(type) || "9".equals(type) || "10".equals(type) || "12".equals(type)) {
                if(!isLookPsdBtn){   //没连上

                    initBle();

                    connect();
                }else{
                    if(token==null || "".equals(token)){
                        getBleToken();
                    }else{
                        getLockStatus();
                    }
                }
            }else if ("5".equals(type) || "6".equals(type)) {
                m_myHandler.sendEmptyMessage(0x98);
            }

        }

//                getLockStatus();
    }

    private void end2() {

        LogUtil.e("mf==end2", type+"==="+m_nowMac);

        if("5".equals(type)  || "6".equals(type)){
            ClientManager.getClient().stopSearch();

            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);
            ClientManager.getClient().disconnect(m_nowMac);


            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener);
//            ClientManager.getClient().unnotifyClose(m_nowMac, mCloseListener);
//            ClientManager.getClient().unregisterConnectStatusListener(m_nowMac, mConnectStatusListener2);

        }else if("4".equals(type) || "8".equals(type)){
        }else if("7".equals(type)){
            if (apiClient != null) {
                apiClient.disConnect();
                apiClient.onDestroy();
                apiClient = null;
            }
        }else if("11".equals(type)){
            if(TbitBle.hasInitialized()){
                TbitBle.disConnect();
            }
        }else if("2".equals(type) || "3".equals(type) || "9".equals(type) || "10".equals(type) || "12".equals(type)){
            BleManager.getInstance().disconnectAllDevice();
            BleManager.getInstance().destroy();
        }
    }

    private void open_lock(){
//        type = "2";
//        m_nowMac = "C8:FD:19:68:2F:90";     //40004690

//        type = "3";
//        m_nowMac = "DF:FF:96:62:68:BB";     //60009090

//        type = "6";
////                m_nowMac = "3C:A3:08:AF:02:C3";     //30005053
////                lock_no = "LPKMIrwLD";
//        m_nowMac = "A4:34:F1:7B:BF:9A";     //30005060
//        lock_no = "GpDTxe7<a";

//        initParams();

        LogUtil.e("open_lock===",  isLookPsdBtn + "===" + type + "===" + isMac + "===" + token);

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在唤醒车锁");
                    loadingDialog.show();
                }

                if ("1".equals(type)) {          //单车机械锁
//                  UIHelper.goToAct(context, CurRoadStartActivity.class);
//                  popupwindow.dismiss();
                } else if ("2".equals(type) || "3".equals(type) || "9".equals(type) || "10".equals(type) || "12".equals(type)) {    //单车蓝牙锁

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

                        LogUtil.e("order===2",  m_nowMac+ "===" + isLookPsdBtn + "===" + type + "===" + isMac + "===" + token);

                        isOpenLock = true;



                        if("10".equals(type) || "12".equals(type)){
                            initBle();

                            connect();
                        }else{
                            if(!isLookPsdBtn){   //没连上

//                            if(isMac){
//                                initBle();
//                                connect();
//                            }else{
//                                setScanRule();
//                                scan2();
//                            }

                                setScanRule();
                                scan2();

//                                initBle();
//                                connect();
                            }else{
//                              BaseApplication.getInstance().getIBLE().openLock();

                                if(token==null || "".equals(token)){
                                    getBleToken();
                                }else{
                                    openLock();
                                }
                            }
                        }

                    }
                }else if ("4".equals(type) || "8".equals(type)) {

                    unlock();

                }else if ("5".equals(type) || "6".equals(type)) {      //泺平单车蓝牙锁

                    LogUtil.e("mf===5_1", deviceuuid + "==="+m_nowMac);

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

                        isOpenLock = true;
                        m_myHandler.sendEmptyMessage(0x98);

                    }
                }else if ("7".equals(type)) {
                    LogUtil.e("mf===7_1", deviceuuid + "==="+m_nowMac);

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
                        if( apiClient==null){
                            XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(context);
                            builder.setBleStateChangeListener(ScanFragment.this);
                            builder.setScanResultCallback(ScanFragment.this);
                            apiClient = builder.build();
                        }


                        ScanFragmentPermissionsDispatcher.connectDeviceWithPermissionCheck(ScanFragment.this, deviceuuid);

                        isConnect = false;
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isConnect){
//                                    closeLoadingDialog();

                                    if (apiClient != null) {
                                        apiClient.disConnect();
                                        apiClient.onDestroy();
                                        apiClient=null;
                                    }

                                    LogUtil.e("mf===7==timeout", isConnect + "==="+activity.isFinishing());

                                    unlock();
                                }
                            }
                        }, timeout);
                    }
                }
//                else if ("12".equals(type)) {      //TODO
//                    LogUtil.e("sf===12_1", deviceuuid + "==="+ bleid + "==="+ m_nowMac);
//
////                  lockStatus = 2;
//                    isNetSuc = false;
////                  unlock();
//
//                    if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                        ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
//                    }
//                    //蓝牙锁
//                    BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//
//                    mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                    if (mBluetoothAdapter == null) {
//                        ToastUtil.showMessageApp(context, "获取蓝牙失败");
//                        return;
//                    }
//                    if (!mBluetoothAdapter.isEnabled()) {
//                        isPermission = false;
//                        closeLoadingDialog2();
//                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableBtIntent, 188);
//                    } else {
//                        isOpenLock = true;
//                        isNetSuc = false;
//
//                        connect();
//                    }
//
//                }
            }
        });


    }

    void initBle(){
        BleManager.getInstance().init(activity.getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(4, 2000)
//                .setOperateTimeout(10000)
                .setConnectOverTime(timeout);
    }

    void scan2(){
//      loadingDialog = DialogUtils.getLoadingDialog(context, "正在搜索...");
//		loadingDialog.setTitle("正在搜索");
//		loadingDialog.show();

//        BleManager.getInstance().cancelScan();

        isFind = false;
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                LogUtil.e("mf===onScanStarted2", "==="+success);
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);

                LogUtil.e("mf===onLeScan2", bleDevice+"==="+bleDevice.getMac());
            }

            @Override
            public void onScanning(final BleDevice bleDevice) {

                LogUtil.e("mf===onScanning2", m_nowMac+"==="+bleDevice.getMac());

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(m_nowMac.equals(bleDevice.getMac())){
                            //                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }

                            isFind = true;
                            BleManager.getInstance().cancelScan();

                            connect();

                            LogUtil.e("onScanning===2_1", isConnect+"==="+bleDevice+"==="+bleDevice.getMac());

                        }
                    }
                });

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

                LogUtil.e("mf===onScanFinished2", isFind+"==="+type);

                if(!isFind){
                    if("3".equals(type)){

                        LogUtil.e("mf===onScanFinished2", isAgain+"==="+isFind+"==="+type);

                        unlock();

                    }else{
                        Toast.makeText(context,"蓝牙连接失败，请靠近车锁，重启软件试试吧！",Toast.LENGTH_LONG).show();
//                        car_notification(isOpenLock?1:isAgain?2:isEndBtn?3:0, 2, 0);
                        if(popupwindow!=null){
                            popupwindow.dismiss();
                        }
                    }
                }


            }
        });
    }

    private void order_finish(){
        LogUtil.e("mf===order_finish", "==="+codenum);

//        RequestParams params = new RequestParams();
//        params.put("number", codenum);

        HttpHelper.post(context, Urls.order_finish+codenum, null, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogUtil.e("mf===order_finish_fail", responseString + "===" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("mf===order_finish_1", carmodel_id + "===" + responseString + "===" + result.data);

                            ToastUtil.showMessageApp(context,result.getMessage());


                        } catch (Exception e) {

//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                        closeLoadingDialog();

                    }
                });
            }
        });
    }

    int state = 0;
    private void connect(){
//        loadingDialog = DialogUtils.getLoadingDialog(this, "正在连接...");
//        loadingDialog.setTitle("正在连接");
//        loadingDialog.show();

        LogUtil.e("connect===", m_nowMac+"==="+carmodel_id+"==="+type+"==="+isLookPsdBtn);

//        BleManager.getInstance().cancelScan();

        BleManager.getInstance().connect(m_nowMac, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                LogUtil.e("onStartConnect===", "===");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                LogUtil.e("onConnectFail===", bleDevice.getMac()+"==="+exception);

                isLookPsdBtn = false;

                closeLoadingDialog2();

                if (!isLookPsdBtn){
//                    BaseApplication.getInstance().getIBLE().stopScan();
//                    BaseApplication.getInstance().getIBLE().refreshCache();
//                    BaseApplication.getInstance().getIBLE().close();
//                    BaseApplication.getInstance().getIBLE().disconnect();

                    LogUtil.e("0x99===timeout", isLookPsdBtn+"==="+isStop+"==="+type);

                    if("3".equals(type) || "12".equals(type)){
                        if(isOpenLock){
                            unlock();
                        }else{
                            Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！",Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！",Toast.LENGTH_LONG).show();
//                        car_notification(isOpenLock?1:isAgain?2:isEndBtn?3:0, 2, 0);      //TODO
//                        if(popupwindow!=null){
//                            popupwindow.dismiss();
//                        }

//                        if("10".equals(type)){
////                          BleManager.getInstance().disconnect();
//                            BleManager.getInstance().disconnectAllDevice();
////                          BleManager.getInstance().destroy();
//
//                            isOpenLock=false;
//                            connect();
//                        }
                    }
                }
            }

            @Override
            public void onConnectSuccess(final BleDevice device, BluetoothGatt gatt, int status) {
//                if (loadingDialog != null && loadingDialog.isShowing()) {
//                    loadingDialog.dismiss();
//                }

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        isLookPsdBtn = true;
                        bleDevice = device;

//                      BleManager.getInstance().cancelScan();

                        LogUtil.e("onConnectSuccess===", bleDevice.getMac()+"===");
//                      Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();

                        bleNotify();
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                getBleToken();
                            }
                        }, 500);

//                        m_myHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//
//
//                            }
//                        }, 2000);


                    }
                });



            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {

                isLookPsdBtn = false;
                LogUtil.e("connect=onDisConnected", type+"==="+isActiveDisConnected);

//                if("10".equals(type)){
////                    BleManager.getInstance().disconnect();
//                    BleManager.getInstance().disconnectAllDevice();
////                    BleManager.getInstance().destroy();
//
//                    isOpenLock=false;
//                    connect();
//                }

                ToastUtil.showMessageApp(context,"蓝牙连接已断开");
                closeLoadingDialog();

//                    if (isActiveDisConnected) {
//                        Toast.makeText(MainActivity.this, getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(MainActivity.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
//                        ObserverManager.getInstance().notifyObserver(bleDevice);
//                    }

            }
        });
    }

    private void bleNotify(){
        BleManager.getInstance().notify(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f6-0000-1000-8000-00805f9b34fb", new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {
                LogUtil.e("onNotifySuccess===", "===");
            }

            @Override
            public void onNotifyFailure(BleException exception) {
                LogUtil.e("onNotifyFailure===", "===");
            }

            @Override
            public void onCharacteristicChanged(final byte[] data) {
//                            byte[] values = characteristic.getValue();

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("onCharacteristicChanged", "===0");

                        byte[] x = new byte[16];
                        System.arraycopy(data, 0, x, 0, 16);

                        byte[] mingwen = EncryptUtils.Decrypt(x, Config.newKey);    //060207FE02433001010606D41FC9553C  FE024330 01 01 06

                        LogUtil.e("onCharacteristicChanged", x.length+"==="+ ConvertUtils.bytes2HexString(data)+"==="+ConvertUtils.bytes2HexString(mingwen));

                        String s1 = ConvertUtils.bytes2HexString(mingwen);

                        if(s1.startsWith("CB0501")){
                            LogUtil.e("CB0501===", loadingDialog.isShowing()+"==="+isOpenLock+"==="+s1);

                            if(isOpenLock){
//                                                openLock();

//                                                getLockStatus();

                                state = 0;
                                m_myHandlerState.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        if(state==0){
                                            getLockStatus();
                                        }else{
                                            closeLoadingDialog();
                                        }

                                    }
                                }, 3000);
                            }
                        }else if(s1.startsWith("0602")){      //获取token

                            token = s1.substring(6, 14);    //0602070C0580E001010406C8D6DC1949
                            GlobalParameterUtils.getInstance().setToken(ConvertUtils.hexString2Bytes(token));

//                                          String tvAgain = tv_againBtn.getText().toString().trim();

                            LogUtil.e("token===", loadingDialog.isShowing()+"==="+type+"==="+isOpenLock+"==="+token+"==="+s1);

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if("2".equals(type) || "3".equals(type)){
                                        getBattery();
                                    }else if("9".equals(type) || "10".equals(type) || "12".equals(type)){
                                        getBattery2();
                                    }

                                }
                            }, 500);


                            if(isOpenLock){
                                openLock();
                            }else{
                                getLockStatus();
                            }

                        }else if(s1.startsWith("0502")){    //开锁
                            LogUtil.e("d_openLock===", loadingDialog.isShowing()+"==="+isOpenLock+"==="+s1);

                            state = 1;
                            m_myHandlerState.removeCallbacksAndMessages(null);
                            if("9".equals(type) || "10".equals(type) || "12".equals(type)){

//                                                if(isForeground){
//                                                    Toast.makeText(context, "开锁成功", Toast.LENGTH_SHORT).show();
//                                                }

//                                                closeLoadingDialog();
                            }else{
                                getLockStatus();
                            }

//                                          end2();

//                                          m_myHandler.sendEmptyMessage(7);
                        }else if(s1.startsWith("0508")){   //关锁==050801RET：RET取值如下：0x00，锁关闭成功。0x01，锁关闭失败。0x02，锁关闭异常。
                            LogUtil.e("lockState===0508", loadingDialog.isShowing()+"==="+isOpenLock+"==="+s1+"==="+s1.substring(6, 8));   //

                            if ("00".equals(s1.substring(6, 8))) {
//                                                ToastUtil.showMessageApp(context,"关锁成功");
//                                                if(isForeground){
//                                                    Toast.makeText(context, "关锁成功", Toast.LENGTH_SHORT).show();
//                                                }

                                LogUtil.e("closeLock===suc", "===");

                            } else {
//                                                ToastUtil.showMessageApp(context,"关锁失败");
                                if(isForeground){
                                    Toast.makeText(context, "关锁失败", Toast.LENGTH_SHORT).show();
                                }

                                LogUtil.e("closeLock===fail", "===");
                            }

                            getLockStatus();
                        }else if(s1.startsWith("050F")){   //锁状态
                            LogUtil.e("lockState===050F", loadingDialog.isShowing()+"==="+isOpenLock+"==="+s1+"==="+s1.substring(6, 8));   //


                            isStop = true;
                            isLookPsdBtn = true;

//                                          查询锁开关状态==050F:0x00表示开启状态；0x01表示关闭状态。
                            if ("01".equals(s1.substring(6, 8))) {

                                if(isOpenLock){
//                                                    openLock();
//                                                    getBleToken();

                                    m_myHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

//                                                            getBleToken();
                                            openLock();

                                        }
                                    }, 1000);
                                }else{
                                    if(isForeground){
//                                                        Toast.makeText(context, "锁已关闭", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(context, "关锁成功", Toast.LENGTH_SHORT).show();
                                    }

                                    LogUtil.e("closeLock===1", "锁已关闭==="+first3);

                                    tv_lock_status.setText("已上锁");

                                    closeLoadingDialog();
                                }

                            } else {


                                if(isOpenLock){
                                    isOpenLock = false;
                                }

                                //锁已开启
//                                                ToastUtil.showMessageApp(context,"锁已打开");
                                if(isForeground){
                                    Toast.makeText(context, "开锁成功", Toast.LENGTH_SHORT).show();
                                }

                                tv_lock_status.setText("已开锁");

//                                              car_notification(3, 5, 0);    //TODO

                                isEndBtn = false;

                                closeLoadingDialog();
                            }

//                                          end2();
                        }else if(s1.startsWith("020201")){    //电量

                            LogUtil.e("battery===", "==="+s1);  //0202016478A2FBC2537CA17B22DB9AE9

//                            if (TextUtils.isEmpty(data)) {
//                                tvCz.setText(R.string.battery_fail);
//                            } else {
//                                tvCz.setText(R.string.battery_success);
//                                tvBattery.setText(getText(R.string.battery) + String.valueOf(Integer.parseInt(data, 16)));
//                            }

                            tv_electricity.setText(Integer.parseInt(s1.substring(6, 8), 16)+"%");

                        }else if(s1.startsWith("020202")){    //电量2

                            LogUtil.e("battery2===", "==="+s1);  //020202 64 00 0E42 0000 E100E040270020

//                            if (TextUtils.isEmpty(data)) {
//                                tvCz.setText(R.string.battery_fail);
//                            } else {
//                                tvCz.setText(R.string.battery_success);
//                                tvBattery.setText(getText(R.string.battery) + String.valueOf(Integer.parseInt(data, 16)));
//                            }

                            tv_electricity.setText(Integer.parseInt(s1.substring(10, 14), 16)/1000f+"V");

                        }else if(s1.startsWith("058502")){

                            LogUtil.e("xinbiao===", "当前操作：搜索信标成功"+s1.substring(2*10, 2*10+2)+"==="+s1.substring(2*11, 2*11+2)+"==="+s1);

                            if("000000000000".equals(s1.substring(2*4, 2*10))){
                                major = 0;
                            }else{
                                major = 1;
                            }

                        }else {
                            LogUtil.e("s1===", loadingDialog.isShowing()+"==="+"==="+s1);  //CB05010100B2F70703C0906C2D5E3A51

//                                            if(isOpenLock && s1.startsWith("CB0501")){
//                                                openLock();
//                                            }

//                                            closeLoadingDialog();
                        }
                    }
                });




//                        EventBus.getDefault().post(BleNotifyEvent(decode));

//                        else if(s1.startsWith("050F")){
//                            LogUtil.e("closeLock===2", "==="+s1);        //050F0101017A0020782400200F690300
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
//                                LogUtil.e("biking===", "biking===锁已关闭==="+first3);
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


    private void getBleToken(){
        String s = new GetTokenTxOrder().generateString();  //06010101490E602E46311640422E5238

        LogUtil.e("getBleToken===1", "==="+s);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                LogUtil.e("getBleToken==onWriteSuc", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                LogUtil.e("getBleToken=onWriteFail", "==="+exception);

                if("3".equals(type)){
                    if(isOpenLock){
                        unlock();
                    }else{
                        Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void getBattery(){
        String s = new BatteryTxOrder().generateString();  //06010101490E602E46311640422E5238

        LogUtil.e("getBattery===1", "==="+s);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                LogUtil.e("getBattery==onWriteS", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                LogUtil.e("getBattery=onWriteFa", "==="+exception);
            }
        });
    }

    private void getBattery2(){
        LogUtil.e("getBattery2===", "==="+new Battery2TxOrder());  //1648395B

        String s = new Battery2TxOrder().generateString();  //06010101490E602E46311640422E5238

        LogUtil.e("getBattery2===1", "==="+s);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                LogUtil.e("getBattery==onWriteS", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                LogUtil.e("getBattery=onWriteFa", "==="+exception);
            }
        });
    }

    private void getLockStatus(){
        String s = new GetLockStatusTxOrder().generateString();  //06010101490E602E46311640422E5238

        LogUtil.e("getLockStatus===1", isOpenLock+"==="+isLookPsdBtn);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                LogUtil.e("getLockStatus==onWriteS", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                LogUtil.e("getLockStatus=onWriteFa", "==="+exception);
            }
        });
    }

    private void getXinbiao(){
        String s = new XinbiaoTxOrder().generateString();  //06010101490E602E46311640422E5238

        LogUtil.e("getXinbiao===1", "==="+s);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                LogUtil.e("getXinbiao==onWriteS", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                LogUtil.e("getXinbiao=onWriteFa", "==="+exception);
            }
        });
    }


    private void openLock() {
        String s = new OpenLockTxOrder().generateString();

        LogUtil.e("openLock===1", token+"==="+s);     //989C064A===050106323031373135989C064A750217

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                LogUtil.e("openLock===onWriteSuccess", current+"==="+total+"==="+justWrite);
            }

            @Override
            public void onWriteFailure(BleException exception) {
                LogUtil.e("openLock===onWriteFailure", "==="+exception);
            }
        });
    }

    //泺平===连接设备
    private void connectDeviceLP() {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(4)
                .setConnectTimeout(2000)
//                .setConnectRetry(0)
//                .setConnectTimeout(timeout)
//                .setServiceDiscoverRetry(0)
//                .setServiceDiscoverTimeout(10000)
//                .setEnableNotifyRetry(0)
//                .setEnableNotifyTimeout(10000)
                .build();

        ClientManager.getClient().connect(m_nowMac, options, new IConnectResponse() {
            @Override
            public void onResponseFail(final int code) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        isStop = false;
                        isLookPsdBtn = false;

                        LogUtil.e("connectDeviceLP===", "Fail==="+Code.toString(code));
//                ToastUtil.showMessageApp(context, Code.toString(code));

//                closeLoadingDialog();
//                if (loadingDialogWithHelp != null && loadingDialogWithHelp.isShowing()){
//                    loadingDialogWithHelp.dismiss();
//                }

//                        if(popupwindow!=null){
//                            popupwindow.dismiss();
//                        }

                        Toast.makeText(context,"蓝牙连接失败，重启软件试试吧！",Toast.LENGTH_LONG).show();
                        closeLoadingDialog();
//                      car_notification(isOpenLock?1:isAgain?2:isEndBtn?3:0, 2, 0);  //TODO
                    }
                });
            }

            @Override
            public void onResponseSuccess(final BleGattProfile profile) {
//                BluetoothLog.v(String.format("profile:\n%s", profile));
//                refreshData(true);

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        isStop = true;
                        isLookPsdBtn = true;

                        LogUtil.e("connectDeviceLP===", "Success==="+profile);

                        if(isOpenLock){
                            getStateLP();
                        }else{
                            queryOpenState();
                        }
//                        getStateLP();
                    }
                });


            }
        });
    }



    //泺平
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            LogUtil.e("scan===","DeviceListActivity.onSearchStarted");
//            mDevices.clear();
//            mAdapter.notifyDataChanged();
        }

        @Override
        public void onDeviceFounded(final SearchResult device) {

            LogUtil.e("scan===onDeviceFounded",device.device.getName() + "===" + device.device.getAddress());

//            bike:GpDTxe8DGN412
//            bike:LUPFKsrUyR405
//            bike:LUPFKsrUyK405
//            bike:L6OsRAiviK289===E8:EB:11:02:2B:E2

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(m_nowMac.equals(device.device.getAddress())){

                        LogUtil.e("scan===stop",device.device.getName() + "===" + device.device.getAddress());

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
            LogUtil.e("scan===","DeviceListActivity.onSearchStopped");

        }

        @Override
        public void onSearchCanceled() {
            LogUtil.e("scan===","DeviceListActivity.onSearchCanceled");

        }
    };

    ////泺平===监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(final String mac, final int status) {

//            LogUtil.e("ConnectStatus===", mac+"===="+(status == STATUS_CONNECTED));

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("ConnectStatus===biking", isLookPsdBtn+"==="+mac+"==="+(status == STATUS_CONNECTED)+"==="+m_nowMac);

                    if(status == STATUS_CONNECTED){
                        isLookPsdBtn = true;

//                        ToastUtil.showMessageApp(context,"设备连接成功");
                    }else{
                        isLookPsdBtn = false;

                        ToastUtil.showMessageApp(context,"蓝牙连接已断开");
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
                    LogUtil.e("onNotifyClose===", "====");

//                    ToastUtil.showMessageApp(context,"锁已关闭");

//                    if("6".equals(type)){
//                        lookPsdBtn.setText("再次开锁");
//                        SharedPreferencesUrls.getInstance().putString("tempStat","1");
//                    }

                    ToastUtil.showMessageApp(context,"锁已关闭");
                    tv_lock_status.setText("已关锁");

//                    queryOpenState();
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
                        LogUtil.e("getBleRecord===###", transType+"==Major:"+ Major +"---Minor:"+Minor+"---mackey:"+mackey);

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
                LogUtil.e("getBleRecord===", "Success===Empty");

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
                        LogUtil.e("getBleRecord===fail", Code.toString(code));
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
                        LogUtil.e("deleteBleRecord2===", transType+"==Major:"+ Major +"---Minor:"+Minor);

                        transtype = transType;
                        major = Major;
                        minor = Minor;

                        deleteBleRecord2(bikeTradeNo);
                    }
                });
            }

            @Override
            public void onResponseSuccessEmpty() {
                LogUtil.e("biking=deleteBleRecord2", "Success===Empty");

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
                        LogUtil.e("biking=deleteBleRecord2", Code.toString(code));
                    }
                });

            }
        });
    }

    private void getStateLP(){
        ClientManager.getClient().getStatus(m_nowMac, new IGetStatusResponse() {
            @Override
            public void onResponseSuccess(String version, String keySerial, String macKey, final String vol) {
//                    quantity = vol+"";

                LogUtil.e("getStatus===", isOpenLock+"===="+vol+"===="+macKey);
                keySource = keySerial;

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_electricity.setText(vol + "V");

                        if(isOpenLock){
                            rent();
                        }else{
                            queryOpenState();
                        }

                    }
                });
            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("getStatus===fail", Code.toString(code));

                        if("锁已开".equals(Code.toString(code))){
                            ToastUtil.showMessageApp(context,"锁已打开");
                            tv_lock_status.setText("已开锁");
                        }

                        closeLoadingDialog();

//                            ToastUtil.showMessageApp(context, Code.toString(code));
                    }
                });
            }

        });
    }

    //泺平===还车_查锁是否关闭
    private void queryOpenState() {
        LogUtil.e("queryOpenState===0", "===="+m_nowMac);

//        UIHelper.showProgress(this, R.string.collectState);
        ClientManager.getClient().queryOpenState(m_nowMac, new IQueryOpenStateResponse() {
            @Override
            public void onResponseSuccess(final boolean open) {
//                UIHelper.dismiss();

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("queryOpenState===", "===="+open);


                        if(open) {
                            ToastUtil.showMessageApp(context,"锁已打开");

                            tv_lock_status.setText("已开锁");
                        }else {
                            ToastUtil.showMessageApp(context,"锁已关闭");

                            tv_lock_status.setText("已关锁");

                        }

                        getBleRecord();
                        closeLoadingDialog();
                    }
                });
            }

            @Override
            public void onResponseFail(final int code) {
                LogUtil.e("queryOpenState===f",  Code.toString(code));
//                UIHelper.dismiss();

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        ToastUtil.showMessageApp(context,Code.toString(code));

                        closeLoadingDialog();
                    }
                });

            }
        });
    }

    //泺平_开锁
    protected void rent(){

        LogUtil.e("rent===000",lock_no+"==="+m_nowMac+"==="+keySource);

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
                            LogUtil.e("rent===","==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            KeyBean bean = JSON.parseObject(result.getData(), KeyBean.class);

//                            if (loadingDialog != null && loadingDialog.isShowing()){
//                                loadingDialog.dismiss();
//                            }

                            encryptionKey = bean.getEncryptionKey();
                            keys = bean.getKeys();
                            serverTime = bean.getServerTime();

                            LogUtil.e("rent===", m_nowMac+"==="+encryptionKey+"==="+keys);

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
        LogUtil.e("mf===openBleLock", serverTime+"==="+keys+"==="+encryptionKey);

        ClientManager.getClient().openLock(m_nowMac,"000000000000", (int) serverTime, keys, encryptionKey, new IEmptyResponse(){
            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("openLock===Fail", m_nowMac+"==="+Code.toString(code));

                        queryOpenState();

                        getBleRecord();

//                        car_notification(1, 3, 0);    //TODO

                    }
                });

            }

            @Override
            public void onResponseSuccess() {
                LogUtil.e("openLock===Success", "===");

//                m_myHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 2000);

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

//                        queryOpenState();
                        ToastUtil.showMessageApp(context,"锁已打开");

                        tv_lock_status.setText("已开锁");

                        getBleRecord();

                        closeLoadingDialog();

                        isFinish = true;
                    }
                });



//                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

//                car_notification(1, 1, 0);      //TODO


            }
        });
    }

    //泺平===与设备，获取记录
    private void getBleRecord() {

        LogUtil.e("getBleRecord===", "###==="+m_nowMac);

        ClientManager.getClient().getRecord(m_nowMac, new IGetRecordResponse() {

            @Override
            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, int Major, int Minor, String vol) {
                LogUtil.e("getBleRecord===0", transType + "==Major:"+ Major +"---Minor:"+Minor+"==="+bikeTradeNo);
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
                LogUtil.e("getBleRecord===1", "Success===Empty");
            }

            @Override
            public void onResponseFail(int code) {
                LogUtil.e("getBleRecord===2", Code.toString(code));
//                ToastUtil.showMessageApp(context, Code.toString(code));
            }
        });
    }

    //泺平===与设备，删除记录
    private void deleteBleRecord(String tradeNo) {
//        UIHelper.showProgress(this, R.string.delete_bike_record);
        ClientManager.getClient().deleteRecord(m_nowMac, tradeNo, new IGetRecordResponse() {

            @Override
            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, int Major, int Minor, String vol) {
                LogUtil.e("biking=deleteBleRecord", "Major:"+ Major +"---Minor:"+Minor);
                deleteBleRecord(bikeTradeNo);
            }

            @Override
            public void onResponseSuccessEmpty() {
//                UIHelper.dismiss();
                LogUtil.e("scan===deleteBleRecord", "Success===Empty");

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoadingDialog();

                        isFinish = true;

//                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");

//                        car_notification(1, 1, 0);        //TODO
                    }
                });

            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("scan===deleteBleRecord", Code.toString(code));
//                      ToastUtil.showMessageApp(context, Code.toString(code));
                        if(popupwindow!=null){
                            popupwindow.dismiss();
                        }
                    }
                });


            }
        });
    }


    //助力车关锁
    private void lock() {
        LogUtil.e("mf===lock", "===");


//        RequestParams params = new RequestParams();
//        params.put("number", codenum);

        HttpHelper.post(context, Urls.lock+codenum, null, new TextHttpResponseHandler() {
            @Override
            public void onStart() {

                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                LogUtil.e("mf===lock_fail", responseString + "===" + throwable.toString());
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("mf===lock_1", carmodel_id + "===" + responseString + "===" + result.data);


                            if(result.getStatus_code()==200){
                                ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");
                                if(!"7".equals(type)){
                                    tv_lock_status.setText("已关锁");
                                }

                            }else{

                                if("11".equals(type)){
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
                                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                                            loadingDialog.setTitle("正在唤醒车锁");
                                            loadingDialog.show();
                                        }

                                        if(!TbitBle.hasInitialized()){
                                            TbitBle.initialize(context, new SecretProtocolAdapter());
                                        }

                                        if(TbitBle.getBleConnectionState()==0){
                                            tbtble_connect();

                                            isConnect = false;
                                            //                            m_myHandler2.postDelayed(new Runnable() {
                                            //                                @Override
                                            //                                public void run() {
                                            //                                    if (!isConnect){
                                            ////                                          closeLoadingDialog();
                                            //
                                            //                                        LogUtil.e("close===11==timeout", isConnect + "==="+activity.isFinishing());
                                            //
                                            //                                        TbitBle.disConnect();
                                            //
                                            //                                        lock();
                                            //                                    }
                                            //                                }
                                            //                            }, timeout);
                                        }else{
                                            tbtble_lock();
                                        }

                                    }
                                }else{
                                    ToastUtil.showMessageApp(context,result.getMessage());
                                }



                            }



//                            n=0;
//                            carLoopClose();

                        } catch (Exception e) {

//                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
                        }

                        closeLoadingDialog();

                    }
                });
            }
        });
    }

    //助力车开锁
    private void unlock() {
        LogUtil.e("mf===unlock", "===");

//        isOpenLock = false;

//        RequestParams params = new RequestParams();
//        params.put("number", codenum);

        HttpHelper.post(context, Urls.unlock+codenum, null, new TextHttpResponseHandler() {     //TODO  都是执行的关锁
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

                            LogUtil.e("mf===unlock1", carmodel_id+ "===" + type + "===" + codenum + "===" + responseString + "===" + result.data);

                            if(result.getStatus_code()==200){
                                closeLoadingDialog();
                                ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                                if(!"7".equals(type)){
                                    tv_lock_status.setText("已开锁");
                                }

                            }else{
//

                                if("11".equals(type)){
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
//                                        closeLoadingDialog();
//                                        if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                            loadingDialog.setTitle("正在唤醒车锁");
//                                            loadingDialog.show();
//                                        }

                                        if(!TbitBle.hasInitialized()){
                                            TbitBle.initialize(context, new SecretProtocolAdapter());
                                        }

                                        LogUtil.e("open===11_2", TbitBle.getBleConnectionState() + "===");

                                        if(TbitBle.getBleConnectionState()==0){
                                            tbtble_connect();

                                            isConnect = false;
//                                m_myHandler2.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (!isConnect){
////                                          closeLoadingDialog();
//
//                                            LogUtil.e("mf===11==timeout", isConnect + "==="+activity.isFinishing());
//
//                                            TbitBle.disConnect();
//
//                                            unlock();
//                                        }
//                                    }
//                                }, timeout);
                                        }else{
                                            tbtble_unlock();
                                        }

                                    }
                                }else{
                                    closeLoadingDialog();
                                    ToastUtil.showMessageApp(context,result.getMessage());
                                }



                            }


//                            popupwindow.dismiss();

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
//        LogUtil.e("mf===carLoopOpen", order_id2+"===" +order_id+"===" + "===" + codenum);
//
//        HttpHelper.get(context, Urls.order_detail+order_id2, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
////                onStartCommon("正在加载");
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                LogUtil.e("mf===carLoopOpen_fail", responseString + "===" + throwable.toString());
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
//                            LogUtil.e("mf===carLoopOpen1", responseString + "===" + result.data);
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
//                    LogUtil.e("mf===queryCarStatusOpen", "===");
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
//        LogUtil.e("mf===carLoopClose", order_id2+"===" +order_id+"===" + isAgain+"===" + codenum);
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
//                LogUtil.e("mf===carLoopClose_fail", responseString + "===" + throwable.toString());
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
//                            LogUtil.e("mf===carLoopClose1", responseString + "===" + result.data);
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
//                    LogUtil.e("mf=queryCarStatusClose", "===");
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

            LogUtil.e("connectDevice===", "==="+imei);
        }
    }

    //小安
    @Override
    public void onConnect(BluetoothDevice bluetoothDevice) {
        LogUtil.e("mf===Xiaoan", "Connect==="+isConnect);

        isConnect = true;
        m_myHandler.removeCallbacksAndMessages(null);

        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

//                String tvAgain = tv_againBtn.getText().toString().trim();

                LogUtil.e("mf===Xiaoan1", isAgain+"==="+isEndBtn+"==="+isOpenLock);

                if(isOpenLock){
                    xiaoanOpen_blue();
                }else{
                    xiaoanClose_blue();
                }


            }
        }, 2 * 1000);

    }



    //小安
    @Override
    public void onDisConnect(BluetoothDevice bluetoothDevice) {
        LogUtil.e("mf===Xiaoan", "DisConnect==="+isConnect);


        if(isConnect){
            isConnect = false;

            LogUtil.e("mf===Xiaoan2", "DisConnect==="+isConnect);
            return;
        }

//        if (apiClient != null) {
//            apiClient.onDestroy();
//        }

        isConnect = false;


    }

    public void xiaoanOpen_blue() {
        apiClient.setDefend(false, new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("xiaoanOpen===", response.toString());

                        if(response.code==0){
                            isFinish = true;

                            closeLoadingDialog();
                            ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");
                            if(!"7".equals(type)){
                                tv_lock_status.setText("已开锁");
                            }


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
                        LogUtil.e("xiaoanClose===", type+"==="+deviceuuid+"==="+response.toString());

                        if(response.code==0){
                            ToastUtil.showMessageApp(context,"恭喜您,关锁成功!");
                            closeLoadingDialog();
                            if(!"7".equals(type)){
                                tv_lock_status.setText("已上锁");
                            }


//                            macList2 = new ArrayList<> (macList);
//
//                            car_notification(isAgain?2:3, 1, isAgain?0:1);    //TODO

                        }else if(response.code==6){
                            ToastUtil.showMessageApp(context,"车辆未停止，请停止后再试");

                            closeLoadingDialog();
                        }else{

                            lock();

//                            if("108".equals(info)){       //TODO  2
//                                LogUtil.e("biking_defend===1", "====");
//
//                            }else{
//                                LogUtil.e("biking_defend===2", "====");
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

//        LogUtil.e("onLocationChanged=0", mListener+"==="+amapLocation);

        if (mListener != null && amapLocation != null) {

//            LogUtil.e("onLocationChanged=1", latitude +"==="+amapLocation.getLatitude() +">>>"+longitude+"==="+amapLocation.getLongitude());

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

//                    LogUtil.e("onLocationChanged=", mFirstFix+"==="+myLocation);

//                    Toast.makeText(context,"==="+myLocation, Toast.LENGTH_SHORT).show();

                    if (mFirstFix){
//                        initNearby(amapLocation.getLatitude(),amapLocation.getLongitude());
//                        cars(false);
                        mFirstFix = false;

//                        Toast.makeText(context,"==="+leveltemp, Toast.LENGTH_SHORT).show();

                        LogUtil.e("onLocationChanged===true", mFirstFix+"==="+myLocation);

//                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, leveltemp));
                        accuracy = amapLocation.getAccuracy();
                        addChooseMarker();
                        addCircle(myLocation, amapLocation.getAccuracy());//添加定位精度圆

                        initHttp(true);

//                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18f));
                    } else {
//                        centerMarker.remove();
                        mCircle.remove();
                    }


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
                LogUtil.e("sf===requestCode", requestCode+"==="+resultCode+"==="+data);

                switch (requestCode) {

                    case 101:
                        if (resultCode == RESULT_OK) {
                            String tz = data.getStringExtra("tz");
                            if(tz!=null && "1".equals(tz)){
                                ((MainActivity)getActivity()).changeTab(4);
                            }

                            String sx = data.getStringExtra("sx");
                            if(sx!=null && "1".equals(sx)){
//                                initNearby(latitude, longitude);
                                cars(false);
                            }


                            codenum = data.getStringExtra("codenum");
                            lock_name = data.getStringExtra("lock_name");
                            lock_title = data.getStringExtra("lock_title");
                            m_nowMac = data.getStringExtra("m_nowMac");
                            carmodel_id = data.getIntExtra("carmodel_id", 1);
                            type = data.getStringExtra("type");
                            lock_no = data.getStringExtra("lock_no");
                            bleid = data.getStringExtra("bleid");
                            deviceuuid = data.getStringExtra("deviceuuid");
                            electricity = data.getStringExtra("electricity");
                            carmodel_name = data.getStringExtra("carmodel_name");
                            lock_status = data.getIntExtra("lock_status", 0);
                            status = data.getIntExtra("status", 0);
                            can_finish_order = data.getIntExtra("can_finish_order", 0);
                            bad_reason = data.getStringExtra("bad_reason");
                            battery_name = data.getStringExtra("battery_name");
                            isMac = data.getBooleanExtra("isMac", false);
                            isSearch = data.getBooleanExtra("isSearch", false);

                            LogUtil.e("sf===requestCode1", isMac+"==="+codenum+"==="+carmodel_id+"==="+type+"==="+m_nowMac+"==="+lock_name+"==="+lock_title+"==="+lock_no+"==="+bleid +"==="+deviceuuid+"==="+electricity+"==="+carmodel_name+"==="+lock_status+"==="+can_finish_order+"==="+status+"==="+bad_reason);

                            initParams();

                            if(carmodel_id==1){
                                initmPopupRentWindowView();
                            }else{
                                initmPopupRentWindowView2();
                            }


                        } else {
                            Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                        }

                        break;

//                    case 1:
//                        if (resultCode == RESULT_OK) {
//                            String result = data.getStringExtra("QR_CODE");
////                    upcarmap(result);
//                            lock(result);
//                        } else {
//                            Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
//                        }
//
//                        LogUtil.e("requestCode===1", "==="+resultCode);
//                        break;
//
//                    case 2:
//                        if (resultCode == RESULT_OK) {
//                            String result = data.getStringExtra("QR_CODE");
//                            switch (isLock){
//                                case 1:
//                                    LogUtil.e("requestCode===2_1", "==="+resultCode);
//                                    lock(result);
//                                    break;
//                                case 2:
//                                    LogUtil.e("requestCode===2_2", "==="+resultCode);
//                                    unLock(result);
//                                    break;
//                                case 3:
//                                    LogUtil.e("requestCode===2_3", "==="+resultCode);
//                                    endCar(result);
//                                    break;
//                                case 4:
//                                    LogUtil.e("requestCode===2_4", "==="+resultCode);
//                                    recycle(result);
//                                    break;
//                                default:
//                                    break;
//                            }
//                        } else {
//                            Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
//                        }
//
//                        LogUtil.e("requestCode===2", "==="+resultCode);
//                        break;

                    case PRIVATE_CODE:
                        openGPSSettings();
                        break;

                    case 188:

                        if (resultCode == RESULT_OK) {
//                            closeLoadingDialog();

                            isPermission = true;

                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("正在唤醒车锁");
                                loadingDialog.show();
                            }

                            LogUtil.e("188===", isAgain+"==="+isConnect+"==="+isLookPsdBtn+"==="+oid+"==="+m_nowMac+"==="+type+">>>"+isOpenLock+"==="+isEndBtn);

                            initParams();

//                            if ("2".equals(type) || "3".equals(type)){
//
//                                LogUtil.e("mf===requestCode2", codenum+"==="+type);
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
//                                LogUtil.e("initView===5", "==="+isLookPsdBtn);
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

                            LogUtil.e("188===order", isAgain+"==="+isLookPsdBtn+"==="+oid+"==="+m_nowMac+"==="+type+">>>"+isOpenLock+"==="+isEndBtn);


                            open_lock();

                        }else{
                            ToastUtil.showMessageApp(context, "需要打开蓝牙");

                            LogUtil.e("188===fail", oid+"===");

                            if(popupwindow!=null){
                                popupwindow.dismiss();
                            }

                            closeLoadingDialog2();

                        }
                        break;

                    case 189:
                        LogUtil.e("189===", oid+"===");

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

    void initParams(){
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
    }


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
//                            SharedPreferencesUrls.getInstance().putString("type", "");

                            end2();

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
