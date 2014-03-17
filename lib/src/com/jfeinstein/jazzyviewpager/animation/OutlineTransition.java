package com.jfeinstein.jazzyviewpager.animation;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.jfeinstein.jazzyviewpager.OutlineContainer;

import android.view.View;

public class OutlineTransition implements DynamicTransition {
	public OutlineTransition() {
		super();
	}

	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state) {
		if (!(left instanceof OutlineContainer))
			return;

		if (state != State.IDLE) {
			if (left != null) {
				((OutlineContainer) left).setOutlineAlpha(1.0f);
			}
			if (right != null) {
				((OutlineContainer) right).setOutlineAlpha(1.0f);
			}
		} else {
			if (left != null) {
				((OutlineContainer) left).start();
			}
			if (right != null) {
				((OutlineContainer) right).start();
			}
		}
	}
}
