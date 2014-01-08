package com.merlin.studentnotes;



import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class Splash extends Activity {
    
	MediaPlayer ourSong;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		ourSong= MediaPlayer.create(Splash.this,R.raw.song );
		ourSong.start();
		 
		Thread t=new Thread(){
			public void run()
			{
				try{
					
					sleep(3000);
				}catch(InterruptedException e)
				{
					
				}
				finally{
					
					Intent openActivity = new Intent(Splash.this,Start.class);
					startActivity(openActivity);
				}
			}
		};
		t.start();
	}


	protected void onPause() {
		
		super.onPause();
		ourSong.release();
		ourSong = null;
		finish();
	}

	

}
