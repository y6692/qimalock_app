package com.qimalocl.manage.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fitsleep.sunshinelibrary.utils.IntentUtils;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseApplication;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceSelectActivity extends Activity {
    @BindView(R.id.bt0)
    Button bt0;
    @BindView(R.id.bt1)
    Button bt1;
    @BindView(R.id.bt2)
    Button bt2;
    @BindView(R.id.bt3)
    Button bt3;
    @BindView(R.id.bt4)
    Button bt4;
    @BindView(R.id.bt5)
    Button bt5;
    @BindView(R.id.bt6)
    Button bt6;
    @BindView(R.id.mainUI_title_backBtn)
    ImageView backBtn;
    @BindView(R.id.mainUI_title_titleText)
    TextView titleText;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 修改状态栏颜色，4.4+生效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.ui_device_select);
        ButterKnife.bind(this);

        context = this;

        titleText.setText("入库");

//        if("t".equals(BaseApplication.mode)){
//            bt0.setVisibility(View.VISIBLE);
//        }else{
//            bt0.setVisibility(View.GONE);
//        }
    }

    @OnClick(R.id.mainUI_title_backBtn)
    void back() {
        finish();
    }

    @OnClick(R.id.bt0)
    void bt0() {
        SharedPreferencesUrls.getInstance().putString("type", "2");

        Intent intent = new Intent();
        intent.setClass(context, DeviceListActivity.class);
//        intent.putExtra("isChange",false);
        intent.putExtra("title", bt0.getText());
        startActivity(intent);
//        UIHelper.goToAct(context, DeviceListActivity.class);
    }

    @OnClick(R.id.bt1)
    void bt1() {
        SharedPreferencesUrls.getInstance().putString("type", "9");

        Intent intent = new Intent();
        intent.setClass(context, DeviceListActivity.class);
//        intent.putExtra("isChange",true);
        intent.putExtra("title",bt1.getText());
        startActivity(intent);
//        UIHelper.goToAct(context, DeviceListActivity.class);
    }



    @OnClick(R.id.bt2)
    void bt2() {
        SharedPreferencesUrls.getInstance().putString("type", "6");
//        UIHelper.goToAct(context, DeviceListActivity.class);

        Intent intent = new Intent();
        intent.setClass(context, DeviceListActivity.class);
        intent.putExtra("title", bt2.getText());
        startActivity(intent);
    }

    @OnClick(R.id.bt3)
    void bt3() {
        SharedPreferencesUrls.getInstance().putString("type", "5");
//        UIHelper.goToAct(context, DeviceList3Activity.class);

        Intent intent = new Intent();
        intent.setClass(context, DeviceListActivity.class);
        intent.putExtra("title", bt3.getText());
        startActivity(intent);
    }

    @OnClick(R.id.bt4)
    void bt4() {

//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        if (access_token == null || "".equals(access_token)){
//            com.qimalocl.manage.core.common.UIHelper.goToAct(context,LoginActivity.class);
//            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//        }else {
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
//                SharedPreferencesUrls.getInstance().putString("type", "7");
//
//                Intent intent = new Intent();
//                intent.setClass(context, ActivityScanerCode.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("isChangeKey",false);
//                intent.putExtra("isAdd",true);
//                startActivityForResult(intent, 1);
//
//            } catch (Exception e) {
//                com.qimalocl.manage.core.common.UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
//            }
//        }
    }

    @OnClick(R.id.bt5)
    void bt5() {
        SharedPreferencesUrls.getInstance().putString("type", "11");
//        UIHelper.goToAct(context, DeviceList3Activity.class);

        Intent intent = new Intent();
//        intent.setClass(context, PowerSelectActivity.class);
        intent.setClass(context, LockStorageTBTDActivity.class);
        intent.putExtra("title", bt5.getText());
        startActivity(intent);
    }

    @OnClick(R.id.bt6)
    void bt6() {
        SharedPreferencesUrls.getInstance().putString("type", "12");
//        UIHelper.goToAct(context, DeviceList3Activity.class);

        Intent intent = new Intent();
        intent.setClass(context, PowerSelectActivity.class);
        intent.putExtra("title", bt6.getText());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
            }
        }
    };

}