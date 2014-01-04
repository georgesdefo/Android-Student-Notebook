package com.merlin.studentnote;

import java.io.File;
import java.io.IOException;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;


/**
 * @author Merlin
 *
 */
public class MediaRecordAndPlaying {

	 private static Start my_activity;
	 public static File file = null;
	 public static String cheminfinal;
	 
	 private static MediaRecorder recorder = null;
	 
	 private static int opformats[] = {MediaRecorder.OutputFormat.MPEG_4,
									   MediaRecorder.OutputFormat.THREE_GPP};
	 private static int curformat = 0;
	 private static String[] file_extension = {".mp4",".3gpp"};
		
	 private static final String TAG = "SoundRecordingActivity";
	 
	 private static int time = 0;
	 private static int time_total = 0;
	 
	public MediaRecordAndPlaying(Start activity) {
		my_activity = activity;
		// Set the hardware buttons to control the music
		my_activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}
		
	public  void recording() {

		MediaRecord();
			
	}
		
	private static void MediaRecord(){
		
		
			Log.e(TAG, " in start recordind");
			start_rec();
	
		
	}
	
	private static void start_rec() {
		
		if(getRecorder() != null){
			
			try {
				getRecorder().release();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		
		setRecorder(new MediaRecorder());
		getRecorder().setAudioSource(MediaRecorder.AudioSource.MIC);
		getRecorder().setOutputFormat(opformats[curformat]);
		getRecorder().setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		getRecorder().setMaxDuration(60000);//1800000);
		//pour une qualite max du song-----
		//recorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
		//recorder.setAudioEncodingBitRate(16);//JUSQU'A 9600
		//recorder.setAudioSamplingRate(44100);
		//-------
		File outFile = new File(getFilePath());//on re ecrit si on a le meme fichier
		if(outFile.exists())
			outFile.delete();
		
		getRecorder().setOutputFile(getFilePath());
		
		
		getRecorder().setOnErrorListener(errorListener);
	    getRecorder().setOnInfoListener(infoListener);
	    
		try {
			 getRecorder().prepare();
			 
		 } catch (IllegalStateException e) {
			Log.e(TAG, "sdcard access error");
	        e.printStackTrace();
	    } catch (IOException e) {
	    	Log.e(TAG, "sdcard access error");
	        e.printStackTrace();
	    }
		
		getRecorder().start();
		
		 time = (int)System.currentTimeMillis();
		 
		 setTime_total(time+60000);//1800000;
		 Log.e(TAG, "start recording");
	}
   
	
	private static String getFilePath(){
		
		String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
           Log.e(TAG, "SD Card is not mounted.  It is " + state + ".");
           return null;
        }
        
		String FilePath = Environment.getExternalStorageDirectory().getPath();
		file = new File(FilePath,"StudentNotes");
		
		if(!file.exists())//creation pour la premiere fois si pas existant
			file.mkdir();
		 
		
		
		cheminfinal = file.getAbsolutePath()+"/audio"+file_extension[curformat];//audio sera remplacer le nom du fichier
		 
		return cheminfinal;
	}
    		
	protected static boolean hasMicrophone() {
		PackageManager pmanager = my_activity.getPackageManager();
		return pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
	}
	
	protected void  displayFormatDialog() {
		/*
		AlertDialog.Builder build = new AlertDialog.Builder(my_activity);
		String formats[] = {"MP4","3GPP"};
		build.setTitle("Select format file").setSingleChoiceItems(formats, curformat, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				curformat = which;
				setFormatButtonCaption();
	            dialog.dismiss();
			}
		}).show();
		*/
		
	}
	
	private static void setFormatButtonCaption() {
		/*on va ecrire ceci dans une ligne dufichier qui va save toutes les donnees
		my_activity.bformat.setText("format  (" + file_extension[curformat].substring(1) + ")");
		*/
	}
	
	public static MediaRecorder getRecorder() {
		return recorder;
	}

	public static void setRecorder(MediaRecorder recorder) {
		MediaRecordAndPlaying.recorder = recorder;
	}

	public static int getTime_total() {
		return time_total;
	}

	public static void setTime_total(int time_total) {
		MediaRecordAndPlaying.time_total = time_total;
	}

	private static MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
	    @Override
	    public void onError(MediaRecorder mr, int what, int extra) {
	       // Toast.makeText(my_activity, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
	    }
	};

	private static MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
	    @Override
	    public void onInfo(MediaRecorder mr, int what, int extra) {
	        //Toast.makeText(my_activity, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
	    }
	};
	
}
