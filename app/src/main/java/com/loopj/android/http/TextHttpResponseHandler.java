/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.android.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qimalocl.manage.R;
import com.qimalocl.manage.activity.LoginActivity;
import com.qimalocl.manage.base.BaseApplication;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.model.ResultConsel;
import com.qimalocl.manage.utils.LogUtil;
import com.qimalocl.manage.utils.ToastUtil;
import com.zxing.lib.scaner.activity.ActivityScanerCode;

import static com.qimalocl.manage.base.BaseApplication.school_id;

/**
 * Used to intercept and handle the responses from requests made using {@link AsyncHttpClient}. The
 * {@link #onSuccess(int, Header[], String)} method is designed to be anonymously
 * overridden with your own response handling code. <p>&nbsp;</p> Additionally, you can override the
 * {@link #onFailure(int, Header[], String, Throwable)}, {@link #onStart()}, and
 * {@link #onFinish()} methods as required. <p>&nbsp;</p> For example: <p>&nbsp;</p>
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * client.get("http://www.google.com", new TextHttpResponseHandler() {
 *     &#064;Override
 *     public void onStart() {
 *         // Initiated the request
 *     }
 *
 *     &#064;Override
 *     public void onSuccess(String responseBody) {
 *         // Successfully got a response
 *     }
 *
 *     &#064;Override
 *     public void onFailure(String responseBody, Throwable e) {
 *         // Response failed :(
 *     }
 *
 *     &#064;Override
 *     public void onFinish() {
 *         // Completed the request (either success or failure)
 *     }
 * });
 * </pre>
 */
public abstract class TextHttpResponseHandler extends AsyncHttpResponseHandler {
    private static final String LOG_TAG = "TextHttpResponseHandler";

    private ConnectLinstener mListener;

//    public abstract void setOnConnectLinstener(ConnectLinstener linstener) {
//        this.mListener = linstener;
//    }

    // 数据接收回调接口
    public interface ConnectLinstener {
        void onReceiveData();
    }

    /**
     * Creates new instance with default UTF-8 encoding
     */
    public TextHttpResponseHandler() {
        this(DEFAULT_CHARSET);
    }

    public TextHttpResponseHandler(ConnectLinstener linstener) {

        this(DEFAULT_CHARSET);
        this.mListener = linstener;
    }

//    public TextHttpResponseHandler() {
//        super();
//    }

    /**
     * Creates new instance with given string encoding
     *
     * @param encoding String encoding, see {@link #setCharset(String)}
     */
    public TextHttpResponseHandler(String encoding) {
        super();
        setCharset(encoding);

    }

    /**
     * Called when request fails
     *
     * @param statusCode     http response status line
     * @param headers        response headers if any
     * @param responseString string response of given charset
     * @param throwable      throwable returned when processing request
     */
    public abstract void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable);

    /**
     * Called when request succeeds
     *
     * @param statusCode     http response status line
     * @param headers        response headers if any
     * @param responseString string response of given charset
     */
    public abstract void onSuccess(int statusCode, Header[] headers, String responseString);

//    public abstract void showMessage();

//    @Override
//    public void showMessage2(){
//
//    };

    private Handler m_myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {

                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBytes) {

        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("onSuccess===00", responseBytes+"===");

                String responseString = getResponseString(responseBytes, getCharset());

                LogUtil.e("onSuccess===0", responseString+"===");

                if(responseString!=null && !"".equals(responseString)){
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                    LogUtil.e("onSuccess===1", responseString+"==="+result.getStatus_code());

                    if(result.getStatus_code()==401 || result.getStatus_code()==402){
                        LogUtil.e("onSuccess===2", responseString+"==="+result.getStatus_code());

                        SharedPreferencesUrls.getInstance().putString("access_token", "");


                        if(result.getStatus_code()==402){
                            school_id = 0;
                            SharedPreferencesUrls.getInstance().putInt("school_id", 0);
                        }

//                        SharedPreferencesUrls.getInstance().putString("iscert", "");

                        ToastUtil.showMessageApp(BaseApplication.context, result.getMessage());

                        Intent intent = new Intent(BaseApplication.context, LoginActivity.class);
                        BaseApplication.context.startActivity(intent);

                    }else if(result.getStatus_code()==406){
                        LogUtil.e("onSuccess===3", BaseApplication.context+"==="+responseString+"==="+result.getStatus_code());

//                        ToastUtil.showMessageApp(BaseApplication.context, result.getMessage());

//                        if (loadingDialog != null && loadingDialog.isShowing()) {
//                            loadingDialog.dismiss();
//                        }

                        onSuccess(statusCode, headers, responseString);

                    }else if(result.getStatus_code()==407){
                        LogUtil.e("onSuccess===407", BaseApplication.context+"==="+responseString+"==="+result.getStatus_code());

//                        showMessage();

                        if(mListener!=null){
                            mListener.onReceiveData();
                        }


                        onSuccess(statusCode, headers, responseString);



                    }else{

                        LogUtil.e("onSuccess===4", BaseApplication.context+"==="+responseString+"==="+result.getStatus_code());

//                        if(result.getStatus_code()!=200){
//                            ToastUtil.showMessageApp(BaseApplication.context, result.getMessage());
//                        }

                        onSuccess(statusCode, headers, responseString);
                    }
                }

            }
        });



//        try {
//        	String returnStr = new String(responseBytes, "utf-8");
//        	LogUtil.e("MyTest", "onSuccess:" + returnStr);
//			onSuccess(statusCode, headers, getResponseString(RSAUtils.decryptByPrivateKey(Base64Utils.decode(returnStr), MyKey.cliPrivateKey), "utf-8"));
//		} catch (Exception e) {
//			LogUtil.e("MyTest", "successError:" + e.toString());
//			e.printStackTrace();
//		}
    }



    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
        onFailure(statusCode, headers, getResponseString(responseBytes, getCharset()), throwable);
    }

    /**
     * Attempts to encode response bytes as string of set encoding
     *
     * @param charset     charset to create string with
     * @param stringBytes response bytes
     * @return String of set encoding or null
     */
    public static String getResponseString(byte[] stringBytes, String charset) {
        try {
            return stringBytes == null ? null : new String(stringBytes, charset);
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Encoding response into string failed", e);
            return null;
        }
    }

}
