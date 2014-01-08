package com.merlin.studentnotepre;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;

public class Utils {

    //Taille maximale du téléchargement
	public static int alert = 1;
	public final static int MAX_SIZE = 3*60*1000;//1800000;
	public static boolean fin = false;
	public static boolean startMark = true;
	public static int starttime_mark,finalTimeMark =0;
	public static int starttime,finalTime =0;
	public static int starttime_note =0;
	public static int timeinitcapture,timefinalcapture =0;
	public static LinkedHashMap<Integer, Map<String,Integer> > dictionary_maker = new LinkedHashMap<Integer, Map<String, Integer>>();
	public static ArrayList<String> marks = new ArrayList<String>();
	public static ArrayList<String> notes = new ArrayList<String>();
	public static ArrayList<String> captures = new ArrayList<String>();
	public static String my_note = null;
	public static String my_mark = null;
	public static String my_capture = null;
	public static String nameImage = null;
	
	public static String nameFileAudio = null;
	public static String nameRecordFile = null;
	public static int once =1;//premier passage dans onRsume
	
	public static Boolean startnewrecord = false;
	
	public static String recentvideoOutput =  null;
	public static Camera camera = null;
	public static MediaRecorder recorder = null;
	public static String outputFileName = null;
	public static List<Size> previewSizes ;
	public static SurfaceHolder holder = null;
	public static boolean ok = true;
	public static boolean okfin = false;
	
	public static String precedentnamefile=null;
	public static boolean precedentnamef ;
	
	public static int gettime(){
		return (int)System.currentTimeMillis();
	}
	
	public static boolean setstartMark(boolean bol){
		
		return startMark = bol;
	}
	public static boolean getstartMark(){
		
		return startMark;
	}
	public static File createBookMarkFile(String nameRecordFile2) { //pour bookmark on return le fichier book_xxx_.txt
	    		
		String  cheminfinal =  getFilePath("BookMark",".txt",nameRecordFile2);
		Log.v("CREATION",cheminfinal);
		File outFile = null;
	    try {
	    	 outFile = new File(cheminfinal);
		} catch (Exception e) {
			Log.e("CREATION", "echec");
		}
	   
		if(outFile.exists())
			outFile.delete();//on re ecrit si on a le meme fichier
	    
		Log.v("Name", outFile.getName());
		
	    return outFile;
	}
	
	public static File createTextFile(String nameFile) { 
	    
	    String  cheminfinal =  getFilePath("Doc",".txt",nameFile);
	    File outFile = null;
	    try {
	    	 outFile = new File(cheminfinal);
		} catch (Exception e) {
			// TODO: handle exception
		}
	   
		if(outFile.exists())
			outFile.delete();//on re ecrit si on a le meme fichier
	    
	    return outFile;
	}
	
	public static File createVideoFile() { //pour video
	    
	    String  cheminfinal =  getFilePath("Video",".mp4",null);
	    File outFile = null;
	    try {
	    	 outFile = new File(cheminfinal);
		} catch (Exception e) {
			// TODO: handle exception
		}
	   
		if(outFile.exists())
			outFile.delete();//on re ecrit si on a le meme fichier
	    
	    return outFile;
	}

	public static File createImageFile(){ //pour image
			    
	    String  cheminfinal =  getFilePath("Images",".jpg",null);
	    
	    File outFile = null;
	    try {
	    	 outFile = new File(cheminfinal);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(outFile.exists())
			outFile.delete();//on re ecrit si on a le meme fichier
	    
	    return outFile;
	}
	
	public static File createAudioFile(String extension){ //pour audio
	    
	    String  cheminfinal =  getFilePath("Audio",extension,null);
	    
	    File outFile = null;
	    try {
	    	 outFile = new File(cheminfinal);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(outFile.exists())
			outFile.delete();//on re ecrit si on a le meme fichier
	    
	    return outFile;
	}
	
	public static File getFileDirectory(){
		String FilePath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(FilePath,"StudentNotes");
		if(!file.exists())//creation pour la premiere fois si pas existant
			file.mkdir();
		
		return file;
	}
	
	public static String prefix(){
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		
	    return  timeStamp + "_";
	}
	
	private static String getFilePath(String Direc, String extension,String nameFile){ 
		
		String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
           Log.e("Reording", "SD Card is not mounted.  It is " + state + ".");
           return null;
        }
        
		
		File  file = Utils.getFileDirectory();//dans => StudentNotes
		
		//un sous dossier
	    File file_curent = new File(file.getPath(),Direc); // Audio Video Image
	    if(!file_curent.exists())//creation pour la premiere fois si pas existant
	    	file_curent.mkdir();
	    
	    Log.v("path", file.getAbsolutePath());
	    
	    String cheminfinal = null;
	   if(nameFile == null)
	       cheminfinal = file.getAbsolutePath()+"/"+Direc+"/"+Utils.prefix()+extension; 
	   else{
		   if(nameFile == "mark_")
		       cheminfinal = file.getAbsolutePath()+"/"+Direc+"/"+nameFile+Utils.prefix()+extension;
		   else if(nameFile == "note_")
			   cheminfinal = file.getAbsolutePath()+"/"+Direc+"/"+nameFile+Utils.prefix()+extension;
		   else if(nameFile!="mark_" && nameFile!="note_")
			   cheminfinal = file.getAbsolutePath()+"/"+Direc+"/"+nameFile+extension;
	   }
	   Log.v("chemin", cheminfinal);

		return cheminfinal;
	}
}
