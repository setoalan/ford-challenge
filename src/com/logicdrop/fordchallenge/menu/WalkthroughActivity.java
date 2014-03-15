package com.logicdrop.fordchallenge.menu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.logicdrop.fordchallenge.R;

public class WalkthroughActivity extends Activity {

	private ViewPager mPager;
	private WalkthroughAdapter mWalkthroughAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.walkthrough);
		
		mPager = (ViewPager) findViewById(R.id.pager);
		mWalkthroughAdapter = new WalkthroughAdapter();
		mPager.setAdapter(mWalkthroughAdapter);
		mPager.setCurrentItem(0);
	}
	
	class WalkthroughAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 5;
		}
		
		public Object instantiateItem(View collection, int position) {
			ImageView img = new ImageView(getBaseContext());
			((ViewPager) collection).addView(img, 0);
			int resId = 0;
			switch (position) {
				case 0:
					resId = R.drawable.intro;
					break;
				case 1:
					resId = R.drawable.camera;
					break;
				case 2:
					resId = R.drawable.display;
					break;
				case 3:
					resId = R.drawable.challenge;
					break;
				case 4:
					resId = R.drawable.compete;
					break;
			}
			img.setScaleType(ScaleType.FIT_XY);
			img.setImageResource(resId);
			return img;
		}
		
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);
		}
	}
}
