package com.jfeinstein.jazzyviewpager.animation;

public class ZoomInAnimation extends ZoomAnimation {
	public ZoomInAnimation() {
		super();
	}

	@Override
	protected float getLeftScale(float positionOffset) {
		return ZOOM_MAX + (1 - ZOOM_MAX) * (1 - positionOffset);
	}

	@Override
	protected float getRightScale(float positionOffset) {
		return ZOOM_MAX + (1 - ZOOM_MAX) * positionOffset;
	}
}
