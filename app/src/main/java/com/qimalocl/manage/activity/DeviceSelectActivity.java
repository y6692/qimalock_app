package com.qimalocl.manage.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qimalocl.manage.R;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceSelectActivity extends Activity {
    @BindView(R.id.bt1)
    Button bt1;
    @BindView(R.id.bt2)
    Button bt2;
    @BindView(R.id.bt3)
    Button bt3;
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

        titleText.setText("入库选择");
    }

    @OnClick(R.id.mainUI_title_backBtn)
    void back() {
        finish();
    }

    @OnClick(R.id.bt1)
    void bt1() {
        UIHelper.goToAct(context, DeviceListActivity.class);
    }

    @OnClick(R.id.bt2)
    void bt2() {
        SharedPreferencesUrls.getInstance().putString("type", "5");
        UIHelper.goToAct(context, DeviceList2Activity.class);
    }

    @OnClick(R.id.bt3)
    void bt3() {
        SharedPreferencesUrls.getInstance().putString("type", "6");
        UIHelper.goToAct(context, DeviceList3Activity.class);
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