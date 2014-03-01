package com.example.testfff;

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

	
	 private static MicroRecord microRecord;
	 
	 private static MediaRecorder recorder = null; 
	 
	 public static int opformats[] = {MediaRecorder.OutputFormat.MPEG_4,
									   MediaRecorder.OutputFormat.THREE_GPP};//different formats
	 public static int curformat = 0;
	 private static String[] file_extension = {".mp3",".3gpp"};//extensions correspondants
		
	 public static final String TAG = "SoundRecordingActivity";
	 
	 public static Long time = (long) 0;//curent time
	 public static Long time_total = (long) 0;//duree de l'enregistrement en reference avec l'instant ou on a clique
	 
	
		
	public MediaRecordAndPlaying(MicroRecord pmicroRecord) {
		microRecord = pmicroRecord;
		//fixe le volume au max
		microRecord.getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}
	
	/**
	 * methode qui permet de demarer l'enregistrement 
	 */
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
		File outFile = Utils.createAudioFile(file_extension[curformat]);//creation du fichier output
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
		
		getRecorder().start();//on start l'enregistrement 
		
		time = System.currentTimeMillis();
		 
		setTime_total(time+(3*60*1000));//1800000;
		Log.e(TAG, "start recording");
	}
	
  	/**
  	 * test si l'appareil a un microphone
  	 */
	protected static boolean hasMicrophone() {
		
		PackageManager pmanager = microRecord.getActivity().getPackageManager();
		return pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
	}
		
	
	public static MediaRecorder getRecorder() {
		return recorder;
	}

	public static void setRecorder(MediaRecorder record) {
		recorder = record;
	}

	public static Long getTime_total() {
		return time_total;
	}

	public static void setTime_total(long l) {
		MediaRecordAndPlaying.time_total = l;
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

	/**
	 * methode qui permet de stopper le recorder
	 */
	public static void stopMediaRecorder() {
		
		if( getRecorder() != null){
			 
			 MediaRecordAndPlaying.getRecorder().stop();
			 MediaRecordAndPlaying.getRecorder().reset();
			 MediaRecordAndPlaying.getRecorder().release();
			 MediaRecordAndPlaying.setRecorder(null);
			 
			}
		 
	}
	
}
