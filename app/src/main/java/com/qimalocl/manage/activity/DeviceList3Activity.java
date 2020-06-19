package com.qimalocl.manage.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.qimalocl.manage.R;
import com.qimalocl.manage.adapters.DeviceListAdapter;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.utils.LogUtil;
import com.sofi.blelocker.library.connect.listener.BluetoothStateListener;
import com.sofi.blelocker.library.search.SearchRequest;
import com.sofi.blelocker.library.search.SearchResult;
import com.sofi.blelocker.library.search.response.SearchResponse;
import com.sofi.blelocker.library.utils.BluetoothUtils;
import com.sofi.blelocker.library.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by heyong on 2017/5/19.
 */

public class DeviceList3Activity extends Activity {
//    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private DeviceListAdapter mAdapter;
    private List<SearchResult> mDevices;
    private static final int REQ_COARSE = 1;

    @BindView(R.id.mainUI_title_backBtn)
    ImageView backBtn;
    @BindView(R.id.mainUI_title_titleText)
    TextView titleText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 修改状态栏颜色，4.4+生效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.ac_ui_device_list);
        ButterKnife.bind(this);

        mRecyclerView = findViewById(R.id.recyclerView);

        titleText.setText("设备列表");

        bindData();
        bindView();

//        setDisplayNone();

//        if(getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        }
//        setTitle("大门锁");
    }

    @OnClick(R.id.mainUI_title_backBtn)
    void back() {
        finish();
    }

    protected void bindData() {
        mDevices = new ArrayList<>();
        mAdapter = new DeviceListAdapter(this,this, mDevices);
        ClientManager.getClient().registerBluetoothStateListener(mBluetoothStateListener);
    }


    protected void bindView() {
//        setTitle(R.string.main_title);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        if (BluetoothUtils.isBluetoothEnabled()) {
            searchDevice();
        } else {
            UIHelper.showToastMsg(this, "ble_disable", R.drawable.ic_error);
        }
    }

    private void searchDevice() {

        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
//                } else {
//                    requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
//                }

                requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQ_COARSE);

                return;
            }else{
                SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
//                    .searchBluetoothClassicDevice(3000, 5)
                    .searchBluetoothLeDevice(0)
                    .build();

                ClientManager.getClient().search(request, mSearchResponse);
            }
        }

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            SearchRequest request = new SearchRequest.Builder()      //duration为0时无限扫描
////                    .searchBluetoothClassicDevice(3000, 5)
//                    .searchBluetoothLeDevice(0)
//                    .build();
//
//            ClientManager.getClient().search(request, mSearchResponse);
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_COARSE);
//        }
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            LogUtil.e("DeviceList2===", "DeviceListActivity.onSearchStarted");
            mDevices.clear();
            mAdapter.notifyDataChanged();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            LogUtil.e("DeviceList2===", "DeviceListActivity.onDeviceFounded " + device.device.getAddress());

            if (!mDevices.contains(device)) {
                mDevices.add(device);
                mAdapter.notifyItemInserted(mDevices.size());
            } else {
                int index = mDevices.indexOf(device);
                mDevices.set(index, device);

                if (StringUtils.checkBikeTag(device.getName())) {
                    mAdapter.notifyDataChanged();
                } else {
                    mAdapter.notifyItemChanged(index);
                }
            }
        }

        @Override
        public void onSearchStopped() {
            LogUtil.e("DeviceList2===","DeviceListActivity.onSearchStopped");

        }

        @Override
        public void onSearchCanceled() {
            LogUtil.e("DeviceList2===","DeviceListActivity.onSearchCanceled");

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_COARSE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SearchRequest request = new SearchRequest.Builder()      //无限扫描
                            .searchBluetoothLeDevice(0).build();

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    ClientManager.getClient().search(request, mSearchResponse);
                }
                else {
                    UIHelper.showToastMsg(this, "ble_permission_deny", R.drawable.ic_error);
                }
                break;
        }
    }


    private BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            if (openOrClosed) {
                searchDevice();
            }
            else {
                UIHelper.showToastMsg(DeviceList3Activity.this, "ble_disable", R.drawable.ic_error);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("DeviceList2===","onDestroy");
        ClientManager.getClient().unregisterBluetoothStateListener(mBluetoothStateListener);
        ClientManager.getClient().stopSearch();
    }
}
