package com.merlin.studentnotepre;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
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

//modification dans on resume si pas nouveau record j'affiche label
/**
 * 5.01.2014
 * android:minSdkVersion="8" android:targetSdkVersion="18"
 * @version 1.0
 * @author Merlin
 *
 */


//la list meme si pas null je pense qu'on doit verifier si un enregistrement a ete fait si non on instancie a nouveau le thread a revoir
public class MicroRecord extends Activity implements OnClickListener{
	
	public static String mark2 = null;//value de la marque choisit par l'user
	public boolean blocprogress = true;//acces du thread au textview pour ecriture //aussi si a false on ne peut rediriger vers une autre activite via le menubar
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
	    
	    case android.R.id.home:
	    		if(blocprogress)
	    			Changepage();
	    		else 
	    			Toast.makeText(getApplicationContext(), "Enregistrement en cours ...", Toast.LENGTH_SHORT).show();
	    			//
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
			
			if(Utils.startnewrecord){//si en cours d'enregistrement on reforme l'affichage
				
				
				long time_total = (Long) list.get(2);	 		
		 		long progress = Utils.MAX_SIZE -(time_total - Utils.gettime());	 		 
		 		long  percent = (progress * 100)/Utils.MAX_SIZE;
				String affiche =  LABEL+" "+((percent >=100.0)?100:percent)+"%";
				tv.setText(affiche);	
			}
			else 
			tv.setText(LABEL);
			
			
			Utils.alert = (Integer) list.get(3);//si vaut un on affiche le message juste en bas
			
			Utils.nameRecordFile = (String) list.get(4);
			Utils.precedentnamef = (Boolean) list.get(5);
			Utils.precedentnamefile = (String) list.get(6);//si on revient d'une onpause method faut se rappeller du nomprecedent
			blocprogress = (Boolean) list.get(7);
		}
		
		if(Utils.alert == 1){
			Toast.makeText(this,"Le titre de l'enregistrement est obligatoire pour de meilleur resultat", Toast.LENGTH_LONG).show();
			Utils.alert++;
		}
		
		
	}
	
	@Override
	public List<?> onRetainNonConfigurationInstance () {
		
		List<Object> list = new ArrayList<Object>();
		list.add(mProgress);
		list.add(Utils.startnewrecord);
		
		list.add(MediaRecordAndPlaying.getTime_total());
		list.add(Utils.alert);
		
		list.add(Utils.nameRecordFile);
		list.add(Utils.precedentnamef);
		list.add(Utils.precedentnamefile);	
		
		list.add(blocprogress);       
		return list;
	}

		
	
	/**
	 * Met à jour l'avancement dans le textView
	 */
	public void updateProgress( ) {
		
			Log.v("double tread "," in bar progress");
		
						
			Long progress = setprogress();
			Long percent = (progress * 100)/Utils.MAX_SIZE;
			
			if(!blocprogress)//si on a pas clique sur le bouton stop
			tv.setText( LABEL+" "+((percent >=100.0)?100:percent)+"%");	
				
			if(progress >= Utils.MAX_SIZE){
				Utils.startnewrecord = !Utils.startnewrecord;	//on le refixe a false			
				tv.setText(LABEL );				
				Utils.finalTime = Utils.gettime(); //on set le temps final
				UtilSaveAndGetFile.saveBookMark(this);//enregistrement general
				 
				Utils.fin = true;//fin de l'enregistrement on blocque l'action du thread dans son indoinbackground
			}
			
			
			
		}
		
	private Long setprogress() {
		
		Long time_curent = Utils.gettime();
		Long result_time = MediaRecordAndPlaying.getTime_total() - time_curent;
		
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
    /**
     * methode qui affiche un prompt en vue de recueiilir les info liees a l'enregistrement
     */
	private void Record() {	
		
		getPromptx().show();				
				
	}
	/**
	 * prompt qui permet a l'utilisateur d'entrer le titre, le type du fichier
     * elle va recuperer ces informations tout en fixant si oui ou non Utils.precedentnamef
	 * @return  alertDialog  AlertDialog
	 */
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
					public void onClick(DialogInterface dialog,int id) {
						
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
    /**
     * method qui permet d'enregistrer les notes de l'utilisateur via un prompt
     */
	private void RecordingNoteService(){
		
		if(Utils.startnewrecord)
			getPrompt().show();			

		else
			Toast.makeText(getApplicationContext(), "Lancer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
				
	}
   /**
    * method qui recupere le temps init de la note et surtout lance la methode setnotes()
    * @return  alertDialog  AlertDialog
    */
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
   /**
    * method qui permet la capture d'image
    */
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
	/**
	 * 	method qui fixe le format de la note et l'introduit dans la liste reservee a cet effet
	 *  puis procede a la sauvegarde
	 *  
	 * @param note  String
	 */
	public void setnotes(String note) {
		Utils.my_note = Utils.starttime_note+"##"+note;
		Utils.notes.add(Utils.my_note);		
		UtilSaveAndGetFile.SaveNotes(Utils.notes,this);
   }
   /**
    * method qui gere le necessaire pour les marquages
    */
	private void RecordingBooks_MakerService() {
		if(Utils.startnewrecord){
				maker();
		}
		else
			Toast.makeText(getApplicationContext(), "Lancer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
	}
	/**
	 * method qui recupere le temps init ,final, et la marque choisit par l'user
	 * elle fixe le formatage et sauvegarde dans un fichier
	 * @param mark2 
	 */
	private void maker() {
		
		if(Utils.startMark){
			Toast.makeText(getApplicationContext(), "Debut marquage", Toast.LENGTH_SHORT).show();		
			
			bookmark.setBackgroundResource(R.drawable.edit);			
		    box();
		}
		else{
			
			
			bookmark.setBackgroundResource(R.drawable.bookmark);
			//final time
			Utils.finalTimeMark  =  Utils.gettime();
			
			//quand on choisit directement Important dans la box on a un Strin null
			mark2 = ( mark2 == null || mark2.length() <= 1)?"Important":mark2;
			Log.v("MARKKK", mark2);
			//format
			Utils.my_mark = Utils.starttime_mark+"##"+Utils.finalTimeMark+"##"+mark2; 
			Log.v("MARKKKREGISTRY", Utils.my_mark);
			//ajout dans la liste
			Utils.marks.add(Utils.my_mark);//utilise dans le save general
			
			/* on ne va pa sauvegarder les marks car tres courtes
				//ajout dans la map selon la key - value
				Map<String, Long> map = new LinkedHashMap<String, Long>();
				map.put(mark2, Utils.finalTimeMark);
				//fin ajout dans la map final selon key value
				Utils.dictionary_maker.put(Utils.starttime_mark, map);
				//sauvegarde du fichier
				UtilSaveAndGetFile.SaveMarks(Utils.dictionary_maker,this);	
			*/
			Toast.makeText(getApplicationContext(), "Fin marquage", Toast.LENGTH_SHORT).show();
			mark2 = null;
		}
		   
		Utils.setstartMark(!Utils.getstartMark());
	}
	/**
	 * methode qui recupere le marque choisit par l'utilisateur dans le promp
	 * @param mark2 
	 */
	private void box() {
		final CharSequence[] items = {"Très important", "Important", "Interessant"};
		 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Remarque")
		    .setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialogInterface, int item) {
		        	mark2 = (String) items[item];
		            Toast.makeText(getApplicationContext(), mark2, Toast.LENGTH_SHORT).show();
		            		            		            
		        }
		    }).setCancelable(true)
		     .setPositiveButton("Yes", new Valid());
		builder.create().show();
	}
   /**
    * class qui ecoute le click sur le bouton yes de la promp quand l'utilsateur fixe un marquage
    * @author Merlin
    *
    */
	class Valid implements  android.content.DialogInterface.OnClickListener{
	   
	   @Override
		public void onClick(DialogInterface dialog, int which) {
		   Utils.starttime_mark= (long) 0;
			Utils.starttime_mark = Utils.gettime();
	   }
   }
   /**
    * lance et arrete l'enregistrement selon les valeur du parametre
    * 
    * @param startrecord boolean
    */
	private void RecordingAudioService(boolean startrecord) {
						
		 if(!startrecord ){
			 
			 if(Utils.startnewrecord){//si en cours d'enregistrement
				 
				 blocprogress = !blocprogress;// passe a true
				 Utils.startnewrecord = !Utils.startnewrecord;//passe a false;
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
			 
			 if(!Utils.startnewrecord){//si pas en cours d'enregistrement
				 	
				 	Record();
				 	blocprogress = !blocprogress;//passe a false
				 	
				 		Utils.startnewrecord = !Utils.startnewrecord;	//passe a true	 
					    Utils.fin = false;	//permet le deroulement du while dans le ondoinbackground(Thread)				    		    							
						
						tv.setText(LABEL);
						mProgress = new ProgressTask(MicroRecord.this);
						// On l'exécute
						mProgress.execute();
						Log.v("Student Note", "in recording audio");
						Utils.starttime = Utils.gettime();//on fixe le temps initial
			
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
				            
					while(!Utils.fin) {	//si pas fin => debut de l'enregistrement						
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
				Log.v("TASK"," dans on progress ");
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
	/**
	 * method qui va creer le fichier image et le passer a l'intent(ForResult) pour la capture image 
	 * tout en recuperant le temps initial 
	 */
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
			case RESULT_OK: Utils.timefinalcapture = Utils.gettime(); //temps final
							Utils.my_capture = Utils.timeinitcapture+"##"+Utils.timefinalcapture+"##"+Utils.nameImage;//format
							Utils.captures.add(Utils.my_capture);//ajout dans l liste
				break;
			}
			
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {//si bouton retour
        	
        	if(blocprogress){//si pas en cours d'enregistrement car passe a false quand on lance un enregistrement
        		
        		 Log.d("TRHEAD STOPPED","back button pressed");
 	            if(mProgress != null && !mProgress.isCancelled()) {
 	            	
 	            	
 	            	
 	            	killthread();//on stop le thread
 	        		
 	                 Log.i("TRHEAD STOPPED", "termine");
 	                 
 	
 			    	  
 	            }
 	            
 	           
 	      	    Changepage();//on change pour une autre activite
 	      
 	      	 
         }	
    	  else{ 
    			Toast.makeText(getApplicationContext(), " Echec Enregistrement", Toast.LENGTH_LONG).show();
    			
    			 Log.d("TRHEAD STOPPED","back button pressed");
  	            if(mProgress != null && !mProgress.isCancelled()) {
  	            	
  	            	
  	            	
  	            	killThreadWithoutSave();//stop le thread
  	        		
  	                 Log.i("TRHEAD STOPPED", "termine");
  	                 
  	
  			    	  
  	            }
  	          Changepage();
    	  }
	           
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
		//on re initiale le necessaire en vue d'un retour dans la page
		 Utils.startnewrecord = false;
		 bookmark.setBackgroundResource(R.drawable.bookmark);
		 tv.setText(LABEL);	
		 Utils.precedentnamefile = null;
		 if(MediaRecordAndPlaying.getRecorder()!=null)
			 MediaRecordAndPlaying.stopMediaRecorder();//on arrete le recorder
		 
    	 mProgress.cancel(true);
		
	}
	private void killThreadWithoutSave() {
		 Utils.startnewrecord = false;
		 bookmark.setBackgroundResource(R.drawable.bookmark);
		 tv.setText(LABEL);	
		 Utils.precedentnamefile = null;		 
   	     mProgress.cancel(true);
		
	}
	
	
}
