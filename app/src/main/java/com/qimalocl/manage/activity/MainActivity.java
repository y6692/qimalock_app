package com.qimalocl.manage.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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
import com.qimalocl.manage.fragment.MaintenanceFragment;
import com.qimalocl.manage.fragment.MissionFragment;
import com.qimalocl.manage.fragment.QueryFragment;
import com.qimalocl.manage.fragment.ScanFragment;
import com.qimalocl.manage.model.NearbyBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.TabEntity;
import com.zbar.lib.ScanCaptureAct;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String INTENT_MSG_COUNT = "INTENT_MSG_COUNT";
    public final static String MESSAGE_RECEIVED_ACTION = "io.yunba.example.msg_received_action";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";


    @BindView(R.id.fl_change) FrameLayout flChange;
    @BindView(R.id.tab) CommonTabLayout tab;
    @BindView(R.id.ll_tab) LinearLayout llTab;

    private Context mContext;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = { "用车", "任务", "查询", "维护" };
    private int[] mIconUnselectIds = {
            R.mipmap.scan, R.mipmap.mission, R.mipmap.query,R.mipmap.maintenance
    };
    private int[] mIconSelectIds = {
            R.mipmap.scan2, R.mipmap.mission2, R.mipmap.query2,R.mipmap.maintenance2
    };
    private ScanFragment scanFragment;
    private MissionFragment missionFragment;
    private QueryFragment queryFragment;
    private MaintenanceFragment maintenanceFragment;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ui_main);
        ButterKnife.bind(this);

        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(mReceiver, filter);

        initData();
        initView();
        initListener();
//        initLocation();
//        AppApplication.getApp().scan();
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();

//            Log.e("main===mReceiver", "==="+action);

            if ("data.broadcast.action".equals(action)) {
                int count = intent.getIntExtra("count", 0);
                if (count > 0) {
                    tab.showMsg(1, count);
                    tab.setMsgMargin(1, -8, 5);
                } else {
                    tab.hideMsg(0);
                }
            }
        }
    };

    private void initData() {
        mContext = this;
        scanFragment = new ScanFragment();
        missionFragment = new MissionFragment();
        queryFragment = new QueryFragment();
        maintenanceFragment = new MaintenanceFragment();
        mFragments.add(scanFragment);
        mFragments.add(missionFragment);
        mFragments.add(queryFragment);
        mFragments.add(maintenanceFragment);

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
    }

    private void initView() {

//        tab = findViewById(R.id.tab);

        tab.setTabData(mTabEntities, MainActivity.this, R.id.fl_change, mFragments);
        tab.setCurrentTab(0);
    }

    public void changeTab(int index) {
        tab.setCurrentTab(index);
    }

    private void initListener() {
    }

    @Override protected void onResume() {
        super.onResume();
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
