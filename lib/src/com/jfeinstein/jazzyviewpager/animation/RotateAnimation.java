package com.jfeinstein.jazzyviewpager.animation;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.nineoldandroids.view.ViewHelper;

abstract public class RotateAnimation implements DynamicAnimation {
	private static final float ROT_MAX = 15.0f;

	private ViewPager mPager;

	public RotateAnimation(ViewPager pager) {
		super();
		mPager = pager;
	}

	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state) {
		if (state != State.IDLE) {
			if (left != null) {
				float rot = (isRotateUp() ? 1 : -1) * (ROT_MAX * positionOffset);
				float trans = (isRotateUp() ? -1 : 1) * (float) (mPager.getMeasuredHeight() -
						mPager.getMeasuredHeight() * Math.cos(rot * Math.PI / 180.0f));
				ViewHelper.setPivotX(left, left.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(left, isRotateUp() ? 0 : left.getMeasuredHeight());
				ViewHelper.setTranslationY(left, trans);
				ViewHelper.setRotation(left, rot);
			}
			if (right != null) {
				float rot = (isRotateUp() ? 1 : -1) * (-ROT_MAX + ROT_MAX * positionOffset);
				float trans = (isRotateUp() ? -1 : 1) * (float) (mPager.getMeasuredHeight() -
						mPager.getMeasuredHeight() * Math.cos(rot * Math.PI / 180.0f));
				ViewHelper.setPivotX(right, right.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(right, isRotateUp() ? 0 : right.getMeasuredHeight());
				ViewHelper.setTranslationY(right, trans);
				ViewHelper.setRotation(right, rot);
			}
		}
	}

	abstract boolean isRotateUp();
}
