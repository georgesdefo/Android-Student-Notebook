package com.example.testfff;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class Utils {

    
	public static int alert = 1;//permet d'afficher un message pour la premiere qu'on lance la classe microrecorder
	public final static int MAX_SIZE = 3*60*1000;//1800000; //Taille maximale du téléchargement
	public static boolean fin = false;//permet le deroulement du while dans le ondoinbackground(Thread)	
	public static boolean startMark = true;//fixe le debut d'un marquage
	public static Long starttime_mark,finalTimeMark =(long) 0;//debut et fin du marquage
	public static Long starttime,finalTime =(long) 0;//debut et fin de l'enregistrement
	public static Long starttime_note =(long) 0;//debut note	
	public static Long timeinitcapture,timefinalcapture =(long) 0;//debut et fin capture 
	//map qui contient le necessaire pour chaque marquage <temps init ,final et contenu>
	public static LinkedHashMap<Long, Map<String,Long> > dictionary_maker = new LinkedHashMap<Long, Map<String, Long>>();
	//list qui contient les textes dans leur forme final en reference avec les key value de la map
	public static ArrayList<String> marks = new ArrayList<String>();
	//liste qui va contenir nos notes
	public static ArrayList<String> notes = new ArrayList<String>();
	//liste pour les captures
	public static ArrayList<String> captures = new ArrayList<String>();
	public static String my_note = null;//format final de notre note => init##notevalue
	public static String my_mark = null;//format final marquage => init##final##markvalue
	public static String my_capture = null;//format final image => init##final##nameimage
	public static String nameImage = null;//nom du fichier Image	
	public static String nameFileAudio = null;//nom du fichier audio
	public static String nameRecordFile = null;//titre de l'enregistrement
	
	public static Boolean startnewrecord = false;	//determine si l'on a deja lance un enregistrement
	public static String recentvideoOutput =  null;	
	public static MediaRecorder recorder = null;	
	public static String outputFileName = null;
		
	public static boolean ok = true;
	public static boolean okfin = false;
	
	public static String precedentnamefile=null;//fixe la re-ecriture avec la racine au niveau du nameRecordFile
	public static boolean precedentnamef ;//nom du precedent ficher enregistre que l'on utilisera via precedentnamefile pour former le nom du suivant ficher si l'user ne fixe pas le titre
	public static int fixeur = 0;
		
	public static Long gettime(){
		Long time = System.currentTimeMillis();
		//String finaltimeexpression = ((time)/60*60*1000+":"+(time)/60*1000+":"+(time)/1000)+"";
		Log.v("TIME", time+"");
		return time;
	}	
	public static boolean setstartMark(boolean bol){
		
		return startMark = bol;
	}
	public static boolean getstartMark(){		
		return startMark;
	}
	
	/**
	 * creation du fichier general contenant les information sur l'activite
	 * de l'utilisateur lors de l'enregistrement
	 * 
	 * @param nameRecordFile2 String
	 * @return outFile File
	 */
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
	/**
	 * methode qui va cree les fichier note et mark dans le dossier Doc
	 * @param nameFile String
	 * @return outFile  File
	 */
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
	/**
	 * creation fichier video
	 * 
	 * @return outFile  File
	 */
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
	/**
	 * creation fichier image
	 * 
	 * @return outFile  File
	 */
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
	/**
	 * creation fichier audio
	 * @param extension String
	 * @return outFile  File
	 */
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
	/**
	 * methode qui fixe le dossier general de l'app
	 * @return FilePath File => root/StudentNotes
	 */
	public static File getFileDirectory(){
		String FilePath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(FilePath,"StudentNotes");
		if(!file.exists())//creation pour la premiere fois si pas existant
			file.mkdir();
		
		return file;
	}
	/**
	 * mise en place d'un prefix pour les fichier 
	 * @return timeStamp + "_" String
	 */
	public static String prefix(){
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		
	    return  timeStamp + "_";
	}
	/**
	 * methode qui retoune le chemin vers le fichier
	 * 
	 * @param Direc String
	 * @param extension String
	 * @param nameFile String
	 * @return cheminfinal String
	 */
	private static String getFilePath(String Direc, String extension,String nameFile){ 
		
		String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
           Log.e("Reording", "SD Card is not mounted.  It is " + state + ".");
           return null;
        }
        
		
		File  file = Utils.getFileDirectory();//dans => StudentNotes
		
		//un sous dossier
	    File file_curent = new File(file.getPath(),Direc); // Audio Video Image Doc BookMaker
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
