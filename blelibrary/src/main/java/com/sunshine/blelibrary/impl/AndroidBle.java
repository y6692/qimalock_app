package com.sunshine.blelibrary.impl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.fitsleep.sunshinelibrary.utils.ConvertUtils;
import com.fitsleep.sunshinelibrary.utils.EncryptUtils;
import com.fitsleep.sunshinelibrary.utils.Logger;
import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.impl.AQ;
import com.sunshine.blelibrary.dispose.impl.Battery;
import com.sunshine.blelibrary.dispose.impl.CloseLock;
import com.sunshine.blelibrary.dispose.impl.Key;
import com.sunshine.blelibrary.dispose.impl.LockResult;
import com.sunshine.blelibrary.dispose.impl.LockStatus;
import com.sunshine.blelibrary.dispose.impl.OpenLock;
import com.sunshine.blelibrary.dispose.impl.Password;
import com.sunshine.blelibrary.dispose.impl.TY;
import com.sunshine.blelibrary.dispose.impl.Token;
import com.sunshine.blelibrary.dispose.impl.UpdateVersion;
import com.sunshine.blelibrary.dispose.impl.Xinbiao;
import com.sunshine.blelibrary.inter.IBLE;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.mode.BatteryTxOrder;
import com.sunshine.blelibrary.mode.GetLockStatusTxOrder;
import com.sunshine.blelibrary.mode.GetTokenTxOrder;
import com.sunshine.blelibrary.mode.KeyTxOrder;
import com.sunshine.blelibrary.mode.OpenLockTxOrder;
import com.sunshine.blelibrary.mode.Order;
import com.sunshine.blelibrary.mode.PasswordTxOrder;
import com.sunshine.blelibrary.mode.ResetAQTxOrder;
import com.sunshine.blelibrary.mode.ResetLockTxOrder;
import com.sunshine.blelibrary.mode.TxOrder;
import com.sunshine.blelibrary.mode.UpddateVersionTxOrder;
import com.sunshine.blelibrary.mode.XinbiaoTxOrder;
import com.sunshine.blelibrary.service.BLEService;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.fitsleep.sunshinelibrary.utils.EncryptUtils.Encrypt;

/**
 * 作者：LiZhao
 * 时间：2017.2.8 11:48
 * 邮箱：44493547@qq.com
 * 备注：
 */
public class AndroidBle implements IBLE {

    private static final String TAG = AndroidBle.class.getSimpleName();
    private BLEService mBLEService;
    private final BluetoothAdapter mBluetoothAdapter;
    private OnDeviceSearchListener mOnDeviceSearchListener;
    private BluetoothGatt mBluetoothGatt;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private OnConnectionListener mOnConnectionListener;
    private BluetoothGattCharacteristic read_characteristic;
    private BluetoothGattCharacteristic write_characteristic;
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    private Token mToken;

    private BluetoothGattCharacteristic OAD_READ;
    private BluetoothGattCharacteristic OAD_WRITE;
    private boolean isChangeKey = false;
    private boolean isChangePsd = false;

    public AndroidBle(BLEService bleService) {
        this.mBLEService = bleService;
        final BluetoothManager bluetoothManager = (BluetoothManager) mBLEService.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
        GlobalParameterUtils.getInstance().setContext(mBLEService.getApplicationContext());
        mToken = new Token();
        Battery battery = new Battery();
        OpenLock openLock = new OpenLock();
        TY ty = new TY();
        CloseLock closeLock = new CloseLock();
        LockStatus lockStatus = new LockStatus();
        Password password = new Password();
        Key key = new Key();
        LockResult lockResult = new LockResult();
        AQ aq = new AQ();
        UpdateVersion updateVersion = new UpdateVersion();
        Xinbiao xinbiao = new Xinbiao();

        mToken.nextHandler = battery;
        battery.nextHandler = openLock;
        openLock.nextHandler = ty;
        ty.nextHandler = closeLock;
        closeLock.nextHandler = lockStatus;
        lockStatus.nextHandler = password;
        password.nextHandler = key;
        key.nextHandler = lockResult;
        lockResult.nextHandler = aq;
        aq.nextHandler = updateVersion;
        updateVersion.nextHandler = xinbiao;
    }

    @Override
    public void setDebug(boolean isOpen) {

    }
    @Override
    public void setChangKey(boolean isChange) {
        isChangeKey = isChange;
    }

    @Override
    public void setChangPsd(boolean isChange) {
        isChangePsd = isChange;
    }

    @Override
    public void startScan(OnDeviceSearchListener onDeviceSearchListener) {
        bluetoothDeviceList.clear();
        if (mBluetoothAdapter == null) return;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        this.mOnDeviceSearchListener = onDeviceSearchListener;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
//        UUID[] myUUId = {Config.bltServerUUID};
//        mBluetoothAdapter.startLeScan(myUUId,mLeScanCallback);
    }

    @Override
    public void stopScan() {
        if (mBluetoothAdapter == null) return;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    @Override
    public void connect(String address, OnConnectionListener onConnectionListener) {
        if (null == onConnectionListener) return;
        this.mOnConnectionListener = onConnectionListener;

        if (TextUtils.isEmpty(address) || mBluetoothAdapter == null) {
            mOnConnectionListener.onDisconnect(Config.OBJECT_EMPTY);
            return;
        }

        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
        if (null == bluetoothDevice) {
            mOnConnectionListener.onDisconnect(Config.OBJECT_EMPTY);
            return;
        }

        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mBluetoothGatt = bluetoothDevice.connectGatt(mBLEService, false, mBluetoothGattCallback);
    }

    @Override
    public void writeByte(byte[] bytes) {
        if (mBluetoothGatt == null || write_characteristic == null) {
            return;
        }

        Log.e("writeByte===", "==="+isChangeKey);

        byte[] miwen = null;
        switch (GlobalParameterUtils.getInstance().getLockType()) {
            case MTS:
                if (isChangeKey){
                    miwen = Encrypt(bytes, Config.newKey);
                }else {
                    miwen = Encrypt(bytes, Config.key);
                }
                break;
            case YXS:
//                miwen = Encrypt(bytes, Config.yx_key);
                break;
        }
        if (miwen != null) {
            write_characteristic.setValue(miwen);
            mBluetoothGatt.writeCharacteristic(write_characteristic);
            Logger.e(AndroidBle.class.getSimpleName(), ConvertUtils.bytes2HexString(bytes));
        }

    }

    @Override
    public boolean getToken() {
        return writeObject(new GetTokenTxOrder());
    }

    @Override
    public boolean getBattery() {
        return writeObject(new BatteryTxOrder());
    }

    @Override
    public boolean openLock() {
        return writeObject(new OpenLockTxOrder(isChangePsd));
    }

    @Override
    public boolean resetLock() {
        return writeObject(new ResetLockTxOrder());
    }

    @Override
    public boolean xinbiao() { return writeObject(new XinbiaoTxOrder()); }

    @Override
    public boolean getLockStatus() {
        return writeObject(new GetLockStatusTxOrder());
    }

    @Override
    public boolean setPassword(Order.TYPE type, byte[] bytes) {
//        return false;
        return writeObject(new PasswordTxOrder(type,bytes));
    }

    @Override
    public boolean setKey(Order.TYPE type, byte[] bytes) {
        return writeObject(new KeyTxOrder(type,bytes));
    }

    @Override
    public boolean updateVersion() {
        return writeObject(new UpddateVersionTxOrder());
    }

    @Override
    public boolean writeWrite(byte[] bytes) {
        if (mBluetoothGatt == null || OAD_WRITE == null) {
            return false;
        }
        OAD_WRITE.setValue(bytes);
        return mBluetoothGatt.writeCharacteristic(OAD_WRITE);
    }

    @Override
    public boolean writeRead(byte[] bytes) {
        if (mBluetoothGatt == null || OAD_READ == null) {
            return false;
        }
        OAD_READ.setValue(bytes);
        return mBluetoothGatt.writeCharacteristic(OAD_READ);
    }


    @Override
    public void resetPasswordAndAQ() {
        writeObject(new ResetAQTxOrder());
    }

    @Override
    public void disconnect() {
        if (mBluetoothGatt == null) {
            return;
        }
//        refreshDeviceCache();
        mBluetoothGatt.disconnect();

    }

    @Override
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
//        refreshDeviceCache();
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;

    }


    /**
     * 写入指令
     *
     * @param txOrder 发送指令对象
     * @return 是否成功
     */
    private boolean writeObject(TxOrder txOrder) {
        if (mBluetoothGatt == null || write_characteristic == null) {
            return false;
        }

        byte[] miwen = null;
        switch (GlobalParameterUtils.getInstance().getLockType()) {
            case MTS:

                Log.e("writeObject===", "==="+isChangeKey);

                if (isChangeKey){
                    miwen = Encrypt(ConvertUtils.hexString2Bytes(txOrder.generateString()), Config.newKey);

                    Log.e("writeObject===2", "==="+miwen);
                }else {
                    miwen = Encrypt(ConvertUtils.hexString2Bytes(txOrder.generateString()), Config.key);

//                    miwen = Encrypt(ConvertUtils.hexString2Bytes(txOrder.generateString()), Config.newKey);

                    Log.e("writeObject===3", "==="+miwen);

                }
                break;
            case YXS:
//                miwen = Encrypt(ConvertUtils.hexString2Bytes(txOrder.generateString()), Config.yx_key);
                break;
        }
        if (miwen != null) {
            write_characteristic.setValue(miwen);
            Logger.e("发送：", txOrder.generateString());
            return mBluetoothGatt.writeCharacteristic(write_characteristic);
        }
        return false;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            if (null != mOnDeviceSearchListener) {
                mOnDeviceSearchListener.onScanDevice(device, rssi, scanRecord);
            }
        }
    };

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                disconnect();
            }
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    gatt.discoverServices();
                    if (null != mOnConnectionListener) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mOnConnectionListener.onConnect();
                            }
                        });
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Logger.e(AndroidBle.class.getSimpleName(),"断开");
                    if (null != mOnConnectionListener) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mOnConnectionListener.onDisconnect(Config.DISCONNECT);
                            }
                        });
                    }
                    gatt.close();
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (GlobalParameterUtils.getInstance().isUpdate()) {
                    BluetoothGattService service = gatt.getService(Config.OAD_SERVICE_UUID);
                    if (null != service) {
                        OAD_READ = service.getCharacteristic(Config.OAD_READ_UUID);
                        OAD_WRITE = service.getCharacteristic(Config.OAD_WRITE_UUID);
                        OAD_WRITE.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);final
                        int write_characteristic_properties = OAD_WRITE.getProperties();
                        if ((write_characteristic_properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            boolean notification = gatt.setCharacteristicNotification(OAD_WRITE, true);
                            if (notification) {
                                Logger.e(AndroidBle.class.getSimpleName(), "写入通知开启");
                            }
                            BluetoothGattDescriptor descriptor = OAD_WRITE.getDescriptor(Config.CLIENT_CHARACTERISTIC_CONFIG);
                            if (null != descriptor) {
                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(descriptor);
                            }
                        }
                    }
                } else {
                    BluetoothGattService service = gatt.getService(Config.bltServerUUID);
                    if (null != service) {
                        read_characteristic = service.getCharacteristic(Config.readDataUUID);
                        write_characteristic = service.getCharacteristic(Config.writeDataUUID);
                        int properties = read_characteristic.getProperties();
                        if ((properties | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            gatt.setCharacteristicNotification(read_characteristic, true);
                            BluetoothGattDescriptor descriptor = read_characteristic.getDescriptor(Config.CLIENT_CHARACTERISTIC_CONFIG);
                            if (null != descriptor) {
                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(descriptor);
                            }
                        }
                    }
                }
                if (null != mOnConnectionListener) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mOnConnectionListener.onServicesDiscovered(TextUtils.isEmpty(gatt.getDevice().getName())?"NokeLock":gatt.getDevice().getName(), gatt.getDevice().getAddress());
                        }
                    });
                }
            }
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (GlobalParameterUtils.getInstance().isUpdate()) {
                GlobalParameterUtils.getInstance().setBusy(false);
                mBLEService.updateBroadcast(ConvertUtils.bytes2HexString(characteristic.getValue()));
                Logger.e(TAG, "onCharacteristicChanged:" + ConvertUtils.bytes2HexString(characteristic.getValue()));
            } else {


                try {
                    byte[] values = characteristic.getValue();
                    byte[] x = new byte[16];
                    System.arraycopy(values, 0, x, 0, 16);

                    Log.e("onCharacteristicChanged", "==="+isChangeKey);

                    byte mingwen[] = null;
                    switch (GlobalParameterUtils.getInstance().getLockType()) {
                        case MTS:
                            if (isChangeKey){
                                mingwen = EncryptUtils.Decrypt(x, Config.newKey);
                            }else {
                                mingwen = EncryptUtils.Decrypt(x, Config.key);
                            }
                            break;
                        case YXS:
//                            mingwen = EncryptUtils.Decrypt(x, Config.yx_key);
                            break;
                    }
                    Logger.e(TAG, "返回：" + ConvertUtils.bytes2HexString(mingwen));
                    mToken.handlerRequest(ConvertUtils.bytes2HexString(mingwen), 0);
                } catch (Exception e) {
                    Log.e("onCharacteristicChanged", "2==="+isChangeKey);

                    byte[] values = characteristic.getValue();
                    byte[] x = new byte[16];
                    System.arraycopy(values, 0, x, 0, 16);
                    byte mingwen[];
                    if (isChangeKey){
                        mingwen = EncryptUtils.Decrypt(x, Config.newKey);
                    }else {
                        mingwen = EncryptUtils.Decrypt(x, Config.key);
                    }
                    Logger.e(TAG, "没有该指令：" + ConvertUtils.bytes2HexString(mingwen));
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

        }
    };

    @Override
    public boolean refreshCache() {
        if (mBluetoothGatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                    return bool;
                }
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
        return false;
    }

//    public boolean refreshDeviceCache(){
//        if (mBluetoothGatt != null){
//            try {
//                BluetoothGatt localBluetoothGatt = mBluetoothGatt;
//                Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
//                if (localMethod != null) {
//                    boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
//                    return bool;
//                }
//            }
//            catch (Exception localException) {
//                Logger.e("#############","An exception occured while refreshing device");
//            }
//        }
//        return false;
//    }
}
