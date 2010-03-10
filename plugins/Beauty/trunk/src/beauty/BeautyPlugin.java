
package beauty;


import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.swing.*;

import beauty.beautifiers.*;


public class BeautyPlugin extends EditPlugin {

    private static Properties modeProperties;

    public void start() {
        loadProperties();
        registerServices();        
    }
    
    public static void registerServices() {
        // read the custom mode beautifiers file and dynamically add services
        // for any defined beautifiers
        PluginJAR jar = jEdit.getPlugin( "beauty.BeautyPlugin" ).getPluginJAR();
        for ( Object k : modeProperties.keySet() ) {
            if ( k == null ) {
                continue;
            }
            String modeName = ( String ) k;
            modeName = modeName.substring(0, modeName.indexOf( '.' ));
            ServiceManager.registerService( "beauty.beautifiers.Beautifier", modeName + ".custom", "new beauty.beautifiers.DefaultBeautifier(\"" + modeName + "\")", jar );
        }
    }
    
    public void stop() {
        saveProperties();   
    }

    private static void loadProperties() {
        modeProperties = new Properties();
        try {
            File homeDir = jEdit.getPlugin( "beauty.BeautyPlugin" ).getPluginHome();
            File customFile = new File( homeDir, "custom_beautifiers.properties" );
            Reader reader = new BufferedReader( new FileReader( customFile ) );
            modeProperties.load( reader );
            reader.close();
        }
        catch ( Exception ignored ) {      // NOPMD
        }
    }
    
    public static Properties getProperties() {
        return modeProperties;   
    }
    
    public static Properties getCustomModeProperties(String modeName) {
        Properties props = new Properties();
        for (Object k : modeProperties.keySet()) {
            if (k == null) {
                continue;   
            }
            String key = (String)k;
            String value = modeProperties.getProperty(key);
            String comp = key.substring( 0, key.indexOf( '.' ) );
            key = key.substring(key.indexOf('.') + 1);
            if (comp.equals(modeName)) {
                props.setProperty(key, value);   
            }
        }
        return props;
    }

    public static void saveProperties() {
        if ( modeProperties == null ) {
            return ;
        }
        try {
            File homeDir = jEdit.getPlugin( "beauty.BeautyPlugin" ).getPluginHome();
            if ( !homeDir.exists() ) {
                homeDir.mkdir();
            }
            File customFile = new File( homeDir, "custom_beautifiers.properties" );
            Writer writer = new BufferedWriter( new FileWriter( customFile ) );
            modeProperties.store( writer, "Properties for custom mode beautifiers" );
            writer.flush();
            writer.close();
        }
        catch ( Exception ignored ) {      // NOPMD
        }
    }

    /**
     * Beautify the current buffer using Beauty.
     *
     * @param buffer  The buffer to be beautified.
     * @param view  The view; may be null, if there is no current view.
     * @param showErrorDialogs  If true, modal error dialogs will be shown
     *        on error. Otherwise, the errors are silently logged.
     */
    public static void beautify( Buffer buffer, View view, boolean showErrorDialogs ) {
        if ( buffer.isReadOnly() ) {
            Log.log( Log.NOTICE, BeautyPlugin.class, jEdit.getProperty( "beauty.error.isReadOnly.message" ) );
            if ( showErrorDialogs ) {
                GUIUtilities.error( view, "beauty.error.isReadOnly", null );
            }
            return ;
        }

        // load beautifier
        String mode = buffer.getStringProperty( "beauty.beautifier" );
        if ( mode == null )
            mode = buffer.getMode().getName();
        Beautifier beautifier = ( Beautifier ) ServiceManager.getService( Beautifier.SERVICE_NAME, mode );
        if ( beautifier == null ) {
            if ( jEdit.getBooleanProperty( "beauty.useBuiltInIndenter", false ) ) {
                indentLines( view );
                return ;
            }
            else {
                if ( showErrorDialogs ) {
                    JOptionPane.showMessageDialog( view, "Error: can't beautify this buffer because I don't know how to handle this mode.",
                            "Beauty Error", JOptionPane.ERROR_MESSAGE );
                    return ;
                }
                else {
                    Log.log( Log.NOTICE, BeautyPlugin.class, "buffer " + buffer.getName()
                            + " not beautified, because mode is not supported." );
                    return ;
                }
            }
        }

        // run the format routine synchronously on the AWT thread
        VFSManager.runInAWTThread( new BeautyThread( buffer, view, showErrorDialogs, beautifier ) );

    }

    static void indentLines( View view ) {
        JEditTextArea ta = view.getEditPane().getTextArea();
        int cp = ta.getCaretPosition();
        ta.selectAll();
        EditAction action = jEdit.getAction( "indent-lines" );
        action.invoke( view );
        ta.selectNone();
        restoreCaretPosition( view.getEditPane(), cp );
    }

    static void restoreCaretPosition( EditPane editPane, int caretPosition ) {
        final EditPane ep = editPane;
        final int offset = Math.min( caretPosition, editPane.getTextArea().getBufferLength() );
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    ep.getTextArea().setCaretPosition( offset, true );
                    ep.getTextArea().scrollToCaret( true );
                }
            }
        );
    }

    public static void toggleSplitAttributes( View view ) {
        boolean split = jEdit.getBooleanProperty( "xmlindenter.splitAttributes", false );
        jEdit.setBooleanProperty( "xmlindenter.splitAttributes", !split );
        beautify( view.getBuffer(), view, true );
    }

}