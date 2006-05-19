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
				
	private static final String RESOURCE_DIR = "resources/";
				
	private static final String ABBREV_FUNCTIONS = 
		"abbrev_functions.bsh";
	private static final String TEMPLATE_GENERATION_FUNCTIONS = 
		"template_generation_functions.bsh";
	
	public static Hashtable readModeFile(String name){
		File modeFile = getModeFile(name);
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
	
	public static void writeModeFile(String mode,Hashtable data){
		File modeFile = getModeFile(mode);
		if (data != null && (!data.isEmpty() || modeFile.exists())){
			try{
				FileOutputStream out = new FileOutputStream(modeFile);
				ObjectOutputStream s = new ObjectOutputStream(out);
				s.writeObject(data);
				s.flush();
			}catch (FileNotFoundException e){
				//TODO log
			}catch (IOException e){
				//TODO log
			}
		}
	}
	
	private static File getModeFile(String name){
		String configDir = jEdit.getSettingsDirectory();
		String path = MiscUtilities.constructPath(configDir,"SuperAbbrevs");
		File modeDir = new File(path);
		
		if (!modeDir.exists()){
			//TODO make defaults
			//make the SuperAbbrev settings dir
			modeDir.mkdir();
		}
		
		File modeFile = 
			new File(MiscUtilities.constructPath(modeDir.toString(),name));
		
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
	
	/**
	 * Method copyFileFromResourceDir(String filename)
	 */
	private static void copyFileFromResourceDir(String filename) {
		URL url = getResource(RESOURCE_DIR+filename);
		String path = MiscUtilities.constructPath(ABBREVS_DIR,filename);
		File file = new File(path); 
		if (url != null && !file.exists()){
			copy(url,file);
		}
	}
	
	public static void writeDefaultAbbrevs(){
		Mode[] modes = jEdit.getModes();
		for(int i = 0; i < modes.length; i++){
			String name = modes[i].getName();
			// would be nicer if I knew how to iterate the files in the resource 
			// directory
			copyFileFromResourceDir(name);
		}
	}
	
	public static void writeDefaultVariables() {
		copyFileFromResourceDir("global.variables");
	}
	
	public static void writeDefaultAbbrevFunctions(){
		File abbrevsDir = new File(ABBREVS_DIR);
		URL url = getResource(ABBREV_FUNCTIONS);
		String path = getAbbrevsFunctionPath();
		File abbrevFunctionsFile = new File(path);
		
		if (url != null && !abbrevFunctionsFile.exists()){
			copy(url,abbrevFunctionsFile);
		}
	}
	
	public static void writeDefaultTemplateGenerationFunctions(){
		File abbrevsDir = new File(ABBREVS_DIR);
		URL url = getResource(TEMPLATE_GENERATION_FUNCTIONS);
		String path = getTemplateGenerationFunctionPath();
		File templateGenerationFunctionsFile = new File(path);
		if (url != null && !templateGenerationFunctionsFile.exists()){
			copy(url,templateGenerationFunctionsFile);
		}
	}
	
	/**
	 * Method getResource(String filename)
	 * Get at resource from the jar file
	 */
	private static URL getResource(String filename) {
		return SuperAbbrevsIO.class.getClassLoader().getResource(filename);
	}
	
	public static String getAbbrevsFunctionPath(){
		return MiscUtilities.constructPath(ABBREVS_DIR,ABBREV_FUNCTIONS);
	}
	
	public static String getTemplateGenerationFunctionPath(){
		return MiscUtilities.constructPath(ABBREVS_DIR,TEMPLATE_GENERATION_FUNCTIONS);
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
