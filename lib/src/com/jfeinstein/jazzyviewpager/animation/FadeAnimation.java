package com.jfeinstein.jazzyviewpager.animation;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.nineoldandroids.view.ViewHelper;

import android.view.View;

public class FadeAnimation implements StaticAnimation {
	public FadeAnimation() {
		super();
	}

	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state) {
		if (left != null) {
			ViewHelper.setAlpha(left, 1 - positionOffset);
		}
		if (right != null) {
			ViewHelper.setAlpha(right, positionOffset);
		}
	}
}
