package com.qimalocl.manage.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.qimalocl.manage.R;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;
import com.qimalocl.manage.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

//财务明细
@SuppressLint("NewApi")
public class FinanceDetailActivity extends SwipeBackActivity implements View.OnClickListener{

    Unbinder unbinder;

    private Context context;

    private ImageView backImg;
    private TextView title;

    @BindView(R.id.rl_cyclingIncomeLayout)
    RelativeLayout rl_cyclingIncomeLayout;
    @BindView(R.id.rl_cardIncomeLayout)
    RelativeLayout rl_cardIncomeLayout;
    @BindView(R.id.rl_rechargeIncomeLayout)
    RelativeLayout rl_rechargeIncomeLayout;
    @BindView(R.id.rl_otherIncomeLayout)
    RelativeLayout rl_otherIncomeLayout;
    @BindView(R.id.rl_cyclingRefundLayout)
    RelativeLayout rl_cyclingRefundLayout;


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

    private int carmodel_id;
    private String codenum;

//    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        v = inflater.inflate(R.layout.fragment_query, null);
//        unbinder = ButterKnife.bind(this, v);
//        return v;
//    }
//
//
//    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        context = getActivity();
//
//        initView();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_detail);
        ButterKnife.bind(this);
        context = this;
//        latitude = Double.parseDouble(getIntent().getExtras().getString("latitude"));
//        longitude = Double.parseDouble(getIntent().getExtras().getString("longitude"));

//        carmodel_id = getIntent().getIntExtra("carmodel_id", 0);
//        codenum = getIntent().getStringExtra("codenum");

        initView();
    }


    private void initView(){
        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("财务明细");

        backImg.setOnClickListener(this);
        rl_cyclingIncomeLayout.setOnClickListener(this);
        rl_cardIncomeLayout.setOnClickListener(this);
        rl_rechargeIncomeLayout.setOnClickListener(this);
        rl_otherIncomeLayout.setOnClickListener(this);
        rl_cyclingRefundLayout.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        super.onResume();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.rl_cyclingIncomeLayout:
//                UIHelper.goToAct(context, HistorysRecordActivity.class);

                Intent intent = new Intent(context, CyclingIncomeActivity.class);
//                intent.putExtra("carmodel_id", carmodel_id);
//                intent.putExtra("codenum", codenum);
                startActivity(intent);
                break;

            case R.id.rl_cardIncomeLayout:
                UIHelper.goToAct(context, CardIncomeActivity.class);
                break;

            case R.id.rl_otherIncomeLayout:
                UIHelper.goToAct(context, OtherIncomeActivity.class);
                break;

            case R.id.rl_cyclingRefundLayout:
                UIHelper.goToAct(context, CyclingRefundActivity.class);
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
