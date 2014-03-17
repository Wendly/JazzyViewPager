package com.jfeinstein.jazzyviewpager.animation;

import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.view.ViewHelper;

abstract public class ZoomAnimation implements Animation {
	protected float mZoomMax;
	protected float mPivotX;
	protected float mPivotY;

	public ZoomAnimation() {
		this(0.5f);
	}

	public ZoomAnimation(float zoomMax) {
		this(zoomMax, -1, -1);
	}

	public ZoomAnimation(float zoomMax, float pivotX, float pivotY) {
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

	public void animate(View view, float offset) {
		if (view != null) {
			View child = ((ViewGroup) view).getChildAt(0);
			float scale = getScale(offset);
			ViewHelper.setPivotX(child, child.getMeasuredWidth() * mPivotX);
			ViewHelper.setPivotY(child, child.getMeasuredHeight() * mPivotY);
			ViewHelper.setScaleX(child, scale);
			ViewHelper.setScaleY(child, scale);
		}
	}

	abstract protected float getScale(float positionOffset);
}
