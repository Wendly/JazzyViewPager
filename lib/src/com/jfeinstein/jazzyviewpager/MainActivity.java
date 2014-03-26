package com.jfeinstein.jazzyviewpager;

import com.jfeinstein.jazzyviewpager.animation.FadeTransition;
import com.jfeinstein.jazzyviewpager.animation.StaticTransition;
import com.jfeinstein.jazzyviewpager.animation.ZoomOutAnimation;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ViewPager mPager;
	private ViewPagerPlayer mPlayer;
	boolean mFadeEnabled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPager = (ViewPager) findViewById(R.id.jazzy_pager);
		mPager.setPageMargin(30);

		setupJazziness("Tablet");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Toggle Fade");
		String[] effects = this.getResources().getStringArray(R.array.jazzy_effects);
		for (String effect : effects)
			menu.add(effect);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equals("Toggle Fade")) {
			if (!mFadeEnabled) {
				mPlayer.setStaticTransition(StaticTransition.NULL);
			} else {
				mPlayer.setStaticTransition(new FadeTransition());
			}
			mFadeEnabled = !mFadeEnabled;
		} else {
			setupJazziness(item.getTitle().toString());
		}
		return true;
	}

	private void setupJazziness(String animation) {
		mPlayer = new ViewPagerPlayer(this, mPager);
		mPlayer.setAdapter(new MainAdapter());
		mPlayer.setDynamicTransition(mPlayer.findDynamicTransition(animation));
		mPlayer.setAnimation(new ZoomOutAnimation(1.0f, 0.5f, 0.5f));
		mPlayer.setOutlineEnabled(true);
		mPlayer.start();
	}

	private class MainAdapter extends PagerAdapter {
		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			TextView text = new TextView(MainActivity.this);
			text.setGravity(Gravity.CENTER);
			text.setTextSize(30);
			text.setTextColor(Color.WHITE);
			text.setText("Page " + position);
			text.setPadding(30, 30, 30, 30);
			int bg = Color.rgb((int) Math.floor(Math.random()*128)+64, 
					(int) Math.floor(Math.random()*128)+64,
					(int) Math.floor(Math.random()*128)+64);
			text.setBackgroundColor(bg);
			container.addView(text, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return text;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object obj) {
			container.removeView(findViewFromObject(container, obj));
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

		@Override
		public int getCount() {
			return 10;
		}
		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}
	}
}
