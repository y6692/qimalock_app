package com.qimalocl.manage.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.fitsleep.sunshinelibrary.utils.ConvertUtils;
import com.fitsleep.sunshinelibrary.utils.DialogUtils;
import com.fitsleep.sunshinelibrary.utils.EncryptUtils;
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
import com.qimalocl.manage.model.CarBean;
import com.qimalocl.manage.model.KeyBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.utils.Globals;
import com.qimalocl.manage.utils.LogUtil;
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
import com.sofi.blelocker.library.utils.StringUtils;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.config.LockType;
import com.sunshine.blelibrary.mode.Battery2TxOrder;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fitsleep.sunshinelibrary.utils.EncryptUtils.Encrypt;
import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

//已改密钥密码
@SuppressLint("NewApi")
public class LockStorageTBTDActivity extends MPermissionsActivity {
    @BindView(R.id.mainUI_title_titleText)
    TextView titleText;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.bt_scan_zk)
    Button bt_scan_zk;
    @BindView(R.id.bt_scan_car)
    Button bt_scan_car;
    @BindView(R.id.tv_SN)
    TextView tv_SN;
    @BindView(R.id.tv_SIM)
    TextView tv_SIM;
    @BindView(R.id.tv_key)
    TextView tv_key;
    @BindView(R.id.tv_qrcode)
    TextView tv_qrcode;

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

    private String vendor_lock_id = "";
    private String qrcode = "";
    String battery_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_storage_tbtd);



        ButterKnife.bind(this);

        battery_name = getIntent().getStringExtra("battery_name");

        LogUtil.e("lsTBTDa===onCreate", battery_name+"===");

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        titleText.setText("入库");
        tv_name.setText("电池型号：" + battery_name);

//        lock_info();
    }

    protected void lock_info(){
        LogUtil.e("lock_info===", "==="+vendor_lock_id);

        RequestParams params = new RequestParams();
//        params.put("lock_mac", mac);
        params.put("vendor_lock_id", vendor_lock_id);
        params.put("type", 3);
        HttpHelper.get(context, Urls.lock_info, params, new TextHttpResponseHandler() {
//        HttpHelper.get(context, Urls.lock_info, params, new TextHttpResponseHandler() {
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
                            LogUtil.e("lock_info===1","==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            if(result.getStatus_code()==0){
                                KeyBean bean = JSON.parseObject(result.getData(), KeyBean.class);

                                isStorage = bean.getType();

                                if(isStorage==1){
                                    ToastUtil.showMessageApp(context, result.getMessage());
                                }else{
                                    tv_SN.setText("S/N号："+bean.getVendor_lock_id());
                                    tv_SIM.setText("SIM号："+bean.getSim());
                                    tv_key.setText("秘钥："+bean.getLock_secretkey());
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

    @OnClick(R.id.bt_scan_zk)
    void scan_zk() {
        Intent intent = new Intent();

        intent.setClass(context, ActivityScanerCode.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isZk",true);

        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.bt_scan_car)
    void scan_car() {
        Intent intent = new Intent();

        intent.setClass(context, ActivityScanerCode.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isCar",true);

        startActivityForResult(intent, 2);
    }

    @OnClick(R.id.bt_lock_in)
    void lock_in() {
        addCar();
    }

    void showDialog(){
        if(isStorage==0){
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
            customBuilder.setTitle("温馨提示").setMessage("开锁成功，是否进行入库")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                    LogUtil.e("sf===onC", "==="+type);

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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("onDestroy===", "===");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        BaseApplication.getInstance().getIBLE().close();
//        isStop = true;

        LogUtil.e("onBackPressed===", "===");

        scrollToFinishActivity();
    }

    Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    break;

            }
            return false;
        }
    });

    private void addCar(){

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("type", 4);
            params.put("qrcode", qrcode);    //二维码链接地址
//            params.put("lock_no", name);     //车辆编号
//            params.put("lock_mac", mac);    //mac地址
            params.put("vendor_lock_id", vendor_lock_id);
            params.put("battery_name", battery_name);

            LogUtil.e("addCar===", qrcode+"==="+vendor_lock_id+"==="+battery_name);

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

                        LogUtil.e("addCar===", responseString+"===");

//                        if (result.getFlag().equals("Success")) {
//                            Toast.makeText(context,"恭喜您，入库成功",Toast.LENGTH_SHORT).show();
//                        }else {
//                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                        }

                        Toast.makeText(context,	result.getMessage(), Toast.LENGTH_SHORT).show();

                        if(result.getStatus_code()==200){
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e("addCar===eee", "==="+e);
                    }

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {

                    if(data==null) return;

                    vendor_lock_id = data.getStringExtra("QR_CODE");

                    if(vendor_lock_id.contains("http:")){
                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                        customBuilder.setTitle("温馨提示").setMessage("请扫描中控二维码！")
                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        customBuilder.create().show();
                    }else{
                        lock_info();
                    }

                    LogUtil.e("onActivityResult===1", "==="+vendor_lock_id);


                } else {
                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                if (resultCode == RESULT_OK) {

                    if(data==null) return;

                    qrcode = data.getStringExtra("QR_CODE");

                    if(!qrcode.contains("http:")){
                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                        customBuilder.setTitle("温馨提示").setMessage("请扫描车辆二维码！")
                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                    }
                                });
                        customBuilder.create().show();
                    }else{
                        tv_qrcode.setText("车辆二维码："+qrcode);
                    }



                    LogUtil.e("onActivityResult===2", "==="+qrcode);

//                    addCar(result);
                } else {
                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
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
                            intent.setClass(LockStorageTBTDActivity.this, AddCarCaptureAct.class);
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
