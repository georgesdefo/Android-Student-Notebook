package com.merlin.studentnotes;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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

public class FragmentsSliderActivity extends FragmentActivity implements OnClickListener, ActionBar.TabListener{
    
	public ProgressTask mProgress = null;
	public List<?> list =null;	
	
	public Button recA ;
	
	int REQUEST_TAKE_PHOTO = 0;
	public static String LABEL = "Enregistrement:";
	
	int progress =0;
	double percent =0;
	String affiche = null;
	
	private PagerAdapter mPagerAdapter;
	List<Fragment> fragments;
	public String fragment ;
	ViewPager pager;
	MicroRecord micro = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.viewpage);

		// Création de la liste de Fragments que fera défiler le PagerAdapter
		 fragments = new Vector<Fragment>();

		// Ajout des Fragments dans la liste
		fragments.add(android.support.v4.app.Fragment.instantiate(this,MicroRecord.class.getName()));
		fragments.add(android.support.v4.app.Fragment.instantiate(this,PageDroiteFragment.class.getName()));

		// Création de l'adapter qui s'occupera de l'affichage de la liste de
		// Fragments
		this.mPagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);

		pager = (ViewPager) super.findViewById(R.id.viewpager);
		// Affectation de l'adapter au ViewPager
		pager.setAdapter(this.mPagerAdapter);
		
     /*
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		
		for (int i = 0; i < mPagerAdapter.getCount(); i++) {
			//actionBar.addTab( actionBar.newTab().setText(mPagerAdapter.getPageTitle(i)) );
		}
		*/
	    //au retour
		
		
	    if(savedInstanceState!=null){
	   	 	
	    	 int time_total = savedInstanceState.getInt("time");
	    	 	 		
	 		 progress = Utils.MAX_SIZE -(time_total - Utils.gettime());	 		 
			 percent = (progress * 100)/Utils.MAX_SIZE;
			 affiche =  LABEL+" "+((percent >=100.0)?100:percent)+"%";
			 
			 ((TextView) fragments.get(0).getView().findViewById(R.id.tv)).setText(affiche);
			 
			 Utils.startnewrecord = savedInstanceState.getBoolean("startnewrecord");
			
			//------// 
			 
			// micro = (MicroRecord)getSupportFragmentManager().findFragmentByTag(MicroRecord.TAGF);//1
			 micro = (MicroRecord)getSupportFragmentManager().getFragment(savedInstanceState, 
					                                            MicroRecord.class.getName());// ou 2
			
	    }
	    else{
	    	if(micro==null){
	         	micro = new MicroRecord();
			
	    	}
	    	mProgress = new ProgressTask(this);
	    }
	    
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {//garder la page en cours si rotation

		super.onSaveInstanceState(outState);
		outState.putBoolean("startnewrecord", Utils.startnewrecord);
		outState.putInt("time", MediaRecordAndPlaying.getTime_total());
		getSupportFragmentManager().putFragment(outState, MicroRecord.class.getName(), micro);//2
		
		
	}	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.microrecord, menu);
	    
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setSubtitle("Student Notes");
			actionBar.setTitle("Starting Taking Notes"); 
			
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
	    	  Intent intent = new Intent(this, Start.class);
	    	  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	  startActivity(intent);
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
	public void onResume() {
		super.onResume();
		/*
		if(list != null){//on peut le faire via OnResume
			
			
			mProgress = (ProgressTask)list.get(0);// On lie l'activité à l'AsyncTask
			mProgress.link(this);
			Utils.startnewrecord = (Boolean) list.get(1);
			Toast.makeText(this, Utils.startnewrecord+"", Toast.LENGTH_SHORT).show();
		}
		 
		*/
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
	
	//-------------------------------------------------------------//
	
	// Met à jour l'avancement dans le textView
	public void updateProgress( ) {
		
			Log.v("double tread "," in bar progress");
		
						
			 progress = setprogress();
			
			 percent = (progress * 100)/Utils.MAX_SIZE;
			 affiche =  LABEL+" "+((percent >=100.0)?100:percent)+"%";
			
			 ((TextView) fragments.get(0).getView().findViewById(R.id.tv)).setText(affiche);	
				
			if(progress >= Utils.MAX_SIZE){
				Utils.startnewrecord = !Utils.startnewrecord;				
				((TextView) fragments.get(0).getView().findViewById(R.id.tv)).setText(LABEL );				
				Utils.finalTime = Utils.gettime(); 
				UtilSaveAndGetFile.saveBookMark(this);//enregistrement general
				((TextView) fragments.get(0).getView().findViewById(R.id.tv)).setText(LABEL); 
				Utils.fin = true;
			}
			
			
			
		}
		
	private int setprogress() {
		
		int time_curent = Utils.gettime();
		int result_time = MediaRecordAndPlaying.getTime_total() - time_curent;
		
		return Utils.MAX_SIZE - result_time;
	}

	//-------------------------------------------------------------//

	private void Record() {	
		
		getPromptx().show();		 				
		
		
	}

	private AlertDialog getPromptx() {
		
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.promptbook, null);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);				
		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);
								
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						Utils.nameRecordFile = userInput.getText().toString();
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
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								Utils.starttime_note = Utils.gettime();
								setnotes(userInput.getText().toString());
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
			
			((ImageButton) fragments.get(0).getView().findViewById(R.id.bmark)).setBackgroundResource(R.drawable.edit);
		   //depart time
			Utils.starttime_mark = Utils.gettime();
			
		    box();
		}
		else{
			
			
			((ImageButton) fragments.get(0).getView().findViewById(R.id.bmark)).setBackgroundResource(R.drawable.bookmark);
			//final time
			Utils.finalTimeMark  =  Utils.gettime();
			
			Toast.makeText(getApplicationContext(), "Fin marquage", Toast.LENGTH_SHORT).show();
			Utils.my_mark =( Utils.my_mark == null || Utils.my_mark.length() < 1)?"Important":Utils.my_mark;
			Log.v("MARKKK", Utils.my_mark);
			String markfinal = 	Utils.starttime_mark+"##"+Utils.finalTimeMark+"##"+Utils.my_mark; 
			Utils.marks.add(markfinal);
			//---
			Map<String, Integer> map = new LinkedHashMap<String, Integer>();
			map.put(Utils.my_mark, Utils.finalTimeMark);
			Utils.dictionary_maker.put(Utils.starttime_mark, map);
			UtilSaveAndGetFile.SaveMarks(Utils.dictionary_maker,this);			
		}
		   
		Utils.setstartMark(!Utils.getstartMark());
	}
	
	private void box() {
		final CharSequence[] items = {"Très important", "Important", "Interessant"};
		 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Marqueur")
		    .setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialogInterface, int item) {
		        	
		            Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
		            		            		            
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
				 
				 ((TextView) fragments.get(0).getView().findViewById(R.id.tv)).setText(LABEL);	
				 MediaRecordAndPlaying.stopMediaRecorder();
				 Utils.finalTime = Utils.gettime();
				 
				 mProgress.cancel(!startrecord);
			     		     
			     UtilSaveAndGetFile.saveBookMark(this);//enregistrement general
				 
			
			     ((TextView) fragments.get(0).getView().findViewById(R.id.tv)).setText(LABEL);
			 }
			 else
				 Toast.makeText(getApplicationContext(), "Lancer un nouvel enregistrement audio", Toast.LENGTH_SHORT).show();
								 
		 }else {
			 
			 if(!Utils.startnewrecord){
				 	Record();
				 	
				    Utils.startnewrecord = !Utils.startnewrecord;		 
				    Utils.fin = false;					    		    							
					
				    ((TextView) fragments.get(0).getView().findViewById(R.id.tv)).setText(LABEL);
					mProgress = new ProgressTask(this);
					// On l'exécute
					mProgress.execute();
					Log.v("Student Note", "in recording audio");
					Utils.starttime = Utils.gettime();
					//Toast.makeText(this,Utils.starttime+"", Toast.LENGTH_SHORT).show();
				
			 }
			 else
				 Toast.makeText(getApplicationContext(), "Enregistrement en cours ...", Toast.LENGTH_SHORT).show();
		 }
		   		
	}

	// L'AsyncTask est bien une classe interne statique
	static class ProgressTask extends AsyncTask<Void, Integer,Boolean> {
		
			// Référence faible à l'activité
			private WeakReference<FragmentsSliderActivity> mActivity = null;
			
			public ProgressTask (FragmentsSliderActivity fragmentsSliderActivity) {
				link(fragmentsSliderActivity);
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
			public void link (FragmentsSliderActivity fragmentsSliderActivity) {//pour ne pas detruire l'activite en cas de changement 
				//on cree une reference faible
				mActivity = new WeakReference<FragmentsSliderActivity>(fragmentsSliderActivity);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		pager.setCurrentItem(tab.getPosition());
		
	}
	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
    
}

