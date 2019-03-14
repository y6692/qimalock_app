package com.qimalocl.manage.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
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
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.base.BaseFragment;
import com.qimalocl.manage.core.common.DisplayUtil;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.NearbyBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.TagBean;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.camera.CameraPreview;
import com.zbar.lib.decode.InactivityTimer;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.apache.http.Header;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    @BindView(R.id.capture_preview) FrameLayout scanPreview;
    @BindView(R.id.capture_container) RelativeLayout scanContainer;
    @BindView(R.id.capture_crop_view) RelativeLayout scanCropView;
    @BindView(R.id.capture_scan_line) ImageView scanLine;
    @BindView(R.id.activity_qr_scan_lightBtn) LinearLayout lightBtn;
    @BindView(R.id.iv_light) ImageView ivLight;
    @BindView(R.id.loca_show_btnBikeNum) TextView bikeNunBtn;

    private Camera mCamera;
    private CameraPreview mPreview;
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
    private Activity activity;

    private LoadingDialog loadingDialog;
    private Dialog dialog;
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

    private EditText bikeNumEdit;
    private Button positiveButton,negativeButton;
    private boolean notShow = false;
    private int type = 0;
    private String bikeNum;

    boolean first=true;

    static {
        System.loadLibrary("iconv");
    }

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
        activity = getActivity();

        tagDatas = new ArrayList<>();
        for (int i = 0; i < 4;i++){
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
                default:
                    break;
            }
            tagDatas.add(bean);
        }

        tagDatas2 = new ArrayList<>();
        for (int i = 0; i < 2;i++){
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
                default:
                    break;
            }
            tagDatas2.add(bean);
        }

        initView();
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
                    releaseCamera();
                    mCameraManager.closeDriver();

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

//                Intent rIntent = new Intent();
//                rIntent.putExtra("QR_CODE", bikeNum);
//                activity.setResult(RESULT_OK, rIntent);
//                scrollToFinishActivity();

                dialog3.show();

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

                resetCamera();
            }
        });

        initViews();
        playBeep = true;
        AudioManager audioService = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    private void initViews() {
        inactivityTimer = new InactivityTimer(activity);

        mImageScanner = new ImageScanner();
        mImageScanner.setConfig(0, Config.X_DENSITY, 3);
        mImageScanner.setConfig(0, Config.Y_DENSITY, 3);

        autoFocusHandler = new Handler();

        mCameraManager = new CameraManager(context);
        try {
            mCameraManager.openDriver();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //调整扫描框大小,自适应屏幕
        Display display = activity.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) scanCropView.getLayoutParams();
        linearParams.height = (int) (width * 0.65);
        linearParams.width = (int) (width * 0.65);
        scanCropView.setLayoutParams(linearParams);

        mCamera = mCameraManager.getCamera();
        mPreview = new CameraPreview(context, mCamera, previewCb, autoFocusCB);
        scanPreview.addView(mPreview);

        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        scanLine.setAnimation(mAnimation);

        autoFocusCB = new Camera.AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                autoFocusHandler.postDelayed(doAutoFocus, 1000);
            }
        };

        previewCb = new Camera.PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Size size = camera.getParameters().getPreviewSize();

                // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
                byte[] rotatedData = new byte[data.length];
                for (int y = 0; y < size.height; y++) {
                    for (int x = 0; x < size.width; x++)
                        rotatedData[x * size.height + size.height - y - 1] = data[x + y * size.width];
                }

                // 宽高也要调整
                int tmp = size.width;
                size.width = size.height;
                size.height = tmp;

                initCrop();

                Image barcode = new Image(size.width, size.height, "Y800");
                barcode.setData(rotatedData);
                barcode.setCrop(mCropRect.left, mCropRect.top, mCropRect.width(), mCropRect.height());

                int result = mImageScanner.scanImage(barcode);
                String resultStr = null;

                if (result != 0) {
                    SymbolSet syms = mImageScanner.getResults();
                    for (Symbol sym : syms) {
                        resultStr = sym.getData();
                    }
                }
                if (!TextUtils.isEmpty(resultStr)) {
                    inactivityTimer.onActivity();
                    playBeepSoundAndVibrate();

                    previewing = false;
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();

                    releaseCamera();
                    barcodeScanned = true;

                    bikeNum = resultStr;

//                Intent rIntent = new Intent();
//                rIntent.putExtra("QR_CODE", resultStr);
//                activity.setResult(RESULT_OK, rIntent);
//                activity.scrollToFinishActivity();

                    Log.e("Maint===preview", "===");

//                WindowManager.LayoutParams params1 = dialog.getWindow().getAttributes();
//                params1.width = LinearLayout.LayoutParams.MATCH_PARENT;
//                params1.height = LinearLayout.LayoutParams.MATCH_PARENT;
                    dialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                dialog.getWindow().setAttributes(params1);
                    dialog2.show();

                }
            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("onResume===Maintenance", "===");

        if(!first){
            resetCamera();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.e("onResume===Maintenance", "===");

        if(!first){
            releaseCamera();
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){

            if(!first){
                releaseCamera();
                if(mCameraManager!=null){
                    mCameraManager.closeDriver();
                }

//                if(inactivityTimer!=null){
//                    inactivityTimer.shutdown();
//                }
            }else{
                first = false;
            }

        }else{

            if(!first){
                resetCamera();
            }else{
                first = false;
            }
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;

            Log.e("===releaseCamera", "==="+mCamera);
        }
    }


    Camera.AutoFocusCallback autoFocusCB;
    Camera.PreviewCallback previewCb;

    private void resetCamera(){
        Log.e("===resetCamera", "==="+mCamera);

        previewing = true;

        mCameraManager = new CameraManager(context);
        try {
            mCameraManager.openDriver();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera = mCameraManager.getCamera();


        Log.e("111====resetCamera", mCamera+"==="+previewCb+"==="+autoFocusCB);

        scanPreview.removeAllViews();
        mPreview = new CameraPreview(context, mCamera, previewCb, autoFocusCB);
        scanPreview.addView(mPreview);
    }



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
                resetCamera();

                break;
            case R.id.dialog_maintenance_affirmLayout:
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
                    UIHelper.goToAct(context,LoginActivity.class);
                    return;
                }
                if (tagFlowLayout.getSelectedList().size() == 0){
                    Toast.makeText(context,"请选择操作方式",Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Integer posotion : tagFlowLayout.getSelectedList()){
                    type = tagDatas.get(posotion).getType();
                }
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                customBuilder.setTitle("温馨提示").setMessage("是否确定提交?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                tagFlowLayout.setAdapter(tagAdapter);
                                resetCamera();
                            }
                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
//                          delpoints(myAdapter.getDatas().get(curPosition).getUid(),type);

                            Log.e("Maintenance===onC", "==="+type);

                            tagFlowLayout.setAdapter(tagAdapter);
//                            tagAdapter.unSelected(0, tagFlowLayout);

//                            tagFlowLayout.
//                                    tagAdapter.

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

                                default:
                                    break;
                            }

                    }
                });
                customBuilder.create().show();
                if (dialog2 != null && dialog2.isShowing()){
                    dialog2.dismiss();
                }
                break;


            case R.id.dialog_maintenance_hand_closeLayout:
                if (dialog3 != null && dialog3.isShowing()){
                    dialog3.dismiss();
                }

                tagFlowLayout2.setAdapter(tagAdapter2);
                resetCamera();

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
                customBuilder = new CustomDialog.Builder(context);
                customBuilder.setTitle("温馨提示").setMessage("是否确定提交?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                tagFlowLayout2.setAdapter(tagAdapter2);
                                resetCamera();
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
                                default:
                                    break;
                            }
                    }
                });
                customBuilder.create().show();
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
            HttpHelper.post(context, Urls.recycle, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在提交");
                        loadingDialog.show();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"恭喜您，回收成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }

                        resetCamera();
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
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
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在提交");
                        loadingDialog.show();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"恭喜您，解锁成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }

                        resetCamera();
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

    private void endCar(String result){
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
            HttpHelper.post(context, Urls.endCar, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在提交");
                        loadingDialog.show();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"恭喜您，结束用车成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }

                        resetCamera();
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
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
            params.put("codenum",result);
            HttpHelper.post(context, Urls.hasRepaired, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("正在提交");
                        loadingDialog.show();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            Toast.makeText(context,"恭喜您，该车已修好",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }

                        resetCamera();
                    } catch (Exception e) {
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }





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




//    @Override
//    public void onDestroy() {
//
//        super.onDestroy();
//    }



    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
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
