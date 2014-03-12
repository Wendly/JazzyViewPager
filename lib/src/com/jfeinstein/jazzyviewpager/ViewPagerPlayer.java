package com.jfeinstein.jazzyviewpager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.jfeinstein.jazzyviewpager.animation.AccordionAnimation;
import com.jfeinstein.jazzyviewpager.animation.CubeInAnimation;
import com.jfeinstein.jazzyviewpager.animation.CubeOutAnimation;
import com.jfeinstein.jazzyviewpager.animation.DynamicAnimation;
import com.jfeinstein.jazzyviewpager.animation.FlipHorizontalAnimation;
import com.jfeinstein.jazzyviewpager.animation.FlipVerticalAnimation;
import com.jfeinstein.jazzyviewpager.animation.RotateDownAnimation;
import com.jfeinstein.jazzyviewpager.animation.RotateUpAnimation;
import com.jfeinstein.jazzyviewpager.animation.StackAnimation;
import com.jfeinstein.jazzyviewpager.animation.StandardAnimation;
import com.jfeinstein.jazzyviewpager.animation.StaticAnimation;
import com.jfeinstein.jazzyviewpager.animation.TabletAnimation;
import com.jfeinstein.jazzyviewpager.animation.ZoomInAnimation;
import com.jfeinstein.jazzyviewpager.animation.ZoomOutAnimation;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ViewPagerPlayer {

	public static final String TAG = "ViewPagerPlayer";

	private Context mContext;

	private Map<String, DynamicAnimation> mDynamicMap;
	private DynamicAnimation mDynamicTransition = new StandardAnimation();
	private StaticAnimation mStaticTransition = StaticAnimation.NULL;
	private DynamicAnimation mAnimation = DynamicAnimation.NULL;

	private boolean mStarted;
	private ValueAnimator mAnimator;
	private int mDuration;
	private int mRepeatMode;
	private int mRepeatCount;

	private Method mInfoForPosition;
	private Field mItemInfoObject;
	private ViewPager mPager;

	private State mState;
	private int mCurrentPage;
	private int mNextPage;

	public ViewPagerPlayer(Context context, ViewPager pager) {
		mContext = context;
		mPager = pager;
		mStarted = false;
		mDuration = 30000;
		mRepeatCount = 0;
		mRepeatMode = ValueAnimator.REVERSE;

		setupScroller();
		initDynamicMap();
		mPager.setClipChildren(false);
		mPager.setOnPageChangeListener(mOnPageChangeListener);
	}

	private void initDynamicMap() {
		mDynamicMap = new HashMap<String, DynamicAnimation>();
		mDynamicMap.put("Tablet", new TabletAnimation());
		mDynamicMap.put("CubeIn", new CubeInAnimation());
		mDynamicMap.put("CubeOut", new CubeOutAnimation());
		mDynamicMap.put("FlipVertical", new FlipVerticalAnimation(mPager));
		mDynamicMap.put("FlipHorizontal", new FlipHorizontalAnimation(mPager));
		mDynamicMap.put("Stack", new StackAnimation(mPager));
		mDynamicMap.put("ZoomIn", new ZoomInAnimation());
		mDynamicMap.put("ZoomOut", new ZoomOutAnimation());
		mDynamicMap.put("RotateUp", new RotateUpAnimation(mPager));
		mDynamicMap.put("RotateDown", new RotateDownAnimation(mPager));
		mDynamicMap.put("Accordion", new AccordionAnimation());
	}

	public DynamicAnimation findDynamicAnimation(String name) {
		DynamicAnimation animation = DynamicAnimation.NULL;
		if (mDynamicMap.get(name) != null) {
			animation = mDynamicMap.get(name);
		}
		return animation;
	}

	public void setDynamicTransition(DynamicAnimation animation) {
		mDynamicTransition = animation;
	}

	public void setStaticTransition(StaticAnimation animation) {
		mStaticTransition = animation;
	}

	public void setAnimation(DynamicAnimation animation) {
		mAnimation = animation;
	}

	public void start() {
		mStarted = true;
		play();
	}

	public void stop() {
		mStarted = false;
		mAnimator.cancel();
		mAnimator = null;
	}

	public void setRepeatMode(int value) {
		mRepeatMode = value;
	}

	public void setRepeatCount(int value) {
		mRepeatCount = value;
	}

	public void next() {
		mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
	}

	public void previous() {
		mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
	}

	public void setDuration(int duration) {
		mDuration = duration;
	}

	private void play() {
		if (mAnimator != null) {
			mAnimator.cancel();
		}

		mAnimator = new PagerAnimator();
		mAnimator.setFloatValues(0, 1);
		mAnimator.setDuration(mDuration);
		mAnimator.setRepeatMode(mRepeatMode);
		mAnimator.setRepeatCount(mRepeatCount);

		mAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (!((PagerAnimator) animation).isCanceled()) {
					mAnimator = null;
					next();
				}
			}
		});

		mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float offset = (float) (Float) animation.getAnimatedValue();
				View view = findViewFromPosition(mPager.getCurrentItem());
				mAnimation.animate(view, null, offset, 0, State.GOING_RIGHT);
			}
		});

		mAnimator.start();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void disableHardwareLayer() {
		if (Build.VERSION.SDK_INT < 11) return;

		for (int i = 0; i < mPager.getChildCount(); i++) {
			View view = mPager.getChildAt(i);
			if (view.getLayerType() != View.LAYER_TYPE_NONE) {
				view.setLayerType(View.LAYER_TYPE_NONE, null);
			}
		}
	}

	private ViewPager.OnPageChangeListener mOnPageChangeListener =
			new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			if (mStarted) {
				play();
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
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

			View left = findViewFromPosition(position);
			View right = findViewFromPosition(position + 1);

			mStaticTransition.animate(left, right, positionOffset, positionOffsetPixels, mState);
			mDynamicTransition.animate(left, right, positionOffset, positionOffsetPixels, mState);

			if (effectOffset == 0) {
				disableHardwareLayer();
				mState = State.IDLE;
				mCurrentPage = mNextPage;
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};

	private void setupScroller() {
		try {
			Class<?> viewpager = ViewPager.class;
			Field scroller = viewpager.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			scroller.set(mPager, new MyScroller(mContext, new Interpolator() {
				private final float mScale = 7.0f;

				@Override
				public float getInterpolation(float input) {
					return 1 - (float) Math.exp(-input * mScale);
				}

			}));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public class MyScroller extends Scroller {
		public MyScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration) {
			float normalizedVelocity = (((float) Math.abs(dx)) / mPager.getWidth()) / duration;
			normalizedVelocity = (float) Math.min(normalizedVelocity, 0.001);
			duration = (int) ((((float) Math.abs(dx)) / mPager.getWidth()) / normalizedVelocity);
			super.startScroll(startX, startY, dx, dy, duration);
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

			Object info = mInfoForPosition.invoke(mPager, position);
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
		Object object = getObjectFromPosition(position);
		if (object == null) {
			return null;
		}
		return findViewFromObject(object);
	}

	public View findViewFromObject(Object o) {
		PagerAdapter adapter = mPager.getAdapter();
		for (int i = 0; i < mPager.getChildCount(); i++) {
			View view = mPager.getChildAt(i);
			if (adapter.isViewFromObject(view, o)) {
				return view;
			}
		}
		return null;
	}

	private class PagerAnimator extends ValueAnimator {
		private boolean mCanceled = false;

		public PagerAnimator() {
			super();
		}

		public void start() {
			mCanceled = false;
			super.start();
		}

		public void cancel() {
			mCanceled = true;
			super.cancel();
		}

		public boolean isCanceled() {
			return mCanceled;
		}
	}

}
