package com.merlin.studentnotepre;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class VideoRecorder implements OnClickListener, 
OnInfoListener, OnErrorListener{


	
	VideoRecorder(){
		
	}
	
	public static void init() {
		
		if(Utils.recorder != null) return;
		
		Utils.outputFileName = Utils.createVideoFile().getAbsolutePath();		
		Utils.recentvideoOutput = Utils.outputFileName;
		
		try {
			Utils.camera.stopPreview();
			Utils.camera.unlock();
			Utils.recorder = new MediaRecorder();
			Utils.recorder.setCamera(Utils.camera);
			
			Utils.recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			Utils.recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			Utils.recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			Utils.recorder.setVideoSize(640, 480);			
			Utils.recorder.setVideoFrameRate(30);
			Utils.recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			//recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			Utils.recorder.setMaxDuration(300000);//5min
			Utils.recorder.setOutputFile(Utils.outputFileName);
			
			Utils.recorder.prepare();
			Log.v("video", "Media recorder initialized");
			
		} catch (Exception e) {
			Log.v("video", "Media recorder fail to initialize");
			e.printStackTrace();
		}
		
		startrecording();
	}
	
	public static void startrecording() {
		Utils.recorder.setOnErrorListener(errorListener);
		Utils.recorder.setOnInfoListener(infoListener);		
		//on doit envoyer un message pour le start enregistrement
		Utils.recorder.start();						
	}
	
	public static void stoprecording() {
		if(Utils.recorder !=null){
			Utils.recorder.setOnErrorListener(null);
			Utils.recorder.setOnInfoListener(null);
			try {
				Utils.recorder.stop();
			} catch (Exception e) {
				Log.v("video", "Media recorder fail to initialize");
				e.printStackTrace();
			}
			
			releaseCamera();
			releaseRecorder();		
		}
		
		
	}
	
	public static void releaseCamera() {
		if(Utils.camera != null){
			try {
				Utils.camera.reconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Utils.camera.release();
			Utils.camera = null;
			
		}
	}
	
	public static void releaseRecorder() {
		if(Utils.recorder != null){
			
			Utils.recorder.release();
			Utils.recorder = null;
			
		}
		
	}
	
	
	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
