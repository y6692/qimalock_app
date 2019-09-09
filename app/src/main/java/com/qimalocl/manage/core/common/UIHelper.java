package com.qimalocl.manage.core.common;

import android.content.Context;
import android.content.Intent;

import com.fitsleep.sunshinelibrary.utils.ToastUtils;
import com.qimalocl.manage.R;
import com.qimalocl.manage.base.BaseActivity;
import com.qimalocl.manage.core.widget.ConfirmDialog;
import com.qimalocl.manage.swipebacklayout.app.SwipeBackActivity;

public class UIHelper {

	private static ConfirmDialog confirmDialog;

	public static void ToastMessageClose(SwipeBackActivity baseActivity, Context context, String msg, int ResID) {

		openDialogToastMsg(baseActivity, context, ResID, msg);
	}

	public static void ToastMessageClose(BaseActivity baseActivity, Context context, String msg, int ResID) {

		openDialogToastMsg(baseActivity, context, ResID, msg);
	}

	// 提醒
	public static void showToastMsg(Context context, String msg, int drawID) {
		openDialogOneMsg(context, drawID, msg);
	}

	/**
	 * 关闭当前页,跳转页面
	 */
	public static void ToastGoActClose(SwipeBackActivity baseActivity, Context context, Class<?> clz, String msgStr,
			int drawID) {

		openDialogGoActColse(baseActivity, context, clz, drawID, msgStr);
	};

	/**
	 * 关闭所有前页,跳转登录页面
	 */
	public static void ToastGoLoginClose(SwipeBackActivity baseActivity, Context context, String msgStr, int drawID) {

		openDialogGoLoginColse(baseActivity, context, drawID, msgStr);
	};

	/**
	 * 关闭所有前页,跳转登录页面
	 */
	public static void ToastGoLoginClose(BaseActivity baseActivity, Context context, String msgStr, int drawID) {

		openDialogGoLoginColse(baseActivity, context, drawID, msgStr);
	};

	/**
	 * 关闭当前页,跳转页面
	 */
	public static void ToastGoActClose(BaseActivity baseActivity, Context context, Class<?> clz, String msgStr,
			int drawID) {

		openDialogGoActColse(baseActivity, context, clz, drawID, msgStr);
	};

	/**
	 * 不关闭当前页,跳转页面
	 */
	public static void ToastGoAc(Context context, Class<?> clz, String msgStr, int drawID) {

		openDialogGoAct(context, clz, drawID, msgStr);
	};

	/**
	 * 弹出Toast错误消息
	 * 
	 * @param msg
	 */
	public static void ToastError(Context context, String msg) {
		if (context == null || msg == null || "".equals(msg))
			return;
		if (!NetworkUtils.isNetWorkAvalible(context)) {
//			openDialogOneMsg(context, R.drawable.ic_error, "无网络连接，请先打开网络连接");
			ToastUtils.showMessage("无网络连接，请先打开网络连接");
		} else if (msg.contains("SocketTimeoutException") || msg.contains("ConnectTimeoutException")) {
//			openDialogOneMsg(context, R.drawable.ic_error, "网速不给力哦！");
//			openDialogOneMsg(context, R.drawable.ic_error, "请求超时，请稍后再试");
			ToastUtils.showMessage("请求超时，请稍后再试");
		} else {
//			openDialogOneMsg(context, R.drawable.ic_error, "请求失败");
			ToastUtils.showMessage("请求失败");
		}
	}

	/**
	 * 不关闭当前页
	 */
	public static void openDialogOneMsg(Context context, int drawID, String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭当前页
	 */
	public static void openDialogToastMsg(final SwipeBackActivity baseActivity, final Context context, int drawID,
			String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
				baseActivity.scrollToFinishActivity();
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭当前页
	 */
	public static void openDialogToastMsg(final BaseActivity baseActivity, final Context context, int drawID,
			String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
				baseActivity.finishMine();
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭当前页,跳转页面
	 */

	public static void openDialogGoActColse(final SwipeBackActivity baseActivity, final Context context,
			final Class<?> clz, int drawID, String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
				goToAct(context, clz);
				baseActivity.scrollToFinishActivity();
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭所有页面,跳转登录页面
	 */

	public static void openDialogGoLoginColse(final SwipeBackActivity baseActivity, final Context context, int drawID,
			String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
//				confirmDialog.dismiss();
//				Intent intent = new Intent(context, LoginActivity.class);
//				intent.putExtra("Tag", 1);
//				context.startActivity(intent);
//				((Activity) context).overridePendingTransition(R.anim.push_rigth_in, R.anim.push_left_out);
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭所有页面,跳转登录页面
	 */

	public static void openDialogGoLoginColse(final BaseActivity baseActivity, final Context context, int drawID,
			String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
//				Intent intent = new Intent(context, LoginActivity.class);
//				intent.putExtra("Tag", 1);
//				context.startActivity(intent);
//				((Activity) context).overridePendingTransition(R.anim.push_rigth_in, R.anim.push_left_out);
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 关闭当前页,跳转页面
	 */

	public static void openDialogGoActColse(final BaseActivity baseActivity, final Context context, final Class<?> clz,
			int drawID, String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
				goToAct(context, clz);
				baseActivity.finishMine();
				;
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	/**
	 * 不关闭关闭当前页,跳转页面
	 */

	public static void openDialogGoAct(final Context context, final Class<?> clz, int drawID, String msgStr) {
		if (confirmDialog != null) {
			if (confirmDialog.isShowing()) {
				confirmDialog.dismiss();
			}
			confirmDialog = null;
		}
		confirmDialog = new ConfirmDialog(context, drawID, msgStr);
		confirmDialog.show();
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.setCancelable(false);
		confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
			@Override
			public void doConfirm() {
				confirmDialog.dismiss();
				goToAct(context, clz);
			}

			@Override
			public void doCancel() {
				confirmDialog.dismiss();
			}
		});
	}

	// 页面跳转
	public static void goToAct(Context context, Class<?> clz) {

		Intent intent = new Intent(context, clz);
		context.startActivity(intent);
	}
	
}
