package com.qimalocl.manage.core.common;

import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**
 * 网络请求帮助类
 * 
 * @author Bo.Zhang
 *
 */
public class HttpHelper {

	private static AsyncHttpClient client = new AsyncHttpClient();

	static {

		client.setTimeout(15000); // 设置链接超时，如果不设置，默认为10s
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
	 * @param params
	 * @param responseHandler
	 */
	public static void get(Context context, String url, AsyncHttpResponseHandler responseHandler) {
		try {
			url = url + "&act=1&platform=Android&version="
					+ context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
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
	public static void get(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		try {
			url = url + "&act=1&platform=Android&version="
					+ context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		client.get(context, url, params, responseHandler);
	}

	/**
	 * post请求
	 * 
	 * @param context
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void post(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		try {
			url = url + "&act=1&platform=Android&version="
					+ context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		client.post(context, url, params, responseHandler);
	}
}
