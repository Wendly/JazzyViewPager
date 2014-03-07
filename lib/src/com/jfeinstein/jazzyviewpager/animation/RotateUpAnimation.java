package com.jfeinstein.jazzyviewpager.animation;

import android.support.v4.view.ViewPager;

public class RotateUpAnimation extends RotateAnimation {
	public RotateUpAnimation(ViewPager pager) {
		super(pager);
	}

	@Override
	boolean isRotateUp() {
		return true;
	}
}
