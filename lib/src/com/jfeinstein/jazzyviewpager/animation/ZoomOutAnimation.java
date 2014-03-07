package com.jfeinstein.jazzyviewpager.animation;

public class ZoomOutAnimation extends ZoomAnimation {
	public ZoomOutAnimation() {
		super();
	}

	@Override
	protected float getLeftScale(float positionOffset) {
		return 1 + ZOOM_MAX - (ZOOM_MAX * (1 - positionOffset));
	}

	@Override
	protected float getRightScale(float positionOffset) {
		return 1 + ZOOM_MAX - (ZOOM_MAX * positionOffset);
	}
}
