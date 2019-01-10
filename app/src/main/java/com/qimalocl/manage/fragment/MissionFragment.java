package com.qimalocl.manage.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.qimalocl.manage.activity.BikeLocationActivity;
import com.qimalocl.manage.activity.HistorysRecordActivity;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.activity.MerchantAddressMapActivity;
import com.qimalocl.manage.activity.MissionDetailActivity;
import com.qimalocl.manage.base.BaseFragment;
import com.qimalocl.manage.base.BaseViewAdapter;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.core.widget.CustomDialog;
import com.qimalocl.manage.core.widget.LoadingDialog;
import com.qimalocl.manage.core.widget.MyListView;
import com.qimalocl.manage.model.HistorysRecordBean;
import com.qimalocl.manage.model.ResultConsel;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class MissionFragment extends BaseFragment implements View.OnClickListener{

    Unbinder unbinder;

    private Context context;

    @BindView(R.id.listview)
    MyListView listview;
    @BindView(R.id.msgText)
    TextView msgText;


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
    private View v;

    private LoadingDialog loadingDialog;
    private Dialog dialog;
    private List<HistorysRecordBean> datas;
    private MyAdapter myAdapter;
    private int curPosition = 0;


    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mission, null);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        datas = new ArrayList<>();

        initView();

        initHttp("20000691");
    }


    private void initView(){
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        dialog = new Dialog(context, R.style.main_publishdialog_style);
        View tagView = LayoutInflater.from(context).inflate(R.layout.dialog_deduct_mark, null);
        dialog.setContentView(tagView);
        dialog.setCanceledOnTouchOutside(false);

        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(datas);
        listview.setAdapter(myAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                curPosition = position;
//                WindowManager.LayoutParams params1 = dialog.getWindow().getAttributes();
//                params1.width = LinearLayout.LayoutParams.MATCH_PARENT;
//                params1.height = LinearLayout.LayoutParams.MATCH_PARENT;
//                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                dialog.getWindow().setAttributes(params1);
//                dialog.show();

                Intent intent = new Intent(context, MissionDetailActivity.class);
                intent.putExtra("latitude", "0");
                intent.putExtra("longitude", "0");
//                intent.putExtra("latitude", jsonObject.getString("latitude"));
//                intent.putExtra("longitude", jsonObject.getString("longitude"));
                startActivity(intent);
            }
        });
    }

    @SuppressLint("NewApi")
    private class MyAdapter extends BaseViewAdapter<HistorysRecordBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_mission_record, null);
            }
            TextView num = BaseViewHolder.get(convertView,R.id.item_num);
            TextView status = BaseViewHolder.get(convertView,R.id.item_status);
            TextView time = BaseViewHolder.get(convertView,R.id.item_time);
            final HistorysRecordBean bean = getDatas().get(position);
            num.setText(bean.getUsername());
            status.setText(bean.getTelphone());
            time.setText(bean.getStart_end_date());

            return convertView;
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
                            for (int i = 0; i < array.length();i++){
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //pause
        }else{
            //resume
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }



    @Override
    public void onClick(View v) {
        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        switch (v.getId()){

            case R.id.historyLayout:
                UIHelper.goToAct(context, HistorysRecordActivity.class);
                break;

            case R.id.locationLayout:
                UIHelper.goToAct(context, BikeLocationActivity.class);
                break;

            default:
                break;
        }
    }

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

        Log.e("requestCode===", "==="+requestCode);

        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("QR_CODE");
                } else {
					Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }

                Log.e("requestCode===1", "==="+resultCode);
                break;

            default:
                break;

        }
    }


}
