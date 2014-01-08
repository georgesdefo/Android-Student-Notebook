package com.merlin.studentnotes;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class PageDroiteFragment extends ListFragment {
	
	public static final String TAGF = "ListViewFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.page_droite_layout, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		AdaptateurPerso aa1 = new AdaptateurPerso(this.getActivity());
		
		setListAdapter(aa1);
	}
	
	public static PageDroiteFragment newInstance(String chaine) {
		PageDroiteFragment fragment = new PageDroiteFragment();
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
