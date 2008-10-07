package superabbrevs.installation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;
import superabbrevs.Paths;
import superabbrevs.Persistence;
import superabbrevs.migration.Migration;
import superabbrevs.utilities.Log;

public class Installation {
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
            Log.log(Log.Level.ERROR, Installation.class, e);
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
    
    private static void writeDefaultAbbrevs(){
        Mode[] modes = jEdit.getModes();
        for(int i = 0; i < modes.length; i++){
            String name = modes[i].getName();
            // would be nicer if I knew how to iterate the files in the resource
            // directory
            copyFileFromResourceDir(name + ".xml");
        }
    }
    
    private static void writeDefaultVariables() {
        copyFileFromResourceDir(Paths.VARIABLES_FILE);
    }
    
    private static void writeDefaultAbbrevFunctions(){
        copyFileFromResourceDir(Paths.ABBREVS_FUNCTION_FILE);
    }
    
    private static void writeDefaultTemplateGenerationFunctions(){
        copyFileFromResourceDir(Paths.TEMPLATE_GENERATION_FUNCTION_FILE,true);
    }
    
    /**
     * Method getResource(String filename)
     * Get at resource from the jar file
     */
    private static URL getResource(String filename) {
        return Persistence.class.getClassLoader().getResource(filename);
    }
    
    
    private static void createAbbrevsDir(){
        File abbrevsDir = new File(Paths.ABBREVS_DIR);
        if (!abbrevsDir.exists()){
            Log.log(Log.Level.DEBUG, Installation.class, 
                    "Creating plugin directory: " + Paths.ABBREVS_DIR);
            abbrevsDir.mkdirs();
        }
    }
    
    public static void install() {
        Log.log(Log.Level.DEBUG, Installation.class, "Installing plugin");
        createAbbrevsDir();
        writeDefaultAbbrevs();
        writeDefaultAbbrevFunctions();
        writeDefaultTemplateGenerationFunctions();
        Migration.Migrate();
    }
}
