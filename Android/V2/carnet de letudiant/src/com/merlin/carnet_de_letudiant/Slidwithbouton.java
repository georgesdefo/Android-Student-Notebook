package com.example.testfff;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;


public class Slidwithbouton extends FragmentActivity {

	//private PagerAdapter mPagerAdapter;
	
	private String mFragment = "Dynamic1Fragment";
    private MicroRecord mDynamic1Fragment;
    private PageDroiteFragment mDynamic2Fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//super.setContentView(R.layout.mainactivity);

		if (savedInstanceState != null)
            mFragment = savedInstanceState.getString("fragment");
			       
		//mFragment = getIntent().getStringExtra("fragment");
		
		       setupFragments();
		        if (mFragment.equals("Dynamic1Fragment")) {
		            showFragment(this.mDynamic1Fragment);
		        } else if (mFragment.equals("Dynamic2Fragment")) {
		            showFragment(this.mDynamic2Fragment);
		        }
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	       outState.putString("fragment", mFragment != null ? mFragment : "");
	       super.onSaveInstanceState(outState);
	}
	private void setupFragments() {
		
		final FragmentManager fm = getSupportFragmentManager();
		
		        this.mDynamic1Fragment = (MicroRecord) fm.findFragmentByTag(MicroRecord.TAGG);
		        if (this.mDynamic1Fragment == null) {
		            this.mDynamic1Fragment = new MicroRecord();
		        }
		
		        this.mDynamic2Fragment = (PageDroiteFragment) fm.findFragmentByTag(PageDroiteFragment.TAGG);
		        if (this.mDynamic2Fragment == null) {
		            this.mDynamic2Fragment = new PageDroiteFragment();
		        }
		
		
		        
    }

	private void showFragment(final Fragment fragment) {
		
		if (fragment == null)
            return;
		mFragment = fragment.getTag();//pour maintenir le bon fragment apres une rotation
		
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        // We can also animate the changing of fragment
        ft.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left);

       // ft.replace(R.id.main, fragment);

        ft.commit();
		
	}

	public void goToFragment1(View v) {
        showFragment(this.mDynamic1Fragment);
	}
		
    public void goToFragment2(View v) {
        showFragment(this.mDynamic2Fragment);
   }
}

