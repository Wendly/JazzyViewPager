package com.jfeinstein.jazzyviewpager.animation;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

abstract public class BaseDynamicAnimation implements DynamicAnimation {

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void manageLayer(View v, boolean enableHardware) {
		if (Build.VERSION.SDK_INT < 11) return;

		int layerType = enableHardware ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE;
		if (layerType != v.getLayerType()) {
			v.setLayerType(layerType, null);
		}
	}
}
