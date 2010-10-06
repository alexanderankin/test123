package ise.plugin.bmp;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;

import org.gjt.sp.jedit.buffer.FoldHandler;

import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.EditorExitRequested;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;

//import org.gjt.sp.util.Log;

/**
 * This plugin stores buffer-local properties in a file and restores those
 * setting when the file is next opened. The settings are stored as a pipe
 * separated string:
 * <ul>
 * <li>getStringProperty("lineSeparator")        Line separator string, values n, r, rn
 * <li>buffer.getStringProperty(Buffer.ENCODING) Character encoding string
 * <li>buffer.getBooleanProperty(Buffer.GZIPPED) gzip on disk boolean, values t, f
 * <li>buffer.getMode().getName()                edit mode string
 * <li>buffer.getFoldHandler().getName()         fold mode string
 * <li>buffer.getStringProperty("wrap");         word wrap string
 * <li>buffer.getIntProperty("maxLineLength");   wrap width int
 * <li>buffer.getIntProperty("tabSize")          tab width int
 * <li>buffer.getIntProperty("indentSize")       indent width int
 * <li>buffer.getBooleanProperty("noTabs")       soft tabs boolean, t = soft tabs, f = hard tabs
 * </ul>
 * <p>
 * example:n|ISO-8859-1|f|java|indent|none|76|3|3|t
 *         n|ISO-8859-1|f|    |      |none|0|4|4|t
 * <p>
 * TODO: need to check how this works with files loaded with the ftp plugin
 * <br>
 * DID: seems to work okay with ftp, need to test some more
 * <p>
 * Jan 5, 2004, per request from Slava: removed
 * persistence of line separator and encoding. Kept the string format as above,
 * but implementation now does not actually use line separator and encoding
 * settings.
 *
 * This class implements WindowListener, then is attached to each view so that
 * the auto-close timer only applies when the window is actually active.  If
 * the window is deactivated or iconified, the timer is stopped.
 *
 * @author    Dale Anson, danson@germane-software.com
 * @version   $Revision$
 * @since     Oct 1, 2003
 */
public class BufferLocalPlugin extends EBPlugin implements WindowListener {

    // runs once every 10 minutes at a low priority to clean up the map
    Thread janitor = createJanitorThread();

    private Thread createJanitorThread() {
        return new Thread() {
                   public void run() {
                       setPriority( Thread.MIN_PRIORITY );
                       while ( true ) {
                           if ( canClean && map.size() > 0 ) {
                               synchronized ( map ) {
                                   try {
                                       // do 2 loops to avoid ConcurrentModificationExceptions
                                       List to_remove = new ArrayList();
                                       Iterator it = map.keySet().iterator();
                                       while ( it.hasNext() ) {
                                           String filename = ( String ) it.next();
                                           File f = new File( filename );
                                           if ( !f.exists() ) {
                                               to_remove.add( filename );
                                           }
                                       }
                                       it = to_remove.iterator();
                                       while ( it.hasNext() ) {
                                           map.remove( it.next() );
                                       }
                                   }
                                   catch ( Exception e ) {     // NOPMD
                                       // ignored
                                   }
                               }
                           }
                           try {
                               sleep( ( long ) TEN_MINUTES );
                           }
                           catch ( InterruptedException e ) {
                               // ignored
                           }
                       }
                   }
               };
    }

    // runs once every staleTime minutes at a low priority to auto-close stale buffers
    Thread bufferCleaner = createBufferCleanerThread();

    private Thread createBufferCleanerThread() {
        return new Thread() {
                   public void run() {
                       setPriority( Thread.MIN_PRIORITY );
                       while ( removeStale ) {
                           try {
                               if ( canClose ) {
                                   closeFiles();
                               }
                               sleep( ( long ) staleTime );
                           }
                           catch ( InterruptedException e ) {
                               // ignored
                           }
                       }
                   }
               };
    }

    private int ONE_MINUTE = 1000 * 60;
    private int TEN_MINUTES = ONE_MINUTE * 10;

    private int staleTime = 30 * ONE_MINUTE;
    private boolean removeStale = false;

    // storage for the properties, key is filename as a String,
    // value is the property settings String, see above
    private Properties map = new Properties();

    // temporary storage for properties. Properties are stored here, then moved
    // to permanent storage when they actually change.
    private Properties tempMap = new Properties();

    // storage for open buffers, key is filename as a String,
    // value is a BufferReference object
    private HashMap<String, BufferReference> openBuffers = new HashMap<String, BufferReference>();

    // control for janitor thread.
    private boolean canClean;

    // control for buffer cleaner thread
    private boolean canClose = true;

    private Date pausedAt = null;

    public static String NAME = "bufferlocal";

    private File configFile = null;

    /**
     * Load the stored buffer local properties. The properties are stored in a
     * file named .bufferlocalplugin.cfg in either the jEdit settings directory
     * (if writable) otherwise, in $user.home.
     */
    public void start() {
        // load settings from jEdit properties
        loadProperties();

        // load configuration setings
        // Previously, this file was stored either in the jEdit settings directory
        // or user.home.  Now it is stored in plugin home.  For backward compatibility,
        // first check plugin home.  If the config file is there, assume it has already
        // been migrated.  If not, check settings directory and user home and copy it
        // to plugin home, then use it.  Once done, delete the old file.
        try {
            File homeDir = jEdit.getPlugin( "ise.plugin.bmp.BufferLocalPlugin" ).getPluginHome();
            homeDir.mkdir();
            configFile = new File( homeDir, ".bufferlocalplugin.cfg" );
            if ( configFile.exists() ) {
                BufferedInputStream in = new BufferedInputStream( new FileInputStream( configFile ) );
                map.load( in );
                in.close();
            }
            else {
                String oldDir = jEdit.getSettingsDirectory();
                if ( oldDir == null ) {
                    oldDir = System.getProperty( "user.home" );
                }
                configFile = new File( oldDir, ".bufferlocalplugin.cfg" );
                if ( configFile.exists() ) {
                    BufferedInputStream in = new BufferedInputStream( new FileInputStream( configFile ) );
                    map.load( in );
                    in.close();
                    // delete the old file and write out the new file
                    configFile.delete();
                    configFile = new File( homeDir, ".bufferlocalplugin.cfg" );
                    synchronized ( map ) {
                        BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( configFile ) );
                        map.store( out, "Machine generated for BufferLocalPlugin, DO NOT EDIT!" );
                        out.flush();
                        out.close();
                    }
                }
            }
        }
        catch (Exception e) {       // NOPMD
            // ignored
        }

        // TODO: attach to open Views
        for ( View view : jEdit.getViews() ) {
            view.addWindowListener( this );
        }

        // prep for currently open buffers
        initOpenBuffers();

        // start janitor and cleaner threads
        canClean = true;
        restartThreads();
    }

    private void restartThreads() {
        if ( janitor.isAlive() ) {
            janitor.interrupt();
        }
        janitor = createJanitorThread();
        janitor.start();
        if ( bufferCleaner.isAlive() ) {
            bufferCleaner.interrupt();
        }
        bufferCleaner = createBufferCleanerThread();
        bufferCleaner.start();
    }

    /**
     * Save the buffer local properties to disk.
     *
     * @see   start
     */
    public void stop() {
        canClean = false;
        janitor.interrupt();
        bufferCleaner.interrupt();
        if ( configFile == null || !configFile.exists() ) {
            File homeDir = jEdit.getPlugin( "ise.plugin.bmp.BufferLocalPlugin" ).getPluginHome();
            homeDir.mkdir();
            configFile = new File( homeDir, ".bufferlocalplugin.cfg" );
        }
        try {
            synchronized ( map ) {
                BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( configFile ) );
                map.store( out, "Machine generated for BufferLocalPlugin, DO NOT EDIT!" );
                out.flush();
                out.close();
            }
        }
        catch ( Exception e ) {     // NOPMD
            // ignored
        }

        // detach from open views
        for ( View view : jEdit.getViews() ) {
            view.removeWindowListener( this );
        }
    }

    /**
     * Check for BufferUpdate messages. Save properties on CLOSED, restore
     * properties on LOADED.
     *
     * @param message
     */
    public void handleMessage( EBMessage message ) {
        if ( message instanceof BufferUpdate ) {
            BufferUpdate bu = ( BufferUpdate ) message;
            Object what = bu.getWhat();
            Buffer buffer = bu.getBuffer();
            if ( buffer == null ) {
                return ;
            }
            String file = buffer.getPath();
            if ( BufferUpdate.LOADED.equals( what ) || BufferUpdate.SAVED.equals( what ) ) {
                String props = map.getProperty( file );
                if ( props != null ) {
                    // parse the stored properties
                    String[] tokens = props.split( "[\\|]" );
                    String ls = tokens[ 0 ];
                    String enc = tokens[ 1 ];
                    String gz = tokens[ 2 ];
                    String em = tokens[ 3 ];
                    String fm = tokens[ 4 ];
                    String wm = tokens[ 5 ];
                    String ll = tokens[ 6 ];
                    String tw = tokens[ 7 ];
                    String iw = tokens[ 8 ];
                    String tabs = tokens[ 9 ];

                    // apply the stored properties to the buffer
                    /// see comments above, don't need this right now
                    /// 13 Dec 2005, more comments, looks like there is a use case for this
                    /// stuff after all
                    if ( "n".equals( ls ) ) {
                        ls = "\n";
                    }
                    else if ( "r".equals( ls ) ) {
                        ls = "\r";
                    }
                    else {
                        ls = "\r\n";
                    }
                    buffer.setStringProperty( "lineSeparator", ls );
                    buffer.setStringProperty( Buffer.ENCODING, enc );
                    ///

                    if ( gz != null && gz.length() > 0 )
                        buffer.setBooleanProperty( Buffer.GZIPPED, "t".equals( gz ) ? true : false );
                    if ( fm != null && fm.length() > 0 && FoldHandler.getFoldHandler( fm ) != null )
                        buffer.setFoldHandler( FoldHandler.getFoldHandler( fm ) );
                    if ( wm != null && wm.length() > 0 )
                        buffer.setStringProperty( "wrap", wm );
                    if ( ll != null && ll.length() > 0 )
                        buffer.setIntegerProperty( "maxLineLength", Integer.parseInt( ll ) );
                    if ( tw != null && tw.length() > 0 )
                        buffer.setIntegerProperty( "tabSize", Integer.parseInt( tw ) );
                    if ( iw != null && iw.length() > 0 )
                        buffer.setIntegerProperty( "indentSize", Integer.parseInt( iw ) );
                    if ( tabs != null && tabs.length() > 0 )
                        buffer.setBooleanProperty( "noTabs", "t".equals( tabs ) ? true : false );
                    if ( em != null && em.length() > 0 )
                        buffer.setMode( em );
                }
                else {
                    // on load, if we don't already have properties stored for this file,
                    // stash the string to check against when the file is closed.
                    tempMap.setProperty( file, getBufferLocalString( buffer ) );
                }
                View view = bu.getView();
                if ( view == null ) {
                    view = jEdit.getActiveView();
                }
                openBuffers.put( buffer.getPath(), new BufferReference( view, buffer ) );
            }
            else if ( BufferUpdate.CLOSED.equals( what ) || BufferUpdate.PROPERTIES_CHANGED.equals( what ) ) {
                String bufferLocalString = getBufferLocalString(buffer);
                map.setProperty( file, bufferLocalString );
                if ( BufferUpdate.CLOSED.equals( what ) ) {
                    openBuffers.remove( buffer.getPath() );
                }
            }
        }
        else if ( message instanceof EditorExitRequested ) {
            // jEdit may be shutting down, so update the map for any buffers still open.
            // Oddly enough, jEdit doesn't send CLOSED messages as it closes buffers
            // during shutdown. Or maybe it does, but this plugin is unloaded before
            // getting those messages.
            Buffer[] buffers = jEdit.getBuffers();
            String file;
            String props;
            String tempProps;
            for ( int i = 0; i < buffers.length; i++ ) {
                file = buffers[ i ].getPath();
                // only save if changed, no need to save if not. Doing it this way
                // rather than on PROPERTY_CHANGED as jEdit sends lots of
                // PROPERTY_CHANGED messages even though the properties really
                // haven't changed
                props = getBufferLocalString( buffers[ i ] );
                tempProps = tempMap.getProperty( file );
                if ( tempProps == null ) {
                    continue;
                }
                if ( !props.equals( tempProps ) ) {
                    map.setProperty( file, props );
                }
            }
        }
        else if ( message instanceof EditPaneUpdate ) {
            // populate the openBuffers list
            EditPaneUpdate epu = ( EditPaneUpdate ) message;
            Object what = epu.getWhat();
            if ( EditPaneUpdate.BUFFER_CHANGED.equals( what ) ) {
                View view = epu.getEditPane().getView();
                Buffer buffer = epu.getEditPane().getBuffer();
                openBuffers.put( buffer.getPath(), new BufferReference( view, buffer ) );
            }
        }
        else if ( message instanceof ViewUpdate ) {
            ViewUpdate vu = ( ViewUpdate ) message;
            if ( ViewUpdate.CREATED.equals( vu.getWhat() ) ) {
                initView( vu.getView() );
                vu.getView().addWindowListener( this );
            }
            else if ( ViewUpdate.CLOSED.equals( vu.getWhat() ) ) {
                vu.getView().removeWindowListener( this );
            }
        }
        else if ( message instanceof PropertiesChanged ) {
            loadProperties();
        }
    }

    /** Closes stale files right now.  */
    public void closeFiles() {
        if ( openBuffers.size() > 0 ) {
            synchronized ( openBuffers ) {
                try {
                    // do 2 loops to avoid ConcurrentModificationExceptions
                    Calendar stale_time = Calendar.getInstance();
                    stale_time.add( Calendar.MILLISECOND, -staleTime );
                    Iterator it = openBuffers.keySet().iterator();
                    List to_close = new ArrayList();
                    while ( it.hasNext() ) {
                        String path = ( String ) it.next();
                        BufferReference br = ( BufferReference ) openBuffers.get( path );
                        Calendar viewed = br.getViewed();
                        View view = br.getView();
                        if ( view == null ) {
                            view = jEdit.getActiveView();
                        }
                        Buffer buffer = br.getBuffer();
                        if ( buffer.equals( view.getEditPane().getBuffer() ) ) {
                            // don't close the current buffer, even though it may
                            // been open for more than stale time
                            continue;
                        }
                        if ( buffer.isDirty() ) {
                            continue;   // don't auto-close a dirty buffer
                        }
                        if ( viewed.before( stale_time ) ) {
                            to_close.add( br );
                        }
                    }
                    it = to_close.iterator();
                    while ( it.hasNext() ) {
                        BufferReference br = ( BufferReference ) it.next();
                        View view = br.getView();
                        if ( view == null ) {
                            view = jEdit.getActiveView();
                        }
                        Buffer buffer = br.getBuffer();
                        if ( jEdit.closeBuffer( view, buffer ) ) {
                            openBuffers.remove( buffer.getPath() );
                        }
                    }
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                    // ignored
                }
            }
        }
    }

    /**
     * @param buffer  the buffer to get properties for
     * @return        a string representing the buffer-local properties. See
     *      explanation and example of string at the top of this file.
     */
    private String getBufferLocalString( Buffer buffer ) {
        // get the properties
        String ls = buffer.getStringProperty( "lineSeparator" );
        String enc = buffer.getStringProperty( Buffer.ENCODING );
        boolean gz = buffer.getBooleanProperty( Buffer.GZIPPED );
        String em;
        if ( buffer.getMode() != null )
            em = buffer.getMode().getName();
        else
            em = "";
        String fm = buffer.getFoldHandler() == null ? "" : buffer.getFoldHandler().getName();
        String wm = buffer.getStringProperty( "wrap" );
        int ll = buffer.getIntegerProperty( "maxLineLength", 0 );
        int tw = buffer.getIntegerProperty( "tabSize", 3 );
        int iw = buffer.getIntegerProperty( "indentSize", 3 );
        boolean tabs = buffer.getBooleanProperty( "noTabs" );

        // build the string
        StringBuffer prop = new StringBuffer();
        if ( "\n".equals( ls ) ) {
            prop.append( "n|" );
        }
        else if ( "\r".equals( ls ) ) {
            prop.append( "r|" );
        }
        else {
            prop.append( "rn|" );
        }
        prop.append( enc ).append( '|' );
        prop.append( gz ? "t|" : "f|" );
        prop.append( em ).append( '|' );
        prop.append( fm ).append( '|' );
        prop.append( wm ).append( '|' );
        prop.append( String.valueOf( ll ) ).append( '|' );
        prop.append( String.valueOf( tw ) ).append( '|' );
        prop.append( String.valueOf( iw ) ).append( '|' );
        prop.append( tabs ? 't' : 'f' );

        return prop.toString();
    }

    /**
     * Reads the properties for this plugin from the jEdit properties. Currently,
     * there are 2 properties, "bufferlocal.staleTime", which is the number of
     * minutes that a file can remain open without being used before it will be
     * closed, and "bufferlocal.removeStale" which is a boolean to decide if
     * stale files should be closed after the staleTime has been reached.
     */
    private void loadProperties() {
        int newStaleTime = jEdit.getIntegerProperty( NAME + ".staleTime", staleTime ) * ONE_MINUTE;
        boolean newRemoveStale = jEdit.getBooleanProperty( NAME + ".removeStale", removeStale );
        if ( newStaleTime != staleTime || newRemoveStale != removeStale ) {
            staleTime = newStaleTime;
            removeStale = newRemoveStale;
            restartThreads();
        }
    }

    /**
     * Gets a list of the currently open buffers and populates openBuffers.
     */
    private void initOpenBuffers() {
        for ( View view : jEdit.getViews() ) {
            initView( view );
        }
    }

    private void initView( View view ) {
        for ( EditPane editPane : view.getEditPanes() ) {
            for ( Buffer buffer : editPane.getBufferSet().getAllBuffers() ) {
                BufferReference br = new BufferReference( view, buffer );
                openBuffers.put( buffer.getPath(), br );
            }
        }
    }


    /**
     * A data object to track buffers
     * @version   $Revision$
     */
    public class BufferReference {
        private View view;
        private Buffer buffer;
        private Calendar viewed;

        /**
         * Constructor for BufferReference
         *
         * @param view
         * @param buffer
         */
        public BufferReference( View view, Buffer buffer ) {
            this.view = view;
            this.buffer = buffer;
            viewed = Calendar.getInstance();
        }

        /** Sets the viewed attribute of the BufferReference object */
        public void setViewed() {
            viewed = Calendar.getInstance();
        }

        /**
         * Gets the view attribute of the BufferReference object
         *
         * @return   The view value
         */
        public View getView() {
            return view;
        }

        /**
         * Gets the buffer attribute of the BufferReference object
         *
         * @return   The buffer value
         */
        public Buffer getBuffer() {
            return buffer;
        }

        /**
         * Gets the viewed attribute of the BufferReference object
         *
         * @return   The viewed value
         */
        public Calendar getViewed() {
            return viewed;
        }

        /**
         * @return   Description of the Returned Value
         */
        public String toString() {
            StringBuffer sb = new StringBuffer( 50 );
            sb.append( "BufferReference[" );
            sb.append( buffer.getPath() ).append( ',' );
            sb.append( viewed.getTime().toString() ).append( ']' );
            return sb.toString();
        }
    }

    private void adjustForPause() {
        if ( jEdit.getBooleanProperty( "bufferlocal.whileActive" ) && pausedAt != null ) {
            Date now = new Date();
            long pausedFor = now.getTime() - pausedAt.getTime();
            pausedAt = null;
            for ( BufferReference br : openBuffers.values() ) {
                br.getViewed().add( Calendar.MILLISECOND, ( int ) pausedFor );
            }
        }
    }

    public void windowActivated( WindowEvent e ) {
        adjustForPause();
        canClose = true;
    }
    public void windowClosed( WindowEvent e ) {
        canClose = false;
        pausedAt = new Date();
    }
    public void windowDeactivated( WindowEvent e ) {
        canClose = false;
        pausedAt = new Date();
    }
    public void windowDeiconified( WindowEvent e ) {
        adjustForPause();
        canClose = true;
    }
    public void windowIconified( WindowEvent e ) {
        canClose = false;
        pausedAt = new Date();
    }
    public void windowGainedFocus( WindowEvent e ) {
        adjustForPause();
        canClose = true;
    }
    public void windowLostFocus( WindowEvent e ) {
        canClose = false;
        pausedAt = new Date();
    }
    public void windowClosing( WindowEvent e ) {}
    public void windowOpened( WindowEvent e ) {}
    public void windowStateChanged( WindowEvent e ) {}

}