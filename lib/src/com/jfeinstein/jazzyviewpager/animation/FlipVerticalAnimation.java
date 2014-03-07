package com.jfeinstein.jazzyviewpager.animation;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.nineoldandroids.view.ViewHelper;

import android.support.v4.view.ViewPager;
import android.view.View;

public class FlipVerticalAnimation extends BaseDynamicAnimation {
	private ViewPager mPager;

	public FlipVerticalAnimation(ViewPager pager) {
		super();
		mPager = pager;
	}

	public void animate(View left, View right, float positionOffset, int positionOffsetPixels,
			State state) {
		if(state != State.IDLE) {
			if (left != null) {
				manageLayer(left, true);
				float rot = 180.0f * positionOffset;
				if (rot > 90.0f) {
					left.setVisibility(View.INVISIBLE);
				} else {
					if (left.getVisibility() == View.INVISIBLE) {
						left.setVisibility(View.VISIBLE);
					}

					float trans = positionOffsetPixels;
					ViewHelper.setPivotX(left, left.getMeasuredWidth() * 0.5f);
					ViewHelper.setPivotY(left, left.getMeasuredHeight() * 0.5f);
					ViewHelper.setTranslationX(left, trans);
					ViewHelper.setRotationX(left, rot);
				}
			}
			if (right != null) {
				manageLayer(right, true);
				float rot = -180.0f * (1 - positionOffset);
				if (rot < -90.0f) {
					right.setVisibility(View.INVISIBLE);
				} else {
					if (right.getVisibility() == View.INVISIBLE) {
						right.setVisibility(View.VISIBLE);
					}

					float trans = positionOffsetPixels - mPager.getWidth() - mPager.getPageMargin();
					ViewHelper.setPivotX(right, right.getMeasuredWidth() * 0.5f);
					ViewHelper.setPivotY(right, right.getMeasuredHeight() * 0.5f);
					ViewHelper.setTranslationX(right, trans);
					ViewHelper.setRotationX(right, rot);
				}
			}
		}
	}
}
