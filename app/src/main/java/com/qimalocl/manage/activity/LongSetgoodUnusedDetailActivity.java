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
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseViewAdapter;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.model.BadCarBean;
import com.qimalocl.manage.model.GlobalConfig;
import com.qimalocl.manage.model.ResultConsel;
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

public class LongSetgoodUnusedDetailActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
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
    private List<BadCarBean> data;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;

    private String date = "";
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_setgood_unused_detail);
        context = this;
        data = new ArrayList<>();

//        date = getIntent().getStringExtra("date");
        type = getIntent().getIntExtra("type", 1);

        initView();
    }

    private void initView(){

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText(type==1?"沉淀车辆":"修好未使用");
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
    private void initHttp(){
        LogUtil.e("sguda===initHttp", date+"==="+type+"==="+showPage);

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
            return;
        }
        RequestParams params = new RequestParams();
        params.put("type", type);
        params.put("school_id", school_id);
        params.put("page", showPage);
        params.put("pagesize", GlobalConfig.PAGE_SIZE);


        LogUtil.e("sguda===0", "===");

        HttpHelper.get(context, Urls.long_setgood_unused, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在加载");
//                    loadingDialog.show();
//                }

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setFooterType(1);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        UIHelper.ToastError(context, throwable.toString());
                        swipeRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                        setFooterType(3);
                        setFooterVisibility();
                    }
                });

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            LogUtil.e("sguda===1", "==="+responseString);


                            JSONArray array = new JSONArray(result.getData());

                            LogUtil.e("sguda===2", "==="+array);

                            if (array.length() == 0 && showPage == 1) {

                                footerLayout.setVisibility(View.VISIBLE);
                                setFooterType(4);
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

                            for (int i = 0; i < array.length();i++){
                                BadCarBean bean = JSON.parseObject(array.getJSONObject(i).toString(), BadCarBean.class);

                                data.add(bean);
                            }

                            LogUtil.e("sguda===3", "===");


                        } catch (Exception e) {
                            e.printStackTrace();

                            footerLayout.setVisibility(View.VISIBLE);
                            setFooterType(4);
                        } finally {
                            swipeRefreshLayout.setRefreshing(false);
                            isRefresh = false;
                            setFooterVisibility();
                        }
                    }
                });

//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
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

    private class MyAdapter extends BaseViewAdapter<BadCarBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_long_setgood_unused_detail, null);
            }

            TextView tv_date = BaseViewHolder.get(convertView,R.id.item_date);
            TextView number = BaseViewHolder.get(convertView,R.id.tv_number);
            TextView last_user_phone = BaseViewHolder.get(convertView,R.id.tv_last_user_phone);
            TextView last_user_time = BaseViewHolder.get(convertView,R.id.tv_last_user_time);
            TextView bad_reason = BaseViewHolder.get(convertView,R.id.tv_bad_reason);
            TextView setgood_reason = BaseViewHolder.get(convertView,R.id.tv_setgood_reason);
            TextView bad_time = BaseViewHolder.get(convertView,R.id.tv_bad_time);
            TextView recycle_time = BaseViewHolder.get(convertView,R.id.tv_recycle_time);
            TextView setgood_time = BaseViewHolder.get(convertView,R.id.tv_setgood_time);
            LinearLayout ll_last_user_phone = BaseViewHolder.get(convertView,R.id.ll_last_user_phone);
            LinearLayout ll_last_user_time = BaseViewHolder.get(convertView,R.id.ll_last_user_time);
            LinearLayout ll_bad_reason = BaseViewHolder.get(convertView,R.id.ll_bad_reason);
            LinearLayout ll_setgood_reason = BaseViewHolder.get(convertView,R.id.ll_setgood_reason);
            LinearLayout ll_bad_time = BaseViewHolder.get(convertView,R.id.ll_bad_time);
            LinearLayout ll_recycle_time = BaseViewHolder.get(convertView,R.id.ll_recycle_time);
            LinearLayout ll_setgood_time = BaseViewHolder.get(convertView,R.id.ll_setgood_time);

            BadCarBean bean = getDatas().get(position);
            number.setText(bean.getNumber());
            last_user_phone.setText(bean.getLast_user_phone());
            last_user_time.setText(bean.getLast_user_time());
            bad_reason.setText(bean.getBad_reason());
            setgood_reason.setText(bean.getSetgood_reason());
            bad_time.setText(bean.getBad_time());
            recycle_time.setText(bean.getRecycle_time());
            setgood_time.setText(bean.getSetgood_time());


            if(type==2){
                ll_last_user_phone.setVisibility(View.GONE);
                ll_last_user_time.setVisibility(View.GONE);
                ll_bad_reason.setVisibility(View.VISIBLE);
                ll_setgood_reason.setVisibility(View.VISIBLE);
                ll_bad_time.setVisibility(View.VISIBLE);
                ll_recycle_time.setVisibility(View.VISIBLE);
                ll_setgood_time.setVisibility(View.VISIBLE);

                if(bean.isLoad()){
                    if(bean.isShowDate()){
                        tv_date.setVisibility(View.VISIBLE);
                        tv_date.setText(bean.getSetgood_time().split(" ")[0]);
                    }else{
                        tv_date.setVisibility(View.GONE);
                        tv_date.setText("");
                    }

                }else{
                    if(!date.equals(bean.getSetgood_time().split(" ")[0])){
                        date = bean.getSetgood_time().split(" ")[0];
                        bean.setShowDate(true);

                        tv_date.setVisibility(View.VISIBLE);
                        tv_date.setText(bean.getSetgood_time().split(" ")[0]);
                    }else{
                        bean.setShowDate(false);
                        tv_date.setVisibility(View.GONE);
                        tv_date.setText("");
                    }

                    bean.setLoad(true);
                }
            }else{
                ll_last_user_phone.setVisibility(View.VISIBLE);
                ll_last_user_time.setVisibility(View.VISIBLE);
                ll_bad_reason.setVisibility(View.GONE);
                ll_setgood_reason.setVisibility(View.GONE);
                ll_bad_time.setVisibility(View.GONE);
                ll_recycle_time.setVisibility(View.GONE);
                ll_setgood_time.setVisibility(View.GONE);

                if(bean.isLoad()){
                    if(bean.isShowDate()){
                        tv_date.setVisibility(View.VISIBLE);
                        tv_date.setText(bean.getLast_user_time().split(" ")[0]);
                    }else{
                        tv_date.setVisibility(View.GONE);
                        tv_date.setText("");
                    }

                }else{
                    if(!date.equals(bean.getLast_user_time().split(" ")[0])){
                        date = bean.getLast_user_time().split(" ")[0];
                        bean.setShowDate(true);

                        tv_date.setVisibility(View.VISIBLE);
                        tv_date.setText(bean.getLast_user_time().split(" ")[0]);
                    }else{
                        bean.setShowDate(false);
                        tv_date.setVisibility(View.GONE);
                        tv_date.setText("");
                    }

                    bean.setLoad(true);
                }

            }



//
//
//            if(bean.getCreated_at()==null){
//                created_at.setText("");
//            }else{
//                created_at.setText("借车:"+ bean.getCreated_at());
//
//                LogUtil.e("getView===", date+"==="+bean.getCreated_at().split(" ")[0]+"==="+bean.getCreated_at());
//

//            }

            return convertView;
        }
    }
}
