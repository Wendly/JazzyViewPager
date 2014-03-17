package com.jfeinstein.jazzyviewpager.animation;

public class ZoomOutTransition extends ZoomTransition {
	public ZoomOutTransition() {
		super();
	}

	public ZoomOutTransition(float zoomMax) {
		super(zoomMax);
	}

	public ZoomOutTransition(float zoomMax, float pivotX, float pivotY) {
		super(zoomMax, pivotX, pivotY);
	}

	@Override
	protected float getLeftScale(float positionOffset) {
		return 1 + mZoomMax - (mZoomMax * (1 - positionOffset));
	}

	@Override
	protected float getRightScale(float positionOffset) {
		return 1 + mZoomMax - (mZoomMax * positionOffset);
	}
}
