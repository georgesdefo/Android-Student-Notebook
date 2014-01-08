package com.merlin.studentnotepre;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MicroRecord extends Activity implements OnClickListener{
	
	//MediaRecordAndPlaying rec;
	
	private ProgressTask mProgress = null;
	private List<?> list = null;	
	
	public Button recA ;
	public TextView tv = null;
	public ImageButton stop ,bookmark,capture,play;
		
	int REQUEST_TAKE_PHOTO = 0;
	private static String LABEL = "Enregistrement:";
	public static  int TAG = 1;
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.micro_layout);
				
		bookmark = (ImageButton)findViewById(R.id.bmark);
		capture = (ImageButton)findViewById(R.id.bcapture);
		play = (ImageButton)findViewById(R.id.play);
		stop =  (ImageButton)findViewById(R.id.stop);
		tv = (TextView)findViewById(R.id.tv); 
			
		stop.setOnClickListener(this);
		play.setOnClickListener(this);
		bookmark.setOnClickListener(this);
		capture.setOnClickListener(this);
	
		tv.setText(LABEL);
		
		// On recupère l'AsyncTask perdu dans le changement de configuration
		list = (List<?>) getLastNonConfigurationInstance();
			
		
		//ceci eviter l'activite de crache au dbut en cas de changement d'orientation
		//en creant ne weak reference des le depart
		if(list == null)
		mProgress = new ProgressTask(MicroRecord.this);
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.microrecord, menu);
	    
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setSubtitle("Student Notes");
			actionBar.setTitle("Enregistrement"); 
			
	        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | 
	        		ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM );
	
	        actionBar.setHomeButtonEnabled(true);
		}
	    
	    return true;

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    switch (item.getItemId()) {
	    
	    case android.R.id.home:Changepage();
	    	  break;		 
	     case R.id.action_video: RecordingCaptureService();
	    	  break;
	     case R.id.action_new:  RecordingBooks_MakerService();
   	  		  break;
	     case R.id.action_edit: RecordingNoteService();
	    	  break;
	     default:
	          break;
	    }	    

	    return super.onOptionsItemSelected(item);

	}
		
	
	@Override	
	protected void onResume() {
		super.onResume();
		
		if(list != null){//on peut le faire via OnResume
						
			mProgress = (ProgressTask)list.get(0);// On lie l'activité à l'AsyncTask
			mProgress.link(this);
			
			Utils.startnewrecord = (Boolean) list.get(1);
			
			if(Utils.startnewrecord){
				int time_total = ((Integer) list.get(2)).intValue();	 		
		 		int progress = Utils.MAX_SIZE -(time_total - Utils.gettime());	 		 
				double  percent = (progress * 100)/Utils.MAX_SIZE;
				String affiche =  LABEL+" "+((percent >=100.0)?100:percent)+"%";
				tv.setText(affiche);	
			}
			tv.setText(LABEL);
			
			Utils.alert = (Integer) list.get(3);
		}
		
		if(Utils.alert == 1){
			Toast.makeText(this,"Le titre de l'enregistrement est obligatoire pour de meilleur resultat", Toast.LENGTH_LONG).show();
			Utils.alert++;
		}
		Utils.precedentnamefile = null;
		
	}
	
	@Override
	public List<?> onRetainNonConfigurationInstance () {
		
		List<Object> list = new ArrayList<Object>();
		list.add(mProgress);
		list.add(Utils.startnewrecord);
		list.add(MediaRecordAndPlaying.getTime_total());
		list.add(Utils.alert);
		return list;
	}

		
	// Met à jour l'avancement dans le textView
	public void updateProgress( ) {
		
			Log.v("double tread "," in bar progress");
		
						
			int progress = setprogress();
			double percent = (progress * 100)/Utils.MAX_SIZE;
			tv.setText( LABEL+" "+((percent >=100.0)?100:percent)+"%");	
				
			if(progress >= Utils.MAX_SIZE){
				Utils.startnewrecord = !Utils.startnewrecord;				
				tv.setText(LABEL );				
				Utils.finalTime = Utils.gettime(); 
				UtilSaveAndGetFile.saveBookMark(this);//enregistrement general
				tv.setText(LABEL); 
				Utils.fin = true;
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
		
			case R.id.play: 	RecordingAudioService(true);
				break;
			case R.id.stop:   	
								RecordingAudioService(false);
				break;
			case R.id.bmark: 	RecordingBooks_MakerService();
				break;
			case R.id.note:  	RecordingNoteService();
				break;
			case R.id.bcapture: RecordingCaptureService();
				break;
		}
	}

	private void Record() {	
		
		getPromptx().show();				
		
		 
		
	}

	private AlertDialog getPromptx() {
		
		String formats[] = {"MP3","3GPP"};
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.promptbook, null);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);				
		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);
								
		alertDialogBuilder
		.setTitle("Titre de l'enregistrement")
		.setCancelable(false)
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						 Utils.nameRecordFile = userInput.getText().toString();
						 Utils.nameRecordFile = ( Utils.nameRecordFile!=null && Utils.nameRecordFile.length() == 0)?
								 "notitle":Utils.nameRecordFile;
						 
						 if(Utils.nameRecordFile == "notitle")
							 Utils.precedentnamef = true;
						else{
							 Utils.precedentnamef = false;
							 
						}
						//Toast.makeText(getApplicationContext(), Utils.nameRecordFile, Toast.LENGTH_SHORT).show();
					}
				})	     
	    .setSingleChoiceItems(formats, MediaRecordAndPlaying.curformat, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MediaRecordAndPlaying.curformat = which;
				
			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		
		return alertDialog;
	}

	private void RecordingNoteService(){
		
		if(Utils.startnewrecord)
			getPrompt().show();			

		else
			Toast.makeText(getApplicationContext(), "Lancer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
				
	}

   private AlertDialog getPrompt() {
	// get prompts.xml view
				LayoutInflater li = LayoutInflater.from(this);
				View promptsView = li.inflate(R.layout.prompts, null);
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);				
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);
		
				final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
										
				alertDialogBuilder
				.setTitle("Nouvelle note")
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								Utils.starttime_note = Utils.gettime();
								MicroRecord.this.setnotes(userInput.getText().toString());
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								
								dialog.cancel();
							}
						});
		
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				
				return alertDialog;
	}

   private void RecordingCaptureService() {
	
	   if(Utils.startnewrecord){
		   	   
			new Thread(new Runnable(){
	
				@Override
				public void run() {					
					Log.v("Student Note", "Captture");
					dispatchTakePictureIntent();
				}
				
			}).start();
		
       }
	   else
		   Toast.makeText(getApplicationContext(), "Lancer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
		
	}
		
	public void setnotes(String note) {
		Utils.my_note = Utils.starttime_note+"##"+note;
		Utils.notes.add(Utils.my_note);		
		UtilSaveAndGetFile.SaveNotes(Utils.notes,this);
   }

	private void RecordingBooks_MakerService() {
		if(Utils.startnewrecord){
				maker();
		}
		else
			Toast.makeText(getApplicationContext(), "Lancer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
	}
	
	private void maker() {
		
		if(Utils.startMark){
			Toast.makeText(getApplicationContext(), "Debut marquage", Toast.LENGTH_SHORT).show();		
			
			bookmark.setBackgroundResource(R.drawable.edit);
		   //depart time
			Utils.starttime_mark = Utils.gettime();
			
		    box();
		}
		else{
			
			
			bookmark.setBackgroundResource(R.drawable.bookmark);
			//final time
			Utils.finalTimeMark  =  Utils.gettime();
			
			
			Utils.my_mark =( Utils.my_mark == null || Utils.my_mark.length() < 1)?"Important":Utils.my_mark;
			Log.v("MARKKK", Utils.my_mark);
			String markfinal = 	Utils.starttime_mark+"##"+Utils.finalTimeMark+"##"+Utils.my_mark; 
			Utils.marks.add(markfinal);
			//---
			Map<String, Integer> map = new LinkedHashMap<String, Integer>();
			map.put(Utils.my_mark, Utils.finalTimeMark);
			Utils.dictionary_maker.put(Utils.starttime_mark, map);
			UtilSaveAndGetFile.SaveMarks(Utils.dictionary_maker,this);	
			
			Toast.makeText(getApplicationContext(), "Fin marquage", Toast.LENGTH_SHORT).show();
		}
		   
		Utils.setstartMark(!Utils.getstartMark());
	}
	
	private void box() {
		final CharSequence[] items = {"Très important", "Important", "Interessant"};
		 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Remarque")
		    .setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialogInterface, int item) {
		        	Utils.my_mark = (String) items[item];
		            Toast.makeText(getApplicationContext(), Utils.my_mark, Toast.LENGTH_SHORT).show();
		            		            		            
		        }
		    }).setCancelable(true)
		     .setPositiveButton("Yes", new Valid());
		builder.create().show();
	}
   
	class Valid implements  android.content.DialogInterface.OnClickListener{
	   
	   @Override
		public void onClick(DialogInterface dialog, int which) {
			
	   }
   }
   
	private void RecordingAudioService(boolean startrecord) {
						
		 if(!startrecord ){
			 if(Utils.startnewrecord){
				 
				 Utils.startnewrecord = false;
				 bookmark.setBackgroundResource(R.drawable.bookmark);
				 tv.setText(LABEL);	
				 MediaRecordAndPlaying.stopMediaRecorder();
				 Utils.finalTime = Utils.gettime();
				 
				 mProgress.cancel(!startrecord);
				 mProgress = null;
				 tv.setText(LABEL);
			     UtilSaveAndGetFile.saveBookMark(this);//enregistrement general
			     tv.setText(LABEL);
			
			     
			 }
			 else
				 Toast.makeText(getApplicationContext(), "Lancer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
								 
		 }else {
			 
			 if(!Utils.startnewrecord){
				 	
				 	Record();
				 		
				 		Utils.startnewrecord = !Utils.startnewrecord;		 
					    Utils.fin = false;					    		    							
						
						tv.setText(LABEL);
						mProgress = new ProgressTask(MicroRecord.this);
						// On l'exécute
						mProgress.execute();
						Log.v("Student Note", "in recording audio");
						Utils.starttime = Utils.gettime();
			
			 }
			 else
				 Toast.makeText(getApplicationContext(), "Enregistrement en cours ...", Toast.LENGTH_SHORT).show();
		 }
		   		
	}

	// L'AsyncTask est bien une classe interne statique
	static class ProgressTask extends AsyncTask<Void, Integer,Boolean> {
		
			// Référence faible à l'activité
			private WeakReference<MicroRecord> mActivity = null;
			
			public ProgressTask (MicroRecord microRecord) {
				link(microRecord);
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
					if(result)
						//Toast.makeText(mActivity.get(), "Enregistrement terminé",Toast.LENGTH_SHORT).show();
						Log.v("TASK"," Enregistrement terminé ");
					else
						//Toast.makeText(mActivity.get(), "Enregistrement annuler",Toast.LENGTH_SHORT).show();
						Log.v("TASK"," Enregistrement annuler ");
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
				//Toast.makeText(mActivity.get(), " Fin Enregistrement ", Toast.LENGTH_SHORT).show();
					Log.v("TASK"," Fin Enregistrement ");
			}
			public void link (MicroRecord pActivity) {//pour ne pas detruire l'activite en cas de changement 
				//on cree une reference faible
				mActivity = new WeakReference<MicroRecord>(pActivity);
			}
			
			public void recording() {
				
				Log.e("Start", "demarage de la class MediaRecordAndPlaying ");				 		
			 		//tout le travail
				//mActivity.get().rec = new MediaRecordAndPlaying(mActivity.get());					 		
				MediaRecordAndPlaying.recording();
			 		
			 		
			 
			}
	}
	 
	private void dispatchTakePictureIntent() {
		
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	   
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        
	        File photoFile = Utils.createImageFile();
	        Utils.nameImage = photoFile.getName();
	        
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	        	 Utils.timeinitcapture = Utils.gettime();
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	            
	        }
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==0){
			
			switch(resultCode){
			case RESULT_OK: Utils.timefinalcapture = Utils.gettime(); 
							Utils.my_capture = Utils.timeinitcapture+"##"+Utils.timefinalcapture+"##"+Utils.nameImage;
							Utils.captures.add(Utils.my_capture);
				break;
			}
			
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	
	            Log.d("TRHEAD STOPPED","back button pressed");
	            if(mProgress != null && !mProgress.isCancelled()) {
	            	
	            	
	            	
	            	killthread();
	        		
	                 Log.i("TRHEAD STOPPED", "termine");
	                 
	
			    	  
	            }
	            
	           
	      	    Changepage();
	      	//on sort et on va dans la precedente page.
	        //un intent car on es le seul dans la pile
        }
            
        return super.onKeyDown(keyCode, event);
    }

	private void Changepage() {
		 Utils.alert = 1;
		  
         Intent intent = new Intent(this, Start.class);
   	     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
   	     finish();
   	     startActivity(intent);
   	    
	}

	private void killthread() {
		 Utils.startnewrecord = false;
		 bookmark.setBackgroundResource(R.drawable.bookmark);
		 tv.setText(LABEL);	
		 Utils.precedentnamefile = null;
		 MediaRecordAndPlaying.stopMediaRecorder();
		 
    	 mProgress.cancel(true);
		
	}
	
	/*
	public void startpreview() {
		
		 MediaController mc = new MediaController(this);
		 vview.setMediaController(mc);
		 vview.setVideoPath(Utils.recentvideoOutput);
		 vview.start();
	
	}
	public void stoppreview() {
	
		vview.stopPlayback();
	}
	
	public void StartVideoRecord(){
		if(!initCamera()){ 
			Toast.makeText(this, "echec initialisation", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	public boolean initCamera() {
		
		if(Utils.camera != null){
			Utils.camera = null;
		}
		try {
			Utils.camera = Camera.open();
			Camera.Parameters params = Utils.camera.getParameters();
			Utils.previewSizes = params.getSupportedPreviewSizes();
			Utils.camera.lock();
			Utils.holder = vview.getHolder();
			Utils.holder.addCallback(this);
			Utils.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			Log.v("video", "initialisation camera");
		} catch (Exception e) {
			Log.v("video", "initialisation impossible");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		Log.v("video", "in surface created");
		
		try {
			Utils.camera.setPreviewDisplay(Utils.holder);
			Utils.camera.startPreview();
		} catch (Exception e) {
			Log.v("video", "in surface created");
			e.printStackTrace();
		}
				
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
*/
}
