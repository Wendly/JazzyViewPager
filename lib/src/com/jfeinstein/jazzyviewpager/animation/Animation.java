package com.jfeinstein.jazzyviewpager.animation;

import android.view.View;

public interface Animation {
	public static final float OFFSET_BEGIN = 0.0f;
	public static final float OFFSET_END = 1.0f;

	public void animate(View view, float offset);

	public static final Animation NULL = new Animation() {
		public void animate(View view, float offset) {
			return;
		}
	};
}

