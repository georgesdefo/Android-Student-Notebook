package com.merlin.studentnotepre;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;
import android.widget.Toast;

public class UtilSaveAndGetFile {

	
	static Long dureeEnregistrement;
	static File file;//fichier d'enregistrement
	static String temps_final = null;
	/**
	 * methode qui recupere le nom du fichier, le cree, l'enregistre, et re initialise les parametres necessaire au bon fonctionnement
	 * 
	 * @param microRecord 
	 */
	public static void saveBookMark(MicroRecord microRecord){
		
		 getname();	 		 
		file = Utils.createBookMarkFile(Utils.nameRecordFile);		
		dureeEnregistrement = Utils.finalTime - Utils.starttime;
				
		writewithprint(microRecord);		 
		init();							
	}
	/**
	 * recupere le nom du fichier precedent	
	 * si l'utilise lance un enregistrement sans preciser le titre de ce dernier
	 * et on formera un nom de fichier dont la racine est le nom du fichier precedent
	 * ainsi on poura regrouper les fichier de la meme racine
	 * tout ceci car le bouton pause n'existe pas : juste play(start) et stop
	 *    
	 */
	private static void getname() {
		
		 if(Utils.precedentnamef == false){
			 //a chaque fois que l'user indique le titre on aura Utils.precedentnamef = false  
			 //ainsi le Utils.precedentnamefile sera egale au nom du dernier ficher enregistre
			 Utils.precedentnamefile = Utils.nameRecordFile;
		 }
		 else{ // si un fichier sans nom on fixe  Utils.precedentnamef = true
			 
			 // si il ne rempli pas le champ titre
			//Utils.nameRecordFile est fixee et = a notitle
			 
			 // on remet tout dans Utils.precedentnamefile pour les cas ou l'on a 1 enregistrement avec titre suivi de +sieurs sans titre
			 Utils.nameRecordFile = Utils.precedentnamefile+"_"+Utils.nameRecordFile;
			 Utils.precedentnamefile = Utils.nameRecordFile;
		 }
	}

/**
 * ecriture dans le fichier de destination avec un encodage particulier
 * @param microRecord
 */
	private static void writewithprint(MicroRecord microRecord) {
		PrintWriter pw = null;
		String sepin ="          -------------------------             " ;
		temps_final = (dureeEnregistrement/(60*60*1000))+":"+(dureeEnregistrement/(60*1000))+":"+(dureeEnregistrement/1000);
        try {
        	 pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
        	 
        	 pw.println("Name audioFile: "+Utils.nameFileAudio.substring(36, Utils.nameFileAudio.indexOf(".") )); //nom        	 
        	 pw.println("Temps initial: "+Utils.starttime);//temps initial
        	 pw.println("Duree: "+temps_final);//duree 
			 if(Utils.captures.size()>0){
				 pw.println(sepin);
				 pw.println("CAPTURES");//separation				
				 for(int i =0;i<Utils.captures.size();i++){// tinit##note
						
						if(i+1 == Utils.captures.size())
							pw.println(Utils.captures.get(i));
						else{
							pw.println(Utils.captures.get(i));
							pw.println("&&");
						}
						
					}
				 pw.println("CAPTURES");
				}
				if(Utils.marks.size()>0){
					pw.println(sepin);
					pw.println("MARKS");//separation			
					for(int i =0;i<Utils.marks.size();i++){// tinit##note
						
						if(i+1 == Utils.marks.size())
							pw.println(Utils.marks.get(i));
						else{
							pw.println(Utils.marks.get(i));
							pw.println("&&");
						}
						
					}
					pw.println("MARKS");
				}
				if(Utils.notes.size()>0){
					pw.println(sepin);
					pw.println("NOTES");//separation
					for(int i =0;i<Utils.notes.size();i++){// tinit##note
						
						if(i+1 == Utils.notes.size())
							pw.println(Utils.notes.get(i));
						else{
							pw.println(Utils.notes.get(i));
							pw.println("&&");
						}
						
					}
					pw.println("NOTES");
				}
							
				Log.v("Writing", "Fin Corp");
            
        }catch (FileNotFoundException e) {
			Log.v("Saving", "File pas trouve");
			//e.printStackTrace();
		}catch (Exception e) {
            Log.d("Error in File write: ", ""+e.getMessage());
        } finally {
            if (pw != null) {
            	pw.flush();
            	pw.close();
            	Log.v("Writing", "Saving");
            }
        }
        
        Toast.makeText(microRecord,"Sauvegarde Terminee", Toast.LENGTH_SHORT).show();
	}

	/**
	 * apres chaque save on reinitialise les attribut necessaires
	 */
	public static void init(){
		
		Utils.notes = new ArrayList<String>();
		Utils.marks = new ArrayList<String>();
		Utils.captures = new ArrayList<String>();
		Utils.dictionary_maker = new LinkedHashMap<Long, Map<String, Long>>();
		
		Utils.finalTime = Utils.starttime = (long)0;
		Utils.starttime_mark = Utils.finalTimeMark = (long)0;
		Utils.starttime = Utils.finalTime = (long)0;
		Utils.starttime_note = (long)0;
		Utils.timeinitcapture = Utils.timefinalcapture = (long)0;
		
		Utils.nameFileAudio = null;
		Utils.nameRecordFile = null;
		Utils.my_note = null;
		Utils.my_mark = null;
		Utils.my_capture = null;
		Utils.nameImage = null;
		
		temps_final = null;
	}
	/**
	 * methode qui cree le fichier destination et ecrit la note dans celui-ci
	 * 	
	 * @param list
	 * @param microRecord
	 */
	public static void SaveNotes(final List<String> list,MicroRecord microRecord) {
		
		final File file = Utils.createTextFile("note_");	
	            write(file,list,microRecord);	       	       	       
   }
	
	private static void write(File file,List<String> my_list, MicroRecord microRecord) {
		
		PrintWriter pw = null;
		
        try {
        	 pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
        	 for (Object value:my_list)
        	    {
        		
        		 		pw.println(value.toString());
        		 		 Log.v("writting", value.toString());
        	    }     
        	Toast.makeText(microRecord,"save", Toast.LENGTH_SHORT).show(); 
            Log.d("File Write is success","fine");
        } catch (Exception e) {
            Log.d("Error in File write: ", ""+e.getMessage());
        } finally {
            if (pw != null) {
            	pw.flush();
            	pw.close();
            }
        }
	}
	/**
	 * 	methode qui cree le fichier destination et ecrit la marque dans celui-ci
	 * 	
	 * @param dictionary_maker
	 * @param microRecord
	 */
   public static void SaveMarks(final LinkedHashMap<Long, Map<String, Long>> dictionary_maker,MicroRecord microRecord) {
		
		
	        final File file = Utils.createTextFile("mark_");	      
					Log.v("writting", "starting write");
					writeMarks(file,dictionary_maker,microRecord);	       
   }	 
		
	private static void writeMarks(File file, LinkedHashMap<Long, Map<String, Long>> dictionary_maker, MicroRecord microRecord) {
		
		List<String> liste = new ArrayList<String>();
		liste = getList(dictionary_maker,liste);
		
            PrintWriter pw = null;		
        try {
        	 pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
        	 for (Object value:liste){
        		  pw.println(value.toString());

        	 }
        	 Toast.makeText(microRecord,"complete", Toast.LENGTH_SHORT).show();
            Log.d("File Write is success","fine");
        } catch (Exception e) {
            Log.d("Error in File write: ", ""+e.getMessage());
        } finally {
        	if (pw != null) {
            	pw.flush();
            	pw.close();
            }
        }
   }
    /**
     * methode qui recupere une map(integer,map)
     * pour chaque key, value : on reforme un simple String selon l'encodage voulu
     * le string ainsi cree est garde dans une liste celle qui est retournee
     * 
     * @param dictionary_maker
     * @param liste
     * @return liste
     */
	private static List<String> getList(LinkedHashMap<Long, Map<String, Long>> dictionary_maker, List<String> liste) {
		
		String finalstring = null;
		
		for (Long key : dictionary_maker.keySet())
		{
			finalstring += key+"_";
		    Map<String, Long> map2 = dictionary_maker.get(key);
		    
		    for (String innerKey : map2.keySet())
		    {
		    	finalstring += innerKey+"_"+map2.get(innerKey); 
		    	liste.add(finalstring);
		    }
		}
		
		return liste;
	}

}
