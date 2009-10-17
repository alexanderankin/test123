package com.addictedtor.orchestra ;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.options.PluginOptions;

/**
 * Main class of the orchestra installer plugin
 *
 * @author Bernd Bischl <bernd_bischl@gmx.net>
 * @author Romain Francois <francoisromain@free.fr>
 */
public class OrchestraPlugin extends EBPlugin {

    public static final String NAME = "OrchestraPlugin";

    // log4j
    public static final String LOG_FILENAME = "orchestra_installer.log";
    protected static final Log logger = LogFactory.getLog(OrchestraPlugin.class);

    // must be public for jedit, but dont call this!
    public OrchestraPlugin() {}

    /**
     * Start method of the plugin. This is where the magic takes place
     */
    @Override
    public void start() {
    	configureLog4J();
        
    	OrchestraModes modes = new OrchestraModes() ;
    	try{
    		modes.deployModes() ;
    	} catch( IOException e){}
    	modes.loadModes() ;

    	String orchestra_rpackage_home = System.getProperty("orchestra.home", "") ;
        if( orchestra_rpackage_home.equals("") ){
        	if( isConfigured() ){
        		/* the system is already installed but jedit started normally, don't mess it up */
        	} else{
        		startInstallerPlugin();
            }
        	
        } else {
            // load the orchestra plugin from the R package tree
            String jar = orchestra_rpackage_home + "/java/R.jar" ;
            if((new File( jar ) ).exists() ){
                jEdit.addPluginJAR( jar ) ;
            } else {
                //todo better handling?
                JOptionPane.showMessageDialog(jEdit.getActiveView(), "Orchestra: Cannot find R.jar. Please install the orchestra R package and then run the installer plugin for jedit again!");
            }
        }
    }

    private void configureLog4J(){
        // configure log4j
        File log = new File(getPluginHome(), LOG_FILENAME);
        Logger rootLogger = Logger.getRootLogger();
        FileAppender fa = new FileAppender();
        fa.setFile(log.getAbsolutePath());
        fa.setLayout(new PatternLayout("%-5p - %m%n       [%t] (%c:%M at %F:%L)%n"));
        fa.setAppend(false);
        rootLogger.addAppender(fa);
        fa.activateOptions();
        rootLogger.info("orchestra plugin started!");
        rootLogger.info("log4j configured.");
        rootLogger.info("log goes to: " + log.getAbsolutePath());
    }

    private void startInstallerPlugin() {
//        // show the option dialog
        Thread t = new Thread() {
            @Override
            public void run() {
                while(!jEdit.isStartupDone()) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        logger.error("Interrupted waiter thread for Orchestra plugin!", e);
                    }
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new PluginOptions(jEdit.getActiveView(), "orchestra");
                    }
                });

            }
        };
        t.start();
    }

    /**
     * Stop method of the plugin. Placeholder at the moment.
     */
    @Override
    public void stop() {
    }

    public void handleMessage(EBMessage message) {
        // placeholder
    }
    
    /**
     * Returns the home of this plugin
     */ 
    public static String getPluginHomePath(){
    	return EditPlugin.getPluginHome( OrchestraPlugin.class ).getAbsolutePath(); 
    }

    /**
     * Is the plugin configured, ie does the startup script exist
     * @return true if the startup script exists
     */
    public static boolean isConfigured(){
    	File startup = new File( getPluginHomePath(), Installer.RSCRIPT_NAME) ;
    	return startup.exists() ;
    }
    
}

