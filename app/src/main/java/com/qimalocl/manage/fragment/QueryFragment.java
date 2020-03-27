package com.qimalocl.manage.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.BikeLocationActivity;
import com.qimalocl.manage.activity.DeviceListActivity;
import com.qimalocl.manage.activity.HistorysRecordActivity;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.activity.MerchantAddressMapActivity;
import com.qimalocl.manage.base.BaseFragment;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.NearbyBean;
import com.qimalocl.manage.model.ResultConsel;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class QueryFragment extends BaseFragment implements View.OnClickListener{

    Unbinder unbinder;

    private Context context;

    @BindView(R.id.ll_history)
    LinearLayout historyLayout;
    @BindView(R.id.ll_location)
    LinearLayout locationLayout;


    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = true;
    private LatLng myLocation = null;
    private Circle mCircle;
    private BitmapDescriptor successDescripter;
    private BitmapDescriptor bikeDescripter;
    private Handler handler = new Handler();
    private Marker centerMarker;
    private boolean isMovingMarker = false;

    private List<Marker> bikeMarkerList;
    private boolean isUp = false;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private int isLock = 0;
    private View v;


    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_query, null);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        initView();
    }


    private void initView(){

        historyLayout.setOnClickListener(this);
        locationLayout.setOnClickListener(this);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //pause
        }else{
            //resume
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.ll_history:
                UIHelper.goToAct(context, HistorysRecordActivity.class);
                break;

            case R.id.ll_location:
                UIHelper.goToAct(context, MerchantAddressMapActivity.class);
                break;


            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("requestCode===", "==="+requestCode);

        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("QR_CODE");
                } else {
					Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }

                Log.e("requestCode===1", "==="+resultCode);
                break;

            default:
                break;

        }
    }


}
