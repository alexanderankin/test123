package com.addictedtor.orchestra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import af.commons.OSTools;
import af.commons.install.FreeDesktop;
import af.commons.install.WindowsDesktop;
import af.commons.io.FileTransfer;

/**
 * Installer.
 *  - Extracts icon and R script files from jars to plugin.home
 *  - Configures the R script
 *  - Creates shortcut for Orchestra
 *
 * @author Bernd Bischl <bernd_bischl@gmx.net>
 * @author Romain Francois <francoisromain@free.fr>
 */
public class Installer {
    protected static final Log logger = LogFactory.getLog(Installer.class);

    private File jeditHomeDir = null;
    private File pluginHomeDir = null;
    private File rHomeDir = null;
		// private File rJavaDir = null;
		// private String rLibPaths = null;
    private File shortcutDir = null;
    public static final String RSCRIPT_NAME = "orchestra_starter.r";
    public static final String PROPERTY_NAME = "orchestra_properties.txt";
    
    public static final String ICON_NAME_WINDOWS = "orchestra_icon.ico";
    public static final String ICON_NAME_LINUX = "orchestra_icon.png";

    public Installer(String jeditHomeDir, String pluginHomeDir,
                     String rHomeDir, String shortcutDir) {
        this.jeditHomeDir = new File(jeditHomeDir);
        this.pluginHomeDir = new File(pluginHomeDir);
        this.rHomeDir = new File(rHomeDir);
        this.shortcutDir = new File(shortcutDir);
        logger.info("Installer was started with:");
        logger.info("JEDIT_HOME:" + this.jeditHomeDir.getAbsolutePath());
        logger.info("PLUGIN_HOME:" + this.pluginHomeDir.getAbsolutePath());
        logger.info("R_HOME:" + this.rHomeDir.getAbsolutePath());
        logger.info("Shortcut dir:" + this.shortcutDir.getAbsolutePath());
    }

    private File extractRScript() {
        String s;
        File scriptFile = new File(pluginHomeDir, RSCRIPT_NAME);
        logger.info("Extracting R starter script to: " + scriptFile);
        try {
            InputStream is = this.getClass().getResourceAsStream("/" + RSCRIPT_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile));
            while((s = reader.readLine()) != null) {
                writer.write(s + "\n" ) ;
            }
            is.close();
            writer.close(); 
            
        } catch( IOException ioe){
        	logger.info( "IOException : " + ioe.getMessage() ) ;
        }
        logger.info("R starter script done.");
        return scriptFile ;
    }

    private File extractPropertyFile() {
    	File propertyFile = new File(pluginHomeDir, PROPERTY_NAME);
        try {
        	/* read the template */
    		InputStream is = this.getClass().getResourceAsStream("/" + PROPERTY_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            BufferedWriter writer = new BufferedWriter(new FileWriter(propertyFile));
            String s ;
            StringBuilder builder = new StringBuilder(); 
            while((s = reader.readLine()) != null) {
                builder.append( s + "\n") ;
            }
            String properties = builder.toString() ;
            
            /* grab the values of the variables of interest */
            String javaStr = forwardSlashes(System.getProperty("java.home"));
            String javaExeStr;
            javaExeStr = OSTools.isWindows() ? "javaw.exe" : "java";
            String jeditStr = forwardSlashes(jeditHomeDir);
            String pluginHomeStr = forwardSlashes(pluginHomeDir.getAbsolutePath()) ;

            /* log these values */
            logger.info("Replacing the following templates:");
            logger.info("@JAVA_HOME@ :" + javaStr);
            logger.info("@JAVA_EXE@ :" + javaExeStr);
            logger.info("@JEDIT_HOME@ :" + jeditStr);
            logger.info("@PLUGIN_HOME@ :" + pluginHomeDir );
            
            /* replace the values in the property file */
            properties = properties.replace("@JAVA_HOME@", javaStr);
            properties = properties.replace("@JAVA_EXE@", javaExeStr);
            properties = properties.replace("@JEDIT_HOME@", jeditStr);
            properties = properties.replace("@PLUGIN_HOME@", pluginHomeStr);
            
            File libDir = new File( pluginHomeDir + "/library" ) ;
            if( !libDir.exists() ){
            	logger.info( "creating R library in plugin home: '" + pluginHomeStr + "/library" ) ;
            	libDir.mkdirs(); 
            } else{
            	// maybe check that the this actually is a library
            }
            
            writer.write(properties);
            writer.close();
        } catch (IOException e) {
            //todo deal with it better
            // should not happen
            e.printStackTrace();
        }
        logger.info("property file done.");
        return propertyFile;
    }

    private void extractOrchestraIcons() throws IOException{
        logger.info("Extracting icons to: " + pluginHomeDir);
        FileTransfer.copyResourceToLocalDir(this.getClass().getResource("/"+ICON_NAME_WINDOWS),
                ICON_NAME_WINDOWS, pluginHomeDir);
        FileTransfer.copyResourceToLocalDir(this.getClass().getResource("/"+ICON_NAME_WINDOWS),
                ICON_NAME_LINUX, pluginHomeDir);
        logger.info("Extracting icons done.");
    }

    private void createShortcutWindows(String targetCmd, File iconFile) throws IOException{
        WindowsDesktop d = new WindowsDesktop();
        d.setExec(targetCmd);
        d.setIconpath(iconFile.getAbsolutePath());
        logger.info("Shortcut icon is: " + iconFile);
        d.setWorkingDir(pluginHomeDir.getAbsolutePath());
        d.createDesktopEntry(shortcutDir, "Orchestra");
    }

    private void createShortcutFreeDesktop(String targetCmd, File iconFile) throws IOException{
        FreeDesktop d = new FreeDesktop();
        d.setExec(targetCmd);
        d.setIconpath(iconFile.getAbsolutePath());
        logger.info("Shortcut icon is: " + iconFile);
        d.createDesktopEntry(shortcutDir, "Orchestra");
    }

    public void install() throws IOException{
    	extractPropertyFile() ;
        File scriptFile = extractRScript();
        extractOrchestraIcons();
        File rExe = new File(new File(rHomeDir, "bin"), "R");
        
        boolean createDesktop = true;
        if (createDesktop) {
            logger.info("Creating shortcut entry in " + shortcutDir.getAbsolutePath());
            String targetCmd = rExe.getAbsolutePath() + " CMD BATCH --vanilla --default-packages=\"base\" " + scriptFile.getAbsolutePath();
            logger.info("Shortcut cmd is: " + targetCmd);
            if (OSTools.isWindows()) {
                createShortcutWindows(targetCmd, new File(pluginHomeDir, ICON_NAME_WINDOWS));
            } else {
                createShortcutFreeDesktop(targetCmd, new File(pluginHomeDir, ICON_NAME_LINUX));
            }
            logger.info("Shortcut done.");
        }
    }
    
    private String forwardSlashes(String s) {
        return s.replace("\\", "/");
    }

    private String forwardSlashes(File f) {
        return forwardSlashes(f.getAbsolutePath());
    }


}
