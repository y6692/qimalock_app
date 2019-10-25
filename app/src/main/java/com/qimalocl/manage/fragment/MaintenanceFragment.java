package com.qimalocl.manage.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.zxing.Result;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.ChangeKeyLockManageActivity;
import com.qimalocl.manage.activity.LockManageActivity;
import com.qimalocl.manage.activity.LockManageAlterActivity;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.activity.Main2Activity;
import com.qimalocl.manage.activity.MainActivity;
import com.qimalocl.manage.base.BaseApplication;
import com.qimalocl.manage.base.BaseFragment;
import com.qimalocl.manage.ble.BLEService;
import com.qimalocl.manage.core.common.DisplayUtil;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.BadCarBean;
import com.qimalocl.manage.model.GlobalConfig;
import com.qimalocl.manage.model.NearbyBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.TagBean;
import com.qimalocl.manage.utils.ByteUtil;
import com.qimalocl.manage.utils.IoBuffer;
import com.qimalocl.manage.utils.SharePreUtil;
import com.qimalocl.manage.utils.ToastUtil;
import com.vondear.rxtools.RxAnimationTool;
import com.vondear.rxtools.RxBeepTool;
import com.vondear.rxtools.interfaces.OnRxScanerListener;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zxing.lib.scaner.CameraManager;
import com.zxing.lib.scaner.CaptureActivityHandler;
import com.zxing.lib.scaner.CaptureActivityHandler2;
import com.zxing.lib.scaner.activity.ActivityScanerCode;
import com.zxing.lib.scaner.decoding.InactivityTimer;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

@SuppressLint("NewApi")
public class MaintenanceFragment extends BaseFragment implements View.OnClickListener{

    Unbinder unbinder;
    private View v;

    @BindView(R.id.capture_preview) SurfaceView scanPreview;
    @BindView(R.id.capture_container) RelativeLayout scanContainer;
    @BindView(R.id.capture_crop_view) RelativeLayout scanCropView;
    @BindView(R.id.capture_scan_line) ImageView scanLine;
    @BindView(R.id.activity_qr_scan_lightBtn) LinearLayout lightBtn;
    @BindView(R.id.iv_light) ImageView ivLight;
    @BindView(R.id.loca_show_btnBikeNum) TextView bikeNunBtn;

    private Camera mCamera;
//    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private CameraManager mCameraManager;

    private InactivityTimer inactivityTimer;

    private Rect mCropRect = null;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private ImageScanner mImageScanner = null;

    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    private Context context;
    private MainActivity activity;

//    private LoadingDialog loadingDialog;
    private Dialog dialog, dialogRemark;
    private Dialog dialog2;
    private LinearLayout tagMainLayout;
    private TagFlowLayout tagFlowLayout;
    private LinearLayout closeLayout;
    private LinearLayout affirmLayout;
    private TagAdapter tagAdapter;
    private List<TagBean> tagDatas;

    private Dialog dialog3;
    private LinearLayout tagMainLayout2;
    private TagFlowLayout tagFlowLayout2;
    private LinearLayout closeLayout2;
    private LinearLayout affirmLayout2;
    private TagAdapter tagAdapter2;
    private List<TagBean> tagDatas2;

    private EditText bikeNumEdit, remarkEdit;
    private Button positiveButton, negativeButton, positiveButton2, negativeButton2;
    private boolean notShow = false;
    private int type = 0;
    public static String bikeNum;
    private String remark;

    boolean first=true;

    private String carType = "";
    private String codenum = "";
    private String mac = "";
    private String bleid = "";

    private int cn = 0;

    private BluetoothAdapter mBluetoothAdapter;
    BLEService bleService = new BLEService();

    private String tel = "13188888888";
    private boolean isHidden = true;

    private boolean isHand = true;

    private int showPage = 1;
    String badtime="2115-02-08 20:20";
    String codenum_bad="";
    String totalnum="";

    private boolean hasSurface;
    SurfaceHolder surfaceHolder;
    private int mCropWidth = 0;
    private int mCropHeight = 0;
    public static int ff = 0;

    private CaptureActivityHandler2 handler;
    private static OnRxScanerListener mScanerListener;
    SurfaceHolder.Callback sf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_scanner_location, null);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        activity = (MainActivity) getActivity();

        tagDatas = new ArrayList<>();
        for (int i = 0; i < 5;i++){
            TagBean bean = new TagBean();
            switch (i){
                case 0:
                    bean.setType(1);
                    bean.setName("回        收");
                    break;
                case 1:
                    bean.setType(2);
                    bean.setName("解除锁定");
                    break;
                case 2:
                    bean.setType(3);
                    bean.setName("结束骑行");
                    break;
                case 3:
                    bean.setType(4);
                    bean.setName("已  修  好");
                    break;
                case 4:
                    bean.setType(5);
                    bean.setName("报        废");
                    break;
                default:
                    break;
            }
            tagDatas.add(bean);
        }

        tagDatas2 = new ArrayList<>();
        for (int i = 0; i < 3;i++){
            TagBean bean = new TagBean();
            switch (i){
                case 0:
                    bean.setType(2);
                    bean.setName("解除锁定");
                    break;
                case 1:
                    bean.setType(3);
                    bean.setName("结束骑行");
                    break;
                case 2:
                    bean.setType(5);
                    bean.setName("报        废");
                    break;
                default:
                    break;
            }
            tagDatas2.add(bean);
        }

        initView();
        //权限初始化
        initPermission();
        //扫描动画初始化
        initScanerAnimation();


        CameraManager.init(context);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(activity);

        mCameraManager = CameraManager.get();

        autoFocusHandler = new Handler();
        autoFocusCB = new Camera.AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                autoFocusHandler.postDelayed(doAutoFocus, 1000);
            }
        };
    }

    private void initPermission() {
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initScanerAnimation() {
        ImageView mQrLineView = (ImageView) activity.findViewById(R.id.capture_scan_line);
        RxAnimationTool.ScaleUpDowm(mQrLineView);
    }


    private void initView() {
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        dialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.pop_circles_menu, null);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);

        bikeNumEdit = (EditText)dialogView.findViewById(R.id.pop_circlesMenu_bikeNumEdit);
        positiveButton = (Button)dialogView.findViewById(R.id.pop_circlesMenu_positiveButton);
        negativeButton = (Button)dialogView.findViewById(R.id.pop_circlesMenu_negativeButton);

        dialogRemark = new Dialog(context, R.style.Theme_AppCompat_Dialog);
        View dialogView2 = LayoutInflater.from(context).inflate(R.layout.pop_circles_menu2, null);
        dialogRemark.setContentView(dialogView2);
        dialogRemark.setCanceledOnTouchOutside(false);

        remarkEdit = (EditText)dialogView2.findViewById(R.id.pop_circlesMenu2_remarkEdit);
        positiveButton2 = (Button)dialogView2.findViewById(R.id.pop_circlesMenu2_positiveButton);
        negativeButton2 = (Button)dialogView2.findViewById(R.id.pop_circlesMenu2_negativeButton);



        if(notShow){
            bikeNunBtn.setVisibility(View.GONE);
        }else{
            bikeNunBtn.setVisibility(View.VISIBLE);
        }

        dialog2 = new Dialog(context, R.style.main_publishdialog_style);
        View tagView = LayoutInflater.from(context).inflate(R.layout.dialog_maintenance, null);
        dialog2.setContentView(tagView);
        dialog2.setCanceledOnTouchOutside(false);

        tagMainLayout = tagView.findViewById(R.id.dialog_maintenance_mainLayout);
        tagFlowLayout = tagView.findViewById(R.id.dialog_maintenance_flowlayout);
        closeLayout = tagView.findViewById(R.id.dialog_maintenance_closeLayout);
        affirmLayout = tagView.findViewById(R.id.dialog_maintenance_affirmLayout);

        LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams)tagMainLayout.getLayoutParams();
        params4.width = DisplayUtil.getWindowWidth(activity) * 3 / 5;
        tagMainLayout.setLayoutParams(params4);

        tagAdapter = new TagAdapter<TagBean>(tagDatas) {
            @Override
            public View getView(FlowLayout parent, int position, TagBean bean) {
                TextView tag = (TextView) LayoutInflater.from(context).inflate(R.layout.ui_tag, tagFlowLayout, false);
                tag.setText(bean.getName());
                return tag;
            }
        };
        tagFlowLayout.setAdapter(tagAdapter);



        dialog3 = new Dialog(context, R.style.main_publishdialog_style);
        tagView = LayoutInflater.from(context).inflate(R.layout.dialog_maintenance_hand, null);
        dialog3.setContentView(tagView);
        dialog3.setCanceledOnTouchOutside(false);

        tagMainLayout2 = tagView.findViewById(R.id.dialog_maintenance_hand_mainLayout);
        tagFlowLayout2 = tagView.findViewById(R.id.dialog_maintenance_hand_flowlayout);
        closeLayout2 = tagView.findViewById(R.id.dialog_maintenance_hand_closeLayout);
        affirmLayout2 = tagView.findViewById(R.id.dialog_maintenance_hand_affirmLayout);

        params4 = (LinearLayout.LayoutParams)tagMainLayout2.getLayoutParams();
        params4.width = DisplayUtil.getWindowWidth(activity) * 3 / 5;
        tagMainLayout2.setLayoutParams(params4);

        tagAdapter2 = new TagAdapter<TagBean>(tagDatas2) {
            @Override
            public View getView(FlowLayout parent, int position, TagBean bean) {
                TextView tag = (TextView) LayoutInflater.from(context).inflate(R.layout.ui_tag, tagFlowLayout2, false);
                tag.setText(bean.getName());
                return tag;
            }
        };
        tagFlowLayout2.setAdapter(tagAdapter2);

        closeLayout.setOnClickListener(this);
        affirmLayout.setOnClickListener(this);
        closeLayout2.setOnClickListener(this);
        affirmLayout2.setOnClickListener(this);

        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                light();
            }
        });

        bikeNunBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = SharedPreferencesUrls.getInstance().getString("uid","");
                String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    UIHelper.goToAct(context, LoginActivity.class);
                }else {
                    //关闭相机
//                    releaseCamera();
//                    mCameraManager.closeDriver();

                    bikeNumEdit.setText("");

                    WindowManager windowManager = activity.getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                    lp.width = (int) (display.getWidth() * 0.8); 								// 设置宽度0.6
                    lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setAttributes(lp);
                    dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
                    dialog.show();

                    InputMethodManager manager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    manager.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
                    manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bikeNum = bikeNumEdit.getText().toString().trim();
                if (bikeNum == null || "".equals(bikeNum)){
                    Toast.makeText(context,"请输入单车编号",Toast.LENGTH_SHORT).show();
                    return;
                }

                InputMethodManager manager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
//				Tag = 1;

                isHand = true;
                lockInfo(bikeNum);


//                Intent rIntent = new Intent();
//                rIntent.putExtra("QR_CODE", bikeNum);
//                activity.setResult(RESULT_OK, rIntent);
//                scrollToFinishActivity();
//
//                dialog3.show();

            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager manager1= (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                manager1.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                initCamera(surfaceHolder);
            }
        });


        positiveButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remark = remarkEdit.getText().toString().trim();
                if (remark == null || "".equals(remark)){
                    Toast.makeText(context,"请输入备注",Toast.LENGTH_SHORT).show();
                    return;
                }

                InputMethodManager manager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏

                if (dialogRemark.isShowing()) {
                    dialogRemark.dismiss();
                }
////				Tag = 1;
//
//                lockInfo(bikeNum);

                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                customBuilder.setTitle("温馨提示").setMessage("是否确定提交?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                tagFlowLayout.setAdapter(tagAdapter);
                                tagFlowLayout2.setAdapter(tagAdapter2);
                                initCamera(surfaceHolder);
                            }
                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
        //                          delpoints(myAdapter.getDatas().get(curPosition).getUid(),type);

                                    Log.e("Maintenance===onC", bikeNum+"==="+type);

                                    tagFlowLayout.setAdapter(tagAdapter);
                                    tagFlowLayout2.setAdapter(tagAdapter2);
        //                            tagAdapter.unSelected(0, tagFlowLayout);

                                    switch (type){
                                        case 1:
                                            Log.e("requestCode===2_1", "==="+bikeNum);
                                            recycle(bikeNum);
                                            break;

                                        case 2:
                                            Log.e("requestCode===2_2", "==="+bikeNum);
                                            unLock(bikeNum);
                                            break;

                                        case 3:
                                            Log.e("requestCode===2_3", "==="+bikeNum);
                                            endCar(bikeNum);
                                            break;

                                        case 4:
                                            Log.e("requestCode===2_4", "==="+bikeNum);
                                            hasRepaired(bikeNum);
                                            break;

                                        case 5:
                                            Log.e("requestCode===2_5", "==="+bikeNum);
                                            setCarScrapped(bikeNum);
                                            break;

                                        default:
                                            break;
                                    }
                            }
                        });
                customBuilder.create().show();
            }
        });

        negativeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager manager1= (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                manager1.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏
                if (dialogRemark.isShowing()) {
                    dialogRemark.dismiss();
                }

                tagFlowLayout.setAdapter(tagAdapter);
                tagFlowLayout2.setAdapter(tagAdapter2);
                initCamera(surfaceHolder);
            }
        });


//        initViews();
        playBeep = true;
        AudioManager audioService = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;




    }

//    private void initViews() {
//        inactivityTimer = new InactivityTimer(activity);
//
//        mImageScanner = new ImageScanner();
//        mImageScanner.setConfig(0, Config.X_DENSITY, 3);
//        mImageScanner.setConfig(0, Config.Y_DENSITY, 3);
//
//        autoFocusHandler = new Handler();
//
//        mCameraManager = CameraManager.get();
//        try {
//            mCameraManager.openDriver();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //调整扫描框大小,自适应屏幕
//        Display display = activity.getWindowManager().getDefaultDisplay();
//        int width = display.getWidth();
//        int height = display.getHeight();
//
//        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) scanCropView.getLayoutParams();
//        linearParams.height = (int) (width * 0.65);
//        linearParams.width = (int) (width * 0.65);
//        scanCropView.setLayoutParams(linearParams);
//
//        mCamera = mCameraManager.getCamera();
//        mPreview = new CameraPreview(context, mCamera, previewCb, autoFocusCB);
//        scanPreview.addView(mPreview);
//
//        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
//                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
//                TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
//        mAnimation.setDuration(1500);
//        mAnimation.setRepeatCount(-1);
//        mAnimation.setRepeatMode(Animation.REVERSE);
//        mAnimation.setInterpolator(new LinearInterpolator());
//        scanLine.setAnimation(mAnimation);
//
//        autoFocusCB = new Camera.AutoFocusCallback() {
//            public void onAutoFocus(boolean success, Camera camera) {
//                autoFocusHandler.postDelayed(doAutoFocus, 1000);
//            }
//        };
//
//        previewCb = new Camera.PreviewCallback() {
//            public void onPreviewFrame(final byte[] data, final Camera camera) {
//                m_myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(!previewing) return;
//
//                        Camera.Size size = null;
//                        if(camera!=null){
//                            size = camera.getParameters().getPreviewSize();
//                        }
//
//                        if(size == null) return;
//
////                        Log.e("0===preview", "===");
//
//                        // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
//                        byte[] rotatedData = new byte[data.length];
//                        for (int y = 0; y < size.height; y++) {
//                            for (int x = 0; x < size.width; x++)
//                                rotatedData[x * size.height + size.height - y - 1] = data[x + y * size.width];
//                        }
//
//                        // 宽高也要调整
//                        int tmp = size.width;
//                        size.width = size.height;
//                        size.height = tmp;
//
//                        initCrop();
//
////                        Log.e("1===preview", "===");
//
//                        Image barcode = new Image(size.width, size.height, "Y800");
//                        barcode.setData(rotatedData);
//                        barcode.setCrop(mCropRect.left, mCropRect.top, mCropRect.width(), mCropRect.height());
//
//                        int result = mImageScanner.scanImage(barcode);
//                        String resultStr = null;
//
//                        if (result != 0) {
//                            SymbolSet syms = mImageScanner.getResults();
//                            for (Symbol sym : syms) {
//                                resultStr = sym.getData();
//                            }
//                        }
//                        if (!TextUtils.isEmpty(resultStr)) {
//                            inactivityTimer.onActivity();
//                            playBeepSoundAndVibrate();
//
//                            previewing = false;
//                            mCamera.setPreviewCallback(null);
//                            mCamera.stopPreview();
//
//                            releaseCamera();
//                            barcodeScanned = true;
//
//                            bikeNum = resultStr;
//
////                          Intent rIntent = new Intent();
////                          rIntent.putExtra("QR_CODE", resultStr);
////                          activity.setResult(RESULT_OK, rIntent);
////                          activity.scrollToFinishActivity();
//
//                            Log.e("Maint===preview", "===");
//
//                            isHand = false;
//                            lockInfo(resultStr);
//
////                          WindowManager.LayoutParams params1 = dialog.getWindow().getAttributes();
////                          params1.width = LinearLayout.LayoutParams.MATCH_PARENT;
////                          params1.height = LinearLayout.LayoutParams.MATCH_PARENT;
//
//
//                        }
//                    }
//                });
//
//            }
//        };
//
//    }


    private void lockInfo(String result){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("tokencode",result);
            params.put("type", 2);    //1：维护或使用中会提示
            HttpHelper.post(context, Urls.lockInfo, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在提交");
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
                                if (result.getFlag().equals("Success")) {
                                    JSONObject jsonObject = new JSONObject(result.getData());

                                    Log.e("lockInfo===", "==="+responseString);

//                                    Log.e("lockInfo===", jsonObject.getString("lock_no")+"==="+jsonObject.getString("bleid")+"==="+responseString+"==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));
                                    carType = jsonObject.getString("type");

                                    if ("1".equals(carType)){      //机械锁
                                    }else {
                                        bikeNum = jsonObject.getString("codenum");
                                        mac = jsonObject.getString("macinfo");

                                        if ("2".equals(carType)){          //蓝牙锁
                                            Log.e("lockInfo===2", "==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));
                                        }else if ("3".equals(carType)){    //3合1锁
                                            Log.e("lockInfo===3", "==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));
                                        }else if ("4".equals(carType)){    //电单车
                                            Log.e("lockInfo===4", "==="+jsonObject.getString("pdk")+"==="+jsonObject.getString("type"));
                                            bleid = jsonObject.getString("bleid");
                                        }
                                    }

                                    if(isHand){
                                        dialog3.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                                      dialog.getWindow().setAttributes(params1);
                                        dialog3.show();
                                    }else{
                                        dialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                                      dialog.getWindow().setAttributes(params1);
                                        dialog2.show();
                                    }

                                } else {
                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                                    if (loadingDialog != null && loadingDialog.isShowing()){
                                        loadingDialog.dismiss();
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("Test","异常"+e);
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        }
    }



    @Override
    public void onResume() {
        super.onResume();

        bikeNumEdit.setText("");
        remarkEdit.setText("");

        Log.e("onResume===Maintenance", isHidden+"==="+first);

//        if(!first){
//            initCamera(surfaceHolder);
//        }
    }

    @Override
    public void onPause() {
        Log.e("onPause===Maintenance", isHidden+"==="+first);

//        if(!first && !isHidden){
//            releaseCamera();
//        }

        super.onPause();

        if (handler != null) {
            handler.quitSynchronously();
//            handler = null;
        }


//        releaseCamera();
        mCameraManager.closeDriver();

    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        mScanerListener = null;
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        super.onDestroy();

        Log.e("mainten===onDestroy", "===");


        if(dialog != null){
            dialog.dismiss();
        }
        if(dialog2 != null){
            dialog2.dismiss();
        }
        if(dialog3 != null){
            dialog3.dismiss();
        }
        if(dialogRemark != null){
            dialogRemark.dismiss();
        }

        m_myHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        isHidden = hidden;

        Log.e("onHiddenChanged===MF", first+"==="+isHidden);

        if(hidden){
            previewing = false;
            if(surfaceHolder!=null){
//                initCamera(surfaceHolder);
                surfaceHolder.removeCallback(sf);
                surfaceHolder = null;
            }


            if(!first){
//                releaseCamera();
//                if(mCameraManager!=null){
//                    mCameraManager.closeDriver();
//                }

//                Log.e("onHiddenChanged===MF1", mCameraManager+"==="+handler);
//
//                if (handler != null) {
//                    handler.quitSynchronously();
//                    handler = null;
//                }
//
//                Log.e("onHiddenChanged===MF2", mCameraManager+"==="+handler);
//
////               releaseCamera();
//                mCameraManager.closeDriver();

                hasSurface = false;

            }else{
                first = false;
            }

        }else{
            previewing = true;

            if(ff==1){
                ff=0;

                dialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog2.show();

            }


            Log.e("surface===MF0", first+"==="+surfaceHolder+"==="+hasSurface);

            if(surfaceHolder!=null){
                surfaceHolder.addCallback(sf);
            }


            if(!first){


                if (!hasSurface) {
                    //Camera初始化

                    if(surfaceHolder==null){
                        Log.e("surface===MF&", first+"==="+surfaceHolder+"==="+hasSurface);

                        surfaceHolder = scanPreview.getHolder();

                        sf = new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                            }

                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {
                                Log.e("surface===MF1", "==="+hasSurface);

                                if (!hasSurface) {
                                    hasSurface = true;

                                    initCamera(holder);
                                }
                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder holder) {
                                Log.e("surface===MF2", "==="+hasSurface);

                                hasSurface = false;

                            }
                        };

                        surfaceHolder.addCallback(sf);
                        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


                        if (mCamera != null) {
                            initCamera(surfaceHolder);
//                            mCamera.startPreview();
                        }
                    }



                } else {
                    initCamera(surfaceHolder);
                }
            }else{
                first = false;

                if(surfaceHolder==null){
                    surfaceHolder = scanPreview.getHolder();

                    surfaceHolder.addCallback(sf);
                    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }
            }
        }
    }



    private void initCamera(SurfaceHolder surfaceHolder) {
        Log.e("initCamera===", "====");

//        releaseCamera();
//        mCameraManager.closeDriver();

        previewing = true;

        try {
//            mCameraManager = CameraManager.get();

            this.surfaceHolder = scanPreview.getHolder();

            mCameraManager.openDriver(this.surfaceHolder);
            mCamera = mCameraManager.getCamera();



            Point point = mCameraManager.getCameraResolution();
            AtomicInteger width = new AtomicInteger(point.y);
            AtomicInteger height = new AtomicInteger(point.x);
            int cropWidth = scanCropView.getWidth() * width.get() / scanContainer.getWidth();
            int cropHeight = scanCropView.getHeight() * height.get() / scanContainer.getHeight();
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);

            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCB);

//            if (handler == null) {
//                handler = new CaptureActivityHandler2(this);
//            }

            handler = new CaptureActivityHandler2(this);
        } catch (Exception ioe) {
            Log.e("initCamera===MF_e", "===="+ioe);
//            return;
        }

    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            Log.e("0===releaseCamera", "==="+mCamera);

            previewing = false;
            mCamera.setPreviewCallback(null);
            Log.e("1===releaseCamera", "==="+mCamera);
            mCamera.release();
            Log.e("2===releaseCamera", "==="+mCamera);
            mCamera = null;
        }
    }

    private void resetCamera(){
//        Log.e("===resetCamera", "==="+mCamera);
//
//        previewing = true;
//
//        mCameraManager = CameraManager.get();
//        try {
//            mCameraManager.openDriver();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        mCamera = mCameraManager.getCamera();
//
//
//        Log.e("111====resetCamera", mCamera+"==="+previewCb+"==="+autoFocusCB);
//
//        scanPreview.removeAllViews();
//        mPreview = new CameraPreview(context, mCamera, previewCb, autoFocusCB);
//        scanPreview.addView(mPreview);
    }

    public void handleDecode(Result result) {
        if(!previewing) return;

        inactivityTimer.onActivity();
        //扫描成功之后的振动与声音提示
        RxBeepTool.playBeep(activity, vibrate);

//        mCamera.setPreviewCallback(null);
//        mCamera.stopPreview();
//
//        releaseCamera();

        Log.e("===", "====");

        String result1 = result.getText();
        if (mScanerListener == null) {
            initDialogResult(result);
        } else {
            mScanerListener.onSuccess("From to Camera", result);
        }
    }

    private void initDialogResult(Result result) {
//		useBike(result.toString());

        isHand = false;
        lockInfo(result.toString());

    }

    public Handler getHandler() {
        return handler;
    }

    public void setCropWidth(int cropWidth) {
        mCropWidth = cropWidth;
        CameraManager.FRAME_WIDTH = mCropWidth;

    }

    public void setCropHeight(int cropHeight) {
        this.mCropHeight = cropHeight;
        CameraManager.FRAME_HEIGHT = mCropHeight;
    }






    Camera.AutoFocusCallback autoFocusCB;
    Camera.PreviewCallback previewCb;





    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.dialog_maintenance_closeLayout:
                if (dialog2 != null && dialog2.isShowing()){
                    dialog2.dismiss();
                }

                tagFlowLayout.setAdapter(tagAdapter);
                initCamera(surfaceHolder);

                break;
            case R.id.dialog_maintenance_affirmLayout:
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    Toast.makeText(context, "请先登录您的账号", Toast.LENGTH_SHORT).show();
                    UIHelper.goToAct(context,LoginActivity.class);
                    return;
                }
                if (tagFlowLayout.getSelectedList().size() == 0){
                    Toast.makeText(context, "请选择操作方式", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Integer posotion : tagFlowLayout.getSelectedList()){
                    type = tagDatas.get(posotion).getType();
                }

                Log.e("affirmLayout===", "==="+type);

                if(type==1 || type==4 || type==5){
//                    releaseCamera();
//                    mCameraManager.closeDriver();

                    remarkEdit.setText("");

                    WindowManager windowManager = activity.getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = dialogRemark.getWindow().getAttributes();
                    lp.width = (int) (display.getWidth() * 0.8); 								// 设置宽度0.6
                    lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    dialogRemark.getWindow().setAttributes(lp);
                    dialogRemark.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
                    dialogRemark.show();

                    InputMethodManager manager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    manager.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
                    manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }else{
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("是否确定提交?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                    tagFlowLayout.setAdapter(tagAdapter);
                                    initCamera(surfaceHolder);
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
//                          delpoints(myAdapter.getDatas().get(curPosition).getUid(),type);

                            Log.e("Maintenance===onC2", bikeNum+"==="+type);

                            tagFlowLayout.setAdapter(tagAdapter);
//                            tagAdapter.unSelected(0, tagFlowLayout);

                            switch (type){
//                                case 1:
//                                    Log.e("requestCode===2_1", "==="+bikeNum);
//                                    recycle(bikeNum);
//                                    break;

                                case 2:
                                    Log.e("requestCode===2_2", "==="+bikeNum);
                                    unLock(bikeNum);
                                    break;

                                case 3:
                                    Log.e("requestCode===2_3", "==="+bikeNum);
                                    endCar(bikeNum);
                                    break;

//                                case 4:
//                                    Log.e("requestCode===2_4", "==="+bikeNum);
//                                    hasRepaired(bikeNum);
//                                    break;

                                default:
                                    break;
                            }

                        }
                    });
                    customBuilder.create().show();
                }

                if (dialog2 != null && dialog2.isShowing()){
                    dialog2.dismiss();
                }
                break;


            case R.id.dialog_maintenance_hand_closeLayout:
                if (dialog3 != null && dialog3.isShowing()){
                    dialog3.dismiss();
                }

                tagFlowLayout2.setAdapter(tagAdapter2);
                initCamera(surfaceHolder);

                break;
            case R.id.dialog_maintenance_hand_affirmLayout:
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
                    UIHelper.goToAct(context,LoginActivity.class);
                    return;
                }
                if (tagFlowLayout2.getSelectedList().size() == 0){
                    Toast.makeText(context,"请选择操作方式",Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Integer posotion : tagFlowLayout2.getSelectedList()){
                    type = tagDatas2.get(posotion).getType();
                }

                if(type==5) {
//                    releaseCamera();
//                    mCameraManager.closeDriver();

                    remarkEdit.setText("");

                    WindowManager windowManager = activity.getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = dialogRemark.getWindow().getAttributes();
                    lp.width = (int) (display.getWidth() * 0.8);                                // 设置宽度0.6
                    lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    dialogRemark.getWindow().setAttributes(lp);
                    dialogRemark.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
                    dialogRemark.show();

                    InputMethodManager manager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    manager.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
                    manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }else{
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("是否确定提交?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                    tagFlowLayout2.setAdapter(tagAdapter2);
                                    initCamera(surfaceHolder);
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
//                          delpoints(myAdapter.getDatas().get(curPosition).getUid(),type);

                            Log.e("MaintenanceHand===onC", "==="+type);

                            tagFlowLayout2.setAdapter(tagAdapter2);

                            switch (type){
                                case 2:
                                    Log.e("requestCode_hand===2_2", "==="+bikeNum);
                                    unLock(bikeNum);
                                    break;
                                case 3:
                                    Log.e("requestCode_hand===2_3", "==="+bikeNum);
                                    endCar(bikeNum);
                                    break;
//                                case 5:
//                                    Log.e("requestCode_hand===2_4", "==="+bikeNum);
//                                    setCarScrapped(bikeNum);
//                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    customBuilder.create().show();
                }


                if (dialog3 != null && dialog3.isShowing()){
                    dialog3.dismiss();
                }
                break;


            default:
                break;
        }
    }

    private void recycle(String result){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("codenum",result);
            params.put("remark", remark);
            HttpHelper.post(context, Urls.recycle, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在提交");
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
                                if (result.getFlag().equals("Success")) {
                                    Toast.makeText(context,"恭喜您，回收成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                                }

                                initHttp();

                                initCamera(surfaceHolder);
                            } catch (Exception e) {
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        }
    }

    private void unLock(String result){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("codenum",result);
            HttpHelper.post(context, Urls.unLock, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在提交");
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
                                if (result.getFlag().equals("Success")) {
                                    Toast.makeText(context,"恭喜您，解锁成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                                }

                                initCamera(surfaceHolder);
                            } catch (Exception e) {
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        }
    }

    private void endCar(final String result){
        int code = 0;
//        if(result.split("&").length==1){
//            if(result.split("\\?")[1].split("&")[0].split("=")[1].matches(".*[a-zA-z].*")){
//                useCar(result);
//                return;
//            }else{
//                code = Integer.parseInt(result.split("\\?")[1].split("&")[0].split("=")[1]);
//            }
//        }else{
//            code = Integer.parseInt(result.split("\\?")[1].split("&")[0].split("=")[1]);
//        }
//
//        if(result.indexOf('&')!=-1 || (code >= 80001651 && code <= 80002000)){  //电单车
//            ebikeInfo(result);
//        }else{
//            useCar(result);
//        }


        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("tokencode",result);
            params.put("back_type","man_OS");
            HttpHelper.post(context, Urls.endCar, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在提交");
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
                                if (result.getFlag().equals("Success")) {

                                    Toast.makeText(context,"恭喜您，结束用车成功",Toast.LENGTH_SHORT).show();

                                    Log.e("biking===000", "endCar===="+carType+"==="+responseString);

                                    if("4".equals(carType) || "7".equals(carType)){
                                        closeEbikeTemp();
                                    }else{
                                        initCamera(surfaceHolder);
                                    }


                                }else {
                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();

                                    initCamera(surfaceHolder);
                                }


                            } catch (Exception e) {
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        }
    }


    private void hasRepaired(String result){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("codenum", result);
            params.put("remark", remark);
            HttpHelper.post(context, Urls.hasRepaired, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在提交");
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
                                if (result.getFlag().equals("Success")) {
                                    Toast.makeText(context,"恭喜您，该车已修好",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                                }

                                initHttp();

                                initCamera(surfaceHolder);
                            } catch (Exception e) {
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        }
    }

    private void setCarScrapped(String result){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }else {
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            params.put("codenum", result);
            params.put("remark", remark);
            HttpHelper.post(context, Urls.set_car_scrapped, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在提交");
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
                                if (result.getFlag().equals("Success")) {
                                    Toast.makeText(context, "恭喜您，该车已报废", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                                }

                                initHttp();

                                initCamera(surfaceHolder);
                            } catch (Exception e) {
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        }
    }

    private void initHttp() {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
            return;
        }
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("page", showPage);
        params.put("pagesize", GlobalConfig.PAGE_SIZE);

        Log.e("badcarList===0", totalnum+"==="+bikeNum);

        HttpHelper.get(context, Urls.badcarList, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在加载");
//                    loadingDialog.show();
//                }

//                setFooterType(1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    Log.e("badcarList===1", "==="+responseString);

                    if (result.getFlag().equals("Success")) {

                        JSONArray array = new JSONArray(result.getData());

                        Log.e("badcarList===2", "==="+array);

                        if (array.length() == 0 && showPage == 1) {
                            totalnum = "0";
                            codenum = "";
                        }

                        for (int i = 0; i < array.length();i++){
                            BadCarBean bean = JSON.parseObject(array.getJSONObject(i).toString(), BadCarBean.class);

                            if(i==0 && bean.getBadtime().compareTo(badtime)<0){
                                badtime = bean.getBadtime();
                                codenum = bean.getCodenum();
                                totalnum = bean.getTotalnum();
                            }

//                            datas.add(bean);
                        }

                        Log.e("badcarList===3", totalnum+"==="+codenum);

                        if(!"".equals(totalnum)){
                            Intent intent = new Intent("data.broadcast.action");
                            intent.putExtra("codenum", codenum);
                            intent.putExtra("count", Integer.parseInt(totalnum));
                            context.sendBroadcast(intent);
                        }


//                        View view = LayoutInflater.from(context).inflate(R.layout.fragment_scan, null);
//                        TextView tvMsg = view.findViewById(R.id.msg);
//                        tvMsg.setText("456");

                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
            }
        });
    }


    public void closeEbikeTemp(){
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");

        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("tokencode", bikeNum);
        HttpHelper.post(context, Urls.closeEbikeDdy, params, new TextHttpResponseHandler() {
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

                            Log.e("biking===000", "closeEbike===="+bikeNum+"==="+responseString);

                            if (result.getFlag().equals("Success")) {
//                              ToastUtil.showMessage(context,"数据更新成功");

                                Log.e("biking===", "closeEbike===="+result.getData());

                                if ("0".equals(result.getData())){
                                    ToastUtil.showMessageApp(context,"关锁成功");

                                    initCamera(surfaceHolder);
                                } else {
                                    ToastUtil.showMessageApp(context,"关锁失败");

                                    final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
                                    mBluetoothAdapter = bluetoothManager.getAdapter();

                                    // 检查设备上是否支持蓝牙
                                    if (mBluetoothAdapter == null) {
                                        Toast.makeText(context, "不支持蓝牙", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    BLEService.bluetoothAdapter = mBluetoothAdapter;

                                    bleService.view = context;
                                    bleService.showValue = true;

                                    bleService.connect(mac);
                                    cn = 0;

                                    temporaryLock();

                                }
                            } else {
                                ToastUtil.showMessageApp(context,result.getMsg());

                                initCamera(surfaceHolder);
                            }
                        } catch (Exception e) {
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });
    }

    void temporaryLock(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("temporaryLock===", "===");

                if(!bleService.connect){
                    cn++;

                    if(cn<5){
                        temporaryLock();
                    }else{
//                        customDialog6.show();
                        ToastUtil.showMessageApp(context,"连接失败，请重试");
                        initCamera(surfaceHolder);
                        return;
                    }

                }else{

                    bleService.write(new byte[]{0x03, (byte) 0x81, 0x01, (byte) 0x82});

                    m_myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("temporaryLock===4_3", "==="+mac);

                            button8();
                            button9();
                            button2();    //设防

                            closeLock();
                        }
                    }, 500);

                }

            }
        }, 2 * 1000);
    }

    void closeLock(){
        m_myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("temporaryLock===4_4", bleService.cc+"==="+"B1 2A 80 00 00 5B ".equals(bleService.cc));

                if("B1 2A 80 00 00 5B ".equals(bleService.cc)){
                    Log.e("temporaryLock===4_5", "==="+bleService.cc);

                    ToastUtil.showMessageApp(context,"关锁成功");

                    SharedPreferencesUrls.getInstance().putString("tempStat","1");

                }else{
//                    customDialog7.show();
                    ToastUtil.showMessageApp(context,"关锁失败，请重试");
                }

                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                initCamera(surfaceHolder);
                Log.e("temporaryLock===4_6", "==="+bleService.cc);

            }
        }, 500);
    }

    //设防
    void button2() {
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        byte[] cmd = sendCmd("00001000", "00000000");
        ioBuffer.writeBytes(cmd);
        bleService.write(toBody(ioBuffer.readableBytes()));
    }

    // 注册
    void button8() {
        //0x02,0x11,0xXX,0xXX,0xXX,0xXX,0xXX,0xXX,0xXX,0xXX, 0xXX,0xXX,0xXX,0xXX,0xXX,0xXX,0xXX, 0xXX,0xXX,0xTT
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        ioBuffer.writeByte((byte) 0x82);
        ByteUtil.log("tel-->" + tel);
        String str = tel;

        byte[] bb = new byte[11];
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            bb[i] = (byte) a;
        }
        ioBuffer.writeBytes(bb);

        int crc = (int) ByteUtil.crc32(getfdqId(bleid));
        byte cc[] = ByteUtil.intToByteArray(crc);
        ioBuffer.writeByte(cc[0] ^ cc[3]);
        ioBuffer.writeByte(cc[1] ^ cc[2]);
        ioBuffer.writeInt(0);
        bleService.write(toBody(ioBuffer.readableBytes()));
    }

    void button9() {
//		bleService.write( new byte[]{0x03, (byte)0x81,0x01,(byte)0x82});
//		bleService.sleep(200);
        IoBuffer ioBuffer = IoBuffer.allocate(20);
        ioBuffer.writeByte((byte) 0x83);
        ioBuffer.writeBytes(getfdqId(tel));
        bleService.write(toBody(ioBuffer.readableBytes()));
        SharePreUtil.getPreferences("FDQID").putString("ID", tel);
    }

    byte[] getfdqId(String str) {

        IoBuffer ioBuffer = IoBuffer.allocate(17);
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            ioBuffer.writeByte((byte) a);
        }
        return ioBuffer.array();
    }

    public byte[] sendCmd(String s1, String s2) {
        IoBuffer ioBuffer = IoBuffer.allocate(5);
        ioBuffer.writeByte(0XA1);
        ioBuffer.writeByte(ByteUtil.BitToByte(s1));
        ioBuffer.writeByte(ByteUtil.BitToByte(s2));

        ioBuffer.writeByte(0);
        ioBuffer.writeByte(0);

        return ioBuffer.array();
    }

    IoBuffer toBody(byte[] bb) {
        IoBuffer buffer = IoBuffer.allocate(20);
        buffer.writeByte(bb.length + 1);
        buffer.writeBytes(bb);
        buffer.writeByte((int) ByteUtil.SumCheck(bb));


        return buffer.flip();
    }


    Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                default:
                    break;
            }
            return false;
        }
    });

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            //相当于Fragment的onResume
//
//            Log.e("onPause===Maintenance1", "===");
//
//        } else {
//            //相当于Fragment的onPause
//
//            Log.e("onPause===Maintenance2", "===");
//        }
//    }








    private Runnable doAutoFocus = new Runnable() {
        public void run() {

//            Log.e("0===doAutoFocus", "==="+previewing);

            if (previewing){

                Log.e("1===doAutoFocus", "==="+previewing);

                mCamera.autoFocus(autoFocusCB);

//                Log.e("2===doAutoFocus", "==="+previewing);
            }

        }
    };


    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = mCameraManager.getCameraResolution().y;
        int cameraHeight = mCameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    boolean flag = true;

    protected void light() {
        if (flag == true) {
            flag = false;
            // 打开
            ivLight.setImageResource(R.drawable.light2);

            mCameraManager.openLight();
        } else {
            flag = true;
            // 关闭
            ivLight.setImageResource(R.drawable.light);
            mCameraManager.offLight();
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            scrollToFinishActivity();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//
//            case 100:
////                if (loadingDialog != null && loadingDialog.isShowing()){
////                    loadingDialog.dismiss();
////                }
////                if (customDialog2 != null && customDialog2.isShowing()){
////                    customDialog2.dismiss();
////                }
//
//                if (grantResults[0] == PERMISSION_GRANTED) {
//                    // Permission Granted
//                    if (permissions[0].equals(Manifest.permission.CAMERA)){
////                        try {
//////                            closeBroadcast();
//////                            deactivate();
//////
//////                            Intent intent = new Intent();
//////                            intent.setClass(MainActivity.this, ActivityScanerCode.class);
//////                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//////                            startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
////
////                        } catch (Exception e) {
////                            UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
////                        }
//                    }
//                }else {
//                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许获取相机权限！")
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            Intent localIntent = new Intent();
//                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
//                            startActivity(localIntent);
////                            finishMine();
//                        }
//                    });
//                    customBuilder.create().show();
//                }
//                break;
//
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }


}
