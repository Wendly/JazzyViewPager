package com.jfeinstein.jazzyviewpager.animation;

public class CubeOutTransition extends CubeTransition {
	public CubeOutTransition() {
		super();
	}

	@Override
	protected float getMaxRot() {
		return -90f;
	}
}
