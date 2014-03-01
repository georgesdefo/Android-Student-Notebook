package com.example.testfff;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import com.example.testfff.MainActivity.YourFragmentInterface;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
public class MicroRecord extends Fragment implements OnClickListener, YourFragmentInterface{
	
	public static String mark2 = null;//value de la marque choisit par l'user
	public static boolean blocprogress = true;//acces du thread au textview pour ecriture //aussi si a false on ne peut rediriger vers une autre activite via le menubar
	public static ProgressTask mProgress = null;
	public static List<?> list = null;	
	
	public Button recA ;
	public  TextView tv = null;
	public ImageButton stop;
	public static ImageButton bookmark;
	public ImageButton capture;
	public ImageButton play;
		
	int REQUEST_TAKE_PHOTO = 0;
	public static String LABEL = "Enregistrement:";
	public   int TAG = 0;
	View vue ;	
	public static final String TAGG ="microrecord";
	
	public static boolean menuIsInflated = false;//gere les intance de oncreatemenu
	public static int count = 0;//si on change l'orientation  => 1 et on suprime le menu car on le veut plus afficher dans l'autre fragment
	//android:configChanges="orientation" tjours pour la multi intance du menu
	
	
	public static MicroRecord newInstance(String chaine) {
		MicroRecord fragment = new MicroRecord();
		    Bundle args = new Bundle();
		    args.putString("KEY_STRING", chaine);
		    fragment.setArguments(args);
		   return fragment;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		 
		vue = inflater.inflate(R.layout.micro_layout, container, false);
		
		return vue;			
	}
	
	private void initControls() {
		
		bookmark = (ImageButton)vue.findViewById(R.id.bmark);
		capture = (ImageButton)vue.findViewById(R.id.bcapture);
		play = (ImageButton)vue.findViewById(R.id.play);
		stop =  (ImageButton)vue.findViewById(R.id.stop);
		tv = (TextView)vue.findViewById(R.id.tv); 
			
		stop.setOnClickListener(this);
		play.setOnClickListener(this);
		bookmark.setOnClickListener(this);
		capture.setOnClickListener(this);
	
		tv.setText(LABEL);
		
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setRetainInstance(true);
        setHasOptionsMenu(true);
	}
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        initControls();
        
        if (savedInstanceState != null) {
            // Restore last state for checked position.
        	
        	//on a pas recuperer le stack
        	blocprogress			 = (Boolean) savedInstanceState.getBoolean("blocprogress");//pour gerer l'affiche dans le on progress
    		Utils.startnewrecord = (Boolean) savedInstanceState.getBoolean("startnewrecord");
			
			if(Utils.startnewrecord){//si en cours d'enregistrement on reforme l'affichage
				blocprogress = false;
				long time_total = savedInstanceState.getLong("Time_total");	 		
				long progress = Utils.MAX_SIZE -(time_total - Utils.gettime());	 		 
				long  percent = (progress * 100)/Utils.MAX_SIZE;
				String affiche =  LABEL+" "+((percent >=100.0)?100:percent)+"%";
				tv.setText(affiche);	
			}
			else {
				blocprogress = true;
				tv.setText(LABEL);
			
			}
			
			
			Utils.alert = (Integer) savedInstanceState.getInt("alert");//si vaut un on affiche le message juste en bas
			
			Utils.nameRecordFile = (String) savedInstanceState.getString("nameRecordFile");
			Utils.precedentnamef = (Boolean) savedInstanceState.getBoolean("precedentnamef");
			Utils.precedentnamefile = (String) savedInstanceState.getString("precedentnamefile");//si on revient d'une onpause method faut se rappeller du nomprecedent
			
			menuIsInflated = savedInstanceState.getBoolean("menuIsInflated");
			count = savedInstanceState.getInt("count");
			
			
    		//ceci eviter l'activite de crache au dbut en cas de changement d'orientation
    		//en creant ne weak reference des le depart		
    		MicroRecord.mProgress.link(this);
			
        }else{
        	
        	if(Utils.alert == 1){
    			Toast.makeText(getActivity(),"Le titre de l'enregistrement est obligatoire pour de meilleur resultat", Toast.LENGTH_LONG).show();
    			Utils.alert++;
    		}
        	
        	
    		//ceci eviter l'activite de crache au dbut en cas de changement d'orientation
    		//en creant ne weak reference des le depart		
    		MicroRecord.mProgress = new ProgressTask(this);
    		MicroRecord.mProgress.link(this);
    		
    		
    		
    		
			
        }
       
	}
	@Override
	public void onStart() {
		super.onStart();
							
	}
				
	@Override
	public void onResume() {
		super.onResume();
		
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		outState.putBoolean("startnewrecord", Utils.startnewrecord);
		outState.putLong("Time_total", MediaRecordAndPlaying.getTime_total());
		outState.putInt("alert", Utils.alert);
		outState.putString("nameRecordFile", Utils.nameRecordFile);
		outState.putBoolean("precedentnamef", Utils.precedentnamef);
		outState.putString("precedentnamefile", Utils.precedentnamefile);
		outState.putBoolean("blocprogress", blocprogress);
		outState.putBoolean("menuIsInflated", menuIsInflated);
		outState.putInt("count", count);
	}
	@Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		
		
		if (!menuIsInflated) {//cette condition n'a pas d'effet
			
/*j'ai du adapte une solution tout en esperant k'il y est un moyen plus facil 
 * ceci a ete fixe suite a l'analyse des different evenements lies au passage du fragment micro a page droite*/	
			
			if(menu.hasVisibleItems()){//quand on rote l'ecran(car lorsqu'on rote j'ai constate que menu.hasVisibleItems() etait > 0 d'ou cette condition)
				//Toast.makeText(getActivity(),"A", Toast.LENGTH_SHORT).show();
				Log.d("OnCreateOptionsMenu", menu.size()+" items");//on a 3 items
				menu.clear();//on efface car il ya plus d'un menu qui s'affiche ou n*x items dans mon cas x=2.
				inflater.inflate(R.menu.microrecord, menu);//on recree un ouveau
				super.onCreateOptionsMenu(menu, inflater);
				Log.d("OnCreateOptionsMenu", "OnCreateOptionsMenu");
				return;
			}
			else{//pour la premiere fois qu'on entre dans ce fragment ouu activite car ce fragment est le root
				if(count == 0){//premiere entree dans ce fragment
					//Toast.makeText(getActivity(),"B", Toast.LENGTH_SHORT).show();
				inflater.inflate(R.menu.microrecord, menu);
				count=1;//on va le memoriser
				}
				else if(count == 1){//si on va dans pagedroit et on revient grace au slide ou tabmenu
					//Toast.makeText(getActivity(),"C", Toast.LENGTH_SHORT).show();
					if(this.isVisible())//si le fragment microrecord est visible ou on utilise la condition sur la vue vue.getVisibility() == View.VISIBLE
						inflater.inflate(R.menu.microrecord, menu);
					else	//si non on est dans la page droite
						menu.clear();	//on efface le surplus(item appartenant a micro) cree				
				}
				Log.d("OnCreateOptionsMenu", "OnCreateOptionsMenu");
				super.onCreateOptionsMenu(menu, inflater);
				return;
			}
			//menuIsInflated = true;
			
			//Toast.makeText(getActivity(),"TRUE", Toast.LENGTH_SHORT).show();
			
		}
		
	    
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
	    
		    case android.R.id.home:
		    		if(MicroRecord.blocprogress)
		    			Changepage();
		    		else 
		    			Toast.makeText(this.getActivity(), "Enregistrement en cours ...", Toast.LENGTH_SHORT).show();
		    			
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
	/**
	 * Met à jour l'avancement dans le textView
	 */
	public void updateProgress( ) {
		
			Log.v("double tread "," in bar progress");
		
						
			Long progress = setprogress();
			Long percent = (progress * 100)/Utils.MAX_SIZE;
			Log.v("TRHEADBLOQUE",(blocprogress)?"true":"false");
			if(!blocprogress)//si on a pas clique sur stop
			tv.setText( LABEL+" "+((percent >=100.0)?100:percent)+"%");	
				
			if(progress >= Utils.MAX_SIZE){
				Utils.startnewrecord = false;	//on le refixe a false		
				tv.setText(LABEL );				
				Utils.finalTime = Utils.gettime(); //on set le temps final
				UtilSaveAndGetFile.saveBookMark(this);//enregistrement general
				 
				Utils.fin = true;//fin de l'enregistrement on blocque l'action du thread dans son indoinbackground
				mProgress.cancel(!Utils.startnewrecord);
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
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.promptbook, null);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());				
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
	public void RecordingNoteService(){
		
		if(Utils.startnewrecord)
			getPrompt().show();			

		else
			Toast.makeText(getActivity(), "Lancer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
				
	}
   /**
    * method qui recupere le temps init de la note et surtout lance la methode setnotes()
    * @return  alertDialog  AlertDialog
    */
   private AlertDialog getPrompt() {
	// get prompts.xml view
				LayoutInflater li = LayoutInflater.from(getActivity());
				View promptsView = li.inflate(R.layout.prompts, null);
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());				
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
   public void RecordingCaptureService() {
	
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
		   Toast.makeText(getActivity(), "Lancer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
		
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
	public void RecordingBooks_MakerService() {
		if(Utils.startnewrecord){
				maker();
		}
		else
			Toast.makeText(getActivity(), "Lancer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
	}
	/**
	 * method qui recupere le temps init ,final, et la marque choisit par l'user
	 * elle fixe le formatage et sauvegarde dans un fichier
	 */
	public void maker() {
		
		if(Utils.startMark){
			Toast.makeText(getActivity(), "Debut marquage", Toast.LENGTH_SHORT).show();		
			
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
			
			/* 
			//ajout dans la map selon la key - value
			Map<String, Long> map = new LinkedHashMap<String, Long>();
			map.put(Utils.my_mark, Utils.finalTimeMark);
			//fin ajout dans la map final selon key value
			Utils.dictionary_maker.put(Utils.starttime_mark, map);
			//sauvegarde du fichier
			UtilSaveAndGetFile.SaveMarks(Utils.dictionary_maker,this);	
			
			*/
			Toast.makeText(getActivity(), "Fin marquage", Toast.LENGTH_SHORT).show();
			mark2 = null;
		}
		   
		Utils.setstartMark(!Utils.getstartMark());
	}
	/**
	 * methode qui recupere le marque choisit par l'utilisateur dans le promp
	 */
	public void box() {
		final CharSequence[] items = {"Très important", "Important", "Interessant"};
		 
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Remarque")
		    .setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialogInterface, int item) {
		        	mark2 = (String) items[item];
		            Toast.makeText(getActivity(), mark2, Toast.LENGTH_SHORT).show();
		            		            		            
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
		   //depart time
		   Utils.starttime_mark = (long) 0;
			Utils.starttime_mark = Utils.gettime();
	   }
   }
   /**
    * lance et arrete l'enregistrement selon les valeur du parametre
    * 
    * @param startrecord boolean
    */
	public void RecordingAudioService(boolean startrecord) {
						
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
				 Toast.makeText(getActivity(), "Lancer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
								 
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
				 Toast.makeText(getActivity(), "Enregistrement en cours ...", Toast.LENGTH_SHORT).show();
		 }
		   		
	}
    
	// L'AsyncTask est bien une classe interne statique
	static class ProgressTask extends AsyncTask<Void, Integer,Boolean> {
		
			// Référence faible à l'activité
			private WeakReference<MicroRecord> mActivity = null;
			
			public ProgressTask (MicroRecord microRecord) {
				link(microRecord);
			}
			public ProgressTask(MainActivity mainActivity) {
				// TODO Auto-generated constructor stub
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
				if(!this.isCancelled())
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
	   
	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
	        
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==0){
			
			switch(resultCode){
			case MainActivity.RESULT_OK://???????????????
							Utils.timefinalcapture = Utils.gettime(); //temps final
							Utils.my_capture = Utils.timeinitcapture+"##"+Utils.timefinalcapture+"##"+Utils.nameImage;//format
							Utils.captures.add(Utils.my_capture);//ajout dans l liste
				break;
			}
			
		}
	}	
	@Override
	public void fragmentBecameVisible() {
		// TODO Auto-generated method stub
		//Toast.makeText(this.getActivity(),"root page", Toast.LENGTH_SHORT).show();
		Utils.fixeur = 0;
	}
	
	private void Changepage() {
		 Utils.alert = 1;
		 menuIsInflated= false;
         Intent intent = new Intent(this.getActivity(), Start.class);
  	     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
  	     getActivity().finish();
  	     startActivity(intent);
  	    
	}
	@Override
	public void onStop(){
	    super.onStop();
	    //getActivity() = null;
	}
	
}
