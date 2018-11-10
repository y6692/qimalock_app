package com.qimalocl.manage.core.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class DialogBuuton extends Button {

	public DialogBuuton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchTouchEvent(final MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(event);
	}
}
