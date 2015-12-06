
package beauty;

import beauty.beautifiers.*;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Marker;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;


// converted this to use a SwingWorker on it's own
public class BeautyThread implements Runnable {

    private View view = null;
    private Buffer buffer = null;
    private boolean showErrorDialogs = false;
    private Beautifier beautifier = null;
    private JDialog waitDialog = null;
    private JLabel messageLabel = null;

    public BeautyThread( Buffer buffer, View view, boolean showErrorDialogs, Beautifier beautifier ) {
        this.buffer = buffer;
        this.view = view;
        this.showErrorDialogs = showErrorDialogs;
        this.beautifier = beautifier;
    }

    public void run() {
        // Log.log( Log.DEBUG, this, "beautifying the buffer..." );
        if ( beautifier == null ) {
            // Log.log( Log.DEBUG, this, "missing beautifier for beautifier" );
            return;
        }

        EditPane[] editPanes = null;
        int[] caretPositions = null;
        try {
            if ( view == null ) {
                view = jEdit.getActiveView();
            }

            if ( view != null ) {
                view.showWaitCursor();
                view.getStatus().setMessage( jEdit.getProperty( "beauty.java8.Wait_--_beautifying_buffer...", "Wait -- beautifying buffer..." ) );
                editPanes = view.getEditPanes();
                if ( editPanes != null ) {
                    caretPositions = new int [editPanes.length] ;
                    for ( int i = 0; i < editPanes.length; i++ ) {
                        caretPositions[i] = editPanes[i].getTextArea().getCaretPosition();
                    }
                }
            }

            // The following properties are set automatically according to the
            // current buffer settings
            Mode mode = buffer.getMode();
            String modeName = mode.getName();
            String ls = "\n";    // jEdit Buffer always uses \n internally
            int tabWidth = buffer.getIntegerProperty( "tabSize", 4 );
            int indentWidth = buffer.getIntegerProperty( "indentSize", 4 );
            boolean softTabs = buffer.getBooleanProperty( "noTabs" );
            int wrapMargin = buffer.getIntegerProperty( "maxLineLength", 1024 );
            String wrapMode = buffer.getStringProperty( "wrap" );
            beautifier.setEditMode( modeName );
            beautifier.setLineSeparator( ls );
            beautifier.setTabWidth( tabWidth );
            beautifier.setIndentWidth( indentWidth );
            beautifier.setUseSoftTabs( softTabs );
            beautifier.setWrapMargin( wrapMargin );
            beautifier.setWrapMode( wrapMode );
            beautifier.setRunner( this );
            // format the buffer contents
            BeautyWorker worker = new BeautyWorker( beautifier, buffer.getText( 0, buffer.getLength() ) );
            worker.addPropertyChangeListener( new BeautyChangeListener() );
            worker.execute();
            showWaitDialog();
            setDialogText( jEdit.getProperty( "beauty.java8.Please_wait,_beautifying_buffer...", "Please wait, beautifying buffer..." ) );
            String contents = worker.getText();
            Exception ex = worker.getError();
            if ( ex != null ) {
                throw ex;
            }

            // check the results
            if ( contents == null || contents.length() == 0 ) {
                // result string is empty!
                Log.log( Log.ERROR, this, jEdit.getProperty( "beauty.error.empty.message" ) );
                if ( showErrorDialogs ) {
                    GUIUtilities.error( view, "beauty.error.empty", null );
                }

                return;
            }

            if ( contents.equals( buffer.getText( 0, buffer.getLength() ) ) ) {
                // don't replace the buffer contents if they haven't changed.
                return;
            }

            // remember and remove all markers:
            Vector markers = ( Vector )buffer.getMarkers().clone();
            buffer.removeAllMarkers();
            // set new buffer contents:
            buffer.beginCompoundEdit();
            buffer.remove( 0, buffer.getLength() );
            buffer.insert( 0, contents );
            buffer.endCompoundEdit();
            // restore markers:
            Enumeration itr = markers.elements();
            while ( itr.hasMoreElements() ) {
                Marker marker = ( Marker )itr.nextElement();
                buffer.addMarker( marker.getShortcut(), marker.getPosition() );
            }
            // restore remembered caret positions:
            if ( editPanes != null ) {
                for ( int i = 0; i < editPanes.length; i++ ) {
                    BeautyPlugin.restoreCaretPosition( editPanes[i], caretPositions[i] );
                }
            }
            // Log.log( Log.DEBUG, this, "completed with success." );
        }
        catch ( Exception ex ) {
            Log.log( Log.ERROR, this, ex );
            if ( showErrorDialogs ) {
                GUIUtilities.error( view, "beauty.error.other", new Object [] {ex} );
            }
        }
        finally {
            if ( view != null ) {
                view.hideWaitCursor();
                view.getStatus().setMessageAndClear( "Beautifying complete." );
            }

            hideWaitDialog();
        }
    }



    class BeautyWorker extends SwingWorker <String, Object> {

        Beautifier beautifier = null;
        String text = "";
        Exception ex = null;

        public BeautyWorker( Beautifier b, String t ) {
            beautifier = b;
            text = t;
            ex = null;
        }

        @Override
        public String doInBackground() {
            // initialize the beautifier
            beautifier.init();
            try {
                return beautifier.beautify( text );
            }
            catch ( Exception e ) {
                e.printStackTrace();
                ex = e;
            }

            return "";
        }

        @Override
        protected void done() {
            try {
                text = get();
            }
            catch ( Exception e ) {
                ex = e;
            }
        }

        public String getText() {
            return text;
        }

        public Exception getError() {
            return ex;
        }
    }



    class BeautyChangeListener implements PropertyChangeListener {

        public void propertyChange( PropertyChangeEvent event ) {
            if ( "state".equals( event.getPropertyName() ) && SwingWorker.StateValue.DONE == event.getNewValue() ) {
                hideWaitDialog();
            }
        }
    }

    private void showWaitDialog() {
        // QUESTION: does the wait dialog need a "Close" button?
        if ( waitDialog == null ) {
            waitDialog = new JDialog( jEdit.getActiveView(), true );
            waitDialog.setUndecorated( true );
            JPanel contents = new JPanel();
            contents.setLayout( new BorderLayout() );
            contents.setBorder( BorderFactory.createEmptyBorder( 11, 11, 11, 11 ) );
            messageLabel = new JLabel( jEdit.getProperty( "beauty.java8.Please_wait,_beautifying_buffer...", "Please wait, beautifying buffer..." ) );
            messageLabel.setIcon( GUIUtilities.loadIcon( "loader.gif" ) );
            contents.add( messageLabel, BorderLayout.CENTER );
            waitDialog.setContentPane( contents );
            waitDialog.pack();
            waitDialog.setLocationRelativeTo( jEdit.getActiveView().getTextArea() );
        }

        waitDialog.setVisible( true );
    }

    private void hideWaitDialog() {
        if ( waitDialog != null ) {
            waitDialog.setVisible( false );
            waitDialog.dispose();
        }
    }

    /**
     * Beautifiers may call this to set progress messages.
     */
    public void setDialogText( String msg ) {
        if ( waitDialog != null && messageLabel != null ) {
            messageLabel.setText( msg );
            waitDialog.pack();
            waitDialog.setLocationRelativeTo( jEdit.getActiveView().getTextArea() );
        }
    }
}

