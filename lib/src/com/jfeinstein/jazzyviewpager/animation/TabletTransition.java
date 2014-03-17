package com.jfeinstein.jazzyviewpager.animation;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.nineoldandroids.view.ViewHelper;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;

public class TabletTransition implements DynamicTransition {
	public static final String TAG = "TabletTransition";

	private Matrix mMatrix = new Matrix();
	private Camera mCamera = new Camera();

	public TabletTransition() {
		super();
	}

	private void logState(View v, String title) {
		Log.v(TAG, title + ": ROT (" + ViewHelper.getRotation(v) + ", " +
				ViewHelper.getRotationX(v) + ", " +
				ViewHelper.getRotationY(v) + "), TRANS (" +
				ViewHelper.getTranslationX(v) + ", " +
				ViewHelper.getTranslationY(v) + "), SCALE (" +
				ViewHelper.getScaleX(v) + ", " +
				ViewHelper.getScaleY(v) + "), ALPHA " +
				ViewHelper.getAlpha(v));
	}

	protected float getOffsetXForRotation(float degrees, int width, int height) {
		mMatrix.reset();
		mCamera.save();
		mCamera.rotateY(Math.abs(degrees));
		mCamera.getMatrix(mMatrix);
		mCamera.restore();

		mMatrix.preTranslate(-width * 0.5f, -height * 0.5f);
		mMatrix.postTranslate(width * 0.5f, height * 0.5f);

		float[] tempFloats = new float[2];
		tempFloats[0] = width;
		tempFloats[1] = height;
		mMatrix.mapPoints(tempFloats);

		return (width - tempFloats[0]) * (degrees > 0.0f ? 1.0f : -1.0f);
	}

	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state) {
		if (state != State.IDLE) {
			if (left != null) {
				float rot = 30.0f * positionOffset;
				float trans = getOffsetXForRotation(rot, left.getMeasuredWidth(),
						left.getMeasuredHeight());
				ViewHelper.setPivotX(left, left.getMeasuredWidth()/2);
				ViewHelper.setPivotY(left, left.getMeasuredHeight()/2);
				ViewHelper.setTranslationX(left, trans);
				ViewHelper.setRotationY(left, rot);
				logState(left, "Left");
			}
			if (right != null) {
				float rot = -30.0f * (1 - positionOffset);
				float trans = getOffsetXForRotation(rot, right.getMeasuredWidth(),
						right.getMeasuredHeight());
				ViewHelper.setPivotX(right, right.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(right, right.getMeasuredHeight() * 0.5f);
				ViewHelper.setTranslationX(right, trans);
				ViewHelper.setRotationY(right, rot);
				logState(right, "Right");
			}
		}
	}
}
