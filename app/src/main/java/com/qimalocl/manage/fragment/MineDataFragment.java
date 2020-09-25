package com.qimalocl.manage.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.ExchangePowerRecordActivity;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.activity.LongSetgoodUnusedDetailActivity;
import com.qimalocl.manage.activity.LowPowerDetailActivity;
import com.qimalocl.manage.activity.MaintenanceRecordActivity;
import com.qimalocl.manage.activity.ScrappedDetailActivity;
import com.qimalocl.manage.activity.SettingActivity;
import com.qimalocl.manage.activity.SuperzoneDetailActivity;
import com.qimalocl.manage.activity.UnGoodUsedDetailActivity;
import com.qimalocl.manage.base.BaseFragment;
import com.qimalocl.manage.base.BaseViewAdapter;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.core.widget.MarqueTextView;
import com.qimalocl.manage.model.AddDispatchBean;
import com.qimalocl.manage.model.DatasBean;
import com.qimalocl.manage.model.GlobalConfig;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.utils.LogUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.qimalocl.manage.base.BaseApplication.school_id;

@SuppressLint("NewApi")
public class MineDataFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener{

    Unbinder unbinder;
    private View v;

    private Context context;
    private Activity activity;

//    @BindView(R.id.Layout_swipeListView)
    ListView listview;
//    @BindView(R.id.Layout_swipeParentLayout)
    SwipeRefreshLayout swipeRefreshLayout;
//    @BindView(R.id.msgText)
//    TextView msgText;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = true;
    private LatLng myLocation = null;
    private Circle mCircle;
    private BitmapDescriptor successDescripter;
    private BitmapDescriptor bikeDescripter;
    private Handler handler = new Handler();
    private Marker centerMarker;
    private boolean isMovingMarker = false;

    private List<Marker> bikeMarkerList;
    private boolean isUp = false;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private int isLock = 0;


    private LoadingDialog loadingDialog;
    private Dialog dialog;
    private List<AddDispatchBean> datas;
    private MyAdapter myAdapter;
    private int curPosition = 0;
    private int showPage = 1;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;

    private View footerView;
    private View footerViewType01;
    private View footerViewType02;
    private View footerViewType03;
    private View footerViewType04;
    private View footerViewType05;

    private ImageView iv_type05;
    private TextView tv_type05;

    private View footerLayout;

    private String badtime="2115-02-08 20:20";
    private String codenum="";
    private String totalnum="";

//    private String card_code;
    private String cars;

    private TextView tv_delivered_cars, tv_is_using_cars, tv_longtime_not_used_cars, tv_not_recycled_cars, tv_not_fixed_cars, tv_fixed_not_used_cars;

    private LinearLayout ll_1, ll_2, ll_3, ll_4, ll_5, ll_6;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mine_data, null);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        activity = getActivity();

        datas = new ArrayList<>();

        cars = activity.getIntent().getStringExtra("cars");

        initView();

//        initHttp();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            while (true){
//
//                try {
//                    Thread.sleep(30*1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                m_myHandler.sendEmptyMessage(1);
//            }
//
//            }
//        }).start();

    }

    protected Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 0:
                    break;
                case 1:
                    resetList();

                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public void resetList(){
        showPage = 1;
        badtime="2115-02-08 20:20";
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //pause
        }else{
            //resume
//            resetList();
        }
    }


    private void initView(){
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

//        dialog = new Dialog(context, R.style.main_publishdialog_style);
//        View tagView = LayoutInflater.from(context).inflate(R.layout.dialog_deduct_mark, null);
//        dialog.setContentView(tagView);
//        dialog.setCanceledOnTouchOutside(false);

        ll_1 = getActivity().findViewById(R.id.ll_1);
        ll_2 = getActivity().findViewById(R.id.ll_2);
        ll_3 = getActivity().findViewById(R.id.ll_3);
        ll_4 = getActivity().findViewById(R.id.ll_4);
        ll_5 = getActivity().findViewById(R.id.ll_5);
        ll_6 = getActivity().findViewById(R.id.ll_6);
        tv_delivered_cars = getActivity().findViewById(R.id.tv_delivered_cars);
        tv_is_using_cars = getActivity().findViewById(R.id.tv_is_using_cars);
        tv_longtime_not_used_cars = getActivity().findViewById(R.id.tv_longtime_not_used_cars);
        tv_not_recycled_cars = getActivity().findViewById(R.id.tv_not_recycled_cars);
        tv_not_fixed_cars = getActivity().findViewById(R.id.tv_not_fixed_cars);
        tv_fixed_not_used_cars = getActivity().findViewById(R.id.tv_fixed_not_used_cars);

//        ll_1.setOnClickListener(this);
//        ll_2.setOnClickListener(this);
        ll_3.setOnClickListener(this);
        ll_4.setOnClickListener(this);
        ll_5.setOnClickListener(this);
        ll_6.setOnClickListener(this);


        datas();
    }

    public void datas() {
        LogUtil.e("mdf===datas", school_id+"==="+isHidden());

//        if(isHidden()) return;

        RequestParams params = new RequestParams();
        params.put("school_id", school_id);

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token != null && !"".equals(access_token)) {
            HttpHelper.get(context, Urls.datas, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在加载");
//                        loadingDialog.show();
//                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    onFailureCommon("minef===datas", throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LogUtil.e("minef===datas1", "==="+responseString);

                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                DatasBean bean = JSON.parseObject(result.getData(), DatasBean.class);

//                              tv_delivered_cars, tv_is_using_cars, tv_longtime_not_used_cars, tv_not_recycled_cars, tv_not_fixed_cars, tv_fixed_not_used_cars;

                                tv_delivered_cars.setText(bean.getDelivered_cars());
                                tv_is_using_cars.setText(bean.getIs_using_cars());
                                tv_longtime_not_used_cars.setText(bean.getLongtime_not_used_cars());
                                tv_not_recycled_cars.setText(bean.getNot_recycled_cars());
                                tv_not_fixed_cars.setText(bean.getNot_fixed_cars());
                                tv_fixed_not_used_cars.setText(bean.getFixed_not_used_cars());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        } else {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }
    }

    @Override
    public void onClick(View v) {

        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token == null || "".equals(access_token)) {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
//            case R.id.personUI_backImage:
//                scrollToFinishActivity();
//                break;

            case R.id.ll_3:
//                UIHelper.goToAct(context, UnGoodUsedDetailActivity.class);

                Intent intent = new Intent();
                intent.setClass(context, LongSetgoodUnusedDetailActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);

                break;

            case R.id.ll_4:
//                UIHelper.goToAct(context, UnGoodUsedDetailActivity.class);

                intent = new Intent();
                intent.setClass(context, UnGoodUsedDetailActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);

                break;

            case R.id.ll_5:
//                UIHelper.goToAct(context, UnGoodUsedDetailActivity.class);

                intent = new Intent();
                intent.setClass(context, UnGoodUsedDetailActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);

                break;



            case R.id.ll_6:
//                UIHelper.goToAct(context, UnGoodUsedDetailActivity.class);

                intent = new Intent();
                intent.setClass(context, LongSetgoodUnusedDetailActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);

                break;



            default:
                break;
        }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @SuppressLint("NewApi")
    private class MyAdapter extends BaseViewAdapter<AddDispatchBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_dispatch_car_detail, null);
            }

            TextView car_number = BaseViewHolder.get(convertView,R.id.item_car_number);
            TextView car_type = BaseViewHolder.get(convertView,R.id.item_car_type);
            TextView car_status = BaseViewHolder.get(convertView,R.id.item_car_status);

            AddDispatchBean bean = getDatas().get(position);

            car_number.setText(bean.getCar_number());
            car_type.setText(bean.getCar_type());

            int status = bean.getCar_status();
            car_status.setText(status==0?"待投放":status==1?"正常":status==2?"锁定":status==3?"确认为坏车":status==4?"坏车已回收":status==5?"调运中":"报废");

//            name.setText(bean.getName());
//            final String card_code = bean.getCode();
//            String price = bean.getPrice();
//            tv_price.setText(price);
//            tv_original_price.setText("¥"+bean.getOriginal_price());
//            tv_desc.setText(bean.getDesc());
//
//            RoundImageView iv_img = BaseViewHolder.get(convertView, R.id.item_iv_img);
//            ImageLoader.getInstance().displayImage(bean.getImage(), iv_img);
//
//
//            tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//
//            ll_payBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
////                    PayCartBean bean = myAdapter.getDatas().get(position);
//
//                    LogUtil.e("bcf===onClick", "==="+position+"==="+card_code);
//
////                    int order_id = bean.getOrder_id();
//
////                    order(bean.getCode());
//
//                    order(card_code);
//
//                }
//            });
//
//            rl_desc.setOnClickListener(new View.OnClickListener() {
//                boolean flag = false;
//                @Override
//                public void onClick(View view) {
//
//                    LogUtil.e("mcf===onClick", "===");
//
//                    if(flag){
//                        flag = false;
//                        iv_down.setImageResource(R.drawable.down_icon2);
//                        tv_desc.setMaxLines(1);
//                    }else{
//                        flag = true;
//                        iv_down.setImageResource(R.drawable.up_icon2);
//                        tv_desc.setMaxLines(20);
//                    }
//
//
//                }
//            });


            return convertView;
        }
    }



    private void initHttp(){
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("tab", 1);

        HttpHelper.get(context, Urls.recycletask, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
                    loadingDialog.show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    LogUtil.e("cycling_cards===1","==="+responseString);

                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    JSONArray array = new JSONArray(result.getData());
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

                    if (array.length() == 0) {
                        footerViewType05.setVisibility(View.VISIBLE);
                    }else{
                        footerViewType05.setVisibility(View.GONE);
                    }

//                    for (int i = 0; i < array.length();i++){
//                        CarBean bean = JSON.parseObject(array.getJSONObject(i).toString(), CarBean.class);
//
////                        if(i==0 && bean.getBadtime().compareTo(badtime)<0){
////                            badtime = bean.getBadtime();
////                            codenum = bean.getCodenum();
////                            totalnum = bean.getTotalnum();
////                        }
//
//                        datas.add(bean);
//                    }
//
//                    myAdapter.notifyDataSetChanged();

//                    Intent intent = new Intent("data.broadcast.action");
//                    intent.putExtra("codenum", codenum);
//                    intent.putExtra("count", Integer.parseInt(totalnum));
//                    context.sendBroadcast(intent);

//                    if (result.getFlag().equals("Success")) {
//
//                    } else {
//                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    swipeRefreshLayout.setRefreshing(false);
//                    isRefresh = false;
//                    setFooterVisibility();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void initHttp2(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("page", showPage);
        params.put("pagesize", GlobalConfig.PAGE_SIZE);

//        HttpHelper.get(context, Urls.badcarList, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                setFooterType(1);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                UIHelper.ToastError(context, throwable.toString());
//                swipeRefreshLayout.setRefreshing(false);
//                isRefresh = false;
//                setFooterType(3);
//                setFooterVisibility();
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().equals("Success")) {
//                        JSONArray array = new JSONArray(result.getData());
//                        if (array.length() == 0 && showPage == 1) {
//                            footerLayout.setVisibility(View.VISIBLE);
//                            setFooterType(4);
//                            return;
//                        } else if (array.length() < GlobalConfig.PAGE_SIZE && showPage == 1) {
//                            footerLayout.setVisibility(View.GONE);
//                            setFooterType(5);
//                        } else if (array.length() < GlobalConfig.PAGE_SIZE) {
//                            footerLayout.setVisibility(View.VISIBLE);
//                            setFooterType(2);
//                        } else if (array.length() >= 10) {
//                            footerLayout.setVisibility(View.VISIBLE);
//                            setFooterType(0);
//                        }
//
//                        for (int i = 0; i < array.length();i++){
//                            BadCarBean bean = JSON.parseObject(array.getJSONObject(i).toString(), BadCarBean.class);
//
//                            if(i==0 && bean.getBadtime().compareTo(badtime)<0){
//                                badtime = bean.getBadtime();
//                                codenum = bean.getCodenum();
//                                totalnum = bean.getTotalnum();
//                            }
//
//                            datas.add(bean);
//                        }
//
//                        Intent intent = new Intent("data.broadcast.action");
//                        intent.putExtra("codenum", codenum);
//                        intent.putExtra("count", Integer.parseInt(totalnum));
//                        context.sendBroadcast(intent);
//
//                    } else {
//                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    swipeRefreshLayout.setRefreshing(false);
//                    isRefresh = false;
//                    setFooterVisibility();
//                }
////                if (loadingDialog != null && loadingDialog.isShowing()){
////                    loadingDialog.dismiss();
////                }
//            }
//        });
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


    @Override
    public void onResume() {
        super.onResume();
        isRefresh = true;
    }



//    @Override
//    public void onClick(View v) {
//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        switch (v.getId()){
//
//            case R.id.footer_Layout:
//                if (!isLast) {
//                    showPage += 1;
//                    initHttp();
//                    myAdapter.notifyDataSetChanged();
//                }
//                break;
//
//            default:
//                break;
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
    }
    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        LogUtil.e("requestCode===", "==="+requestCode);

        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("QR_CODE");
                } else {
					Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }

                LogUtil.e("requestCode===1", "==="+resultCode);
                break;

            default:
                break;

        }
    }


}
