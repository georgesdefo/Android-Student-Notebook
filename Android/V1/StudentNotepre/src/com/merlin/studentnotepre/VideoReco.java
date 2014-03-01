package com.merlin.studentnotepre;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoReco extends Activity implements OnClickListener, SurfaceHolder.Callback {

	public boolean blocprogress = true;
	public ProgressTask mProgress = null;
	public List<?> list = null;
	
	private static Button  bstart, bstop, bplay, bstopReview;
	private VideoView vv;
	public TextView tv;
	
	public Camera camera = null;
	public SurfaceHolder holder = null;
	public MediaRecorder recorder = null;
	MediaController mc = null;
	private String outputFileName ;
	//private List<Size> previewSizes ;
	boolean init = false;
	private boolean start = false;
	int time;
	private long time_total;
	static String  STRINGTOSAVE;
	static final String TAG = "VIDEO";	
	static int etape = 0;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		
		
		bstart = (Button)findViewById(R.id.startv);
		bstop = (Button)findViewById(R.id.stoptv);
		bplay = (Button)findViewById(R.id.startrev);
		bstopReview = (Button)findViewById(R.id.stoprev);
		vv = (VideoView)findViewById(R.id.vView);
		tv = (TextView) findViewById(R.id.textView1);
		tv.setText("start");
		
		bstart.setOnClickListener(this);
		bstop.setOnClickListener(this);
		bplay.setOnClickListener(this);
		bstopReview.setOnClickListener(this);
		
		// On recupère l'AsyncTask perdu dans le changement de configuration
		list = (List<?>) getLastNonConfigurationInstance();
			
		
		//ceci eviter l'activite de crache au dbut en cas de changement d'orientation
		//en creant ne weak reference des le depart
		if(list == null)
		new ProgressTask(VideoReco.this);
		

		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.videorecord, menu);
	    
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
	    	Changepage();
    	  break;		 
	     
	     default:
	          break;
	    }	    

	    return super.onOptionsItemSelected(item);

	}
	
	 public void reload() {
             
		    Intent intent = getIntent();		    
		    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		    finish();		    		    
		    startActivity(intent);
		    overridePendingTransition(0, 0);
		}
	@Override
	public void onClick(View v) {
		
		
		switch(v.getId()){
					
			case R.id.startv:  	
			if(start){
				
				if(init){
					stopRecording();
					stopPreview();
					reload();
										
				}else{
					etape = 1;
					init();
					startRecording();
					init = true;
					bstart.setEnabled(false); bstop.setEnabled(true);bstopReview.setEnabled(false);bplay.setEnabled(false);
				}

			}
				
				break;
			case R.id.stoptv: 	etape = 2;
				bstop.setEnabled(false);	bplay.setEnabled(true);	 bstart.setEnabled(true);		
				stopRecording();
				break;
			case R.id.startrev: etape = 3;
				bplay.setEnabled(false);	bstart.setEnabled(true);  bstopReview.setEnabled(true);	
				startPreview();
				break;
			case R.id.stoprev: etape = 4;
				bstopReview.setEnabled(false);	bplay.setEnabled(true);					
				stopPreview();
				break;
							
		}
	}
	
	@Override
    public void onResume(){
		
		Log.v("video", "in onResume");
		super.onResume();
		
		if(list != null){//on peut le faire via OnResume			
			mProgress = (ProgressTask)list.get(0);// On lie l'activité à l'AsyncTask
			mProgress.link(this);		
			//on doit sauvegarde les aspets des bouton aussi
			
			Utils.startnewrecord = (Boolean) list.get(1);
			
			if(Utils.startnewrecord){
				
				long time_total =  (Long) list.get(2);	 		
		 		long progress = 300000 -(time_total - Utils.gettime());	 		 
		 		long  percent = (progress * 100)/300000;
				String affiche =  ((percent >=100.0)?100:percent)+"%";
				tv.setText(affiche);	
			}else
			  tv.setText("start");
			
			switch(etape){
			case 1: bstart.setEnabled(false); bstop.setEnabled(true);bstopReview.setEnabled(false);bplay.setEnabled(false);
				break;
			case 2: bstop.setEnabled(false);	bplay.setEnabled(true);	 bstart.setEnabled(true);
				break;
			case 3: bplay.setEnabled(false);	bstart.setEnabled(true);  bstopReview.setEnabled(true);
				break;
			case 4: bstopReview.setEnabled(false);	bplay.setEnabled(true);	
				break;
			case 0:
					bstart.setEnabled(true);bstop.setEnabled(false);
					bplay.setEnabled(false);bstopReview.setEnabled(false);
					break;
					
			}
			
		}else{
			
			bstart.setEnabled(false);
			bstop.setEnabled(false);
			bplay.setEnabled(false);
			bstopReview.setEnabled(false);
					
			init = false;
					
			if(!initCamera()){
				finish();
			}
		}
		
   }

	@Override
	public List<?> onRetainNonConfigurationInstance () {
		
		List<Object> list = new ArrayList<Object>();
		list.add(mProgress);
		list.add(Utils.startnewrecord);
		list.add(getTime_total());
		
		list.add(etape);//les etats du bouton (enable)
			
		return list;
	}
   
	// Met à jour l'avancement dans le textView
	public void updateProgress( ) {
			
				Log.v("double tread "," in bar progress");
			
							
				long progress = setprogress();
				double percent = (progress * 100)/300000;
				
				if(!blocprogress)
					tv.setText( ((percent >=100.0)?100:percent)+"%");	
				
					
				if(progress >= Utils.MAX_SIZE){
					
					Utils.startnewrecord = !Utils.startnewrecord;				
					tv.setText("fin" );				
					
					//UtilSaveAndGetFile.saveBookMark(this);//enregistrement general
					
					Utils.fin = true;
				}
												
			}
			
	private long setprogress() {
			
		long time_curent = Utils.gettime();
		long result_time = getTime_total() -  time_curent  ;//getTime_total() - time_curent;
			
			return  300000 - result_time; //Utils.MAX_SIZE - result_time;
		}
		
	@SuppressWarnings("deprecation")
	private boolean initCamera() {
		
		if(camera != null){
			camera = null;
		}
		try {
			camera = Camera.open();
			//Camera.Parameters params = camera.getParameters();
			//previewSizes = params.getSupportedPreviewSizes();
			camera.lock();
			holder = vv.getHolder();
			holder.addCallback(this);
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			Log.v("video", "initialisation camera");
		} catch (Exception e) {
			Log.v("video", "initialisation impossible");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//probleme sur la previsualisation a revoir
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
		camera.setPreviewCallback(new PreviewCallback() {
            public void onPreviewFrame(byte[] _data, Camera _camera) {
                Log.d("onPreviewFrame-surfaceChanged",String.format("Got %d bytes of camera data", _data.length));
            }
        });
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		Log.v("video", "in surface created");
		
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			start  = true;
			bstart.setEnabled(true);
		} catch (Exception e) {
			Log.v("video", "in surface created");
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		
	} 
	
	public  void init() {
		
		if(this.recorder != null) return;
		
		outputFileName = Environment.getExternalStorageDirectory()+"/StudentNotes/Video/video.mp4";
		File file =  new File(outputFileName);
		if(file.exists())
			  file.delete();
		
		try {
			camera.stopPreview();
			camera.unlock();
			recorder = new MediaRecorder();
			recorder.setCamera(camera);
			
			recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			
			//tv.setText("size"+previewSizes.get(0).width+" ,  "+previewSizes.get(0).height);
						
			recorder.setVideoSize(640, 480);
						
			recorder.setVideoFrameRate(30);
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setMaxDuration(300000);//5min
			recorder.setOutputFile(outputFileName);
			
			recorder.prepare();
			Log.v("video", "Media recorder initialized");
			
		} catch (Exception e) {
			Log.v("video", "Media recorder fail to initialize");
			e.printStackTrace();
		}
		
		
	}
	
	public  void startRecording() {

		Utils.startnewrecord = !Utils.startnewrecord;
		Utils.fin = false;
		blocprogress = false;
		mProgress = new ProgressTask(this);
		mProgress.execute();
										
	}
	public void start(){
		
		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);
		recorder.start();
		
		time = (int)System.currentTimeMillis();		 
		setTime_total(time+(5*60*1000));//300000
		Log.e(TAG, "start recording");
		tv.setText("RECORDING");
		
	}
	public  long getTime_total() {
		return time_total;
	}

	public  void setTime_total(int time_total) {
		this.time_total = time_total;
	}
	
	public  void stopRecording() {
		
		Utils.startnewrecord = false;
		Utils.fin = true;
		blocprogress = true;
		tv.setText("new");
		
		mProgress.cancel(true);
		
		if(recorder !=null){
			recorder.setOnErrorListener(null);
			recorder.setOnInfoListener(null);
			try {
				recorder.stop();
			} catch (Exception e) {
				Log.v("video", "Media recorder fail to initialize");
				e.printStackTrace();
			}
			
			releaseCamera();
			Log.e(TAG, "stop recording");
			//tv.setText("RECORDING finish");
			releaseRecorder();
		}
		
	}
	
	private void releaseCamera() {
			if(camera != null){
				try {
					camera.reconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
				camera.release();
				camera = null;
				
			}
		}

	private void releaseRecorder() {
			if(recorder != null){
				
				recorder.release();
				recorder = null;
				
			}
			
		}

	private void startPreview() {
		
		tv.setText("playing video");
		  mc = new MediaController(this);
		 vv.setMediaController(mc);
		 vv.setVideoPath(outputFileName);
		 vv.start();
		 				
	}

	private void stopPreview() {

		tv.setText("...");
		vv.stopPlayback();
		mc = null;
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	
        	
        	Changepage();
        
	           
        }
        
        return super.onKeyDown(keyCode, event);
    }
	
	private void Changepage() {
		
		if(recorder!= null)
			vv.stopPlayback();
		
		 if(recorder != null)
			 stopRecording();
		 
         Intent intent = new Intent(this, Start.class);
         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
  	     finish();
  	     startActivity(intent);
  	    
	}

	static MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
	    @Override
	    public void onError(MediaRecorder mr, int what, int extra) {
	       // Toast.makeText(my_activity, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
	    }
	};

	static MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
	    @Override
	    public void onInfo(MediaRecorder mr, int what, int extra) {
	        //Toast.makeText(my_activity, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
	    }
	};

	
	// L'AsyncTask est bien une classe interne statique
		static class ProgressTask extends AsyncTask<Void, Integer,Boolean> {
			
				// Référence faible à l'activité
				private WeakReference<VideoReco> mActivity = null;
				
				public ProgressTask (VideoReco VRecord) {
					link(VRecord);
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
				public void link (VideoReco pActivity) {//pour ne pas detruire l'activite en cas de changement 
					//on cree une reference faible
					mActivity = new WeakReference<VideoReco>(pActivity);
				}
				
				public void recording() {
					
					Log.e("Start", "demarage de la class MediaRecordAndPlaying ");				 		
				 		//tout le travail
					//mActivity.get().rec = new MediaRecordAndPlaying(mActivity.get());					 		
					VideoRecording  vrecord = new VideoRecording(mActivity.get());
							vrecord.start();
				 		
				 		
				 
				}
		}
	
}
