package com.qimalocl.manage.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.http.OkHttpClientManager;
import com.http.ResultCallback;
import com.http.rdata.RGetUserTradeStatus;
import com.http.rdata.RRent;
import com.http.rdata.RetData;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.KeyBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.utils.Globals;
import com.qimalocl.manage.utils.ToastUtil;
import com.qimalocl.manage.utils.UIHelper;
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
import com.sofi.blelocker.library.protocol.ITemporaryActionResponse;
import com.sofi.blelocker.library.search.SearchRequest;
import com.sofi.blelocker.library.search.SearchResult;
import com.sofi.blelocker.library.search.response.SearchResponse;
import com.sofi.blelocker.library.utils.BluetoothLog;
import com.sofi.blelocker.library.utils.StringUtils;

import org.apache.http.Header;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

/**
 * Created by heyong on 2017/5/19.
 */

public class DeviceDetailAlertActivity extends Activity implements View.OnClickListener{
    private static final String TAG = DeviceDetailAlertActivity.class.getSimpleName();

    Context context;

    //    @BindView(R.id.layLock)
    RelativeLayout layLock;
    //    @BindView(R.id.tvName)
    TextView tvName;
    TextView tvAddress;
    //    @BindView(R.id.tvState)
    TextView tvState;
    TextView tvBattery;
    //    @BindView(R.id.tvOpen)
    TextView tvOpen;
//    @BindView(R.id.tvLog)
//    TextView tvLog;


//    @BindView(R.id.bt_wx)
//    TextView bt_wx;
//    @BindView(R.id.mainUI_title_backBtn)
//    ImageView backBtn;
    @BindView(R.id.mainUI_title_titleText)
    TextView titleText;

    Button btnQueryState;
    Button temporaryAction;

    private String mac, name, battery;
    private boolean mConnected = false;
    private String version = "";   //硬件版本号

    private Dialog loadingDialog;

    private String keySource = "";
    //密钥索引
    int encryptionKey= 0;
    //开锁密钥
    String keys = null;
    //服务器时间戳，精确到秒，用于锁同步时间
    long serverTime;

    String type = "5";
    private boolean isStop = false;

    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 修改状态栏颜色，4.4+生效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.ac_ui_device_detail_alert);
        ButterKnife.bind(this);

        context = this;

        type = SharedPreferencesUrls.getInstance().getString("type", "");

        Log.e("DD===onCreate", "==="+type);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

//        loadingDialog = DialogUtils.getLoadingDialog(this, getString(R.string.loading));
//        loadingDialog.show();

        layLock = findViewById(R.id.layLock);
        tvName = findViewById(R.id.tv_name);
        tvAddress = findViewById(R.id.tv_address);
        tvState = findViewById(R.id.tv_status);
        tvBattery = findViewById(R.id.tv_battery);
        tvOpen = findViewById(R.id.tvOpen);
        btnQueryState = findViewById(R.id.btnQueryState);

        TextView tvState2 = findViewById(R.id.tvState);
        tvState2.setVisibility(View.GONE);

        layLock.setOnClickListener(this);
        btnQueryState.setOnClickListener(this);

        titleText.setText("锁的信息");

        bindData();
//        bindView();

//        setDisplayNone();

//        if(getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        }
//        setTitle("大门锁");
//        btWx.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }

    //@Override
    protected void bindData() {
        if (getIntent() != null) {
            mac = getIntent().getStringExtra("mac");
            name = StringUtils.getBikeName(getIntent().getStringExtra("name"));
            int n = getIntent().getStringExtra("name").length();
            battery = getIntent().getStringExtra("name").substring(n-3, n);

            tvAddress.setText("MAC：" + mac);
            tvName.setText("Name：" + name);
//            tvStatus.setText(getText(R.string.connect_status) + "Disconnect");
//            tvCz.setText(R.string.current_cz);

//            tvVersion.setText(R.string.device_version);
        }




        Log.e("bindData===", name+"==="+mac+"==="+getIntent().getStringExtra("name"));

//        mac = "A4:34:F1:7B:BF:A9";
//        mac = "A4:34:F1:7B:BF:9A";
//        name = "GpDTxe7<a";


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
            finish();
        }
        //蓝牙锁
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
        } else {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }

            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.setTitle("正在唤醒车锁");
                loadingDialog.show();
            }


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ClientManager.getClient().stopSearch();
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().unregisterConnectStatusListener(mac, mConnectStatusListener);

                                SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
                                        .searchBluetoothLeDevice(0)
                                        .build();

                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    Log.e("usecar===1", "===");

                                    return;
                                }

                                Log.e("usecar===2", "===");

//                                                                ClientManager.getClient().stopSearch();
                                m_myHandler.sendEmptyMessage(0x98);
                                ClientManager.getClient().search(request, mSearchResponse);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

//        m_myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                connectDevice();
//
//                ClientManager.getClient().registerConnectStatusListener(mac, mConnectStatusListener);
//                ClientManager.getClient().notifyClose(mac, mCloseListener); //监听锁关闭事件
//
//            }
//        }, 2 * 1000);


        OkHttpClientManager.getInstance().GetUserTradeStatus(new ResultCallback<RGetUserTradeStatus>() {
            @Override
            public void onResponse(final RGetUserTradeStatus rGetUserTradeStatus) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (rGetUserTradeStatus.getResult() < 0) {
                            UIHelper.showToast(DeviceDetailAlertActivity.this, ""+rGetUserTradeStatus.getResult());
                        } else {
                            RGetUserTradeStatus.ResultBean resultBean = rGetUserTradeStatus.getInfo();

                            Globals.bType = resultBean.getUserTradeStatus();
                            Globals.bikeNo = resultBean.getBikeNo();
                            if (Globals.bType == 1) {
                                tvOpen.setText("已开锁");
//                                returnCar();
                            } else {
                                tvOpen.setText("开锁");
//                                rentCar();
                            }
                        }
                    }
                });

            }

            @Override
            public void onError(Request request, final Exception e) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        UIHelper.showToast(DeviceDetailAlertActivity.this, e.getMessage());
                    }
                });

            }
        });
    }

//    @OnClick(R.id.mainUI_title_backBtn)
//    void back() {
//        finish();
//    }
//
//    @OnClick(R.id.bt_wx)
//    void bt_wx() {
//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
////        String bikeName = edbikeNum.getText().toString().trim();
//        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
//            com.qimalocl.manage.core.common.UIHelper.goToAct(context,LoginActivity.class);
//            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//        }else {
////            if (bikeName == null || "".equals(bikeName)){
////                ToastUtils.showMessage("请输入车编号");
////                return;
////            }
//            if (Build.VERSION.SDK_INT >= 23) {
//                int checkPermission = checkSelfPermission(Manifest.permission.CAMERA);
//                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//                        requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
//                    } else {
//                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                        customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
//                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                    }
//                                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                                requestPermissions(new String[] { Manifest.permission.CAMERA },100);
//                            }
//                        });
//                        customBuilder.create().show();
//                    }
//                    return;
//                }
//            }
//            try {
//                Intent intent = new Intent();
//                intent.setClass(context, AddCarCaptureAct.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("isChangeKey",false);
//                startActivityForResult(intent, 1);
//
//            } catch (Exception e) {
//                com.qimalocl.manage.core.common.UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
//            }
//        }
//    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (requestCode) {
                    case 1:
                        if (resultCode == RESULT_OK) {
                            String result = data.getStringExtra("QR_CODE");
//                    codenum = edbikeNum.getText().toString().trim();
                            addCar(result);
                        } else {
                            Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;
                }
            }
        });

    }

    protected void rent(){
        Log.e("rent===000",mac+"==="+name+"==="+keySource);

        RequestParams params = new RequestParams();
        params.put("lock_no", name);
        params.put("macinfo", mac);
        params.put("keySource",keySource);
        HttpHelper.get(this, Urls.rent, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                            loadingDialog.setTitle("正在提交");
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
                        com.qimalocl.manage.core.common.UIHelper.ToastError(context, throwable.toString());
                    }
                });


            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("rent===","==="+responseString);
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                            if (result.getFlag().equals("Success")) {
                                KeyBean bean = JSON.parseObject(result.getData(), KeyBean.class);

                                encryptionKey = bean.getEncryptionKey();
                                keys = bean.getKeys();
                                serverTime = bean.getServerTime();

                                Log.e("rent===", mac+"==="+encryptionKey+"==="+keys);

                                openBleLock(null);
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
        });
    }

    private void addCar(String result){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            com.qimalocl.manage.core.common.UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("tokencode",result);    //二维码链接地址
            params.put("codenum",result.split("=")[1]);     //车辆编号
            params.put("macinfo",mac);    //mac地址
            params.put("lock_no", name);    //
            params.put("type", type);    //泺平新锁

            Log.e("addCar===", uid+"==="+access_token+"==="+result+"==="+result.split("=")[1]+"==="+mac+"==="+name+"==="+type);

            HttpHelper.post(context, Urls.addCar, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (loadingDialog != null && !loadingDialog.isShowing()) {
                                loadingDialog.setTitle("正在提交");
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
                            com.qimalocl.manage.core.common.UIHelper.ToastError(context, throwable.toString());
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
                                if (result.getFlag().equals("Success")) {
                                    Toast.makeText(context,"恭喜您，入库成功",Toast.LENGTH_SHORT).show();
                                    //修改密钥
                                }else {
                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                                }

                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("addCar===eee", "==="+e);
                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            }
                        }
                    });



                }
            });
        }
    }



//    @Override
//    protected void bindView() {
//        super.bindView();
//        setDisplayNone();
//        setTitle("大门锁");
//    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.mainUI_title_backBtn:
////                closeBle();
//
//                scrollToFinishActivity();
//                break;
//            default:
//                break;
//        }
//    }



    //    @OnClick({R.id.layLock})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layLock :
                UIHelper.showProgress(this, "获取锁状态中");

                Log.e("layLock===0", "==="+mac);

//                ClientManager.getClient().

                ClientManager.getClient().getStatus(mac, new IGetStatusResponse() {
                    @Override
                    public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
                        keySource = keySerial;

                        Log.e("layLock===1", keySource+"==="+macKey);

                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                UIHelper.dismiss();
                                rent();
                            }
                        });
                    }

                    @Override
                    public void onResponseFail(final int code) {

                        Log.e("layLock===2", "==="+code);

                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                getBleRecord();

                                Log.e(TAG, Code.toString(code));
                                UIHelper.dismiss();
                                UIHelper.showToast(DeviceDetailAlertActivity.this, Code.toString(code));
                            }
                        });
                    }

                });
                break;

            case R.id.btnQueryState:
                queryOpenState();
                break;
//            case R.id.temporaryAction:
//                temporaryAction();
//                break;
//            case R.id.btnScan:
//                ClientManager.getClient().disconnect(mac);
//                break;
//            case R.id.btnBuzzer :
//                buzzer();
//                break;
//            case R.id.btnQueryOpenState :
//                queryOpenState();
//                break;
//            case R.id.btnQueryRunState :
//                queryRunState();
//                break;
//            case R.id.btnGetConfig :
//                getGprsConfig();
//                break;
//            case R.id.btnGetRecord :
//                getBleRecord();
//                break;
//            case R.id.btnLowPacketEnable :
//                enableLowPower();
//                break;
//            case R.id.btnLowPacketDisable :
//                disableLowPower();
//                break;
//            case R.id.btnUpdatekey :
//                requestKeyServer(name);
//                break;
//            case R.id.btnExitFactory :
//                exitFactory();
//                break;
        }
    }

    private void connectDeviceLP() {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(1)
                .setServiceDiscoverTimeout(10000)
                .setEnableNotifyRetry(1)
                .setEnableNotifyTimeout(10000)
                .build();

        ClientManager.getClient().connect(mac, options, new IConnectResponse() {
            @Override
            public void onResponseFail(int code) {
                isStop = false;

                Log.e("connectDevice===", "Fail==="+Code.toString(code));
//                ToastUtil.showMessageApp(context, Code.toString(code));
            }

            @Override
            public void onResponseSuccess(BleGattProfile profile) {
//                BluetoothLog.v(String.format("profile:\n%s", profile));
//                refreshData(true);

                isStop = true;

                Log.e("connectDevice===", "Success==="+profile);

//                if (Globals.bType == 1) {
//                    ToastUtil.showMessageApp(context, "正在关锁中");
////                    getBleRecord();
//                }
            }
        });
    }


    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    break;
                case 0x98://搜索超时
//                    BaseApplication.getInstance().getIBLE().connect(m_nowMac, ActivityScanerCode.this);

                    connectDeviceLP();
                    ClientManager.getClient().registerConnectStatusListener(mac, mConnectStatusListener);


                    Log.e("0x98===", "==="+isStop);

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isStop){
                                if (loadingDialog != null && loadingDialog.isShowing()) {
                                    loadingDialog.dismiss();
                                }

//                                Toast.makeText(context,"请重启软件，开启定位服务,输编号用车",5 * 1000).show();
                                Toast.makeText(context,"扫码唤醒失败，重启手机蓝牙换辆车试试吧！",Toast.LENGTH_LONG).show();

                                ClientManager.getClient().stopSearch();
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().disconnect(mac);
                                ClientManager.getClient().unregisterConnectStatusListener(mac, mConnectStatusListener);

                                finish();
                            }
                        }
                    }, 15 * 1000);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void refreshData(boolean refresh) {
        if (refresh) {
            tvState.setText("连接状态：蓝牙已连接");
//            tvName.setText(Globals.BLE_NAME);
            layLock.setClickable(true);


            ClientManager.getClient().getStatus(mac, new IGetStatusResponse() {
                @Override
                public void onResponseSuccess(String version, String keySerial, String macKey, final String vol) {
                    keySource = keySerial;

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvBattery.setText("当前电压：" + vol + "V");
                        }
                    });
                }

                @Override
                public void onResponseFail(final int code) {

                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, Code.toString(code));
                            UIHelper.dismiss();
                            UIHelper.showToast(DeviceDetailAlertActivity.this, Code.toString(code));
                        }
                    });
                }

            });

        } else {
            tvState.setText("连接状态：开始蓝牙扫描");
//            tvName.setText("");
            layLock.setClickable(false);
        }
    }

    //连接设备
    private void connectDevice() {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(1)
                .setServiceDiscoverTimeout(10000)
                .setEnableNotifyRetry(1)
                .setEnableNotifyTimeout(10000)
                .build();

        ClientManager.getClient().connect(mac, options, new IConnectResponse() {
            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, Code.toString(code));
                        UIHelper.showToast(DeviceDetailAlertActivity.this, Code.toString(code));
                    }
                });

            }

            @Override
            public void onResponseSuccess(BleGattProfile profile) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        BluetoothLog.v(String.format("profile:\n%s", profile));
                        refreshData(true);

                        if (Globals.bType == 1) {
                            UIHelper.showProgress(DeviceDetailAlertActivity.this, "正在关锁中");
                            getBleRecord();
                        }
                    }
                });

            }
        });
    }

    private void connectDeviceIfNeeded() {
        if (!mConnected) {
            connectDevice();
        } else {
            ClientManager.getClient().stopSearch();
        }
    }

    //扫描结果
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            Log.e("===","DeviceDetailActivity.onSearchStarted");
        }

        @Override
        public void onDeviceFounded(final SearchResult device) {
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("onDeviceFounded===",mac + "=== " + device.device.getAddress());
                    if (device.getAddress().contains(mac)) {
                        ClientManager.getClient().stopSearch();

                        connectDeviceLP();

                        ClientManager.getClient().registerConnectStatusListener(mac, mConnectStatusListener);

//                        connectDeviceIfNeeded();
                    }
                }
            });

        }

        @Override
        public void onSearchStopped() {
            Log.e("===","DeviceDetailActivity.onSearchStopped");

        }

        @Override
        public void onSearchCanceled() {
            Log.e("===","DeviceDetailActivity.onSearchCanceled");

        }
    };

    //扫描，0是一直扫描
    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(0).build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ClientManager.getClient().search(request, mSearchResponse);
    }

    //租车，开启扫描
    private void rentCar() {
        connectDeviceIfNeeded();
    }

    //还车，支持长连接不断开，不支持断开扫描
    private void returnCar() {
        if(Globals.isBleConnected && StringUtils.isNotEmpty(version) && StringUtils.supportLongConnected(version)) {

        }
        else {
            ClientManager.getClient().disconnect(mac);
            searchDevice();
        }
    }

    //获取服务器的加密信息
    private void queryStatusServer(String version, String keySerial, String macKey, String vol) {
        Log.e(TAG, "version:" + version + " keySerial:" + keySerial + " macKey:" + macKey + " vol:" + vol);
        this.version = version;
        int timestamp = (int) StringUtils.getCurrentTimestamp();

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                UIHelper.showProgress(context, "get_bike_server");
            }
        });
        OkHttpClientManager.getInstance().Rent(macKey, keySerial, timestamp, new ResultCallback<RRent>() {

            @Override
            public void onResponse(final RRent rRent) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        UIHelper.dismiss();
                        if (rRent.getResult() >= 0) {
                            RRent.ResultBean resultBean = rRent.getInfo();
                            openBleLock(resultBean);
                        }
                        else {
                            UIHelper.showToast(DeviceDetailAlertActivity.this, ""+rRent.getResult());
                        }
                    }
                });

            }

            @Override
            public void onError(Request request, final Exception e) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        UIHelper.dismiss();
                        UIHelper.showToast(DeviceDetailAlertActivity.this, e.getMessage());
                    }
                });

            }

        });
    }

    //与设备，开锁
    private void openBleLock(RRent.ResultBean resultBean) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                UIHelper.showProgress(context, "open_bike_status");
            }
        });

//        ClientManager.getClient().openLock(mac, "18112348925", resultBean.getServerTime(),

        Log.e("scan===openBleLock", serverTime+"==="+keys+"==="+encryptionKey);

        ClientManager.getClient().openLock(mac,"000000000000", (int) serverTime, keys, encryptionKey, new IEmptyResponse(){
//        ClientManager.getClient().openLock(mac,"000000000000", resultBean.getServerTime(), resultBean.getKeys(), resultBean.getEncryptionKey(), new IEmptyResponse(){
                    @Override
                    public void onResponseFail(final int code) {
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("openLock===Fail", code+"==="+Code.toString(code));
                                UIHelper.dismiss();
                                UIHelper.showToast(DeviceDetailAlertActivity.this, Code.toString(code));
                                getBleRecord();
                            }
                        });

                    }

                    @Override
                    public void onResponseSuccess() {
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("openLock===Success", "===");
                                UIHelper.dismiss();
                                getBleRecord();

//                                ClientManager.getClient().unregisterConnectStatusListener(mac, mConnectStatusListener);
                            }
                        });

                    }
                });
    }

    //与设备，临时停车
    private void temporaryAction() {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                UIHelper.showProgress(context, "temporaryAction");
            }
        });

//        ClientManager.getClient().temporaryAction(mac, "18112348925", new ITemporaryActionResponse() {
        ClientManager.getClient().temporaryAction(mac, "000000000000", new ITemporaryActionResponse() {
            @Override
            public void onResponseSuccess() {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        UIHelper.dismiss();
                        UIHelper.showToast(DeviceDetailAlertActivity.this, "临时停车成功");
                    }
                });


            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, Code.toString(code));
                        UIHelper.dismiss();
                        UIHelper.showToast(DeviceDetailAlertActivity.this, Code.toString(code));
                    }
                });

            }
        });
    }

    //与设备，获取记录
    private void getBleRecord() {
//        m_myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                UIHelper.showProgress(context, "get_bike_record");
//            }
//        });

        ClientManager.getClient().getRecord(mac, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, String transType, String mackey, String index, final int Major, final int Minor, String vol) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===Success", "===");
//                        UIHelper.dismiss();
//                        uploadRecordServer(phone, bikeTradeNo, timestamp, transType, mackey, index, cap, vol);
                        deleteBleRecord(bikeTradeNo);
                    }
                });

            }

            @Override
            public void onResponseSuccessEmpty() {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===SuccessE", "===");
//                        UIHelper.dismiss();
                        UIHelper.showToast(DeviceDetailAlertActivity.this, "record empty");
                    }
                });

            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===Fail", Code.toString(code));
//                        UIHelper.dismiss();
                        UIHelper.showToast(DeviceDetailAlertActivity.this, Code.toString(code));
                    }
                });

            }
        });
    }

    //与服务器，上传记录
    private String tradeNo = "";
    private void uploadRecordServer(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, String cap, String vol) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                UIHelper.showProgress(context, "upload_record_server");
            }
        });

        tradeNo = bikeTradeNo;
//        OkHttpClientManager.getInstance().BikeTradeRecord(phone, StringUtils.decodeTradeNo(bikeTradeNo),
//                timestamp, transType, mackey, index, cap, vol, "", "", new ResultCallback<RetData>() {
//                    @Override
//                    public void onResponse(final RetData retData) {
//                        m_myHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                UIHelper.dismiss();
//                                if (retData.getResult() >= 0) {
//                                    deleteBleRecord();
//                                }
//                                else {
//                                    UIHelper.showToast(DeviceDetailAlertActivity.this, ""+retData.getResult());
//                                }
//                            }
//                        });
//
//                    }
//
//                    @Override
//                    public void onError(Request request, final Exception e) {
//                        m_myHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                UIHelper.dismiss();
//                                UIHelper.showToast(DeviceDetailAlertActivity.this, e.getMessage());
//                            }
//                        });
//
//                    }
//                });
    }

    //与设备，删除记录
    private void deleteBleRecord(String tradeNo) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                UIHelper.showProgress(context, "delete_bike_record");
            }
        });

        ClientManager.getClient().deleteRecord(mac, tradeNo, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, String transType, String mackey, String index, final int Major, final int Minor, String vol) {

                Log.e("deleteBleRecord=Success", "===");

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        UIHelper.dismiss();
//                        uploadRecordServer(phone, bikeTradeNo, timestamp, transType, mackey, index, cap, vol);
                        deleteBleRecord(bikeTradeNo);
                    }
                });
            }

            @Override
            public void onResponseSuccessEmpty() {
                Log.e("deleteBleRecord=SucE", "===");

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        UIHelper.dismiss();

                        if(Globals.bType == 1) {
                            Globals.bType = 0;
                            tvOpen.setText("开锁");
//                            rentCar();
                        }
                        else {
                            Globals.bType = 1;
                            tvOpen.setText("已开锁");
//                            returnCar();
                        }
                    }
                });

            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("deleteBleRecord===Fail", Code.toString(code));
                        UIHelper.dismiss();
                        UIHelper.showToast(DeviceDetailAlertActivity.this, Code.toString(code));
                    }
                });


            }
        });
    }

    //    //与设备，蜂鸣器响
//    private void buzzer() {
//        UIHelper.showProgress(this, buzzer);
//        ClientManager.getClient().buzzer(mac, new IEmptyResponse(){
//                    @Override
//                    public void onResponseFail(int code) {
//                        LOG.E(TAG, Code.toString(code));
//                        UIHelper.dismiss();
//                        UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//                    }
//
//                    @Override
//                    public void onResponseSuccess() {
//                        UIHelper.dismiss();
//                        appendLog(getString(R.string.buzzer) + getString(R.string.success));
//                    }
//                });
//    }
//
    //与设备，校准锁开关状态
    private void queryOpenState() {
        UIHelper.showProgress(this, "collectState");
        ClientManager.getClient().queryOpenState(mac, new IQueryOpenStateResponse() {
            @Override
            public void onResponseSuccess(final boolean open) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        UIHelper.dismiss();

                        Log.e("queryOpenState===", "===="+open);
                    }
                });


            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, Code.toString(code));
                        UIHelper.dismiss();
                        UIHelper.showToast(DeviceDetailAlertActivity.this, Code.toString(code));
                    }
                });

            }
        });
    }
//
//    //与设备，获取锁运行状态
//    private void queryRunState() {
//        UIHelper.showProgress(this, R.string.runState);
//        ClientManager.getClient().queryRunState(mac, new IQueryRunStateResponse() {
//            @Override
//            public void onResponseSuccess(String time, String vol, boolean elec, boolean buzzer, int recordNum) {
//                LOG.E(TAG, "time:" + time + " vol:" + vol + " elec:" + elec + " buzzer:" + buzzer +
//                        " recordNum:" + recordNum);
//                UIHelper.dismiss();
//                RunState runState = new RunState(time, vol, elec, buzzer, recordNum);
//                appendLog(runState.toString());
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//            }
//        });
//    }
//
//    //与设备，获取锁GPRS配置
//    private void getGprsConfig() {
//        UIHelper.showProgress(this, R.string.getGprsConfig);
//        ClientManager.getClient().getGprsConfig(mac, new IGetGprsConfigResponse() {
//            @Override
//            public void onResponseSuccess(String ip, String path, String port, String apn, String ping,
//                                          String gps) {
//                LOG.E(TAG, "ip:" + ip + " path:" + path + " port:" + port + " apn:" + apn +
//                        " ping:" + ping + " gps:" + gps);
//                UIHelper.dismiss();
//                GprsConfig gprsConfig = new GprsConfig(ip, path, port, apn, ping, gps);
//                appendLog(gprsConfig.toString());
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//            }
//        });
//    }
//
//    //与设备，启用低功耗模式
//    private void enableLowPower() {
//        UIHelper.showProgress(this, R.string.enableLowPower);
//        ClientManager.getClient().lowPower(mac, true, new IEmptyResponse() {
//            @Override
//            public void onResponseSuccess() {
//                UIHelper.dismiss();
//                appendLog(getString(R.string.enableLowPower) + getString(R.string.success));
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//            }
//        });
//    }
//
//    //与设备，禁用低功耗模式
//    private void disableLowPower() {
//        UIHelper.showProgress(this, R.string.disableLowPower);
//        ClientManager.getClient().lowPower(mac, false, new IEmptyResponse() {
//            @Override
//            public void onResponseSuccess() {
//                UIHelper.dismiss();
//                appendLog(getString(R.string.disableLowPower) + getString(R.string.success));
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//            }
//        });
//    }
//
//    //与服务器，请求更新秘钥
//    private String lockNo = "";
//    private void requestKeyServer(String lockno) {
//        UIHelper.showProgress(this, R.string.request_key_server);
//        lockNo = lockno;
//        OkHttpClientManager.getInstance().CourtRequestUpdate(lockno, new ResultCallback<RCourtCommitUpdate>() {
//                    @Override
//                    public void onResponse(RCourtCommitUpdate rCourtCommitUpdate) {
//                        UIHelper.dismiss();
//                        if (rCourtCommitUpdate.getResult() >= 0) {
//                            RCourtCommitUpdate.InfoBean infoBean = rCourtCommitUpdate.getInfo();
//                            updateKey(infoBean.getNewkey(), infoBean.getOldkey(), infoBean.getIndex());
//                        }
//                        else {
//                            UIHelper.showToast(DeviceDetailActivity.this, Utils.toString(rCourtCommitUpdate.getResult()));
//                        }
//                    }
//
//                    @Override
//                    public void onError(Request request, Exception e) {
//                        UIHelper.dismiss();
//                        UIHelper.showToast(DeviceDetailActivity.this, e.getMessage());
//                    }
//                });
//    }
//
//    //与服务器，确认更新秘钥
//    private void commitKeyServer(String lockno, boolean success) {
//        UIHelper.showProgress(this, R.string.commit_key_server);
//        OkHttpClientManager.getInstance().CourtCommitUpdate(lockno, success, new ResultCallback<RetData>() {
//            @Override
//            public void onResponse(RetData retData) {
//                UIHelper.dismiss();
//                if (retData.getResult() >= 0) {
//                    appendLog(getString(R.string.updateKey) + getString(R.string.success));
//                }
//                else {
//                    UIHelper.showToast(DeviceDetailActivity.this, Utils.toString(retData.getResult()));
//                }
//            }
//
//            @Override
//            public void onError(Request request, Exception e) {
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, e.getMessage());
//            }
//        });
//    }
//
//    //与设备，更新秘钥
//    private void updateKey(String newKey, String oldKey, int indexKey) {
//        UIHelper.showProgress(this, R.string.updateKey);
//        ClientManager.getClient().updateKey(mac, newKey, oldKey, indexKey, new IEmptyResponse() {
//            @Override
//            public void onResponseSuccess() {
//                UIHelper.dismiss();
//                commitKeyServer(lockNo, true);
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//                commitKeyServer(lockNo, false);
//            }
//        });
//    }
//
//    //与设备，退出工厂模式
//    private void exitFactory() {
//        UIHelper.showProgress(this, R.string.exitFactory);
//        ClientManager.getClient().exitFactory(mac, new IEmptyResponse() {
//            @Override
//            public void onResponseSuccess() {
//                UIHelper.dismiss();
//                appendLog(getString(R.string.exitFactory) + getString(R.string.success));
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//            }
//        });
//    }
//
//    private void appendLog(final String str) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                String text = tvLog.getText().toString();
//                tvLog.setText(text +"\n"+str);
//
//                int offset = tvLog.getLineCount() * tvLog.getLineHeight();
//                if(offset >tvLog.getHeight()) {
//                    tvLog.scrollTo(0, offset - tvLog.getHeight());
//                }
//            }
//        });
//    }

//    R.id.btnScan, R.id.btnBuzzer, R.id.btnQueryOpenState, R.id.btnQueryRunState,
//    R.id.btnGetConfig, R.id.btnGetRecord, R.id.btnLowPacketEnable, R.id.btnLowPacketDisable,
//    R.id.btnUpdatekey, R.id.btnExitFactory





    //监听锁关闭事件
    private final ICloseListener mCloseListener = new ICloseListener() {
        @Override
        public void onNotifyClose() {
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("onNotifyClose===", "====");

//                    BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onNotifyClose"));
                    tvOpen.setText("开锁");
                    getBleRecord();
                }
            });

        }
    };

    //监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(final String mac, final int status) {
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
//                    BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onConnectStatusChanged %d in %s", status, Thread.currentThread().getName()));

                    Log.e("ConnectStatus===", mac+"===="+(status == STATUS_CONNECTED));


                    if(status != STATUS_CONNECTED){
                        return;
                    }

                    ClientManager.getClient().stopSearch();

                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }

                    refreshData(true);

//                    Globals.isBleConnected = mConnected = (status == STATUS_CONNECTED);
//                    refreshData(mConnected);
//                    connectDeviceIfNeeded();
                }
            });

        }
    };

    @Override
    protected void onDestroy() {

        Log.e("onDestroy===", "==="+mac);

        ClientManager.getClient().stopSearch();
        ClientManager.getClient().disconnect(mac);
        ClientManager.getClient().disconnect(mac);
        ClientManager.getClient().disconnect(mac);
        ClientManager.getClient().disconnect(mac);
        ClientManager.getClient().disconnect(mac);
        ClientManager.getClient().disconnect(mac);
        ClientManager.getClient().unnotifyClose(mac, mCloseListener);
        ClientManager.getClient().unregisterConnectStatusListener(mac, mConnectStatusListener);
        super.onDestroy();
    }

}