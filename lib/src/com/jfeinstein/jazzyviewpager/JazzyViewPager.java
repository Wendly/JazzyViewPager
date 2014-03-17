package com.jfeinstein.jazzyviewpager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jfeinstein.jazzyviewpager.animation.AccordionTransition;
import com.jfeinstein.jazzyviewpager.animation.CubeInTransition;
import com.jfeinstein.jazzyviewpager.animation.CubeOutTransition;
import com.jfeinstein.jazzyviewpager.animation.DynamicTransition;
import com.jfeinstein.jazzyviewpager.animation.FadeTransition;
import com.jfeinstein.jazzyviewpager.animation.FlipHorizontalTransition;
import com.jfeinstein.jazzyviewpager.animation.FlipVerticalTransition;
import com.jfeinstein.jazzyviewpager.animation.RotateDownTransition;
import com.jfeinstein.jazzyviewpager.animation.RotateUpTransition;
import com.jfeinstein.jazzyviewpager.animation.StackTransition;
import com.jfeinstein.jazzyviewpager.animation.StaticTransition;
import com.jfeinstein.jazzyviewpager.animation.TabletTransition;
import com.jfeinstein.jazzyviewpager.animation.ZoomInTransition;
import com.jfeinstein.jazzyviewpager.animation.ZoomOutTransition;
import com.jfeinstein.jazzyviewpager.animation.OutlineTransition;

public class JazzyViewPager extends ViewPager {

	public static final String TAG = "JazzyViewPager";

	private Context mContext;
	private ContainerPagerAdapter mAdapter;

	private boolean mEnabled = true;
	private boolean mOutlineEnabled = false;
	private OutlineTransition mOutlineTransition = new OutlineTransition();
	public static int sOutlineColor = Color.WHITE;

	private DynamicTransition mDynamicTransition = DynamicTransition.NULL;
	private Map<String, DynamicTransition> mDynamicMap;
	private StaticTransition mStaticTransition = StaticTransition.NULL;
	
	private Method mInfoForPosition;
	private Field mItemInfoObject;

	public JazzyViewPager(Context context) {
		this(context, null);
	}

	public JazzyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setClipChildren(false);
		initDynamicMap();
		// now style everything!
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.JazzyViewPager);
		int effect = ta.getInt(R.styleable.JazzyViewPager_style, 0);
		String[] transitions = getResources().getStringArray(R.array.jazzy_effects);
		setDynamicTransition(findDynamicTransition(transitions[effect]));
		if (ta.getBoolean(R.styleable.JazzyViewPager_fadeEnabled, false)) {
			setStaticTransition(new FadeTransition());
		}
		setOutlineEnabled(ta.getBoolean(R.styleable.JazzyViewPager_outlineEnabled, false));
		setOutlineColor(ta.getColor(R.styleable.JazzyViewPager_outlineColor, Color.WHITE));
		ta.recycle();
	}

	private void initDynamicMap() {
		mDynamicMap = new HashMap<String, DynamicTransition>();
		mDynamicMap.put("Tablet", new TabletTransition());
		mDynamicMap.put("CubeIn", new CubeInTransition());
		mDynamicMap.put("CubeOut", new CubeOutTransition());
		mDynamicMap.put("FlipVertical", new FlipVerticalTransition(this));
		mDynamicMap.put("FlipHorizontal", new FlipHorizontalTransition(this));
		mDynamicMap.put("Stack", new StackTransition(this));
		mDynamicMap.put("ZoomIn", new ZoomInTransition());
		mDynamicMap.put("ZoomOut", new ZoomOutTransition());
		mDynamicMap.put("RotateUp", new RotateUpTransition(this));
		mDynamicMap.put("RotateDown", new RotateDownTransition(this));
		mDynamicMap.put("Accordion", new AccordionTransition());
	}

	public DynamicTransition findDynamicTransition(String name) {
		DynamicTransition transition = DynamicTransition.NULL;
		if (mDynamicMap.get(name) != null) {
			transition = mDynamicMap.get(name);
		}
		return transition;
	}

	public void setDynamicTransition(DynamicTransition transition) {
		mDynamicTransition = transition;
	}

	public void setStaticTransition(StaticTransition transition) {
		mStaticTransition = transition;
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

	@Override
	public void setAdapter(PagerAdapter adapter) {
		mAdapter = new ContainerPagerAdapter(adapter, mContext);
		super.setAdapter(mAdapter);
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

		mStaticTransition.animate(mLeft, mRight, effectOffset, positionOffsetPixels, mState);

		if (mOutlineEnabled) {
			mOutlineTransition.animate(mLeft, mRight, effectOffset, positionOffsetPixels, mState);
		}

		mDynamicTransition.animate(mLeft, mRight, positionOffset, positionOffsetPixels, mState);

		super.onPageScrolled(position, positionOffset, positionOffsetPixels);

		if (effectOffset == 0) {
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
