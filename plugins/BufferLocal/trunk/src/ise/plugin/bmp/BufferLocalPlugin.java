// $Id$
package ise.plugin.bmp;

import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.FoldHandler;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.EditorExitRequested;

/**
 * This plugin stores buffer-local properties in a file and restores those
 * setting when the file is next opened. The settings are stored as a pipe
 * separated string: Line separator string, values n, r, rn,
 * getStringProperty("lineSeparator") Character encoding string,
 * buffer.getStringProperty(Buffer.ENCODING) gzip on disk boolean, values t, f,
 * buffer.getBooleanProperty(Buffer.GZIPPED) edit mode string,
 * buffer.getMode().getName() fold mode string,
 * buffer.getFoldHandler().getName() word wrap string,
 * buffer.getStringProperty("wrap"); wrap width int,
 * buffer.getIntProperty("maxLineLength"); tab width int,
 * buffer.getIntProperty("tabSize") indent width int,
 * buffer.getIntProperty("indentSize") soft tabs boolean, t = soft tabs, f =
 * hard tabs, buffer.getBooleanProperty("noTabs")
 * example:n|ISO-8859-1|f|java|indent|none|76|3|3|t TODO: need to check how this
 * works with files loaded with the ftp plugin DID: seems to work okay with ftp,
 * need to test some more Jan 5, 2004, per request from Slava: removed
 * persistence of line separator and encoding. Kept the string format as above,
 * but implementation now does not actually use line separator and encoding
 * settings.
 *
 * @author    Dale Anson, danson@germane-software.com
 * @version   $Revision$
 * @since     Oct 1, 2003
 */
public class BufferLocalPlugin extends EBPlugin {

   public static String NAME = "bufferlocal";

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

   // control for janitor thread
   private boolean canClean;

   // storage for open buffers, key is filename as a String,
   // value is a BufferReference object
   private HashMap openBuffers = new HashMap();

   /**
    * Load the stored buffer local properties. The properties are stored in a a
    * file named .bufferlocalplugin.cfg in $user.home.
    */
   public void start() {
      loadProperties();

      File f = new File( System.getProperty( "user.home" ), ".bufferlocalplugin.cfg" );
      if ( f.exists() ) {
         try {
            BufferedInputStream in = new BufferedInputStream( new FileInputStream( f ) );
            map.load( in );
            in.close();
         }
         catch ( Exception e ) {
            // ignored, don't worry about what doesn't work
         }
      }
      initOpenBuffers();
      canClean = true;
      janitor.start();
   }

   private void loadProperties() {
      staleTime = jEdit.getIntegerProperty( NAME + ".staleTime", staleTime );
      removeStale = jEdit.getBooleanProperty( NAME + ".removeStale", removeStale );
   }

   private void initOpenBuffers() {
      if ( !removeStale )
         return ;
      View[] views = jEdit.getViews();
      for ( int i = 0; i < views.length; i++ ) {
         EditPane[] edit_panes = views[ i ].getEditPanes();
         for ( int j = 0; j < edit_panes.length; j++ ) {
            Buffer buffer = edit_panes[ i ].getBuffer();
            BufferReference br = new BufferReference( views[ i ], buffer );
            openBuffers.put( buffer.getPath(), views[ i ] );
         }
      }
   }

   /**
    * Save the buffer local properties to disk.
    *
    * @see   start
    */
   public void stop() {
      canClean = false;
      janitor.interrupt();
      File f = new File( System.getProperty( "user.home" ), ".bufferlocalplugin.cfg" );
      try {
         synchronized ( map ) {
            BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( f ) );
            map.store( out, "Machine generated for BufferLocalPlugin, DO NOT EDIT!" );
            out.flush();
            out.close();
         }
      }
      catch ( Exception e ) {
         // ignored
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
         String file = buffer.getPath();
         if ( BufferUpdate.LOADED.equals( what ) ) {
            String props = map.getProperty( file );
            if ( props != null ) {
               // parse the stored properties
               StringTokenizer st = new StringTokenizer( props, "|" );
               String ls = st.nextToken();
               String enc = st.nextToken();
               String gz = st.nextToken();
               String em = st.nextToken();
               String fm = st.nextToken();
               String wm = st.nextToken();
               String ll = st.nextToken();
               String tw = st.nextToken();
               String iw = st.nextToken();
               String tabs = st.nextToken();

               // apply the stored properties to the buffer
               /// see comments above, don't need this right now
               /*
               if ( "n".equals( ls ) )
               ls = "\n";
               else if ( "r".equals( ls ) )
               ls = "\r";
               else
               ls = "\r\n";
               */ 
               /// see comments above, out per request from Slava
               ///buffer.setStringProperty( "lineSeparator", ls );
               ///buffer.setStringProperty( Buffer.ENCODING, enc );
               buffer.setBooleanProperty( Buffer.GZIPPED, gz.equals( "t" ) ? true : false );
               buffer.setFoldHandler( FoldHandler.getFoldHandler( fm ) );
               buffer.setStringProperty( "wrap", wm );
               buffer.setIntegerProperty( "maxLineLength", Integer.parseInt( ll ) );
               buffer.setIntegerProperty( "tabSize", Integer.parseInt( tw ) );
               buffer.setIntegerProperty( "indentSize", Integer.parseInt( iw ) );
               buffer.setBooleanProperty( "noTabs", tabs.equals( "t" ) ? true : false );
               buffer.setMode( em );
            }
            else {
               // on load, if we don't already have properties stored for this file,
               // stash the string to check against when the file is closed.
               tempMap.setProperty( file, getBufferLocalString( buffer ) );
            }
            if ( removeStale ) {
               View view = bu.getView();
               if ( view == null )
                  view = jEdit.getActiveView();
               openBuffers.put( buffer.getPath(), new BufferReference( view, buffer ) );
            }
         }
         else if ( BufferUpdate.CLOSED.equals( what ) ) {
            // only save if changed, no need to save if not. Doing it this way
            // rather than on PROPERTY_CHANGED as jEdit sends lots of
            // PROPERTY_CHANGED messages even though the properties really
            // haven't changed
            if ( !getBufferLocalString( buffer ).equals( tempMap.getProperty( file ) ) ) {
               map.setProperty( file, getBufferLocalString( buffer ) );
            }
            if ( removeStale )
               openBuffers.remove( buffer.getPath() );
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
            if ( tempProps == null )
               continue;
            if ( !props.equals( tempProps ) ) {
               map.setProperty( file, props );
            }
         }
      }
      else if ( message instanceof EditPaneUpdate ) {
         if ( removeStale ) {
            // populate the openBuffers list
            EditPaneUpdate epu = ( EditPaneUpdate ) message;
            Object what = epu.getWhat();
            if ( EditPaneUpdate.BUFFER_CHANGED.equals( what ) ) {
               View view = epu.getEditPane().getView();
               Buffer buffer = epu.getEditPane().getBuffer();
               openBuffers.put( buffer.getPath(), new BufferReference( view, buffer ) );
            }
         }
      }
   }

   /**
    * @param buffer
    * @return        a string representing the buffer-local properties. See
    *      explanation and example of string at the top of this file.
    */
   private String getBufferLocalString( Buffer buffer ) {
      // get the properties
      String ls = buffer.getStringProperty( "lineSeparator" );
      String enc = buffer.getStringProperty( Buffer.ENCODING );
      boolean gz = buffer.getBooleanProperty( Buffer.GZIPPED );
      String em = buffer.getMode().getName();
      String fm = buffer.getFoldHandler().getName();
      String wm = buffer.getStringProperty( "wrap" );
      int ll = buffer.getIntegerProperty( "maxLineLength", 0 );
      int tw = buffer.getIntegerProperty( "tabSize", 3 );
      int iw = buffer.getIntegerProperty( "indentSize", 3 );
      boolean tabs = buffer.getBooleanProperty( "noTabs" );

      // build the string
      StringBuffer prop = new StringBuffer();
      if ( ls.equals( "\n" ) )
         prop.append( "n|" );
      else if ( ls.equals( "\r" ) )
         prop.append( "r|" );
      else
         prop.append( "rn|" );
      prop.append( enc ).append( "|" );
      prop.append( gz ? "t|" : "f|" );
      prop.append( em ).append( "|" );
      prop.append( fm ).append( "|" );
      prop.append( wm ).append( "|" );
      prop.append( String.valueOf( ll ) ).append( "|" );
      prop.append( String.valueOf( tw ) ).append( "|" );
      prop.append( String.valueOf( iw ) ).append( "|" );
      prop.append( tabs ? "t" : "f" );

      return prop.toString();
   }

   // runs once every 10 minutes at a low priority to clean up the map
   Thread janitor =
      new Thread() {
         public void run() {
            setPriority( Thread.MIN_PRIORITY );
            while ( canClean ) {
               loadProperties();
               if ( map.size() > 0 ) {
                  synchronized ( map ) {
                     try {
                        // do 2 loops to avoid ConcurrentModificationExceptions
                        List to_remove = new ArrayList();
                        Iterator it = map.keySet().iterator();
                        while ( it.hasNext() ) {
                           String filename = ( String ) it.next();
                           File f = new File( filename );
                           if ( !f.exists() )
                              to_remove.add( filename );
                        }
                        it = to_remove.iterator();
                        while ( it.hasNext() ) {
                           map.remove( it.next() );
                        }
                     }
                     catch ( Exception e ) {
                        // ignored
                     }
                  }
               }
               if ( removeStale && openBuffers.size() > 0 ) {
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
                           if ( view == null )
                              view = jEdit.getActiveView();
                           Buffer buffer = br.getBuffer();
                           if ( buffer.equals( view.getEditPane().getBuffer() ) ) {
                              // this buffer is the current buffer, but may have
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
                           if ( view == null )
                              view = jEdit.getActiveView();
                           Buffer buffer = br.getBuffer();
                           if ( jEdit.closeBuffer( view, buffer ) )
                              openBuffers.remove( buffer.getPath() );
                        }
                     }
                     catch ( Exception e ) {
                        e.printStackTrace();
                        // ignored
                     }
                  }
               }
               try {
                  sleep( ( long ) staleTime );
               }
               catch ( InterruptedException e ) {
                  // ignored
               }
            }
         }
      }
      ;

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

      public View getView() {
         return view;
      }

      public Buffer getBuffer() {
         return buffer;
      }

      public void setViewed() {
         viewed = Calendar.getInstance();
      }

      public Calendar getViewed() {
         return viewed;
      }
   }
}

