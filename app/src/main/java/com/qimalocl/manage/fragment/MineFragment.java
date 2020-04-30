package com.qimalocl.manage.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.BuildConfig;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.ExchangePowerRecordActivity;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.activity.LongSetgoodUnusedDetailActivity;
import com.qimalocl.manage.activity.MainActivity;
import com.qimalocl.manage.activity.MaintenanceRecordActivity;
import com.qimalocl.manage.activity.ScrappedDetailActivity;
import com.qimalocl.manage.activity.SetGoodUsedDetailActivity;
import com.qimalocl.manage.activity.SettingActivity;
import com.qimalocl.manage.activity.UnGoodUsedDetailActivity;
import com.qimalocl.manage.base.BaseApplication;
import com.qimalocl.manage.base.BaseFragment;
import com.qimalocl.manage.core.common.BitmapUtils1;
import com.qimalocl.manage.core.common.GetImagePath;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.core.widget.MarqueTextView;
import com.qimalocl.manage.model.DatasBean;
import com.qimalocl.manage.model.LowPowerBean;
import com.qimalocl.manage.model.LowPowerDetailBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.SchoolListBean;
import com.qimalocl.manage.model.ScrappedBean;
import com.qimalocl.manage.model.UserBean;
import com.qimalocl.manage.utils.ToastUtil;
import com.qimalocl.manage.utils.UtilAnim;
import com.qimalocl.manage.utils.UtilBitmap;
import com.qimalocl.manage.utils.UtilScreenCapture;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class MineFragment extends BaseFragment implements View.OnClickListener{

    private View v;
    Unbinder unbinder;

    private Context context;
    private Activity activity;

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private LoadingDialog loadingDialog;
    private ImageView rightBtn, iv_isRead;
    private ImageView backImage;
    private ImageView settingImage;
    private ImageView headerImageView;
    private ImageView authState;
    private TextView userName, roleName, tv_delivered_cars, tv_is_using_cars, tv_longtime_not_used_cars, tv_not_recycled_cars, tv_not_fixed_cars, tv_fixed_not_used_cars;


    private MarqueTextView schoolName;

    private LinearLayout ll_1, ll_2, ll_3, ll_4, ll_5, ll_6, curRouteLayout, hisRouteLayout;
    private RelativeLayout  maintenanceRecordLayout, exchangePowerRecordLayout, lowPowerLayout, scrappedLayout, changePhoneLayout, authLayout, inviteLayout;

    private TextView tv_low_power_count, tv_scrapped_count;

    private ImageView iv_popup_window_back;
    private RelativeLayout rl_popup_window;

    private Button takePhotoBtn, pickPhotoBtn, cancelBtn;
    private Bitmap upBitmap;

    private String imgUrl = Urls.uploadsheadImg;
    private String imageurl = "";
    private Uri imageUri;
    private final String IMAGE_FILE_NAME = "picture.jpg";// 照片文件名称
    private String urlpath; // 图片本地路径
    private String resultStr = ""; // 服务端返回结果集
    private final int REQUESTCODE_PICK = 0; // 相册选图标记
    private final int REQUESTCODE_TAKE = 1; // 相机拍照标记
    private final int REQUESTCODE_CUTTING = 2; // 图片裁切标记

    private String credit_scores_h5_title;
    private String credit_scores_h5_url;
    private String invite_h5_title;
    private String invite_h5_url;
    private String history_order_h5_title;
    private String history_order_h5_url;

    private PopupWindow popupwindow;

    private int ultra_low_count_xa;
    private int low_count_xa;
    private int ultra_low_count_xyt;
    private int low_count_xyt;
    private int ultra_low_count_tbt;
    private int low_count_tbt;

    String role = "";

    private boolean isLowPowerLayout;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mine, null);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        activity = getActivity();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
//            if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                Toast.makeText(context, "您的设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
//                getActivity().finish();
//            }
//            //蓝牙锁
//            BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
//
//            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
//            if (mBluetoothAdapter == null) {
//                Toast.makeText(context, "获取蓝牙失败", Toast.LENGTH_SHORT).show();
//                scrollToFinishActivity();
//                return;
//            }
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, 188);
//            }
//        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

//        scrollView = (PullToZoomScrollViewEx) getActivity().findViewById(R.id.scroll_view);
//        loadViewForCode();
//        imageWith = (int)(getActivity().getWindowManager().getDefaultDisplay().getWidth() * 0.8);

        initView();

        View view = getView();
        if (view != null) {

        }

//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                    Log.e("minef===Created", "===");
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Log.e("minef===onHiddenChanged", "==="+hidden);

        if(hidden){
            //pause
        }else{
            //resume

            String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
            if("".equals(access_token)){
                ToastUtil.showMessageApp(context, "请先登录");

                Intent intent = new Intent(BaseApplication.context, LoginActivity.class);
                startActivity(intent);
            }else{
                initHttp();
                datas();
                carbatteryaction_lowpower();
                carbadaction_scrapped();
            }

        }
    }


    private void initView(){
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        imageUri = Uri.parse("file:///sdcard/temp.jpg");
        iv_popup_window_back = getActivity().findViewById(R.id.popupWindow_back);
        rl_popup_window = getActivity().findViewById(R.id.popupWindow);

        takePhotoBtn = getActivity().findViewById(R.id.takePhotoBtn);
        pickPhotoBtn = getActivity().findViewById(R.id.pickPhotoBtn);
        cancelBtn = getActivity().findViewById(R.id.cancelBtn);

        takePhotoBtn.setOnClickListener(itemsOnClick);
        pickPhotoBtn.setOnClickListener(itemsOnClick);
        cancelBtn.setOnClickListener(itemsOnClick);

        rightBtn = getActivity().findViewById(R.id.personUI_rightBtn);
        headerImageView = getActivity().findViewById(R.id.personUI_header);
        userName = getActivity().findViewById(R.id.personUI_userName);
        schoolName = getActivity().findViewById(R.id.personUI_schoolName);
        roleName = getActivity().findViewById(R.id.personUI_roleName);
        schoolName.setSelected(true);

        iv_isRead = getActivity().findViewById(R.id.iv_isRead);

//        hisRouteLayout = getActivity().findViewById(R.id.personUI_bottom_hisRouteLayout);
//        myPurseLayout = getActivity().findViewById(R.id.personUI_bottom_myPurseLayout);
//        myRouteLayout = getActivity().findViewById(R.id.personUI_bottom_myRouteLayout);
//        actionCenterLayout = getActivity().findViewById(R.id.personUI_bottom_actionCenterLayout);

//        settingLayout = getActivity().findViewById(R.id.personUI_bottom_settingLayout);
//

        ll_1 = getActivity().findViewById(R.id.ll_1);
        ll_2 = getActivity().findViewById(R.id.ll_2);
        ll_3 = getActivity().findViewById(R.id.ll_3);
        ll_4 = getActivity().findViewById(R.id.ll_4);
        ll_5 = getActivity().findViewById(R.id.ll_5);
        ll_6 = getActivity().findViewById(R.id.ll_6);

        tv_low_power_count = getActivity().findViewById(R.id.tv_low_power_count);
        tv_scrapped_count = getActivity().findViewById(R.id.tv_scrapped_count);

//        delivered_cars	String
//        投放车辆数量
//
//        is_using_cars	String
//        正在使用车辆数量
//
//        longtime_not_used_cars	String
//        长时间未使用车辆数量
//
//        not_recycled_cars	String
//        未回收车辆数量
//
//        not_fixed_cars	String
//        未修好车辆数量
//
//        fixed_not_used_cars	String
//        修好未使用车辆数量

        tv_delivered_cars = getActivity().findViewById(R.id.tv_delivered_cars);
        tv_is_using_cars = getActivity().findViewById(R.id.tv_is_using_cars);
        tv_longtime_not_used_cars = getActivity().findViewById(R.id.tv_longtime_not_used_cars);
        tv_not_recycled_cars = getActivity().findViewById(R.id.tv_not_recycled_cars);
        tv_not_fixed_cars = getActivity().findViewById(R.id.tv_not_fixed_cars);
        tv_fixed_not_used_cars = getActivity().findViewById(R.id.tv_fixed_not_used_cars);

        maintenanceRecordLayout = getActivity().findViewById(R.id.personUI_maintenanceRecordLayout);
        exchangePowerRecordLayout = getActivity().findViewById(R.id.personUI_exchangePowerRecordLayout);
        lowPowerLayout = getActivity().findViewById(R.id.personUI_lowPowerLayout);
        scrappedLayout = getActivity().findViewById(R.id.personUI_scrappedLayout);
        changePhoneLayout = getActivity().findViewById(R.id.personUI_changePhoneLayout);
        authLayout = getActivity().findViewById(R.id.personUI_authLayout);
        inviteLayout = getActivity().findViewById(R.id.personUI_inviteLayout);


        rightBtn.setOnClickListener(this);
//        headerImageView.setOnClickListener(this);

//        ll_1.setOnClickListener(this);
//        ll_2.setOnClickListener(this);
        ll_3.setOnClickListener(this);
        ll_4.setOnClickListener(this);
        ll_5.setOnClickListener(this);
        ll_6.setOnClickListener(this);

        maintenanceRecordLayout.setOnClickListener(this);
        exchangePowerRecordLayout.setOnClickListener(this);
        lowPowerLayout.setOnClickListener(this);
        scrappedLayout.setOnClickListener(this);
        changePhoneLayout.setOnClickListener(this);
        authLayout.setOnClickListener(this);
        inviteLayout.setOnClickListener(this);


//        billRule();
    }


    @Override
    public void onResume() {
        super.onResume();
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token == null || "".equals(access_token)) {
//            superVip.setVisibility(View.GONE);
        } else {

            boolean flag = activity.getIntent().getBooleanExtra("flag", false);

            Log.e("minef===onResume", flag+"==="+SharedPreferencesUrls.getInstance().getString("access_token", ""));

            if(flag){

            }

            initHttp();


//            if (("0".equals(bikenum) || bikenum == null || "".equals(bikenum))
//                    && ("0".equals(specialdays) || specialdays == null || "".equals(specialdays))){
//                superVip.setVisibility(View.GONE);
//            }else {
//                superVip.setVisibility(View.VISIBLE);
//            }
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("minef=onActivityResult", requestCode+"==="+resultCode);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
//                    codenum = data.getStringExtra("codenum");
//                    m_nowMac = data.getStringExtra("m_nowMac");



                    ((MainActivity)getActivity()).changeTab(0);

                } else {
//                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUESTCODE_PICK:// 直接从相册获取


                if (resultCode == RESULT_OK) {

                    if (data != null) {
                        try {
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                                Log.e("minef=REQUESTCODE_PICK", Build.VERSION.SDK_INT+"==="+data.getData());

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    File imgUri = new File(GetImagePath.getPath(context, data.getData()));

                                    Log.e("minef=REQUESTCODE_PICK2", imgUri+"==="+ BuildConfig.APPLICATION_ID);

                                    Uri dataUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", imgUri);

                                    Log.e("minef=REQUESTCODE_PICK3", imgUri+"==="+dataUri);

                                    startPhotoZoom(dataUri);
                                } else {
                                    startPhotoZoom(data.getData());
                                }
                            } else {
                                Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();// 用户点击取消操作
                        }
                    }

//                    if (data != null){
//                        try {
//                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//                                if (imageUri != null) {
//                                urlpath = getRealFilePath(data.getData());
////                                urlpath  = FileUtil.getFilePathByUri(context, data.getData());
////                                urlpath = getRealFilePath(context, imageUri);
//
////                                urlpath  = "/zhuanke/firstBottomMenu/4321681icon_w_1_ORIGIN_0kjM.png";
//
////                                    Cursor cursor = null;
////
//////                                String[] proj = { MediaStore.Images.Media.DATA};
//////                                cursor = getActivity().getContentResolver().query(data.getData(), proj, null, null, null);
////                                    cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null);
////
////                                    if (cursor.moveToFirst()) {
//////                                      urlpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
////                                        urlpath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
////
////                                        Log.e("minef=REQUESTCODE_PICK0", cursor+"==="+urlpath);
////
////                                        //api>=19时，photo_path的值为null，此时再做处理
////                                        if(urlpath == null) {
////                                            String wholeID = getDocumentId(data.getData());
////                                            String id = wholeID.split(":")[1];
////                                            String[] column = { MediaStore.Images.Media.DATA };
////                                            String sel = MediaStore.Images.Media._ID +"=?";
////                                            cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[] { id }, null);
////                                            int columnIndex = cursor.getColumnIndex(column[0]);
////                                            if (cursor.moveToFirst()){
////                                                urlpath = cursor.getString(columnIndex);//此时的路径为照片路径
////                                            }
////                                        }
////                                    }
////                                    cursor.close();
//
////                                    mHandler.sendEmptyMessage(1);
//
//                                    compress(); //压缩图片
//
//                                    //                                Bitmap bitmap = BitmapFactory.decodeFile(filepath.getPath());
//                                    //                                upBitmap = BitmapFactory.decodeFile(urlpath);
//
//                                    Log.e("minef=REQUESTCODE_PICK", urlpath+"==="+imageUri+"==="+upBitmap);
//
//                                    //                                upBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uriImageview));
//
//                                    headerImageView.setImageBitmap(upBitmap);
//
//                                    Log.e("minef=REQUESTCODE_PICK3", "===");
//
//
////                                uploadImage();
//                                }
//                            }else {
//                                Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();// 用户点击取消操作
//                        }
//                    }

                } else {
//                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }



                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
//                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        //通过FileProvider创建一个content类型的Uri
//                        Uri inputUri = FileProvider.getUriForFile(context,
//                                BuildConfig.APPLICATION_ID + ".provider",
//                                new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME));
//                        startPhotoZoom(inputUri);//设置输入类型
//                    } else {
//                        File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
//                        startPhotoZoom(Uri.fromFile(temp));
//                    }
//                } else {
//                    Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
//                }

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

                    File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
                    if (Uri.fromFile(temp) != null) {
                        urlpath = getRealFilePath(Uri.fromFile(temp));
                        Log.e("REQUESTCODE_TAKE===", temp+"==="+urlpath);

//                        Uri filepath = Uri.fromFile(temp);

                        compress(); //压缩图片

                        headerImageView.setImageBitmap(upBitmap);

                        Log.e("REQUESTCODE_TAKE===3", upBitmap+"===");

//                        uploadImage();
                    }

//                            File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
//                            if (Uri.fromFile(temp) != null) {
//                                urlpath = getRealFilePath(context, Uri.fromFile(temp));
//
//                                Log.e("REQUESTCODE_TAKE===", temp+"==="+urlpath);
//
//                                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                    loadingDialog.setTitle("请稍等");
//                                    loadingDialog.show();
//                                }
//
//                                new Thread(uploadImageRunnable).start();
//                            }
                }else {
                    Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                }

                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        Log.e("minef=startPhotoZoom", imageUri+"==="+uri);

        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    public String getRealFilePath(final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
//            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);

                        Log.e("getRealFilePath===", cursor+"==="+data);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void setPicToView(Intent data) {
        Bundle extras = data.getExtras();
        if (imageUri != null) {
            urlpath = getRealFilePath(imageUri);
//            if (loadingDialog != null && !loadingDialog.isShowing()) {
//                loadingDialog.setTitle("请稍等");
//                loadingDialog.show();
//            }
//            new Thread(uploadImageRunnable).start();

            Bitmap bitmap = BitmapFactory.decodeFile(urlpath);
            headerImageView.setImageBitmap(bitmap);

//            urlpath  = FileUtil.getFilePathByUri(context, data.getData());
//            urlpath  = FileUtil.getFilePathByUri(context, extras);

            Log.e("minef===setPicToView", data.getData()+"==="+urlpath);

//            compress(); //压缩图片
//            headerImageView.setImageBitmap(upBitmap);

        }

    }

    void compress(){
        // 设置参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(urlpath, options);
        int height = options.outHeight;
        int width= options.outWidth;
        int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
//        int minLen = Math.min(height, width); // 原图的最小边长
//        if(minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
//            float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
//            inSampleSize = (int)ratio;
//        }
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
        upBitmap = BitmapFactory.decodeFile(urlpath, options); // 解码文件
    }

    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
//                    if (loadingDialog != null && loadingDialog.isShowing()) {
//                        loadingDialog.dismiss();
//                    }
//                    try {
//                        // 返回数据示例，根据需求和后台数据灵活处理
//                        JSONObject jsonObject = new JSONObject(resultStr);
//                        // 服务端以字符串“1”作为操作成功标记
//                        if (jsonObject.optString("flag").equals("Success")) {
//                            BitmapFactory.Options option = new BitmapFactory.Options();
//                            // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图，3为三分之一
//                            option.inSampleSize = 1;
//                            imageurl = jsonObject.optString("data");
////                            Glide.with(context).load(Urls.host + imageurl).asBitmap().into(headerImageView);
//                            ImageLoader.getInstance().displayImage(Urls.host + imageurl, headerImageView);
//                            Toast.makeText(context, "照片上传成功", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(context, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
//                        }
//
//                    } catch (JSONException e) {
//                    }

                    break;

                case 1:
                    Bitmap bitmap = BitmapFactory.decodeFile(urlpath);
                    headerImageView.setImageBitmap(bitmap);

//                    compress(); //压缩图片
//
//                    //                                Bitmap bitmap = BitmapFactory.decodeFile(filepath.getPath());
//                    //                                upBitmap = BitmapFactory.decodeFile(urlpath);
//
//                    Log.e("minef=REQUESTCODE_PICK", urlpath+"==="+imageUri+"==="+upBitmap);
//
//                    //                                upBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uriImageview));
//
//                    headerImageView.setImageBitmap(upBitmap);
//
//                    Log.e("minef=REQUESTCODE_PICK3", "===");

                    break;

                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 使用HttpUrlConnection模拟post表单进行文件 上传平时很少使用，比较麻烦 原理是：
     * 分析文件上传的数据格式，然后根据格式构造相应的发送给服务器的字符串。
     */
//    Runnable uploadImageRunnable = new Runnable() {
//        @Override
//        public void run() {
//
//            if (TextUtils.isEmpty(imgUrl)) {
//                Toast.makeText(context, "还没有设置上传服务器的路径！", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Map<String, String> textParams;
//            Map<String, File> fileparams;
//            try {
//                // 创建一个URL对象
//                URL url = new URL(imgUrl);
//                textParams = new HashMap<>();
//                fileparams = new HashMap<>();
//                // 要上传的图片文件
//                File file = new File(urlpath);
//                if (file.length() >= 2097152 / 2) {
//                    file = new File(BitmapUtils1.compressImageUpload(urlpath,480f,800f));
//                }
//                fileparams.put("key1", file);
//                textParams.put("uid", SharedPreferencesUrls.getInstance().getString("uid", ""));
//                textParams.put("access_token", SharedPreferencesUrls.getInstance().getString("access_token", ""));
//                // 利用HttpURLConnection对象从网络中获取网页数据
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                // 设置连接超时（记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作）
//                conn.setConnectTimeout(5000);
//                // 设置允许输出（发送POST请求必须设置允许输出）
//                conn.setDoOutput(true);
//                // 设置使用POST的方式发送
//                conn.setRequestMethod("POST");
//                // 设置不使用缓存（容易出现问题）
//                conn.setUseCaches(false);
//                conn.setRequestProperty("Charset", "UTF-8");// 设置编码
//                // 在开始用HttpURLConnection对象的setRequestProperty()设置,就是生成HTML文件头
//                conn.setRequestProperty("ser-Agent", "Fiddler");
//                // 设置contentType
//                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + NetUtil.BOUNDARY);
//                OutputStream os = conn.getOutputStream();
//                DataOutputStream ds = new DataOutputStream(os);
//                NetUtil.writeStringParams(textParams, ds);
//                NetUtil.writeFileParams(fileparams, ds);
//                NetUtil.paramsEnd(ds);
//                // 对文件流操作完,要记得及时关闭
//                os.close();
//                // 服务器返回的响应吗
//                int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
//                // 对响应码进行判断
//                if (code == 200) {// 返回的响应码200,是成功
//                    // 得到网络返回的输入流
//                    InputStream is = conn.getInputStream();
//                    resultStr = NetUtil.readString(is);
//                } else {
//                    Toast.makeText(context, "请求URL失败！", Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//
//            }
//            mHandler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
//        }
//    };




    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.CAMERA)) {

                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context,
                                        BuildConfig.APPLICATION_ID + ".fileprovider",
                                        new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                                takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            } else {
                                // 下面这句指定调用相机拍照后的照片存储的路径
                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                            }
                            startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                        } else {
                            Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")  //TODO  setType(3).
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finishMine();
                                }
                            }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                            startActivity(localIntent);
                            finishMine();
                        }
                    });
                    customBuilder.create().show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    @Override
    public void onClick(View v) {

        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token == null || "".equals(access_token)) {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
//            case R.id.personUI_backImage:
//                scrollToFinishActivity();
//                break;

            case R.id.personUI_rightBtn:
//                UIHelper.goToAct(context, SettingActivity.class);

                Intent intent = new Intent();
                intent.setClass(context, SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 10);
                break;

            case R.id.ll_4:
//                UIHelper.goToAct(context, UnGoodUsedDetailActivity.class);

                intent = new Intent();
                intent.setClass(context, UnGoodUsedDetailActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);

                break;

            case R.id.ll_5:
//                UIHelper.goToAct(context, UnGoodUsedDetailActivity.class);

                intent = new Intent();
                intent.setClass(context, UnGoodUsedDetailActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);

                break;

            case R.id.ll_3:
//                UIHelper.goToAct(context, UnGoodUsedDetailActivity.class);

                intent = new Intent();
                intent.setClass(context, LongSetgoodUnusedDetailActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);

                break;

            case R.id.ll_6:
//                UIHelper.goToAct(context, UnGoodUsedDetailActivity.class);

                intent = new Intent();
                intent.setClass(context, LongSetgoodUnusedDetailActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);

                break;

            case R.id.personUI_maintenanceRecordLayout:
                UIHelper.goToAct(context, MaintenanceRecordActivity.class);
                break;

            case R.id.personUI_exchangePowerRecordLayout:
                UIHelper.goToAct(context, ExchangePowerRecordActivity.class);
                break;

            case R.id.personUI_lowPowerLayout:
//                carbatteryaction_lowpower();

//                onStartCommon("正在加载");

                Log.e("_lowPowerLayout===", isLowPowerLayout+"==="+popupwindow);

//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在加载");
//                    loadingDialog.show();
//                }


//                if(!isLowPowerLayout){
//                    isLowPowerLayout = true;
//                    initmPopupWindowView();
//                }

//                if(popupwindow==null || (popupwindow!=null && !popupwindow.isShowing())){
                if(popupwindow==null || !popupwindow.isShowing()){
                    initmPopupWindowView();
                }

                break;

            case R.id.personUI_scrappedLayout:
                UIHelper.goToAct(context, ScrappedDetailActivity.class);
                break;

            case R.id.personUI_header:
                clickPopupWindow();
//                UIHelper.goToAct(context, PersonInfoActivity.class);

                break;

            case R.id.popupWindow_back:
                isLowPowerLayout = false;
                popupwindow.dismiss();
                break;

            default:
                break;
        }
    }

    private void carbatteryaction_lowpower(){

        Log.e("lowpower===", "===");

//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token != null && !"".equals(access_token)){
//            RequestParams params = new RequestParams();
//            params.put("uid",uid);
//            params.put("access_token",access_token);
            HttpHelper.get(context, Urls.carbatteryaction_lowpower, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在加载");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onFailureCommon(throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                Log.e("lowpower===1", tv_low_power_count+"==="+responseString);

                                LowPowerBean bean = JSON.parseObject(result.getData(), LowPowerBean.class);

                                Log.e("lowpower===2", "==="+bean.getCount());

                                tv_low_power_count.setText(""+bean.getCount());

                                LowPowerDetailBean  bean_xa= JSON.parseObject(bean.getXiaoan(), LowPowerDetailBean.class);
                                LowPowerDetailBean  bean_xyt= JSON.parseObject(bean.getXyt(), LowPowerDetailBean.class);
                                LowPowerDetailBean  bean_tbt= JSON.parseObject(bean.getTbt(), LowPowerDetailBean.class);

                                ultra_low_count_xa = bean_xa.getUltra_low_count();
                                low_count_xa = bean_xa.getLow_count();
                                ultra_low_count_xyt = bean_xyt.getUltra_low_count();
                                low_count_xyt = bean_xyt.getLow_count();
                                ultra_low_count_tbt = bean_tbt.getUltra_low_count();
                                low_count_tbt = bean_tbt.getLow_count();

                                Log.e("lowpower===3", bean_xa.getUltra_low_count()+"==="+bean_xa.getLow_count()+"==="+bean_xyt.getUltra_low_count()+"==="+bean_xyt.getLow_count()+"==="+bean_tbt.getUltra_low_count()+"==="+bean_tbt.getLow_count());

//                                initmPopupWindowView();

//                                if (result.getFlag().equals("Success")) {
//
//                                } else {
//                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                                Log.e("lowpower===e", "==="+e);
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        }else {
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
        }
    }

    private void carbadaction_scrapped(){
        Log.e("scrapped===", "===");

//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token != null && !"".equals(access_token)){
//            RequestParams params = new RequestParams();
//            params.put("uid",uid);
//            params.put("access_token",access_token);
            HttpHelper.get(context, Urls.carbadaction_scrapped, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在加载");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onFailureCommon(throwable.toString());
                    Log.e("scrapped===fail", "==="+throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                Log.e("scrapped===1", tv_scrapped_count+"==="+responseString);

                                ScrappedBean bean = JSON.parseObject(result.getData(), ScrappedBean.class);

                                Log.e("scrapped===2", "==="+bean.getCount());

                                tv_scrapped_count.setText(""+bean.getCount());

//                                LowPowerDetailBean  bean_xa= JSON.parseObject(bean.getXiaoan(), LowPowerDetailBean.class);
//                                LowPowerDetailBean  bean_xyt= JSON.parseObject(bean.getXyt(), LowPowerDetailBean.class);
//
//                                ultra_low_count_xa = bean_xa.getUltra_low_count();
//                                low_count_xa = bean_xa.getLow_count();
//                                ultra_low_count_xyt = bean_xyt.getUltra_low_count();
//                                low_count_xyt = bean_xyt.getLow_count();
//
//                                Log.e("lowpower===3", bean_xa.getUltra_low_count()+"==="+bean_xa.getLow_count()+"==="+bean_xyt.getUltra_low_count()+"==="+bean_xyt.getLow_count());

//                                initmPopupWindowView();

//                                if (result.getFlag().equals("Success")) {
//
//                                } else {
//                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                                Log.e("lowpower===e", "==="+e);
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        }else {
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
        }
    }

    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
//                    roleName.setText(roles[0]);
                    roleName.setText(role);
                    roleName.setSelected(true);
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    public void initmPopupWindowView(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_low_power_bike, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = customView.findViewById(R.id.pop_low_power_bg);
        ImageView iv_popup_window_back = customView.findViewById(R.id.popupWindow_back);

        TextView tv_low_count_xa = customView.findViewById(R.id.tv_low_count_xa);
        TextView tv_ultra_low_count_xa = customView.findViewById(R.id.tv_ultra_low_count_xa);
        TextView tv_low_count_xyt = customView.findViewById(R.id.tv_low_count_xyt);
        TextView tv_ultra_low_count_xyt = customView.findViewById(R.id.tv_ultra_low_count_xyt);
        TextView tv_low_count_tbt = customView.findViewById(R.id.tv_low_count_tbt);
        TextView tv_ultra_low_count_tbt = customView.findViewById(R.id.tv_ultra_low_count_tbt);

        tv_low_count_xa.setText("低电："+low_count_xa);
        tv_ultra_low_count_xa.setText("超低电："+ultra_low_count_xa);
        tv_low_count_xyt.setText("低电："+low_count_xyt);
        tv_ultra_low_count_xyt.setText("超低电："+ultra_low_count_xyt);
        tv_low_count_tbt.setText("低电："+low_count_tbt);
        tv_ultra_low_count_tbt.setText("超低电："+ultra_low_count_tbt);

        iv_popup_window_back.setOnClickListener(this);



        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(activity);
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
        // 创建PopupWindow宽度和高度
        popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        //设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

//        popupwindow.setFocusable(false);// 这个很重要
//        popupwindow.setOutsideTouchable(false);
//        customView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                Log.e("popup===onKey", "==="+keyCode);
//
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    return true;
//                }
//                return false;
//            }
//        });

        isLowPowerLayout = false;

        customView.setFocusable(true);
        customView.setFocusableInTouchMode(true);
        customView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e("popup===onKey", "==="+keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    dismissPopupWindow();
                    return true;
                }
                return false;
            }
        });

        Log.e("initmPopup===", "===");

//        if (loadingDialog != null && loadingDialog.isShowing()) {
//            loadingDialog.dismiss();
//        }


    }

    private void clickPopupWindow() {
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(getActivity());

        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 5, 0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }

        // 打开弹窗
        UtilAnim.showToUp(rl_popup_window, iv_popup_window_back);

    }

    private void clickClosePopupWindow() {
        UtilAnim.hideToDown(rl_popup_window, iv_popup_window_back);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View v) {
            clickClosePopupWindow();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkPermission = context.checkSelfPermission(Manifest.permission.CAMERA);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
                            } else {
                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")     //TODO  setType(3).
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                101);

                                    }
                                });
                                customBuilder.create().show();
                            }
                            return;
                        }
                    }

                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                        if(!file.getParentFile().exists()){
                            file.getParentFile().mkdirs();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file));

                        }else {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        }


                        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    }else {
                        Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                    }

                    break;

                // 相册选择图片
                case R.id.pickPhotoBtn:
//                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            pickIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(PersonAlterActivity.this,
//                                    BuildConfig.APPLICATION_ID + ".provider",
//                                    new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                            pickIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                            pickIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        } else {
//                            // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
//                            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                        }
//                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
//                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
////                        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
//                        // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
//                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
//
//                        Log.e("minef===pickPhotoBtn", "==="+Intent.ACTION_PICK);

                        Intent intent;
                        if (Build.VERSION.SDK_INT < 19) {
                            intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                        } else {
                            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        }
                        startActivityForResult(intent, REQUESTCODE_PICK);
                    } else {
                        Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }

                    break;
                default:
                    break;
            }
        }
    };

    public void initHttp() {
        Log.e("minef===initHttp", "==="+isHidden());

        if(isHidden()) return;

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token != null && !"".equals(access_token)) {
            HttpHelper.get(context, Urls.user, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在加载");
                        loadingDialog.show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.e("minef===initHttp1", "==="+responseString);

                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                UserBean bean = JSON.parseObject(result.getData(), UserBean.class);
//                            myPurse.setText(bean.getMoney());
//                            myIntegral.setText(bean.getPoints());
                                userName.setText(bean.getName());

                                String[] schools = bean.getSchools();

                                Log.e("minef===initHttp2", schools+"===");

                                if(schools!=null && schools.length>0){

                                    Log.e("minef===initHttp3", schools[0]+"===");

                                    SchoolListBean bean2 = JSON.parseObject(schools[0], SchoolListBean.class);

                                    schoolName.setText(bean2.getName());
                                }

                                String[] roles = bean.getRoles();
                                if(roles!=null && roles.length>0){

                                    Log.e("minef===initHttp4", "===");

                                    role = roles[0];
                                    roleName.setText(roles[0]);
                                    roleName.setSelected(true);

//                                    m_myHandler.sendEmptyMessage(0);
                                }

//                        if(bean.getUnread_count()==0){
//                            iv_isRead.setVisibility(View.GONE);
//                        }else{
//                            iv_isRead.setVisibility(View.VISIBLE);
//                        }
//
//                        credit_scores_h5_title = bean.getCredit_scores_h5_title();
//                        credit_scores_h5_url = bean.getCredit_scores_h5_url();
//                        invite_h5_title = bean.getInvite_h5_title();
//                        invite_h5_url = bean.getInvite_h5_url();
//                        history_order_h5_title = bean.getHistory_order_h5_title();
//                        history_order_h5_url = bean.getHistory_order_h5_url();

                                //TODO  3
//                            if (bean.getHeadimg() != null && !"".equals(bean.getHeadimg())) {
//                                if ("gif".equalsIgnoreCase(bean.getHeadimg().substring(bean.getHeadimg().lastIndexOf(".") + 1, bean.getHeadimg().length()))) {
//                                    Glide.with(getActivity()).load(Urls.host + bean.getHeadimg()).asGif().centerCrop().into(headerImageView);
//                                } else {
//                                    Glide.with(getActivity()).load(Urls.host + bean.getHeadimg()).asBitmap().centerCrop().into(headerImageView);
//                                }
//                            }

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
        } else {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }
    }


    public void datas() {
        Log.e("minef===datas", "==="+isHidden());

        if(isHidden()) return;

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token != null && !"".equals(access_token)) {
            HttpHelper.get(context, Urls.datas, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在加载");
                        loadingDialog.show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        Log.e("minef===datas1", "==="+responseString);

                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        DatasBean bean = JSON.parseObject(result.getData(), DatasBean.class);

//                        tv_delivered_cars, tv_is_using_cars, tv_longtime_not_used_cars, tv_not_recycled_cars, tv_not_fixed_cars, tv_fixed_not_used_cars;

                        tv_delivered_cars.setText(bean.getDelivered_cars());
                        tv_is_using_cars.setText(bean.getIs_using_cars());
                        tv_longtime_not_used_cars.setText(bean.getLongtime_not_used_cars());
                        tv_not_recycled_cars.setText(bean.getNot_recycled_cars());
                        tv_not_fixed_cars.setText(bean.getNot_fixed_cars());
                        tv_fixed_not_used_cars.setText(bean.getFixed_not_used_cars());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }
    }

}
