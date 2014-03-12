package com.jfeinstein.jazzyviewpager.animation;

public class ZoomInAnimation extends ZoomAnimation {
	public ZoomInAnimation() {
        super();
    }

	public ZoomInAnimation(float zoomMax) {
		super(zoomMax);
	}

	public ZoomInAnimation(float zoomMax, float pivotX, float pivotY) {
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
