package com.qimalocl.manage.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
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
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.fitsleep.sunshinelibrary.utils.ConvertUtils;
import com.fitsleep.sunshinelibrary.utils.DialogUtils;
import com.fitsleep.sunshinelibrary.utils.EncryptUtils;
import com.fitsleep.sunshinelibrary.utils.Logger;
import com.fitsleep.sunshinelibrary.utils.ToastUtils;
import com.fitsleep.sunshinelibrary.utils.ToolsUtils;
import com.fitsleep.sunshinelibrary.utils.UtilSharedPreference;
import com.http.rdata.RRent;
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
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.KeyBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.utils.Globals;
import com.qimalocl.manage.utils.ToastUtil;
import com.sofi.blelocker.library.Code;
import com.sofi.blelocker.library.connect.listener.BleConnectStatusListener;
import com.sofi.blelocker.library.connect.options.BleConnectOptions;
import com.sofi.blelocker.library.model.BleGattProfile;
import com.sofi.blelocker.library.protocol.ICloseListener;
import com.sofi.blelocker.library.protocol.IConnectResponse;
import com.sofi.blelocker.library.protocol.IEmptyResponse;
import com.sofi.blelocker.library.protocol.IGetRecordResponse;
import com.sofi.blelocker.library.protocol.IGetStatusResponse;
import com.sofi.blelocker.library.utils.BluetoothLog;
import com.sofi.blelocker.library.utils.StringUtils;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.mode.BatteryTxOrder;
import com.sunshine.blelibrary.mode.GetLockStatusTxOrder;
import com.sunshine.blelibrary.mode.GetTokenTxOrder;
import com.sunshine.blelibrary.mode.OpenLockTxOrder;
import com.sunshine.blelibrary.mode.Order;
import com.sunshine.blelibrary.mode.XinbiaoTxOrder;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.zxing.lib.scaner.activity.ActivityScanerCode;
import com.zxing.lib.scaner.activity.AddCarCaptureAct;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fitsleep.sunshinelibrary.utils.EncryptUtils.Encrypt;
import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

//已改密钥密码
@SuppressLint("NewApi")
public class LockStorageActivity extends MPermissionsActivity {
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    @BindView(R.id.tv_version)
    TextView tvVersion;
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
    @BindView(R.id.edbikeNum)
    ClearEditText edbikeNum;

    private int count = 0;
    private String type;
    private String carType;
    private String name;
    private String mac;
    private boolean isAuto;
    private Dialog loadingDialog;
    private Dialog loadingDialog2;
    public static final int QR_SCAN_REQUEST_CODE = 3638;

//    private List<BleDevice> bleDeviceList = new ArrayList<>();
//    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
//    private BleDevice bleDevice;

    private boolean isStop = false;
    private boolean isPwd = false;

    private Context context = this;
    private String codenum = "";

    private boolean isChangePsd = false;
    private boolean isConnect = false;
    BleDevice bleDevice;
    String token;

    private boolean isOpen = false;
    private boolean isMac = false;
    private boolean isFind = false;

    private int isStorage;

//    private boolean mConnected = false;

    private String keySource = "";
    //密钥索引
    int encryptionKey= 0;
    //开锁密钥
    String keys = null;
    //服务器时间戳，精确到秒，用于锁同步时间
    long serverTime;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_storage);

        Log.e("onCreate===", "===");

        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(10, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(10000);

        ButterKnife.bind(this);
//        registerReceiver(broadcastReceiver, Config.initFilter());
        appVersion.setText("Version:" + ToolsUtils.getVersion(getApplicationContext()));


//        hexStringToByteArray("566C230035BBE3827CE40778567B3A7A");

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
//        type = getIntent().getStringExtra("type");
        type = SharedPreferencesUrls.getInstance().getString("type", "");
        if("2".equals(type) || "3".equals(type) || "9".equals(type) || "10".equals(type)){
            carType = "2";
            name = getIntent().getStringExtra("name");
        }else{
            carType = "1";
            name = StringUtils.getBikeName(getIntent().getStringExtra("name"));
        }

        mac = getIntent().getStringExtra("mac");
        codenum = getIntent().getStringExtra("codenum");



//        BaseApplication.getInstance().getIBLE().setChangKey(true);
        BaseApplication.getInstance().getIBLE().setChangKey(false);
        BaseApplication.getInstance().getIBLE().setChangPsd(false);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        titleText.setText("入库");
        tvName.setText("锁名：" + name);
        tvAddress.setText("MAC地址：" + mac);

        Log.e("LockStorageActivity===", name+"==="+mac+"==="+codenum);


        changePsdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改密码
//                if ("1".equals(pwd)) {

                Log.e("lsa===changePsdBtn", "===");

                codenum = "40002000";

//                isChangePsd = true;
                loadingDialog = DialogUtils.getLoadingDialog(context, "正在修改密码");
                loadingDialog.show();

                byte[] bytes = {Config.key[0],
                        Config.key[1], Config.key[2], Config.key[3], Config.key[4],
                        Config.key[5], Config.key[6], Config.key[7]};
                BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY, bytes);

                m_myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        byte[] bytes1 = {Config.key[8],
                                Config.key[9], Config.key[10], Config.key[11], Config.key[12],
                                Config.key[13], Config.key[14], Config.key[15]};
                        BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY2, bytes1);

                        BaseApplication.getInstance().getIBLE().setChangKey(false);
                    }
                }, 2000);

//                byte[] bytes = {Config.newKey[0],
//                        Config.newKey[1], Config.newKey[2], Config.newKey[3], Config.newKey[4],
//                        Config.newKey[5], Config.newKey[6], Config.newKey[7]};
//                BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY, bytes);
//                m_myHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        byte[] bytes1 = {Config.newKey[8],
//                                Config.newKey[9], Config.newKey[10], Config.newKey[11], Config.newKey[12],
//                                Config.newKey[13], Config.newKey[14], Config.newKey[15]};
//                        BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY2, bytes1);
//                    }
//                }, 2000);


//                byte[] bytes = {Config.password[0], Config.password[1], Config.password[2], Config.password[3], Config.password[4], Config.password[5]};
////                byte[] bytes = {Config.passwordnew[0], Config.passwordnew[1],
////                        Config.passwordnew[2], Config.passwordnew[3],
////                        Config.passwordnew[4], Config.passwordnew[5]};
////                byte[] bytes = {Config.passwordnew2[0], Config.passwordnew2[1],
////                        Config.passwordnew2[2], Config.passwordnew2[3],
////                        Config.passwordnew2[4], Config.passwordnew2[5]};
//                BaseApplication.getInstance().getIBLE().setPassword(Order.TYPE.RESET_PASSWORD, bytes);
//
//                m_myHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        byte[] bytes = {Config.passwordnew[0], Config.passwordnew[1],
//                                Config.passwordnew[2], Config.passwordnew[3],
//                                Config.passwordnew[4], Config.passwordnew[5]};
//
////                        byte[] bytes = {Config.passwordnew2[0], Config.passwordnew2[1],
////                                Config.passwordnew2[2], Config.passwordnew2[3],
////                                Config.passwordnew2[4], Config.passwordnew2[5]};
//
////                        BaseApplication.getInstance().getIBLE().setChangKey(false);
//
//                        BaseApplication.getInstance().getIBLE().setPassword(Order.TYPE.RESET_PASSWORD2, bytes);
//                    }
//                }, 2000);
//                }else {
//                    Toast.makeText(context,"此设备密码已修改",Toast.LENGTH_SHORT).show();
//                }
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
                        int checkPermission = LockStorageActivity.this.checkSelfPermission(Manifest.permission.CAMERA);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
                            } else {
                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(LockStorageActivity.this);
                                customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        LockStorageActivity.this.requestPermissions(
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


        lock_info();


    }

    protected void lock_info(){
        Log.e("lock_info===",mac+"===");

//        RequestParams params = new RequestParams();
//        params.put("lock_mac", mac);
        HttpHelper.get(context, Urls.lock_info+mac, new TextHttpResponseHandler() {
//        HttpHelper.get(context, Urls.lock_info, params, new TextHttpResponseHandler() {
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
                            Log.e("lock_info===","==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            if(result.getStatus_code()==0){
                                KeyBean bean = JSON.parseObject(result.getData(), KeyBean.class);

                                isStorage = bean.getType();

                                tvType.setText("是否入库："+(isStorage==1?"是":"否"));

                                if (!TextUtils.isEmpty(mac)) {
//                                  BaseApplication.getInstance().getIBLE().connect(address, this);

                                    loadingDialog.setTitle("正在连接");
                                    loadingDialog.show();

                                    if("2".equals(type) || "3".equals(type) || "9".equals(type)){

                                        Config.key = hexStringToByteArray(bean.getLock_secretkey());
                                        Config.password = hexStringToByteArray(bean.getLock_password());

                                        m_myHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
//                    if(isMac){
//                        connect();
//                    }else{
//
//                        setScanRule();
//                        scan();
//                    }

                                                connect();
                                            }
                                        }, 0 * 1000);
                                    }else{
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {


                                                            m_myHandler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    connectDevice();
                                                                    ClientManager.getClient().registerConnectStatusListener(mac, mConnectStatusListener);
                                                                    ClientManager.getClient().notifyClose(mac, mCloseListener);


                                                                }
                                                            }, 0 * 1000);
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    }
                                }
                            }else{
                                ToastUtil.showMessageApp(context, result.getMessage());
                            }


                        }catch (Exception e){
                            closeLoadingDialog();
                        }


                    }
                });



            }
        });
    }

    //type2、3
    void connect(){
//        loadingDialog = DialogUtils.getLoadingDialog(this, "正在连接...");


        BleManager.getInstance().connect(mac, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.e("onStartConnect===", "===");
            }

            @Override
            public void onConnectFail(com.clj.fastble.data.BleDevice bleDevice, BleException exception) {
                Log.e("onConnectFail===", bleDevice.getMac()+"==="+exception);

//                Toast.makeText(context, "连接失败", Toast.LENGTH_LONG).show();
                tvStatus.setText(getText(R.string.connect_status) + "连接失败");

                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onConnectSuccess(com.clj.fastble.data.BleDevice device, BluetoothGatt gatt, int status) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                isConnect = true;
                bleDevice = device;

//                BleManager.getInstance().cancelScan();

                Log.e("onConnectSuccess===", bleDevice.getMac()+"===");
//                Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();

                tvStatus.setText(getText(R.string.connect_status) + "连接成功");

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

                        byte[] mingwen = EncryptUtils.Decrypt(x, Config.key);    //060207FE02433001010606D41FC9553C  FE024330 01 01 06

                        Log.e("onCharacteristicChanged", x.length+"==="+ ConvertUtils.bytes2HexString(data)+"==="+ConvertUtils.bytes2HexString(mingwen));

                        String s1 = ConvertUtils.bytes2HexString(mingwen);

                        if(s1.startsWith("0602")){      //获取token

                            token = s1.substring(6, 14);    //0602070C0580E001010406C8D6DC1949
                            GlobalParameterUtils.getInstance().setToken(ConvertUtils.hexString2Bytes(token));

                            Log.e("token===", isOpen+"==="+token+"==="+s1);

                            m_myHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getBattery();
                                }
                            }, 1000);

                            if(isOpen){
                                openLock();
                            }else{
                                getLockStatus();
                            }

//                            Toast.makeText(context, "token获取成功", Toast.LENGTH_LONG).show();
                        }else if(s1.startsWith("0502")){
                            Log.e("openLock===", "==="+s1);

//                            getLockStatus();
                            closeLoadingDialog();

                            Toast.makeText(context, "开锁成功", Toast.LENGTH_LONG).show();

                            showDialog();

                        }else if(s1.startsWith("0508")){
                            Log.e("closeLock===1", "==="+s1);

//                            if("00".equals(s1.substring(6, 8))){
//                                Toast.makeText(context, "关闭成功", Toast.LENGTH_LONG).show();
//                            }else{
//                                Toast.makeText(context, "关闭失败", Toast.LENGTH_LONG).show();
//                            }

//                            getLockStatus();
                        }else if(s1.startsWith("0202")){    //电量

                            Log.e("battery===", "==="+s1);  //0202016478A2FBC2537CA17B22DB9AE9

                            tvBattery.setText("电池电量："+Integer.parseInt(s1.substring(6, 8), 16)+"%");

                        }else if(s1.startsWith("050F")){
                            Log.e("closeLock===2", "==="+s1);        //050F0101017A0020782400200F690300

                            closeLoadingDialog();

                            if("01".equals(s1.substring(6, 8))){
                                Toast.makeText(context, "锁已关闭", Toast.LENGTH_LONG).show();
                            }else{
//                                Toast.makeText(context, "锁已打开", Toast.LENGTH_LONG).show();
                            }
                        }else if(s1.startsWith("058502")){

                            Log.e("xinbiao===", "当前操作：搜索信标成功"+s1.substring(2*10, 2*10+2)+"==="+s1.substring(2*11, 2*11+2)+"==="+s1);

//                            if("000000000000".equals(s1.substring(2*4, 2*10))){
//                                major = 0;
//                            }else{
//                                major = 1;
//                            }
                        }

                    }
                });



            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, com.clj.fastble.data.BleDevice bleDevice, BluetoothGatt gatt, int status) {

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

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.key);

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

    private void getBattery(){
        String s = new BatteryTxOrder().generateString();  //06010101490E602E46311640422E5238

        Log.e("getBattery===1", "==="+s);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.key);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("getBattery==onWriteS", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("getBattery=onWriteFa", "==="+exception);
            }
        });
    }

    void getLockStatus(){
        String s = new GetLockStatusTxOrder().generateString();  //06010101490E602E46311640422E5238

        Log.e("getLockStatus===1", "==="+s);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.key);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("getLockStatus==onWriteS", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("getLockStatus=onWriteFa", "==="+exception);
            }
        });
    }

    void getXinbiao(){
        String s = new XinbiaoTxOrder().generateString();  //06010101490E602E46311640422E5238

        Log.e("getXinbiao===1", "==="+s);  //1648395B

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.key);

        BleManager.getInstance().write(bleDevice, "0000fee7-0000-1000-8000-00805f9b34fb", "000036f5-0000-1000-8000-00805f9b34fb", bb, true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.e("getXinbiao==onWriteS", current+"==="+total+"==="+ConvertUtils.bytes2HexString(justWrite));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("getXinbiao=onWriteFa", "==="+exception);
            }
        });
    }

    void openLock() {
        Log.e("openLock===", type+"==="+isConnect+"==="+mac);


        String s = new OpenLockTxOrder(false).generateString();

//        s= s.substring(0, 18) + token + s.substring(26, 32);

        Log.e("onWriteSuccess===1", token+"==="+s);     //989C064A===050106323031373135989C064A750217

        byte[] bb = Encrypt(ConvertUtils.hexString2Bytes(s), Config.key);

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



    //type5、6   连接设备
    private void connectDevice() {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(1)
                .setConnectTimeout(10000)
                .setServiceDiscoverRetry(1)
                .setServiceDiscoverTimeout(10000)
                .setEnableNotifyRetry(1)
                .setEnableNotifyTimeout(10000)
                .build();

        ClientManager.getClient().connect(mac, options, new IConnectResponse() {
            @Override
            public void onResponseFail(final int code) {
                isStop = false;
                isConnect = false;

//                com.qimalocl.manage.utils.UIHelper.dismiss();

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("connect===fail", Code.toString(code));
//                        com.qimalocl.manage.utils.UIHelper.showToast(context, Code.toString(code));
//                        ToastUtil.showMessageApp(context, Code.toString(code));

                        tvStatus.setText(getText(R.string.connect_status) + "连接失败");
                        closeLoadingDialog();
                    }
                });

            }

            @Override
            public void onResponseSuccess(BleGattProfile profile) {
                isStop = true;
                isConnect = true;

                tvStatus.setText(getText(R.string.connect_status) + "连接成功");

//                com.qimalocl.manage.utils.UIHelper.dismiss();

                Log.e("connect===Success", "===");

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        BluetoothLog.v(String.format("profile:\n%s", profile));
//                        refreshData(true);

                        if (Globals.bType == 1) {
//                            com.qimalocl.manage.utils.UIHelper.showProgress(context, "正在关锁中");
                            getBleRecord();
                        }
                    }
                });

            }
        });
    }

    private void connectDeviceIfNeeded() {
        if (!isConnect) {
            connectDevice();
        } else {
            ClientManager.getClient().stopSearch();
        }
    }

    //与设备，获取记录
    private void getBleRecord() {
//        m_myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                com.qimalocl.manage.utils.UIHelper.showProgress(context, "get_bike_record");
//            }
//        });

        ClientManager.getClient().getRecord(mac, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, String transType, String mackey, String index, final int Major, final int Minor, String vol) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===suc", "");

//                        com.qimalocl.manage.utils.UIHelper.dismiss();
                        deleteBleRecord(bikeTradeNo);
//                        uploadRecordServer(phone, bikeTradeNo, timestamp, transType, mackey, index, cap, vol);
                    }
                });

            }

            @Override
            public void onResponseSuccessEmpty() {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===Empty", "");
//                        com.qimalocl.manage.utils.UIHelper.dismiss();
//                        com.qimalocl.manage.utils.UIHelper.showToast(context, "record empty");

//                        closeLoadingDialog();
                    }
                });

            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("getBleRecord===", Code.toString(code));
//                        com.qimalocl.manage.utils.UIHelper.dismiss();
//                        com.qimalocl.manage.utils.UIHelper.showToast(context, Code.toString(code));

                        ToastUtil.showMessageApp(context, Code.toString(code));
                        closeLoadingDialog();
                    }
                });

            }
        });
    }

    //与设备，删除记录
    private void deleteBleRecord(String tradeNo) {
//        m_myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                com.qimalocl.manage.utils.UIHelper.showProgress(context, "delete_bike_record");
//            }
//        });

        ClientManager.getClient().deleteRecord(mac, tradeNo, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, final String bikeTradeNo, String timestamp, String transType, String mackey, String index, final int Major, final int Minor, String vol) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("deleteBleRecord===suc", "");

//                        com.qimalocl.manage.utils.UIHelper.dismiss();
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
                        Log.e("deleteBleRecord===Empty", "");

//                        com.qimalocl.manage.utils.UIHelper.dismiss();

//                        ToastUtil.showMessageApp(context, Code.toString(code));
                        closeLoadingDialog();

//                        if(Globals.bType == 1) {
//                            Globals.bType = 0;
//                            tvOpen.setText("开锁");
//                        }
//                        else {
//                            Globals.bType = 1;
//                            tvOpen.setText("已开锁");
//                        }
                    }
                });

            }

            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("deleteBleRecord===f", Code.toString(code));
//                        com.qimalocl.manage.utils.UIHelper.dismiss();
//                        com.qimalocl.manage.utils.UIHelper.showToast(context, Code.toString(code));

                        ToastUtil.showMessageApp(context, Code.toString(code));
                        closeLoadingDialog();
                    }
                });


            }
        });
    }

    protected void rent(){
        Log.e("rent===000",mac+"==="+name+"==="+keySource);

        RequestParams params = new RequestParams();
        params.put("lock_no", name);
//        params.put("macinfo", mac);
        params.put("keySource",keySource);
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
                            Log.e("rent===","==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            KeyBean bean = JSON.parseObject(result.getData(), KeyBean.class);

//                            if (loadingDialog != null && loadingDialog.isShowing()){
//                                loadingDialog.dismiss();
//                            }

                            encryptionKey = bean.getEncryptionKey();
                            keys = bean.getKeys();
                            serverTime = bean.getServerTime();

                            Log.e("rent===", mac+"==="+encryptionKey+"==="+keys);

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

    //与设备，开锁
    private void openBleLock(RRent.ResultBean resultBean) {
//        m_myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                com.qimalocl.manage.utils.UIHelper.showProgress(context, "open_bike_status");
//            }
//        });

//        ClientManager.getClient().openLock(mac, "18112348925", resultBean.getServerTime(),

        Log.e("scan===openBleLock", serverTime+"==="+keys+"==="+encryptionKey);

        ClientManager.getClient().openLock(mac,"000000000000", (int) serverTime, keys, encryptionKey, new IEmptyResponse(){
            //        ClientManager.getClient().openLock(mac,"000000000000", resultBean.getServerTime(), resultBean.getKeys(), resultBean.getEncryptionKey(), new IEmptyResponse(){
            @Override
            public void onResponseFail(final int code) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("scan===openBleLock1", code+"==="+Code.toString(code));
//                        com.qimalocl.manage.utils.UIHelper.dismiss();
//                        com.qimalocl.manage.utils.UIHelper.showToast(context, Code.toString(code));

                        ToastUtil.showMessageApp(context, Code.toString(code));
                        closeLoadingDialog();

                        getBleRecord();
                    }
                });

            }

            @Override
            public void onResponseSuccess() {
                Log.e("scan===openBleLock2", "===");
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        com.qimalocl.manage.utils.UIHelper.dismiss();
                        getBleRecord();

                        closeLoadingDialog();
                        ToastUtil.showMessageApp(context,"恭喜您,开锁成功!");


                        showDialog();
                    }
                });

            }
        });
    }

    void showDialog(){
        if(isStorage>0){
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
            customBuilder.setTitle("温馨提示").setMessage("开锁成功，是否进行入库")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                    Log.e("sf===onC", "==="+type);

                    Intent intent = new Intent();

                    intent.setClass(context, ActivityScanerCode.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("isChangeKey",false);
                    intent.putExtra("isAdd",true);

                    startActivityForResult(intent, 1);

                }
            });
            customBuilder.create().show();


        }

    }

    //监听锁关闭事件
    private final ICloseListener mCloseListener = new ICloseListener() {
        @Override
        public void onNotifyClose() {
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("onNotifyClose===", "====");

//                    BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onNotifyClose"));
//                    tvOpen.setText("开锁");

                    ToastUtil.showMessageApp(context,"锁已关闭");

                    getBleRecord();
                }
            });

        }
    };

    //监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, final int status) {
            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
//                    BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onConnectStatusChanged %d in %s", status, Thread.currentThread().getName()));

                    Log.e("ConnectStatus===", "===="+(status == STATUS_CONNECTED));

                    if(status == STATUS_CONNECTED){
//                        refreshData(true);

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }


                    Globals.isBleConnected = isConnect = (status == STATUS_CONNECTED);
                    connectDeviceIfNeeded();
                }
            });

        }
    };

    void closeLoadingDialog(){
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    @OnClick(R.id.mainUI_title_backBtn)
    void back() {
        scrollToFinishActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChangePsd = false;
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                scanDevice();
//            }
//        }, 500);
    }

//    public void scanDevice() {
//        bluetoothDeviceList.clear();
//        bleDeviceList.clear();
//        requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
//    }
//
//    @Override
//    public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
//        if (!bluetoothDeviceList.contains(device)) {
//            bluetoothDeviceList.add(device);
//            bleDevice = new BleDevice(device, scanRecord, rssi);
//            bleDeviceList.add(bleDevice);
//        }
//        if (address.equals(device.getAddress())) {
//            isStop = true;
//            BaseApplication.getInstance().getIBLE().stopScan();
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (broadcastReceiver != null) {
//            unregisterReceiver(broadcastReceiver);
//            broadcastReceiver = null;
//        }
//        isStop = true;
//        m_myHandler.removeCallbacksAndMessages(null);
//        BaseApplication.getInstance().getIBLE().stopScan();

        if("2".equals(type) || "3".equals(type) || "9".equals(type)){
            BleManager.getInstance().disconnectAllDevice();
            BleManager.getInstance().destroy();
        }else{
            ClientManager.getClient().stopSearch();
            ClientManager.getClient().disconnect(mac);
            ClientManager.getClient().disconnect(mac);
            ClientManager.getClient().disconnect(mac);
            ClientManager.getClient().disconnect(mac);
            ClientManager.getClient().disconnect(mac);
            ClientManager.getClient().disconnect(mac);
            ClientManager.getClient().unnotifyClose(mac, mCloseListener);
            ClientManager.getClient().unregisterConnectStatusListener(mac, mConnectStatusListener);
        }




        Log.e("onDestroy===", "===");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        BaseApplication.getInstance().getIBLE().close();
//        isStop = true;

        Log.e("onBackPressed===", "===");

        scrollToFinishActivity();
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
                    if (TextUtils.isEmpty(data)) {
                        Toast.makeText(context, "密钥修改失败", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "密钥修改成功", Toast.LENGTH_LONG).show();
//                        changKey();
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

//    @Override
//    public void onConnect() {
//        Logger.e(getClass().getSimpleName(), "连接成功");
//    }

    private void changKey() {
        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
            Toast.makeText(context, "请先登录您的账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
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
//                        BaseApplication.getInstance().getIBLE().setChangKey(false);



//                        isChangePsd = true;
                        loadingDialog2 = DialogUtils.getLoadingDialog(context, "正在修改密码");
                        loadingDialog2.show();

//                        loadingDialog.setTitle("正在修改密码");
//                        loadingDialog.show();

                        isPwd = false;

                        byte[] bytes = {Config.passwordnew[0], Config.passwordnew[1],
                                Config.passwordnew[2], Config.passwordnew[3],
                                Config.passwordnew[4], Config.passwordnew[5]};
                        BaseApplication.getInstance().getIBLE().setPassword(Order.TYPE.RESET_PASSWORD, bytes);

                        m_myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

//                                byte[] bytes = {Config.passwordnew[0], Config.passwordnew[1],
//                                        Config.passwordnew[2], Config.passwordnew[3],
//                                        Config.passwordnew[4], Config.passwordnew[5]};
                                byte[] bytes = {Config.password[0], Config.password[1],
                                        Config.password[2], Config.password[3],
                                        Config.password[4], Config.password[5]};

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
                        Toast.makeText(context, "恭喜您，密码提交成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog2 != null && loadingDialog2.isShowing()) {
                    loadingDialog2.dismiss();
                }
            }
        });
    }

//    @Override
//    public void onDisconnect(int state) {
//        count = 0;
//        tvCount.setText(getText(R.string.open_count) + String.valueOf(count));
//        tvAddress.setText("MAC:");
//        tvName.setText("Name:");
//        tvStatus.setText(getText(R.string.connect_status) + "Disconnect");
//        tvCz.setText(R.string.current_cz);
//        tvBattery.setText(R.string.battery);
//        tvVersion.setText(R.string.device_version);
//        m_myHandler.sendEmptyMessageDelayed(0, 1000);
//    }
//
//    @Override
//    public void onServicesDiscovered(String name, String address) {
//        if (null != loadingDialog && loadingDialog.isShowing()) {
//            loadingDialog.dismiss();
//            loadingDialog = null;
//        }
//        Logger.e(getClass().getSimpleName(), "服务");
//        tvAddress.setText("MAC:" + address);
//        tvName.setText("Name:" + name);
//        tvStatus.setText(getText(R.string.connect_status) + "Connected");
//        getToken();
//    }
//
//    /**
//     * 获取token
//     */
//    private void getToken() {
//        m_myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                BaseApplication.getInstance().getIBLE().getToken();
//            }
//        }, 1000);
//    }

    @OnClick(R.id.bt_open)
    void open() {


//        BaseApplication.getInstance().getIBLE().openLock();

        Log.e("open===", type+"==="+isConnect+"==="+mac+"==="+token);

        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.setTitle("正在唤醒车锁");
            loadingDialog.show();
        }

        if("2".equals(type) || "3".equals(type) || "9".equals(type)){

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
        }else{
            if(isConnect){
                ClientManager.getClient().getStatus(mac, new IGetStatusResponse() {
                    @Override
                    public void onResponseSuccess(String version, String keySerial, String macKey, final String vol) {
                        keySource = keySerial;

                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
//                                com.qimalocl.manage.utils.UIHelper.dismiss();
                                tvBattery.setText("电池电量：" + vol + "V");

                                rent();
                            }
                        });
                    }

                    @Override
                    public void onResponseFail(final int code) {

                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("getStatus===f", Code.toString(code));
//                                com.qimalocl.manage.utils.UIHelper.dismiss();
//                                com.qimalocl.manage.utils.UIHelper.showToast(context, Code.toString(code));

                                ToastUtil.showMessageApp(context, Code.toString(code));
                                closeLoadingDialog();
                            }
                        });
                    }

                });
            }else{
                connectDevice();
            }
        }
    }

    @OnClick(R.id.bt_close)
    void close() {
//        BaseApplication.getInstance().getIBLE().resetLock();
    }


    @OnClick(R.id.bt_status)
    void status() {
//        BaseApplication.getInstance().getIBLE().getLockStatus();

        Log.e("status===", "==="+isConnect);


        if(isConnect){
            getLockStatus();
        }else{
            isOpen = false;
            connect();
        }
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
//                    BaseApplication.getInstance().getIBLE().connect(mac, LockStorageActivity.this);
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

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("type", carType);
            params.put("qrcode", result);    //二维码链接地址
            params.put("lock_no", name);     //车辆编号
            params.put("lock_mac", mac);    //mac地址

            Log.e("addCar===", result+"==="+result+"==="+name+"==="+mac);

            HttpHelper.post(context, Urls.lock_in, params, new TextHttpResponseHandler() {
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

                        Log.e("addCar===", responseString+"===");

//                        if (result.getFlag().equals("Success")) {
//                            Toast.makeText(context,"恭喜您，入库成功",Toast.LENGTH_SHORT).show();
//                        }else {
//                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                        }

                        Toast.makeText(context,	result.getMessage(), Toast.LENGTH_SHORT).show();

                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("addCar===eee", "==="+e);
                    }

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }

                }
            });
        }
    }

    private void addCar2(String result,String codenum){

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
            params.put("macinfo",mac);    //mac地址

            Log.e("addCar===", uid+"==="+access_token+"==="+result+"==="+codenum+"==="+mac);

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
                            Toast.makeText(context,"恭喜您，入库成功",Toast.LENGTH_SHORT).show();
                            //修改密钥
//                            loadingDialog = DialogUtils.getLoadingDialog(context, "正在修改密钥");
//                            loadingDialog.show();
//                            byte[] bytes = {Config.newKey[0],
//                                    Config.newKey[1], Config.newKey[2], Config.newKey[3], Config.newKey[4],
//                                    Config.newKey[5], Config.newKey[6], Config.newKey[7]};
//                            BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY, bytes);
//                            m_myHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    byte[] bytes1 = {Config.newKey[8],
//                                            Config.newKey[9], Config.newKey[10], Config.newKey[11], Config.newKey[12],
//                                            Config.newKey[13], Config.newKey[14], Config.newKey[15]};
//                                    BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY2, bytes1);
//                                }
//                            }, 2000);


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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("addCar===eee", "==="+e);

                    }

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
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
                            intent.setClass(LockStorageActivity.this, AddCarCaptureAct.class);
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
