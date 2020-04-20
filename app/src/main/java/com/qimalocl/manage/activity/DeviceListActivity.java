package com.qimalocl.manage.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fitsleep.sunshinelibrary.utils.IntentUtils;
import com.fitsleep.sunshinelibrary.utils.Logger;
import com.qimalocl.manage.base.MPermissionsActivity;
import com.fitsleep.sunshinelibrary.utils.ToolsUtils;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseApplication;
import com.qimalocl.manage.model.BleDevice;
import com.qimalocl.manage.utils.ParseLeAdvData;
import com.qimalocl.manage.utils.SortComparator;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceListActivity extends MPermissionsActivity implements OnDeviceSearchListener {
    @BindView(R.id.tv_scan)
    TextView tvScan;
    @BindView(R.id.bt_scan)
    TextView btScan;
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
    private BleDevice bleDevice;
    private boolean isChange = false;

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

        isChange = getIntent().getBooleanExtra("isChange", false);

        initWidget();
    }

    private void initWidget() {
        parseLeAdvData = new ParseLeAdvData();
        comp = new SortComparator();
        appVersion.setText("Version:" + ToolsUtils.getVersion(getApplicationContext()));
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
                bundle.putString("address", address);

                Log.e("DLA===", "==="+isChange);

                if(isChange){
                    BaseApplication.getInstance().getIBLE().setChangKey(true);
                    BaseApplication.getInstance().getIBLE().setChangPsd(true);
                    IntentUtils.startActivity(DeviceListActivity.this, LockStorageActivity.class, bundle);
                }else{
                    BaseApplication.getInstance().getIBLE().setChangKey(false);
                    BaseApplication.getInstance().getIBLE().setChangPsd(false);
//                    BaseApplication.getInstance().getIBLE().setChangKey(true);
//                    BaseApplication.getInstance().getIBLE().setChangPsd(true);
                    IntentUtils.startActivity(DeviceListActivity.this, LockStorage2Activity.class, bundle);
                }

            }
        });
        new Thread(new DeviceThread()).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scanDevice();
            }
        }, 500);
    }

    @OnClick(R.id.bt_scan)
    void scanDevice() {
        if (isScan) {
            mBluetoothDeviceList.clear();
            requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        if (101 == requestCode) {
            Logger.e(getClass().getSimpleName(), "申请成功了");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvScan.setText(R.string.search_over);
                    isScan = true;
                    BaseApplication.getInstance().getIBLE().stopScan();
                }
            }, 3000);
            tvScan.setText(R.string.Searching);
            isScan = false;
            bluetoothDeviceList.clear();
            adapterList.clear();
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            BaseApplication.getInstance().getIBLE().startScan(this);
        }
    }

    @Override
    public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
//        if (rssi > -75 && !bluetoothDeviceList.contains(device)) {
        if (!bluetoothDeviceList.contains(device)){
            bluetoothDeviceList.add(device);
            bleDevice = new BleDevice(device, scanRecord, rssi);
            bleDeviceList.add(bleDevice);
        }
    }
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
            viewHolder.listRiss.setText("" + device.getRiss());
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
                    BleDevice bleDevice = bleDeviceList.get(0);
                    if (null != bleDevice && parseAdvData(bleDevice.getRiss(), bleDevice.getScanBytes())) {
                        adapterList.add(bleDevice);
                        handler.sendEmptyMessage(0);
                    }
                    bleDeviceList.remove(0);
                }
            }
        }
    }
}