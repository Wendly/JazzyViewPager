package com.jfeinstein.jazzyviewpager.animation;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;

import android.view.View;

public interface PagerAnimation {
	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state);
}

