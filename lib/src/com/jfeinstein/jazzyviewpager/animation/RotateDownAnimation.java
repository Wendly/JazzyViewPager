package com.jfeinstein.jazzyviewpager.animation;

import android.support.v4.view.ViewPager;

public class RotateDownAnimation extends RotateAnimation {
	public RotateDownAnimation(ViewPager pager) {
		super(pager);
	}

	@Override
	boolean isRotateUp() {
		return false;
	}
}
