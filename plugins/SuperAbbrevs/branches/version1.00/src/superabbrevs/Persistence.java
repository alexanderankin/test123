package superabbrevs;
import com.thoughtworks.xstream.XStream;
import java.util.*;
import java.io.*;
import java.net.*;
import org.gjt.sp.jedit.EditPlugin;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.util.Log;

public class Persistence {
        
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
        File modeDir = new File(Paths.ABBREVS_DIR);
        
        if (!modeDir.exists()){
            //make the SuperAbbrev settings dir
            modeDir.mkdir();
        }
        
        File modeFile = new File(MiscUtilities.constructPath(
                Paths.ABBREVS_DIR,name));
        
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
            Log.log(Log.ERROR, Persistence.class, e);
        }
    }
    
    public static void removeOldMacros(){
        
        File macrosDir = new File(Paths.MACRO_DIR);
        if (macrosDir.exists()){
            File tabFile = new File(MiscUtilities.constructPath(
                    Paths.MACRO_DIR,
                    Paths.TAB_MACRO));
            tabFile.delete();
            
            File shiftTabFile = new File(MiscUtilities.constructPath(
                    Paths.MACRO_DIR,
                    Paths.SHIFT_TAB_MACRO));
            shiftTabFile.delete();
            macrosDir.delete();
        }
    }
    
    private static void copyFileFromResourceDir(String filename, 
            boolean override) {
        URL url = getResource(Paths.RESOURCE_DIR+"/"+filename);
        String path = MiscUtilities.constructPath(Paths.ABBREVS_DIR,filename);
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
        copyFileFromResourceDir(Paths.VARIABLES_FILE);
    }
    
    public static void writeDefaultAbbrevFunctions(){
        copyFileFromResourceDir(Paths.ABBREVS_FUNCTION_FILE);
    }
    
    public static void writeDefaultTemplateGenerationFunctions(){
        copyFileFromResourceDir(Paths.TEMPLATE_GENERATION_FUNCTION_FILE,true);
    }
    
    
    
    /**
     * Method getResource(String filename)
     * Get at resource from the jar file
     */
    private static URL getResource(String filename) {
        return Persistence.class.getClassLoader().getResource(filename);
    }
    
    public static void createPluginsDir(){
        File pluginsDir = new File(Paths.PLUGINS_DIR);
        if (!pluginsDir.exists()){
            pluginsDir.mkdir();
        }
    }
    
    public static void createAbbrevsDir(){
        File abbrevsDir = new File(Paths.ABBREVS_DIR);
        if (abbrevsDir.exists()){
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
                Log.log(Log.ERROR, Persistence.class, e);
            } catch (IOException e){
                Log.log(Log.ERROR, Persistence.class, e);
            } catch (ClassNotFoundException e){
                Log.log(Log.ERROR, Persistence.class, e);
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
            Log.log(Log.ERROR, Persistence.class, e);
        }catch (IOException e){
            Log.log(Log.ERROR, Persistence.class, e);
        }
    }
    
    public static void saveAbbrevs(String mode, ArrayList<Abbrev> Abbrevs) 
    throws IOException {
        File modeFile = new File(mode+".xml");
        if (Abbrevs.isEmpty()) {
            // Remove the abbreviation file
            if (modeFile.exists()) {
                modeFile.delete();
            }
        } else {
            modeFile.createNewFile();        
            FileOutputStream out = new FileOutputStream(
                    Paths.getModeAbbrevsFile(mode));
            XStream xstream = new XStream();
            xstream.alias("Abbrev", Abbrev.class);
            xstream.alias("Abbrevs", ArrayList.class);
            xstream.toXML(Abbrevs, out);   
        }
    }
    
    public static ArrayList<Abbrev> loadAbbrevs(String mode) {
        try {
            FileInputStream in = new FileInputStream(
                    Paths.getModeAbbrevsFile(mode));
            XStream xstream = new XStream();
            xstream.alias("Abbrev", Abbrev.class);
            xstream.alias("Abbrevs", ArrayList.class);
            return (ArrayList<Abbrev>)xstream.fromXML(in);
        } catch (Exception ex) {
            // If the mode file does not exist return a empty abbreviation list
            return new ArrayList<Abbrev>();
        }
    }
}
