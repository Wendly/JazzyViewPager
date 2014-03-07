package com.jfeinstein.jazzyviewpager.animation;

public class CubeOutAnimation extends CubeAnimation {
	public CubeOutAnimation() {
		super();
	}

	@Override
	protected float getMaxRot() {
		return -90f;
	}
}
