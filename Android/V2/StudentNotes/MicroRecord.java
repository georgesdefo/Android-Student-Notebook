package com.merlin.studentnotes;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class MicroRecord extends Fragment {
	
	public static String TAGF = "frg0";
	public ImageButton stop ,bookmark,capture,play;
	public TextView tv = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
	
		View vue = 	inflater.inflate(R.layout.micro_layout, container, false);	
		FragmentsSliderActivity ff= (FragmentsSliderActivity) this.getActivity();
		
		bookmark = (ImageButton)vue.findViewById(R.id.bmark);
		capture = (ImageButton)vue.findViewById(R.id.bcapture);
		play = (ImageButton)vue.findViewById(R.id.play);
		stop =  (ImageButton)vue.findViewById(R.id.stop);
		tv = (TextView)vue.findViewById(R.id.tv);  
        

		stop.setOnClickListener((OnClickListener) this.getActivity());
		play.setOnClickListener((OnClickListener) this.getActivity());
		bookmark.setOnClickListener((OnClickListener) this.getActivity());
		capture.setOnClickListener((OnClickListener) this.getActivity());
		
		tv.setText(FragmentsSliderActivity.LABEL);
		
		
		return vue;
			
	}

	
	public static MicroRecord newInstance(String chaine) {
		MicroRecord fragment = new MicroRecord();
		Bundle args = new Bundle();
		args.putString("KEY_STRING", chaine);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    Log.i("TAG", "onCreate(Bundle)");
	    super.onCreate(savedInstanceState);
	    setRetainInstance(true);
	  }	
	
}
	