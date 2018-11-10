package com.qimalocl.manage.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseViewAdapter;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.core.common.DisplayUtil;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.ClearEditText;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.core.widget.MyListView;
import com.qimalocl.manage.model.HistorysRecordBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.TagBean;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator1 on 2017/11/9.
 */

public class BikeLocationActivity extends SwipeBackActivity implements View.OnClickListener {

    @BindView(R.id.ui_historysRecord_backBtn)
    ImageView backBtn;
    @BindView(R.id.ui_historysRecord_codeNumEdit)
    ClearEditText codeNumEdit;
    @BindView(R.id.ui_historysRecord_searchBtn)
    TextView searchBtn;
    @BindView(R.id.ui_historysRecord_msgText)
    TextView msgText;
    private Context context;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_historys_record);
        ButterKnife.bind(this);
        context = this;
        init();
    }

    private void init(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) codeNumEdit.getLayoutParams();
        params.width = DisplayUtil.getWindowWidth(this) * 3 / 5;
        codeNumEdit.setLayoutParams(params);
        backBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.ui_historysRecord_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.ui_historysRecord_searchBtn:
                String codenum = codeNumEdit.getText().toString().trim();
                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
                    UIHelper.goToAct(context,LoginActivity.class);
                    return;
                }
                if (codenum == null || "".equals(codenum)){
                    Toast.makeText(context,"请输入车辆编号",Toast.LENGTH_SHORT).show();
                    return;
                }
                initHttp(codenum);
                break;
            default:
                break;
        }
    }

    private void initHttp(final String codenum) {
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
        params.put("codenum",codenum);
        HttpHelper.get(context, Urls.carsLocation, params, new TextHttpResponseHandler() {
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
                Log.e("Test","RRRR:"+responseString);
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")){
                        JSONObject jsonObject = new JSONObject(result.getData());
                        if (!"[]".equals(result.getData())){
                            Intent intent = new Intent(context,MerchantAddressMapActivity.class);
                            intent.putExtra("latitude",jsonObject.getString("latitude"));
                            intent.putExtra("longitude",jsonObject.getString("longitude"));
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Test","异常:"+e);
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }
    private void delpoints(String fuid,int type) {
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
        params.put("fuid",fuid);
        params.put("type",type);
        HttpHelper.post(context, Urls.delpoints, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
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
                        Toast.makeText(context,"恭喜您，操作成功!",Toast.LENGTH_SHORT).show();
                        scrollToFinishActivity();
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
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
    private String linkTel = "";
    @SuppressLint("NewApi")
    private class MyAdapter extends BaseViewAdapter<HistorysRecordBean>{

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_history_record, null);
            }
            TextView realName = BaseViewHolder.get(convertView,R.id.item_historyRecord_realName);
            TextView tel = BaseViewHolder.get(convertView,R.id.item_historyRecord_tel);
            TextView codeNum = BaseViewHolder.get(convertView,R.id.item_historyRecord_codeNum);
            TextView money = BaseViewHolder.get(convertView,R.id.item_historyRecord_money);
            final HistorysRecordBean bean = getDatas().get(position);
            realName.setText("姓名："+bean.getUsername());
            tel.setText("联系电话:"+ bean.getTelphone());
            codeNum.setText(bean.getStart_end_date());
            money.setText("￥"+bean.getPrices());
            tel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Test","111111111");
                    linkTel = bean.getTelphone();
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkPermission = context.checkSelfPermission(Manifest.permission.CALL_PHONE);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                                requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 1);
                            } else {
                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开拨打电话权限！")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        BikeLocationActivity.this.requestPermissions(
                                                new String[] { Manifest.permission.CALL_PHONE }, 1);
                                    }
                                });
                                customBuilder.create().show();
                            }
                            return;
                        }
                    }
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("确认拨打" + bean.getTelphone() + "吗?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + bean.getTelphone()));
                            startActivity(intent);
                        }
                    });
                    customBuilder.create().show();
                }
            });
            return convertView;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (permissions[0].equals(Manifest.permission.CALL_PHONE)) {
                        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                        customBuilder.setTitle("温馨提示").setMessage("确认拨打" + linkTel + "吗?")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent intent=new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + linkTel));
                                startActivity(intent);
                            }
                        });
                        customBuilder.create().show();
                    }
                } else {
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                    customBuilder.setTitle("温馨提示").setMessage("您需要在设置里允许电话权限！")
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
                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
