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
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;

import org.jymc.jpydebug.*;
import org.jymc.jpydebug.swing.ui.*;

import java.io.*;

import java.util.*;


/**
 * @author jean-yves Keep track of a JEdit source python buffer
 */
public class PythonBuffer
{

  private Buffer        _buffer;
  private JEditTextArea _ta;

  private Hashtable        _bps          = new Hashtable(); // key = Integer line number = Value.
  private int              _selectedLine = -1;
  private int              _currentLine  = -1;
  private JPYGutterPainter _painter      = null;
  private boolean          _loading      = false;
  private _DEBUG_TOOLTIP_  _tooltip      = null;

  /**
   * Creates a new PythonBuffer object.
   *
   * @param ta        DOCUMENT ME!
   * @param container DOCUMENT ME!
   * @param buffer    DOCUMENT ME!
   */
  public PythonBuffer(
                      JEditTextArea        ta,
                      PythonDebugContainer container
                   )
  {
    _ta = ta;

    _buffer = (Buffer)ta.getBuffer() ;
    
    // allocate a new UI TextAreaExtension and store it
    _painter = new JPYGutterPainter( _ta );

    // allocate a Mouse Listener on text area to capture
    // debugging values around mouse position
    _tooltip = new _DEBUG_TOOLTIP_( container );
    _ta.getPainter().addExtension( _tooltip );
  }

  /**
   * DOCUMENT ME!
   *
   * @param loading DOCUMENT ME!
   */
  public void set_loading( boolean loading )
  {
    _loading = loading;
  }


  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public boolean is_loading()
  {
    return _loading;
  }


  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public int get_selectedLine()
  {
    return _selectedLine;
  }


  public Buffer getBuffer()
  {
    return _buffer;
  }


  public String getPath()
  {
    if (_buffer != null)
      return _buffer.getPath();

    return null;
  }


  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public JPYGutterPainter get_painter()
  {
    return _painter;
  }


  /**
   * @return the python file name without the path information
   */
  public String getPyModuleName()
  {
    File pyFile = new File( getPath() );

    return pyFile.getName();
  }


  public boolean isSet( int line )
  {
    if (_bps.containsKey( new Integer( line ) ))
      return true;

    return false;
  }


  public void setCurrentLine( int currentLine )
  {
    _currentLine = currentLine;

    // clear selected line as soon as current is updated
    _selectedLine = -1;
    paintCurrentLine();

    // cleanup the tooltip current value content since
    // the new run may have changed the value
    _tooltip.newIntruction();
  }


  private void paintCurrentLine()
  {
    if (_painter != null)
      _painter.setCurrentLine( _currentLine );
  }


  public void setBreakpoints( Hashtable breakpoints )
  {
    _bps = breakpoints;
  }


  private Hashtable buildHashBP( int numLine )
  {
    if (numLine == -1)
      return _bps;
    if (!_bps.contains( new Integer( numLine ) ))
    {
      _bps.put(
               new Integer( numLine ),
               new Integer( numLine )
            );
    }
    else
    {
      _bps.remove( new Integer( numLine ) );
    }
    _selectedLine = -1;

    return _bps;
  }


  /**
   * DOCUMENT ME!
   *
   * @param line         DOCUMENT ME!
   * @param bpHighlights DOCUMENT ME!
   */
  public void manageBreakPoint( int line, Hashtable bpHighlights )
  {
    Hashtable h = buildHashBP( line );

    Enumeration en = bpHighlights.elements();
    while (en.hasMoreElements())
    {
      BreakpointHighlight breakpointHighlight =
        (BreakpointHighlight) en.nextElement();
      Buffer              curBuffer           =
        breakpointHighlight.get_buffer();
      if (curBuffer == _buffer)
      {
        breakpointHighlight.setBreakPoints( h );
      }

      // we need to have a painter and textarea must be loaded and in place
      if ((_painter != null) && (_buffer.isLoaded()))
        _painter.safeRepaint();
    }
  }


  public Enumeration getBreakpointList()
  {
    return _bps.keys();
  }

  class _DEBUG_TOOLTIP_ extends TextAreaExtension
  implements PythonDebugEventListener
  {
    private final static String _CMD_       = "CMD ";
    private final static String _OK_        = "OK";
    private final static String _EOL_       = "/EOL/";
    private final static String _FILE_      = "File";
    private final static String _TRACEBACK_ = "Traceback";

    private PythonDebugContainer _container     = null;
    private StringBuffer         _evaluation    = null;
    private String               _lastEvaluated = null;

    public _DEBUG_TOOLTIP_( PythonDebugContainer container )
    {
      _container = container;
    }

    /**
     * best effort to extract python expression around mouse location
     */
    private String getMouseWord( int x, int y )
    {
      Buffer buffer = (Buffer) _ta.getBuffer();

      // if some areas are selected in source return it
      // as preferred evaluation area
      if (_ta.getSelectedText() != null)
        return _ta.getSelectedText();

      // if not return best expression around the mouse location
      int    offset     = _ta.xyToOffset( x, y );
      int    line       = buffer.getLineOfOffset( offset );
      String full       = buffer.getLineText( line );
      int    lineOffset = buffer.getLineStartOffset( line );
      int    textPos    = offset - lineOffset;
      int    lineLen    = full.length();
      if (lineLen < textPos) // outside bound
        return null;

      DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
      buffer.markTokens( line, tokenHandler );

      Token token = tokenHandler.getTokens();
      while ((token.offset + token.length) < textPos)
      {
        token = token.next;
        if (token == null)
          return null;
      }
      if (
          (token.id == Token.COMMENT1) ||
                      (token.id == Token.COMMENT2) ||
                      (token.id == Token.COMMENT3) ||
                      (token.id == Token.COMMENT4) ||
                      (token.id == Token.FUNCTION) ||
                      (token.id == Token.INVALID) ||
                      (token.id == Token.KEYWORD1) ||
                      (token.id == Token.KEYWORD2) ||
                      (token.id == Token.KEYWORD3) ||
                      (token.id == Token.KEYWORD4) ||
                      (token.id == Token.OPERATOR)
         )
        return null;

      String returned =
        full.substring( token.offset, token.offset + token.length ).trim();
      if (returned.length() == 0)
        return null;

      return returned;
    }


    public void launcherMessage( PythonDebugEvent e ){ }


    public void newDebugEvent( PythonDebugEvent e )
    {
      switch (e.get_type())
      {

        case PythonDebugEvent.COMMAND:
          if (e.get_msgContent().equals( _OK_ )) // end of result we can notify
          {
            synchronized (this)
            {
              this.notify();
            }
          }

          // else error has occured we will notify at EXCEPTION
          break;

        case PythonDebugEvent.STDOUT:

          String cur = e.get_msgContent();
          if (!cur.equals( _EOL_ ))
            _evaluation.append( cur );

          break;

        case PythonDebugEvent.COMMANDDETAIL:
          cur = e.get_msgContent();
          if (cur != null)
          {
            cur = cur.trim();
            if (!(cur.startsWith( _FILE_ ) || cur.startsWith( _TRACEBACK_ ))) // bypass error stack info
            {
              _evaluation.append( cur );
            }
            // by design Last COMMANDDETAIL contains no message
          }
          else
          {
            synchronized (this)
            {
              this.notify();
            }
          }

          break;

      }
    }


    private synchronized void waitEvaluationCompletion()
    {
      try
      {
        this.wait();
      }
      catch (InterruptedException e){ }
    }


    public void newIntruction()
    {

      // just reset the _lastEvaluated value
      _lastEvaluated = null;
    }


    private synchronized String evaluateExpression( String expr )
    {
      PythonDebugClient pyClient = _container.get_pyClient();

      // just done
      if ((_lastEvaluated != null) && (_lastEvaluated.equals( expr )))
        return expr + "=" + _evaluation.toString();

      StringBuffer bf = new StringBuffer( _CMD_ );
      bf.append( expr );
      try
      {

        // populate user's variable changes though debugging interface
        PythonDebugEventListener saved = pyClient.get_listener();
        pyClient.setPythonDebugEventListener( this );
        _evaluation = new StringBuffer();
        pyClient.sendCommand( bf.toString() );
        waitEvaluationCompletion();
        pyClient.setPythonDebugEventListener( saved );
        _lastEvaluated = expr;

        return expr + "=" + _evaluation;
      }
      catch (PythonDebugException ex)
      {
        return "evaluation failed : " + ex.getMessage();
      }
    }


    public String getToolTipText( int x, int y )
    {
      if (
          PythonDebugParameters.get_debugDynamicEvaluation() &&
                      PythonJeditPanel.is_debugging()
         )
      {
        String toEval = getMouseWord( x, y );
        if (toEval != null)
          return (evaluateExpression( toEval ));
      }

      return null;
    }
  }
}
