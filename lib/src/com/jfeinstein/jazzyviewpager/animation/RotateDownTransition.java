package com.jfeinstein.jazzyviewpager.animation;

import android.support.v4.view.ViewPager;

public class RotateDownTransition extends RotateTransition {
	public RotateDownTransition(ViewPager pager) {
		super(pager);
	}

	@Override
	boolean isRotateUp() {
		return false;
	}
}
