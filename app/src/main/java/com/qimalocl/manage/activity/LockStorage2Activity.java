package com.qimalocl.manage.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.fitsleep.sunshinelibrary.utils.DialogUtils;
import com.fitsleep.sunshinelibrary.utils.Logger;
import com.fitsleep.sunshinelibrary.utils.ToastUtils;
import com.fitsleep.sunshinelibrary.utils.ToolsUtils;
import com.fitsleep.sunshinelibrary.utils.UtilSharedPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseApplication;
import com.qimalocl.manage.base.MPermissionsActivity;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.ClearEditText;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.model.BleDevice;
import com.qimalocl.manage.model.ResultConsel;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.mode.Order;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.zxing.lib.scaner.activity.ActivityScanerCode;
import com.zxing.lib.scaner.activity.AddCarCaptureAct;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//未改密钥密码
@SuppressLint("NewApi")
public class LockStorage2Activity extends MPermissionsActivity implements OnConnectionListener, OnDeviceSearchListener {
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    @BindView(R.id.tv_cz)
    TextView tvCz;
    @BindView(R.id.open_count)
    TextView tvCount;
    @BindView(R.id.bt_open)
    Button btOpen;
    @BindView(R.id.bt_auto)
    CheckBox btAuto;
    @BindView(R.id.bt_close)
    Button btClose;
    @BindView(R.id.app_version)
    TextView appVersion;

    @BindView(R.id.rb_mt)
    RadioButton rbMt;
    @BindView(R.id.rb_yx)
    RadioButton rbYx;
    @BindView(R.id.rg_type)
    RadioGroup rgType;
    @BindView(R.id.mainUI_title_backBtn)
    ImageView backBtn;
    @BindView(R.id.mainUI_title_titleText)
    TextView titleText;
    @BindView(R.id.bt_status)
    Button btStatus;
    @BindView(R.id.bt_wx)
    Button btWx;
    @BindView(R.id.change_key_btn)
    Button changeKeyBtn;
    @BindView(R.id.change_psd_btn)
    Button changePsdBtn;
    @BindView(R.id.edbikeNum)
    ClearEditText edbikeNum;

    private int count = 0;
    private String name;
    private String address;
    private boolean isAuto;
    private Dialog loadingDialog;
    private Dialog loadingDialog2;
    public static final int QR_SCAN_REQUEST_CODE = 3638;

    private List<BleDevice> bleDeviceList = new ArrayList<>();
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    private BleDevice bleDevice;

    private boolean isStop = false;

    private Context context = this;
    private String codenum = "";

    private boolean isChangePsd = false;
    private boolean isPwd = false;

    public static byte[] hexStringToByteArray(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        Log.e("StringToByte===1", bytes+"==="+bytes[0]+"==="+bytes[5]);

        Config.key = bytes;

        return bytes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_storage);
        ButterKnife.bind(this);
        registerReceiver(broadcastReceiver, Config.initFilter());
        appVersion.setText("Version:" + ToolsUtils.getVersion(getApplicationContext()));

        btAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isAuto = true;
                    open();
                } else {
                    isAuto = false;
                }
            }
        });
        int lock_type = UtilSharedPreference.getIntValue(getApplicationContext(), "LOCK_TYPE");
        if (lock_type == LockType.YXS.getValue()) {
            rgType.check(R.id.rb_yx);
            GlobalParameterUtils.getInstance().setLockType(LockType.YXS);
        } else {
            rgType.check(R.id.rb_mt);
            GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
        }
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_mt:
                        GlobalParameterUtils.getInstance().setLockType(LockType.MTS);
                        UtilSharedPreference.saveInt(getApplicationContext(), "LOCK_TYPE", LockType.MTS.getValue());
                        BaseApplication.getInstance().getIBLE().disconnect();
                        break;
                    case R.id.rb_yx:
                        GlobalParameterUtils.getInstance().setLockType(LockType.YXS);
                        BaseApplication.getInstance().getIBLE().disconnect();
                        UtilSharedPreference.saveInt(getApplicationContext(), "LOCK_TYPE", LockType.YXS.getValue());
                        break;
                }
            }
        });
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        codenum = getIntent().getStringExtra("codenum");


        hexStringToByteArray(Config.keyMap.get(address.replaceAll(":", "")));

//        BaseApplication.getInstance().getIBLE().setChangKey(false);

        Log.e("LockStorage2Activity===", name+"==="+address+"==="+codenum);

        if (!TextUtils.isEmpty(address)) {
            BaseApplication.getInstance().getIBLE().connect(address, this);
        }
        loadingDialog = DialogUtils.getLoadingDialog(this, getString(R.string.loading));
        loadingDialog.show();

        titleText.setText("锁的信息");

        changeKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改密码
//                if ("1".equals(pwd)) {

                Log.e("ls2a===changeKeyBtn", "===");

                //修改密钥
                loadingDialog = DialogUtils.getLoadingDialog(context, "正在修改密钥");
                loadingDialog.show();

                byte[] bytes2 = {Config.newKey[0],
                        Config.newKey[1], Config.newKey[2], Config.newKey[3], Config.newKey[4],
                        Config.newKey[5], Config.newKey[6], Config.newKey[7]};
                BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY, bytes2);
                m_myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        byte[] bytes1 = {Config.newKey[8],
                                Config.newKey[9], Config.newKey[10], Config.newKey[11], Config.newKey[12],
                                Config.newKey[13], Config.newKey[14], Config.newKey[15]};
                        BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY2, bytes1);

                        BaseApplication.getInstance().getIBLE().setChangKey(true);

//                        BaseApplication.getInstance().getIBLE().disconnect();
//
//                        m_myHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                BaseApplication.getInstance().getIBLE().connect(address, LockStorage2Activity.this);
//                            }
//                        }, 2000);


                    }
                }, 2000);


//                byte[] bytes = {Config.key[0],
//                        Config.key[1], Config.key[2], Config.key[3], Config.key[4],
//                        Config.key[5], Config.key[6], Config.key[7]};
//                BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY, bytes);
//                m_myHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        byte[] bytes1 = {Config.key[8],
//                                Config.key[9], Config.key[10], Config.key[11], Config.key[12],
//                                Config.key[13], Config.key[14], Config.key[15]};
//                        BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY2, bytes1);
//                    }
//                }, 2000);

            }
        });

        changePsdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改密码
//                if ("1".equals(pwd)) {

                Log.e("ls2a===changePsdBtn", "===");

//                isChangePsd = true;

                //修改密码
                loadingDialog = DialogUtils.getLoadingDialog(context, "正在修改密码");
                loadingDialog.show();

                byte[] bytes = {Config.password[0], Config.password[1], Config.password[2], Config.password[3], Config.password[4], Config.password[5]};
                BaseApplication.getInstance().getIBLE().setPassword(Order.TYPE.RESET_PASSWORD, bytes);

                m_myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        byte[] bytes = {Config.passwordnew[0], Config.passwordnew[1],
                                Config.passwordnew[2], Config.passwordnew[3],
                                Config.passwordnew[4], Config.passwordnew[5]};


                        BaseApplication.getInstance().getIBLE().setPassword(Order.TYPE.RESET_PASSWORD2, bytes);
                    }
                }, 2000);



            }
        });
        btWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = SharedPreferencesUrls.getInstance().getString("uid","");
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
                String bikeName = edbikeNum.getText().toString().trim();
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    UIHelper.goToAct(context,LoginActivity.class);
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                }else {
                    if (bikeName == null || "".equals(bikeName)){
                        ToastUtils.showMessage("请输入车编号");
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkPermission = LockStorage2Activity.this.checkSelfPermission(Manifest.permission.CAMERA);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
                            } else {
                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(LockStorage2Activity.this);
                                customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        LockStorage2Activity.this.requestPermissions(
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
                        intent.putExtra("isAdd",true);

                        startActivityForResult(intent, 1);

//                        37===010580b015b14bb200a444f8a765f4a1===http://www.7mate.cn/app.php?randnum=bqk0008804===60008804===CB:12:F0:0C:60:33

//                        addCar("http://www.7mate.cn/app.php?randnum=bqk0008804","60008804");


                    } catch (Exception e) {
                        UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
                    }
                }
            }
        });

    }

    @OnClick(R.id.mainUI_title_backBtn)
    void back() {
        scrollToFinishActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChangePsd = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scanDevice();
            }
        }, 500);
    }

    public void scanDevice() {
        bluetoothDeviceList.clear();
        bleDeviceList.clear();
        requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
    }

    @Override
    public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (!bluetoothDeviceList.contains(device)) {
            bluetoothDeviceList.add(device);
            bleDevice = new BleDevice(device, scanRecord, rssi);
            bleDeviceList.add(bleDevice);
        }
        if (address.equals(device.getAddress())) {
            isStop = true;
            BaseApplication.getInstance().getIBLE().stopScan();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        isStop = true;
        m_myHandler.removeCallbacksAndMessages(null);
        BaseApplication.getInstance().getIBLE().stopScan();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BaseApplication.getInstance().getIBLE().close();
        isStop = true;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String data = intent.getStringExtra("data");
            switch (action) {
                case Config.TOKEN_ACTION:
                    isStop = true;
                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            BaseApplication.getInstance().getIBLE().getBattery();
                        }
                    }, 1000);
                    tvVersion.setText(getText(R.string.device_version) + GlobalParameterUtils.getInstance().getVersion());
                    break;
                case Config.BATTERY_ACTION:
                    if (TextUtils.isEmpty(data)) {
                        tvCz.setText(R.string.battery_fail);
                    } else {
                        tvCz.setText(R.string.battery_success);
                        tvBattery.setText(getText(R.string.battery) + String.valueOf(Integer.parseInt(data, 16)));
                    }
                    break;
                case Config.OPEN_ACTION:
                    if (TextUtils.isEmpty(data)) {
                        tvCz.setText(R.string.open_fail);
                    } else {
                        tvCz.setText(R.string.open_success);
                        count++;
                        tvCount.setText(getText(R.string.open_count) + String.valueOf(count));
                    }
                    break;
                case Config.CLOSE_ACTION:
                    if (TextUtils.isEmpty(data)) {
                        tvCz.setText(R.string.reset_fail);
                    } else {
                        tvCz.setText(R.string.reset_success);
                    }
                    break;
                case Config.LOCK_STATUS_ACTION:
                    if (TextUtils.isEmpty(data)) {
                        tvCz.setText(R.string.closeed);
                    } else {
                        tvCz.setText(R.string.opened);
                    }
                    break;
                case Config.LOCK_RESULT:
                    if (TextUtils.isEmpty(data)) {
                        tvCz.setText(R.string.Lock_success);
                    } else {
                        tvCz.setText(R.string.Lock_success);
                        if (isAuto) {
                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    open();
                                }
                            }, 1000);
                        }
                    }
                    break;
                case Config.KEY_ACTION:

                    Log.e("KEY_ACTION===", "===");

                    if (TextUtils.isEmpty(data)) {
                        Toast.makeText(context, "密钥修改失败", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "密钥修改成功", Toast.LENGTH_LONG).show();

                        changKey();
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                case Config.PASSWORD_ACTION:
                    if (TextUtils.isEmpty(data)) {
                        Toast.makeText(context, "密码修改失败", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isPwd) {
                            Toast.makeText(context, "密码修改成功", Toast.LENGTH_SHORT).show();
                            changPsd();
                        }

                    }
                    if (loadingDialog2 != null && loadingDialog2.isShowing()) {
                        loadingDialog2.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onConnect() {
        Logger.e(getClass().getSimpleName(), "连接成功");
    }

    private void changKey() {
        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
            Toast.makeText(context, "请先登录您的账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
            return;
        }


        codenum = edbikeNum.getText().toString().trim();

        if (codenum  == null || "".equals(codenum)) {
            Toast.makeText(context, "请输入车辆编号", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams();

        Log.e("changKey===", uid+"==="+access_token+"==="+codenum);

        params.put("uid", uid);
        params.put("access_token", access_token);
        params.put("codenum", codenum);



        HttpHelper.post(context, Urls.changeKey, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在提交");
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
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        Toast.makeText(context, "恭喜您，数据提交成功", Toast.LENGTH_SHORT).show();
                        BaseApplication.getInstance().getIBLE().setChangKey(true);

//                        BaseApplication.getInstance().getIBLE().connect(address, LockStorage2Activity.this);

//                        isChangePsd = true;
                        loadingDialog2 = DialogUtils.getLoadingDialog(context, "正在修改密码");
                        loadingDialog2.show();

                        isPwd = false;

                        byte[] bytes = {Config.password[0], Config.password[1],
                                Config.password[2], Config.password[3],
                                Config.password[4], Config.password[5]};
                        BaseApplication.getInstance().getIBLE().setPassword(Order.TYPE.RESET_PASSWORD, bytes);

                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                byte[] bytes = {Config.passwordnew[0], Config.passwordnew[1],
                                        Config.passwordnew[2], Config.passwordnew[3],
                                        Config.passwordnew[4], Config.passwordnew[5]};
//                                byte[] bytes = {Config.password[0], Config.password[1],
//                                        Config.password[2], Config.password[3],
//                                        Config.password[4], Config.password[5]};

                                BaseApplication.getInstance().getIBLE().setPassword(Order.TYPE.RESET_PASSWORD2, bytes);
                            }
                        }, 2000);

                        isPwd = true;

                    } else {
                        Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

            }
        });
    }

    private void changPsd() {
        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
            Toast.makeText(context, "请先登录您的账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
            return;
        }

        codenum = edbikeNum.getText().toString().trim();

        if (codenum  == null || "".equals(codenum)) {
            Toast.makeText(context, "请输入车辆编号", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.e("changPsd===", uid+"==="+access_token+"==="+codenum);

        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        params.put("codenum", codenum);
        params.put("latitude", SharedPreferencesUrls.getInstance().getString("latitude", ""));
        params.put("longitude", SharedPreferencesUrls.getInstance().getString("longitude", ""));
        HttpHelper.post(context, Urls.changePsd, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog2 != null && !loadingDialog2.isShowing()) {
                    loadingDialog2.setTitle("正在提交");
                    loadingDialog2.show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog2 != null && loadingDialog2.isShowing()) {
                    loadingDialog2.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        BaseApplication.getInstance().getIBLE().setChangPsd(true);
//                        Toast.makeText(context, "恭喜您，密码提交成功", Toast.LENGTH_SHORT).show();
                        Toast.makeText(context,"恭喜您，入库成功",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (loadingDialog2 != null && loadingDialog2.isShowing()) {
                    loadingDialog2.dismiss();
                }
            }
        });
    }

    @Override
    public void onDisconnect(int state) {
        count = 0;
        tvCount.setText(getText(R.string.open_count) + String.valueOf(count));
        tvAddress.setText("MAC:");
        tvName.setText("Name:");
        tvStatus.setText(getText(R.string.connect_status) + "Disconnect");
        tvCz.setText(R.string.current_cz);
        tvBattery.setText(R.string.battery);
        tvVersion.setText(R.string.device_version);
        m_myHandler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void onServicesDiscovered(String name, String address) {
        if (null != loadingDialog && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        Logger.e(getClass().getSimpleName(), "服务");
        tvAddress.setText("MAC:" + address);
        tvName.setText("Name:" + name);
        tvStatus.setText(getText(R.string.connect_status) + "Connected");
        getToken();
    }

    /**
     * 获取token
     */
    private void getToken() {
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BaseApplication.getInstance().getIBLE().getToken();
            }
        }, 1000);
    }

    @OnClick(R.id.bt_open)
    void open() {


        BaseApplication.getInstance().getIBLE().openLock();
    }

    @OnClick(R.id.bt_close)
    void close() {
        BaseApplication.getInstance().getIBLE().resetLock();
    }


    @OnClick(R.id.bt_status)
    void status() {
        BaseApplication.getInstance().getIBLE().getLockStatus();
    }

//    @OnClick(R.id.bt_wx)
//    void wx() {
//        requestPermission(new String[]{Manifest.permission.CAMERA}, 101);
//    }

    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("QR_CODE");
                    codenum = edbikeNum.getText().toString().trim();
                    addCar(result, codenum);
                } else {
                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    BaseApplication.getInstance().getIBLE().connect(address, LockStorage2Activity.this);
                    break;
                case 1:
                    break;
                case 2:
                    if (null != loadingDialog && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                        loadingDialog = null;
                    }
                    ToastUtils.showMessage("请求超时");
                    tvCz.setText("上传请求超时");
                    break;
                case 3:
                    if (null != loadingDialog && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                        loadingDialog = null;
                    }
                    tvCz.setText("当前操作：入库成功");
                    break;
                case 9:
                    if (null != loadingDialog && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                        loadingDialog = null;
                    }
                    tvCz.setText("当前操作：入库失败");
                    break;
            }
            return false;
        }
    });

    private void addCar(String result,String codenum){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("tokencode",result);    //二维码链接地址
            params.put("codenum",codenum);     //车辆编号
            params.put("macinfo",address);    //mac地址

            Log.e("addCar===", uid+"==="+access_token+"==="+result+"==="+codenum+"==="+address);

            HttpHelper.post(context, Urls.addCar, params, new TextHttpResponseHandler() {
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

                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            //修改密钥
                            loadingDialog = DialogUtils.getLoadingDialog(context, "正在修改密钥");
                            loadingDialog.show();
                            byte[] bytes2 = {Config.newKey[0],
                                    Config.newKey[1], Config.newKey[2], Config.newKey[3], Config.newKey[4],
                                    Config.newKey[5], Config.newKey[6], Config.newKey[7]};
                            BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY, bytes2);
                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] bytes1 = {Config.newKey[8],
                                            Config.newKey[9], Config.newKey[10], Config.newKey[11], Config.newKey[12],
                                            Config.newKey[13], Config.newKey[14], Config.newKey[15]};
                                    BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY2, bytes1);

                                    BaseApplication.getInstance().getIBLE().setChangKey(true);

                                }
                            }, 2000);

//                            byte[] bytes = {Config.newKey2[0],
//                                    Config.newKey2[1], Config.newKey2[2], Config.newKey2[3], Config.newKey2[4],
//                                    Config.newKey2[5], Config.newKey2[6], Config.newKey2[7]};
//                            BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY, bytes);
//
//                            m_myHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    byte[] bytes1 = {Config.newKey2[8],
//                                            Config.newKey2[9], Config.newKey2[10], Config.newKey2[11], Config.newKey2[12],
//                                            Config.newKey2[13], Config.newKey2[14], Config.newKey2[15]};
//                                    BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY2, bytes1);
//                                }
//                            }, 2000);

//                            return;
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();

                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
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
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.CAMERA)){
                        try {
                            Intent intent = new Intent();
                            intent.setClass(LockStorage2Activity.this, AddCarCaptureAct.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, 1);
                        } catch (Exception e) {
                            UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
                        }
                    }
                }else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
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
                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(localIntent);
                            finishMine();
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
