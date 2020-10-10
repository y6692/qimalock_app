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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
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
import com.qimalocl.manage.model.PowerExchangeBean;
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

public class ExchangePowerRecordActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
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
    private List<PowerExchangeBean> data;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;

    private boolean isManager;
    private int admin_id;
    private String timeJson;

    private String begintime;
    private String endtime;

    LinearLayout ll_day;
    TextView tv_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_power_record);
        context = this;
        data = new ArrayList<>();




        initView();
    }

    private void initView(){

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("换电记录");
//        rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
//        rightBtn.setText("查询");

        ll_day = (LinearLayout) findViewById(R.id.ll_day);
        tv_day = (TextView) findViewById(R.id.tv_day);


        isManager = getIntent().getBooleanExtra("isManager", false);

        if(isManager){
            ll_day.setVisibility(View.GONE);
            timeJson = getIntent().getStringExtra("time");
        }else{
            ll_day.setVisibility(View.VISIBLE);

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
        }
        admin_id = getIntent().getIntExtra("admin_id", 0);






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
        tv_day.setOnClickListener(this);
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

        LogUtil.e("epra===initHttp", school_id+"==="+admin_id+"==="+showPage);

//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
//        params.put("page", showPage);
        params.put("admin_id", admin_id);
        params.put("school_id", school_id);
        params.put("time", timeJson);

        HttpHelper.get(context, Urls.carbatteryaction_count, params, new TextHttpResponseHandler() {

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

                    LogUtil.e("epra===initHttp1", "==="+responseString);

//                    footerViewType01 = footerView.findViewById(R.id.footer_Layout_type01);// 点击加载更多
//                    footerViewType02 = footerView.findViewById(R.id.footer_Layout_type02);// 正在加载，请您稍等
//                    footerViewType03 = footerView.findViewById(R.id.footer_Layout_type03);// 已无更多
//                    footerViewType04 = footerView.findViewById(R.id.footer_Layout_type04);// 刷新失败，请重试
//                    footerViewType05 = footerView.findViewById(R.id.footer_Layout_type05);// 暂无数据

                    JSONArray array = new JSONArray(result.getData());
//                    if (array.length() == 0 && showPage == 1) {// 暂无数据
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(4);
//                        return;
//                    } else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1) {
//                        footerLayout.setVisibility(View.GONE);
//                        setFooterType(5);
//                    } else if (array.length() < GlobalConfig.PAGE_SIZE) {// 已无更多
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(2);
//                    } else if (array.length() >= 10) {// 点击加载更多
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(0);
//                    }

                    if ((array==null || array.length() == 0) && showPage == 1) {
                        footerLayout.setVisibility(View.VISIBLE);
                        setFooterType(4);
                        return;
                    }else{
                        footerLayout.setVisibility(View.GONE);
                    }

//                    if (array.length() == 0 && showPage == 1) {// 暂无数据
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(4);
//                        return;
//                    } else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1) {
//                        footerLayout.setVisibility(View.GONE);
//                        setFooterType(5);
//                    } else if (array.length() < GlobalConfig.PAGE_SIZE) {// 已无更多
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(2);
//                    } else if (array.length() >= 10) {// 点击加载更多
//                        footerLayout.setVisibility(View.VISIBLE);
//                        setFooterType(0);
//                    }
//                    setFooterType(0);

                    LogUtil.e("epra===initHttp2", "==="+array.length());

                    for (int i = 0; i < array.length(); i++) {
                        PowerExchangeBean bean = JSON.parseObject(array.getJSONObject(i).toString(), PowerExchangeBean.class);
                        data.add(bean);
                    }

                    myAdapter.notifyDataSetChanged();

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
            case 0: // 点击加载更多
                isLast = false;
                showPage += 1;
                footerViewType01.setVisibility(View.VISIBLE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 1: // 正在加载，请您稍等
                isLast = false;
//                showPage += 1;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.VISIBLE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 2: // 已无更多
                isLast = true;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.VISIBLE);
                footerViewType04.setVisibility(View.GONE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 3: // 刷新失败，请重试
                isLast = false;
                // showPage -= 1;
                footerViewType01.setVisibility(View.GONE);
                footerViewType02.setVisibility(View.GONE);
                footerViewType03.setVisibility(View.GONE);
                footerViewType04.setVisibility(View.VISIBLE);
                footerViewType05.setVisibility(View.GONE);
                break;
            case 4:// 暂无数据
                isLast = true;
//                showPage += 1;
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

    private class MyAdapter extends BaseViewAdapter<PowerExchangeBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_exchange_power_record, null);
            }

            LinearLayout ll_valid_power_exchange = BaseViewHolder.get(convertView,R.id.ll_valid_power_exchange);
            LinearLayout ll_invalid_power_exchange = BaseViewHolder.get(convertView,R.id.ll_invalid_power_exchange);
            LinearLayout ll_power_exchanging = BaseViewHolder.get(convertView,R.id.ll_power_exchanging);

            TextView date = BaseViewHolder.get(convertView,R.id.tv_date);
            TextView car_course = BaseViewHolder.get(convertView,R.id.tv_car_course);
            TextView car_valid = BaseViewHolder.get(convertView,R.id.tv_car_valid);
            TextView car_invalid = BaseViewHolder.get(convertView,R.id.tv_car_invalid);

            final PowerExchangeBean bean = getDatas().get(position);
            date.setText(bean.getDate());
            car_course.setText(bean.getCar_course());
            car_valid.setText(bean.getCar_valid());
            car_invalid.setText(bean.getCar_invalid());


            ll_power_exchanging.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ExchangePowerDetailActivity.class);
//                    intent.putExtra("date", "2020-2-18");
                    intent.putExtra("date", bean.getDate());
                    intent.putExtra("admin_id", admin_id);
                    intent.putExtra("status", 0);
                    context.startActivity(intent);
                }

            });

            ll_valid_power_exchange.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ExchangePowerDetailActivity.class);
                    intent.putExtra("date", bean.getDate());
                    intent.putExtra("admin_id", admin_id);
                    intent.putExtra("status", 1);
                    context.startActivity(intent);
                }

            });

            ll_invalid_power_exchange.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ExchangePowerDetailActivity.class);
                    intent.putExtra("date", bean.getDate());
                    intent.putExtra("admin_id", admin_id);
                    intent.putExtra("status", 2);
                    context.startActivity(intent);
                }

            });

            return convertView;
        }
    }
}
