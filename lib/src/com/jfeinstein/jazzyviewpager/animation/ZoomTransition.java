package com.jfeinstein.jazzyviewpager.animation;

import android.view.View;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.nineoldandroids.view.ViewHelper;

abstract public class ZoomTransition implements DynamicTransition {
	protected float mZoomMax;
	protected float mPivotX;
	protected float mPivotY;

	public ZoomTransition() {
		this(0.5f);
	}

	public ZoomTransition(float zoomMax) {
		this(zoomMax, -1, -1);
	}

	public ZoomTransition(float zoomMax, float pivotX, float pivotY) {
		super();
		mZoomMax = zoomMax;

		mPivotX = 0.5f;
		if ((pivotX >= 0.0f) && (pivotX <= 1.0f)) {
			mPivotX = pivotX;
		}

		mPivotY = 0.5f;
		if ((pivotY >= 0.0f) && (pivotY <= 1.0f)) {
			mPivotY = pivotY;
		}
	}

	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state) {
		if (state != State.IDLE) {
			if (left != null) {
				float scale = getLeftScale(positionOffset);
				ViewHelper.setPivotX(left, left.getMeasuredWidth() * mPivotX);
				ViewHelper.setPivotY(left, left.getMeasuredHeight() * mPivotY);
				ViewHelper.setScaleX(left, scale);
				ViewHelper.setScaleY(left, scale);
			}
			if (right != null) {
				float scale = getRightScale(positionOffset);
				ViewHelper.setPivotX(right, right.getMeasuredWidth() * mPivotX);
				ViewHelper.setPivotY(right, right.getMeasuredHeight() * mPivotY);
				ViewHelper.setScaleX(right, scale);
				ViewHelper.setScaleY(right, scale);
			}
		}
	}

	abstract protected float getLeftScale(float positionOffset);
	abstract protected float getRightScale(float positionOffset);
}
