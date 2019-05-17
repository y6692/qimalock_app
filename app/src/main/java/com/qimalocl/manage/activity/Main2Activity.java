package com.qimalocl.manage.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseApplication;
import com.qimalocl.manage.ble.BLEService;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.model.EbikeInfoBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.qimalocl.manage.utils.ByteUtil;
import com.qimalocl.manage.utils.IoBuffer;
import com.qimalocl.manage.utils.SharePreUtil;
import com.zbar.lib.ScanCaptureAct;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Main2Activity extends SwipeBackActivity {
    private BluetoothAdapter mBluetoothAdapter;
    BLEService bleService = new BLEService();

    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.mainUI_title_titleText)
    TextView titleText;

    private String tel = "13188888888";

    private String codenum = "";
    private String mac = "";

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main2);
        ButterKnife.bind(this);
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        device_text = (TextView) super.findViewById(R.id.device);
//        readvalue = (TextView) super.findViewById(R.id.readvalue);

        titleText.setText("锁的信息");

        final Intent intent = getIntent();
        codenum = intent.getStringExtra("codenum");
        mac = intent.getStringExtra("address");

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        BLEService.bluetoothAdapter = mBluetoothAdapter;

        bleService.view = this;
        bleService.showValue = true;

//        bleService.connect("34:03:DE:54:E6:C6");  //922
//        Log.e("connect===", "==="+bleService.connect("34:03:DE:54:E6:C6"));

        Log.e("connect===", "==="+bleService.connect(mac));   //629

//        Log.e("connect===", "==="+bleService.connect("01:02:03:04:16:10"));

//        bleService.artifClose();

//        bleService.connect(this.deviceAddress);


        ebikeInfo();
    }

    @Override
    public void onBackPressed() {
        bleService.artifClose();
        super.onBackPressed();
        //Toast.makeText(FDQControlAct.this, "onBackPessed", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.mainUI_title_backBtn)
    void back() {
        scrollToFinishActivity();
    }

    void ebikeInfo() {

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(this,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(this, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("tokencode",codenum);
            HttpHelper.get(Main2Activity.this, Urls.ebikeInfo, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在提交");
//                        loadingDialog.show();
//                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
                    UIHelper.ToastError(Main2Activity.this, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
//                            JSONObject jsonObject = new JSONObject(result.getData());
//

                            EbikeInfoBean bean = JSON.parseObject(result.getData(), EbikeInfoBean.class);

                            tv.setText("电量："+bean.getElectricity());

                            Log.e("ebikeInfo===", responseString + "===");
                        }else {
                            Toast.makeText(Main2Activity.this, result.getMsg(),Toast.LENGTH_SHORT).show();
//                            if (loadingDialog != null && loadingDialog.isShowing()){
//                                loadingDialog.dismiss();
//                            }
                            scrollToFinishActivity();
                        }

                    } catch (Exception e) {
                        Log.e("Test","异常"+e);
                    }
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
                }
            });
        }
    }

    @OnClick(R.id.b3)
    void b3() {

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(this,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(this, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("tokencode",codenum);
            HttpHelper.post(Main2Activity.this, Urls.battery_unlock, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在提交");
//                        loadingDialog.show();
//                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
                    UIHelper.ToastError(Main2Activity.this, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            JSONObject jsonObject = new JSONObject(result.getData());

                            Log.e("b3===", responseString + "===");
                        }else {
                            Toast.makeText(Main2Activity.this, result.getMsg(),Toast.LENGTH_SHORT).show();
//                            if (loadingDialog != null && loadingDialog.isShowing()){
//                                loadingDialog.dismiss();
//                            }
                            scrollToFinishActivity();
                        }

                    } catch (Exception e) {
                        Log.e("Test","异常"+e);
                    }
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
                }
            });
        }
    }




    @OnClick(R.id.open)
    void button3() {

        bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

        button8();
        button9();

        button31();

    }

//    //关闭
//    @OnClick(R.id.button4_fdq)
//    void button4(Button b) {
//        IoBuffer ioBuffer = IoBuffer.allocate(20);
//        byte[] cmd = sendCmd("00000010", "00000000");
//        ioBuffer.writeBytes(cmd);
//        bleService.write(toBody(ioBuffer.readableBytes()));
//    }

    @OnClick(R.id.close)
    void close() {

//        bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

//        IoBuffer ioBuffer = IoBuffer.allocate(20);
//        byte[] cmd = sendCmd("00000010", "00000000");
//        ioBuffer.writeBytes(cmd);
//        bleService.write(toBody(ioBuffer.readableBytes()));

        bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

//        button8();
        button9();

        IoBuffer ioBuffer = IoBuffer.allocate(20);
        byte[] cmd = sendCmd("00001000", "00000000");
        ioBuffer.writeBytes(cmd);
        bleService.write(toBody(ioBuffer.readableBytes()));

//        bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});
//
////        bleService.sleep(2000);
//
//        IoBuffer ioBuffer = IoBuffer.allocate(20);
//        byte[] cmd = sendCmd("00000101", "00000000");
//        ioBuffer.writeBytes(cmd);
//        bleService.write(toBody(ioBuffer.readableBytes()));
    }

    void button31() {
//        bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

//        bleService.sleep(2000);

        IoBuffer ioBuffer = IoBuffer.allocate(20);
        byte[] cmd = sendCmd("00000101", "00000000");
        ioBuffer.writeBytes(cmd);
        bleService.write(toBody(ioBuffer.readableBytes()));
    }

    public byte[] sendCmd(String s1, String s2) {
        IoBuffer ioBuffer = IoBuffer.allocate(5);
        ioBuffer.writeByte(0XA1);
        ioBuffer.writeByte(ByteUtil.BitToByte(s1));
        ioBuffer.writeByte(ByteUtil.BitToByte(s2));

        ioBuffer.writeByte(0);
        ioBuffer.writeByte(0);

        return ioBuffer.array();
    }

    IoBuffer toBody(byte[] bb) {
        IoBuffer buffer = IoBuffer.allocate(20);
        buffer.writeByte(bb.length + 1);
        buffer.writeBytes(bb);
        buffer.writeByte((int) ByteUtil.SumCheck(bb));


        return buffer.flip();
    }

    @Override
    public void oncall() {
        super.oncall();
        button9();
    }

    // 注册
    void button8() {
        //0x02,0x11,0xXX,0xXX,0xXX,0xXX,0xXX,0xXX,0xXX,0xXX, 0xXX,0xXX,0xXX,0xXX,0xXX,0xXX,0xXX, 0xXX,0xXX,0xTT
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        ioBuffer.writeByte((byte) 0x82);
        ByteUtil.log("tel-->" + tel);
        String str = tel;

        byte[] bb = new byte[11];
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            bb[i] = (byte) a;
        }
        ioBuffer.writeBytes(bb);

        int crc = (int) ByteUtil.crc32(getfdqId("G3A1800000629BCXH"));
        byte cc[] = ByteUtil.intToByteArray(crc);
        ioBuffer.writeByte(cc[0] ^ cc[3]);
        ioBuffer.writeByte(cc[1] ^ cc[2]);
        ioBuffer.writeInt(0);
        bleService.write(toBody(ioBuffer.readableBytes()));
    }

    void button9() {
//		bleService.write( new byte[]{0x03, (byte)0x81,0x01,(byte)0x82});
//		bleService.sleep(200);
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        ioBuffer.writeByte((byte) 0x83);
        ioBuffer.writeBytes(getfdqId(tel));
        bleService.write(toBody(ioBuffer.readableBytes()));
        SharePreUtil.getPreferences("FDQID").putString("ID", tel);
    }

    byte[] getfdqId(String str) {

        IoBuffer ioBuffer = IoBuffer.allocate(17);
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            ioBuffer.writeByte((byte) a);
        }
        return ioBuffer.array();
    }

    @Override
    protected void onResume() {
//        bleService.connect("34:03:DE:54:E6:C6");
//        bleService.connect(this.deviceAddress);
//        connected = true;
//        invalidateOptionsMenu();
        super.onResume();
    }

//    @Override
//    protected void onPause() {
//        bleService.artifClose();
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        bleService.artifClose();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        bleService.artifClose();
//    }

}