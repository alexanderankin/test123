
package ise.plugin.bmp;

import java.io.*;
import java.util.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.FoldHandler;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditorExitRequested;

/**
 * This plugin stores buffer-local properties in a file and restores those
 * setting when the file is next opened. 
 * 
 * The settings are stored as a pipe separated string:
 * Line separator string, values n, r, rn, getStringProperty("lineSeparator")
 * Character encoding string, buffer.getStringProperty(Buffer.ENCODING)
 * gzip on disk boolean, values t, f, buffer.getBooleanProperty(Buffer.GZIPPED)
 * edit mode string, buffer.getMode().getName()
 * fold mode string, buffer.getFoldHandler().getName()
 * word wrap string, buffer.getStringProperty("wrap");
 * wrap width int, buffer.getIntProperty("maxLineLength");
 * tab width int, buffer.getIntProperty("tabSize")
 * indent width int, buffer.getIntProperty("indentSize")
 * soft tabs boolean, t = soft tabs, f = hard tabs, buffer.getBooleanProperty("noTabs")
 * 
 * example:n|ISO-8859-1|f|java|indent|none|76|3|3|t
 *
 * TODO: need to check how this works with files loaded with the ftp plugin
 * 
 * @author Dale Anson, danson@germane-software.com
 * @since Oct 1, 2003
 */
public class BufferLocalPlugin extends EBPlugin {

   // storage for the properties, key is filename as a String,
   // value is the property settings String, see above
   private Properties map = new Properties();

   // temporary storage for properties. Properties are stored here, then moved
   // to permanent storage when they actually change.
   private Properties tempMap = new Properties();

   // control for janitor thread
   private boolean canClean;

   /**
    * Load the stored buffer local properties. The properties are stored in a 
    * a file named .bufferlocalplugin.cfg in $user.home.
    */
   public void start() {
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
      canClean = true;
      janitor.start();
   }

   /**
    * Save the buffer local properties to disk.   
    * @see start
    */
   public void stop() {
      File f = new File( System.getProperty( "user.home" ), ".bufferlocalplugin.cfg" );
      try {
         BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( f ) );
         map.store( out, "Machine generated for BufferLocalPlugin, DO NOT EDIT!" );
         out.flush();
         out.close();
      }
      catch ( Exception e ) {
         // ignored
      }
      canClean = false;
      janitor.interrupt();
   }

   /**
    * Check for BufferUpdate messages. Save properties on CLOSED, 
    * restore properties on LOADED.
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
               if ( "n".equals( ls ) )
                  ls = "\n";
               else if ( "r".equals( ls ) )
                  ls = "\r";
               else
                  ls = "\r\n";
               buffer.setStringProperty( "lineSeparator", ls );
               buffer.setStringProperty( Buffer.ENCODING, enc );
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
         }
         else if ( BufferUpdate.CLOSED.equals( what ) ) {
            // only save if changed, no need to save if not. Doing it this way
            // rather than on PROPERTY_CHANGED as jEdit sends lots of
            // PROPERTY_CHANGED messages even though the properties really
            // haven't changed
            if ( !getBufferLocalString( buffer ).equals( tempMap.getProperty( file ) ) ){
               map.setProperty( file, getBufferLocalString( buffer ) );
            }
         }
      }
      else if ( message instanceof EditorExitRequested ) {
         // jEdit may be shutting down, so update the map for any buffers still open.
         // Oddly enough, jEdit doesn't send CLOSED messages as it closes buffers
         // during shutdown. Or maybe it does, but this plugin is unloaded before 
         // getting those messages.
         Buffer[] buffers = jEdit.getBuffers();
         String file, props, tempProps;
         for ( int i = 0; i < buffers.length; i++ ) {
            file = buffers[ i ].getPath();
            // only save if changed, no need to save if not. Doing it this way
            // rather than on PROPERTY_CHANGED as jEdit sends lots of
            // PROPERTY_CHANGED messages even though the properties really
            // haven't changed
            props = getBufferLocalString(buffers[i]);
            tempProps = tempMap.getProperty(file);
            if (tempProps == null)
               continue;
            if ( !props.equals( tempProps ) ){
               map.setProperty( file, props );
            }
         }
      }
   }

   /**
    * @return a string representing the buffer-local properties. See
    * explanation and example of string at the top of this file.
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
   Thread janitor = new Thread() {
            public void run() {
               if ( map.size() == 0 )
                  return ;
               setPriority( Thread.MIN_PRIORITY );
               while ( canClean ) {
                  Iterator it = map.keySet().iterator();
                  while ( it.hasNext() ) {
                     String filename = ( String ) it.next();
                     File f = new File( filename );
                     if ( !f.exists() )
                        map.remove( filename );
                  }
                  try {
                     sleep( 600000 );
                  }
                  catch ( InterruptedException e ) {
                     // ignored
                  }
               }
            }
         }
         ;
}

