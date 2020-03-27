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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.qimalocl.manage.core.widget.ClearEditText;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.MyListView;
import com.qimalocl.manage.model.GlobalConfig;
import com.qimalocl.manage.model.HistorysRecordBean;
import com.qimalocl.manage.model.PowerExchangeBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator1 on 2017/2/13.
 */

public class HistorysRecordActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {

    @BindView(R.id.ui_historysRecord_backBtn)
    ImageView backBtn;
    @BindView(R.id.ui_historysRecord_codeNumEdit)
    ClearEditText codeNumEdit;
    @BindView(R.id.ui_historysRecord_searchBtn)
    TextView searchBtn;
//    @BindView(R.id.ui_historysRecord_listview)
//    MyListView listview;
//    @BindView(R.id.ui_historysRecord_msgText)
//    TextView msgText;

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
    private List<HistorysRecordBean> data = new ArrayList<>();
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;
    private String starttime = "";
    private String endtime = "";

    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_historys_record);
        ButterKnife.bind(this);

        context = this;

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        data = new ArrayList<>();
        initView();
    }

    private void initView(){

//        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
//        title = (TextView) findViewById(R.id.mainUI_title_titleText);
//        title.setText("换电记录");
//        rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
//        rightBtn.setText("查询");

        // list投资列表
        footerView = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
        footerViewType01 = footerView.findViewById(R.id.footer_Layout_type01);// 点击加载更多
        footerViewType02 = footerView.findViewById(R.id.footer_Layout_type02);// 正在加载，请您稍等
        footerViewType03 = footerView.findViewById(R.id.footer_Layout_type03);// 已无更多
        footerViewType04 = footerView.findViewById(R.id.footer_Layout_type04);// 刷新失败，请重试
        footerViewType05 = footerView.findViewById(R.id.footer_Layout_type05);// 暂无数据


        footerViewType01.setVisibility(View.GONE);
        footerViewType02.setVisibility(View.GONE);
        footerViewType03.setVisibility(View.GONE);
        footerViewType04.setVisibility(View.GONE);
        footerViewType05.setVisibility(View.GONE);

        footerLayout = footerView.findViewById(R.id.footer_Layout);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.Layout_swipeParentLayout);
        myList = (ListView)findViewById(R.id.Layout_swipeListView);
        myList.addFooterView(footerView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light), getResources().getColor(android.R.color.holo_red_light));

        myList.setOnItemClickListener(this);
//        if(data.isEmpty()){
//            initHttp();
//        }

        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(data);
        myList.setAdapter(myAdapter);

        backBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
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

            String codenum = codeNumEdit.getText().toString().trim();
            if (codenum == null || "".equals(codenum)){
                Toast.makeText(context,"请输入车辆编号",Toast.LENGTH_SHORT).show();
                return;
            }
            initHttp(codenum);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onClick(View v) {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()) {
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
                Intent intent = new Intent(context,HistoryRoadFiltateActivity.class);
                startActivityForResult(intent,0);
                break;
            case R.id.footer_Layout:
                String codenum = codeNumEdit.getText().toString().trim();
                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
                    UIHelper.goToAct(context,LoginActivity.class);
                    return;
                }
                if (codenum == null || "".equals(codenum)){
                    Toast.makeText(context,"请输入车辆编号",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isLast) {
                    showPage += 1;
                    initHttp(codenum);
                    myAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.ui_historysRecord_backBtn:
                scrollToFinishActivity();
                break;
            case R.id.ui_historysRecord_searchBtn:
                codenum = codeNumEdit.getText().toString().trim();
                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录您的账号",Toast.LENGTH_SHORT).show();
                    UIHelper.goToAct(context,LoginActivity.class);
                    return;
                }
                if (codenum == null || "".equals(codenum)){
                    Toast.makeText(context,"请输入车辆编号",Toast.LENGTH_SHORT).show();
                    return;
                }
                initHttp(codenum);

                inputMethodManager.hideSoftInputFromWindow(codeNumEdit.getWindowToken(), 0);

                break;

            default:
                break;
        }
    }
    private void initHttp(String codenum){

        Log.e("hra===initHttp", codenum+"==="+showPage);

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("car_number", codenum);
        params.put("page", showPage);
        params.put("per_page", GlobalConfig.PAGE_SIZE);
        HttpHelper.get(context, Urls.cyclingorder, params, new TextHttpResponseHandler() {

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

                    Log.e("hra===initHttp1", "==="+responseString);

                    if(data.size()>0){
                        data.clear();
                        myAdapter.notifyDataSetChanged();
                    }

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



                    for (int i = 0; i < array.length(); i++) {
                        HistorysRecordBean bean = JSON.parseObject(array.getJSONObject(i).toString(),HistorysRecordBean.class);
                        data.add(bean);
                    }

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

    String date = "";
    private class MyAdapter extends BaseViewAdapter<HistorysRecordBean> {


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

            final TextView tv_date = BaseViewHolder.get(convertView,R.id.item_date);
            ImageView iv_order_state = BaseViewHolder.get(convertView,R.id.item_iv_order_state);
            TextView tv_order_state = BaseViewHolder.get(convertView,R.id.item_tv_order_state);
            TextView user_name = BaseViewHolder.get(convertView,R.id.item_user_name);
            TextView user_phone = BaseViewHolder.get(convertView,R.id.item_user_phone);
            TextView created_at = BaseViewHolder.get(convertView,R.id.item_created_at);
            TextView car_end_time = BaseViewHolder.get(convertView,R.id.item_car_end_time);
            TextView order_amount = BaseViewHolder.get(convertView,R.id.item_order_amount);

            final HistorysRecordBean bean = getDatas().get(position);

            int order_state = bean.getOrder_state();
            iv_order_state.setImageResource(order_state==0?R.drawable.order_0:order_state==10?R.drawable.order_10:order_state==20?R.drawable.order_20:order_state==30?R.drawable.order_30:R.drawable.order_40);
            tv_order_state.setText(order_state==0?"已取消":order_state==10?"已下单":order_state==20?"进行中":order_state==30?"待支付":"已完成");

            user_name.setText(bean.getUser_name());
            user_phone.setText("联系电话:"+ bean.getUser_phone());


            if(bean.getCreated_at()==null){
                created_at.setText("");
            }else{
                created_at.setText("借车:"+ bean.getCreated_at());

                Log.e("getView===", date+"==="+bean.getCreated_at().split(" ")[0]+"==="+bean.getCreated_at());

                if(bean.isLoad()){
                    if(bean.isShowDate()){
                        tv_date.setVisibility(View.VISIBLE);
                        tv_date.setText(bean.getCreated_at().split(" ")[0]);
                    }else{
                        tv_date.setVisibility(View.GONE);
                        tv_date.setText("");
                    }

                }else{
                    if(!date.equals(bean.getCreated_at().split(" ")[0])){
                        date = bean.getCreated_at().split(" ")[0];
                        bean.setShowDate(true);

                        tv_date.setVisibility(View.VISIBLE);
                        tv_date.setText(bean.getCreated_at().split(" ")[0]);
                    }else{
                        bean.setShowDate(false);
                        tv_date.setVisibility(View.GONE);
                        tv_date.setText("");
                    }

                    bean.setLoad(true);
                }



            }

            if(bean.getCar_end_time()==null){
                car_end_time.setText("");
            }else{
                car_end_time.setText("还车:"+ bean.getCar_end_time());
            }

            order_amount.setText("¥"+ bean.getOrder_amount());


            user_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Test","111111111");
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
                    customBuilder.setTitle("温馨提示").setMessage("确认拨打" + bean.getUser_phone() + "吗?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + bean.getUser_phone()));
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
