package superabbrevs;
import java.util.*;
import java.io.*;
import java.net.*;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.util.Log;

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
				
	private static final String RESOURCE_DIR = "resources";
	
	public static final String ABBREVS_FUNCTION_FILE = "abbrev_functions.bsh";
	public static final String TEMPLATE_GENERATION_FUNCTION_FILE =  
		"template_generation_functions.bsh";
	public static final String VARIABLES_FILE = "global.variables";
	
	public static Hashtable readModeFile(String name){
    return readObjectFile(getModeFile(name));
	}
	
	public static void writeModeFile(String mode,Hashtable data){
		File modeFile = getModeFile(mode);
		if (data != null && (!data.isEmpty() || modeFile.exists())){
      writeObjectFile(modeFile,data);
		}
	}
	
	private static File getModeFile(String name){
		File modeDir = new File(ABBREVS_DIR);
		
		if (!modeDir.exists()){
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
       Log.log(Log.ERROR, SuperAbbrevsIO.class, e);
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
	
	private static void copyFileFromResourceDir(String filename, boolean override) {
		URL url = getResource(RESOURCE_DIR+"/"+filename);
		String path = MiscUtilities.constructPath(ABBREVS_DIR,filename);
		File file = new File(path); 
		if (url != null && (override || !file.exists())){
			copy(url,file);
		}
	}
	
	private static void copyFileFromResourceDir(String filename) {
		copyFileFromResourceDir(filename,false);
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
		copyFileFromResourceDir(VARIABLES_FILE);
	}
	
	public static void writeDefaultAbbrevFunctions(){
		copyFileFromResourceDir(ABBREVS_FUNCTION_FILE);
	}
	
	public static void writeDefaultTemplateGenerationFunctions(){
		copyFileFromResourceDir(TEMPLATE_GENERATION_FUNCTION_FILE,true);
	}
	
	public static String getAbbrevsFunctionPath(){
		return MiscUtilities.constructPath(ABBREVS_DIR,
										   TEMPLATE_GENERATION_FUNCTION_FILE);
	}
	
	public static String getTemplateGenerationFunctionPath(){
		return MiscUtilities.constructPath(ABBREVS_DIR,ABBREVS_FUNCTION_FILE);
	}
	
	/**
	 * Method getResource(String filename)
	 * Get at resource from the jar file
	 */
	private static URL getResource(String filename) {
		return SuperAbbrevsIO.class.getClassLoader().getResource(filename);
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
  
  public static Hashtable readObjectFile(File file) {
    if (file.exists()){
    	try{
        FileInputStream in = new FileInputStream(file);
        ObjectInputStream s = new ObjectInputStream(in);
        return (Hashtable)s.readObject();
      } catch (FileNotFoundException e){
        Log.log(Log.ERROR, SuperAbbrevsIO.class, e);
      } catch (IOException e){
        Log.log(Log.ERROR, SuperAbbrevsIO.class, e);
      } catch (ClassNotFoundException e){
        Log.log(Log.ERROR, SuperAbbrevsIO.class, e);
      }
    }
    return null;
  }
  
  public static void writeObjectFile(File file, Object data) {
    try{
      FileOutputStream out = new FileOutputStream(file);
      ObjectOutputStream s = new ObjectOutputStream(out);
      s.writeObject(data);
      s.flush();
    }catch (FileNotFoundException e){
      Log.log(Log.ERROR, SuperAbbrevsIO.class, e);
    }catch (IOException e){
      Log.log(Log.ERROR, SuperAbbrevsIO.class, e);
    }
  }
}
