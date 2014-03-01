package com.example.testfff;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class TabsPagerAdapter extends FragmentPagerAdapter {
 
	private Fragment[] fragments = new Fragment[] {
            new MicroRecord(), new PageDroiteFragment()};
	
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
	        case 0:
	            // MicroRecord fragment activity
	            return fragments[index];
	        case 1:
	            // PageDroiteFragment fragment activity
	            return fragments[index];
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return fragments.length;
    }
    /*
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragments[position] = fragment;
        Log.v("INSTANTIATEITEM","instantiateItem");
        return fragment;
    }
    
    @Override
    public Object instantiateItem(View container, int position) {
    	((ViewPager) container).setCurrentItem(position);
        return (ViewPager) container;
    }
*/
    public Fragment[] getFragments() {
        return fragments;
    }
}
