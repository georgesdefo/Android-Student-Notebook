package com.merlin.studentnotepre;

import java.io.File;
import java.io.IOException;

import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.util.Log;



/**
 * @author Merlin
 *
 */
public class MediaRecordAndPlaying {

	 private static Start my_activity;
	 private static MicroRecord microRecord;
	 public static File file = null;
	 public static String cheminfinal;
	 
	 private static MediaRecorder recorder = null;
	 
	 public static int opformats[] = {MediaRecorder.OutputFormat.MPEG_4,
									   MediaRecorder.OutputFormat.THREE_GPP};
	 public static int curformat = 0;
	 private static String[] file_extension = {".mp3",".3gpp"};
		
	 public static final String TAG = "SoundRecordingActivity";
	 
	 public static int time = 0;
	 public static int time_total = 0;
	 
	public MediaRecordAndPlaying(Start activity) {
		my_activity = activity;
		// Set the hardware buttons to control the music
		my_activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}
		
	public MediaRecordAndPlaying(MicroRecord pmicroRecord) {
		microRecord = pmicroRecord;
		// Set the hardware buttons to control the music
		microRecord.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	public static void recording() {

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
		//getRecorder().setAudioEncoder(MediaRecorder.getAudioSourceMax());
		getRecorder().setMaxDuration(3*60*1000);//1800000);
		//pour une qualite max du song-----
		
		getRecorder().setAudioEncodingBitRate(16);//JUSQU'A 9600
		getRecorder().setAudioSamplingRate(44100);
		//-------
		File outFile = Utils.createAudioFile(file_extension[curformat]);//on re ecrit si on a le meme fichier
		Utils.nameFileAudio = outFile.getAbsolutePath();
		getRecorder().setOutputFile(Utils.nameFileAudio);
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
		 
		 setTime_total(time+(3*60*1000));//1800000;
		 Log.e(TAG, "start recording");
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
	
	@SuppressWarnings("unused")
	private static void setFormatButtonCaption() {
		/*on va ecrire ceci dans une ligne dufichier qui va save toutes les donnees
		my_activity.bformat.setText("format  (" + file_extension[curformat].substring(1) + ")");
		*/
	}
	
	public static MediaRecorder getRecorder() {
		return recorder;
	}

	public static void setRecorder(MediaRecorder record) {
		recorder = record;
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

	public static void stopMediaRecorder() {
		
		if( getRecorder() != null){
			 
			 MediaRecordAndPlaying.getRecorder().stop();
			 MediaRecordAndPlaying.getRecorder().reset();
			 MediaRecordAndPlaying.getRecorder().release();
			 MediaRecordAndPlaying.setRecorder(null);
			 
			}
		 
	}
	
}
