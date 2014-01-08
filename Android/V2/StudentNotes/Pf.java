package com.merlin.studentnotes;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Pf extends PreferenceFragment {
	
	public static final String TAG = "MyPreferenceFragment";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//addPreferencesFromResource(R.xml.preferences);
	}
}
