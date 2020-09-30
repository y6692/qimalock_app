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
import android.widget.RelativeLayout;
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
import com.qimalocl.manage.model.CarbatteryactionDispatcherStatisticsBean;
import com.qimalocl.manage.model.CyclingRefundBean;
import com.qimalocl.manage.model.GlobalConfig;
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

public class CarbatteryactionDispatcherStatisticsActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
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
    private List<CarbatteryactionDispatcherStatisticsBean> data;
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
    String timeJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carbatteryaction_dispatcher_statistics);
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
        title.setText("换电统计");
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


        Gson gson = new Gson();
        List<String> timeList = new ArrayList<>();
        timeList.add(begintime);
        timeList.add(endtime);

        timeJson = gson.toJson(timeList);


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


    private void initHttp(){
//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }

//        Gson gson = new Gson();
//        List<String> timeList = new ArrayList<>();
//        timeList.add(begintime);
//        timeList.add(endtime);

        LogUtil.e("cdsa===initHttp", school_id+"==="+timeJson);

        RequestParams params = new RequestParams();
//        params.put("car_number", codenum);
        params.put("school_id", school_id);
        params.put("time", timeJson);
        params.put("page", showPage);
        params.put("per_page", GlobalConfig.PAGE_SIZE);

        tv_order_amount.setText("");
        HttpHelper.get(context, Urls.carbatteryaction_dispatcher_statistics, params, new TextHttpResponseHandler() {

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

                    LogUtil.e("cia===initHttp1", "==="+responseString);

                    JSONArray array = new JSONArray(result.getData());
                    if (array.length() == 0 && showPage == 1) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(4);
                        return;
                    }else{
                        footerLayout.setVisibility(View.GONE);
                    }
//                    else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1) {
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
                        CarbatteryactionDispatcherStatisticsBean bean = JSON.parseObject(array.getJSONObject(i).toString(), CarbatteryactionDispatcherStatisticsBean.class);
                        data.add(bean);
                    }

                    myAdapter.notifyDataSetChanged();


//                    refundorder_statistics();

                } catch (Exception e) {

                } finally {
                    swipeRefreshLayout.setRefreshing(false);
                    isRefresh = false;
//                    setFooterVisibility();
                }
            }
        });
    }

    private void refundorder_statistics(){
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

        HttpHelper.get(context, Urls.refundorder_statistics, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                setFooterType(1);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                UIHelper.ToastError(context, throwable.toString());
//                swipeRefreshLayout.setRefreshing(false);
//                isRefresh = false;
//                setFooterType(3);
//                setFooterVisibility();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("cyclingorder_s1", "==="+responseString);

                            JSONObject json = JSON.parseObject(responseString);

//
                            tv_order_amount.setText("共计："+json.getString("refund_amount")+"元");

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

                    Gson gson = new Gson();
                    List<String> timeList = new ArrayList<>();
                    timeList.add(begintime);
                    timeList.add(endtime);

                    timeJson = gson.toJson(timeList);

//                    onRefresh();

                    if(data.size()!=0){
                        myAdapter.getDatas().clear();
                        myAdapter.notifyDataSetChanged();
                    }
                    initHttp();
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

    private class MyAdapter extends BaseViewAdapter<CarbatteryactionDispatcherStatisticsBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_carbatteryaction_dispatcher_statistics, null);
            }

            RelativeLayout rl_item = BaseViewHolder.get(convertView, R.id.rl_item);
            TextView tv_name = BaseViewHolder.get(convertView, R.id.tv_name);
            TextView tv_count = BaseViewHolder.get(convertView, R.id.tv_count);

            final CarbatteryactionDispatcherStatisticsBean bean = getDatas().get(position);
            tv_name.setText(bean.getName());
            tv_count.setText(bean.getCount()+"次");

//            int state = bean.getRefund_state();  //订单状态 (0、待退款 1、退款成功 2、退款失败)
//            tv_refund_state.setText(""+(state==0?"待退款":state==1?"退款成功":"退款失败"));
//
//            tv_refund_amount.setText(bean.getRefund_amount());
//            tv_refund_time.setText(bean.getRefund_time());

            rl_item.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ExchangePowerRecordActivity.class);
                    intent.putExtra("isManager", true);
                    intent.putExtra("admin_id", bean.getId());
                    intent.putExtra("time", timeJson);
                    context.startActivity(intent);
                }

            });

            return convertView;
        }
    }
}
