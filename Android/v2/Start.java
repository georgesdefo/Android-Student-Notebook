package com.merlin.studentnote;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Start extends Activity implements OnClickListener{
    
	MediaRecordAndPlaying rec;
	private ProgressTask mProgress = null;
	private List<?> list ;	
	
	public Button recA, bookmark,note,capture;
	public TextView tv = null;
	public Button stop = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.startplus);
		
		recA = (Button)findViewById(R.id.recordaudio);
		bookmark = (Button)findViewById(R.id.mark);
		note = (Button)findViewById(R.id.note);
		capture = (Button)findViewById(R.id.capture);
		
		stop =  (Button)findViewById(R.id.stop);
		tv = (TextView)findViewById(R.id.tv); 
		
		
		bookmark.setEnabled(false);
		
		recA.setOnClickListener(this);
		bookmark.setOnClickListener(this);
		note.setOnClickListener(this);
		capture.setOnClickListener(this);
		stop.setOnClickListener(this);
		
		stop.setClickable(false);
		
		tv.setText("click to start recording...");
		
		// On recupère l'AsyncTask perdu dans le changement de configuration
		list = (List<?>) getLastNonConfigurationInstance();
			
		
		//ceci eviter l'activite de crache au dbut en cas de changement d'orientation
		//en creant ne weak reference des le depart
		mProgress = new ProgressTask(Start.this);
		//pour nos makers
		Utils.dictionary_maker =  new LinkedHashMap<Integer, Integer>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.graph, menu);
	    
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setSubtitle("subtitle");
			actionBar.setTitle("title"); 
			
	        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | 
	        		ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM );
	
	        actionBar.setHomeButtonEnabled(true);
		}
	    
	    return true;

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    switch (item.getItemId()) {
	    
		   
		    case android.R.id.home:
		    	  Intent intent = new Intent(this, Splash.class);
		    	  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	  startActivity(intent);
		    	  break; 
		    case R.id.action_edit:
		    	Toast.makeText(this, "action edit", Toast.LENGTH_SHORT).show();
			      break;
			      
		    default:
		      break;
	    }	    

	    return super.onOptionsItemSelected(item);

	}

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(list != null){//on peut le faire via OnResume
			Toast.makeText(this, "recuperation via getLastNonConfigurationInstance", Toast.LENGTH_SHORT).show();
			
			mProgress = (ProgressTask)list.get(0);// On lie l'activité à l'AsyncTask
			mProgress.link(this);
			bookmark = (Button)list.get(1);
			bookmark.setEnabled(true);
		}
	}

	
	@Override
	public List<?> onRetainNonConfigurationInstance () {
		
		List<Object> list = new ArrayList<Object>();
		list.add(mProgress);
		list.add(bookmark);
		return list;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
		
	// Met à jour l'avancement dans le textView
	public void updateProgress( ) {
		
			Log.v("double tread "," in bar progress");
		
						
			int progress = setprogress();
			double percent = (progress * 100)/Utils.MAX_SIZE;
			tv.setText( percent+"%");	
				
			if(progress >= Utils.MAX_SIZE){	
				
				Utils.fin = true;
				stop.setClickable(false);
				//Bitmap bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.stop4);
			    //recA.setImageBitmap(bImage);
			}
			
			
			
		}
	
	

	private int setprogress() {
		
		int time_curent = Utils.gettime();
		int result_time = MediaRecordAndPlaying.getTime_total() - time_curent;
		
		return Utils.MAX_SIZE - result_time;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
			case R.id.recordaudio: RecordingAudioService(true);
				break;
			case R.id.stop: RecordingAudioService(false);
				break;
			case R.id.mark: RecordingBooks_MakerService();
				break;
			case R.id.note:  RecordingNoteService();
				break;
			case R.id.capture: RecordingCaptureService();
				break;
		}
	}

	private void RecordingNoteService() {
		
		Toast.makeText(this, "note", Toast.LENGTH_SHORT).show();
		//sharedPreferences
	}

	private void RecordingCaptureService() {
		
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				Log.v("Student Note", "Captture");
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent,0);
			}
			
		}).start();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode==0){
			Log.v("Student Note", "retour Captture");
			//on recupere le bitmap et on le sauvegarde.
			//Bitmap img = (Bitmap)data.getExtras().get("data");
			
			
		}
	}

	private void RecordingBooks_MakerService() {
		Toast.makeText(this, "Book maker", Toast.LENGTH_SHORT).show();
		
		
		if(Utils.startMark){
			
			//Bitmap bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.stop4);
			bookmark.setBackgroundResource(R.drawable.ic_launcher);
		    //bookmark.setImageResource(R.drawable.stop4);
			Utils.starttime_mark = Utils.gettime();
			 
		    //ouverture d'une box quand il valide on change  pas besoin de cahngement d'etat 
		}
		else{
			
			//Bitmap bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.start2);
			bookmark.setBackgroundResource(R.drawable.index3);
			//bookmark.setImageResource(R.drawable.start2);
			Utils.finalTimeMark  =  Utils.gettime();
			
			Utils.dictionary_maker.put(Utils.starttime_mark, Utils.finalTimeMark);
		}
		
		Utils.setstartMark(!Utils.getstartMark());
	}
	
	private void RecordingAudioService(boolean startrecord) {
		
		
		
		 if(!startrecord){
			 
			 rec.stopMediaRecorder();
			 mProgress.cancel(!startrecord);
			 
		    tv.setText("click to start new recording");
		    //Bitmap bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.stop4);
		    //recA.setImageBitmap(bImage);
		    
		    stop.setClickable(startrecord);
			    
		 }else {
			  
			 
			    Utils.fin = false;
			    
			    stop.setClickable(startrecord);
			    //Bitmap bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.stop4);
			    //recA.setImageBitmap(bImage);
				bookmark.setEnabled(startrecord);
				
				// On recrée à chaque fois l'objet
				mProgress = new ProgressTask(Start.this);
				// On l'exécute
				mProgress.execute();
				Log.v("Student Note", "in recording audio");
				
		 }
		   
		
	}

	// L'AsyncTask est bien une classe interne statique
	static class ProgressTask extends AsyncTask<Void, Integer,Boolean> {
		
			// Référence faible à l'activité
			private WeakReference<Start> mActivity = null;
			
			public ProgressTask (Start pActivity) {
				link(pActivity);
			}
			@Override
			protected void onPreExecute () {
			// Au lancement, on affiche la boîte de dialogue
			if(mActivity.get() != null);
				//mActivity.get().showDialog(ID_DIALOG);
			}
			@Override
			protected void onPostExecute (Boolean result) {
				if (mActivity.get() != null) {
					if(result)Toast.makeText(mActivity.get(), "Recording terminé",Toast.LENGTH_SHORT).show();
					else
					Toast.makeText(mActivity.get(), "recording stopped",Toast.LENGTH_SHORT).show();
				}
				
				this.cancel(true);
			}
			@Override
			protected Boolean doInBackground (Void... arg0) {
			try {
				Log.v("Student Note", "in ondoInBackGround Task");
				
				            recording();
				            
					while(!Utils.fin) {							
						publishProgress();
						Thread.sleep(1000);
				     }
					
					   
				}catch(InterruptedException e) {
					e.printStackTrace();
					return false;
				}
			       return true;
			     
			}
			@Override
			protected void onProgressUpdate (Integer... prog) {
				// À chaque avancement du téléchargement, on met à jour la boîte de dialogue
				if (mActivity.get() != null)
				mActivity.get().updateProgress();
			}
			@Override
			protected void onCancelled () {
				if(mActivity.get() != null)
				Toast.makeText(mActivity.get(), "Annulation du téléchargement", Toast.LENGTH_SHORT).show();
			}
			public void link (Start pActivity) {//pour ne pas detruire l'activite en cas de changement 
				//on cree une reference faible
				mActivity = new WeakReference<Start>(pActivity);
			}
			
			public void recording() {
				
				Log.e("Start", "demarage de la class MediaRecordAndPlaying ");				 		
			 		//tout le travail
				mActivity.get().rec = new MediaRecordAndPlaying(mActivity.get());					 		
				mActivity.get().rec.recording();
			 		
			 		
			 
			}
	}
	


	

	

}


