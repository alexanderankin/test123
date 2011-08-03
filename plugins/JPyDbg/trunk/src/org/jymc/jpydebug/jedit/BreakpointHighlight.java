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
/*

  Manage Highlight for the text Area : Painter and Gutter
    Breakpoints                           x           x
    CurrentLine                           x           x
*/


import java.util.*;

import org.gjt.sp.jedit.*;



public class BreakpointHighlight 
{

   private Breakpoint[] _bp = null;
   private Hashtable _invalidsBPTab = new Hashtable();
   //private JEditTextArea _textArea;

   private Buffer _buffer;
   private PythonBuffer _pythonBuffer ; 
   private JPYGutterPainter _breakpointHighlightForPainter;

   public Buffer get_buffer()
   { return _buffer ; }

   public BreakpointHighlight( JPYGutterPainter breakpointHighlightForPainter )
                         
   {
	 _breakpointHighlightForPainter =  breakpointHighlightForPainter ;        	
     _buffer = _breakpointHighlightForPainter.get_buffer();
     _bp = new Breakpoint[0];
   }

   public void bufferChanged( PythonBuffer pyBuffer)
   {
     _pythonBuffer = pyBuffer;
     _buffer = pyBuffer.getBuffer();
   }	

   public PythonBuffer get_pythonBuffer()
   { return _pythonBuffer ; }
   
   public void setInvalidBreakpoint( int breakpointInvalidLineNumber, PythonBuffer pyBuf)
   {
     if ( PythonJeditPanel.isPython(_buffer))
     {
       if (pyBuf.getBuffer().equals(_buffer)) 
       {
         for (int i = 0; i < _bp.length; i++) 
         {
         Breakpoint b = _bp[i];
           if (b.getLineNumber() == breakpointInvalidLineNumber)
             b.setInvalid();
         }
         Integer ii = new Integer(breakpointInvalidLineNumber);
         _invalidsBPTab.put(ii,ii);
       }
     }
   }

   public void setBreakPoints(Hashtable breakpoints) 
   {
   Enumeration en = breakpoints.elements();
   int i =0;
   _bp = new Breakpoint[breakpoints.size()];
     while (en.hasMoreElements()) 
     {
       Integer ii = (Integer) en.nextElement();
       int numLine = ii.intValue();
       _bp[i] = new Breakpoint(numLine);
       if (_invalidsBPTab.contains(ii)){
         _bp[i].setInvalid();
         //invalidsBPTab.remove(ii);
       }
        i++;
     }
     _breakpointHighlightForPainter.setBreakPoints(_bp);
   }



}
