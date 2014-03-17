package com.jfeinstein.jazzyviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class OutlinePagerAdapter extends ContainerPagerAdapter {
	public OutlinePagerAdapter(PagerAdapter adapter, Context context) {
		super(adapter, context);
	}

	protected boolean isOurContainer(View view) {
		return OutlineContainer.class.isInstance(view);
	}

	protected ViewGroup getContainer(Context context) {
		return new OutlineContainer(context);
	}
}
