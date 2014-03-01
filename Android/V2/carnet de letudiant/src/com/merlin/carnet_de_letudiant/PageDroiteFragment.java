package com.example.testfff;

import com.example.testfff.MainActivity.YourFragmentInterface;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PageDroiteFragment extends Fragment implements YourFragmentInterface{

	public static final String TAGG ="pdf";
	public static final int TAG = 1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View vue = inflater.inflate(R.layout.page_droite_layout, container, false);
		TextView textView = (TextView)vue.findViewById(R.id.textView1);
	    textView.setGravity(Gravity.CENTER);
		//Bundle args = getArguments();
		//textView.setText(args.getString("KEY_STRING"));
		textView.setText("ici sera affiche dans un format particulier les differents enregistrements de l'utilisateurs");
		return vue;
	}
	
	public static PageDroiteFragment newInstance(String chaine) {
		PageDroiteFragment fragment = new PageDroiteFragment();
		    Bundle args = new Bundle();
		    args.putString("KEY_STRING", chaine);
		    fragment.setArguments(args);
		   return fragment;
	}
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       setHasOptionsMenu(false);	
	}
	
	@Override
	public void fragmentBecameVisible() {
		// TODO Auto-generated method stub
		//Toast.makeText(this.getActivity(),"page detail", Toast.LENGTH_LONG).show();
		Utils.fixeur = 1;
	}
	@Override
	public void onStop(){
	    super.onStop();
	    //getActivity() = null;
	}
}
