package com.jfeinstein.jazzyviewpager.animation;

import android.view.View;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;

public class StandardAnimation extends BaseDynamicAnimation {
	public StandardAnimation() {
		super();
	}

	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state) {
		View front = null;

		if (state == State.GOING_RIGHT) {
			front = right;
		} else if (state == State.GOING_LEFT) {
			front = left;
		}

		if (front != null) {
			front.bringToFront();
		}
	}
}
