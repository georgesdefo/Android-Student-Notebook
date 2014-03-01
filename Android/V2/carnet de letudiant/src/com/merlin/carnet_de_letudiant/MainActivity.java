package com.example.testfff;


import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener{

	
	private ViewPager mViewPager;
	private ViewPager mLandscapePager;
	private Boolean mIsLandscape;
	
	FragmentManager fm = null;
	FragmentTransaction fragmentTransaction =null;
	private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    
 // Tab titles
    private String[] tabs = { "Micro", "PageDroite" };
	
	PageDroiteFragment pdf;
	MicroRecord micro;
	static String fragname = null;
	static int curent_position = 0;
	
	//android:configChanges="orientation"
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpageland);
        
        fm = getSupportFragmentManager();
        Resources res = getResources();
        mIsLandscape = (res.getConfiguration().orientation == 1) ? false : true;        
        mAdapter = new TabsPagerAdapter(fm); 
        
       
       
        //apres le changement d'orientation
        if (savedInstanceState != null){       	
        	curent_position = savedInstanceState.getInt("curent_position");
        	fragname 		= savedInstanceState.getString("fragment");
        	
        	if(fragname == "PageDroiteFragment")
        		pdf = (PageDroiteFragment) fm.findFragmentByTag(PageDroiteFragment.TAGG);
        	else if(fragname == "MicroRecord")
        		micro = (MicroRecord) fm.findFragmentByTag(MicroRecord.TAGG);
        }
        else{
        	fragname = null;
        	curent_position = 0;        	
        	    		    		
     		//on recupere notre fragment root pour d'eventuel tache
     		micro = (MicroRecord) fm.findFragmentByTag(MicroRecord.TAGG);
     		
     		
        }
        
        if (mIsLandscape) 
        	setland();
        else
        	setport();
        
        //config bar      
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.setSubtitle("Student Notes");
			actionBar.setTitle("Enregistrement"); 
			
	        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | 
	        		ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM );
	
	        actionBar.setHomeButtonEnabled(true);
			
			
			
		}
        
        	
        // Adding Tabs avec mAdapter
	        for (String tab_name : tabs) {
	            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
	        }
	
    }
    
    private void setport() {
    	 mViewPager = (ViewPager) super.findViewById(R.id.viewPager);
    	 //Toast.makeText(MainActivity.this,curent_position+"", Toast.LENGTH_SHORT).show();
    	 mViewPager.setAdapter(mAdapter);
    	 
    	 mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
 	        @Override
 	        public void onPageScrolled(final int position, final float v, final int i2) {
 	        	
 	        }
 	        @Override
 	        public void onPageSelected(final int position) {
 	            YourFragmentInterface fragment = (YourFragmentInterface) mAdapter.instantiateItem(mViewPager, position);
 	           curent_position = position;
 	            if (fragment != null) {
 	            	
 	                fragment.fragmentBecameVisible();
 	                if(Utils.fixeur == 0){
 	                	micro = (MicroRecord) fm.findFragmentByTag(MicroRecord.TAGG);
 	                	//Toast.makeText(MainActivity.this,curent_position+"", Toast.LENGTH_SHORT).show();
 	                	fragname = "MicroRecord";
 	                	actionBar.setSelectedNavigationItem(position);
 	                }
 	                else{
 	                	pdf = (PageDroiteFragment) fm.findFragmentByTag(PageDroiteFragment.TAGG);
 	                	//Toast.makeText(MainActivity.this,curent_position+"", Toast.LENGTH_SHORT).show();
 	                	fragname = "PageDroiteFragment";
 	                	actionBar.setSelectedNavigationItem(position);
 	                }
 	              
 	            }
 	            
 	            
 	        }
 	        @Override
 	        public void onPageScrollStateChanged(final int position) {
 	        }
 	    }); 
    	 
    	// set the default page 	 
    	 if(fragname == null){
  			Log.v("TRANSACTION","INIT");
  			Log.v("getBackStackEntryCount",	getSupportFragmentManager().getBackStackEntryCount()+""); 
  		}else{
  			/*
  			if(getSupportFragmentManager().getBackStackEntryCount() > 2){
		        getSupportFragmentManager().popBackStackImmediate();
		        getSupportFragmentManager().popBackStackImmediate();            
		    }
 			*/
  			Log.v("getBackStackEntryCount",	getSupportFragmentManager().getBackStackEntryCount()+""); 
  			//testbacktrace();
  			if(fragname =="PageDroiteFragment"){ 		
 	 			Log.v("TRANSACTION","PAGEDROITE");	
 	 		//	mViewPager.setCurrentItem(0);		
 	 		}else if(fragname =="MicroRecord"){
 	 			mViewPager.setCurrentItem(0);
 	 		//	Log.v("TRANSACTION","MICRORECORD");
 	 		}
  			
  			
  		
  		}
     	 
	}

	private void setland() {      
        mLandscapePager = (ViewPager) super.findViewById(R.id.viewpagerland);        
		mLandscapePager.setAdapter(mAdapter);
			
		mLandscapePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
	        @Override
	        public void onPageScrolled(final int position, final float v, final int i2) {
	        	
	        }
	        @Override
	        public void onPageSelected(final int position) {
	            YourFragmentInterface fragment = (YourFragmentInterface) mAdapter.instantiateItem(mLandscapePager, position);
	            curent_position = position;
	            if (fragment != null) {
	            	
	                fragment.fragmentBecameVisible();
	                if(Utils.fixeur == 0){
	                	micro = (MicroRecord) fm.findFragmentByTag(MicroRecord.TAGG);
	                	//Toast.makeText(MainActivity.this,curent_position+"", Toast.LENGTH_SHORT).show();
	                	fragname = "MicroRecord";
	                	actionBar.setSelectedNavigationItem(position);
	                }
	                else{
	                	pdf = (PageDroiteFragment) fm.findFragmentByTag(PageDroiteFragment.TAGG);
	                	//Toast.makeText(MainActivity.this,curent_position+"", Toast.LENGTH_SHORT).show();
	                	fragname = "PageDroiteFragment";
	                	actionBar.setSelectedNavigationItem(position);
	                }
	                
	            
	            }
	            
	            
	        }
	        @Override
	        public void onPageScrollStateChanged(final int position) {
	        }
	    }); 
		
		// set the default page
		//actionBar.setSelectedNavigationItem(1);
		if(fragname == null){
			Log.v("TRANSACTION","INIT");	
			Log.v("getBackStackEntryCount",	getSupportFragmentManager().getBackStackEntryCount()+""); 
		}else{
			/*
			if(getSupportFragmentManager().getBackStackEntryCount() > 2){
		        getSupportFragmentManager().popBackStackImmediate();
		        getSupportFragmentManager().popBackStackImmediate();            
		    }
			*/
			Log.v("getBackStackEntryCount",	getSupportFragmentManager().getBackStackEntryCount()+""); 
			//testbacktrace();
 			if(fragname =="PageDroiteFragment"){ 	
	 			//mLandscapePager.setCurrentItem(0);
	 		}else if(fragname =="MicroRecord"){
	 			//mLandscapePager.setCurrentItem(0);
	 			Log.v("TRANSACTION","MICRORECORD");
	 		}
 			
 			
 		}
	}

	@Override
    protected void onSaveInstanceState(Bundle outState) {
	    outState.putString("fragment", fragname != null ? fragname :null);
	    outState.putInt("curent_position",curent_position);
	    super.onSaveInstanceState(outState);
    }
     void testbacktrace(){
    	 for(int i =0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
    		    Log.v("DualPaneActivity", getSupportFragmentManager().getBackStackEntryAt(i).getName());    
    		}

    		getSupportFragmentManager().popBackStack();
    		getSupportFragmentManager().popBackStack();
    		getSupportFragmentManager().executePendingTransactions();

    		for(int i =0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
    		    Log.v("DualPaneActivity", getSupportFragmentManager().getBackStackEntryAt(i).getName());    
    		} 
     }
    public interface YourFragmentInterface {
        void fragmentBecameVisible();
    }
    		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			
	     if ((keyCode == KeyEvent.KEYCODE_BACK)) {//si bouton retour
	        	
	        if(Utils.fixeur== 0)//si dans micro
	        	fixebeforesave();
	        else{
	        	fixebeforesave();
	        	//Toast.makeText(this,"c'etait 11111", Toast.LENGTH_LONG).show();
	        }
	        			           
	    }
	        
	        return super.onKeyDown(keyCode, event);//?????????????????????
	 }
	
	void fixebeforesave(){
		

	    	if(MicroRecord.blocprogress){//si pas en cours d'enregistrement car passe a false quand on lance un enregistrement
	    		
	    		 Log.d("TRHEAD STOPPED","back button pressed");
		            
	   	               Changepage();//on change pour une autre activite
	   	 
	        }	
		     else{ 
				Toast.makeText(this, " Enregistrement stoppe mais sauvegarde", Toast.LENGTH_LONG).show();	    			
				 Log.d("TRHEAD STOPPED","back button pressed");
		             if(MicroRecord.mProgress != null && !MicroRecord.mProgress.isCancelled()) {
	
		            	 killthread();//stop le thread	  	        		
		                 Log.i("TRHEAD STOPPED", "termine");
			  			    	  
		             	}
		          Changepage();
		          
		    }
	}
	private void Changepage() {
		 Utils.alert = 1;
		 MicroRecord.menuIsInflated= false;
         Intent intent = new Intent(this, Start.class);
   	     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
   	     this.finish();
   	     startActivity(intent);
   	    
	}

	private void killthread() {
		//on re initiale le necessaire en vue d'un retour dans la page
		 Utils.startnewrecord = false;
		 MicroRecord.bookmark.setBackgroundResource(R.drawable.bookmark);
		// MicroRecord.tv.setText(MicroRecord.LABEL);	
		 Utils.precedentnamefile = null;
		 if(MediaRecordAndPlaying.getRecorder()!=null)
			 MediaRecordAndPlaying.stopMediaRecorder();//on arrete le recorder
		 if(MicroRecord.mProgress!=null)
		 MicroRecord.mProgress.cancel(true);
		
	}
	/*private void killThreadWithoutSave() {
		 Utils.startnewrecord = false;
		 MicroRecord.bookmark.setBackgroundResource(R.drawable.bookmark);
		 //MicroRecord.tv.setText(MicroRecord.LABEL);	
		 Utils.precedentnamefile = null;
		 if(MicroRecord.mProgress!=null)
		 MicroRecord.mProgress.cancel(true);
		
	}
		*/		
	@Override
	public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// on tab selected
        // show respected fragment view
		 if (mIsLandscape){
			 curent_position = tab.getPosition();
	            mLandscapePager.setCurrentItem(tab.getPosition());
		 }
	     else{ 
	    	 curent_position = tab.getPosition();
	        	mViewPager.setCurrentItem(tab.getPosition());
	     }
	              		
	}
	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onStop(){
	    super.onStop();
	   
	}
}
