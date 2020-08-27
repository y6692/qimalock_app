package com.qimalocl.manage.activity;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.model.LatLng;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseViewAdapter;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.fragment.ScanFragment;
import com.qimalocl.manage.model.CarDispatchBean;
import com.qimalocl.manage.model.CarSchoolBean;
import com.qimalocl.manage.model.GlobalConfig;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.qimalocl.manage.utils.LogUtil;
import com.qimalocl.manage.utils.ToastUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator1 on 2017/2/13.
 */

public class CarDispatchActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {

    private Context context;
    private ImageView backImg;
    private TextView title;
    private ImageView rightBtn;
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
    private List<CarDispatchBean> data;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;
    private String starttime = "";
    private String endtime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_dispatch);
        context = this;
        data = new ArrayList<>();
        initView();
    }

    private void initView(){

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("车辆调运");
        rightBtn = (ImageView)findViewById(R.id.mainUI_title_rightBtn);
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

        }

        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(data);
        myList.setAdapter(myAdapter);

        backImg.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
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
//        if(data.size()!=0){
//            myAdapter.notifyDataSetChanged();
//        }

        initHttp();
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
                Intent intent = new Intent(context, AddDispatchActivity.class);
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
        RequestParams params = new RequestParams();
        params.put("per_page", GlobalConfig.PAGE_SIZE);

        HttpHelper.get(context, Urls.transports, params, new TextHttpResponseHandler() {

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

                    LogUtil.e("cda===initHttp1", "==="+responseString);

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

                    if(data!=null && data.size()>0){
                        data.clear();
                    }

                    for (int i = 0; i < array.length(); i++) {
                        CarDispatchBean bean = JSON.parseObject(array.getJSONObject(i).toString(), CarDispatchBean.class);
                        data.add(bean);
                    }

                    myAdapter.notifyDataSetChanged();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    starttime = data.getExtras().getString("starttime");
                    endtime = data.getExtras().getString("endtime");
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


    private class MyAdapter extends BaseViewAdapter<CarDispatchBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_car_dispatch_record, null);
            }

            TextView tv_sn = BaseViewHolder.get(convertView,R.id.item_sn);
            TextView tv_status = BaseViewHolder.get(convertView,R.id.item_status);
            TextView tv_created_at = BaseViewHolder.get(convertView,R.id.item_created_at);
            TextView tv_end_at = BaseViewHolder.get(convertView,R.id.item_end_at);
            TextView tv_car_numbers = BaseViewHolder.get(convertView,R.id.item_car_numbers);
            Button btn_dispatch_detail = BaseViewHolder.get(convertView,R.id.btn_dispatch_detail);
            Button btn_confirm_finish = BaseViewHolder.get(convertView,R.id.btn_confirm_finish);

            final CarDispatchBean bean = getDatas().get(position);

            tv_sn.setText("调运单号："+bean.getSn());
            String status = bean.getStatus();
            if("0".equals(status)){
                btn_confirm_finish.setVisibility(View.VISIBLE);
                tv_status.setText("调运中");
            }else{
                btn_confirm_finish.setVisibility(View.GONE);
                tv_status.setText("已完成");
            }

            tv_created_at.setText("开始时间："+bean.getStart_time());
            if(bean.getEnd_time()==null || "".equals(bean.getEnd_time())){
                tv_end_at.setVisibility(View.GONE);
            }else{
                tv_end_at.setVisibility(View.VISIBLE);
                tv_end_at.setText("结束时间："+bean.getEnd_time());
            }


            try {
                JSONArray jsonArray = new JSONArray(bean.getCars());
                List carList = new ArrayList();
                String cars="";

                for (int i = 0; i < jsonArray.length(); i++) {
                    if(i<jsonArray.length()-1){
                        cars += jsonArray.getJSONObject(i).getString("car_number")+"、";
                    }else{
                        cars += jsonArray.getJSONObject(i).getString("car_number");
                    }
                }

                tv_car_numbers.setText(cars);




//                for (int i = 0; i < jsonArray.length(); i++) {
//                    if(i<jsonArray.length()-1){
//                        cars += jsonArray.getJSONObject(i).getString("car_number")+"、";
//                    }else{
//                        cars += jsonArray.getJSONObject(i).getString("photo_url");
//                    }
//                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            btn_dispatch_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    JSONArray jsonArray = null;
                    String image_url = "";
                    try {
                        jsonArray = new JSONArray(bean.getPhotos());
                        image_url = jsonArray.getJSONObject(0).getString("photo_url");


                        Intent intent = new Intent(context, DispatchDetailActivity.class);
                        intent.putExtra("id", bean.getId());
                        intent.putExtra("sn", bean.getSn());
                        intent.putExtra("status", bean.getStatus());
                        intent.putExtra("start_time", bean.getStart_time());
                        intent.putExtra("end_time", bean.getEnd_time());
                        intent.putExtra("cars", bean.getCars());
                        intent.putExtra("start_longitude", bean.getStart_longitude());
                        intent.putExtra("start_latitude", bean.getStart_latitude());
                        intent.putExtra("end_longitude", bean.getEnd_longitude());
                        intent.putExtra("end_latitude", bean.getEnd_latitude());
                        intent.putExtra("image_url", image_url);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }




                }
            });

            btn_confirm_finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transport_finish(bean.getId());
                }
            });

            return convertView;
        }
    }

    public void transport_finish(int id){

        LogUtil.e("cda===transport_finish", id+"==="+ScanFragment.longitude+"==="+ScanFragment.latitude);

        RequestParams params = new RequestParams();
        params.put("longitude", ScanFragment.longitude);
        params.put("latitude", ScanFragment.latitude);

        HttpHelper.put(context, Urls.transport_finish+id+"/finish", params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());

//                isSubmit = true;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.e("cda===transport_finish1", "==="+responseString);

                            final ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            ToastUtil.showMessageApp(context, result.getMessage());

                            if(result.getStatus_code()==200){
                                initHttp();
                            }else{
//                                isSubmit = true;

                                if (loadingDialog != null && loadingDialog.isShowing()){
                                    loadingDialog.dismiss();
                                }
                            }
                        } catch (Exception e) {
//                            isSubmit = true;

                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                        }
                    }
                });
            }
        });

    }
}
