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
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.State;
import com.jfeinstein.jazzyviewpager.animation.AccordionTransition;
import com.jfeinstein.jazzyviewpager.animation.Animation;
import com.jfeinstein.jazzyviewpager.animation.CubeInTransition;
import com.jfeinstein.jazzyviewpager.animation.CubeOutTransition;
import com.jfeinstein.jazzyviewpager.animation.DynamicTransition;
import com.jfeinstein.jazzyviewpager.animation.FlipHorizontalTransition;
import com.jfeinstein.jazzyviewpager.animation.FlipVerticalTransition;
import com.jfeinstein.jazzyviewpager.animation.RotateDownTransition;
import com.jfeinstein.jazzyviewpager.animation.RotateUpTransition;
import com.jfeinstein.jazzyviewpager.animation.StackTransition;
import com.jfeinstein.jazzyviewpager.animation.StandardTransition;
import com.jfeinstein.jazzyviewpager.animation.StaticTransition;
import com.jfeinstein.jazzyviewpager.animation.TabletTransition;
import com.jfeinstein.jazzyviewpager.animation.ZoomInTransition;
import com.jfeinstein.jazzyviewpager.animation.ZoomOutTransition;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ViewPagerPlayer {

	public static final String TAG = "ViewPagerPlayer";

	private static final int DEFAULT_DURATION = 30000;

	public static final int REPEAT_MODE_ONE = 0;
	public static final int REPEAT_MODE_ALL = 1;

	private Context mContext;
	private boolean mOutlineEnabled;
	private int mOutlineColor;
	private boolean mEnabled;

	private Map<String, DynamicTransition> mDynamicMap;
	private DynamicTransition mDynamicTransition = new StandardTransition();
	private StaticTransition mStaticTransition = StaticTransition.NULL;
	private Animation mAnimation = Animation.NULL;

	private boolean mStarted;
	private ValueAnimator mAnimator;
	private int mDuration;
	private int mRepeatMode;
	private int mRepeatCount;
	private Map<View, PageAnimator> mPageAnimators = new HashMap<View, PageAnimator>();

	private Method mInfoForPosition;
	private Field mItemInfoObject;
	private ViewPager mPager;
	private ContainerPagerAdapter mAdapter;
	private OnTouchListener mOuterOnTouchListener;
	private OnPageChangeListener mOuterOnPageChangeListener;

	private State mState;
	private int mCurrentPage;
	private int mNextPage;

	public ViewPagerPlayer(Context context, ViewPager pager) {
		mContext = context;
		mPager = pager;
		mStarted = false;
		mDuration = DEFAULT_DURATION * 2;
		mRepeatCount = 0;
		mOutlineEnabled = false;
		mOutlineColor = Color.WHITE;
		mEnabled = true;

		setupScroller();
		initDynamicMap();
		mPager.setClipChildren(false);
		mPager.setOnPageChangeListener(mOnPageChangeListener);
		mPager.setOnTouchListener(mOnTouchListener);
		updateAdapter();
	}

	public void setPagingEnabled(boolean enabled) {
		mEnabled = enabled;
	}

	public void setOutlineEnabled(boolean enabled) {
		mOutlineEnabled = enabled;
		updateAdapter();
	}

	public void setOutlineColor(int color) {
		mOutlineColor = color;
		updateAdapter();
	}

	public void setAdapter(PagerAdapter adapter) {
		updateAdapter(adapter);
	}

	private void updateAdapter() {
		if (mAdapter != null) {
			updateAdapter(mAdapter.getAdapter());
		} else {
			updateAdapter(mPager.getAdapter());
		}
	}

	private void updateAdapter(PagerAdapter adapter) {
		if (mOutlineEnabled) {
			mAdapter = new OutlinePagerAdapter(adapter, mContext);
			((OutlinePagerAdapter) mAdapter).setColor(mOutlineColor);
		} else {
			mAdapter = new ContainerPagerAdapter(adapter, mContext);
		}
		mPager.setAdapter(mAdapter);
	}

	private void initDynamicMap() {
		mDynamicMap = new HashMap<String, DynamicTransition>();
		mDynamicMap.put("Tablet", new TabletTransition());
		mDynamicMap.put("CubeIn", new CubeInTransition());
		mDynamicMap.put("CubeOut", new CubeOutTransition());
		mDynamicMap.put("FlipVertical", new FlipVerticalTransition(mPager));
		mDynamicMap.put("FlipHorizontal", new FlipHorizontalTransition(mPager));
		mDynamicMap.put("Stack", new StackTransition(mPager));
		mDynamicMap.put("ZoomIn", new ZoomInTransition());
		mDynamicMap.put("ZoomOut", new ZoomOutTransition());
		mDynamicMap.put("RotateUp", new RotateUpTransition(mPager));
		mDynamicMap.put("RotateDown", new RotateDownTransition(mPager));
		mDynamicMap.put("Accordion", new AccordionTransition());
	}

	public DynamicTransition findDynamicTransition(String name) {
		DynamicTransition transition = DynamicTransition.NULL;
		if (mDynamicMap.get(name) != null) {
			transition = mDynamicMap.get(name);
		}
		return transition;
	}

	public void setDynamicTransition(DynamicTransition animation) {
		mDynamicTransition = animation;
	}

	public void setStaticTransition(StaticTransition animation) {
		mStaticTransition = animation;
	}

	public void setAnimation(Animation animation) {
		mAnimation = animation;
	}

	public void start() {
		mStarted = true;
		play();
	}

	public void stop() {
		mStarted = false;
		mAnimator.cancel();
		stopAllPage();
		mAnimator = null;
	}

	public void setRepeatMode(int value) {
		if (value == REPEAT_MODE_ONE) {
			mRepeatCount = ValueAnimator.INFINITE;
		} else if (value == REPEAT_MODE_ALL) {
			mRepeatCount = 0;
		}
		mRepeatMode = value;
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
		mAnimator.setFloatValues(Animation.OFFSET_BEGIN, Animation.OFFSET_END);
		mAnimator.setDuration(mDuration);
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

		mAnimator.start();
	}

	public void setOnTouchListener(OnTouchListener listener) {
		mOuterOnTouchListener = listener;
	}

	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mEnabled) {
				if (mOuterOnTouchListener != null) {
					return mOuterOnTouchListener.onTouch(v, event);
				} else {
					return false;
				}
			} else {
				return true;
			}
		}
	};

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mOuterOnPageChangeListener = listener;
	}

	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			if (mOuterOnPageChangeListener != null) {
				mOuterOnPageChangeListener.onPageSelected(position);
			}

			if (mStarted) {
				playPage(findViewFromPosition(position));
				play();
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			if (mOuterOnPageChangeListener != null) {
				mOuterOnPageChangeListener.onPageScrolled(position, positionOffset,
						positionOffsetPixels);
			}

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

			if (mStarted) {
				play();
				playPage(left);
				if (positionOffset != 0) {
					playPage(right);
				}
			}

			mStaticTransition.animate(left, right, positionOffset, positionOffsetPixels, mState);
			mDynamicTransition.animate(left, right, positionOffset, positionOffsetPixels, mState);

			if (effectOffset == 0) {
				mState = State.IDLE;
				mCurrentPage = mNextPage;
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (mOuterOnPageChangeListener != null) {
				mOuterOnPageChangeListener.onPageScrollStateChanged(state);
			}
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

	private void playPage(View page) {
		if (page == null) {
			return;
		}

		PageAnimator animator = mPageAnimators.get(page);
		if (animator == null) {
			animator = new PageAnimator(page);
			mPageAnimators.put(page, animator);
		}

		if (!animator.isStarted()) {
			animator.start();
		}
	}

	private void stopAllPage() {
		for (Map.Entry<View, PageAnimator> entry : mPageAnimators.entrySet()) {
			entry.getValue().cancel();
		}
		mPageAnimators.clear();
	}

	private class PageAnimator extends ValueAnimator {
		private View mPage;

		public PageAnimator(View page) {
			super();

			mPage = page;

			setFloatValues(Animation.OFFSET_BEGIN, Animation.OFFSET_END);
			setDuration(DEFAULT_DURATION);
			setRepeatMode(ValueAnimator.REVERSE);
			setRepeatCount(Integer.MAX_VALUE);

			addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mAnimation.animate(mPage, Animation.OFFSET_BEGIN);
				}
			});

			addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float offset = (float) (Float) animation.getAnimatedValue();
					if (mPage.isShown()) {
						mAnimation.animate(mPage, offset);
					}
				}
			});
		}
	}

}
