package com.jfeinstein.jazzyviewpager.animation;

public class ZoomOutAnimation extends ZoomAnimation {
	public ZoomOutAnimation() {
		super();
	}

	public ZoomOutAnimation(float zoomMax) {
		super(zoomMax);
	}

	public ZoomOutAnimation(float zoomMax, float pivotX, float pivotY) {
		super(zoomMax, pivotX, pivotY);
	}

	@Override
	protected float getScale(float positionOffset) {
		return 1 + mZoomMax - (mZoomMax * (1 - positionOffset));
	}
}
