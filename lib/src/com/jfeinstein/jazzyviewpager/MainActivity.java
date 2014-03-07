package com.jfeinstein.jazzyviewpager;

import com.jfeinstein.jazzyviewpager.animation.DynamicAnimation;
import com.jfeinstein.jazzyviewpager.animation.FadeAnimation;
import com.jfeinstein.jazzyviewpager.animation.StaticAnimation;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	private JazzyViewPager mJazzy;
	boolean mFadeEnabled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mJazzy = (JazzyViewPager) findViewById(R.id.jazzy_pager);

		setupJazziness(mJazzy.findDynamicAnimation("Tablet"));
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
				mJazzy.setStaticAnimation(StaticAnimation.NULL);
			} else {
				mJazzy.setStaticAnimation(new FadeAnimation());
			}
			mFadeEnabled = !mFadeEnabled;
		} else {
			setupJazziness(mJazzy.findDynamicAnimation(item.getTitle().toString()));
		}
		return true;
	}

	private void setupJazziness(DynamicAnimation animation) {
		mJazzy.setDynamicAnimation(animation);
		mJazzy.setAdapter(new MainAdapter());
		mJazzy.setPageMargin(30);
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
			container.removeView(mJazzy.findViewFromObject(obj));
		}
		@Override
		public int getCount() {
			return 10;
		}
		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof OutlineContainer) {
				return ((OutlineContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}		
	}
}
