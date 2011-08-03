/**
 * Copyright (C) 2003 Jean-Yves Mengant
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package org.jymc.jpydebug.swing.ui;


import org.jymc.jpydebug.*;
import org.jymc.jpydebug.utils.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;


/**
 * @author jean-yves debugging toolbar
 */
public class DebugToolbar extends JToolBar
{

  /** frame states to handle */
  public final static int NOT_SET   = -1;

  /** DOCUMENT ME! */
  public final static int STOPPED   = 0;

  /** DOCUMENT ME! */
  public final static int STARTED   = 1;

  /** DOCUMENT ME! */
  public final static int DEBUGGING = 2;

  private static String _STEP_INTO_SHORTCUT_ = "step-into.shortcut";
  private static String _NEXT_SHORTCUT_      = "step-over.shortcut";
  private static String _CONTINUE_SHORTCUT_  = "continue.shortcut";

  private static final String _PY_SUFFIX_ = ".py";

  private static Color _COLORDARKRED_ = new Color( 128, 0, 0 );

  private final static String _NAME_ = "debugToolbar";

  private final static DebugToolbar _DUMMY_ = new DebugToolbar();


  /** run icon */
  public final static ImageIcon RUN_ICON =
    new ImageIcon(
                  _DUMMY_.getClass().getResource( "images/run.gif" ),
                  "run"
               );

  /** step icon */
  public final static ImageIcon STEPINTO_ICON =
    new ImageIcon(
                  _DUMMY_.getClass().getResource( "images/step.gif" ),
                  "step"
               );

  /** start icon */
  public final static ImageIcon START_ICON =
    new ImageIcon(
                  _DUMMY_.getClass().getResource( "images/varicon.gif" ),
                  "start"
               );

  /** stop icon */
  public final static ImageIcon STOP_ICON =
    new ImageIcon(
                  _DUMMY_.getClass().getResource( "images/stop.gif" ),
                  "stop"
               );

  /**Step over icon */
  public final static ImageIcon STEPOVER_ICON =
    new ImageIcon(
                  _DUMMY_.getClass().getResource( "images/stepover.gif" ),
                  "stepover"
               );

  /** DOCUMENT ME! */
  public final static ImageIcon REMOTE_ICON =
    new ImageIcon(
                  _DUMMY_.getClass().getResource( "images/remote.gif" ),
                  "remote"
               );

  /** python icon */
  public final static ImageIcon PYTHON_ICON =
    new ImageIcon(
                  _DUMMY_.getClass().getResource( "images/python.gif" ),
                  "python"
               );

  /** pgm args icon */
  public final static ImageIcon PGMARGS_ICON =
    new ImageIcon(
                  _DUMMY_.getClass().getResource( "images/pgmargs.gif" ),
                  "pgmargs"
               );

  /** jython icon  */
  public final static ImageIcon JYTHON_ICON =
    new ImageIcon(
                  _DUMMY_.getClass().getResource( "images/jython.gif" ),
                  "jython"
               );

  /** jEdit communication usage */
  private static DebugEventListener _listener = null;


  private int _currentState = NOT_SET;

  /** debug bar button collection */
  private Hashtable _buttons = new Hashtable();

  /** assoc Actions collection */
  private Hashtable _actions = new Hashtable();

  private JTextField        _curModule    = new JTextField();
  private ModuleConfigCombo _moduleConfig = new ModuleConfigCombo();

  /**
   * populated by remote debugging startup if remote debugging is in progress
   */
  private String _remoteArguments = null;


  /**
   * Creates a new DebugToolbar object.
   * the DebugToolbar is only visible for jEdit context ; for netbeans 
   * this context is displayed from the netbeans editors contexts
   *
   * @param moduleConfig netbeans module config or null when jedit
   */
  public DebugToolbar()
  {
    super.setName( _NAME_ );
    super.addSeparator();
    buildPythonJythonToggle(
                            DebugEvent.TOGGLEJYTHON,
                            PythonDebugParameters.get_jythonActivated()
                         );
    super.addSeparator();

    buildToolbarButton( DebugEvent.START, START_ICON, null );
    buildToolbarButton( DebugEvent.STOP, STOP_ICON, null );

    buildToolbarButton( DebugEvent.RUN, RUN_ICON, _CONTINUE_SHORTCUT_ );
    buildToolbarButton( DebugEvent.STEPOVER, STEPOVER_ICON, _NEXT_SHORTCUT_ );
    buildToolbarButton(
                       DebugEvent.STEPINTO,
                       STEPINTO_ICON,
                       _STEP_INTO_SHORTCUT_
                    );
    super.addSeparator();
    buildToolbarButton( DebugEvent.REMOTESTART, REMOTE_ICON, null );
    super.addSeparator();
    super.add( _curModule );
    super.addSeparator();
    super.addSeparator();
    buildToolbarButton( DebugEvent.PGMARGS, PGMARGS_ICON, null );
    super.add( _moduleConfig );
    // super.add( new FileChooserPane(  null , _curModule , buildToolbarButton(
    // "load python module" , LOAD_ICON  ) )) ;
  }

  public int get_currentState()
  {
    return _currentState;
  }


  /**
   * adding event listener
   *
   * @param listener provided listener
   */
  public static synchronized void add_debugEventListener(
                                                         DebugEventListener listener
                                                      )
  {
    _listener = listener;
  }


  /**
   * remove listener 
   *
   * @param listener  candidate
   */
  public static synchronized void remove_debugEventListener(
                                                            DebugEventListener listener
                                                         )
  {
    if (listener == listener)
      _listener = null;
  }


  /**
   * generic build button methons
   *
   * @param  toolTip     the tooltip to use
   * @param  icon        the icon to use
   * @param  shortcutKey assoc shortkey
   *
   * @return built button
   */
  public JButton buildButton(
                             String    toolTip,
                             ImageIcon icon,
                             String    shortcutKey
                          )
  {
    JButton retButton = new JButton( icon );
    retButton.setName( toolTip );
    retButton.setToolTipText( buildShortcutKeyInfo( toolTip, shortcutKey ) );

    _buttons.put( toolTip, retButton );

    return retButton;
  }


  /**
   * generic build combo
   *
   * @param  toolTip     the tooltip to use
   * @param  editable    editable if true
   * @param  enterKey    assoc enterKey action
   *
   * @return built button
   */
  public EditableEnterCombo buildCombo(
                                       String         toolTip,
                                       boolean        editable,
                                       ActionListener enterAction
                                    )
  {
    EditableEnterCombo retCombo = new EditableEnterCombo( enterAction );
    retCombo.setEditable( editable );
    retCombo.setName( toolTip );
    retCombo.setToolTipText( toolTip );
    _buttons.put( toolTip, retCombo );

    return retCombo;
  }


  private JToggleButton buildPythonJythonToggle(
                                                String  toolTip,
                                                boolean jythonOn
                                             )
  {
    JToggleButton returned = new JToggleButton();
    returned.setSelected( jythonOn );
    returned.setSelectedIcon( JYTHON_ICON );
    returned.setIcon( PYTHON_ICON );
    returned.setToolTipText( toolTip );
    _buttons.put( toolTip, returned );
    super.add( returned );

    return returned;
  }


  private JButton buildToolbarButton(
                                     String    toolTip,
                                     ImageIcon icon,
                                     String    shortcutKey
                                  )
  {
    JButton retButton = buildButton( toolTip, icon, shortcutKey );
    super.add( retButton );
    super.addSeparator();

    return retButton;
  }


  /**
   * set current module context
   *
   * @param modName current module name + path
   */
  public void set_curModule( String modName )
  {
    if (modName.endsWith( _PY_SUFFIX_ ))
    {
      _curModule.setText( modName );
      _curModule.invalidate();
      _moduleConfig.populate_program_arguments( modName );

      // populate infor to listener
      if (_listener != null)
      {
        _listener.moduleChanged( new DebugEvent( this, modName, null, null ) );
      }
    }
  }


  /**
   * set module name + arguments provided by remote debugging connection
   *
   * @param modName
   */
  public void set_remoteModule( String modName )
  {
    String namePart = modName.trim();
    int    pos      = namePart.indexOf( ' ' ); // check for parameter space
    if (pos != -1) // parameter provided
    {
      namePart = modName.substring( 0, pos );

      /* populate remote arguments */
      _remoteArguments = modName.substring( pos + 1 );
    }
    set_curModule( namePart );
  }


  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public String get_curModule()
  {
    return _curModule.getText();
  }


  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public String get_curModuleArguments()
  {
    if (_remoteArguments == null)
      return _moduleConfig.getArgValue();

    return _remoteArguments;
  }


  /**
   * DOCUMENT ME!
   *
   * @param  key DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public Action getAction( String key )
  {
    return (Action) _actions.get( key );
  }


  /**
   * DOCUMENT ME!
   *
   * @param  key DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public AbstractButton getButton( String key )
  {
    return (AbstractButton) _buttons.get( key );
  }


  /**
   * DOCUMENT ME!
   *
   * @param key       DOCUMENT ME!
   * @param component DOCUMENT ME!
   */
  public void putGuiComponent( String key, Object component )
  {
    _buttons.put( key, component );
  }


  /**
   * DOCUMENT ME!
   *
   * @param  key DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public Component getGuiComponent( String key )
  {
    return (Component) _buttons.get( key );
  }


  private String buildShortcutKeyInfo( String msginf, String shortCut )
  {
    return PythonDebugParameters.ideFront.getShortcutKeyInfo(
                                                             msginf,
                                                             shortCut
                                                          );
  }


  /**
   * DOCUMENT ME!
   *
   * @param curK DOCUMENT ME!
   * @param n    DOCUMENT ME!
   */
  public synchronized void setAction( String curK, ActionListener n )
  {
    AbstractButton b   = (AbstractButton) _buttons.get( curK );
    _ACTIONDEBUG_  cur = (_ACTIONDEBUG_) _actions.get( curK );
    Assert.that( b != null );
    Assert.that( n != null );

    if (cur == null)
    // not yet allocated
    {
      cur = new _ACTIONDEBUG_( curK, n, b );
      _actions.put( curK, cur );
    }
    else
      cur.set_listener( n );

  }


  /**
   * populate provided action listeners to DebugBar
   *
   * @param nActions
   */
  public void setActions( Hashtable nActions )
  {
    Enumeration keys = nActions.keys();
    while (keys.hasMoreElements())
    {
      String         curK   = (String) keys.nextElement();
      ActionListener action = (ActionListener) nActions.get( curK );
      setAction( curK, action );
    }
  }


  /**
   * DOCUMENT ME!
   *
   * @param key    DOCUMENT ME!
   * @param enable DOCUMENT ME!
   */
  public void set_enable( String key, boolean enable )
  {
    Action a = (Action) _actions.get( key );
    if (a == null)
    {
      Component b = (Component) _buttons.get( key );
      Assert.that( b != null );
      b.setEnabled( enable );
    }
    else
      a.setEnabled( enable );
  }


  /**
   * enable disable buttons based on current state
   *
   * @param running
   */
  public void setContext( int context )
  {
    _currentState = context;
    switch (context)
    {

      case DEBUGGING:
        set_enable( DebugEvent.START, false );
        set_enable( DebugEvent.REMOTESTART, false );
        set_enable( DebugEvent.STOP, true );
        set_enable( DebugEvent.STEPOVER, true );
        set_enable( DebugEvent.STEPINTO, true );
        set_enable( DebugEvent.RUN, true );
        set_enable( DebugEvent.COMMANDFIELD, true );
        set_enable( DebugEvent.SENDCOMMAND, true );
        set_enable( DebugEvent.TOGGLEJYTHON, false );
        _curModule.setForeground( _COLORDARKRED_ );

        break;

      case STARTED:
        set_enable( DebugEvent.START, false );
        set_enable( DebugEvent.REMOTESTART, false );
        set_enable( DebugEvent.STOP, true );
        set_enable( DebugEvent.STEPOVER, false );
        set_enable( DebugEvent.STEPINTO, false );
        set_enable( DebugEvent.RUN, false );
        set_enable( DebugEvent.COMMANDFIELD, true );
        set_enable( DebugEvent.SENDCOMMAND, true );
        set_enable( DebugEvent.TOGGLEJYTHON, false );
        _curModule.setForeground( _COLORDARKRED_ );

        break;

      case STOPPED:
        set_enable( DebugEvent.START, true );
        set_enable( DebugEvent.REMOTESTART, true );
        set_enable( DebugEvent.STOP, false );
        set_enable( DebugEvent.STEPOVER, false );
        set_enable( DebugEvent.STEPINTO, false );
        set_enable( DebugEvent.RUN, false );
        set_enable( DebugEvent.COMMANDFIELD, false );
        set_enable( DebugEvent.SENDCOMMAND, false );
        set_enable( DebugEvent.TOGGLEJYTHON, true );
        _curModule.setForeground( Color.black );

        break;
    }
  }

  private class _ACTIONDEBUG_ extends AbstractAction
  {

    private String         _key = null;
    private ActionListener _al  = null;

    public _ACTIONDEBUG_( String key, ActionListener al, AbstractButton b )
    {
      _key = key;
      _al  = al;

      super.putValue( Action.ACTION_COMMAND_KEY, key );
      super.putValue(
                     Action.SMALL_ICON,
                     b.getIcon()
                  );
      super.putValue(
                     Action.SHORT_DESCRIPTION,
                     b.getToolTipText()
                  );

      // give a chance to listener to override Actions

      // populate infor to listener
      if (_listener != null)
      {
        _listener.initializing(
                               new DebugEvent( this,
                                               _curModule.getText(),
                                               this,
                                               b
                                            )
                            );
      }

      b.setAction( this ); // bind

      // initialize Action enabling based on current button status
      setEnabled( b.isEnabled() );

    }

    public String get_key()
    {
      return _key;
    }


    public void set_listener( ActionListener al )
    {
      _al = al;
    }


    public void actionPerformed( ActionEvent e )
    {
      if (this.isEnabled())
      {
        boolean continu = true;

        // populate infor to listener
        if (_listener != null)
        {
          continu =
            _listener.continueDebug(
                                    new DebugEvent( this,
                                                    _curModule.getText(),
                                                    this,
                                                    null
                                                 )
                                 );
        }

        if (continu)
          _al.actionPerformed( new ActionEvent( this, 0, "" ) );
      }
      else
        Toolkit.getDefaultToolkit().beep();
    }


  } // end class


}
