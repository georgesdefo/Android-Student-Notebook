package com.merlin.studentnotes;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VuePersonnalisee extends LinearLayout {
	
		private TextView mTextView;
		
		public VuePersonnalisee(Context context, AttributeSet attrs, int defStyle) {
		 super(context, attrs, defStyle);
		  init();
		}
		public VuePersonnalisee(Context context, AttributeSet attrs) {
		 super(context, attrs);
		 init();
		}
		public  VuePersonnalisee(Context context) {
		 super(context);
		 init();
		}
		private void init() {
			inflate(getContext(), R.layout.perso, this);
			mTextView = (TextView) findViewById(R.id.textView);
		}
		public void bind(int text) {
			mTextView.setText(getResources().getString(text));
		}
		

}
