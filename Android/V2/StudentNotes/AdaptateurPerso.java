package com.merlin.studentnotes;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AdaptateurPerso extends BaseAdapter {
	
	private List<Integer> mModel = new ArrayList<Integer>();
	private Context mContext;
	
	public AdaptateurPerso(Context context) {
		mContext = context;
	}
	@Override
	public int getCount() {
		return mModel.size();
	}
	@Override
	public Integer getItem(int position) {
		return mModel.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		VuePersonnalisee v = null;
		// Notre vue n'a pas encore été construite, nous le faisons
		if (convertView == null) {
			v = new VuePersonnalisee(mContext);
		} // Notre vue peut être récupérée, nous le faisons
		else {
			
			v = (VuePersonnalisee) convertView;
		}
		v.bind(getItem(position));
		return v;
	}
	public void bind(List<Integer> model) {
		mModel = model;
	}
}
