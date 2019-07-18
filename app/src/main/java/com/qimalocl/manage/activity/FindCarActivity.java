package com.qimalocl.manage.activity;

import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qimalocl.manage.R;
import com.xiaoantech.sdk.XiaoanBleApiClient;
import com.xiaoantech.sdk.ble.scanner.ScanResult;
import com.xiaoantech.sdk.listeners.BleStateChangeListener;
import com.xiaoantech.sdk.listeners.ScanResultCallback;
import com.xiaoantech.sdk.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;

import static com.xiaoantech.sdk.utils.Util.convertAddrToImei;

/**
 * Created by xunce on 2018/12/5.
 */

public class FindCarActivity extends ListActivity implements BleStateChangeListener,
        ScanResultCallback {
    private XiaoanBleApiClient apiClient;
    private DeviceAdapter mDeviceAdapter;

    private double kalmanP = -1, kalmanRssi = 0;
    private HashMap<String, Pair<Double, Double>> mRssiMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getListView().setDivider(null);
        getListView().setDividerHeight(30);

        mDeviceAdapter = new DeviceAdapter();
        setListAdapter(mDeviceAdapter);
        XiaoanBleApiClient.Builder builder = new XiaoanBleApiClient.Builder(this);
        builder.setBleStateChangeListener(this);
        builder.setScanResultCallback(this);
        builder.setReadRssiInterval(1000);
        apiClient = builder.build();
    }

    private void start() {
        if (apiClient != null) {
            apiClient.connectToIMEI("", false);
        }
    }

    private void stop() {
        if (apiClient != null) {
            apiClient.stopScan();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    public void onConnect(BluetoothDevice device) {

    }

    @Override
    public void onDisConnect(BluetoothDevice device) {

    }

    @Override
    public void onDeviceReady(BluetoothDevice device) {

    }

    @Override
    public void onReadRemoteRssi(int rssi) {

    }

    @Override
    public void onError(BluetoothDevice device, String message, int errorCode) {

    }

    @Override
    public void onBleAdapterStateChanged(int state) {

    }

    @Override
    public void onResult(ScanResult result) {
        DeviceItem item = new DeviceItem();

        //handle result with kalman filter
        String address = result.getDevice().toString();
        String imei = convertAddrToImei(address);

        double kalmanP = 10;
        double kalmanRssi = result.getRssi();
        if (mRssiMap.containsKey(imei)) {
            Pair<Double, Double> kalmanPair = mRssiMap.get(imei);
            kalmanP = kalmanPair.first;
            kalmanRssi = kalmanPair.second;
        }

        Util.Kalman kalman = new Util.Kalman(kalmanP, 9, 100, kalmanRssi);
        double newKalmanRssi = kalman.KalmanFilter(result.getRssi());

        kalmanP = kalman.getP();
        mRssiMap.put(imei, new Pair<Double, Double>(kalmanP, newKalmanRssi));

        // and show
        item.setDevName(imei);
        item.setDevRssi((int) newKalmanRssi);

        double delta = newKalmanRssi - kalmanRssi;
        if (delta < -1f) {
            item.setDevStatus("正在远离");
        } else if (delta > 1f) {
            item.setDevStatus("正在靠近");
        }

        mDeviceAdapter.addDevice(item);
        mDeviceAdapter.notifyDataSetChanged();
    }

    private class DeviceAdapter extends BaseAdapter {
        private ArrayList<DeviceItem> mDevices;
        private LayoutInflater mInflator;

        public DeviceAdapter() {
            super();
            mDevices = new ArrayList<>();
            mInflator = getLayoutInflater();
        }

        public void addDevice(DeviceItem device) {
            int index = mDevices.indexOf(device);
            if(index < 0) {
                mDevices.add(device);
            } else {
                DeviceItem item = mDevices.get(index);
                item.setDevRssi(device.devRssi);
                item.setDevStatus(device.devStatus);
            }
        }

        public DeviceItem getDevice(int position) {
            return mDevices.get(position);
        }

        public void clear() {
            mDevices.clear();
        }

        @Override
        public int getCount() {
            return mDevices != null ?  mDevices.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.findcar_item, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = convertView.findViewById(R.id.device_name);
                viewHolder.rssi = convertView.findViewById(R.id.device_rssi);
                viewHolder.deviceStatus = convertView.findViewById(R.id.device_status);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            DeviceItem deviceItem = mDevices.get(position);
            if (deviceItem == null) return null;
            viewHolder.deviceName.setText(deviceItem.devName);
            viewHolder.deviceStatus.setText(deviceItem.devStatus);
            viewHolder.rssi.setText(deviceItem.devRssi);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView rssi;
        TextView deviceStatus;
    }

    static class DeviceItem {
        private String devName;
        private String devRssi;
        private String devStatus;

        public String getDevName() {
            return devName;
        }

        public void setDevName(String devName) {
            this.devName = devName;
        }

        public String getDevRssi() {
            return devRssi;
        }

        public void setDevRssi(String devRssi) {
            this.devRssi = devRssi;
        }

        public void setDevRssi(int devRssi) {
            this.devRssi = String.valueOf(devRssi);
        }

        public String getDevStatus() {
            return devStatus;
        }

        public void setDevStatus(String devStatus) {
            this.devStatus = devStatus;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DeviceItem item = (DeviceItem) o;

            return devName.equals(item.devName);
        }

        @Override
        public int hashCode() {
            return devName.hashCode();
        }
    }
}
