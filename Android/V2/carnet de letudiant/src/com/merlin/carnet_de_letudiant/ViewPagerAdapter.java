package com.example.testfff;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter{

	public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
   }

	@Override
	public Fragment getItem(int pos) {
	        switch(pos) {
	        case 1: return MicroRecord.newInstance("Je suis le premier écran !");
	        case 2: return PageDroiteFragment.newInstance("Je suis le second écran !");
	    }
	        return null;
	}
    
	@Override
	public int getCount() {
		return 2;
	}

}
