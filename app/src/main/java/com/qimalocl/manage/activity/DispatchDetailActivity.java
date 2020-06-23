package com.qimalocl.manage.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseViewHolder;
import com.qimalocl.manage.fragment.DispatchCarDetailFragment;
import com.qimalocl.manage.fragment.DispatchPhotoFragment;
import com.qimalocl.manage.fragment.DispatchStartEndFragment;
import com.qimalocl.manage.model.CarDispatchBean;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * 调运详情
 * Created by Wikison on 2017/9/16.
 */
public class DispatchDetailActivity extends SwipeBackActivity implements View.OnClickListener{
  public static final String INTENT_INDEX = "INTENT_INDEX";
//  @BindView(R.id.ll_back) LinearLayout llBack;
//  @BindView(R.id.lh_tv_title) TextView lhTvTitle;
//  @BindView(R.id.tab) TabLayout tab;
//  @BindView(R.id.vp) ViewPager vp;

    TabLayout tab;
    ViewPager vp;

//  private PrivateLockFragment privateLockFragment;
//  private RentLockFragment rentLockFragment;
  private MyPagerAdapter myPagerAdapter;

  private LinearLayout ll_back;
  private TextView title;
  private TextView rightBtn;

  private String sn;
  private String status;
  private String start_time;
  private String end_time;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dispatch_detail);
    context = this;

    sn = getIntent().getStringExtra("sn");
    status = getIntent().getStringExtra("status");
    start_time = getIntent().getStringExtra("start_time");
    end_time = getIntent().getStringExtra("end_time");

    init();
  }

  private void init(){

    ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
    title = (TextView) findViewById(R.id.mainUI_title_titleText);
    title.setText("调运详情");
//    rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
//    rightBtn.setText("我的套餐卡");

    tab = (TabLayout) findViewById(R.id.tab);
    vp = (ViewPager)findViewById(R.id.vp);

    myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
    vp.setAdapter(myPagerAdapter);
    tab.setupWithViewPager(vp);

    vp.setCurrentItem(0);

    vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override public void onPageSelected(int position) {
            vp.setCurrentItem(position);
        }

        @Override public void onPageScrollStateChanged(int state) {
        }
    });

    ll_back.setOnClickListener(this);
//    rightBtn.setOnClickListener(this);

    TextView tv_sn = (TextView)findViewById(R.id.dispatch_drtail_sn);
    TextView tv_status = (TextView)findViewById(R.id.dispatch_drtail_status);
    TextView tv_created_at = (TextView)findViewById(R.id.dispatch_drtail_created_at);
    TextView tv_end_at = (TextView)findViewById(R.id.dispatch_drtail_end_at);
//    TextView tv_car_numbers = (TextView)findViewById(R.id.item_car_numbers);
//    Button btn_dispatch_detail = BaseViewHolder.get(convertView,R.id.btn_dispatch_detail);
//    Button btn_confirm_finish = BaseViewHolder.get(convertView,R.id.btn_confirm_finish);

    tv_sn.setText("调运单号："+sn);
    if("0".equals(status)){
      tv_status.setText("调运中");
    }else{
      tv_status.setText("已完成");
    }

    tv_created_at.setText("开始时间："+start_time);
    if(end_time==null || "".equals(end_time)){
      tv_end_at.setVisibility(View.GONE);
    }else{
      tv_end_at.setVisibility(View.VISIBLE);
      tv_end_at.setText("结束时间："+end_time);
    }

  }

  @Override
  public void onClick(View v) {

    switch (v.getId()) {
      case R.id.ll_backBtn:
        scrollToFinishActivity();
        break;
      case R.id.mainUI_title_rightBtn:
//        UIHelper.goToAct(context, HistoryDetailActivity.class);
        break;
    }
  }

  class MyPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"车辆信息", "调运起始点", "照片"};
    private List<Fragment> fragmentList;

    public MyPagerAdapter(FragmentManager fm) {
      super(fm);

      DispatchCarDetailFragment dispatchCarDetailFragment = new DispatchCarDetailFragment();
      DispatchStartEndFragment dispatchStartEndFragment = new DispatchStartEndFragment();
      DispatchPhotoFragment dispatchPhotoFragment = new DispatchPhotoFragment();

      fragmentList = new ArrayList<>();
      fragmentList.add(dispatchCarDetailFragment);
      fragmentList.add(dispatchStartEndFragment);
      fragmentList.add(dispatchPhotoFragment);
    }

    @Override
    public Fragment getItem(int position) {
      return fragmentList.get(position);
    }

    @Override
    public int getCount() {
      return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

      return titles[position];
    }
  }

//  public void btn(View view) {
//    int viewId = view.getId();
//    if (viewId == R.id.ll_bike) {
//      UIHelper.goToAct(this, MainFragment.class);
//      scrollToFinishActivity();
//    } else if (viewId == R.id.ll_purse) {
//      UIHelper.goToAct(this, MyPurseActivity.class);
//      scrollToFinishActivity();
//    } else if (viewId == R.id.ll_mine) {
//      UIHelper.goToAct(this, PersonAlterActivity.class);
//      scrollToFinishActivity();
//    }
//  }

}
