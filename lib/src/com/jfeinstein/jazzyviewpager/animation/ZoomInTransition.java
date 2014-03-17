package com.jfeinstein.jazzyviewpager.animation;

public class ZoomInTransition extends ZoomTransition {
	public ZoomInTransition() {
        super();
    }

	public ZoomInTransition(float zoomMax) {
		super(zoomMax);
	}

	public ZoomInTransition(float zoomMax, float pivotX, float pivotY) {
		super(zoomMax, pivotX, pivotY);
	}

	@Override
	protected float getLeftScale(float positionOffset) {
		return mZoomMax + (1 - mZoomMax) * (1 - positionOffset);
	}

	@Override
	protected float getRightScale(float positionOffset) {
		return mZoomMax + (1 - mZoomMax) * positionOffset;
	}
}
