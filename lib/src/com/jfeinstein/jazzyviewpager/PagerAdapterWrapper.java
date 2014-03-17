
/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfeinstein.jazzyviewpager;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class PagerAdapterWrapper extends PagerAdapter {
	private PagerAdapter mAdapter;

	public PagerAdapterWrapper(PagerAdapter adapter) {
		mAdapter = adapter;
	}

	public PagerAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public int getCount() {
		return mAdapter.getCount();
	}

	@Override
	public void startUpdate(ViewGroup container) {
		mAdapter.startUpdate(container);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return mAdapter.instantiateItem(container, position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		mAdapter.destroyItem(container, position, object);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		mAdapter.setPrimaryItem(container, position, object);
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		mAdapter.finishUpdate(container);
	}

	@SuppressWarnings("deprecation")
	public void startUpdate(View container) {
		mAdapter.startUpdate(container);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object instantiateItem(View container, int position) {
		return mAdapter.instantiateItem(container, position);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void destroyItem(View container, int position, Object object) {
		mAdapter.destroyItem(container, position, object);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setPrimaryItem(View container, int position, Object object) {
		mAdapter.setPrimaryItem(container, position, object);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void finishUpdate(View container) {
		mAdapter.finishUpdate(container);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return mAdapter.isViewFromObject(view, object);
	}

	@Override
	public Parcelable saveState() {
		return mAdapter.saveState();
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		mAdapter.restoreState(state, loader);
	}

	@Override
	public int getItemPosition(Object object) {
		return mAdapter.getItemPosition(object);
	}

	@Override
	public void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mAdapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mAdapter.unregisterDataSetObserver(observer);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mAdapter.getPageTitle(position);
	}

	@Override
	public float getPageWidth(int position) {
		return mAdapter.getPageWidth(position);
	}
}
