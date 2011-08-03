package org.jymc.jpydebug.jedit;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.Log;

import java.awt.*;

import javax.swing.*;


/**
 * Copyright (C) 2003,2004 Jean-Yves Mengant This program is free software; you
 * can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2
 * of the License, or any later version. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

public class JPYGutterPainter extends TextAreaExtension
{
  private static final int FOLD_MARKER_SIZE = 12;

  private static final JPYGutterPainter _DUMMY_ = new JPYGutterPainter( null );

  private static final ImageIcon _ICONBULLET_ =
    new ImageIcon(
                  _DUMMY_.getClass().getResource( "images/bulletpb.gif" ),
                  "bulletpb"
               );

  private static final ImageIcon _ICONBULLETINVALID_ =
    new ImageIcon(
                  _DUMMY_.getClass().getResource( "images/bulletpbinvalid.gif" ),
                  "bulletpbinvalid"
               );


  public static Color   bpLineColor            = Color.red;
  public static Color   bpLineColorInvalid     = new Color( 224, 224, 224 ); // new Color(99,165,33); //vert fonce
  public static Color   selectedLineColor      = new Color( 255, 255, 0 ); // Jaune
  public static boolean filledDebugLine        = false;
  public static Color   currentLineColor       = new Color( 0, 0, 0 );
  public static Color   currentLineBorderColor = new Color( 0, 0, 128 );

  public static final String NO_WORD_SEP = new String( "_.<>" );
  // private Color bpLineColor = new Color(128, 0, 0);

  private Breakpoint[]  _bp       = null;
  private JEditTextArea _textArea;
  private Buffer        _buffer;

  private int _currentLine = -1;


  private int _selectedLine = -1;

  /**
   * Creates a new JPYGutterPainter object.
   *
   * @param textArea DOCUMENT ME!
   * @param buffer   DOCUMENT ME!
   */
  public JPYGutterPainter( JEditTextArea textArea )
  {
    _textArea = textArea;
    if (textArea != null)
    {
      _buffer = (Buffer) _textArea.getBuffer();
      _bp     = new Breakpoint[0];
      textArea.getPainter().addExtension( new _LINE_PAINTER_() );

      // get the gutter and populate extension
      textArea.getGutter().addExtension( this );
      ToolTipManager.sharedInstance().registerComponent( textArea.getPainter() );
      ToolTipManager.sharedInstance().setInitialDelay( 1000 );
      ToolTipManager.sharedInstance().setDismissDelay( 3000 );
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public Buffer get_buffer()
  {
    return (Buffer) _textArea.getBuffer();
  }


  private void repaint()
  {
    Log.log( Log.DEBUG, this, Thread.currentThread().getName() + " painting line :" + _currentLine );
    _textArea.getPainter().repaint();
    Log.log( Log.DEBUG, this, Thread.currentThread().getName() + " painting gutter :" + _currentLine );
    _textArea.getGutter().repaint();
  }


  public void setBreakPoints( Breakpoint[] bp )
  {
    _bp = bp;
  }


  /* REPORTED AD UNUSED LOCALLY
   * private String getWordAt(int offset )
   * {
   * String word = null;
   * int numLine = _textArea.getLineOfOffset(offset);
   * String line = _textArea.getLineText(numLine);
   * int lineStartOffset = _textArea.getLineStartOffset(numLine);
   * int lineOffset =offset - lineStartOffset;
   * if (lineOffset <line.length ())
   * {
   * int s = TextUtilities.findWordStart (line,lineOffset,NO_WORD_SEP);
   * int en = TextUtilities.findWordEnd (line,lineOffset,NO_WORD_SEP);
   * word = line.substring(s ,en);
   * if (word.equals("")) word=null;
   * }
   * return word;
   * }
   */

  private int getBulletX()
  {
    Gutter gutter = _textArea.getGutter();
    if (!gutter.isExpanded())
      return 0;

    int width = gutter.getWidth();
    int x     = 0;
    switch (gutter.getLineNumberAlignment())
    {

      case Gutter.LEFT:
        x = width - 16;

        break;

      case Gutter.CENTER:
        x = FOLD_MARKER_SIZE;

        break;

      case Gutter.RIGHT:
        x = FOLD_MARKER_SIZE;

        break;
    }

    return x;
  }


  /**
   * DOCUMENT ME!
   *
   * @param gfx          DOCUMENT ME!
   * @param screenLine   DOCUMENT ME!
   * @param physicalLine DOCUMENT ME!
   * @param start        DOCUMENT ME!
   * @param end          DOCUMENT ME!
   * @param y            DOCUMENT ME!
   */
  public void paintValidLine(
                             Graphics2D gfx,
                             int        screenLine,
                             int        physicalLine,
                             int        start,
                             int        end,
                             int        y
                          )
  {
    View          cur       = jEdit.getActiveView();
    Buffer        curBuffer = cur.getBuffer();
    JEditTextArea ta        = cur.getTextArea();

    // check for textArea availability
    if (!curBuffer.isLoaded())
      return;

    if (!PythonJeditPanel.isPython( curBuffer ))
      return;

    for (int i = 0; i < _bp.length; ++i)
    {
      if (_bp[i].getLineNumber() == (physicalLine + 1))
      {
        Image bullet = null;
        if (_bp[i].isInvalid())
          bullet = _ICONBULLETINVALID_.getImage();
        else
          bullet = _ICONBULLET_.getImage();
        gfx.drawImage(
                      bullet,
                      getBulletX(),
                      y + 2,
                      12,
                      12,
                      null
                   );
      }
    }

    if (_currentLine == -1)
      return;

    // ARROW CURRENT LINE DRAWING
    if (!PythonJeditPanel.is_debugging()) // not in debugging phase
      return;

    if (cur.getBuffer() != _buffer)
      return;

    if ((physicalLine + 1) != _currentLine)
      return;

    // EXPAND FOLDER IN ORDER TO MAKE DBG ARROW VISIBLE
    // int lineOffset = _textArea.getLineStartOffset(_currentLine) ;
    // _textArea.getDisplayManager().expandFold(lineOffset,false) ;
    // scrollToCurrentLine(_currentLine) ;

    FontMetrics fm         = ta.getPainter().getFontMetrics();
    int         lineHeight = fm.getHeight();
    Polygon     polygon    =
      createDebugMark(
                      y + lineHeight - 6,
                      getBulletX()
                   );
    gfx.setColor( Color.yellow );
    gfx.fillPolygon( polygon );
    gfx.setColor( Color.black );
    gfx.drawPolygon( polygon );

  }


  private static java.awt.Polygon createDebugMark( int ybase, int xbase )
  {
    ybase = ybase - 8;

    int[] xp = new int[7];
    int[] yp = new int[7];
    xp[0] = 0 + xbase;
    yp[0] = 3 + ybase;
    xp[1] = 6 + xbase;
    yp[1] = 3 + ybase;
    xp[2] = 6 + xbase;
    yp[2] = 0 + ybase;
    xp[3] = 11 + xbase;
    yp[3] = 5 + ybase;
    xp[4] = 6 + xbase;
    yp[4] = 10 + ybase;
    xp[5] = 6 + xbase;
    yp[5] = 7 + ybase;
    xp[6] = 0 + xbase;
    yp[6] = 7 + ybase;

    return new java.awt.Polygon( xp, yp, 7 );
  }


  /**
   * DOCUMENT ME!
   */
  public void safeRepaint()
  {
    SwingUtilities.invokeLater(
                               new Runnable()
      {
        public void run()
        {
          repaint();
        }
      }
                            );
  }


  public void setCurrentLine( int currentLine )
  {
    _currentLine = currentLine;
    if (currentLine != -1)
    {
      SwingUtilities.invokeLater(
                                 new _UIUPDATER_( (Buffer) _textArea.getBuffer(), _textArea, currentLine )
                              );
    }

  }


  public void setSelectedLine( int selectedLine )
  {
    _selectedLine = selectedLine;
  }

  class _UIUPDATER_ extends Thread
  {
    private Buffer        _uiBuffer;
    private JEditTextArea _uiArea;
    private int           _uiLine;
    public _UIUPDATER_( Buffer buffer, JEditTextArea area, int line )
    {
      _uiBuffer = buffer;
      _uiArea   = area;
      _uiLine   = line;
    }

    private void scrollToCurrentLine( int line, JEditTextArea area )
    {
      line--;
      if (line != -1)
      {
        int            lineStart      = area.getLineStartOffset( line );
        int            offset         =
          Math.max(
                   0,
                   Math.min( area.getLineLength( line ) - 1, area.getCaretPosition() - lineStart )
                );
        DisplayManager displayManager = area.getDisplayManager();

        // _textArea.setCaretPosition(lineStart) ;
        if (!displayManager.isLineVisible( lineStart ))
        {
          if (
              (lineStart < displayManager.getFirstVisibleLine()) ||
                          (lineStart > displayManager.getLastVisibleLine())
             )
          {
            int collapseFolds =
              _uiBuffer.getIntegerProperty(
                                           "collapseFolds",
                                           0
                                        );
            if (collapseFolds != 0)
            {
              displayManager.expandFolds( collapseFolds );
              displayManager.expandFold( lineStart, false );
            }
            else
              displayManager.expandAllFolds();
          }
          else
            displayManager.expandFold( lineStart, false );
        }
        area.scrollTo( line, offset, true );

      }
    }


    public void run()
    {
      if (_uiBuffer.isLoaded())
      {
        Log.log( Log.DEBUG, this, Thread.currentThread().getName() + " Scrolling to line :" + _uiLine );
        scrollToCurrentLine( _uiLine, _uiArea );
        repaint(); // paint updated line position
      }
    }
  }


  class _LINE_PAINTER_ extends TextAreaExtension
  {


    public void paintValidLine(
                               Graphics2D gfx,
                               int        screenLine,
                               int        physicalLine,
                               int        start,
                               int        end,
                               int        y
                            )
    {
      View   cur       = jEdit.getActiveView();
      Buffer curBuffer = cur.getBuffer();

      // if ( cur.getBuffer() != _buffer )
      // return ;
      // Check for JTextArea availability
      if (!curBuffer.isLoaded())
        return;

      if (!PythonJeditPanel.isPython( curBuffer ))
        return;

      int lineHeight = _textArea.getPainter().getFontMetrics().getHeight();

      // BREAKPOINT LINE
      for (int i = 0; i < _bp.length; ++i)
      {
        if (_bp[i].getLineNumber() == (physicalLine + 1))
        {
          if (_bp[i].isInvalid())
            gfx.setColor( bpLineColorInvalid );
          else
            gfx.setColor( bpLineColor );
          gfx.fillRect( 0, y, _textArea.getWidth() - 1, lineHeight - 1 );
        }
      }

      // SELECTED LINE (from trace)
      if (_selectedLine > -1)
      {
        if ((physicalLine + 1) == _selectedLine)
        {
          gfx.setColor( selectedLineColor );
          gfx.fillRect( 0, y, _textArea.getWidth() - 1, lineHeight - 1 );
        }
      }

      // current  buffer is debugging
      if (cur.getBuffer() != _buffer)
        return;

      // CURRENT DEBUG LINE
      if ((_currentLine > -1) && PythonJeditPanel.is_debugging())
      {
        if ((physicalLine + 1) == _currentLine)
        {
          if (filledDebugLine)
          {
            gfx.setColor( currentLineColor );
            gfx.fillRect( 0, y, _textArea.getWidth() - 1, lineHeight - 1 );
          }
          gfx.setColor( currentLineBorderColor );
          gfx.drawRect( 0, y, _textArea.getWidth() - 1, lineHeight - 1 );
        }
      }

    }
  }
}
