package com.addictedtor.orchestra_installer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.af.commons.io.FileTransfer;
import org.af.commons.install.WindowsDesktop;
import org.af.commons.install.Desktop;
import org.af.commons.install.FreeDesktop;
import org.af.commons.OSTools;

import java.io.*;


/**
 * Installer.
 *  - Extracts icon and R script files from jars to plugin.home
 *  - Configures the R script
 *  - Creates shortcut for JEditR
 *
 * @author Bernd Bischl <bernd_bischl@gmx.net>
 * @author Romain Francois <francoisromain@free.fr>
 */

public class Installer {
    protected static final Log logger = LogFactory.getLog(Installer.class);

    private File jeditHomeDir = null;
    private File pluginHomeDir = null;
    private File rHomeDir = null;
//    private File rJavaDir = null;
//    private String rLibPaths = null;
    private File shortcutDir = null;
    public static final String RSCRIPT_NAME = "orchestra_starter.r";
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
        String script = "", s;
        File scriptFile = new File(pluginHomeDir, RSCRIPT_NAME);
        logger.info("Extracting R starter script to: " + scriptFile);
        try {
            InputStream is = this.getClass().getResourceAsStream("/" + RSCRIPT_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(scriptFile));
            while((s = reader.readLine()) != null) {
                script += s + "\n";
            }
            is.close();

            String javaStr = forwardSlashes(System.getProperty("java.home"));
            String javaExeStr;
            if (OSTools.isWindows())
                javaExeStr = "javaw.exe";
            else
                javaExeStr = "java";
            String jeditStr = forwardSlashes(jeditHomeDir);

            String pluginHomeStr = forwardSlashes(pluginHomeDir.getAbsolutePath()) ;
            
            logger.info("Replacing the following templates:");
            logger.info("@JAVA_HOME@ :" + javaStr);
            logger.info("@JAVA_EXE@ :" + javaExeStr);
            logger.info("@JEDIT_HOME@ :" + jeditStr);
            logger.info("@PLUGIN_HOME@ :" + pluginHomeDir );
            

            script = script.replace("@JAVA_HOME@", javaStr);
            script = script.replace("@JAVA_EXE@", javaExeStr);
            script = script.replace("@JEDIT_HOME@", jeditStr);
            script = script.replace("@PLUGIN_HOME@", pluginHomeStr);
            
            File libDir = new File( pluginHomeDir + "/library" ) ;
            if( !libDir.exists() ){
            	logger.info( "creating R library in plugin home: '" + pluginHomeStr + "/library" ) ;
            	libDir.mkdirs(); 
            } else{
            	// maybe check that the this actually is a library
            }
            
            writer.write(script);
            writer.close();
        } catch (IOException e) {
            //todo deal with it better
            // should not happen
            e.printStackTrace();
        }
        logger.info("R starter script done.");
        return scriptFile;
    }

    private void extractIcons() throws IOException{
        logger.info("Extracting icons to: " + pluginHomeDir);
        FileTransfer.copyResourceToLocalDir(this.getClass().getResource("/"+ICON_NAME_WINDOWS),
                ICON_NAME_WINDOWS, pluginHomeDir);
        FileTransfer.copyResourceToLocalDir(this.getClass().getResource("/"+ICON_NAME_WINDOWS),
                ICON_NAME_LINUX, pluginHomeDir);
        logger.info("Extracting icons done.");
    }

    private void createShortcut(File script) throws IOException{
        File rExe = new File(new File(rHomeDir, "bin"), "R");
        Desktop desktop;
        File iconFile;
        if (OSTools.isWindows()) {
            desktop = new WindowsDesktop();
            ((WindowsDesktop)desktop).setWorkingDir(pluginHomeDir.getAbsolutePath());
            ((WindowsDesktop)desktop).setArguments("CMD BATCH \"\"" + script.getAbsolutePath() + "\"\"");
            iconFile = new File(pluginHomeDir, ICON_NAME_WINDOWS);
        } else {
            desktop = new FreeDesktop();
            iconFile = new File(pluginHomeDir, ICON_NAME_LINUX);
        }
        logger.info("Shortcut target is: " + rExe.getAbsolutePath());
        desktop.setExec(rExe.getAbsolutePath());
        logger.info("Shortcut icon is: " + iconFile);
        desktop.setIconpath(iconFile.getAbsolutePath());
        desktop.createDesktopEntry(shortcutDir, "Orchestra");
    }

//    private void createShortcutFreeDesktop(String targetCmd, File iconFile) throws IOException{
//        FreeDesktop d = new FreeDesktop();
//        d.setExec(targetCmd);
//        d.setIconpath(iconFile.getAbsolutePath());
//        logger.info("Shortcut icon is: " + iconFile);
//        d.createDesktopEntry(shortcutDir, "Orchestra");
//    }

    public void install() throws IOException{
        File scriptFile = extractRScript();
        extractIcons();

        boolean createShortcut = true;
        if (createShortcut) {
            logger.info("Creating shortcut entry in " + shortcutDir.getAbsolutePath());
            createShortcut(scriptFile);
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
