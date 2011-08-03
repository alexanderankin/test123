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


package org.jymc.jpydebug.jedit;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.BufferChangeAdapter;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.Log;

import org.jymc.jpydebug.*;
import org.jymc.jpydebug.jedit.popup.ContextPopup;
import org.jymc.jpydebug.swing.ui.*;
import org.jymc.jpydebug.utils.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;


/**
 * @author jean-yves Jedit Front-End Panel
 */
public class PythonJeditPanel extends JPanel
implements EBComponent,
           PluginEventListener
{
  private static final char _STEP_INTO_ = 's';
  private static final char _NEXT_      = 'n';
  private static final char _CONTINUE_  = 'c';

  private static final int    _FOLD_MARKER_SIZE_     = 12;
  private static final String _PYTHON_               = "python";
  private static final String _PYTHON_SOURCE_SUFFIX_ = "py";
  private static final int    _SHOW_BPS_             = -1;
  private static final String _VERSION_SUFFIX_       = ".jpydbg.version";
  private static final String _XML_SUFFIX_           = ".xml";


  private final static String _CONFIGURATION_PROPERTIES_PROPERTY_ =
    "options.jpydebug-arguments.propertyfile";
  private final static String _CURRENT_DIR_                       = "./";

  /** GUI set breakpoints list per buffer */
  private static Hashtable _bpHighlights = new Hashtable(); // key = editPane    value = BreakpointHighlight
  private static Hashtable _buffers      = new Hashtable(); // key = buffer      value = Object[2]{ TechprintBuffer, Hastable bpHighlights}

  /** set to true when entering debugging session set to false when leaving */
  private static boolean _debugging = false;

  /** python hosting */
  private static PythonDebugContainer _container   = null;
  private static boolean              _propsLoaded = false;

  /** autocompletion */
  private static _DOCUMENT_HANDLER_ _characterHandler =
    new _DOCUMENT_HANDLER_();

  private static java.util.Timer _completionTimer = new java.util.Timer();

  /** current view */
  private View _view;

  /** is debugger unbundled from JEdit */
  private boolean _floating;

  /** source loading in progress */
  private EditPane _loadingPane = null;

  /** keep track of buffer having the focus */
  private Buffer _currentFocusBuffer = null;

  /** In order to set debug Source change on step into */
  private Integer _sourceChangedDbgLine = null;

  /**
   * Creates a new PythonJeditPanel object.
   *
   * @param view     DOCUMENT ME!
   * @param position DOCUMENT ME!
   */
  public PythonJeditPanel( View view, String position )
  {
    super( new BorderLayout() );
    Log.log( Log.DEBUG, this, "Initing PythonJeditPanel" );
    _view     = view;
    _floating = position.equals( DockableWindowManager.FLOATING );


    activate();

    // buildShortCuts() ;

    if (_floating)
      this.setPreferredSize( new Dimension( 1500, 500 ) );
  }


  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public static boolean get_propsLoaded()
  {
    return _propsLoaded;
  }


  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public static boolean is_debugging()
  {
    return _debugging;
  }


  public static boolean isPython( Buffer buf )
  {
    if (buf.getMode() != null)
      if (buf.getMode().getName().equals( _PYTHON_ ))
        return true;

    String extension = MiscUtilities.getFileExtension( buf.getPath() );

    /* consider .py as python sources and force mode anyway */
    if (extension.endsWith( _PYTHON_SOURCE_SUFFIX_ ))
    {
      if (buf != null)
        buf.setMode( _PYTHON_ );

      return true;
    }

    return false;
  }


  /**
   * DOCUMENT ME!
   *
   * @param line DOCUMENT ME!
   * @param buf  DOCUMENT ME!
   */
  public static void aTPBufferBPModifiedAtLine( int line, Buffer buf )
  {
    Enumeration en = _buffers.keys();
    while (en.hasMoreElements())
    {
      Buffer buffer = (Buffer) en.nextElement();
      if (buffer.equals( buf ))
      {
        Object[]     o            = (Object[]) _buffers.get( buffer );
        PythonBuffer pyBuf        = (PythonBuffer) o[0];
        Hashtable    bpHighlights = (Hashtable) o[1];
        boolean      wasSet       = pyBuf.isSet( line );
        pyBuf.manageBreakPoint( line, bpHighlights );
        if ((_container != null) && (line != _SHOW_BPS_))
        {
          if (!wasSet)
            _container.setBreakPoint(
                                     pyBuf.getPath(),
                                     line
                                  );
          else
            _container.clearBreakPoint(
                                       pyBuf.getPath(),
                                       line
                                    );
        }
      }
    }
  }


  public static File getPropFile()
  {
    String settingDir = jEdit.getSettingsDirectory();
    if (settingDir == null)
      settingDir = _CURRENT_DIR_; // use current if setting is undefined

    String propFName =
      jEdit.getProperty( _CONFIGURATION_PROPERTIES_PROPERTY_ );
    Assert.that( propFName != null );

    File propFile = new File( settingDir, propFName );

    return propFile;
  }


  /**
   * JEdit python debug actions XHORTCUT entries
   */
  public static void dbgactions( char cmdType )
  {
    if (_container == null)
      return; // not allocated case
    switch (cmdType)
    {

      case _STEP_INTO_:
        _container.activateStepInto();

        break;

      case _NEXT_:
        _container.activateNext();

        break;

      case _CONTINUE_:
        _container.activateContinue();

        break;

    }
  }


  private void activate()
  {

    // initialiy load properties
    propertiesChanged();

    // make that guy get editing event back through handleMessage callback
    Log.log( Log.DEBUG, this, "Adding to JEditBus" );
    EditBus.addToBus( this );

    // activate GUI interface
    _container = new PythonDebugContainer( this );

    // populate current buffer for debug
    install( _view.getEditPane() );
  }


  /**
   * DOCUMENT ME!
   */
  public void deActivate()
  {
    EditBus.removeFromBus( this );
  }


  private PythonBuffer createOrGetPythonBuffer( JEditTextArea ta )
  {
    PythonBuffer tb     = null;
    Buffer       buffer = (Buffer) ta.getBuffer();

    if (!_buffers.containsKey( buffer ))
    {
      tb = new PythonBuffer( ta, _container );

      _buffers.put(
                   buffer,
                   new Object[] { tb, _bpHighlights }
                );
    }
    else
    {
      Object[] o = (Object[]) _buffers.get( buffer );
      tb = (PythonBuffer) o[0];
    }

    return tb;
  }


  private void saveDebugInf( Buffer buffer )
  {
    String modName = buffer.getPath();
    if (_buffers.containsKey( buffer ))
    {
      Object[]     o     = (Object[]) _buffers.get( buffer );
      PythonBuffer tpBuf = (PythonBuffer) o[0];
      PythonDebuggingProps.setBreakPoints(
                                          modName,
                                          tpBuf.getBreakpointList()
                                       );
      try
      {
        PythonDebuggingProps.save();
      }
      catch (PythonDebugException e)
      {
        Log.log(
                Log.ERROR,
                this,
                e.getMessage()
             );
      }
    }
  }


  private void loadDebugInf( Buffer buffer )
  {
    String    modName = buffer.getPath();
    Hashtable hbps    = PythonDebuggingProps.getBreakPoints( modName );
    if (hbps != null)
    {
      if (_buffers.containsKey( buffer ))
      {
        Enumeration bpList = hbps.keys();
        while (bpList.hasMoreElements())
          aTPBufferBPModifiedAtLine(
                                    ((Integer) bpList.nextElement()).intValue(),
                                    buffer
                                 );
      }
    }

  }


  private void manageBufferUpdateForDebug( BufferUpdate bu )
  {
    Buffer buffer = bu.getBuffer();
    if (isDebugEnabled())
      Log.log( Log.DEBUG, this, "Entering bufferUpdate" );
    if (bu.getWhat().equals( BufferUpdate.CLOSED ))
    {

      // handle breakpoint savings on close event
      saveDebugInf( buffer );
      _buffers.remove( buffer );
      buffer.removeBufferChangeListener( _characterHandler );
    }

    else if (bu.getWhat().equals( BufferUpdate.CREATED )){ }
    else if (bu.getWhat().equals( BufferUpdate.LOAD_STARTED ))
    {
      // _loading = true ;
    }
    else if (bu.getWhat().equals( BufferUpdate.LOADED ))
    {

        // activate autocompletion
      if (PythonDebugParameters.get_autocompletion())
        buffer.addBufferChangeListener( _characterHandler );
      if (isDebugEnabled())
        Log.log( Log.DEBUG, this, "buffer loaded :" + bu.getBuffer().getName() + " mode = " + bu.getBuffer().getMode() );
      if (_loadingPane != null)
      {
        populateEditPane( _loadingPane );
        _loadingPane = null;
      }

      // handle breakpoint loadings from file on load event
      loadDebugInf( buffer );
    }
  }


  private void populateEditPane( EditPane pane )
  {
    JEditTextArea       ta          = pane.getTextArea();
    Buffer              myBuf       = (Buffer) ta.getBuffer();
    BreakpointHighlight bpHighlight =
      (BreakpointHighlight) _bpHighlights.get( pane );
    if (bpHighlight != null)
    {
      PythonBuffer tb = createOrGetPythonBuffer( ta );

      // debugging source switch on trace into
      if (_sourceChangedDbgLine != null)
      {
        bufferLineIsChanged( myBuf, _sourceChangedDbgLine );
        _sourceChangedDbgLine = null;
      }

      if (_container != null)
        _container.setCurrentPythonModuleName( tb.getPath() );
      bpHighlight.bufferChanged( tb );
      aTPBufferBPModifiedAtLine( _SHOW_BPS_, (Buffer) ta.getBuffer() );

      _currentFocusBuffer = myBuf;
    }
  }


  private void manageEditPaneForDebug( EditPaneUpdate epu )
  {
    EditPane      pane = epu.getEditPane();
    JEditTextArea ta   = epu.getEditPane().getTextArea();
    if (epu.getWhat().equals( EditPaneUpdate.CREATED ))
    {
      install( epu.getEditPane() );
      _currentFocusBuffer = (Buffer) ta.getBuffer();
    }
    else if (epu.getWhat().equals( EditPaneUpdate.BUFFER_CHANGED ))
    {
      Log.log( Log.DEBUG, this, "buffer changed to :" + ((Buffer) ta.getBuffer()).getName() );

      Buffer myBuf = (Buffer) ta.getBuffer();
      if (PythonJeditPanel.isPython( myBuf ))
      {
        if (((Buffer) ta.getBuffer()).isLoaded())

          // ta loaded in place ready to proceed
          populateEditPane( pane );
        else

          // wait for LOADED event to complete
          _loadingPane = pane;
      }
      // removeDebuggerToolBar(epu.getEditPane(), tb );
    }
    else if (epu.getWhat().equals( EditPaneUpdate.DESTROYED ))
    {
      _bpHighlights.remove( epu.getEditPane() );
    }

  }


  /**
   * install a BreakPoint Highlight UI instance TextArea
   *
   * @param editPane
   */
  private void install( EditPane editPane )
  {
    Log.log( Log.DEBUG, this, "Installing pane" );
    if (_bpHighlights.containsKey( editPane )) // allready stored
      return;

    JEditTextArea ta = editPane.getTextArea();
    Object test = ta.getBuffer();
    _currentFocusBuffer = (Buffer) ta.getBuffer();

    // get fresh copy of stored properties values
    propertiesChanged();

    PythonBuffer tb = createOrGetPythonBuffer( ta );

    BreakpointHighlight bpHighlight =
      new BreakpointHighlight( tb.get_painter() );

    /* Following instruction blocks JEDIT File loading  */
    _bpHighlights.put( editPane, bpHighlight );


    // make mouse gesture listening around the gutter
    _GUTTERLISTENER_ gutterListener = new _GUTTERLISTENER_();
    ta.getGutter().addMouseListener( gutterListener );
    ta.getGutter().addMouseMotionListener( gutterListener );


    if (_container != null)
      _container.setCurrentPythonModuleName( tb.getPath() );
    bpHighlight.bufferChanged( tb );
    aTPBufferBPModifiedAtLine( _SHOW_BPS_, (Buffer) ta.getBuffer() );
    Log.log( Log.DEBUG, this, "pane installed" );
  }


  private void manageExitJEDITForDebug()
  {
    for (int i = 0; i < jEdit.getBuffers().length; i++)
    {
      Buffer buf = jEdit.getBuffers()[i];
      saveDebugInf( buf );
    }
  }


  private static void writeVersionFile( File versionFile )
  {
    try
    {
      PrintWriter p = new PrintWriter( new FileWriter( versionFile ) );
      p.write( PythonDebugContainer.VERSION );
      p.close();
    }
    catch (IOException e)
    {
      Log.log( Log.ERROR, PythonJeditPanel.class, "failed to write dow version file :" + versionFile.toString() );
    }
  }


  /**
   * Take control over some JEdit source panels events
   */
  public void handleMessage( EBMessage message )
  {
    if (isDebugEnabled())
      Log.log( Log.DEBUG, this, "JpyDbg Message Handler entered:" + message );

    if (message instanceof EditPaneUpdate)
    {
      EditPaneUpdate epu = (EditPaneUpdate) message;
      manageEditPaneForDebug( epu );
    }
    else if (message instanceof ViewUpdate)
    {
      ViewUpdate vu   = (ViewUpdate) message;
      View       view = vu.getView();
      install( view.getEditPane() );
      if (vu.getWhat() == ViewUpdate.CREATED)
        view.addKeyListener( _characterHandler );
      else if (vu.getWhat() == ViewUpdate.CLOSED)
        view.removeKeyListener( _characterHandler );

    }
    else if (message instanceof BufferUpdate)
    {
      manageBufferUpdateForDebug( (BufferUpdate) message );
    }
    else if (message instanceof EditorExitRequested)
    {
      manageExitJEDITForDebug();
    }
    else if (message instanceof PropertiesChanged)
    {
      propertiesChanged();
    }
    if (isDebugEnabled())
      Log.log( Log.DEBUG, this, "JpyDbg Message Handler exited" );
  }


  private static void checkInspectorPath( String pathName )
  {
    File dir         = new File( pathName );
    File versionFile =
      new File( dir, PythonDebugContainer.VERSION + _VERSION_SUFFIX_ );
    if (dir.exists())
    {
      if (versionFile.exists())
        return; // good this version has already clean up the xml space
      Log.log(
              Log.MESSAGE,
              PythonJeditPanel.class,
              "Building JpyDbg Version File"
           );

      // clean up the xml space
      String[] content = dir.list();
      for (int ii = 0; ii < content.length; ii++)
      {
        String wk = content[ii];
        if (wk.endsWith( _XML_SUFFIX_ ) || wk.endsWith( _VERSION_SUFFIX_ ))
        {
          File wkFile = new File( dir, wk );
          if (wkFile.isFile())
            if (!wkFile.delete())
              Log.log( Log.ERROR, PythonJeditPanel.class, "failed to initial cleanup :" + wkFile.toString() );
        }
      }
    }
    else
    {
      dir.mkdir();
    }

    // finally build the current version file
    // to prevent subsequent cleanups before leaving
    writeVersionFile( versionFile );
  }


  /**
   * DOCUMENT ME!
   */
  public static void loadProperties()
  {

    // load customization properties
    try
    {
      PythonDebuggingProps.load( getPropFile() );
      _propsLoaded = true;
    }
    catch (PythonDebugException e)
    {
      Log.log(
              Log.ERROR,
              null,
              e.getMessage()
           );
    }

    JPYGutterPainter.bpLineColor            =
      GUIUtilities.parseColor(
                              jEdit.getProperty( JpyDbgConfigurationPanel.BREAKPOINT_LINE_COLOR )
                           );
    JPYGutterPainter.currentLineBorderColor =
      GUIUtilities.parseColor(
                              jEdit.getProperty( JpyDbgConfigurationPanel.CURRENT_LINE_BORDER_COLOR )
                           );

    // populating functional properties to PythonDebugParameters class
    PythonDebugParameters.set_tempDir(
                                      jEdit.getProperty( JpyDbgConfigurationPanel.TEMPDIR_PROPERTY )
                                   );
    PythonDebugParameters.set_dbgHost(
                                      jEdit.getProperty( JpyDbgConfigurationPanel.DEBUGGINGHOST_PROPERTY )
                                   );
    PythonDebugParameters.set_pythonShellPath(
                                              jEdit.getProperty( JPYJCPythonOptionPane.LOCALPYTHON_PROPERTY )
                                           );
    PythonDebugParameters.set_jythonShellJvm(
                                             jEdit.getProperty( JPYJCPythonOptionPane.LOCALJYTHONJVM_PROPERTY )
                                          );
    PythonDebugParameters.set_jythonShellArgs(
                                              jEdit.getProperty( JPYJCPythonOptionPane.LOCALJYTHON_PROPERTY )
                                           );
    PythonDebugParameters.set_pythonShellPath(
                                              jEdit.getProperty( JPYJCPythonOptionPane.LOCALPYTHON_PROPERTY )
                                           );

    // PythonDebugParameters.set_jpydbgScript(jEdit.getProperty(
    // JpyDbgConfigurationPanel.JPYLOCATION_PROPERTY )) ;
    PythonDebugParameters.set_connectingPort(
                                             jEdit.getIntegerProperty( JpyDbgConfigurationPanel.CONNECTINGPORT_PROPERTY, -1 )
                                          );
    PythonDebugParameters.set_listeningPort(
                                            jEdit.getIntegerProperty( JpyDbgConfigurationPanel.CALLBACKPORT_PROPERTY, -1 )
                                         );
    PythonDebugParameters.set_jpydbgScriptArgs(
                                               jEdit.getProperty( JpyDbgConfigurationPanel.JPYARGS_PROPERTY )
                                            );
    PythonDebugParameters.set_autocompletion(
                                             jEdit.getBooleanProperty( JpyDbgConfigurationPanel.AUTOCOMPLETION_PROPERTY )
                                          );
    PythonDebugParameters.set_jythonActivated(
                                              jEdit.getBooleanProperty( JPYJCPythonOptionPane.JYTHON_ENVIRONMENT )
                                           );
    PythonDebugParameters.set_jythonHome(
                                         jEdit.getProperty( JPYJCPythonOptionPane.JYTHON_HOME )
                                      );
    PythonDebugParameters.set_autoCompletionDelay(
                                                  jEdit.getIntegerProperty( JpyDbgConfigurationPanel.AUTOCOMPLETION_DELAY_PROPERTY, 200 )
                                               );
    PythonDebugParameters.set_debugTrace(
                                         jEdit.getBooleanProperty( JpyDbgConfigurationPanel.DEBUG_TRACE, true )
                                      );
    PythonDebugParameters.set_debugDynamicEvaluation(
                                                     jEdit.getBooleanProperty( JpyDbgConfigurationPanel.DEBUG_DYNAMIC_EVALUATION, true )
                                                  );

    PythonDebugParameters.set_shellBackground(
                                              GUIUtilities.parseColor(jEdit.getProperty( JpyShellConfigurationPanel.JPYSHELL_BACKGROUND_COLOR ) )
                                           );
    PythonDebugParameters.set_shellError(
                                         GUIUtilities.parseColor(jEdit.getProperty( JpyShellConfigurationPanel.JPYSHELL_ERROR_COLOR ) )
                                      );
    PythonDebugParameters.set_shellHeader(
                                          GUIUtilities.parseColor(jEdit.getProperty( JpyShellConfigurationPanel.JPYSHELL_HEADER_COLOR ) )
                                       );
    PythonDebugParameters.set_shellMessage(
                                           GUIUtilities.parseColor(jEdit.getProperty( JpyShellConfigurationPanel.JPYSHELL_MSG_COLOR ) )
                                        );
    PythonDebugParameters.set_shellWarning(
                                           GUIUtilities.parseColor(jEdit.getProperty( JpyShellConfigurationPanel.JPYSHELL_WNG_COLOR ) )
                                        );
    PythonDebugParameters.set_shellFont(
                                        jEdit.getFontProperty( JpyShellConfigurationPanel.JPYSHELL_FONT )
                                     );
    PythonDebugParameters.set_pyPathLocation(
                                             JpyDbgConfigurationPanel.buildPythonPathFileName( false )
                                          );
    PythonDebugParameters.set_jyPathLocation(
                                             JpyDbgConfigurationPanel.buildPythonPathFileName( true )
                                          );

    PythonDebugParameters.set_pyLintLocation(
                                             jEdit.getProperty( PyLintConfigurationPanel.PYLINT_LOCATION_PROPERTY )
                                          );
    PythonDebugParameters.set_pyLintArgs(
                                         jEdit.getProperty( PyLintConfigurationPanel.PYLINT_ARGUMENT_PROPERTY )
                                      );
    PythonDebugParameters.set_usePyLint(
                                        jEdit.getBooleanProperty( PyLintConfigurationPanel.USEPYLINT_PROPERTY )
                                     );
    PythonDebugParameters.set_pyLintFatal(
                                          jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_FATAL_PROPERTY )
                                       );
    PythonDebugParameters.set_pyLintError(
                                          jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_ERROR_PROPERTY )
                                       );
    PythonDebugParameters.set_pyLintWarning(
                                            jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_WARNING_PROPERTY )
                                         );
    PythonDebugParameters.set_pyLintConvention(
                                               jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_CONVENTION_PROPERTY )
                                            );
    PythonDebugParameters.set_pyLintRefactor(
                                             jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_REFACTOR_PROPERTY )
                                          );


    checkInspectorPath( PythonInspector.getJpyDbgXmlDirectory() );

  }


  private void propertiesChanged()
  {
    loadProperties();

    // populate potential coloring changes to python debug containet
    if (_container != null)
      _container.parametersChanged();
  }


  private void bufferLineIsChanged( Buffer buf, Integer currentLine )
  {
    if (buf != null)
    {
      Object[] o = (Object[]) _buffers.get( buf );
      if (o != null)
      {
        PythonBuffer tpBuf   = (PythonBuffer) o[0];
        int          curLine = currentLine.intValue();

        // make requested source as active buffer if not so
        if (_view.getBuffer() != buf)
        {
          Log.log( Log.DEBUG, this, "View buffer changed to " + buf.getName() );
          _view.setBuffer( buf );
        }
        tpBuf.setCurrentLine( curLine );
        aTPBufferBPModifiedAtLine( _SHOW_BPS_, buf );
      }
    }
  }


  private void currentSourceIsChanged(
                                      String  moduleNameStr,
                                      Integer currentLine
                                   ) throws PythonDebugException
  {
    Buffer buf = getBufferFromModuleName( moduleNameStr );

    // make requested source as active buffer if not so
    if (buf == null)
    {
      bufferLineIsChanged(
                          _view.getBuffer(),
                          new Integer( -1 )
                       ); // reset Old Source Current
      _sourceChangedDbgLine = currentLine;
      buf                   = loadBufferFromModuleName( moduleNameStr );

      // _view.setBuffer(buf) ;
    }
    else

      // in place line change will not be populated by JEdit event
      currentLineIsChanged( moduleNameStr, currentLine );
  }


  /**
   * provide the correct SourcePane focus when line chaning on debug events
   *
   * @param moduleNameStr
   * @param currentLine
   */
  public void currentLineIsChanged( String  moduleNameStr,
                                    Integer currentLine
                                 )
  {
    Buffer buf = getBufferFromModuleName( moduleNameStr );
    bufferLineIsChanged( buf, currentLine );
  }


  private Buffer getBufferFromModuleName( String moduleNameStr )
  {
    Enumeration en = _buffers.keys();
    while (en.hasMoreElements())
    {
      Buffer b   = (Buffer) en.nextElement();
      String cur = b.getPath();
      if (MiscStatic.sameFile( moduleNameStr, cur ))
        return b;
    }

    return null;
  }


  private Buffer loadBufferFromModuleName( String moduleNameStr )
                                   throws PythonDebugException
  {
    Buffer current = getBufferFromModuleName( moduleNameStr );
    if (current == null)
    {

      // not found we need to bring it upfront by loading it inside view
      current = jEdit.openFile( _view, moduleNameStr );


      if (current == null)
        throw new PythonDebugException(
                                       "python source not available :" +
                                       moduleNameStr
                                    );
    }

    return current;
  }


  public String getModuleNameFromBuffer( Buffer buffer )
  {
    if (buffer != null)
    {
      return MiscUtilities.getFileName( buffer.getPath() );
    }

    return null;
  }


  /**
   * listening to debugging events requesting actions on source files
   */
  public void newDebuggingEvent( PluginEvent e ) throws PythonDebugException
  {
    switch (e.get_type())
    {

      case PluginEvent.STARTING:
        _debugging = true;

        break;

      case PluginEvent.ENDING:
        _debugging = false;
        bufferLineIsChanged(
                            _currentFocusBuffer,
                            new Integer( -1 )
                         );

        break;

      case PluginEvent.NEWSOURCE:
        currentSourceIsChanged(
                               e.get_source(),
                               new Integer( e.get_line() )
                            );

        break;

      case PluginEvent.NEWLINE:
        currentLineIsChanged(
                             e.get_source(),
                             new Integer( e.get_line() )
                          );

        break;

      default:
        Assert.that( false ); // should not jump here

        break;
    }
  }


  public static boolean isDebugEnabled()
  {
    return PythonDebugParameters.get_debugTrace();
  }


  /* autocompletion stuff */

  private static View getViewWithFocus()
  {
    return jEdit.getActiveView();
  }


  private static class SomethingTypedPopupTask extends TimerTask
  implements Runnable
  {
    private Buffer        _doc;
    private int           _offset;
    private JEditTextArea _textArea;

    public SomethingTypedPopupTask(
                                   Buffer        doc,
                                   int           offset,
                                   JEditTextArea textArea
                                )
    {
      _doc      = doc;
      _offset   = offset;
      _textArea = textArea;
    }

    public int get_offset()
    {
      return _offset;
    }


    public JEditTextArea get_textArea()
    {
      return _textArea;
    }


    /**
     * Main processing method for the SomethingTypedPopupTask object
     */
    public void run()
    {
      SwingUtilities.invokeLater(
                                 new Runnable()
        {
          public void run()
          {
            try
            {
              synchronized (_doc)
              {
                if (getViewWithFocus().getTextArea().hasFocus())
                  ContextPopup.getInstance( getViewWithFocus().getTextArea() ).addCompletionContext();
                  /* if (!CodeAidPopup.isPopupVisible()) {
                   *  CompletionHandler.popupKey.complete(textArea);
                   *} */
              }
            }
            catch (Throwable t)
            {
              if (isDebugEnabled())
                Log.log(
                        Log.DEBUG,
                        JPYJeditPlugin.class,
                        "Error invoking popup"
                     );
              if (isDebugEnabled())
                Log.log( Log.DEBUG, JPYJeditPlugin.class, t );
            }
          }
        }
                              );
    }
  }


  /*
   * Internal class used to capture mouse events on Gutter
   */
  class _GUTTERLISTENER_ extends MouseAdapter
  implements MouseMotionListener
  {

    private Gutter        _gutter = null;
    private JEditTextArea _ta     = null;

    private MouseListener _nativeMouseListener          = null;
    private boolean       _nativeMouseListenerIsRemoved = false;
    private boolean       _allowBP                      = true;

    public _GUTTERLISTENER_(){ }

    public void mousePressed( MouseEvent e )
    {
      if (!_allowBP)
        return;
      _gutter = (Gutter) e.getSource();
      _ta     = (JEditTextArea) _gutter.getParent();
      if (isPython( (Buffer) _ta.getBuffer() ))
      {
        int line = getLineFromY( e.getY() );
        aTPBufferBPModifiedAtLine( line + 1, (Buffer) _ta.getBuffer() );
      }
    }


    private int getLineFromY( int y )
    {
      int screenLine = y / _ta.getPainter().getFontMetrics().getHeight();

      return _ta.getPhysicalLineOfScreenLine( screenLine );
    }


    private MouseListener getNativeMouseListener()
    {
      EventListener[] evs = _gutter.getListeners( MouseListener.class );
      for (int i = 0; i < evs.length; i++)
      {
        if (evs[i] instanceof MouseInputAdapter)
        {
          return (MouseListener) evs[i];
        }
      }

      return null;
    }


    private void removeNativeGutterMouseListener()
    {
      if (!_nativeMouseListenerIsRemoved)
      {
        _nativeMouseListener = getNativeMouseListener();
        _gutter.removeMouseListener( _nativeMouseListener );
        _nativeMouseListenerIsRemoved = true;
      }
    }


    private void restorNativeGutterMouseListener()
    {
      if (_nativeMouseListenerIsRemoved && (_nativeMouseListener != null))
      {
        _gutter.addMouseListener( _nativeMouseListener );
        _nativeMouseListenerIsRemoved = false;
      }
    }


    public void mouseMoved( MouseEvent e )
    {
      _gutter = (Gutter) e.getSource();
      _ta     = (JEditTextArea) _gutter.getParent();

      Buffer buf = (Buffer) _ta.getBuffer();
      _allowBP = true;
      if (isPython( buf ))
      {
        int line = getLineFromY( e.getY() );
        if (line < 0)
          return;
        if (buf.isFoldStart( line ))
        {
          if (e.getX() <= _FOLD_MARKER_SIZE_)
          {
            restorNativeGutterMouseListener();
            _allowBP = false;
          }
          else
          {
            removeNativeGutterMouseListener();
          }
        }
      }
    }


    public void mouseDragged( MouseEvent e ){ }
  }


  private static class _DOCUMENT_HANDLER_ extends BufferChangeAdapter
  implements KeyListener
  {
    public void contentRemoved(
                               Buffer buffer,
                               int    startLine,
                               int    offset,
                               int    numLines,
                               int    length
                            )
    {
      if (length == 0)
        return;
      _completionTimer.cancel();

      View viewWithFocus = getViewWithFocus();

      if (
          (viewWithFocus != null) &&
                      (PythonDebugParameters.get_autocompletion() ||
                      ContextPopup.getInstance( viewWithFocus.getTextArea() ).isIncompletion())
         )
      {
        _completionTimer = new java.util.Timer();
        if (
            ContextPopup.getInstance( viewWithFocus.getTextArea() ).isIncompletion()
           )
        {
          _completionTimer.schedule(
                                    new SomethingTypedPopupTask(
                                                                buffer,
                                                                offset - 1,
                                                                viewWithFocus.getTextArea()
                                                             ),
                                    1
                                 );
        }
        else
        {
          _completionTimer.schedule(
                                    new SomethingTypedPopupTask(
                                                                buffer,
                                                                offset - 1,
                                                                viewWithFocus.getTextArea()
                                                             ),
                                    PythonDebugParameters.get_autoCompletionDelay()
                                 );
        }
      }
    }


    public void contentInserted(
                                Buffer buffer,
                                int    startLine,
                                int    offset,
                                int    numLines,
                                int    length
                             )
    {
      _completionTimer.cancel();

      View          view = getViewWithFocus();
      JEditTextArea ta   = view.getTextArea();
      ContextPopup  po   = ContextPopup.getInstance( ta );

      if (isDebugEnabled())
        Log.log(
                Log.DEBUG,
                this,
                "something of a python identifier was typed"
             );
      if (isDebugEnabled())
        Log.log( Log.DEBUG, this, "InCompletion: " + po.isIncompletion() );
      if (PythonDebugParameters.get_autocompletion() || po.isIncompletion())
      {
        _completionTimer = new java.util.Timer();
        if (po.isIncompletion())
        {
          _completionTimer.schedule(
                                    new SomethingTypedPopupTask(
                                                                buffer,
                                                                offset,
                                                                view.getTextArea()
                                                             ),
                                    1
                                 );
        }
        else
        {
          _completionTimer.schedule(
                                    new SomethingTypedPopupTask(
                                                                buffer,
                                                                offset,
                                                                view.getTextArea()
                                                             ),
                                    PythonDebugParameters.get_autoCompletionDelay()
                                 );
        }
      }
    }


    /**
     * Handle a key press. Cancels the timer so that non buffer modifying key
     * presses can cancel the popup window.
     */
    public void keyPressed( KeyEvent evt )
    {
      _completionTimer.cancel();
    }


    public void keyTyped( KeyEvent evt ){ }


    public void keyReleased( KeyEvent evt ){ }
  }


}
