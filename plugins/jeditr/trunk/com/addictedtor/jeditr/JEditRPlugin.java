package com.addictedtor.jeditr;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditPlugin; 
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import java.io.File;



/**
 * Main class of the jeditr installer plugin
 *
 * @author Bernd Bischl <bernd_bischl@gmx.net>
 * @author Romain Francois <francoisromain@free.fr>
 */
public class JEditRPlugin extends EBPlugin {

    public static final String NAME = "JEditRPlugin";

    // log4j
    public static final String LOG_FILENAME = "jeditr_installer.log";


    // must be public for jedit, but dont call this!
    public JEditRPlugin() {
    }

    /**
     * Start method of the plugin. This is where the magic takes place
     */
    @Override
    public void start() {
        String jeditr_home = System.getProperty("jeditr.home", "") ;
        if( jeditr_home.equals("") ){
            startInstallerPlugin();
        } else {
            // load the jeditr plugin from the R package tree
            String jar = jeditr_home + "/java/R.jar" ;
            if((new File( jar ) ).exists() ){
                jEdit.addPluginJAR( jar ) ;
            } else {
                //todo better handling?
                JOptionPane.showMessageDialog(jEdit.getActiveView(), "JEditR: Cannot find R.jar. Please install the jeditr R package and then run the installer plugin for jedit again!");
            }
        }
    }

    private void startInstallerPlugin() {
        // configure log4j
        File log = new File(getPluginHome(), LOG_FILENAME);
        Logger rootLogger = Logger.getRootLogger();
        FileAppender fa = new FileAppender();
        fa.setFile(log.getAbsolutePath());
        fa.setLayout(new PatternLayout("%-5p - %m%n       [%t] (%c:%M at %F:%L)%n"));
        fa.setAppend(false);
        rootLogger.addAppender(fa);
        fa.activateOptions();
        rootLogger.info("jeditr plugin started!");
        rootLogger.info("log4j configured.");
        rootLogger.info("log goes to: " + log.getAbsolutePath());
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
    	return EditPlugin.getPluginHome( JEditRPlugin.class ).getAbsolutePath(); 
    }

}

