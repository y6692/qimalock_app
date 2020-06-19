package com.qimalocl.manage.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.fitsleep.sunshinelibrary.utils.IntentUtils;
import com.fitsleep.sunshinelibrary.utils.Logger;
import com.qimalocl.manage.base.MPermissionsActivity;
import com.fitsleep.sunshinelibrary.utils.ToolsUtils;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseApplication;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.utils.LogUtil;
import com.qimalocl.manage.utils.ParseLeAdvData;
import com.qimalocl.manage.utils.SortComparator;
import com.qimalocl.manage.utils.ToastUtil;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.tbit.tbitblesdk.Bike.TbitBle;
import com.tbit.tbitblesdk.Bike.model.BikeState;
import com.tbit.tbitblesdk.Bike.services.command.callback.StateCallback;
import com.tbit.tbitblesdk.Bike.util.BikeUtil;
import com.tbit.tbitblesdk.bluetooth.scanner.ScanBuilder;
import com.tbit.tbitblesdk.bluetooth.scanner.ScannerCallback;
import com.tbit.tbitblesdk.bluetooth.scanner.decorator.FilterNameCallback;
import com.tbit.tbitblesdk.bluetooth.scanner.decorator.LogCallback;
import com.tbit.tbitblesdk.bluetooth.scanner.decorator.NoneRepeatCallback;
import com.tbit.tbitblesdk.protocol.Packet;
import com.tbit.tbitblesdk.protocol.callback.PacketCallback;
import com.tbit.tbitblesdk.protocol.callback.ResultCallback;
import com.tbit.tbitblesdk.user.entity.W206State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceListActivity extends MPermissionsActivity{
//    @BindView(R.id.mainUI_title_backBtn)
//    TextView tvScan;
    @BindView(R.id.tv_scan)
    TextView tvScan;
    @BindView(R.id.bt_scan)
    ImageView btScan;
    @BindView(R.id.list_item)
    ListView listItem;
    @BindView(R.id.app_version)
    TextView appVersion;
    private List<BleDevice> mBluetoothDeviceList = new ArrayList<>();
    private ListAdapter mAdapter;
    private boolean isScan = true;
    private ParseLeAdvData parseLeAdvData;
    private Comparator comp;
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    private List<BleDevice> bleDeviceList = new ArrayList<>();
    private List<BleDevice> adapterList = new ArrayList<>();
    private List<String> macList = new ArrayList<>();
    private BleDevice bleDevice;
    private boolean isChange = false;
    private boolean isThread = true;
    private boolean isFresh = false;
    private String title;

    private long timeout = 10000;
    private float degree;

    private String type;

    private Thread thread;

    BluetoothAdapter mBluetoothAdapter;

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
        setContentView(R.layout.ui_device_list);
        ButterKnife.bind(this);

//        isChange = getIntent().getBooleanExtra("isChange", false);
        type = SharedPreferencesUrls.getInstance().getString("type", "");
        title = getIntent().getStringExtra("title");

        initWidget();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isThread){
                    try {
                        Thread.sleep(1 * 20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(isFresh){
//                      LogUtil.e("dla===thread", isFresh + "===");

                        degree = (degree+30)%360;
                        m_myHandler.sendEmptyMessage(1);
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LogUtil.e("dla===onResume", "==="+macList.size());

        if(macList.size()>0){
            macList.clear();
            adapterList.clear();
        }

        mAdapter.notifyDataSetChanged();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ToastUtil.showMessageApp(context, "您的设备不支持蓝牙4.0");
        }
        //蓝牙锁
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            ToastUtil.showMessageApp(context, "获取蓝牙失败");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
//            isPermission = false;
//            closeLoadingDialog2();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 188);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanDevice();
//                    scanDevice2();
                }
            }, 500);
        }
    }

    void scanDevice2() {
//              TbitBle.initialize(MainActivity.this, new MyProtocolAdapter());
        TbitBle.initialize(context, new SecretProtocolAdapter());

        // 添加装饰器
        // 方式一：
        // 过滤设备名字的装饰器
////      FilterNameCallback filterNameCallback = new FilterNameCallback(DEVICE_NAME, scannerCallback);
//        FilterNameCallback filterNameCallback = new FilterNameCallback("[DD-EBIKE]", scannerCallback);
//        // 确保结果非重复的装饰器
//        NoneRepeatCallback noneRepeatCallback = new NoneRepeatCallback(filterNameCallback);
//        // 收集日志的装饰器，这个最好放在最外层包裹
//        LogCallback logCallback = new LogCallback(noneRepeatCallback);
//
//
//
//        // 方式二：(与上述效果相同)
//        ScanBuilder builder = new ScanBuilder(scannerCallback);
//        ScannerCallback decoratedCallback = builder
//                .setFilter("[DD-EBIKE]")
//                .setRepeatable(false)
//                .setLogMode(true)
//                .build();
//
//        // 开始扫描(目前同一时间仅支持启动一个扫描),返回状态码
//        int code = TbitBle.startScan(decoratedCallback, 10000);

//        int code = TbitBle.startScan(scannerCallback, 10000);
//
//        machineId = "003486809";    //===CGFDV0ETMGTWGHUB

        TbitBle.connect("003486809", "83869c8326fb65283df234855ce3c481a0d3227799bc15be8ac76a0eece6362a", new ResultCallback() {
            @Override
            public void onResult(int resultCode) {
                // 连接回应
                LogUtil.e("connect===onResult", resultCode+"===");


                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //// 解锁
                        TbitBle.unlock(new ResultCallback() {
                            @Override
                            public void onResult(int resultCode) {
                                // 解锁回应

                                LogUtil.e("unlock===onResult", resultCode+"===");
                            }
                        });
                    }
                });

//
//// 上锁
//            TbitBle.lock(new ResultCallback() {
//                @Override
//                public void onResult(int resultCode) {
//                    // 上锁回应
//                }
//            });


            }
        }, new StateCallback() {
            @Override
            public void onStateUpdated(BikeState bikeState) {
                LogUtil.e("connect=onStateUpdated", bikeState+"===");


                // 连接成功状态更新
                // 通过车辆状态获取设备特定信息
                W206State w206State = (W206State) TbitBle.getConfig().getResolver().resolveCustomState(bikeState);
            }
        });
    }

    ScannerCallback scannerCallback = new ScannerCallback() {
        @Override
        public void onScanStart() {
            LogUtil.e("ma===", "onScanStart: ");
        }

        @Override
        public void onScanStop() {
            LogUtil.e("ma===", "onScanStop: ");
        }

        @Override
        public void onScanCanceled() {
            LogUtil.e("ma===", "onScanCanceled: ");
        }

        @Override
        public void onDeviceFounded(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {

            LogUtil.e("ma===onDeviceFounded", bluetoothDevice+"==="+bytes+"==="+TbitBle.getBleConnectionState());

            String machineId = BikeUtil.resolveMachineIdByAdData(bytes);



            if (!TextUtils.isEmpty(machineId)) {
//                        showLog("扫描到设备: " + bluetoothDevice.getAddress()+ " | " + machineId);
                LogUtil.e("ma===onDeviceFounded1", machineId+"==="+bluetoothDevice.getName()+"==="+bluetoothDevice.getAddress()+"==="+bluetoothDevice.getUuids());


//                TbitBle.commonCommand((byte)0x03, (byte)0x02, new Byte[]{0x01}, new ResultCallback() {
//                    @Override
//                    public void onResult(int resultCode) {
//                        // 发送状态回复
//                        LogUtil.e("connect===onResult", resultCode+"===");
//                    }
//                }, new PacketCallback() {
//                    @Override
//                    public void onPacketReceived(Packet packet) {
//                        // 收到packet回复
//                        LogUtil.e("connect=onPacketRec", packet+"===");
//                    }
//                });

//                003486809===CGFDV0ETMGTWGHUB
                machineId = "003486809";    //===CGFDV0ETMGTWGHUB

                TbitBle.connect(machineId, "83869c8326fb65283df234855ce3c481a0d3227799bc15be8ac76a0eece6362a", new ResultCallback() {
                    @Override
                    public void onResult(int resultCode) {
                        // 连接回应
                        LogUtil.e("connect===onResult", resultCode+"===");
                    }
                }, new StateCallback() {
                    @Override
                    public void onStateUpdated(BikeState bikeState) {
                        LogUtil.e("connect=onStateUpdated", bikeState+"===");


                        // 连接成功状态更新
                        // 通过车辆状态获取设备特定信息
                        W206State w206State = (W206State) TbitBle.getConfig().getResolver().resolveCustomState(bikeState);
                    }
                });

            }
        }
    };

    private void initWidget() {
        parseLeAdvData = new ParseLeAdvData();
        comp = new SortComparator();
        tvScan.setText(title);
//        appVersion.setText("Version:" + ToolsUtils.getVersion(getApplicationContext()));
        mAdapter = new ListAdapter();
        listItem.setAdapter(mAdapter);
        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseApplication.getInstance().getIBLE().stopScan();
                BleDevice bluetoothDevice = adapterList.get(position);
                String name = bluetoothDevice.getDevice().getName();
                if (TextUtils.isEmpty(name)) {
                    name = "null";
                }
                String address = bluetoothDevice.getDevice().getAddress();
                if (name.equals("NokeLockOAD")) {
                    GlobalParameterUtils.getInstance().setUpdate(true);
                } else {
                    GlobalParameterUtils.getInstance().setUpdate(false);
                }

                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("mac", address);
//                bundle.putString("type", "2");

                LogUtil.e("dla===", "==="+isChange);

                if("t".equals(BaseApplication.mode)){
                    IntentUtils.startActivity(DeviceListActivity.this, LockStorageTestActivity.class, bundle);
                }else{
                    IntentUtils.startActivity(DeviceListActivity.this, LockStorageActivity.class, bundle);
                }


//                if(isChange){
//                    BaseApplication.getInstance().getIBLE().setChangKey(true);
//                    BaseApplication.getInstance().getIBLE().setChangPsd(true);
//                    IntentUtils.startActivity(DeviceListActivity.this, LockStorageActivity.class, bundle);
//                }else{
//                    BaseApplication.getInstance().getIBLE().setChangKey(false);
//                    BaseApplication.getInstance().getIBLE().setChangPsd(false);
//                    IntentUtils.startActivity(DeviceListActivity.this, LockStorage2Activity.class, bundle);
//                }

            }
        });
//        new Thread(new DeviceThread()).start();



    }

    private void setScanRule() {

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
//				  .setDeviceMac(address)                  // 只扫描指定mac的设备，可选
//                .setAutoConnect(true)                 // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(timeout)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    void scan(){
//        loadingDialog = DialogUtils.getLoadingDialog(context, "正在搜索...");
//		loadingDialog.setTitle("正在搜索");
//		loadingDialog.show();

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
//                mDeviceAdapter.clearScanDevice();
//                mDeviceAdapter.notifyDataSetChanged();
//                img_loading.startAnimation(operatingAnim);
//                img_loading.setVisibility(View.VISIBLE);
//                btn_scan.setText(getString(R.string.stop_scan));
                LogUtil.e("dla===onScanStarted", macList.size()+"==="+adapterList.size()+"==="+success);

                if(macList.size()>0){
                    macList.clear();
                    adapterList.clear();
                }
            }

            @Override
            public void onLeScan(BleDevice device) {
                super.onLeScan(device);

                LogUtil.e("dla===onLeScan", type+"==="+device.getName()+"==="+device.getMac()+"==="+device.getDevice()+"==="+macList.contains(device.getMac())+"==="+macList.contains("03:92:63:60:9B:3C"));

//                if (!bluetoothDeviceList.contains(device)){
//                    bluetoothDeviceList.add(device);
//                    bleDevice = new BleDevice(device, scanRecord, rssi);
//                    bleDevice = new BleDevice(device);
//                    bleDeviceList.add(bleDevice);
//                }

                if (device.getName()!=null && !macList.contains(device.getMac()) && ( ("2".equals(type) && device.getName().startsWith("NokeLock")) || ("9".equals(type) && device.getName().startsWith("SNLock-")) || (("5".equals(type) || "6".equals(type)) && device.getName().startsWith("bike:")) ) ) {

                    LogUtil.e("dla===onLeScan2", device+"==="+device.getMac()+"==="+adapterList.contains(device));

                    macList.add(device.getMac());

                    adapterList.add(device);
//                    bleDevice = new BleDevice(device);
//                    bleDeviceList.add(bleDevice);

//                    mAdapter.notifyDataSetChanged();

                    handler.sendEmptyMessage(0);
                }



//                public BleDevice(BluetoothDevice device, int rssi, byte[] scanRecord, long timestampNanos) {

            }

            @Override
            public void onScanning(final com.clj.fastble.data.BleDevice bleDevice) {
//                mDeviceAdapter.addDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();

                LogUtil.e("dla===onScanning", bleDevice+"==="+bleDevice.getMac());


            }

            @Override
            public void onScanFinished(List<com.clj.fastble.data.BleDevice> scanResultList) {
//                img_loading.clearAnimation();
//                img_loading.setVisibility(View.INVISIBLE);
//                btn_scan.setText(getString(R.string.start_scan));

                isFresh = false;

                LogUtil.e("dla===onScanFinished", scanResultList+"==="+scanResultList.size());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        BleManager.getInstance().cancelScan();

//        TbitBle.destroy();

        LogUtil.e("dla===onDestroy", "===");

        if(macList.size()>0){
            macList.clear();
            adapterList.clear();
        }

        isFresh = false;
        isThread = false;

        if(thread!=null){
            thread.interrupt();
        }


        m_myHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
    }




    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("dla===requestCode", requestCode+"==="+resultCode+"==="+data);

                switch (requestCode) {


                    case 188:

                        if (resultCode == RESULT_OK) {
//                            closeLoadingDialog();

                            LogUtil.e("dla===188", requestCode+"==="+resultCode+"==="+data);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    scanDevice();
                                }
                            }, 500);

//                            isPermission = true;
//
//                            if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                loadingDialog.setTitle("正在唤醒车锁");
//                                loadingDialog.show();
//                            }
//
//                            LogUtil.e("188===", isAgain+"==="+isConnect+"==="+isLookPsdBtn+"==="+oid+"==="+m_nowMac+"==="+type+">>>"+isOpenLock+"==="+isEndBtn);
//
//                            initParams();
//
//
//                            LogUtil.e("188===order", isAgain+"==="+isLookPsdBtn+"==="+oid+"==="+m_nowMac+"==="+type+">>>"+isOpenLock+"==="+isEndBtn);
//
//
//                            open_lock();

                        }else{
                            ToastUtil.showMessageApp(context, "需要打开蓝牙");

//                            LogUtil.e("188===fail", oid+"===");

//                            if(popupwindow!=null){
//                                popupwindow.dismiss();
//                            }
//
//                            closeLoadingDialog2();

                        }
                        break;


                    default:

                        break;

                }
            }
        });

    }



    @OnClick(R.id.mainUI_title_backBtn)
    void back() {
//        BleManager.getInstance().cancelScan();
        finish();
    }

    @OnClick(R.id.bt_scan)
    void scanDevice() {
//        macList.clear();
//        adapterList.clear();


        isFresh = true;


//        BleManager.getInstance().cancelScan();
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(10, 5000)
                .setConnectOverTime(timeout)
                .setOperateTimeout(10000);

        setScanRule();
        scan();

        if (isScan) {


//            mBluetoothDeviceList.clear();
//            requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

//    Thread thread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//
//            while (isThread){
//
//                try {
//                    Thread.sleep(1 * 20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//
//
//                if(isFresh){
////                    LogUtil.e("dla===thread", isFresh + "===");
//
//                    degree = (degree+30)%360;
//                    m_myHandler.sendEmptyMessage(1);
//                }
//
//            }
//
//        }
//    });

    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    break;

                case 1:
                    btScan.setPivotX(btScan.getWidth()/2);
                    btScan.setPivotY(btScan.getHeight()/2);//支点在图片中心
                    btScan.setRotation(degree);

                    break;


                default:
                    break;
            }
            return false;
        }
    });

//    @Override
//    public void permissionSuccess(int requestCode) {
//        super.permissionSuccess(requestCode);
//        if (101 == requestCode) {
//            Logger.e(getClass().getSimpleName(), "申请成功了");
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
////                    tvScan.setText(R.string.search_over);
//                    isScan = true;
//                    BaseApplication.getInstance().getIBLE().stopScan();
//                }
//            }, 3000);
////            tvScan.setText(R.string.Searching);
//            isScan = false;
//            bluetoothDeviceList.clear();
//            adapterList.clear();
//            if (mAdapter != null) {
//                mAdapter.notifyDataSetChanged();
//            }
//            BaseApplication.getInstance().getIBLE().startScan(this);
//        }
//    }
//
//    @Override
//    public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
////        if (rssi > -75 && !bluetoothDeviceList.contains(device)) {
////        if (!bluetoothDeviceList.contains(device)){
////            bluetoothDeviceList.add(device);
////            bleDevice = new BleDevice(device, scanRecord, rssi);
////            bleDeviceList.add(bleDevice);
////        }
//    }
    private boolean parseAdvData(int rssi, byte[] scanRecord) {
        byte[] bytes = ParseLeAdvData.adv_report_parse(ParseLeAdvData.BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA, scanRecord);
        if(bytes==null || bytes.length==0){
            return false;
        }else{
            if (bytes[0] == 0x01 && bytes[1] == 0x02) {
                return true;
            }
        }

        return false;
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Collections.sort(adapterList, comp);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    /**
     * 填充器
     */
    private class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return adapterList.size() > 5 ? 5 : adapterList.size();
        }

        @Override
        public Object getItem(int position) {
            return adapterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(DeviceListActivity.this, R.layout.device_list, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BleDevice device = adapterList.get(position);
            viewHolder.listRiss.setText("" + device.getRssi());
            viewHolder.listAddress.setText("" + device.getDevice().getAddress());
            viewHolder.listName.setText("" + device.getDevice().getName());
            return convertView;
        }

        class ViewHolder {
            TextView listRiss;
            TextView listAddress;
            TextView listName;

            ViewHolder(View view) {
                listName = (TextView) view.findViewById(R.id.list_name);
                listAddress = (TextView) view.findViewById(R.id.list_address);
                listRiss = (TextView) view.findViewById(R.id.list_riss);
            }
        }
    }


    class DeviceThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                if (bleDeviceList.size() > 0) {
//                    BleDevice bleDevice = bleDeviceList.get(0);
//                    if (null != bleDevice && parseAdvData(bleDevice.getRiss(), bleDevice.getScanBytes())) {
//                        adapterList.add(bleDevice);
//                        handler.sendEmptyMessage(0);
//                    }
//                    bleDeviceList.remove(0);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        LogUtil.e("onKeyDown===", "==="+keyCode);

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            finishMine();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}