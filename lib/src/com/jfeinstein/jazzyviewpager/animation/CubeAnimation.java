package com.jfeinstein.jazzyviewpager.animation;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.nineoldandroids.view.ViewHelper;

import android.view.View;

abstract public class CubeAnimation extends BaseDynamicAnimation {
	public CubeAnimation() {
		super();
	}

	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state) {
		if (state != State.IDLE) {
			if (left != null) {
				manageLayer(left, true);
				float rot = getMaxRot() * positionOffset;
				ViewHelper.setPivotX(left, left.getMeasuredWidth());
				ViewHelper.setPivotY(left, left.getMeasuredHeight() * 0.5f);
				ViewHelper.setRotationY(left, rot);
			}
			if (right != null) {
				manageLayer(right, true);
				float rot = getMaxRot() * (positionOffset - 1);
				ViewHelper.setPivotX(right, 0);
				ViewHelper.setPivotY(right, right.getMeasuredHeight() * 0.5f);
				ViewHelper.setRotationY(right, rot);
			}
		}
	}

	abstract protected float getMaxRot();
}
