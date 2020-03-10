package com.qimalocl.manage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.qimalocl.manage.model.HistorysRecordBean;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.model.SchoolListBean;
import com.qimalocl.manage.model.UserBean;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/2/18 0018.
 */

public class SchoolSelectActivity extends SwipeBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private Context context;

    private LinearLayout ll_back;
    private TextView title;

    private EditText et_school;
    private Button nextBtn;

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
    private List<SchoolListBean> datas;
    private boolean isRefresh = true;// 是否刷新中
    private boolean isLast = false;
    private int showPage = 1;

    private List<SchoolListBean> schoolList;
    static ArrayList<String> item = new ArrayList<>();
    static ArrayList<String[]> item1 = new ArrayList<>();

    private int school_id;
    private String school_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_select);
        context = this;

        datas = new ArrayList<>();
        schoolList = new ArrayList<>();

        initView();
    }

    private void initView(){

        ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("选择学校");

//        btnQuery = (Button)findViewById(R.id.btn_click_one);
        et_school = (EditText)findViewById(R.id.et_school);
        nextBtn = (Button)findViewById(R.id.schoolSelectUI_btn);

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

//        if(datas.isEmpty()){
//            initHttp();
//        }

        if (schoolList.isEmpty() || item1.isEmpty()){
            getSchoolList();
        }

        myAdapter = new MyAdapter(context);
        myAdapter.setDatas(datas);
        myList.setAdapter(myAdapter);

        ll_back.setOnClickListener(this);
        footerLayout.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        et_school.addTextChangedListener(tw);
    }

    TextWatcher tw = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}//文本改变之前执行

        @Override
        //文本改变的时候执行
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //如果长度为0
            querySchoolList();
        }

        public void afterTextChanged(Editable s) { }//文本改变之后执行
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SchoolListBean bean = myAdapter.getDatas().get(position);

        school_id = bean.getId();
        school_name = bean.getName();


        Log.e("ssa===onItemClick", bean.getId()+"==="+bean.getName());

        et_school.removeTextChangedListener(tw);
        et_school.setText(bean.getName());
        et_school.addTextChangedListener(tw);

//        Intent rIntent = new Intent();
//        rIntent.putExtra("school_id", bean.getId());
//        rIntent.putExtra("school_name", bean.getName());
//        setResult(RESULT_OK, rIntent);
//        scrollToFinishActivity();
//
//        Log.e("ssa===onItemClick2", bean.getId()+"==="+bean.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        isRefresh = true;
        if(datas.size()!=0){
            myAdapter.notifyDataSetChanged();
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
            getSchoolList();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.schoolSelectUI_btn:
//                UIHelper.goToAct(context, ActivityScanerCode.class);

                Log.e("ssa===onClick", school_id+"===");

                Intent intent = new Intent();
                intent.setClass(context, ActivityScanerCode.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("isBindSchool",true);
                intent.putExtra("school_id", school_id);
                intent.putExtra("school_name", school_name);
                intent.putExtra("isChangeKey",false);
                intent.putExtra("isAdd",true);
                startActivityForResult(intent, 1);
                scrollToFinishActivity();

//        Intent rIntent = new Intent();
//        rIntent.putExtra("school_id", bean.getId());
//        rIntent.putExtra("school_name", bean.getName());
//        setResult(RESULT_OK, rIntent);
//        scrollToFinishActivity();
//
//        Log.e("ssa===onItemClick2", bean.getId()+"==="+bean.getName());

                break;

            case R.id.footer_Layout:
                if (!isLast) {
                    showPage += 1;
                    getSchoolList();
                    myAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    private void querySchoolList(){
        Log.e("onClick===et", "==="+et_school.getText());

        myAdapter.getDatas().clear();

        for (int i = 0; i < schoolList.size(); i++){
//                    SchoolListBean bean = JSON.parseObject(JSONArray.getJSONObject(i).toString(),SchoolListBean.class);
//                    schoolList.add(bean);
            if(schoolList.get(i).getName().contains(et_school.getText().toString())){
                datas.add(schoolList.get(i));
            }


            myAdapter.notifyDataSetChanged();

//                    item.add(bean.getSchool());
//                    item1.add(new String[]{bean.getSchool(), bean.getCert_method()});

        }
    }

    private void getSchoolList(){

        Log.e("getSchoolList===", "===");

        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token != null && !"".equals(access_token)) {
            HttpHelper.get(context, Urls.user, new TextHttpResponseHandler() {
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
                        Log.e("getSchoolList===1", "==="+responseString);

                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        UserBean bean = JSON.parseObject(result.getData(), UserBean.class);

                        String[] schools = bean.getSchools();

                        Log.e("getSchoolList===2", schools+"===");

                        if (schoolList.size() != 0 || !schoolList.isEmpty()){
                            schoolList.clear();
                        }
                        if (item.size() != 0 || !item.isEmpty()){
                            item.clear();
                        }

                        for (int i = 0; i < schools.length;i++){
                            SchoolListBean bean2 = JSON.parseObject(schools[i], SchoolListBean.class);
                            schoolList.add(bean2);
                            datas.add(bean2);
                            item.add(bean.getName());
                        }

                        setFooterType(2);

                        Log.e("getSchoolList===3", datas.size()+"==="+schoolList.size());

                        myAdapter.notifyDataSetChanged();

//                        if(schools!=null && schools.length>0){
//
//                            Log.e("getSchoolList===3", schools[0]+"===");
//
//                            SchoolListBean bean2 = JSON.parseObject(schools[0], SchoolListBean.class);
//
//                            schoolName.setText(bean2.getName());
//                        }
//
//                        String[] roles = bean.getRoles();
//                        if(roles!=null && roles.length>0){
//
//                            Log.e("getSchoolList===4", roles[0]+"==="+roles[1]);
//
//                            roleName.setText(roles[0]);
//                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }

//        RequestParams params = new RequestParams();
//        params.put("name","");
//
//        HttpHelper.get(context, Urls.schools, params, new TextHttpResponseHandler() {
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
//                m_myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//
//                            Log.e("getSchoolList===", "==="+responseString);
//
//                            JSONArray JSONArray = new JSONArray(result.getData());
//                            if (schoolList.size() != 0 || !schoolList.isEmpty()){
//                                schoolList.clear();
//                            }
//                            if (item.size() != 0 || !item.isEmpty()){
//                                item.clear();
//                            }
//                            if (item1.size() != 0 || !item1.isEmpty()){
//                                item1.clear();
//                            }
//                            for (int i = 0; i < JSONArray.length();i++){
//                                SchoolListBean bean = JSON.parseObject(JSONArray.getJSONObject(i).toString(),SchoolListBean.class);
//                                schoolList.add(bean);
//                                datas.add(bean);
////                                    item.add(bean.getSchool()+"_"+bean.getCert_method());
//                                item.add(bean.getName());
////                                    item1.add(new String[]{bean.getName(), bean.getCert_method()});
//
//                            }
//
//                            setFooterType(2);
//
//                            Log.e("getSchoolList===2", datas.size()+"==="+schoolList.size());
//
//                            myAdapter.notifyDataSetChanged();
//
//                        }catch (Exception e) {
//                            e.printStackTrace();
//                        }finally {
//                            isRefresh = false;
//                            swipeRefreshLayout.setRefreshing(false);
//                        }
//
//                        if (loadingDialog != null && loadingDialog.isShowing()){
//                            loadingDialog.dismiss();
//                        }
//                    }
//                });
//
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

    private class MyAdapter extends BaseViewAdapter<SchoolListBean> {

        private LayoutInflater inflater;

        public MyAdapter(Context context){
            super(context);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_school_list, null);
            }
            TextView name = BaseViewHolder.get(convertView, R.id.item_school_name);
            SchoolListBean bean = getDatas().get(position);

            Log.e("SchoolListAdapter===", "==="+bean.getName());

            name.setText(bean.getName());
            return convertView;

        }
    }
}
