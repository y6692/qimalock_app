package com.qimalocl.manage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.StringUtil;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.qimalocl.manage.utils.ToastUtil;

import org.apache.http.Header;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class SettingActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private Context context;

    private ImageView backImg;
    private TextView title;

    private LoadingDialog loadingDialog;
    private Button logoutBtn;

    public static boolean isForeground = false;

    private String telphone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        context = this;
        initView();

        isForeground = true;
        isRefresh = true;
        Log.e("LA===onCreate", "==="+isRefresh);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//      must store the new intent unless getIntent() will return the old one
        setIntent(intent);

        Log.e("la===onNewIntent", SharedPreferencesUrls.getInstance().getString("access_token", "") + "===");

    }

    @Override
    public void onResume() {
        isForeground = true;
//        isRefresh = true;
        Log.e("LA===onResume", "==="+isRefresh);
        super.onResume();
    }

    @Override
    public void onPause() {
//        isForeground = false;
        Log.e("LA===onPause", "==="+isRefresh);
        super.onPause();
    }


    @Override
    protected void onDestroy() {
//        isForeground = false;

        Log.e("LA===onDestroy", "==="+isRefresh);
        super.onDestroy();
    }

    private void initView() {

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("设置");

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

//        change_phone = (TextView) findViewById(R.id.loginUI_change_phone);
        logoutBtn = (Button) findViewById(R.id.settingUI_btn);

        backImg.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        isForeground = true;
        switch (v.getId()) {
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.settingUI_btn:

//                telphone = phoneEdit.getText().toString().trim().replaceAll("\\s+", "");
//                if (telphone == null || "".equals(telphone)) {
//                    Toast.makeText(context, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!StringUtil.isPhoner(telphone)) {
//                    Toast.makeText(context, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                sendCode();
                break;



        }
    }

    private void sendCode() {

        Log.e("verificationcode===0", "==="+telphone);

        try{
            RequestParams params = new RequestParams();
            params.add("phone", telphone);
            params.add("scene", "1");

            HttpHelper.post2(context, Urls.verificationcode, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("请稍等");
                        loadingDialog.show();
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

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {

//                        Toast.makeText(context, "=="+responseString, Toast.LENGTH_LONG).show();

                        Log.e("verificationcode===", "==="+responseString);

                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        if(result.getStatus_code()==200){
                            Intent intent = new Intent();
                            intent.setClass(context, NoteLoginActivity.class);
                            intent.putExtra("telphone",telphone);
                            startActivity(intent);
                        }else{
                            ToastUtil.showMessageApp(context, result.getMessage());
                        }

//                        if (result.getFlag().equals("Success")) {
//
//                        } else {
//                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }


            });
        }catch (Exception e){
            Toast.makeText(context, "==="+e, Toast.LENGTH_SHORT).show();
        }

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
