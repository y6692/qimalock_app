package com.qimalocl.manage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseViewAdapter;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.model.GlobalConfig;
import com.qimalocl.manage.model.HistorysRecordBean;
import com.qimalocl.manage.model.OtherIncomeBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.qimalocl.manage.utils.LogUtil;

import org.apache.http.Header;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.qimalocl.manage.base.BaseApplication.school_id;


/**
 * Created by Administrator1 on 2017/2/13.
 */

public class OtherIncomeActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
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
    private List<OtherIncomeBean> data;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;

    private String date;
    private int type;

    private int lock_id;
    private String lock_title;

    private String begintime;
    private String endtime;

    TextView tv_day, tv_order_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_income);
        context = this;
        data = new ArrayList<>();

        lock_id = getIntent().getIntExtra("lock_id", 0);
        lock_title = getIntent().getStringExtra("lock_title");
//        type = getIntent().getIntExtra("type", 1);

        initView();
    }

    private void initView(){

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("其他收入");
//        rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
//        rightBtn.setText("查询");

        tv_day = (TextView) findViewById(R.id.tv_day);
        tv_order_amount = (TextView) findViewById(R.id.tv_order_amount);

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

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        String dateString="";
        try {
            calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
            date=calendar.getTime(); //这个时间就是日期往后推一天的结果
            endtime = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Date datenow = new Date(System.currentTimeMillis());
        begintime = simpleDateFormat.format(datenow);

        Log.e("cia===onCreate", begintime+"==="+endtime);

        tv_day.setText(begintime+" 到 "+endtime);

        myList.setOnItemClickListener(this);
        if(data.isEmpty()){
            initHttp();
        }

        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(data);
        myList.setAdapter(myAdapter);

        backImg.setOnClickListener(this);
        tv_day.setOnClickListener(this);
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
            case R.id.tv_day:
                Intent intent = new Intent(context, HistoryRoadFiltateActivity.class);
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

    private void initHttp(){


//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        List<String> timeList = new ArrayList<>();
        timeList.add(begintime);
        timeList.add(endtime);

        LogUtil.e("cia===initHttp", school_id+"==="+timeList);

        RequestParams params = new RequestParams();
//        params.put("car_number", codenum);
        params.put("school_id", school_id);
        params.put("time", gson.toJson(timeList));
        params.put("page", showPage);
        params.put("per_page", GlobalConfig.PAGE_SIZE);


        HttpHelper.get(context, Urls.otherorder, params, new TextHttpResponseHandler() {

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

                    LogUtil.e("oia===initHttp1", "==="+responseString);

                    JSONArray array = new JSONArray(result.getData());
                    if (array.length() == 0 && showPage == 1) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(4);
                        return;
                    }else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1) {
                        footerLayout.setVisibility(View.GONE);
                        setFooterType(5);
                    } else if (array.length() < GlobalConfig.PAGE_SIZE) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(2);
                    } else if (array.length() >= GlobalConfig.PAGE_SIZE) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(0);
                    }

                    for (int i = 0; i < array.length(); i++) {
                        OtherIncomeBean bean = JSON.parseObject(array.getJSONObject(i).toString(), OtherIncomeBean.class);
                        data.add(bean);
                    }

//                    myAdapter.notifyDataSetChanged();


                    otherorder_statistics();

                } catch (Exception e) {
                    footerLayout.setVisibility(View.VISIBLE);
                    setFooterType(4);
                } finally {
                    swipeRefreshLayout.setRefreshing(false);
                    isRefresh = false;
                    setFooterVisibility();
                }
            }
        });
    }

    private void otherorder_statistics(){
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        List<String> timeList = new ArrayList<>();
        timeList.add(begintime);
        timeList.add(endtime);

        LogUtil.e("cia===initHttp", school_id+"==="+timeList);

        RequestParams params = new RequestParams();
        params.put("school_id", school_id);
        params.put("time", gson.toJson(timeList));

        HttpHelper.get(context, Urls.otherorder_statistics, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
//                setFooterType(1);
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
//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("otherorder_s1", "==="+responseString);

                            JSONObject json = JSON.parseObject(responseString);

//
                            tv_order_amount.setText("共计："+json.getString("order_amount")+"元");

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent idata) {
        switch (requestCode) {
            case 0:
                if (idata != null) {
                    begintime = idata.getExtras().getString("starttime");
                    endtime = idata.getExtras().getString("endtime");

                    tv_day.setText(begintime+" 到 "+endtime);

                    LogUtil.e("onActivityResult===",begintime+"==="+endtime+"==="+data);

                    tv_order_amount.setText("");
                    onRefresh();

//                    if(data.size()!=0){
//                        myAdapter.getDatas().clear();
//                        myAdapter.notifyDataSetChanged();
//                    }
//
//                    tv_order_amount.setText("");
//                    initHttp();
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

    private class MyAdapter extends BaseViewAdapter<OtherIncomeBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_other_income, null);
            }

            TextView tv_user_name= BaseViewHolder.get(convertView,R.id.tv_user_name);
            TextView tv_user_phone = BaseViewHolder.get(convertView,R.id.tv_user_phone);
            TextView tv_carmodel_id = BaseViewHolder.get(convertView,R.id.tv_carmodel_id);
            TextView tv_type = BaseViewHolder.get(convertView,R.id.tv_type);
            TextView tv_order_amount = BaseViewHolder.get(convertView,R.id.tv_order_amount);
            TextView tv_verify_status = BaseViewHolder.get(convertView,R.id.tv_verify_status);
            TextView tv_order_state = BaseViewHolder.get(convertView,R.id.tv_order_state);
            TextView tv_admin_name = BaseViewHolder.get(convertView,R.id.tv_admin_name);
            TextView tv_created_at = BaseViewHolder.get(convertView,R.id.tv_created_at);

            OtherIncomeBean bean = getDatas().get(position);
            tv_user_name.setText(bean.getUser_name());
            tv_user_phone.setText(bean.getUser_phone());

            int carmodel_id = bean.getCarmodel_id();    //车型ID (1、单车 2、助力车)
            tv_carmodel_id.setText(carmodel_id==1?"单车":"助力车");

            int type = bean.getType();   //订单类型 (1、调度单 2、赔偿单)
            tv_type.setText(type==1?"调度单":"赔偿单");

            tv_order_amount.setText(bean.getOrder_amount());

            int verify_status = bean.getVerify_status();  //审核状态 (0、待审核 1、未通过 2、通过)
            tv_verify_status.setText((verify_status==0?"待审核":verify_status==1?"未通过":"通过"));


            int state = bean.getOrder_state();  //订单状态 (0、已取消 10、待支付 20、已完成)
            tv_order_state.setText((state==0?"已取消":state==10?"待支付":"已完成"));

            tv_admin_name.setText(bean.getAdmin_name());
            tv_created_at.setText(bean.getCreated_at());


            return convertView;
        }
    }
}