package com.jfeinstein.jazzyviewpager.animation;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.nineoldandroids.view.ViewHelper;

import android.view.View;

public class AccordionAnimation extends BaseDynamicAnimation {
	public AccordionAnimation() {
		super();
	}

	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state) {
		if (state != State.IDLE) {
			if (left != null) {
				manageLayer(left, true);
				ViewHelper.setPivotX(left, left.getMeasuredWidth());
				ViewHelper.setPivotY(left, 0);
				ViewHelper.setScaleX(left, 1 - positionOffset);
			}
			if (right != null) {
				manageLayer(right, true);
				ViewHelper.setPivotX(right, 0);
				ViewHelper.setPivotY(right, 0);
				ViewHelper.setScaleX(right, positionOffset);
			}
		}
	}
}
