package ise.plugin.bmp;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;

import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;

//import org.gjt.sp.util.Log;

/**
 * BufferMinder checks how long buffers have been opened without being used and
 * automatically closes them after a certain time period.
 *
 * This class implements WindowListener, then is attached to each view so that
 * the auto-close timer only applies when the window is actually active.  If
 * the window is deactivated or iconified, the timer is stopped.
 *
 * @author    Dale Anson, danson@germane-software.com
 * @version   $Revision: 18720 $
 * @since     Oct 1, 2003
 */
public class BufferMinder implements WindowListener {
    
    // runs once every staleTime minutes at a low priority to auto-close stale buffers
    private Thread bufferCleaner;

    public static final int ONE_MINUTE = 1000 * 60;
    private int staleTime = 30 * ONE_MINUTE;
    private boolean removeStale = false;

    // storage for open buffers, key is filename as a String,
    // value is a BufferReference object
    private HashMap<String, BufferReference> openBuffers = new HashMap<String, BufferReference>();

    // control for buffer cleaner thread
    private boolean canClose = true;

    private Date pausedAt = null;


    public BufferMinder() {
        bufferCleaner = createBufferCleanerThread();

        for ( View view : jEdit.getViews() ) {
            view.addWindowListener( this );
        }

        // prep for currently open buffers
        initOpenBuffers();

        // start cleaner threads
        restartCleaner();
    }

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


    private void restartCleaner() {
        if ( bufferCleaner.isAlive() ) {
            bufferCleaner.interrupt();
        }
        bufferCleaner = createBufferCleanerThread();
        bufferCleaner.start();
    }

    /**
     * Stop cleaning.
     */
    public void stop() {
        bufferCleaner.interrupt();

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
                View view = bu.getView();
                if ( view == null ) {
                    view = jEdit.getActiveView();
                }
                openBuffers.put( buffer.getPath(), new BufferReference( view, buffer ) );
            }
            else if ( BufferUpdate.CLOSED.equals( what ) || BufferUpdate.PROPERTIES_CHANGED.equals( what ) ) {
                if ( BufferUpdate.CLOSED.equals( what ) ) {
                    openBuffers.remove( buffer.getPath() );
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
                // TODO: clean out the openBuffers map
                vu.getView().removeWindowListener( this );
            }
        }
        else if ( message instanceof PropertiesChanged ) {
            loadProperties();
        }
    }

    /**
     * Reads the properties for this plugin from the jEdit properties. Currently,
     * there are 2 properties, "bufferlocal.staleTime", which is the number of
     * minutes that a file can remain open without being used before it will be
     * closed, and "bufferlocal.removeStale" which is a boolean to decide if
     * stale files should be closed after the staleTime has been reached.
     */
    private void loadProperties() {
        int newStaleTime = jEdit.getIntegerProperty( BufferLocalPlugin.NAME + ".staleTime", staleTime ) * ONE_MINUTE;
        boolean newRemoveStale = jEdit.getBooleanProperty( BufferLocalPlugin.NAME + ".removeStale", removeStale );
        if ( newStaleTime != staleTime || newRemoveStale != removeStale ) {
            staleTime = newStaleTime;
            removeStale = newRemoveStale;
            restartCleaner();
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
                    List<BufferReference> toClose = new ArrayList<BufferReference>();
                    for (String path : openBuffers.keySet() ) {
                        BufferReference br = ( BufferReference ) openBuffers.get( path );
                        Calendar viewed = br.getViewed();
                        View view = br.getView();
                        if ( view == null ) {
                            view = jEdit.getActiveView();
                        }
                        Buffer buffer = br.getBuffer();
                        if ( buffer != null && buffer.equals( view.getEditPane().getBuffer() ) ) {
                            // don't close the current buffer, even though it may
                            // been open for more than stale time
                            continue;
                        }
                        if ( buffer.isDirty() ) {
                            continue;   // don't auto-close a dirty buffer
                        }
                        if ( viewed.before( stale_time ) ) {
                            toClose.add( br );
                        }
                    }
                    for (BufferReference br : toClose) {
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