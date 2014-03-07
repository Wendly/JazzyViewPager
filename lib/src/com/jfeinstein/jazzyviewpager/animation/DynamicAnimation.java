package com.jfeinstein.jazzyviewpager.animation;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;

import android.view.View;

public interface DynamicAnimation extends PagerAnimation {
	public static final DynamicAnimation NULL = new DynamicAnimation() {
		public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
				State state) {
			return;
		}
	};
}

