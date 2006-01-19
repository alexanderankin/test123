package superabbrevs;
import java.util.*;
import java.io.*;
import java.net.*;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;

public class SuperAbbrevsIO {
	
	private static final String ABBREVS_DIR = 
		MiscUtilities.constructPath(
			jEdit.getSettingsDirectory(),
			"SuperAbbrevs");
			
	private static final String MACRO_DIR = 
		MiscUtilities.constructPath(MiscUtilities.constructPath(
				jEdit.getSettingsDirectory(),
				"macros"),
				"SuperAbbrevs");
				
	private static final String ABBREV_FUNCTIONS = "abbrev_functions.bsh";
	
	public static Hashtable readAbbrevs(String mode){
		File modeFile = getModeFile(mode);
		try{
			FileInputStream in = new FileInputStream(modeFile);
			ObjectInputStream s = new ObjectInputStream(in);
			return (Hashtable)s.readObject();
		}catch (FileNotFoundException e){
			//TODO log
		}catch (IOException e){
			//TODO log
		}catch (ClassNotFoundException e){
			//TODO log
		}
		return null;
	}
	
	public static void write(String mode,Hashtable abbrevs){
		File modeFile = getModeFile(mode);
		if (abbrevs !=  null && (!abbrevs.isEmpty() || modeFile.exists())){
			try{
				FileOutputStream out = new FileOutputStream(modeFile);
				ObjectOutputStream s = new ObjectOutputStream(out);
				s.writeObject(abbrevs);
				s.flush();
			}catch (FileNotFoundException e){
				//TODO log
			}catch (IOException e){
				//TODO log
			}
		}
	}
	
	private static File getModeFile(String mode){
		String configDir = jEdit.getSettingsDirectory();
		File modeDir = 
			new File(MiscUtilities.constructPath(configDir,"SuperAbbrevs"));
		
		if (!modeDir.exists()){
			//TODO make defaults
			//make the SuperAbbrev settings dir
			modeDir.mkdir();
		}
		
		File modeFile = 
			new File(MiscUtilities.constructPath(modeDir.toString(),mode));
		
		return modeFile;
	}
	
	private static void copy(URL url, File f) {
		try{
			InputStream in = url.openStream();
			// Create a new file output stream
			FileOutputStream out = new FileOutputStream(f);
			byte[] buf = new byte[1024];
			int i = 0;
			while((i=in.read(buf))!=-1) {
				out.write(buf, 0, i);
			}
			in.close();
			out.close();
		} catch (Exception e) {
				//TODO log
				System.out.println("WriteToFile: File error: "+e.getMessage());
		}
	}
	
	public static void removeOldMacros(){
		
		File macrosDir = new File(MACRO_DIR);
		if (macrosDir.exists()){
			File tabFile = 
				new File(MiscUtilities.constructPath(MACRO_DIR,"tab.bsh"));
			tabFile.delete();

			File shiftTabFile = 
				new File(MiscUtilities.constructPath(MACRO_DIR,"shift-tab.bsh"));
			shiftTabFile.delete();
			macrosDir.delete();
		}	
	}
	
	public static void writeDefaultAbbrevs(){
		
		File abbrevsDir = new File(ABBREVS_DIR);
		if (!abbrevsDir.exists()){
			abbrevsDir.mkdir();
		}	
		
		Mode[] modes = jEdit.getModes();
		for(int i = 0; i < modes.length; i++){
			String name = modes[i].getName();
			URL url = SuperAbbrevsIO.class.getClassLoader().getResource("abbrevs/"+name+".abbr");
			File abbrevsFile = new File(MiscUtilities.constructPath(ABBREVS_DIR,name)); 
			if (url != null && !abbrevsFile.exists()){
				copy(url,abbrevsFile);
			}
		}
	}

	public static void writeDefaultAbbrevFunctions(){
		// the abbrevs dir is created by the writeDefaultAbbrevs function
		File abbrevsDir = new File(ABBREVS_DIR);
		URL url = SuperAbbrevsIO.class.getClassLoader().getResource(ABBREV_FUNCTIONS);
		File abbrevFunctionsFile = new File(MiscUtilities.constructPath(ABBREVS_DIR,ABBREV_FUNCTIONS)); 
		if (url != null && !abbrevFunctionsFile.exists()){
			copy(url,abbrevFunctionsFile);
		}
	}
	
	public static String getGlobalFunctionPath(){
		return MiscUtilities.constructPath(ABBREVS_DIR,ABBREV_FUNCTIONS);
	}
	
	public static boolean abbrevsDirExists(){
		File abbrevsDir = new File(ABBREVS_DIR);
		return abbrevsDir.exists();
	}
	
	public static void createAbbrevsDir(){
		if (!abbrevsDirExists()){
			
			File abbrevsDir = new File(ABBREVS_DIR);
			abbrevsDir.mkdir();
		}
	}
	
}
