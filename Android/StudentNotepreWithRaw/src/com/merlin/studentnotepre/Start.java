package com.merlin.studentnotepre;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
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
import android.widget.Toast;

public class Start extends Activity implements OnClickListener{
    
	MediaRecordAndPlaying rec;
	private List<?> list ;	
	
	public Button recA, bookmark,note,capture;
	
	int REQUEST_TAKE_PHOTO = 0;
	public static final int TAGACTIVITY = 0;
	
	@SuppressWarnings("deprecation")
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
		
		
		// On recupère l'AsyncTask perdu dans le changement de configuration
		list = (List<?>) getLastNonConfigurationInstance();
			
		
		//ceci eviter l'activite de crache au dbut en cas de changement d'orientation
		//en creant ne weak reference des le depart
		new Link(Start.this);
		//pour nos makers
		Utils.dictionary_maker =  new LinkedHashMap<Integer, Map<String, Integer>>();
		Utils.notes = new ArrayList<String>();
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
	    	  
		    case R.id.action_edit: RecordingNoteService();
		    	break;
		     case R.id.action_audio: finish();//on sort de la pile
		    	 Intent intentaudio = new Intent(Start.this, MicroRecord.class);
             						this.startActivity(intentaudio);
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
			//Toast.makeText(this, "recuperation via getLastNonConfigurationInstance", Toast.LENGTH_SHORT).show();			
			//bookmark = (Button)list.get(0);
			//bookmark.setEnabled(true);
		}
	}
	
	@Override
	public List<?> onRetainNonConfigurationInstance () {
		
		List<Object> list = new ArrayList<Object>();
		//list.add(bookmark);
		return list;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
			case R.id.recordaudio: finish();//on sort de la pile
				Intent intent = new Intent(this, MicroRecord.class);
			                       this.startActivity(intent);
				break;
			case R.id.note:  	RecordingNoteService();
				break;
			case R.id.capture: 	RecordingCaptureService();
				break;
			case R.id.mark: 	
				Toast.makeText(getApplicationContext(),"Commencer un nouvel enregistrement", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private void RecordingNoteService(){
		
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(Start.this);
		View promptsView = li.inflate(R.layout.prompts, null);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Start.this);
		
		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						
						Start.this.setnotes(userInput.getText().toString());
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

		// show it
		alertDialog.show();
		
		
		
	}

   private void RecordingCaptureService() {
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				Log.v("Student Note", "Captture");
				dispatchTakePictureIntent();
			}
			
		}).start();
		
	}
		
	public void setnotes(String note) {
		Utils.my_note = note;
		Utils.notes.add(Utils.my_note);				
		SaveNotes(Utils.notes);
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
    	
	private void dispatchTakePictureIntent() {
		
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	   
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        
	        File photoFile = Utils.createImageFile();
	              
	       
	         //Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	public void SaveNotes(final List<String> list) {
		
		final File file = Utils.createTextFile("note_");	
	            write(file,list);	       	       	       
   }
	
	private void write(File file,List<String> my_list) {
		
		PrintWriter pw = null;
		
        try {
        	 pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
        	 for (Object value:my_list)
        	    {
        		
        		 		pw.println(value.toString());
        		 		 Log.v("writting", value.toString());
        	    }     
        	Toast.makeText(getApplicationContext(),"save complete", Toast.LENGTH_SHORT).show(); 
            Log.d("File Write is success","fine");
        } catch (Exception e) {
            Log.d("Error in File write: ", ""+e.getMessage());
        } finally {
            if (pw != null) {
            	pw = null;
            }
        }
	}
			
   public void SaveMarks(final Map<Integer,Map<String,Integer>> map) {
		
		
	        final File file = Utils.createTextFile("mark_");	      
					Log.v("writting", "starting write");
					writeMarks(file,map);	       
   }	 
		
	private void writeMarks(File file, Map<Integer, Map<String, Integer>> map) {
		
		List<String> liste = new ArrayList<String>();
		liste = getList(map,liste);
		
            PrintWriter pw = null;		
        try {
        	 pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
        	 for (Object value:liste){
        		  pw.println(value.toString());

        	 }
        	 Toast.makeText(getApplicationContext(),"save complete", Toast.LENGTH_SHORT).show();
            Log.d("File Write is success","fine");
        } catch (Exception e) {
            Log.d("Error in File write: ", ""+e.getMessage());
        } finally {
            if (pw != null) {
            	pw = null;
            }
        }
   }

	private List<String> getList(Map<Integer, Map<String, Integer>> map, List<String> liste) {
		
		String finalstring = null;
		
		for (Integer key : map.keySet())
		{
			finalstring += key+"_";
		    Map<String, Integer> map2 = map.get(key);
		    
		    for (String innerKey : map2.keySet())
		    {
		    	finalstring += innerKey+"_"+map2.get(innerKey); 
		    	liste.add(finalstring);
		    }
		}
		
		return liste;
	}

	 
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
           finish();//on sort car 1 dans la pile.
           //on a ete recre par microrecord activity
        }
        
        return super.onKeyDown(keyCode, event);
    }

    

}

