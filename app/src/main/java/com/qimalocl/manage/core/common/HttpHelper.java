package com.qimalocl.manage.core.common;

import org.apache.http.impl.cookie.BasicClientCookie;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.qimalocl.manage.utils.LogUtil;
import com.qimalocl.manage.utils.SystemUtil;

/**
 * 网络请求帮助类
 * 
 * @author Bo.Zhang
 *
 */
public class HttpHelper {

	private static AsyncHttpClient client = new AsyncHttpClient();

	static {

		client.setTimeout(300000); // 设置链接超时，如果不设置，默认为10s
		client.setUserAgent("Mozilla/5.0 (Linux; U; Android " + android.os.Build.VERSION.RELEASE + "; zh-cn; "
				+ android.os.Build.MODEL + ") AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
	}

	/**
	 * 添加cookie
	 * 
	 * @param context
	 * @param hostUrl
	 */
	public static void addCookie(Context context, String hostUrl) {
		PersistentCookieStore cookieStore = new PersistentCookieStore(context);
		client.setCookieStore(cookieStore);
		BasicClientCookie newCookie = new BasicClientCookie("cookiesare", "awesome");
		newCookie.setVersion(1);
		newCookie.setDomain(hostUrl);
		newCookie.setPath("/");
		cookieStore.addCookie(newCookie);
	}

	/**
	 * get请求
	 *
	 * @param context
	 * @param url
	 * @param responseHandler
	 */
	public static void get(Context context, String url, AsyncHttpResponseHandler responseHandler) {


//		try {
//			url = url + "&act=1&platform=Android&version="
//					+ context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}


		addHeader(context);

		client.get(context, url, responseHandler);
	}

	public static void get2(Context context, String url, AsyncHttpResponseHandler responseHandler) {


//		try {
//			url = url + "&act=1&platform=Android&version="
//					+ context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}


		addHeader2(context);

		client.get(context, url, responseHandler);
	}

	/**
	 * get请求
	 *
	 * @param context
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//		try {
//			url = url + "&act=1&platform=Android&version="
//					+ context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}


		addHeader(context);

		LogUtil.e("params===get", url+"?"+params);

		client.get(context, url, params, responseHandler);
	}

	public static void get2(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//		try {
//			url = url + "&act=1&platform=Android&version="
//					+ context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}


		addHeader2(context);

		LogUtil.e("params===get", url+"?"+params);

		client.get(context, url, params, responseHandler);
	}

    public static void put(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        addHeader(context);

        client.put(context, url, params, responseHandler);
    }

	public static void post(Context context, String url, AsyncHttpResponseHandler responseHandler) {

		addHeader(context);

		client.post(context, url, responseHandler);
	}

	public static void post2(Context context, String url, AsyncHttpResponseHandler responseHandler) {

		addHeader2(context);

		client.post(context, url, responseHandler);
	}

	/**
	 * post请求
	 *
	 * @param context
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//		try {
//
//
//			url = url + "&act=1&platform=Android&version=" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}

//
		LogUtil.e("post===0", SharedPreferencesUrls.getInstance().getString("access_token","")+"===");


		addHeader(context);

		LogUtil.e("params===post", url+"?"+params);

		client.post(context, url, params, responseHandler);
	}

	public static void post2(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		LogUtil.e("post===0", SharedPreferencesUrls.getInstance().getString("access_token","")+"===");


		addHeader2(context);

		LogUtil.e("params===post", url+"?"+params);

		client.post(context, url, params, responseHandler);
	}

	/**
	 * post请求带head
	 *
	 * @param context
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void postWithHead(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//		try {
//			url = url + "&act=1&platform=Android&version="
//					+ context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}

//		String time = ""+Math.round(new Date().getTime()/1000);
//		String sign = SHA1.encode(time+"ga4H9dwf"+"StfmzsxJ6NBQGRFd2lI5gWhZnPVboLjU4eCcwauHYrqKOE0739AM18iDyTkXvp");

//		client.addHeader("A-APPKEY", "ga4H9dwf");
//		client.addHeader("A-TIMESTAMP", time);
//		client.addHeader("A-SIGN", sign);

		addHeader(context);

		client.post(context, url, params, responseHandler);
	}

	public static void delete(Context context, String url, AsyncHttpResponseHandler responseHandler) {

		addHeader(context);

		client.delete(context, url, responseHandler);
	}

	public static void addHeader(Context context) {
		try {

			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				return;
			}

			LogUtil.e("header===0", "===" + SharedPreferencesUrls.getInstance().getString("access_token",""));

			client.addHeader("Authorization", SharedPreferencesUrls.getInstance().getString("access_token",""));
			client.addHeader("Accept", "application/vnd.ws.manage+json");
//			client.addHeader("Phone-Brand", new Build().MANUFACTURER.toUpperCase());
//			client.addHeader("Phone-Model", new Build().MODEL);
//			client.addHeader("Phone-System", "Android");
//			client.addHeader("Phone-System-Version", SystemUtil.getSystemVersion());
//			client.addHeader("App-Version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
//			client.addHeader("Device_UUID", "" + tm.getDeviceId());
//			client.addHeader("Client", "Android_APP");

			LogUtil.e("post===", "===");


		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}

	public static void addHeader2(Context context) {
		try {

			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				return;
			}

			LogUtil.e("header===0", "===" + SharedPreferencesUrls.getInstance().getString("access_token",""));

			client.addHeader("Accept", "application/vnd.ws.manage+json");
//			client.addHeader("Phone-Brand", new Build().MANUFACTURER.toUpperCase());
//			client.addHeader("Phone-Model", new Build().MODEL);
//			client.addHeader("Phone-System", "Android");
//			client.addHeader("Phone-System-Version", SystemUtil.getSystemVersion());
//			client.addHeader("App-Version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
//			client.addHeader("Device_UUID", "" + tm.getDeviceId());
//			client.addHeader("Client", "Android_APP");

			LogUtil.e("post===", new Build().MANUFACTURER.toUpperCase()+"==="+new Build().MODEL+"==="+SystemUtil.getSystemVersion()+"==="+context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName+"==="+tm.getDeviceId());


		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}

	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	public int getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			int version = info.versionCode;

			LogUtil.e("getVersion===", "==="+version);

			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
