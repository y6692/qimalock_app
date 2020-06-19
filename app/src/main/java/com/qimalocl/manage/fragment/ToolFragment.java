package com.qimalocl.manage.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.BindSchoolActivity;
import com.qimalocl.manage.activity.CarDispatchActivity;
import com.qimalocl.manage.activity.DeviceSelectActivity;
import com.qimalocl.manage.activity.GetDotActivity;
import com.qimalocl.manage.activity.HistorysRecordActivity;
import com.qimalocl.manage.activity.MerchantAddressMapActivity;
import com.qimalocl.manage.activity.QueryActivity;
import com.qimalocl.manage.base.BaseFragment;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class ToolFragment extends BaseFragment implements View.OnClickListener{

    Unbinder unbinder;

    private Context context;

//    @BindView(R.id.ll_history)
//    LinearLayout historyLayout;
//    @BindView(R.id.ll_location)
//    LinearLayout locationLayout;

    @BindView(R.id.ll_bind) LinearLayout bindLayout;
    @BindView(R.id.ll_in_storage) LinearLayout inStorageLayout;
    @BindView(R.id.ll_fence) LinearLayout fenceLayout;
    @BindView(R.id.ll_dispatch) LinearLayout dispatchLayout;
    @BindView(R.id.ll_query) LinearLayout queryLayout;

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
        v = inflater.inflate(R.layout.fragment_tool, null);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        initView();
    }


    private void initView(){

//        historyLayout.setOnClickListener(this);
//        locationLayout.setOnClickListener(this);

//        @BindView(R.id.ll_bind) LinearLayout bindLayout;
//        @BindView(R.id.ll_in_storage) LinearLayout inStorageLayout;
//        @BindView(R.id.ll_fence) LinearLayout fenceLayout;
//        @BindView(R.id.ll_dispatch) LinearLayout dispatchLayout;
//        @BindView(R.id.ll_query) LinearLayout queryLayout;

        bindLayout.setOnClickListener(this);
        inStorageLayout.setOnClickListener(this);
        fenceLayout.setOnClickListener(this);
        dispatchLayout.setOnClickListener(this);
        queryLayout.setOnClickListener(this);
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

//            case R.id.ll_history:
//                UIHelper.goToAct(context, HistorysRecordActivity.class);
//                break;
//
//            case R.id.ll_location:
//                UIHelper.goToAct(context, MerchantAddressMapActivity.class);
//                break;

            case R.id.ll_bind:
                UIHelper.goToAct(context, BindSchoolActivity.class);
                break;

            case R.id.ll_in_storage:
//                end2();
                UIHelper.goToAct(context, DeviceSelectActivity.class);
                break;

            case R.id.ll_fence:
                UIHelper.goToAct(context, GetDotActivity.class);
                break;

            case R.id.ll_dispatch:
                UIHelper.goToAct(context, CarDispatchActivity.class);
                break;

            case R.id.ll_query:
                UIHelper.goToAct(context, QueryActivity.class);
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

        LogUtil.e("requestCode===", "==="+requestCode);

        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("QR_CODE");
                } else {
					Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }

                LogUtil.e("requestCode===1", "==="+resultCode);
                break;

            default:
                break;

        }
    }


}
