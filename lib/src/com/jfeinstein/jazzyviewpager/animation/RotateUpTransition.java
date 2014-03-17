package com.jfeinstein.jazzyviewpager.animation;

import android.support.v4.view.ViewPager;

public class RotateUpTransition extends RotateTransition {
	public RotateUpTransition(ViewPager pager) {
		super(pager);
	}

	@Override
	boolean isRotateUp() {
		return true;
	}
}
