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
import com.qimalocl.manage.core.common.AppManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator1 on 2017/11/9.
 */

public class HistorysRecordActivity extends SwipeBackActivity implements View.OnClickListener {

    @BindView(R.id.ui_historysRecord_backBtn)
    ImageView backBtn;
    @BindView(R.id.ui_historysRecord_codeNumEdit)
    ClearEditText codeNumEdit;
    @BindView(R.id.ui_historysRecord_searchBtn)
    TextView searchBtn;
    @BindView(R.id.ui_historysRecord_listview)
    MyListView listview;
    @BindView(R.id.ui_historysRecord_msgText)
    TextView msgText;
    private Context context;
//    private LoadingDialog loadingDialog;

    private List<HistorysRecordBean> datas;
    private MyAdapter myAdapter;

    private Dialog dialog;
    private LinearLayout tagMainLayout;
    private TagFlowLayout tagFlowLayout;
    private LinearLayout closeLayout;
    private LinearLayout affirmLayout;
    private TagAdapter tagAdapter;
    private List<TagBean> tagDatas;
    private int curPosition = 0;
    private int type = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_historys_record);
        ButterKnife.bind(this);
        context = this;
        datas = new ArrayList<>();
        tagDatas = new ArrayList<>();
        for (int i = 0; i < 8;i++){
            TagBean bean = new TagBean();
            switch (i){
                case 0:
                    bean.setType(3);
                    bean.setName("举报有效，经核实有效(+1)分");
                    break;
                case 1:
                    bean.setType(4);
                    bean.setName("违停一次(-20)分");
                    break;
                case 2:
                    bean.setType(5);
                    bean.setName("未关锁或未复位/未拨乱密码(-20)分");
                    break;
                case 3:
                    bean.setType(6);
                    bean.setName("校内未关锁导致单车丢失(-20)分");
                    break;
                case 4:
                    bean.setType(7);
                    bean.setName("非法移车（在非租车状态下骑车）(-20分)");
                    break;
                case 5:
                    bean.setType(8);
                    bean.setName("骑出校外，车辆找回，一次(-20)分");
                    break;
                case 6:
                    bean.setType(9);
                    bean.setName("骑出校外导致单车丢失(扣至0分)");
                    break;
                case 7:
                    bean.setType(10);
                    bean.setName("加装私锁(-100分)");
                    break;
                default:
                    break;
            }
            tagDatas.add(bean);
        }
        init();
    }

    private void init(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) codeNumEdit.getLayoutParams();
        params.width = DisplayUtil.getWindowWidth(this) * 3 / 5;
        codeNumEdit.setLayoutParams(params);

        dialog = new Dialog(context, R.style.main_publishdialog_style);
        View tagView = LayoutInflater.from(context).inflate(R.layout.dialog_deduct_mark, null);
        dialog.setContentView(tagView);
        dialog.setCanceledOnTouchOutside(false);

        tagMainLayout = tagView.findViewById(R.id.dialog_tag_mainLayout);
        tagFlowLayout = tagView.findViewById(R.id.dialog_deductMark_flowlayout);
        closeLayout = tagView.findViewById(R.id.dialog_deductMark_closeLayout);
        affirmLayout = tagView.findViewById(R.id.dialog_deductMark_affirmLayout);

        LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams)tagMainLayout.getLayoutParams();
        params4.width = DisplayUtil.getWindowWidth(this) * 4 / 5;
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

        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(datas);
        listview.setAdapter(myAdapter);
        backBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);

        closeLayout.setOnClickListener(this);
        affirmLayout.setOnClickListener(this);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curPosition = position;
                WindowManager.LayoutParams params1 = dialog.getWindow().getAttributes();
                params1.width = LinearLayout.LayoutParams.MATCH_PARENT;
                params1.height = LinearLayout.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setAttributes(params1);
                dialog.show();
            }
        });
        tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener(){
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent){
                return true;
            }
        });
        tagFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener(){
            @Override
            public void onSelected(Set<Integer> selectPosSet){

            }
        });
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
            case R.id.dialog_deductMark_closeLayout:
                if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
                break;
            case R.id.dialog_deductMark_affirmLayout:
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
                CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
                customBuilder.setTitle("温馨提示").setMessage("是否确定提交?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        delpoints(myAdapter.getDatas().get(curPosition).getUid(),type);
                    }
                });
                customBuilder.create().show();
                if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
                break;
            default:
                break;
        }
    }

    private void initHttp(String codenum) {
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
        HttpHelper.get(context, Urls.historys, params, new TextHttpResponseHandler() {
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
                        JSONArray array = new JSONArray(result.getData());
                        if (0 == array.length()) {
                            msgText.setVisibility(View.VISIBLE);
                            listview.setVisibility(View.GONE);
                            msgText.setText("对不起,暂无信息");
                        }else {
                            msgText.setVisibility(View.GONE);
                            listview.setVisibility(View.VISIBLE);
                            if (datas.size() != 0){
                                datas.clear();
                            }
                            for (int i = 0; i < 4;i++){
                                HistorysRecordBean bean = JSON.parseObject(array.getJSONObject(i).toString(),HistorysRecordBean.class);
                                datas.add(bean);
                            }
                            myAdapter.notifyDataSetChanged();
                        }
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
                                        HistorysRecordActivity.this.requestPermissions(
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
