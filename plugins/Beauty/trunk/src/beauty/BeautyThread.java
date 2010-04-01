

package beauty;

import java.util.Enumeration;
import java.util.Vector;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Marker;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import beauty.beautifiers.*;

// TODO: the wait cursor is not being displayed, need to fix that.
public class BeautyThread implements Runnable {

    private View view = null;
    private Buffer buffer = null;
    private boolean showErrorDialogs = false;
    private Beautifier beautifier = null;

    public BeautyThread( Buffer buffer, View view, boolean showErrorDialogs, Beautifier beautifier ) {
        this.buffer = buffer;
        this.view = view;
        this.showErrorDialogs = showErrorDialogs;
        this.beautifier = beautifier;
    }


    public void run() {
        //Log.log( Log.DEBUG, this, "beautifying the buffer..." );

        if ( beautifier == null ) {
            //Log.log( Log.DEBUG, this, "missing beautifier for beautifier" );
            return ;
        }

        EditPane[] editPanes = null;
        int[] caretPositions = null;

        try {
            if ( view == null ) {
                view = jEdit.getActiveView();
            }
            if ( view != null ) {
                view.showWaitCursor();
                editPanes = view.getEditPanes();
                if ( editPanes != null ) {
                    caretPositions = new int[ editPanes.length ];
                    for ( int i = 0; i < editPanes.length; i++ ) {
                        caretPositions[ i ] = editPanes[ i ].getTextArea().getCaretPosition();
                    }
                }
            }

            // The following properties are set automatically according to the
            // current buffer settings:
            // line separator
            // tab width
            // indent width
            // soft tabs
            /// should soft wrap and line width be included here?
            String mode = buffer.getMode().getName();
            String ls = buffer.getStringProperty( "lineSeparator" );
            int tabWidth = buffer.getIntegerProperty( "tabSize", 4 );
            int indentWidth = buffer.getIntegerProperty( "indentSize", 4 );
            boolean softTabs = buffer.getBooleanProperty( "noTabs" );
            int wrapMargin = buffer.getIntegerProperty( "maxLineLength", 1024 );
            String wrapMode = buffer.getStringProperty( "wrap" );
            beautifier.setBuffer( buffer );
            beautifier.setEditMode( mode );
            beautifier.setLineSeparator( ls );
            beautifier.setTabWidth( tabWidth );
            beautifier.setIndentWidth( indentWidth );
            beautifier.setUseSoftTabs( softTabs );
            beautifier.setWrapMargin( wrapMargin );
            beautifier.setWrapMode( wrapMode );

            // format the buffer
            buffer.readLock();
            String contents = beautifier.beautify( buffer.getText( 0, buffer.getLength() ) );
            buffer.readUnlock();

            // store the string back:
            if ( contents == null || contents.length() == 0 ) {
                // result string is empty!
                Log.log( Log.ERROR, this, jEdit.getProperty( "beauty.error.empty.message" ) );
                if ( showErrorDialogs )
                    GUIUtilities.error( view, "beauty.error.empty", null );
                return ;
            }

            // remember and remove all markers:
            Vector markers = ( Vector ) buffer.getMarkers().clone();
            buffer.removeAllMarkers();

            // set new buffer contents:
            buffer.beginCompoundEdit();
            buffer.remove( 0, buffer.getLength() );
            buffer.insert( 0, contents );
            buffer.endCompoundEdit();

            // if using the default beautifier, have jEdit indent the lines
            if ( beautifier instanceof DefaultBeautifier ) {
                BeautyPlugin.indentLines( view );
            }

            // restore markers:
            Enumeration itr = markers.elements();
            while ( itr.hasMoreElements() ) {
                Marker marker = ( Marker ) itr.nextElement();
                buffer.addMarker( marker.getShortcut(), marker.getPosition() );
            }

            // restore remembered caret positions:
            if ( editPanes != null ) {
                for ( int i = 0; i < editPanes.length; i++ ) {
                    BeautyPlugin.restoreCaretPosition( editPanes[ i ], caretPositions[ i ] );
                }
            }

            //Log.log( Log.DEBUG, this, "completed with success." );
        }
        catch ( Exception ex ) {
            Log.log( Log.ERROR, this, ex );
            if ( showErrorDialogs ) {
                GUIUtilities.error( view, "beauty.error.other", new Object[] { ex } );
            }
        }
        finally {
            if ( view != null ) {
                view.hideWaitCursor();
            }
        }
    }
}