package com.jfeinstein.jazzyviewpager.animation;

public class CubeInTransition extends CubeTransition {
	public CubeInTransition() {
		super();
	}

    @Override
	protected float getMaxRot() {
        return 90.0f;
    }
}
