package com.example.testfff;

import android.util.Log;

public class VideoRecording {

	VideoReco videoReco;
	
	public VideoRecording(VideoReco videoReco) {
		this.videoReco = videoReco;
	}

	public void start() {
		
		videoReco.recorder.setOnErrorListener(VideoReco.errorListener);
		videoReco.recorder.setOnInfoListener(VideoReco.infoListener);
		videoReco.recorder.start();
		
		videoReco.time = System.currentTimeMillis();		 
		videoReco.setTime_total(videoReco.time+(5*60*1000));//300000
		Log.e(VideoReco.TAG, "start recording");
		
	}

	

}
