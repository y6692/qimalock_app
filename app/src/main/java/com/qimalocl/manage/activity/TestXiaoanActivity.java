package com.qimalocl.manage.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.qimalocl.manage.R;
import com.xiaoantech.sdk.XiaoanBleApiClient;
import com.xiaoantech.sdk.ble.model.Response;
import com.xiaoantech.sdk.ble.scanner.ScanResult;
import com.xiaoantech.sdk.listeners.BleCallback;
import com.xiaoantech.sdk.listeners.BleStateChangeListener;
import com.xiaoantech.sdk.listeners.ScanResultCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class TestXiaoanActivity extends AppCompatActivity implements BleStateChangeListener,
        ScanResultCallback {

    @BindView(R.id.mainUI_title_titleText)
    TextView titleText;

    private XiaoanBleApiClient apiClient;
    private boolean isConnect = false;
    private TextView statusTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_xiaoan);
        ButterKnife.bind(this);

        statusTxt = findViewById(R.id.statusTxt);
//        titleText = findViewById(R.id.mainUI_title_titleText);
//
        titleText.setText("锁的信息");

        XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(this);
        builder.setBleStateChangeListener(this);
        builder.setScanResultCallback(this);
        apiClient = builder.build();
    }

    @OnClick(R.id.mainUI_title_backBtn)
    void back() {
        finish();
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH})
    public void connectDevice(String imei) {
        if (apiClient != null) {
            apiClient.connectToIMEI(imei);
        }
    }

    public void getStatusClick(View view){
        apiClient.getStatus(new BleCallback() {
            @Override
            public void onResponse(Response response) {
                Log.d("status: ", response.toString());
            }
        });
    }
    public void rebootClick(View view){
        apiClient.restart(new BleCallback() {
            @Override
            public void onResponse(Response response) {
                Log.d("reboot: ", response.toString());
            }
        });
    }
    public void shutdownClick(View view){
        apiClient.shutdown(new BleCallback() {
            @Override
            public void onResponse(Response response) {
                Log.d("shutdown", response.toString());
            }
        });
    }
    public void findcarClick(View view){
        apiClient.findCar(new BleCallback() {
            @Override
            public void onResponse(Response response) {
                Log.d("findcar", response.toString());
            }
        });
    }
    public void accClick(View view){
        final Switch btnSW = (Switch) view;
        apiClient.setAcc(btnSW.isChecked(), new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                TestXiaoanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("acc : ", response.toString());
                    }
                });
            }
        });
    }

    public void fenceClick(final View view){
        final Switch btnSW = (Switch) view;
        apiClient.setDefend(btnSW.isChecked(), new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                TestXiaoanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("defend: ", response.toString());
                    }
                });
            }
        });
    }
    public void seatClick(final View view){
        final Switch btnSW = (Switch) view;
        apiClient.setSaddle(btnSW.isChecked(), new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                TestXiaoanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("seat: ", response.toString());
                    }
                });
            }
        });
    }
    public void wheelClick(final View view){
        final Switch btnSW = (Switch) view;
        apiClient.setBackWheel(btnSW.isChecked(), new BleCallback() {
            @Override
            public void onResponse(final Response response) {
                TestXiaoanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("wheel: ", response.toString());
                    }
                });
            }
        });
    }

    public void connectClick(View v) {
        TextView imeiTxt = findViewById(R.id.imeiTxt);
        String imei = imeiTxt.getText().toString();
        if (TextUtils.isEmpty(imei) || imei.trim().length() != 15) {
            Toast.makeText(this, "imei号错误", Toast.LENGTH_SHORT).show();
            return;
        }
        MainActivityPermissionsDispatcher.connectDeviceWithPermissionCheck(this, imei);
    }

    public void disconnectClick(View v) {
        apiClient.disConnect();
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        isConnect = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (statusTxt != null) {
                    statusTxt.setText("已连接");
                }
            }
        });
    }

    @Override
    public void onDisConnect(BluetoothDevice device) {
        isConnect = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (statusTxt != null) {
                    statusTxt.setText("未连接");
                }
            }
        });
    }

    @Override
    public void onError(BluetoothDevice device, String message, int errorCode) {

    }

    @Override
    public void onBleAdapterStateChanged(int state) {

    }

    @Override
    public void onDeviceReady(BluetoothDevice device) {
        //表明服务发现完毕，可以进行操作了。
    }

    @Override
    public void onReadRemoteRssi(int rssi) {

    }

    @Override
    public void onResult(ScanResult result) {
        Log.d("scanresult : ", result.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (apiClient != null) {
            apiClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiClient != null) {
            apiClient.onDestroy();
        }
    }

    public void bleFindCar(View view) {
        Intent intent = new Intent(this, FindCarActivity.class);
        startActivity(intent);
    }

//    public void bleOpenBatBox(View view) {
//        Intent intent = new Intent(this, OpenBoxActivity.class);
//        startActivity(intent);
//    }
}
