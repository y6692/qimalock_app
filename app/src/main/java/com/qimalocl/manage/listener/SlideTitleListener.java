package com.qimalocl.manage.listener;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

public class SlideTitleListener implements OnClickListener {

	private int currentIndex;
	private ViewPager viewpager;

	public SlideTitleListener(int currentIndex, ViewPager viewpager) {
		this.currentIndex = currentIndex;
		this.viewpager = viewpager;
	}

	public void onClick(View v) {
		viewpager.setCurrentItem(currentIndex);
	}
}
