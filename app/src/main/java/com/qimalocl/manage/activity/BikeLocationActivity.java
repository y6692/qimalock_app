package com.qimalocl.manage.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseViewAdapter;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.core.common.DisplayUtil;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.ClearEditText;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.core.widget.MyListView;
import com.qimalocl.manage.model.GPSTrackBean;
import com.qimalocl.manage.model.HistorysRecordBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.TagBean;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.qimalocl.manage.utils.LogUtil;
import com.qimalocl.manage.utils.ToastUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator1 on 2017/11/9.
 */

public class BikeLocationActivity extends SwipeBackActivity implements View.OnClickListener {

    @BindView(R.id.ui_bike_location_backBtn)
    ImageView backBtn;
    @BindView(R.id.ui_bike_location_codeNumEdit)
    ClearEditText codeNumEdit;
    @BindView(R.id.ui_bike_location_searchBtn)
    TextView searchBtn;
    @BindView(R.id.ui_bike_location_map)
    MapView mapView;

    private Context context;
//    private LoadingDialog loadingDialog;

    private double latitude;
    private double longitude;
    private AMap aMap;
    private static final int STROKE_COLOR = Color.argb(80, 3, 0, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private LatLng myLocation = null;
    private Marker centerMarker;
    private BitmapDescriptor successDescripter;
    private BitmapDescriptor bikeDescripter;
    private BitmapDescriptor originDescripter;
    private BitmapDescriptor terminusDescripter;

    private Polyline polyline;
    private PolylineOptions mPolyoptions;

    Marker originMarker;
    Marker terminusMarker;

    String begintime;
    String endtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_bike_location);
        ButterKnife.bind(this);
        context = this;
        init();
    }

    private void init(){
//        titleText.setText("车辆位置");
//        rightBtn.setText("查询");
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(16);// 设置缩放监听
        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
//        bikeDescripter = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);
//        originDescripter = BitmapDescriptorFactory.fromResource(R.drawable.origin_icon);
//        terminusDescripter = BitmapDescriptorFactory.fromResource(R.drawable.terminus_icon);
        aMap.moveCamera(cameraUpdate);


        backBtn.setOnClickListener(this);
//        rightBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);

    }

    private void gpstrack(String codenum){

        final String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            ToastUtil.showMessageApp(context,"请先登录账号");
            UIHelper.goToAct(context, LoginActivity.class);
        }else {

            if (polyline != null) {
                polyline.remove();
                polyline = null;
            }

            if(originMarker!=null){
                originMarker.remove();
            }

            if(terminusMarker!=null){
                terminusMarker.remove();
            }

            mPolyoptions = new PolylineOptions();
            mPolyoptions.width(40f);
            mPolyoptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.grasp_trace_line));

            RequestParams params = new RequestParams();
            params.put("uid", uid);
            params.put("access_token", access_token);
            params.put("tokencode", codenum);        //80001819、80002041
            params.put("begintime", begintime);
            params.put("endtime", endtime);
            HttpHelper.get(context, Urls.gpstrack, params, new TextHttpResponseHandler() {
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

                            LogUtil.e("gpstrack===","==="+responseString);

//                            for (Marker marker : bikeMarkerList){
//                                if (marker != null){
//                                    marker.remove();
//                                }
//                            }
//                            if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
//                                bikeMarkerList.clear();
//                            }
                            if (0 == array.length()){
                                ToastUtil.showMessageApp(context, "没有轨迹");
                            }else {
                                for (int i = 0; i < array.length(); i++){
                                    GPSTrackBean bean = JSON.parseObject(array.getJSONObject(i).toString(), GPSTrackBean.class);
                                    // 加入自定义标签

                                    LatLng latLng = new LatLng(Double.parseDouble(bean.getLat()),Double.parseDouble(bean.getLon()));

                                    if(i==0){
                                        MarkerOptions bikeMarkerOption = new MarkerOptions().position(latLng).icon(originDescripter);
                                        originMarker = aMap.addMarker(bikeMarkerOption);

                                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                                    }else if(i==array.length()-1){
                                        MarkerOptions bikeMarkerOption = new MarkerOptions().position(latLng).icon(terminusDescripter);
                                        terminusMarker = aMap.addMarker(bikeMarkerOption);
                                    }else{
//                                        MarkerOptions bikeMarkerOption = new MarkerOptions().position(latLng).icon(bikeDescripter);
//                                        Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
                                    }


                                    mPolyoptions.add(latLng);

                                    LogUtil.e("gpstrack===",polyline+"==="+mPolyoptions.getPoints().size());

                                    if (mPolyoptions.getPoints().size() > 1) {
                                        if (polyline != null) {
                                            polyline.setPoints(mPolyoptions.getPoints());
                                        } else {
                                            polyline = aMap.addPolyline(mPolyoptions);
                                        }
//                                        polyline = aMap.addPolyline(mPolyoptions);
                                    }

//                                    bikeMarkerList.add(bikeMarker);
                                }
                            }
                        } else {
                            ToastUtil.showMessageApp(context,result.getMsg());
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

    private void initListener(){

        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
                Intent intent = new Intent(context, HistoryRoadFiltateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,0);
                break;
            case R.id.ui_bike_location_searchBtn:
                String codenum = codeNumEdit.getText().toString().trim();
                if (codenum == null || "".equals(codenum)){
                    Toast.makeText(context,"请输入车辆编号",Toast.LENGTH_SHORT).show();
                    return;
                }
                initHttp(codenum);
                break;
            default:
                break;
        }
    }

    private void initHttp(final String codenum) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
            return;
        }
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("codenum",codenum);
        HttpHelper.get(context, Urls.carsLocation, params, new TextHttpResponseHandler() {
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
                LogUtil.e("Test","RRRR:"+responseString);
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")){
                        JSONObject jsonObject = new JSONObject(result.getData());
                        if (!"[]".equals(result.getData())){
//                            Intent intent = new Intent(context,MerchantAddressMapActivity.class);
//                            intent.putExtra("latitude",jsonObject.getString("latitude"));
//                            intent.putExtra("longitude",jsonObject.getString("longitude"));
//                            startActivity(intent);

//                          latitude = Double.parseDouble(getIntent().getExtras().getString("latitude"));
//                          longitude = Double.parseDouble(getIntent().getExtras().getString("longitude"));

                            initListener();
                            myLocation = new LatLng(Double.parseDouble(getIntent().getExtras().getString("latitude")), Double.parseDouble(getIntent().getExtras().getString("longitude")));
                            addChooseMarker();
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
                        }
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    LogUtil.e("Test","异常:"+e);
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case 0:
//                if (data != null) {
//                    begintime = data.getExtras().getString("starttime");
//                    endtime = data.getExtras().getString("endtime");
//
//                    tv_day.setText("日期范围："+begintime+" 到 "+endtime);
//
//                    LogUtil.e("onActivityResult===",begintime+"==="+endtime);
//
//                    String codenum = codeNumEdit.getText().toString().trim();
//                    if (codenum == null || "".equals(codenum)){
//                        Toast.makeText(context,"请输入车辆编号",Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    gpstrack(codenum);
//                }
//                break;
//
//            default:
//                break;
//        }
//    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setupLocationStyle();
    }

    /**
     * 设置自定义定位蓝点
     */
    private void setupLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.bike_icon));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    private void addChooseMarker() {
        // 加入自定义标签
        MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation).icon(successDescripter);
        centerMarker = aMap.addMarker(centerMarkerOption);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CameraUpdate update = CameraUpdateFactory.zoomTo(15);
                aMap.animateCamera(update, 1000, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        }, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
