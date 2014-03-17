package com.jfeinstein.jazzyviewpager;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class ContainerPagerAdapter extends PagerAdapterWrapper {
	private Context mContext;

	public ContainerPagerAdapter(PagerAdapter adapter, Context context) {
		super(adapter);
		mContext = context;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void manageLayer(View v, boolean enableHardware) {
		if (Build.VERSION.SDK_INT < 11) return;

		int layerType = enableHardware ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE;
		if (layerType != v.getLayerType()) {
			v.setLayerType(layerType, null);
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Object object = super.instantiateItem(container, position);

		View view = findViewFromObject(container, object);
		if (view == null) {
			return object;
		}

		int index = container.indexOfChild(view);
		container.removeView(view);

		ViewGroup dynamicLayer = getContainer(mContext);
		dynamicLayer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		dynamicLayer.addView(view);
		container.addView(dynamicLayer, index);

		manageLayer(dynamicLayer, true);

		return object;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		return instantiateItem((ViewGroup) container, position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = findViewFromObject(container, object);
		if (view == null) {
			super.destroyItem(container, position, object);
			return;
		}

		int index = container.indexOfChild(view);
		container.removeView(view);
		View child = ((ViewGroup) view).getChildAt(0);
		((ViewGroup) view).removeView(child);
		container.addView(child, index);

		super.destroyItem(container, position, object);
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		destroyItem((ViewGroup) container, position, object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		if (isOurContainer(view)) {
			return super.isViewFromObject(((ViewGroup) view).getChildAt(0), object);
		} else {
			return super.isViewFromObject(view, object);
		}
	}

	private View findViewFromObject(ViewGroup container, Object object) {
		View view = null;
		for (int i = 0; i < container.getChildCount(); i++) {
			view = container.getChildAt(i);
			if (isViewFromObject(view, object)) {
				break;
			}
			view = null;
		}
		return view;
	}

	protected boolean isOurContainer(View view) {
		return DynamicLayout.class.isInstance(view);
	}

	protected ViewGroup getContainer(Context context) {
		return new DynamicLayout(context);
	}

	private class DynamicLayout extends FrameLayout {
		public DynamicLayout(Context context) {
			super(context);
		}
	}
}
