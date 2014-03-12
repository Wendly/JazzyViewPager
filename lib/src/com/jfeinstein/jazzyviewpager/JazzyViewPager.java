package com.jfeinstein.jazzyviewpager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jfeinstein.jazzyviewpager.animation.AccordionAnimation;
import com.jfeinstein.jazzyviewpager.animation.CubeInAnimation;
import com.jfeinstein.jazzyviewpager.animation.CubeOutAnimation;
import com.jfeinstein.jazzyviewpager.animation.DynamicAnimation;
import com.jfeinstein.jazzyviewpager.animation.FadeAnimation;
import com.jfeinstein.jazzyviewpager.animation.FlipHorizontalAnimation;
import com.jfeinstein.jazzyviewpager.animation.FlipVerticalAnimation;
import com.jfeinstein.jazzyviewpager.animation.RotateDownAnimation;
import com.jfeinstein.jazzyviewpager.animation.RotateUpAnimation;
import com.jfeinstein.jazzyviewpager.animation.StackAnimation;
import com.jfeinstein.jazzyviewpager.animation.StaticAnimation;
import com.jfeinstein.jazzyviewpager.animation.TabletAnimation;
import com.jfeinstein.jazzyviewpager.animation.ZoomInAnimation;
import com.jfeinstein.jazzyviewpager.animation.ZoomOutAnimation;
import com.jfeinstein.jazzyviewpager.animation.OutlineAnimation;

public class JazzyViewPager extends ViewPager {

	public static final String TAG = "JazzyViewPager";

	private boolean mEnabled = true;
	private boolean mOutlineEnabled = false;
	private OutlineAnimation mOutlineAnimation = new OutlineAnimation();
	public static int sOutlineColor = Color.WHITE;

	private DynamicAnimation mDynamicAnimation = DynamicAnimation.NULL;
	private Map<String, DynamicAnimation> mDynamicMap;
	private StaticAnimation mStaticAnimation = StaticAnimation.NULL;
	
	private Method mInfoForPosition;
	private Field mItemInfoObject;

	private static final boolean API_11;
	static {
		API_11 = Build.VERSION.SDK_INT >= 11;
	}

	public JazzyViewPager(Context context) {
		this(context, null);
	}

	public JazzyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		setClipChildren(false);
		initDynamicMap();
		// now style everything!
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.JazzyViewPager);
		int effect = ta.getInt(R.styleable.JazzyViewPager_style, 0);
		String[] transitions = getResources().getStringArray(R.array.jazzy_effects);
		setDynamicAnimation(findDynamicAnimation(transitions[effect]));
		if (ta.getBoolean(R.styleable.JazzyViewPager_fadeEnabled, false)) {
			setStaticAnimation(new FadeAnimation());
		}
		setOutlineEnabled(ta.getBoolean(R.styleable.JazzyViewPager_outlineEnabled, false));
		setOutlineColor(ta.getColor(R.styleable.JazzyViewPager_outlineColor, Color.WHITE));
		ta.recycle();
	}

	private void initDynamicMap() {
		mDynamicMap = new HashMap<String, DynamicAnimation>();
		mDynamicMap.put("Tablet", new TabletAnimation());
		mDynamicMap.put("CubeIn", new CubeInAnimation());
		mDynamicMap.put("CubeOut", new CubeOutAnimation());
		mDynamicMap.put("FlipVertical", new FlipVerticalAnimation(this));
		mDynamicMap.put("FlipHorizontal", new FlipHorizontalAnimation(this));
		mDynamicMap.put("Stack", new StackAnimation(this));
		mDynamicMap.put("ZoomIn", new ZoomInAnimation());
		mDynamicMap.put("ZoomOut", new ZoomOutAnimation());
		mDynamicMap.put("RotateUp", new RotateUpAnimation(this));
		mDynamicMap.put("RotateDown", new RotateDownAnimation(this));
		mDynamicMap.put("Accordion", new AccordionAnimation());
	}

	public DynamicAnimation findDynamicAnimation(String name) {
		DynamicAnimation animation = DynamicAnimation.NULL;
		if (mDynamicMap.get(name) != null) {
			animation = mDynamicMap.get(name);
		}
		return animation;
	}

	public void setDynamicAnimation(DynamicAnimation animation) {
		mDynamicAnimation = animation;
	}

	public void setStaticAnimation(StaticAnimation animation) {
		mStaticAnimation = animation;
	}

	public void setPagingEnabled(boolean enabled) {
		mEnabled = enabled;
	}

	public void setOutlineEnabled(boolean enabled) {
		mOutlineEnabled = enabled;
		wrapWithOutlines();
	}

	public void setOutlineColor(int color) {
		sOutlineColor = color;
	}

	private void wrapWithOutlines() {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (!(v instanceof OutlineContainer)) {
				removeView(v);
				super.addView(wrapChild(v), i);
			}
		}
	}

	private View wrapChild(View child) {
		if (!mOutlineEnabled || child instanceof OutlineContainer) return child;
		OutlineContainer out = new OutlineContainer(getContext());
		out.setLayoutParams(generateDefaultLayoutParams());
		child.setLayoutParams(new OutlineContainer.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		out.addView(child);
		return out;
	}

	public void addView(View child) {
		super.addView(wrapChild(child));
	}

	public void addView(View child, int index) {
		super.addView(wrapChild(child), index);
	}

	public void addView(View child, LayoutParams params) {
		super.addView(wrapChild(child), params);
	}

	public void addView(View child, int width, int height) {
		super.addView(wrapChild(child), width, height);
	}

	public void addView(View child, int index, LayoutParams params) {
		super.addView(wrapChild(child), index, params);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return mEnabled ? super.onInterceptTouchEvent(arg0) : false;
	}

	private State mState;
	private int mCurrentPage;
	private int mNextPage;

	private View mLeft;
	private View mRight;

	public enum State {
		IDLE,
		GOING_LEFT,
		GOING_RIGHT
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void disableHardwareLayer() {
		if (!API_11) return;
		View v;
		for (int i = 0; i < getChildCount(); i++) {
			v = getChildAt(i);
			if (v.getLayerType() != View.LAYER_TYPE_NONE)
				v.setLayerType(View.LAYER_TYPE_NONE, null);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (positionOffset < 0.3) {
			mNextPage = position;
		} else if (positionOffset > 0.7) {
			mNextPage = position + 1;
		}

		if (position > mCurrentPage) {
			mCurrentPage = mNextPage;
		} else if ((mCurrentPage - position) > 1) {
			mCurrentPage = mNextPage;
		}

		if (mCurrentPage == position) {
			mState = State.GOING_RIGHT;
		} else {
			mState = State.GOING_LEFT;
		}

		float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;

		mLeft = findViewFromPosition(position);
		mRight = findViewFromPosition(position + 1);

		mStaticAnimation.animate(mLeft, mRight, effectOffset, positionOffsetPixels, mState);

		if (mOutlineEnabled) {
			mOutlineAnimation.animate(mLeft, mRight, effectOffset, positionOffsetPixels, mState);
		}

		mDynamicAnimation.animate(mLeft, mRight, positionOffset, positionOffsetPixels, mState);

		super.onPageScrolled(position, positionOffset, positionOffsetPixels);

		if (effectOffset == 0) {
			disableHardwareLayer();
			mState = State.IDLE;
			mCurrentPage = mNextPage;
		}
	}

	private boolean isSmall(float positionOffset) {
		return Math.abs(positionOffset) < 0.0001;
	}

	private Object getObjectFromPosition(int position) {
		Object object = null;

		try {
			if (mInfoForPosition == null) {
				mInfoForPosition = ViewPager.class.getDeclaredMethod("infoForPosition", int.class);
				mInfoForPosition.setAccessible(true);
			}

			Object info = mInfoForPosition.invoke(this, position);
			if (info == null) {
				return object;
			}

			if (mItemInfoObject == null) {
				mItemInfoObject = info.getClass().getDeclaredField("object");
				mItemInfoObject.setAccessible(true);
			}

			object = mItemInfoObject.get(info);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		return object;
	}

	public View findViewFromPosition(int position) {
		Object o = getObjectFromPosition(position);
		if (o == null) {
			return null;
		}
		return findViewFromObject(o);
	}

	public View findViewFromObject(Object o) {
		PagerAdapter a = getAdapter();
		View v;
		for (int i = 0; i < getChildCount(); i++) {
			v = getChildAt(i);
			if (a.isViewFromObject(v, o))
				return v;
		}
		return null;
	}
}
