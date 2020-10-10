package com.qimalocl.manage.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseViewAdapter;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.model.GlobalConfig;
import com.qimalocl.manage.model.OverAreaDetailBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.ScrappedDetailBean;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.qimalocl.manage.utils.LogUtil;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.qimalocl.manage.base.BaseApplication.school_id;


/**
 * Created by Administrator1 on 2017/2/13.
 */

public class SuperzoneDetailActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {

    private Context context;
    private ImageView backImg;
    private TextView title;
    private TextView rightBtn;
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
    private List<OverAreaDetailBean> data;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;

    private String date;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superzone_detail);
        context = this;
        data = new ArrayList<>();

        date = getIntent().getStringExtra("date");
//        type = getIntent().getIntExtra("type", 1);

        initView();
    }

    private void initView(){

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("超区车辆");
//        rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
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
        myList.addFooterView(footerView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        myList.setOnItemClickListener(this);
        if(data.isEmpty()){
            initHttp();
        }

        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(data);
        myList.setAdapter(myAdapter);

        backImg.setOnClickListener(this);
//        rightBtn.setOnClickListener(this);
        footerLayout.setOnClickListener(this);
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
        if(data.size()!=0){
            myAdapter.notifyDataSetChanged();
        }
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
            if(data.size()!=0){
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
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
                Intent intent = new Intent(context,HistoryRoadFiltateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,0);
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


//    private void over_area_cars(){
//        LogUtil.e("over_area_cars===", "===");
//
////        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        if (access_token != null && !"".equals(access_token)){
////            RequestParams params = new RequestParams();
////            params.put("uid",uid);
////            params.put("access_token",access_token);
//            HttpHelper.get(context, Urls.over_area_cars, new TextHttpResponseHandler() {
//                @Override
//                public void onStart() {
//                    onStartCommon("正在加载");
//                }
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    onFailureCommon(throwable.toString());
//                    LogUtil.e("over_area_cars===fail", "==="+throwable.toString());
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, final String responseString) {
//                    m_myHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//
//                                LogUtil.e("over_area_cars===1", tv_superzone_count+"==="+responseString);
//
//                                com.alibaba.fastjson.JSONObject object = JSON.parseObject(result.getData());
//
////                                ScrappedBean bean = JSON.parseObject(result.getData(), ScrappedBean.class);
////
//                                LogUtil.e("over_area_cars===2", "==="+object.getString("count"));
//
//                                tv_superzone_count.setText(""+object.getString("count"));
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//
//                                LogUtil.e("lowpower===e", "==="+e);
//                            }
//                            if (loadingDialog != null && loadingDialog.isShowing()){
//                                loadingDialog.dismiss();
//                            }
//                        }
//                    });
//
//                }
//            });
//        }else {
//            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//            UIHelper.goToAct(context,LoginActivity.class);
//        }
//    }

    private void initHttp(){
        LogUtil.e("over_area_cars===initHttp", school_id+"==="+date+"==="+type+"==="+showPage);

//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("school_id", school_id);

        HttpHelper.get(context, Urls.over_area_cars, params, new TextHttpResponseHandler() {

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

                    LogUtil.e("over_area_cars===initHttp1", "==="+responseString);

                    com.alibaba.fastjson.JSONObject object = JSON.parseObject(result.getData());

//                  ScrappedBean bean = JSON.parseObject(result.getData(), ScrappedBean.class);

                    JSONArray array = new JSONArray(object.getString("data"));

                    LogUtil.e("over_area_cars===2", array+"==="+showPage);

                    if ((array==null || array.length() == 0) && showPage == 1) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(4);
//                        footerLayout.setVisibility(View.GONE);
                        return;
                    }else{
                        footerLayout.setVisibility(View.GONE);
                    }

//                    if (array.length() == 0 && showPage == 1) {
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(4);
//                        return;
//                    } else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1) {
//                        footerLayout.setVisibility(View.GONE);
//                        setFooterType(5);
//                    } else if (array.length() < GlobalConfig.PAGE_SIZE) {
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(2);
//                    } else if (array.length() >= 10) {
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(0);
//                    }
                    for (int i = 0; i < array.length(); i++) {
                        OverAreaDetailBean bean = JSON.parseObject(array.getJSONObject(i).toString(), OverAreaDetailBean.class);
                        data.add(bean);
                    }

//                    RecycleDetailBean bean = new RecycleDetailBean();
//                    data.add(bean);

                    myAdapter.notifyDataSetChanged();

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
                    footerLayout.setVisibility(View.VISIBLE);
                    setFooterType(4);
                } finally {
                    swipeRefreshLayout.setRefreshing(false);
                    isRefresh = false;
//                    setFooterVisibility();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
//                    starttime = data.getExtras().getString("starttime");
//                    endtime = data.getExtras().getString("endtime");
                    onRefresh();
                }
                break;

            default:
                break;
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

    private class MyAdapter extends BaseViewAdapter<OverAreaDetailBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_superzone_detail, null);
            }

            TextView tv_number = BaseViewHolder.get(convertView,R.id.tv_number);
            TextView tv_username = BaseViewHolder.get(convertView,R.id.tv_username);
            TextView tv_telphone = BaseViewHolder.get(convertView,R.id.tv_telphone);
            TextView tv_start_time = BaseViewHolder.get(convertView,R.id.tv_start_time);
            TextView tv_end_time = BaseViewHolder.get(convertView,R.id.tv_end_time);
            LinearLayout ll_telphone = BaseViewHolder.get(convertView,R.id.ll_telphone);

            OverAreaDetailBean bean = getDatas().get(position);
            tv_number.setText(bean.getNumber());
            tv_username.setText(bean.getUser_name());
            final String phone = bean.getUser_phone();
            tv_telphone.setText(bean.getUser_phone());
            tv_start_time.setText(bean.getStart_time());
            tv_end_time.setText(bean.getEnd_time());

//            if(status==0){
//                text_aft_electricity.setText("当前电量：");
//            }else{
//                text_aft_electricity.setText("更换后电量：");
//            }

            ll_telphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.e("Test","111111111");
//                    linkTel = bean.getTelphone();
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
                                        requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 1);
                                    }
                                });
                                customBuilder.create().show();
                            }
                            return;
                        }
                    }
                    CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                    customBuilder.setTitle("温馨提示").setMessage("确认拨打" + phone + "吗?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + phone));
                            startActivity(intent);
                        }
                    });
                    customBuilder.create().show();
                }
            });

            return convertView;
        }
    }
}
