package com.qimalocl.manage.activity;

import android.Manifest;
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
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.BuildConfig;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseViewAdapter;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.fragment.ScanFragment;
import com.qimalocl.manage.model.AddDispatchBean;
import com.qimalocl.manage.model.CarSchoolBean;
import com.qimalocl.manage.model.GlobalConfig;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.UpTokenBean;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.qimalocl.manage.utils.LogUtil;
import com.qimalocl.manage.utils.QiNiuInitialize;
import com.qimalocl.manage.utils.ToastUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator1 on 2017/2/13.
 */

public class AddDispatchActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {

    private final String IMAGE_FILE_NAME = "picture.jpg";// 照片文件名称
    private final int REQUESTCODE_TAKE = 1;

    private List<String> imageList = new ArrayList<>();
    private List<String> carList = new ArrayList<>();

    private Context context;
    private ImageView backImg;
    private TextView title;
    private Button rightBtn;
    // List
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView myList;

    private View footerView;
    private View footerViewType01;
    private View footerViewType02;
    private View footerViewType03;
    private View footerViewType04;
    private View footerViewType05;

    private View footerLayout;

    private MyAdapter myAdapter;
    private List<AddDispatchBean> datas;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;
    private String starttime = "";
    private String endtime = "";

    RelativeLayout rl_photo;
    ImageView iv_photo;
    ImageView iv_add_photo;
    ImageView iv_add_car;
    Button btn_photo_delete;

    private String urlpath = ""; // 图片本地路径
    private Bitmap upBitmap;

    private String upToken = "";
    private String imageurl = "";

    CustomDialog.Builder customBuilder;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dispatch);
        context = this;
        datas = new ArrayList<>();
        initView();
    }

    private void initView(){
        customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示").setMessage("临时数据将被清除，确定退出吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        scrollToFinishActivity();
                        dialog.cancel();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        customDialog = customBuilder.create();

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("新增调运");
        rightBtn = (Button)findViewById(R.id.mainUI_title_rightBtn);
//        rightBtn.setText("查询");

        // list投资列表
        footerView = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
        footerViewType01 = footerView.findViewById(R.id.footer_Layout_type01);// 点击加载更多
        footerViewType02 = footerView.findViewById(R.id.footer_Layout_type02);// 正在加载，请您稍等
        footerViewType03 = footerView.findViewById(R.id.footer_Layout_type03);// 已无更多
        footerViewType04 = footerView.findViewById(R.id.footer_Layout_type04);// 刷新失败，请重试
        footerViewType05 = footerView.findViewById(R.id.footer_Layout_type05);// 暂无数据

        footerLayout = footerView.findViewById(R.id.footer_Layout);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.Layout_swipeParentLayout);
        myList = (ListView)findViewById(R.id.Layout_swipeListView);
//        myList.addFooterView(footerView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        myList.setOnItemClickListener(this);
        if(datas.isEmpty()){

        }

        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(datas);
        myList.setAdapter(myAdapter);

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);
        iv_add_photo = (ImageView) findViewById(R.id.iv_add_photo);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        btn_photo_delete = (Button) findViewById(R.id.btn_photo_delete);
        iv_add_car = (ImageView) findViewById(R.id.iv_add_car);

        rl_photo.setVisibility(View.GONE);

        backImg.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        footerLayout.setOnClickListener(this);
        iv_add_photo.setOnClickListener(this);
        iv_add_car.setOnClickListener(this);
        btn_photo_delete.setOnClickListener(this);

        getUpToken();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(context, HistoryRoadDetailActivity.class);
//        intent.putExtra("oid",myAdapter.getDatas().get(position).getOid());
//        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        isRefresh = true;
//        if(data.size()!=0){
//            myAdapter.notifyDataSetChanged();
//        }

//        initHttp();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        m_myHandler.removeCallbacks(null);
    }

    @Override
    public void onRefresh() {
        showPage = 1;
        if (!isRefresh) {
            if(datas.size()!=0){
                myAdapter.getDatas().clear();
                myAdapter.notifyDataSetChanged();
            }
            isRefresh = true;
            initHttp();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.mainUI_title_backBtn:
//                scrollToFinishActivity();
                customDialog.show();
                break;

            case R.id.mainUI_title_rightBtn:
//                Intent intent = new Intent(context, SchoolSelectActivity.class);
//                startActivityForResult(intent,0);

//                uploadImage(upBitmap);

//                SubmitBtn();

                LogUtil.e("rightBtn===", imageList+"==="+urlpath);

                loadingDialog.setTitle("正在提交");
                loadingDialog.show();

                if(imageList.size()==0){
//                    if(imageUrlList.size()>0){
//                        for(int i =0; i<imageUrlList.size(); i++){
//                            upBitmap = imageUrlList.get(i);
//
//                            m_myHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    uploadImage();
//                                }
//                            });
//                        }
//                    }else{
//                        SubmitBtn();
//                    }

                    if(!"".equals(urlpath)){
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                uploadImage();
                            }
                        });
                    }else{
                        SubmitBtn();
                    }



                }else{
                    SubmitBtn();
                }

                break;

            case R.id.iv_add_photo:
                takePhoto();
                break;

            case R.id.btn_photo_delete:
                urlpath = "";
                imageList.clear();
                iv_photo.setImageBitmap(null);
                rl_photo.setVisibility(View.GONE);
                iv_add_photo.setVisibility(View.VISIBLE);
                ToastUtil.showMessageApp(context, "照片删除成功");
                break;

            case R.id.iv_add_car:
                Intent intent = new Intent();
                intent.setClass(context, ActivityScanerCode.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("isAddDispatch",true);
                startActivityForResult(intent, 10);
//                scrollToFinishActivity();
                break;

            case R.id.footer_Layout:
                if (!isLast) {
                    showPage += 1;
                    initHttp();
                    myAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    private void SubmitBtn(){

        LogUtil.e("SubmitBtn===0", imageList+"==="+carList+"==="+ScanFragment.longitude+"==="+ScanFragment.latitude);


        RequestParams params = new RequestParams();
        params.put("photos", ""+imageList);
        params.put("cars", ""+carList);
        params.put("longitude", ScanFragment.longitude);
        params.put("latitude", ScanFragment.latitude);


        HttpHelper.post(context, Urls.transport, params, new TextHttpResponseHandler() {     //TODO
            @Override
            public void onStart() {
//                onStartCommon("正在提交");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon("ada==SubmitBtn", throwable.toString());   //org.apache.http.client.HttpResponseException: Internal Server Error
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("SubmitBtn===", "==="+responseString);

                            ToastUtil.showMessageApp(context, result.getMessage());

                            if(result.getStatus_code()==200){
//                                UIHelper.goToAct(context, MainActivity.class);

//                                Intent rIntent = new Intent();
//                                setResult(RESULT_OK, rIntent);
                                setResult(RESULT_OK);
                                scrollToFinishActivity();

//                                setResult(RESULT_OK);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });
    }

    public void getUpToken() {
        RequestParams params = new RequestParams();
//        params.put("uid",uid);
//        params.put("access_token",access_token);
        HttpHelper.get(context, Urls.uploadtoken, params, new TextHttpResponseHandler() {
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
                            LogUtil.e("uploadtoken===", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

//                            LogUtil.e("uploadtoken===1", result.getData()+"==="+result.getStatus_code());

                            UpTokenBean bean = JSON.parseObject(result.getData(), UpTokenBean.class);

                            LogUtil.e("uploadtoken===2", bean+"==="+bean.getToken());

                            if (null != bean.getToken()) {

                                upToken = bean.getToken();

//                                SharedPreferencesUrls.getInstance().putString("access_token", "Bearer "+bean.getToken());
//                                Toast.makeText(context,"恭喜您,获取成功",Toast.LENGTH_SHORT).show();
//                                scrollToFinishActivity();

//                                uploadImage();
                            }else{
                                Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });

    }

    public void uploadImage() {
        //定义数据上传结束后的处理动作
        final UpCompletionHandler upCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

//                JSONObject jsonObject = new JSONObject(info.timeStamp);

                LogUtil.e("uploadImage===0", "==="+response);

                try {
                    JSONObject jsonObject = new JSONObject(response.getString("image"));

                    imageurl = jsonObject.getString("key");
                    imageList.add("\""+imageurl+"\"");

                    LogUtil.e("UpCompletion===", imageurl+"==="+imageList);

//                    if(imageList.size()==imageUrlList.size()){
//
//                    }

//                    iv_add_photo.setVisibility(View.GONE);

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }

                    SubmitBtn();

                } catch (JSONException e) {

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    e.printStackTrace();
                }



            }
        };
        final UploadOptions uploadOptions = new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, final double percent) {
                //百分数格式化
                NumberFormat fmt = NumberFormat.getPercentInstance();
                fmt.setMaximumFractionDigits(2);//最多两位百分小数，如25.23%

                LogUtil.e("progress===", "==="+fmt.format(percent));

//                tv.setText("图片已经上传:" + fmt.format(percent));
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        });
        try {
            //上传图片jjj
            LogUtil.e("uploadImage===", "==="+upToken);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        QiNiuInitialize.getSingleton().put(getByte(), null, upToken, upCompletionHandler, uploadOptions);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void takePhoto(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = context.checkSelfPermission(Manifest.permission.CAMERA);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    requestPermissions(new String[] { Manifest.permission.CAMERA }, 101);
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            requestPermissions(new String[] { Manifest.permission.CAMERA },101);

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


//                        File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
//                        File file = new File(Environment.getExternalStorageDirectory()+"/", IMAGE_FILE_NAME);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file));

            }else {
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            }


            startActivityForResult(takeIntent, REQUESTCODE_TAKE);
        }else {
            Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
        }
    }

    //获取资源文件中的图片
    public byte[] getByte() {
//        Resources res = getResources();
//        Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.bike3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 80, baos);
        LogUtil.e("getByte===1", upBitmap+"===");
        upBitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
//        upBitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
        LogUtil.e("getByte===2", upBitmap+"==="+baos.toByteArray().length);

//        QiNiuInitialize.getSingleton().put(getByte(), null, upToken, upCompletionHandler, uploadOptions);

        return baos.toByteArray();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        m_myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        });

        LogUtil.e("dfaa===onActivityResult", requestCode+"==="+resultCode);

        switch (requestCode) {

            case 10:
                if (resultCode == RESULT_OK) {

                    String codenum = data.getStringExtra("codenum");
                    String lock_name = data.getStringExtra("lock_name");
                    int status = data.getIntExtra("status", 0);

                    LogUtil.e("requestCode===10", codenum+"==="+lock_name+"==="+status);


                    AddDispatchBean bean = new AddDispatchBean();
                    bean.setCar_number(codenum);
                    bean.setCar_type(lock_name);
                    bean.setCar_status(status);
                    datas.add(bean);

                    carList.add("\""+codenum+"\"");

                    myAdapter.notifyDataSetChanged();

//
//                    schoolText.setText(school_name);

                } else {
//                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }


                break;

            case REQUESTCODE_TAKE:// 调用相机拍照
                if (resultCode == RESULT_OK) {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("请稍等");
                        loadingDialog.show();
                    }

                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

                        File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
                        if (Uri.fromFile(temp) != null) {
                            urlpath = getRealFilePath(context, Uri.fromFile(temp));
                            LogUtil.e("REQUESTCODE_TAKE===", temp+"==="+urlpath);

                            Uri filepath = Uri.fromFile(temp);

                            compress();

                            rl_photo.setVisibility(View.VISIBLE);
                            iv_photo.setImageBitmap(upBitmap);
                            iv_add_photo.setVisibility(View.GONE);

                            LogUtil.e("REQUESTCODE_TAKE===3", upBitmap+"==="+filepath.getPath());

//                          uploadImage();
                        }else{

                        }

                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }

                    }else {
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }

                        Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context,"已取消！",Toast.LENGTH_SHORT).show();
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
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

//        imageUrlList.add(upBitmap);
//
//        LogUtil.e("compress===", "==="+imageUrlList.size());
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null,
                    null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    private void initHttp(){

//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("per_page", GlobalConfig.PAGE_SIZE);

        HttpHelper.get(context, Urls.carschoolaction, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                setFooterType(1);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                UIHelper.ToastError(context, throwable.toString());
                swipeRefreshLayout.setRefreshing(false);
                isRefresh = false;
                setFooterType(3);
                setFooterVisibility();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    LogUtil.e("initHttp===", "==="+responseString);

                    JSONArray array = new JSONArray(result.getData());
                    if (array.length() == 0 && showPage == 1) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(4);
                        return;
                    } else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1) {
                        footerLayout.setVisibility(View.GONE);
                        setFooterType(5);
                    } else if (array.length() < GlobalConfig.PAGE_SIZE) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(2);
                    } else if (array.length() >= 10) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(0);
                    }

//                    if(data!=null && data.size()>0){
//                        data.clear();
//                    }

//                    for (int i = 0; i < array.length(); i++) {
//                        CarSchoolBean bean = JSON.parseObject(array.getJSONObject(i).toString(), CarSchoolBean.class);
//                        data.add(bean);
//                    }
//
//                    myAdapter.notifyDataSetChanged();

//                    if ("Success".equals(result.getFlag())) {
//
//
//                    } else {
//                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//
//                        swipeRefreshLayout.setRefreshing(false);
//                        isRefresh = false;
//                        setFooterVisibility();
//                    }
                } catch (Exception e) {

                } finally {
                    swipeRefreshLayout.setRefreshing(false);
                    isRefresh = false;
                    setFooterVisibility();
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            scrollToFinishActivity();
            customDialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setFooterType(int type) {
        switch (type) {
            case 0:
                isLast = false;
                footerViewType01.setVisibility(View.VISIBLE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 1:
                isLast = false;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.VISIBLE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 2:
                isLast = true;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.VISIBLE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 3:
                isLast = false;
                // showPage -= 1;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.VISIBLE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 4:
                isLast = true;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.VISIBLE);
                break;
            case 5:
                isLast = true;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.GONE);
                break;
        }
    }

    private void setFooterVisibility() {
        if (footerView.getVisibility() == View.GONE) {
            footerView.setVisibility(View.VISIBLE);
        }
    }

    private class MyAdapter extends BaseViewAdapter<AddDispatchBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_add_dispatch_record, null);
            }

            //	车辆状态 0待投放 1正常 2锁定 3确认为坏车 4坏车已回收 5调运中 6报废

            TextView car_number = BaseViewHolder.get(convertView,R.id.item_car_number);
            TextView car_type = BaseViewHolder.get(convertView,R.id.item_car_type);
            TextView car_status = BaseViewHolder.get(convertView,R.id.item_car_status);
            Button btn_dispatch_delete = BaseViewHolder.get(convertView,R.id.btn_dispatch_delete);

            final AddDispatchBean bean = getDatas().get(position);

            car_number.setText(bean.getCar_number());
            car_type.setText(bean.getCar_type());

            int status = bean.getCar_status();
            car_status.setText(status==0?"待投放":status==1?"正常":status==2?"锁定":status==3?"确认为坏车":status==4?"坏车已回收":status==5?"调运中":"报废");

            btn_dispatch_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, DispatchDetailActivity.class);
////                    intent.putExtra("carmodel_id", carmodel_id);
////                    intent.putExtra("codenum", codenum);
//                    startActivity(intent);

                    datas.remove(bean);
                    carList.remove("\""+bean.getCar_number()+"\"");
                    myAdapter.notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }
}
