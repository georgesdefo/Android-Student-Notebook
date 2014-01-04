package com.merlin.studentnote;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
    
	private int pass = 0;
	private static boolean fin = false;
	private static boolean stopTask = false;
	
    //Taille maximale du téléchargement
	public final static int MAX_SIZE = 60000;//1800000;
	// Identifiant de la boîte de dialogue
	public final static int ID_DIALOG = 0;
	
	private ProgressTask mProgress = null;
	private TestTask task = null;
	List<?> list ;	
	
	private static MenuItem menuItem;
	public ImageView recA, bookmark,note,capture;
	public TextView tv = null;
	private Button stop = null;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.start);
		
		recA = (ImageView)findViewById(R.id.recordaudio);
		bookmark = (ImageView)findViewById(R.id.mark);
		note = (ImageView)findViewById(R.id.note);
		capture = (ImageView)findViewById(R.id.capture);
		
		tv = (TextView)findViewById(R.id.tv);
		stop = (Button)findViewById(R.id.stop);
		
		bookmark.setEnabled(false);
		
		recA.setOnClickListener(this);
		bookmark.setOnClickListener(this);
		note.setOnClickListener(this);
		capture.setOnClickListener(this);
		stop.setOnClickListener(this);
		
		tv.setVisibility(View.GONE);
		stop.setVisibility(View.GONE);
		
		// On recupère l'AsyncTask perdu dans le changement de configuration
		list = (List<?>) getLastNonConfigurationInstance();
			
		if(list != null){//on peut le faire via OnResume
			Toast.makeText(this, "recuperation via getLastNonConfigurationInstance", Toast.LENGTH_SHORT).show();
			
			mProgress = (ProgressTask)list.get(0);// On lie l'activité à l'AsyncTask
			task = (TestTask)list.get(1);
			mProgress.link(this);
		}
		//ceci eviter l'activite de crache au dbut en cas de changement d'orientation
		//en creant ne weak reference des le depart
		mProgress = new ProgressTask(Start.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.start, menu);
	    
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
	    
		    case R.id.menu_load:
		      menuItem = item;
		      menuItem.setActionView(R.layout.search);
		      menuItem.expandActionView();
		      task = new TestTask();
		      task.execute("test");

		      break;
		    case android.R.id.home:
		    	  Intent intent = new Intent(this, Splash.class);
		    	  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	  startActivity(intent);
		    	  break; 
		    case R.id.menu_settings:
		    	Toast.makeText(this, "Menu Item 2 selected", Toast.LENGTH_SHORT).show();
			      break;
		    case R.id.conf:
		    	Toast.makeText(this, "Menu Item 3 selected", Toast.LENGTH_SHORT).show();
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
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	@Override
	public List<?> onRetainNonConfigurationInstance () {
		
		List<Object> list = new ArrayList<Object>();
		list.add(mProgress);
		list.add(task);
		
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
			
			int time_curent = this.gettime();
			int result_time = MediaRecordAndPlaying.getTime_total() - time_curent;	
			int progress = MAX_SIZE - result_time;
			
			if(pass==0){
				
				recA.setEnabled(false);
				bookmark.setEnabled(true);
				tv.setVisibility(View.VISIBLE);
				stop.setVisibility(View.VISIBLE);
				pass++;
			}
			
			double percent = (progress * 100)/MAX_SIZE;
			this.tv.setText( percent+"%");	
				
			if(progress >= MAX_SIZE){	
				tv.setVisibility(View.GONE);
				stop.setVisibility(View.GONE);
				recA.setEnabled(true);
				bookmark.setEnabled(true);
				fin = true;
			}
			
			
			
		}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
			case R.id.recordaudio: RecordingAudioService(true);
				break;
			case R.id.mark: RecordingBooks_MakerService();
				break;
			case R.id.note:  RecordingNoteService();
				break;
			case R.id.capture: RecordingCaptureService();
				break;
			case R.id.stop: RecordingAudioService(false);
			break;
				
		}
	}

	private void RecordingNoteService() {
		
		Toast.makeText(this, "note", Toast.LENGTH_SHORT).show();
	}

	private void RecordingCaptureService() {
		Log.v("Student Note", "Captture");
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent,0);
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
	}
	
	private void RecordingAudioService(boolean b) {
		
		Log.v("Student Note", "in recording audio");
		stopTask = false;
		
		 if(mProgress!=null && mProgress.isCancelled()){
			  
		 
		    tv.setText("recording");
			tv.setVisibility(View.GONE);
			stop.setVisibility(View.GONE);
		 }
		 if(b==false){
			 mProgress.cancel(true);
			
			 if( MediaRecordAndPlaying.getRecorder() != null){
				 MediaRecordAndPlaying.getRecorder().stop();
				 MediaRecordAndPlaying.getRecorder().reset();
				 MediaRecordAndPlaying.getRecorder().release();
				 MediaRecordAndPlaying.setRecorder(null);
				}
			 
			    tv.setText("recording");
			    tv.setVisibility(View.GONE);
				stop.setVisibility(View.GONE);
				recA.setEnabled(true);
		 }else {
			 
				pass = 0;
				fin = false;
				// On recrée à chaque fois l'objet
				mProgress = new ProgressTask(Start.this);
				// On l'exécute
				mProgress.execute();
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
				            
					while(!fin) {							
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
			 		MediaRecordAndPlaying rec = new MediaRecordAndPlaying(mActivity.get());					 		
			 		rec.recording();
			 		
			 		
			 
			}
	}
	
	static class TestTask extends AsyncTask<String, Void, String> {
		
		@Override
	    protected String doInBackground(String... params) {
	      // Simulate something long running
	      try {
	        Thread.sleep(1000);
	      } catch (InterruptedException e) {
	        e.printStackTrace();
	      }
	      return null;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	      menuItem.collapseActionView();
	      menuItem.setActionView(null);
	    }
	    
	 }

	public int gettime(){
				return (int)System.currentTimeMillis();
			}

			

}


