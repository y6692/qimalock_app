package com.qimalocl.manage.activity;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.fitsleep.sunshinelibrary.utils.ConvertUtils;
import com.fitsleep.sunshinelibrary.utils.DialogUtils;
import com.fitsleep.sunshinelibrary.utils.EncryptUtils;
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
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.ResultConsel;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.mode.GetTokenTxOrder;
import com.sunshine.blelibrary.mode.OpenLockTxOrder;
import com.sunshine.blelibrary.mode.Order;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

import org.apache.http.Header;

import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fitsleep.sunshinelibrary.utils.EncryptUtils.Encrypt;

public class LockManageAlterActivity extends MPermissionsActivity implements OnConnectionListener {
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
    @BindView(R.id.change_psd_btn)
    Button changePsdBtn;
    @BindView(R.id.update_version_btn)
    Button updateVersionBtn;
    @BindView(R.id.bt_xinbiao)
    Button btXinbiao;

    private int count = 0;
    private String name;
    private String address;
    private boolean isAuto;
    private Dialog loadingDialog;
    private Dialog loadingDialog2;
    public static final int QR_SCAN_REQUEST_CODE = 3638;

    private Dialog dialog;
    private EditText bikeNumEdit;
    private Button positiveButton, negativeButton;
    private String code = "";

    private boolean isStop = false;
    private boolean isOpen = false;

    private Context context = this;
    private String codenum = "";
    private String pdk = "";
    private String pwd = "";

    private boolean isChangePsd = false;
    private boolean isConnect = false;
    BleDevice bleDevice;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_manage);

        BleManager.getInstance().init(getApplication());
//        BleManager.getInstance()
//                .enableLog(true)
//                .setReConnectCount(10, 10000)
//                .setConnectOverTime(20000)
//                .setOperateTimeout(10000);

        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);

//        BleManager.getInstance().disconnectAllDevice();
//        BleManager.getInstance().destroy();

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
        pdk = getIntent().getStringExtra("pdk");
        pwd = getIntent().getStringExtra("pwd");

        Log.e("LockManageAlert===", pdk+"==="+address);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        tvAddress.setText("MAC:" + address);

        if (!TextUtils.isEmpty(address)) {
//            BaseApplication.getInstance().getIBLE().connect(address, this);
            setScanRule();
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {

//                    scan();
                    connect();
                }
            });

        }


        dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.pop_circles_menu, null);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);

        bikeNumEdit = (EditText) dialogView.findViewById(R.id.pop_circlesMenu_bikeNumEdit);
        positiveButton = (Button) dialogView.findViewById(R.id.pop_circlesMenu_positiveButton);
        negativeButton = (Button) dialogView.findViewById(R.id.pop_circlesMenu_negativeButton);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bikeNum = bikeNumEdit.getText().toString().trim();
                if (bikeNum == null || "".equals(bikeNum)) {
                    ToastUtils.showMessage("请输入车偏号");
                    return;
                }
                loadingDialog = DialogUtils.getLoadingDialog(LockManageAlterActivity.this, "正在提交");
                loadingDialog.show();
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Message message = new Message();
                message.what = 1;
                message.obj = bikeNum;
                m_myHandler.sendMessage(message);
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager manager1 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager1.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        titleText.setText("锁的信息");

        changePsdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //修改密码
                isChangePsd = true;
                loadingDialog = DialogUtils.getLoadingDialog(context, "正在修改密码");
                loadingDialog.show();
                byte[] bytes = {Config.password[0], Config.password[1], Config.password[2], Config.password[3], Config.password[4],
                        Config.password[5]};
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
        //固件
        updateVersionBtn.setVisibility(View.GONE);
    }

    private void setScanRule() {
//        String[] uuids;
//        String str_uuid = et_uuid.getText().toString();
//        if (TextUtils.isEmpty(str_uuid)) {
//            uuids = null;
//        } else {
//            uuids = str_uuid.split(",");
//        }
//        UUID[] serviceUuids = null;
//        if (uuids != null && uuids.length > 0) {
//            serviceUuids = new UUID[uuids.length];
//            for (int i = 0; i < uuids.length; i++) {
//                String name = uuids[i];
//                String[] components = name.split("-");
//                if (components.length != 5) {
//                    serviceUuids[i] = null;
//                } else {
//                    serviceUuids[i] = UUID.fromString(uuids[i]);
//                }
//            }
//        }

//        String[] names;
//        String str_name = et_name.getText().toString();
//        if (TextUtils.isEmpty(str_name)) {
//            names = null;
//        } else {
//            names = str_name.split(",");
//        }
//
//        String mac = et_mac.getText().toString();
//
//        boolean isAutoConnect = sw_auto.isChecked();

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setDeviceMac(address)                  // 只扫描指定mac的设备，可选
//                .setAutoConnect(true)                 // 连接时的autoConnect参数，可选，默认false
//                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    void scan(){
//        loadingDialog = DialogUtils.getLoadingDialog(context, "正在搜索...");
        loadingDialog.setTitle("正在搜索");
        loadingDialog.show();

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
//                mDeviceAdapter.clearScanDevice();
//                mDeviceAdapter.notifyDataSetChanged();
//                img_loading.startAnimation(operatingAnim);
//                img_loading.setVisibility(View.VISIBLE);
//                btn_scan.setText(getString(R.string.stop_scan));
                Log.e("onScanStarted===", "==="+success);

            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);

                Log.e("onLeScan===", bleDevice+"==="+bleDevice.getMac());
            }

            @Override
            public void onScanning(final BleDevice bleDevice) {
//                mDeviceAdapter.addDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();

                Log.e("onScanning===", bleDevice+"==="+bleDevice.getMac());

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(address.equals(bleDevice.getMac())){
                            //                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }

                            BleManager.getInstance().cancelScan();

                            Log.e("onScanning===2", isConnect+"==="+bleDevice+"==="+bleDevice.getMac());

                            Toast.makeText(context, "搜索成功", Toast.LENGTH_LONG).show();

                            connect();

//                            m_myHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(!isConnect)
//                                        connect();
//                                }
//                            }, 5 * 1000);
                        }
                    }
                });

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
//                img_loading.clearAnimation();
//                img_loading.setVisibility(View.INVISIBLE);
//                btn_scan.setText(getString(R.string.start_scan));

                Log.e("onScanFinished===", scanResultList+"==="+scanResultList.size());
            }
        });
    }

    void connect(){
//        loadingDialog = DialogUtils.getLoadingDialog(this, "正在连接...");
        loadingDialog.setTitle("正在连接");
        loadingDialog.show();

        BleManager.getInstance().connect(address, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.e("onStartConnect===", "===");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Log.e("onConnectFail===", bleDevice.getMac()+"==="+exception);

                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onConnectSuccess(BleDevice device, BluetoothGatt gatt, int status) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                isConnect = true;
                bleDevice = device;

//                BleManager.getInstance().cancelScan();

                Log.e("onConnectSuccess===", bleDevice.getMac()+"===");
                Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();


                tvName.setText("Name:" + name);
                tvStatus.setText(getText(R.string.connect_status) + "Connected");

                m_myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        getBleToken();

                    }
                }, 500);

                BleManager.getInstance().notify(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f6-0000-1000-8000-00805f9b34fb", new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Log.e("onNotifySuccess===", "===");
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Log.e("onNotifyFailure===", "===");
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
//                            byte[] values = characteristic.getValue();

                        Log.e("onCharacteristicChanged", "===0");


                        byte[] x = new byte[16];
                        System.arraycopy(data, 0, x, 0, 16);

                        byte[] mingwen = EncryptUtils.Decrypt(x, Config.newKey);    //060207FE02433001010606D41FC9553C  FE024330 01 01 06

                        Log.e("onCharacteristicChanged", x.length+"==="+ConvertUtils.bytes2HexString(data)+"==="+ConvertUtils.bytes2HexString(mingwen));

                        String s1 = ConvertUtils.bytes2HexString(mingwen);

                        if(s1.startsWith("0602")){      //获取token

                            token = s1.substring(6, 14);    //0602070C0580E001010406C8D6DC1949
                            GlobalParameterUtils.getInstance().setToken(ConvertUtils.hexString2Bytes(token));

                            Log.e("token===", isOpen+"==="+token+"==="+s1);

                            if(isOpen){
                                openLock();
                            }

                        }else if(s1.startsWith("0502")){
                            Log.e("openLock===", "==="+s1);

                            Toast.makeText(context, "开锁成功", Toast.LENGTH_LONG).show();
                        }else if(s1.startsWith("0508")){
                            Log.e("closeLock===1", "==="+s1);

                            if("00".equals(s1.substring(6, 8))){
                                Toast.makeText(context, "关闭成功", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(context, "关闭失败", Toast.LENGTH_LONG).show();
                            }

                        }else if(s1.startsWith("050F")){
                            Log.e("closeLock===2", "==="+s1);        //050F0101017A0020782400200F690300

                            if("01".equals(s1.substring(6, 8))){
                                Toast.makeText(context, "锁已关闭", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });



            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {

                isConnect = false;
                Log.e("connect=onDisConnected", "==="+isActiveDisConnected);

//                    if (isActiveDisConnected) {
//                        Toast.makeText(MainActivity.this, getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(MainActivity.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
//                        ObserverManager.getInstance().notifyObserver(bleDevice);
//                    }

            }
        });
    }

    void getBleToken(){
        String s = new GetTokenTxOrder().generateString();  //06010101490E602E46311640422E5238

        Log.e("getBleToken===1", "==="+s);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("getBleToken==onWriteSuc", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("getBleToken=onWriteFail", "==="+exception);
            }
        });
    }

    @OnClick(R.id.bt_open)
    void open() {
//        BaseApplication.getInstance().getIBLE().openLock();

        Log.e("open===", "==="+isConnect);

        isOpen =true;
        if(isConnect){

            if(token==null || "".equals(token)){
                getBleToken();
            }else{
                openLock();
            }

        }else{
            connect();
        }


    }

    void openLock() {
        String s = new OpenLockTxOrder(true).generateString();

//        s= s.substring(0, 18) + token + s.substring(26, 32);

        Log.e("onWriteSuccess===1", token+"==="+s);     //989C064A===050106323031373135989C064A750217

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.newKey);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("onWriteSuccess===a", current+"==="+total+"==="+justWrite);
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("onWriteFailure===a", "==="+exception);
            }
        });
    }

    @OnClick(R.id.bt_close)
    void close() {
//        BaseApplication.getInstance().getIBLE().resetLock();
    }

    @OnClick(R.id.bt_xinbiao)
    void xinbiao() {
//        BaseApplication.getInstance().getIBLE().xinbiao();
    }

    @OnClick(R.id.bt_status)
    void status() {
//        BaseApplication.getInstance().getIBLE().getLockStatus();
    }

    @OnClick(R.id.bt_wx)
    void wx() {
        requestPermission(new String[]{Manifest.permission.CAMERA}, 101);
    }

    @OnClick(R.id.mainUI_title_backBtn)
    void back() {
        scrollToFinishActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChangePsd = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
//        isStop = true;
//        m_myHandler.removeCallbacksAndMessages(null);
//        BaseApplication.getInstance().getIBLE().refreshCache();
//        BaseApplication.getInstance().getIBLE().stopScan();
//        BaseApplication.getInstance().getIBLE().close();
//        BaseApplication.getInstance().getIBLE().disconnect();



//        byte[] bb = {30,85,45,80,52,73,60,70,45,75,60,86,10,90,40,42};

        byte[] bb=new byte[3];

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb",
                bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("onWriteSuccess===", current+"==="+total+"==="+justWrite);
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("onWriteFailure===", "==="+exception);
            }
        });

        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();

        Log.e("onDestroy===", "===");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        BaseApplication.getInstance().getIBLE().close();
//        isStop = true;

//        BleManager.getInstance().disconnectAllDevice();
//        BleManager.getInstance().destroy();



        Log.e("onBackPressed===", "===");


        scrollToFinishActivity();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String data = intent.getStringExtra("data");

            Log.e("Receiver===", action+"==="+data);

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
                    //修改密钥
                    if ("1".equals(pdk)) {
                        loadingDialog = DialogUtils.getLoadingDialog(context, "正在修改密钥");
                        loadingDialog.show();
                        byte[] bytes = {Config.newKey[0],
                                Config.newKey[1], Config.newKey[2], Config.newKey[3], Config.newKey[4],
                                Config.newKey[5], Config.newKey[6], Config.newKey[7]};
                        BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY, bytes);
                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                byte[] bytes1 = {Config.newKey[8],
                                        Config.newKey[9], Config.newKey[10], Config.newKey[11], Config.newKey[12],
                                        Config.newKey[13], Config.newKey[14], Config.newKey[15]};
                                BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY2, bytes1);
                            }
                        }, 2000);
                    }
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
                        if (isChangePsd) {
                            Toast.makeText(context, "密码修改成功", Toast.LENGTH_SHORT).show();
                            changPsd();
                        }
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    break;

                case Config.XINBIAO_ACTION:        //搜索信标

//                    else if (decryptString.startsWith("0585")){

                    tvCz.setText("当前操作：搜索信标成功"+data.substring(2*10, 2*10+2)+"==="+data.substring(2*11, 2*11+2)+"==="+data);

//                    Log.e("parseData===", "==="+decryptString);

                    break;
                //固件升级
                case Config.UPDATE_VERSION_ACTION:
                    if (TextUtils.isEmpty(data)) {
                        Toast.makeText(context, "不支持升级", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "开始升级", Toast.LENGTH_SHORT).show();
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

        Log.e("changKey===", uid+"==="+access_token+"==="+codenum);

        RequestParams params = new RequestParams();
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
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        params.put("codenum", codenum);
        params.put("latitude", SharedPreferencesUrls.getInstance().getString("latitude", ""));
        params.put("longitude", SharedPreferencesUrls.getInstance().getString("longitude", ""));
        HttpHelper.post(context, Urls.changePsd, params, new TextHttpResponseHandler() {
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
                        BaseApplication.getInstance().getIBLE().setChangPsd(true);
                        pwd = "2";
                        Toast.makeText(context, "恭喜您，密码提交成功", Toast.LENGTH_SHORT).show();
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
        Log.e("getToken===1", "===");

        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("===2", "===");
                BaseApplication.getInstance().getIBLE().getToken();
                Log.e("===3", "===");
            }
        }, 1000);
    }



    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_SCAN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            code = data.getStringExtra("code");
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = (int) (display.getWidth() * 0.8); // 设置宽度0.6
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
            dialog.show();

            InputMethodManager manager = (InputMethodManager) getSystemService(
                    INPUT_METHOD_SERVICE);
            manager.showSoftInput(positiveButton, InputMethodManager.RESULT_SHOWN);
            manager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    BaseApplication.getInstance().getIBLE().connect(address, LockManageAlterActivity.this);
                    break;
                case 1:
                    String codenum = (String) mes.obj;
//                    postHttp(codenum);
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
}
