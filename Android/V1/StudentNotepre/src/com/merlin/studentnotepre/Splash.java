package com.merlin.studentnotepre;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;

public class Splash extends Activity {
    
	MediaPlayer ourSong;
	public static boolean stop = false;
	public int count = 0;
	public int tour = 0;
	static String TAG = "Splash";
	static int MAX_SIZE = 100;
	ProgressTask mProgress;
	private ProgressDialog mDialog = null;
	List<?> list ;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		
		
		ourSong= MediaPlayer.create( Splash.this,R.raw.zoo );
		ourSong.start();
		
		list = (List<?>) getLastNonConfigurationInstance();
		
		if(list != null){//on peut le faire via OnResume
			//Toast.makeText(this, "recuperation via getLastNonConfigurationInstance", Toast.LENGTH_SHORT).show();
			mDialog = (ProgressDialog)list.get(0);// On lie la progressbar
			mProgress = (ProgressTask)list.get(1);// On lie l'activit� � l'AsyncTask
			mProgress.link(this);
		}
		
		 go();
				
	}
	
	
	
	
	public void go() {
		
		// On recr�e � chaque fois l'objet
	     mProgress = new ProgressTask(this);
		// On l'ex�cute
		mProgress.execute();
	}
	
	@Override
	protected Dialog onCreateDialog (int id) {
		mDialog = new ProgressDialog(this);
		mDialog.setCancelable(false);
		/*mDialog.setOnCancelListener(new
		DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
			      mProgress.cancel(true);
			}
		});*/
		mDialog.setTitle("T�l�chargement en cours");
		mDialog.setMessage("Patientez un peu...");
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setMax(MAX_SIZE);
		return mDialog;
	}
	
	@Override
	public List<?> onRetainNonConfigurationInstance () {
		
		List<Object> list = new ArrayList<Object>();
		list.add(mDialog);
		list.add(mProgress);
		
		return list;
	}
	
	// L'AsyncTask est bien une classe interne statique
	static class ProgressTask extends AsyncTask<Void, Integer,Boolean> {
				
					// R�f�rence faible � l'activit�
					private WeakReference<Splash> mActivity = null;
					// Progression du t�l�chargement
					private int mProgression = 0;
					
					public ProgressTask (Splash pActivity) {
						link(pActivity);
					}
					@SuppressWarnings("deprecation")
					@Override
					protected void onPreExecute () {
					// Au lancement, on affiche la bo�te de dialogue
					if(mActivity.get() != null)
						mActivity.get().showDialog(0);
					}
					@Override
					protected void onPostExecute (Boolean result) {
						if (mActivity.get() != null) {
							if(result)
								Toast.makeText(mActivity.get(), " ",Toast.LENGTH_SHORT).show();
							else
							Log.e(TAG,"Echec du t�l�chargement");
						}
						this.cancel(true);
					}
					@Override
					protected Boolean doInBackground (Void... arg0) {
					try {
							while(download() <= MAX_SIZE) {
								
								publishProgress(mProgression);
								Thread.sleep(1000);
						     }
							return true;
						}catch(InterruptedException e) {
							e.printStackTrace();
							return false;
						}
					}
					
					@Override
					protected void onProgressUpdate (Integer... prog) {
						// � chaque avancement du t�l�chargement, on met � jour la bo�te de dialogue
						if (mActivity.get() != null)
							mActivity.get().updateProgress(prog[0]);
					}
					@Override
					protected void onCancelled () {
						if(mActivity.get() != null)
							Log.e(TAG,"THREAD STOPPE");
					}
					public void link (Splash pActivity) {//pour ne pas detruire l'activite en cas de changement 
						//on cree une reference faible
						mActivity = new WeakReference<Splash>(pActivity);
					}
					private int download() {
						
						mProgression += 20;
						
						return mProgression;
					}
					
			}
	@Override
	protected void onPause() {		
		super.onPause();
	
		//finish(); car le thread peut mettre long et une mise en veille du telephone va provoquer la fin de l'application
	}
	public void updateProgress(Integer progress) {
		
			mDialog.setProgress(progress);
			if(progress >= 100){
				mDialog.setProgress(100);                   
			  mProgress.cancel(true); 
				ourSong.release();
				ourSong = null;
				
				//Toast.makeText(this, "GOOOOOO",Toast.LENGTH_SHORT).show();
				mDialog.dismiss();	
			  finish();//on sort de la pile car on ne souhaite plus retourner sous l'action du bouton retour
			 Intent intent = new Intent(this, Start.class);
			 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 startActivity(intent);
			}
		
	}

	

}
