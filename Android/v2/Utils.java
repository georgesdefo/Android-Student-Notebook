package com.merlin.studentnote;

import java.util.LinkedHashMap;

public class Utils {

	    //Taille maximale du téléchargement
		public final static int MAX_SIZE = 60000;//1800000;
		public static boolean fin = false;
		public static boolean startMark = true;
		public static int starttime_mark,finalTimeMark =0;
		public static LinkedHashMap<Integer, Integer> dictionary_maker;
		
		
	public static int gettime(){
		return (int)System.currentTimeMillis();
	}
	
	public static boolean setstartMark(boolean bol){
		
		return startMark = bol;
	}
	public static boolean getstartMark(){
		
		return startMark;
	}

}
