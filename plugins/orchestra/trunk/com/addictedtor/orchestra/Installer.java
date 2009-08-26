package com.addictedtor.orchestra;

import org.af.commons.install.DesktopShortcut;
import org.af.commons.install.FreeDesktopShortcut;
import org.af.commons.install.WindowsDesktopShortcut;
import org.af.commons.io.FileTransfer;
import org.af.commons.threading.SafeSwingWorker;
import org.af.commons.tools.OSTools;
import org.af.jhlir.packages.CantFindPackageException;
import org.af.jhlir.packages.RPackage;
import org.af.jhlir.tools.RCmdBatch;
import org.af.jhlir.tools.RCmdBatchException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * Installer.
 *  - Extracts icon and R script files from jars to plugin.home
 *  - Configures the R script
 *  - Creates shortcut for Orchestra
 *
 * @author Bernd Bischl <bernd_bischl@gmx.net>
 * @author Romain Francois <francoisromain@free.fr>
 */
public class Installer extends SafeSwingWorker<Void, String> {
    protected static final Log logger = LogFactory.getLog(Installer.class);

    private File jeditHomeDir = null;
    private File pluginHomeDir = null;
    private File rlibsDir = null;
    private File rHomeDir = null;
		// private File rJavaDir = null;
		// private String rLibPaths = null;
    private File shortcutDir = null;
    private File startScript;
    private boolean forcePackInstall;

    public static final String RSCRIPT_NAME = "orchestra_starter.r";
    public static final String PROPERTY_NAME = "orchestra_properties.txt";
    public static final String ICON_NAME_WINDOWS = "orchestra_icon.ico";
    public static final String ICON_NAME_LINUX = "orchestra_icon.png";
    public static final String R_LIBS_DIR_NAME = "library";

    public Installer(String jeditHomeDir, String pluginHomeDir,
                     String rHomeDir, String shortcutDir, boolean forcePackInstall) {
        this.jeditHomeDir = new File(jeditHomeDir);
        this.pluginHomeDir = new File(pluginHomeDir);
        this.rHomeDir = new File(rHomeDir);
        this.rlibsDir = new File(pluginHomeDir, R_LIBS_DIR_NAME);
        // null means no shortcut
        if (shortcutDir != null)
            this.shortcutDir = new File(shortcutDir);
        else
            this.shortcutDir = null;
        this.forcePackInstall = forcePackInstall;
        logger.info("Installer was started with:");
        logger.info("JEDIT_HOME:" + this.jeditHomeDir.getAbsolutePath());
        logger.info("PLUGIN_HOME:" + this.pluginHomeDir.getAbsolutePath());
        logger.info("R_HOME:" + this.rHomeDir.getAbsolutePath());
        logger.info("Shortcut dir:" + this.shortcutDir);
    }

    private File extractRScript() throws IOException {
        logger.info("Extracting R starter script to: " + pluginHomeDir);
        File f = FileTransfer.copyResourceToLocalDir(this.getClass().getResource("/"+RSCRIPT_NAME),
                RSCRIPT_NAME, pluginHomeDir);
        logger.info("Extracting script done.");
        return f;
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
            
            if( !rlibsDir.exists() ){
            	logger.info( "creating R library in plugin home: '" + pluginHomeStr + "/library" ) ;
            	rlibsDir.mkdirs();
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

    private void createShortcut(File script) throws IOException{
        File rExe = new File(new File(rHomeDir, "bin"), "Rscript");
        DesktopShortcut dsc;
        File iconFile;
        if (OSTools.isWindows()) {
            dsc = new WindowsDesktopShortcut(shortcutDir, "Orchestra", rExe);
            iconFile = new File(pluginHomeDir, ICON_NAME_WINDOWS);
        } else {
            dsc = new FreeDesktopShortcut(shortcutDir, "Orchestra", rExe);
            iconFile = new File(pluginHomeDir, ICON_NAME_LINUX);
        }
        logger.info("Shortcut target is: " + rExe.getAbsolutePath());
        dsc.addParameter("--vanilla");
        dsc.addParameter("--default-packages=\"base\"");
        dsc.addParameter(script);
        logger.info("Shortcut icon is: " + iconFile);
        dsc.setIconpath(iconFile);
        dsc.setWorkingDir(pluginHomeDir);
        dsc.create();
    }

    private String forwardSlashes(String s) {
        return s.replace("\\", "/");
    }

    private String forwardSlashes(File f) {
        return forwardSlashes(f.getAbsolutePath());
    }

    protected void onSuccess(Void result) {
    }

    @Override
    protected void onFailure(Throwable t) {
        publish(t.toString());
        logger.debug("Failure in Installer!", t);
    }

    private void logAndPublish(String s) {
        logger.info(s);
        publish(s);
    }

    protected void installRPackage() throws RCmdBatchException, CantFindPackageException {
        RCmdBatch rCmdBatch = new RCmdBatch(rHomeDir);
        RPackage rp = null;
        if (forcePackInstall) {
            logAndPublish("Forcing installation of Orchestra R package.");
        } else {
            logAndPublish("Checking Orchestra R package.");
            rp = rCmdBatch.getInstalledPackInfo("orchestra");
            if (rp == null)
                logAndPublish("Orchestra R package not found in current R installation.");
            else
                logAndPublish("Orchestra R package already installed.");
        }
        setProgress(20);
        if (forcePackInstall || rp == null) {
            logAndPublish("Trying to install from CRAN.");
            try {
                rCmdBatch.installCranPackage("orchestra", rlibsDir);
                setProgress(40);
            } catch (CantFindPackageException e) {
                logAndPublish("CRAN: Orchestra R package was not found, trying to install from R-Forge.");
                rCmdBatch.installRForgePackage("orchestra", rlibsDir);
            }
        }
        setProgress(50);
    }

    protected Void doInBackground() throws Exception {
        publish("Checking orchestra R package...");
        setProgress(10);
        installRPackage();
        setProgress(60);
        extractPropertyFile();
        publish("Start property file extracted.");
        setProgress(70);
        startScript = extractRScript();
        publish("Start script extracted.");
        setProgress(80);
        if (shortcutDir != null) {
            extractOrchestraIcons();
            publish("Desktop icons extracted.");
            setProgress(90);
            logger.info("Creating shortcut entry in " + shortcutDir.getAbsolutePath());
            createShortcut(startScript);
            logger.info("Shortcut done.");
            publish("Desktop link created.");
        }
        setProgress(100);
        publish("Done.");
        return null;
    }

    public File getShortcutDir() {
        return shortcutDir;
    }

    public File getStartScript() {
        return startScript;
    }
}
