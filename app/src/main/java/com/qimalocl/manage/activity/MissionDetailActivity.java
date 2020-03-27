package com.qimalocl.manage.activity;

import android.Manifest;
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
import android.view.View;
import android.widget.ImageView;
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
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.BadCarBean;
import com.qimalocl.manage.model.GlobalConfig;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/10/18 0018.
 */

public class MissionDetailActivity extends SwipeBackActivity implements View.OnClickListener {

    @BindView(R.id.mainUI_title_backBtn)
    ImageView backBtn;
    @BindView(R.id.mainUI_title_titleText)
    TextView titleText;
    @BindView(R.id.ui_merchantAddress_map)
    MapView mapView;

    @BindView(R.id.num)
    TextView tvNum;
    @BindView(R.id.tel)
    TextView tvTel;
    @BindView(R.id.time)
    TextView tvTime;

    private Context context;
//    private LoadingDialog loadingDialog;

    private double latitude;
    private double longitude;
    private String codenum;
    private String telphone;
    private String time;
    private AMap aMap;
    private static final int STROKE_COLOR = Color.argb(80, 3, 0, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private LatLng myLocation = null;
    private Marker centerMarker;
    private BitmapDescriptor successDescripter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_mission_detail);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        context = this;


        codenum =getIntent().getExtras().getString("codenum");
        latitude = Double.parseDouble(getIntent().getExtras().getString("latitude"));
        longitude = Double.parseDouble(getIntent().getExtras().getString("longitude"));
        telphone =getIntent().getExtras().getString("telphone");
        time =getIntent().getExtras().getString("time");

        init();


        tvNum.setText(codenum);
        tvTel.setText(telphone);
        tvTime.setText(time.substring(0,16));

        myLocation = new LatLng(latitude, longitude);
        addChooseMarker();
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12));

        tvTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Test","111111111");
//                                linkTel = bean.getTelphone();
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkPermission = context.checkSelfPermission(Manifest.permission.CALL_PHONE);
                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                            requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 1);
                        } else {
                            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                            customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开拨打电话权限！")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    MissionDetailActivity.this.requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 1);
                                }
                            });
                            customBuilder.create().show();
                        }
                        return;
                    }
                }
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                customBuilder.setTitle("温馨提示").setMessage("确认拨打" + telphone + "吗?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + telphone));
                        startActivity(intent);
                    }
                });
                customBuilder.create().show();
            }
        });

//        initHttp();
    }

    private void init(){

        titleText.setText("任务");
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(15);// 设置缩放监听
        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.icon_usecarnow_position_succeed);
        aMap.moveCamera(cameraUpdate);

        initListener();

    }


    private void initHttp() {
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
        params.put("codenum", codenum);

        HttpHelper.get(context, Urls.badcarShow, params, new TextHttpResponseHandler() {
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
//                        JSONArray array = new JSONArray(result.getData());

                        JSONObject obj = new JSONObject(result.getData());

//                        {"data":{"longitude":"117.147399","latitude":"34.218477","codenum":"50007307","telphone":"15105203536","lastusetime":"2018-12-26 07:59:22"}}

                        Log.e("initHttp===", "==="+obj.getString("longitude"));

                        longitude = Double.parseDouble(obj.getString("longitude"));
                        latitude = Double.parseDouble(obj.getString("latitude"));



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

    private void initListener(){

        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            default:
                break;
        }
    }
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
