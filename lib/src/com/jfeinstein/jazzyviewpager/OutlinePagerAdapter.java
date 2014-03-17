package com.jfeinstein.jazzyviewpager;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class OutlinePagerAdapter extends ContainerPagerAdapter {
	private int mColor = Color.WHITE;

	public OutlinePagerAdapter(PagerAdapter adapter, Context context) {
		super(adapter, context);
	}

	protected boolean isOurContainer(View view) {
		return OutlineContainer.class.isInstance(view);
	}

	protected ViewGroup getContainer(Context context) {
		OutlineContainer container = new OutlineContainer(context);
		container.setColor(mColor);
		return container;
	}

	public void setColor(int color) {
		mColor = color;
	}
}
