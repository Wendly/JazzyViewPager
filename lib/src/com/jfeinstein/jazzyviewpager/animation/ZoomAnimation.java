package com.jfeinstein.jazzyviewpager.animation;

import android.view.View;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.nineoldandroids.view.ViewHelper;

abstract public class ZoomAnimation extends BaseDynamicAnimation {
	protected static final float ZOOM_MAX = 0.5f;

	public ZoomAnimation() {
		super();
	}

	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state) {
		if (state != State.IDLE) {
			if (left != null) {
				manageLayer(left, true);
				float scale = getLeftScale(positionOffset);
				ViewHelper.setPivotX(left, left.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(left, left.getMeasuredHeight() * 0.5f);
				ViewHelper.setScaleX(left, scale);
				ViewHelper.setScaleY(left, scale);
			}
			if (right != null) {
				manageLayer(right, true);
				float scale = getRightScale(positionOffset);
				ViewHelper.setPivotX(right, right.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(right, right.getMeasuredHeight() * 0.5f);
				ViewHelper.setScaleX(right, scale);
				ViewHelper.setScaleY(right, scale);
			}
		}
	}

	abstract protected float getLeftScale(float positionOffset);
	abstract protected float getRightScale(float positionOffset);
}
