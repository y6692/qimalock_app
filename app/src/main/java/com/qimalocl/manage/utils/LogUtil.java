package com.qimalocl.manage.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.qimalocl.manage.base.BaseActivity;


public class LogUtil {

    public static void e(final String s1, final String s2) {

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {


                LogUtil.e(s1, s2);

                if(BaseActivity.testLog.length()>60000){
                    BaseActivity.testLog = "";
                }

//              MainFragment.tv_test0.setText(MainFragment.testLog);
                BaseActivity.testLog += (s1 + "：" + s2 + "\n");

                if(BaseActivity.tv_test != null){
//                  MainFragment.tv_test0.setText(s1 + "：");
//                  MainFragment.tv_test.setText(s2 + "\n");
                    BaseActivity.tv_test.setText(BaseActivity.testLog);

//                  scrollToBottom(MainFragment.sv_test, MainFragment.tv_test);

                }
            }
        });


//        MainFragment.sv_test.fullScroll(ScrollView.FOCUS_DOWN); //滚动到底部

    }

    public static void scrollToBottom(final View scroll, final View inner) {

        Handler mHandler = new Handler();

        mHandler.post(new Runnable() {
            public void run() {
                if (scroll == null || inner == null) {
                    return;
                }
                int offset = inner.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }

                scroll.scrollTo(0, offset);
            }
        });
    }


    private static Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {


                default:
                    break;
            }
            return false;
        }
    });
}
