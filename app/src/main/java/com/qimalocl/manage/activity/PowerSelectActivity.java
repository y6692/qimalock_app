package com.qimalocl.manage.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseViewAdapter;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.model.PowerListBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.SchoolListBean;
import com.qimalocl.manage.model.ScrappedBean;
import com.qimalocl.manage.model.UserBean;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.qimalocl.manage.utils.LogUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator1 on 2017/2/15.
 */
public class PowerSelectActivity extends SwipeBackActivity implements View.OnClickListener, PLA_AdapterView.OnItemClickListener{

    private Context context;
    private LoadingDialog loadingDialog;
    private ImageView backImg;
    private TextView title;
    private TextView rightBtn;

    private MultiColumnListView moneyListView;
    private RelativeLayout alipayTypeLayout,WeChatTypeLayout;
    private ImageView alipayTypeImage,WeChatTypeImage;
    private LinearLayout submitBtn;
    private TextView serviceProtocol;

    private List<PowerListBean> datas;
    private MyAdapter myAdapter;
    private int selectPosition = 0;

    private String rid = ""; //充值类型
    private String paytype = "1";//1支付宝2微信
    private String osn = "";
    private LinearLayout dealLayout;
    private String price = ""; //

    private boolean isRemain = false;

    private List<PowerListBean> powerList = new ArrayList<>();
    static ArrayList<String> item = new ArrayList<>();

    TextView titleText;

    String battery_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_select);
        context = this;
        datas = new ArrayList<>();
//        IntentFilter filter = new IntentFilter("data.broadcast.rechargeAction");
//        registerReceiver(broadcastReceiver, filter);

        isRemain = getIntent().getBooleanExtra("isRemain", false);

        initView();
    }

//    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            scrollToFinishActivity();
//        }
//    };

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
//        title = (TextView) findViewById(R.id.mainUI_title_titleText);
//        title.setText("充值");
//        rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
//        rightBtn.setText("充值记录");

        titleText = (TextView)findViewById(R.id.mainUI_title_titleText);
        titleText.setText("选择电池");

        moneyListView = (MultiColumnListView)findViewById(R.id.rechargeUI_moneyList);
        submitBtn = (LinearLayout)findViewById(R.id.rechargeUI_submitBtn);

        if (datas.isEmpty() || 0 == datas.size()){
            getPowerList();
        }
        myAdapter = new MyAdapter(context);
        moneyListView.setAdapter(myAdapter);

        moneyListView.setOnItemClickListener(this);

        backImg.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.rechargeUI_submitBtn:
                if (access_token == null || "".equals(access_token)){
                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("rid===", "==="+rid);

//                order();

                Intent intent = new Intent(context, LockStorageTBTDActivity.class);
//                intent.putExtra("order_type", 3);
                intent.putExtra("battery_name", battery_name);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                break;

        }
    }

    private void order() {
        Log.e("order===", "==="+price);

//        RequestParams params = new RequestParams();
//        params.put("order_type", 3);        //订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单
//        params.put("price", price);        //传价格数值 例如：20.00(order_type为3、4时必传)
//
//        HttpHelper.post(context, Urls.order, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                onStartCommon("正在加载");
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                onFailureCommon(throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
//
//                m_myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//
//                            Log.e("order===1", responseString + "===" + result.data);
//
//                            JSONObject jsonObject = new JSONObject(result.getData());
//
//                            int order_id = jsonObject.getInt("order_id");
//                            String order_amount = jsonObject.getString("order_amount");
//
//                            Log.e("order===1", order_id + "===" + order_amount );
//
//                            Intent intent = new Intent(context, SettlementPlatformActivity.class);
//                            intent.putExtra("order_type", 3);
//                            intent.putExtra("order_amount", order_amount);
//                            intent.putExtra("order_id", order_id);
//                            intent.putExtra("isRemain", isRemain);
//                            context.startActivity(intent);
//
//                        } catch (Exception e) {
////                            memberEvent(context.getClass().getName()+"_"+e.getStackTrace()[0].getLineNumber()+"_"+e.getMessage());
//                        }
//
//                        if (loadingDialog != null && loadingDialog.isShowing()) {
//                            loadingDialog.dismiss();
//                        }
//
//                    }
//                });
//
//
//            }
//        });
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private class MyAdapter extends BaseViewAdapter<PowerListBean> {

        private LayoutInflater inflater;


        public MyAdapter(Context context) {
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_power_list, null);
                holder = new ViewHolder();
                holder.layout = BaseViewHolder.get(convertView,R.id.item_recharge_layout);
                holder.moneyText = BaseViewHolder.get(convertView,R.id.item_recharge_money);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

//            final LinearLayout layout;
//            final TextView moneyText;


            final PowerListBean bean = getDatas().get(position);

            LogUtil.e("SchoolListAdapter===", battery_name + "===" + bean.getName());


//            if(battery_name.equals(bean.getName())){
////                layout.setSelected(bean.isSelected());
////                moneyText.setSelected(bean.isSelected());
//                holder.layout.setSelected(true);
//                holder.moneyText.setSelected(true);
//                holder.layout.setPressed(true);
//                holder.moneyText.setPressed(true);
//
////                convertView.setSelected(true);
////                convertView.setPressed(true);
//            }else{
//                holder.layout.setSelected(false);
//                holder.moneyText.setSelected(false);
//                holder.layout.setPressed(false);
//                holder.moneyText.setPressed(false);
//
////                convertView.setSelected(false);
////                convertView.setPressed(false);
//            }

            holder.moneyText.setText(bean.getName());

            if(position==0){
                layout2 = holder.layout;
                moneyText2 = holder.moneyText;

                layout2.setSelected(true);
                moneyText2.setSelected(true);

                battery_name = bean.getName();
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Log.e("layout===onClick", "==="+position+"==="+battery_name);


//                    order(card_code);

                    if (position != selectPosition){

                        layout2.setSelected(false);
                        moneyText2.setSelected(false);

                        holder.layout.setSelected(true);
                        holder.moneyText.setSelected(true);

                        battery_name = bean.getName();

                        selectPosition = position;

                        layout2 = holder.layout;
                        moneyText2 = holder.moneyText;
                    }


                }
            });

            return convertView;
        }

        class ViewHolder {
            LinearLayout layout;
            TextView moneyText;
        }
    }

    LinearLayout layout2;
    TextView moneyText2;

    @Override
    public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
//        PowerListBean bean = myAdapter.getDatas().get(position);
//        rid = bean.getId();
//        battery_name = bean.getName();
//
//        Log.e("psa===onItemClick", "==="+position+"==="+selectPosition);


//        if (position != selectPosition){
//            myAdapter.getDatas().get(position).setSelected(true);
//            myAdapter.getDatas().get(selectPosition).setSelected(false);
//
//
//
//            selectPosition = position;
//        }
//        myAdapter.notifyDataSetChanged();
    }



    private void getPowerList(){

        LogUtil.e("getPowerList===", "===");

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token != null && !"".equals(access_token)) {
            HttpHelper.get(context, Urls.battery, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
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
                                LogUtil.e("getPowerList===1", "==="+responseString);

                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                JSONArray powers = new JSONArray(result.getData());
//
//                                UserBean bean = JSON.parseObject(result.getData(), UserBean.class);
//
//                                String[] schools = bean.getSchools();
//
                                LogUtil.e("getPowerList===2", powers+"==="+powers.length());

                                if (powerList.size() != 0 || !powerList.isEmpty()){
                                    powerList.clear();
                                }

                                for (int i = 0; i < powers.length(); i++){

                                    LogUtil.e("getPowerList===3", datas.size()+"==="+powerList.size());

                                    PowerListBean bean2 = JSON.parseObject(powers.getJSONObject(i).toString(), PowerListBean.class);
                                    powerList.add(bean2);

                                    if ( 0 == i){
//                                        rid = bean.getId();
                                        battery_name = bean2.getName();
                                        bean2.setSelected(true);
                                    }else {
                                        bean2.setSelected(false);
                                    }
                                }

                                LogUtil.e("getPowerList===4", datas.size()+"==="+powerList.size());

                                myAdapter.setDatas(powerList);
                                myAdapter.notifyDataSetChanged();



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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
