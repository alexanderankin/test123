// $Id$
/*
Copyright (c) 2002, Dale Anson
All rights reserved.
 
Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:
 
* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.nav;

import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PositionChanging;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;


/**
 * NavigatorPlugin, mostly static methods, allows one Navigator per View.
 *
 * @author Dale Anson
 * @version $Revision$
 * @since Oct 25, 2003
 */
public class NavigatorPlugin extends EBPlugin {

    public final static String NAME = "Navigator";

    /**
     * VIEW_SCOPE indicates to Navigator that history is tracked per View.
     */
    public final static int VIEW_SCOPE = 1;

    /**
     * EDITPANE_SCOPE indicates to Navigator that history is tracked per EditPane.
     */
    public final static int EDITPANE_SCOPE = 2;

    // the current scope
    private static int scope = EDITPANE_SCOPE;

    // key into property file for show on toolbar value
    public static final String showOnToolBarKey = "navigator.showOnToolbar";

    /**
     * View/Navigator map.  Each View is assigned exactly one Navigator.
     */
    private final static HashMap<View, Navigator> viewNavigatorMap = new HashMap<View, Navigator>();

    /**
     * View/toolbar map.  Each View has a single main toolbar, and if
     * the Navigator buttons are to be shown on the toolbar, they are
     * added to this main toolbar.
     */
    private final static HashMap<View, JToolBar> toolbarMap = new HashMap<View, JToolBar>();

    /**
     * @return true if the Navigator buttons should be shown on the main toolbar for the View.
     */
    public static boolean showOnToolBars() {
        return jEdit.getBooleanProperty( showOnToolBarKey, false );
    }

    /**
     * @return true if the positions shown in the back and forward popup lists
     * should be grouped by filename.
     */
    public static boolean groupByFile() {
        return jEdit.getBooleanProperty( "navigator.groupByFile" );
    }

    /**
     * Show the Navigator buttons on the main toolbar for the View.
     */
    public static void showButtons() {
        jEdit.setBooleanProperty( showOnToolBarKey, true );
        setToolBars();
    }

    /**
     * Hide the Navigator buttons on the main toolbar for the View.
     */
    public static void hideButtons( View view ) {
        jEdit.setBooleanProperty( showOnToolBarKey, false );
        clearToolBars();
    }

    /**
     * Revalidates the Views.  This is called after the Navigator buttons are
     * added to or removed from the main toolbar for the Views.
     */
    private static void revalidateViews() {
        View views[] = jEdit.getViews();
        for ( int i = 0; i < views.length; ++i ) {
            if ( views[ i ] != null ) {
                views[ i ].getRootPane().revalidate();
            }
        }
    }

    /**
     * Actually add the Navigator buttons to the main toolbar of the view.
     */
    public static void setToolBars() {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    if ( showOnToolBars() ) {
                        for ( View view : viewNavigatorMap.keySet() ) {
                            JToolBar toolbar = toolbarMap.get( view );
                            if ( toolbar == null ) {
                                // only add toolbar if there isn't one already
                                Navigator nav = createNavigator( view );
                                toolbar = new NavToolBar( nav );
                                Container viewToolbar = view.getToolBar();
                                if ( viewToolbar != null ) {
                                    Component[] children = viewToolbar.getComponents();
                                    for ( Component child : children ) {
                                        if ( child instanceof JToolBar ) {
                                            ( ( JToolBar ) child ).add( toolbar );
                                            toolbarMap.put( view, toolbar );
                                            break;
                                        }
                                    }
                                }
                                else {
                                    JOptionPane.showMessageDialog(null, "jEdit toolbar is not visible, can't add Navigator buttons.", "Error", JOptionPane.ERROR_MESSAGE);   
                                }
                            }
                        }
                        revalidateViews();
                    }
                }
            }
        );
    }

    /**
     * Create a Navigator for each currently open View.  Add the Navigator
     * buttons to the Views, depending on the user settings for the toolbar.
     * Note that when jEdit first starts up, this won't do anything as plugins
     * are started before the first View is actually created.
     */
    public void start() {
        scope = jEdit.getIntegerProperty( "navigator.scope", EDITPANE_SCOPE );
        for ( View v : jEdit.getViews() ) {
            createNavigator( v );
        }
        clearToolBars();
        setToolBars();
    }

    /**
     * Remove the Navigator from each View.  Remove the Navigator navigation buttons
     * from the main toolbar for each View.
     */
    public void stop() {
        viewNavigatorMap.clear();
        clearToolBars( true );
        toolbarMap.clear();
        jEdit.setIntegerProperty( "navigator.scope", scope );
    }

    /**
     * @return one of VIEW_SCOPE or EDITPANE_SCOPE
     */
    public static int getScope() {
        return scope;
    }

    /**
     * Set the scope of the Navigators.  All Navigators use the same scope.
     * @param scope one of VIEW_SCOPE or EDITPANE_SCOPE.
     */
    public static void setScope( int scope ) {
        switch ( scope ) {
            case VIEW_SCOPE:
            case EDITPANE_SCOPE:
                NavigatorPlugin.scope = scope;
        }
    }

    public static void toggleScope() {
        String msg = null;
        switch ( scope ) {
            case VIEW_SCOPE:
                scope = EDITPANE_SCOPE;
                msg = jEdit.getProperty( "navigator.message.editPaneScope", "Navigator switched to EditPane scope." );
                break;
            case EDITPANE_SCOPE:
                scope = VIEW_SCOPE;
                msg = jEdit.getProperty( "navigator.message.viewScope", "Navigator switched to View scope." );
                break;
            default:
                return ;
        }
        jEdit.setIntegerProperty( "navigator.scope", scope );
        if ( msg != null ) {
            for ( View view : viewNavigatorMap.keySet() ) {
                view.getStatus().setMessage( msg );
            }
        }
    }

    /**
     * Removes the Navigator buttons from the main toolbar for each View.
     */
    public static void clearToolBars() {
        clearToolBars( false );
    }

    /**
     * @param force force removal of the buttons from the main toolbar for each
     * View regardless of the showOnToolBars setting.
     */
    public static void clearToolBars( boolean force ) {
        if ( force || !showOnToolBars() ) {
            for ( View view : viewNavigatorMap.keySet() ) {
                clearToolBar( view );
            }
            revalidateViews();
        }
    }

    /**
     * Removes the Navigator buttons from the main toolbar for the given view.
     */
    private static void clearToolBar( View view ) {
        JToolBar toolbar = toolbarMap.get( view );
        if ( toolbar != null ) {
            Container viewToolbar = view.getToolBar();
            viewToolbar.remove( toolbar );
            toolbarMap.remove( view );
            revalidateViews();
        }
    }

    /**
     * Adds a Navigator. Navigators are per View.
     *
     * @param view
     *                The View for the Navigator
     * @param navigator
     *                The Navigator
     */
    public static void addNavigator( View view, Navigator navigator ) {
        if ( view == null ) {
            return ;
        }
        if ( viewNavigatorMap.containsKey( view ) ) {
            // already have a Navigator for this view
            return ;
        }
        viewNavigatorMap.put( view, navigator );
    }

    /**
     * Gets the current Navigator for the given View.
     * @param view the View to find the Navigator for.
     * @return the Navigator for the View, or null if there is no Navigator for this view
     */
    public static Navigator getNavigator( View view ) {
        return viewNavigatorMap.get( view );
    }

    /**
     * Create a Navigator for the given View.
     * @param view the View to create a Navigator for.
     * @return a previously existing or a newly created Navigator for this View
     */
    public static Navigator createNavigator( View view ) {
        Navigator navigator = getNavigator( view );
        if ( navigator == null ) {
            navigator = new Navigator( view );
            addNavigator( view, navigator );
        }
        return navigator;
    }

    /**
     * Wrapper for the 'backList' method of the Navigator for the given view.
     *
     * @param view
     *                The view for the Navigator
     */
    public static void backList( View view ) {
        Navigator navigator = getNavigator( view );
        if ( navigator != null ) {
            navigator.backList();
        }
    }

    /**
     * Wrapper for the 'goBack' method of the Navigator for the given view.
     *
     * @param view
     *                The view for the Navigator
     */
    public static void goBack( View view ) {
        Navigator navigator = getNavigator( view );
        if ( navigator != null ) {
            navigator.goBack();
        }
    }

    /**
     * Wrapper for the 'forwardList' method of the Navigator for the given
     * view.
     *
     * @param view
     *                The view for the Navigator
     */
    public static void forwardList( View view ) {
        Navigator navigator = getNavigator( view );
        if ( navigator != null ) {
            navigator.forwardList();
        }
    }

    /**
     * Wrapper for the 'goForward' method of the Navigator for the given
     * view.
     *
     * @param view
     *                The view for the Navigator
     */
    public static void goForward( View view ) {
        Navigator navigator = getNavigator( view );
        if ( navigator != null ) {
            navigator.goForward();
        }
    }

    /**
     * Clear the Navigator history for the given view.
     */
    public static void clearHistory( View view ) {
        Navigator navigator = getNavigator( view );
        if ( navigator != null ) {
            navigator.clearHistory();
        }
    }

    public void handleMessage( EBMessage message ) {
        // When we create a new View, create a new navigator for it
        if ( message instanceof ViewUpdate ) {
            ViewUpdate vu = ( ViewUpdate ) message;
            View v = vu.getView();
            Object what = vu.getWhat();
            if ( what == ViewUpdate.CREATED ) {
                createNavigator( v );
                clearToolBars();
                setToolBars();
            }
            else if ( what.equals( ViewUpdate.CLOSED ) ) {
                viewNavigatorMap.remove( v );
                toolbarMap.remove( v );
            }
        }

        // If the editpane changes its current position, we want to know
        // just before it happens so the last position can be recorded in the
        // history.
        else if ( message instanceof PositionChanging ) {
            PositionChanging cc = ( PositionChanging ) message;
            EditPane p = cc.getEditPane();
            Navigator n = getNavigator( p.getView() );
            if ( n != null ) {
                n.addToHistory();
            }
        }
        else if ( message instanceof EditPaneUpdate ) {
            EditPaneUpdate epu = ( EditPaneUpdate ) message;
            if ( epu.getWhat() == EditPaneUpdate.CREATED ) {
                EditPane editPane = epu.getEditPane();
                Navigator nav = viewNavigatorMap.get( editPane.getView() );
                if ( nav == null ) {
                    // this will add a mouse listener to the edit pane
                    createNavigator( editPane.getView() );
                    return ;
                }
                else {
                    // navigator for the view already exists, so have it add a
                    // mouse listener to this editPane
                    nav.addMouseListenerTo( editPane );
                }
            }
            else if ( epu.getWhat() == EditPaneUpdate.DESTROYED && scope == EDITPANE_SCOPE ) {
                EditPane editPane = epu.getEditPane();
                Navigator nav = viewNavigatorMap.get( editPane.getView() );
                if ( nav != null ) {
                    nav.removeHistory( editPane );
                }
            }
        }
        else if ( message instanceof PropertiesChanged ) {
            if ( showOnToolBars() ) {
                setToolBars();
            }
            else {
                clearToolBars();
            }
            for ( Navigator nav : viewNavigatorMap.values() ) {
                nav.setMaxHistorySize( jEdit.getIntegerProperty( "navigator.maxStackSize", 512 ) );
            }
            setScope( jEdit.getIntegerProperty( "navigator.scope", EDITPANE_SCOPE ) );
        }
    }
}