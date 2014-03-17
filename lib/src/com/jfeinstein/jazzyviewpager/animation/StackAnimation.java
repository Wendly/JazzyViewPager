package com.jfeinstein.jazzyviewpager.animation;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.nineoldandroids.view.ViewHelper;

import android.support.v4.view.ViewPager;
import android.view.View;

public class StackAnimation implements DynamicAnimation {
	private static final float SCALE_MAX = 0.5f;

	private ViewPager mPager;

	public StackAnimation(ViewPager pager) {
		super();
		mPager = pager;
	}

	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state) {
		if (state != State.IDLE) {
			if (right != null) {
				float scale = (1 - SCALE_MAX) * positionOffset + SCALE_MAX;
				float trans = positionOffsetPixels - mPager.getWidth() - mPager.getPageMargin();
				ViewHelper.setScaleX(right, scale);
				ViewHelper.setScaleY(right, scale);
				ViewHelper.setTranslationX(right, trans);
			}
			if (left != null) {
				left.bringToFront();
			}
		}
	}
}
