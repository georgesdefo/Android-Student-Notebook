package com.merlin.studentnotes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.util.Log;
import android.widget.Toast;

public class UtilSaveAndGetFile {

	
	static int dureeEnregistrement;
	static FileOutputStream fos;
	static FileInputStream fin;
	static File file;
	
	public static void saveBookMark(FragmentsSliderActivity fragmentsSliderActivity){
		//Utils.starttime,Utils.finalTime,Utils.notes,Utils.marks,Utils.captures
		
		file = Utils.createBookMarkFile(Utils.nameRecordFile);
		
		dureeEnregistrement = Utils.finalTime - Utils.starttime;
		
		//write(microRecord);
		 writewithprint(fragmentsSliderActivity);
		init();
		//Read(microRecord);					
	}
	
    
	private static void writewithprint(FragmentsSliderActivity fragmentsSliderActivity) {
		PrintWriter pw = null;
		String sepin ="          -------------------------             " ;
        try {
        	 pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
        	 
        	 pw.println("Name audioFile: "+Utils.nameFileAudio.substring(36, Utils.nameFileAudio.indexOf(".") )); //nom        	 
        	 pw.println("Duree: "+dureeEnregistrement);//duree
        	  
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
        
        Toast.makeText(fragmentsSliderActivity,"Sauvegarde Terminee", Toast.LENGTH_SHORT).show();
	}

	
	public static void init(){
		
		Utils.notes = new ArrayList<String>();
		Utils.marks = new ArrayList<String>();
		Utils.captures = new ArrayList<String>();
		Utils.finalTime = Utils.starttime = 0;
		Utils.nameFileAudio = null;
		Utils.nameRecordFile = null;
		Utils.my_note = null;
		Utils.my_mark = null;
		Utils.my_capture = null;
		Utils.nameImage = null;
	}
		
	public static void SaveNotes(final List<String> list,FragmentsSliderActivity fragmentsSliderActivity) {
		
		final File file = Utils.createTextFile("note_");	
	            write(file,list,fragmentsSliderActivity);	       	       	       
   }
	
	private static void write(File file,List<String> my_list, FragmentsSliderActivity fragmentsSliderActivity) {
		
		PrintWriter pw = null;
		
        try {
        	 pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
        	 for (Object value:my_list)
        	    {
        		
        		 		pw.println(value.toString());
        		 		 Log.v("writting", value.toString());
        	    }     
        	Toast.makeText(fragmentsSliderActivity,"save", Toast.LENGTH_SHORT).show(); 
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
			
   public static void SaveMarks(final Map<Integer,Map<String,Integer>> map,FragmentsSliderActivity fragmentsSliderActivity) {
		
		
	        final File file = Utils.createTextFile("mark_");	      
					Log.v("writting", "starting write");
					writeMarks(file,map,fragmentsSliderActivity);	       
   }	 
		
	private static void writeMarks(File file, Map<Integer, Map<String, Integer>> map, FragmentsSliderActivity fragmentsSliderActivity) {
		
		List<String> liste = new ArrayList<String>();
		liste = getList(map,liste);
		
            PrintWriter pw = null;		
        try {
        	 pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
        	 for (Object value:liste){
        		  pw.println(value.toString());

        	 }
        	 Toast.makeText(fragmentsSliderActivity,"complete", Toast.LENGTH_SHORT).show();
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

	private static List<String> getList(Map<Integer, Map<String, Integer>> map, List<String> liste) {
		
		String finalstring = null;
		
		for (Integer key : map.keySet())
		{
			finalstring += key+"_";
		    Map<String, Integer> map2 = map.get(key);
		    
		    for (String innerKey : map2.keySet())
		    {
		    	finalstring += innerKey+"_"+map2.get(innerKey); 
		    	liste.add(finalstring);
		    }
		}
		
		return liste;
	}

}
