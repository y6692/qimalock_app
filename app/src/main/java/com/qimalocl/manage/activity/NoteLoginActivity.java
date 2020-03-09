package com.qimalocl.manage.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.UserMsgBean;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.qimalocl.manage.utils.ToastUtil;
import com.xylitolz.androidverificationcode.view.VerificationCodeView;

import org.apache.http.Header;

import java.util.Set;
import java.util.UUID;


import static android.text.TextUtils.isEmpty;

/**
 * Created by Administrator1 on 2017/2/16.
 */

public class NoteLoginActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;

    private LinearLayout headLayout;
    private EditText userNameEdit;
    private EditText codeEdit;
    private Button codeBtn;
    private Button loginBtn;
    private TextView findPsd;

    private boolean isCode;
    private int num;
    private TelephonyManager tm;

//    private LinearLayout content;
    private VerificationCodeView icv;

    private TextView tv_telphone;
    private TextView time;
    private TextView no_note;

    private String telphone = "";

    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_login);
        context = this;

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        telphone = getIntent().getStringExtra("telphone");

        initView();


//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        width = metrics.widthPixels;
//        height = metrics.heightPixels;
//
//        Log.e("NoteLogin===", metrics.density+"==="+width+"==="+height);

    }

    private void initView() {

        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);

//        userNameEdit = (EditText) findViewById(R.id.noteLoginUI_userName);
//        codeEdit = (EditText) findViewById(R.id.noteLoginUI_phoneNum_code);

        tv_telphone = (TextView) findViewById(R.id.tv_telphone);
        time = (TextView) findViewById(R.id.noteLoginUI_time);
        no_note = (TextView) findViewById(R.id.noteLoginUI_no_note);

        tv_telphone.setText(telphone);


//        content = (LinearLayout) findViewById(R.id.note_content);
        icv = (VerificationCodeView) findViewById(R.id.icv);


        backImg.setOnClickListener(this);
        time.setOnClickListener(this);
        no_note.setOnClickListener(this);

        icv.setInputCompleteListener(new VerificationCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                Log.e("icv_input===", icv.getContent()+"===");

                if(icv.getContent().length()==6){
                    inputMethodManager.hideSoftInputFromWindow(icv.getWindowToken(), 0); // 隐藏

                    loginHttp(icv.getContent());
                }

            }

            @Override
            public void deleteContent() {
                Log.e("icv_delete===", icv.getContent());
            }
        });

//        inputMethodManager.showSoftInput(icv, InputMethodManager.RESULT_SHOWN);
//        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

//        sendCode();

        handler.sendEmptyMessage(2);

        // 开始60秒倒计时
        handler.sendEmptyMessageDelayed(1, 1000);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
//        String telphone = userNameEdit.getText().toString();
        switch (v.getId()) {
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.noteLoginUI_time:
//                if (telphone == null || "".equals(telphone)) {
//                    Toast.makeText(context, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!StringUtil.isPhoner(telphone)) {
//                    Toast.makeText(context, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                sendCode();

                break;
            case R.id.noteLoginUI_no_note:
//                if (telphone == null || "".equals(telphone)) {
//                    Toast.makeText(context, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!StringUtil.isPhoner(telphone)) {
//                    Toast.makeText(context, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                sendCode(telphone);

//                UIHelper.goToAct(context, ServiceCenterActivity.class);

                break;
        }
    }

    private String getMyUUID() {

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        tmDevice = "" + tm.getDeviceId();

        tmSerial = "" + tm.getSimSerialNumber();

        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

        String uniqueId = deviceUuid.toString();

        Log.d("debug", "uuid=" + uniqueId);

        return uniqueId;

    }

    private void sendCode() {

//        "Accept": "application/vnd.ws.v1+json",
//        "Phone_Brand": "IPHONE",
//        "Phone_Model": "iPhone X",
//        "Phone_System": "iOS",
//        "Phone_System_Version": "13.1.2",
//        "App_Version": "1.0.0",
//        "Device_UUID": "B45A95F3-E1DB-44CA-989D-971618140D5E"

        Log.e("verificationcode===0", "==="+telphone);

        try{
            RequestParams params = new RequestParams();
            params.add("phone", telphone);
            params.add("scene", "1");

            HttpHelper.post(context, Urls.verificationcode, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("请稍等");
                        loadingDialog.show();
                    }
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {

//                        Toast.makeText(context, "=="+responseString, Toast.LENGTH_LONG).show();

                        Log.e("verificationcode===", "==="+responseString);

                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        if(result.getStatus_code()==200){
                            handler.sendEmptyMessage(2);

                            // 开始60秒倒计时
                            handler.sendEmptyMessageDelayed(1, 1000);
                        }else{
                            ToastUtil.showMessageApp(context, result.getMessage());
                        }


//                        if (result.getFlag().equals("Success")) {
//
//
//                        } else {
//                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(context, "fail=="+responseString, Toast.LENGTH_LONG).show();

                    Log.e("verificationcode===fail", throwable.toString()+"==="+responseString);

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }
            });
        }catch (Exception e){
            Toast.makeText(context, "==="+e, Toast.LENGTH_SHORT).show();
        }

    }

    private void loginHttp(String telcode) {
        Log.e("loginHttp===", telphone+"==="+telcode);

        RequestParams params = new RequestParams();
        params.put("phone", telphone);
        params.put("verification_code", telcode);

        HttpHelper.post2(context, Urls.authorizations, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("nla===authorizations", throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("authorizations===", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("authorizations===1", result.getData()+"==="+result.getStatus_code());

                            UserMsgBean bean = JSON.parseObject(result.getData(), UserMsgBean.class);

                            Log.e("authorizations===2", bean+"==="+bean.getToken());

                            if (null != bean.getToken()) {
                                SharedPreferencesUrls.getInstance().putString("access_token", "Bearer "+bean.getToken());
                                Toast.makeText(context,"恭喜您,登录成功",Toast.LENGTH_SHORT).show();

//                                UIHelper.goToAct(context, MainActivity.class);

                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("flag", true);
                                startActivity(intent);

                                scrollToFinishActivity();

                            }else{
                                Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
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
        });
    }



    public static String getUniqueID() {
        //获得独一无二的Psuedo ID
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public String getDeviceId() {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //wifi mac地址
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String wifiMac = info.getMacAddress();
            if (!isEmpty(wifiMac)) {
                deviceId.append("wifi");
                deviceId.append(wifiMac);
                Log.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            String imei = tm.getDeviceId();
            if(!isEmpty(imei)){
                deviceId.append("imei");
                deviceId.append(imei);
                Log.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
            //序列号（sn）
            String sn = tm.getSimSerialNumber();
            if(!isEmpty(sn)){
                deviceId.append("sn");
                deviceId.append(sn);
                Log.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = UUID.randomUUID().toString();
            if(!isEmpty(uuid)){
                deviceId.append("id");
                deviceId.append(uuid);
                Log.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("getDeviceId : ", deviceId.toString());
        return deviceId.toString();
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (num != 1) {
                    time.setText((--num) + "s 后点击重新获取");
                } else {
                    time.setText("重新获取");
                    time.setEnabled(true);
                    isCode = false;
                }
                if (isCode) {
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }else{
                num = 60;
                isCode = true;
                time.setText(num + "s 后点击重新获取");
                time.setEnabled(false);
            }
        };
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
