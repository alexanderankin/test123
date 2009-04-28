package jdiff;

import java.io.*;
import java.util.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.util.Log;

import jdiff.component.*;
import jdiff.text.FileLine;
import jdiff.util.*;
import jdiff.util.patch.*;

public class DualDiffManager {

    public static final String JDIFF_LINES = "jdiff-lines";
    public static final String BEEP_ON_ERROR = "jdiff.beep-on-error";
    public static final String HORIZ_SCROLL = "jdiff.horiz-scroll";
    public static final String SELECT_WORD = "jdiff.select-word";

    private static HashMap<View, DualDiff> dualDiffs = new HashMap<View, DualDiff>();
    private static HashMap<View, String> splitConfigs = new HashMap<View, String>();
    private static HashMap< View, HashMap < String, List<Integer> >> caretPositions = new HashMap < View, HashMap < String, List<Integer> >> ();

    public static DualDiff getDualDiffFor( View view ) {
        return ( DualDiff ) dualDiffs.get( view );
    }

    public static boolean isEnabledFor( View view ) {
        return ( dualDiffs.get( view ) != null );
    }

    public static void editPaneCreated( View view ) {
        DualDiffManager.removeFrom( view );
    }

    public static void editPaneDestroyed( View view, EditPane editPane ) {
        DualDiffManager.removeFrom( view );
        DiffHighlight.removeHighlightFrom( editPane );
    }

    public static void editPaneBufferChanged( View view ) {
        DualDiffManager.refreshFor( view );
    }

    public static void addTo( View view ) {
        DualDiff dualDiff = new DualDiff( view );

        EditBus.addToBus( dualDiff );

        dualDiff.enableHighlighters();
        dualDiff.addHandlers();
        DiffLineOverview diffLineOverview = new DiffLineOverview( dualDiff, view );
        dualDiff.setDiffLineOverview( diffLineOverview );

        dualDiff.getDiffOverview0().synchroScrollRight();
        dualDiff.getDiffOverview1().repaint();

        dualDiffs.put( view, dualDiff );
        diffLineOverview.reset();
    }

    public static void removeFrom( View view ) {
        DualDiff dualDiff = ( DualDiff ) dualDiffs.get( view );

        EditBus.removeFromBus( dualDiff );

        if ( dualDiff != null ) {
            dualDiff.removeHandlers();
            dualDiff.disableHighlighters();

            dualDiff.removeOverviews();

            dualDiffs.remove( view );

            dualDiff.getDiffLineOverview().setModel( null );
        }
    }

    /*
     * Go through the split config and check if any of the files listed in the
     * config have been closed since the config was cached.  Let the user know
     * if any file have been closed.
     */
    private static void validateConfig( View view, String splitConfig ) {
        if ( splitConfig == null ) {
            return ;
        }

        String[] tokens = splitConfig.split( " " );
        HashSet<String> filenames = new HashSet<String>();
        for ( int i = 0; i < tokens.length; i++ ) {
            String token = tokens[ i ];
            // tokens starting and ending with " are probably file names
            if ( token.startsWith( "\"" ) && token.endsWith( "\"" )
                    && ( tokens[ i + 1 ].equals( "buffer" ) || tokens[ i + 1 ].equals( "buff" ) ) ) {
                token = token.substring( 1, token.length() - 1 );
                if ( jEdit.getBuffer( token ) == null ) {
                    filenames.add( token );
                }
            }
        }
        for ( String filename : filenames ) {
            JOptionPane.showMessageDialog( view, "JDiff encountered this problem while restoring perspective:\n\nFile closed during diff:\n" + filename, "JDiff Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    public static void toggleFor( final View view ) {
        Runnable r = new Runnable() {
                    public void run() {
                        if ( DualDiffManager.isEnabledFor( view ) ) {
                            // possibly restore split config, see tracker 2540573
                            if ( jEdit.getBooleanProperty( "jdiff.restore-view", true ) ) {
                                String splitConfig = splitConfigs.get( view );
                                if ( splitConfig != null ) {
                                    validateConfig( view, splitConfig );
                                    splitConfigs.remove( view );
                                    view.setSplitConfig( null, splitConfig );
                                }
                                else {
                                    view.unsplit();
                                }
                            }

                            // turn off DualDiff so auto-scroll is deactivated before
                            // restoring caret and viewport.
                            DualDiffManager.removeFrom( view );
                            view.getDockableWindowManager().hideDockableWindow( JDIFF_LINES );

                            // possibly restore caret positions/viewports regardless of
                            // restore split config setting
                            if ( jEdit.getBooleanProperty( "jdiff.restore-caret", true ) ) {
                                HashMap < String, List < Integer >> cps = caretPositions.get( view );
                                if ( cps != null ) {
                                    for ( EditPane ep : view.getEditPanes() ) {
                                        List<Integer> values = cps.get( ep.getBuffer().getPath( false ) );
                                        if ( values != null ) {
                                            int caret_position = values.get( 0 );
                                            int first_physical_line = values.get( 1 );
                                            ep.getTextArea().setCaretPosition( caret_position );
                                            ep.getTextArea().setFirstPhysicalLine( first_physical_line );
                                        }
                                    }
                                    cps = null;
                                    caretPositions.remove( view );
                                }
                            }

                            // let others know that the diff session is over --
                            // the SVN Plugin needs this, others might be interested.
                            EditBus.send( new DiffMessage( view, DiffMessage.OFF ) );

                            view.invalidate();
                            view.validate();
                        }
                        else {
                            // remember split configuration so it can be restored later
                            // and caret positions
                            EditPane[] editPanes = view.getEditPanes();
                            String splitConfig = view.getSplitConfig();
                            if ( splitConfig != null ) {
                                splitConfigs.put( view, splitConfig );
                            }

                            // split the view -- if already split correctly,
                            // don't split.  This might be a bit of a hack in
                            // the case where the view is split in two, but
                            // horizontally rather than vertically. I'm checking
                            // the output of the split config, if it ends with
                            // "horizontal", the view is split horizontally and
                            // needs to be split vertically.
                            if ( editPanes.length != 2 || ( splitConfig != null && !splitConfig.endsWith( "horizontal" ) ) ) {
                                view.unsplit();
                                view.splitVertically();
                            }

                            // at this point, the View is split, so capture the
                            // caret positions and first physical lines for the two files
                            editPanes = view.getEditPanes();
                            HashMap < String, List < Integer >> cps = new HashMap < String, List < Integer >> (); // <String = buffer path, List<Integer> = [0] caret position, [1] first physical line
                            List<Integer> values = new ArrayList<Integer>();
                            values.add( editPanes[ 0 ].getTextArea().getCaretPosition() );
                            values.add( editPanes[ 0 ].getTextArea().getFirstPhysicalLine() );
                            cps.put( editPanes[ 0 ].getBuffer().getPath( false ), values );
                            values = new ArrayList<Integer>();
                            values.add( editPanes[ 1 ].getTextArea().getCaretPosition() );
                            values.add( editPanes[ 1 ].getTextArea().getFirstPhysicalLine() );
                            cps.put( editPanes[ 1 ].getBuffer().getPath( false ), values );
                            caretPositions.put( view, cps );

                            // create the dual diff
                            DualDiffManager.addTo( view );
                            
                            // possibly show the dockable
                            DockableWindowManager dwm = view.getDockableWindowManager();
                            if ( !dwm.isDockableWindowVisible( DualDiffManager.JDIFF_LINES ) && jEdit.getBooleanProperty( "jdiff.auto-show-dockable" ) ) {
                                if ( dwm.getDockableWindow( DualDiffManager.JDIFF_LINES ) == null ) {
                                    dwm.addDockableWindow( DualDiffManager.JDIFF_LINES );
                                }
                                dwm.showDockableWindow( DualDiffManager.JDIFF_LINES );
                            }

                            EditBus.send( new DiffMessage( view, DiffMessage.ON ) );

                            // danson, make sure the divider is in the middle.  For some reason,
                            // the left side would be much smaller than the right side, this
                            // takes care of that.
                            view.invalidate();
                            view.validate();

                            SwingUtilities.invokeLater( new Runnable() {
                                        public void run() {
                                            JSplitPane sp = view.getSplitPane();
                                            sp.setDividerLocation( 0.5 );
                                        }
                                    }
                                                      );
                        }
                    }
                };
        SwingUtilities.invokeLater( r );
    }

    public static void refreshFor( View view ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( view );
        if ( dualDiff != null ) {
            dualDiff.refresh();

            JSplitPane sp = view.getSplitPane();
            sp.setDividerLocation( 0.5 );

            view.invalidate();
            view.validate();
        }
        else {
            if ( jEdit.getBooleanProperty( BEEP_ON_ERROR ) ) {
                view.getToolkit().beep();
            }
        }
    }

    public static boolean getIgnoreCaseFor( View view ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( view );
        if ( dualDiff == null ) {
            return false;
        }

        JSplitPane sp = view.getSplitPane();
        sp.setDividerLocation( 0.5 );

        return dualDiff.getIgnoreCase();
    }

    public static void toggleIgnoreCaseFor( View view ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( view );
        if ( dualDiff != null ) {
            dualDiff.toggleIgnoreCase();
            dualDiff.refresh();

            JSplitPane sp = view.getSplitPane();
            sp.setDividerLocation( 0.5 );

            view.invalidate();
            view.validate();
        }
        else {
            if ( jEdit.getBooleanProperty( BEEP_ON_ERROR ) ) {
                view.getToolkit().beep();
            }
        }
    }

    public static boolean getTrimWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( view );
        if ( dualDiff == null ) {
            return false;
        }

        return dualDiff.getTrimWhitespace();
    }

    public static void toggleTrimWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( view );
        if ( dualDiff != null ) {
            dualDiff.toggleTrimWhitespace();
            dualDiff.refresh();

            JSplitPane sp = view.getSplitPane();
            sp.setDividerLocation( 0.5 );

            view.invalidate();
            view.validate();
        }
        else {
            if ( jEdit.getBooleanProperty( BEEP_ON_ERROR ) ) {
                view.getToolkit().beep();
            }
        }
    }

    public static boolean getIgnoreAmountOfWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( view );
        if ( dualDiff == null ) {
            return false;
        }

        return dualDiff.getIgnoreAmountOfWhitespace();
    }

    public static void toggleIgnoreAmountOfWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( view );
        if ( dualDiff != null ) {
            dualDiff.toggleIgnoreAmountOfWhitespace();
            dualDiff.refresh();

            JSplitPane sp = view.getSplitPane();
            sp.setDividerLocation( 0.5 );

            view.invalidate();
            view.validate();
        }
        else {
            if ( jEdit.getBooleanProperty( BEEP_ON_ERROR ) ) {
                view.getToolkit().beep();
            }
        }
    }

    public static boolean getIgnoreAllWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( view );
        if ( dualDiff == null ) {
            return false;
        }

        return dualDiff.getIgnoreAllWhitespace();
    }

    public static void toggleIgnoreAllWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( view );
        if ( dualDiff != null ) {
            dualDiff.toggleIgnoreAllWhitespace();
            dualDiff.refresh();

            JSplitPane sp = view.getSplitPane();
            sp.setDividerLocation( 0.5 );

            view.invalidate();
            view.validate();
        }
        else {
            if ( jEdit.getBooleanProperty( BEEP_ON_ERROR ) ) {
                view.getToolkit().beep();
            }
        }
    }

    public static void nextDiff( EditPane editPane ) {
        // danson, the nextDiff0 and nextDiff1 weren't working correctly, they
        // were using the first visible line rather than the caret line to
        // calculate the next diff position.  Using the first physical line
        // meant the "next" diff was always the first visible diff, even if
        // there were 2 visible diffs.
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( editPane.getView() );
        if ( dualDiff == null ) {
            if ( jEdit.getBooleanProperty( BEEP_ON_ERROR ) ) {
                editPane.getToolkit().beep();
            }
            return ;
        }

        if ( dualDiff.getEditPane0() == editPane ) {
            dualDiff.nextDiff0();
        }
        else if ( dualDiff.getEditPane1() == editPane ) {
            dualDiff.nextDiff1();
        }
        else {
            if ( jEdit.getBooleanProperty( BEEP_ON_ERROR ) ) {
                editPane.getToolkit().beep();
            }
        }
    }

    public static void prevDiff( EditPane editPane ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( editPane.getView() );
        if ( dualDiff == null ) {
            if ( jEdit.getBooleanProperty( BEEP_ON_ERROR ) ) {
                editPane.getToolkit().beep();
            }
            return ;
        }

        if ( dualDiff.getEditPane0() == editPane ) {
            dualDiff.prevDiff0();
        }
        else if ( dualDiff.getEditPane1() == editPane ) {
            dualDiff.prevDiff1();
        }
        else {
            if ( jEdit.getBooleanProperty( BEEP_ON_ERROR ) ) {
                editPane.getToolkit().beep();
            }
        }
    }

    public static void moveRight( EditPane editPane ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( editPane.getView() );
        if ( dualDiff == null ) {
            if ( jEdit.getBooleanProperty( BEEP_ON_ERROR ) ) {
                editPane.getToolkit().beep();
            }
            return ;
        }
        editPane = editPane.getView().getEditPanes() [ 0 ];
        dualDiff.getDiffOverview0().moveRight( editPane.getTextArea().getCaretLine() );
    }

    public static void moveLeft( EditPane editPane ) {
        DualDiff dualDiff = DualDiffManager.getDualDiffFor( editPane.getView() );
        if ( dualDiff == null ) {
            editPane.getToolkit().beep();
            return ;
        }
        editPane = editPane.getView().getEditPanes() [ 1 ];
        dualDiff.getDiffOverview0().moveLeft( editPane.getTextArea().getCaretLine() );
    }

    public static void diffNormalOutput( View view ) {
        if ( !DualDiffManager.isEnabledFor( view ) ) {
            if ( jEdit.getBooleanProperty( BEEP_ON_ERROR ) ) {
                view.getToolkit().beep();
            }
            return ;
        }

        DualDiff dualDiff = DualDiffManager.getDualDiffFor( view );

        // Generate the script
        Buffer buf0 = dualDiff.getEditPane0().getBuffer();
        Buffer buf1 = dualDiff.getEditPane1().getBuffer();

        FileLine[] fileLines0 = DualDiffUtil.getFileLines( dualDiff, buf0 );
        FileLine[] fileLines1 = DualDiffUtil.getFileLines( dualDiff, buf1 );

        Diff d = new Diff( fileLines0, fileLines1 );
        Diff.Change script = d.diff_2();

        // Files are identical: return
        if ( script == null ) {
            GUIUtilities.message( view, "jdiff.identical-files", null );
            return ;
        }

        // Generate the normal output
        StringWriter sw = new StringWriter();
        DiffOutput diffOutput = new DiffNormalOutput( fileLines0, fileLines1 );
        diffOutput.setOut( new BufferedWriter( sw ) );
        diffOutput.setLineSeparator( "\n" );
        try {
            diffOutput.writeScript( script );
        }
        catch ( IOException ioe ) {
            Log.log( Log.DEBUG, DualDiff.class, ioe );
        }

        // Get/create the output view and create a new buffer
        View outputView = jEdit.getFirstView();
        for ( ; outputView != null; outputView = outputView.getNext() ) {
            if ( !DualDiffManager.isEnabledFor( outputView ) ) {
                break;
            }
        }
        if ( outputView == null ) {
            outputView = jEdit.newView( view, view.getBuffer() );
        }
        Buffer outputBuffer = jEdit.newFile( outputView );

        // Insert the normal output into the buffer
        String s = sw.toString();
        outputBuffer.insert( 0, s );
        // When the string ends with a newline, the generated buffer
        // adds one extra newline so we remove it
        if ( s.endsWith( "\n" ) && outputBuffer.getLength() > 0 ) {
            outputBuffer.remove( outputBuffer.getLength() - 1, 1 );
        }
    }

    /**
     * Shows a dialog for the user to select
     * a patch file, then applies that patch file to the current buffer.
     * @param view the view displaying the buffer
     */
    public static void applyPatch( View view ) {
        try {
            // let the user select the patch file and patch file type
            PatchSelectionDialog dialog = new PatchSelectionDialog( view );
            DualDiffUtil.center( view, dialog );
            dialog.setVisible( true );
            String patch_file = dialog.getPatchFile();
            if ( patch_file == null || patch_file.length() == 0 ) {
                // null means user canceled
                return ;
            }

            // load the patch file
            Reader reader = new BufferedReader( new FileReader( patch_file ) );
            StringWriter writer = new StringWriter();
            PatchUtils.copyToWriter( reader, writer );
            String patch = writer.toString();
            if ( patch == null || patch.length() == 0 ) {
                JOptionPane.showMessageDialog( view, "Invalid patch file, file has no content.", "Error", JOptionPane.ERROR_MESSAGE );
                return ;
            }

            // load the file to be patched
            Buffer buffer = view.getEditPane().getBuffer();
            String bufferText = buffer.getText( 0, buffer.getLength() );

            // apply the patch
            String results = Patch.patch( patch, bufferText );

            // show the results as a new file so the user can check it against
            // the original before saving it
            jEdit.newFile( view ).insert( 0, results );
        }
        catch ( Exception e ) {
            JOptionPane.showMessageDialog( view, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }


}