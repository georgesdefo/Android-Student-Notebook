package com.example.testfff;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Start extends Activity implements OnClickListener{
    	
	public Button recA, bookmark,note,capture;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.startplus);
		
		recA = (Button)findViewById(R.id.recordaudio);
		bookmark = (Button)findViewById(R.id.mark);
		note = (Button)findViewById(R.id.note);
		capture = (Button)findViewById(R.id.capture);
		
		
		
		recA.setOnClickListener(this);
		bookmark.setOnClickListener(this);
		note.setOnClickListener(this);
		capture.setOnClickListener(this);
		
		
		
		//ceci eviter l'activite de crache au dbut en cas de changement d'orientation
		//en creant ne weak reference des le depart
		new Link(Start.this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.graph, menu);
	    
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setSubtitle("Student Notes");
			actionBar.setTitle("Home"); 
			
	        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | 
	        		ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM );
	
	        actionBar.setHomeButtonEnabled(true);
		}
	    
	    return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    switch (item.getItemId()) {
	    	  
		    
		    case R.id.action_audio: finish();//on sort de la pile ainsi la prochaine app est au bas de la pile et la seule presente
			 						startActivity(new Intent(Start.this, MainActivity.class));
 						break;
			case R.id.action_video: //finish();//on sort de la pile ainsi la prochaine app est au bas de la pile et la seule presente
									//startActivity( new Intent(this, VideoReco.class));
									Toast.makeText(getApplicationContext(),"l\'enregistrement video pour la suite", Toast.LENGTH_SHORT).show();
						break;
			default:
						break;
	    }	    

	    return super.onOptionsItemSelected(item);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
			
	}
	
	@Override
	public List<?> onRetainNonConfigurationInstance () {
		
		List<Object> list = new ArrayList<Object>();
		return list;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
			case R.id.recordaudio: finish();//on sort de la pile ainsi la prochaine app est au bas de la pile et la seule presente
									startActivity(new Intent(Start.this, MainActivity.class));
				break;
			case R.id.note:  	
				Toast.makeText(getApplicationContext(),"Commencer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
				break;
			case R.id.capture: 	//finish();//on sort de la pile ainsi la prochaine app est au bas de la pile et la seule presente
								//startActivity( new Intent(this, VideoReco.class));
								Toast.makeText(getApplicationContext(),"l\'enregistrement video pour la suite", Toast.LENGTH_SHORT).show();
				break;
			case R.id.mark: 	
				Toast.makeText(getApplicationContext(),"Commencer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	static class Link  {		
			// Référence faible à l'activité
			@SuppressWarnings("unused")
			private WeakReference<Start> mActivity = null;
			
			public Link (Start pActivity) {
				link(pActivity);
			}
		
			public void link (Start pActivity) {
				mActivity = new WeakReference<Start>(pActivity);
			}
	}
    		 
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
           finish();//on sort totalement de l'app car seul dans la pile.
           
        }
        
        return super.onKeyDown(keyCode, event);
    }
    
}

