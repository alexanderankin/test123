/*
* GenericExporter.java
* Copyright (c) 2009 Romain Francois <francoisromain@free.fr>
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

package code2html.generic ;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.ServiceManager ;


import code2html.* ;
import code2html.services.LinkProvider ;

public abstract class GenericExporter {

    protected Selection[] selection = null;
    protected Buffer buffer ;

    protected Style style = null;
    protected GenericGutter gutter = null;
    protected GenericPainter painter = null;
    protected GenericDocument document = null;
    protected LinkProvider linkProvider = null ;

    public GenericExporter(
        Buffer buffer, SyntaxStyle[] syntaxStyle, Selection[] selection ) {
        this.buffer = buffer ;
        this.selection = selection ;
        Object lp = ServiceManager.getService(
                    "code2html.services.LinkProvider",
                    buffer.getMode().toString()
                ) ;
        if ( lp != null ) {
            this.linkProvider = ( LinkProvider ) lp ;
        }

    }

    public Buffer getDocumentBuffer() {
        return makeBuffer( getDocumentString(), getMode() );
    }

    public Buffer getContentBuffer() {
        return makeBuffer( getContentString(), getMode() );
    }

    public Buffer getStyleBuffer() {
        return makeBuffer( this.getDocumentString(), style.getMode() );
    }

    private Buffer makeBuffer( String str, String mode ) {
        if ( str == null ) {
            return null;
        }
        Buffer newBuffer = jEdit.newFile( jEdit.getActiveView() );
        newBuffer.insert( 0, str );
        if ( mode != null )
            newBuffer.setMode( mode ) ;
        return newBuffer;
    }


    public GenericGutter getGutter() {
        return gutter ;
    }
    public GenericPainter getPainter() {
        return painter ;
    }
    public GenericDocument getDocument() {
        return document ;
    }
    public Style getStyle() {
        return style ;
    }
    public void setStyle( Style style ) {
        this.style = style ;
        this.style.setLinkProvider( this.linkProvider ) ;
    }

    public abstract String getMode() ;

    public String getContentString( ) {
        StringWriter sw = new StringWriter();
        GenericDocument document = getDocument( ) ;
        try {
            BufferedWriter out = new BufferedWriter( sw );

            document.beforeContent( out ) ;
            writeContent( out ) ;
            document.afterContent( out ) ;

            out.flush();
            out.close();
        }
        catch ( IOException ioe ) {
            Log.log( Log.ERROR, this, ioe );
            return null;
        }

        return sw.toString();
    }

    public String getStyleString( ) {
        StringWriter sw = new StringWriter();
        GenericDocument document = getDocument( ) ;
        try {
            BufferedWriter out = new BufferedWriter( sw );

            document.openStyle( out );

            out.flush();
            out.close();
        }
        catch ( IOException ioe ) {
            Log.log( Log.ERROR, this, ioe );
            return null;
        }

        return sw.toString();
    }

    public String getDocumentString() {
        StringWriter sw = new StringWriter();
        GenericDocument document = getDocument( ) ;
        try {
            BufferedWriter out = new BufferedWriter( sw );

            document.open( out );
            document.beforeContent( out ) ;
            writeContent( out ) ;
            document.afterContent( out ) ;
            document.close( out );

            out.flush();
            out.close();
        }
        catch ( IOException ioe ) {
            Log.log( Log.ERROR, this, ioe );
            return null;
        }

        return sw.toString();
    }

    private void writeContent( Writer out ) throws IOException {

        int physicalFirst = 0;
        int physicalLast = buffer.getLineCount() - 1;


        if ( this.selection == null ) {
            this.text( out, physicalFirst, physicalLast );
        }
        else {
            int last = 0;
            for ( int i = 0; i < selection.length; i++ ) {
                if ( selection[ i ].getEndLine() > last ) {
                    last = selection[ i ].getEndLine();
                }
            }

            // Sort selections by their start lines
            Arrays.sort( selection, new SelectionStartLineComparator() );

            if ( getGutter() != null ) {
                getGutter().setGutterSize( Integer.toString( last + 1 ).length() );
            }

            int lastLine = -1;
            for ( int i = 0; i < selection.length; i++ ) {
                physicalFirst = selection[ i ].getStartLine();
                physicalLast = selection[ i ].getEndLine();

                if ( physicalLast <= lastLine ) {
                    continue;
                }

                this.text( out, Math.max( physicalFirst, lastLine + 1 ) , physicalLast );

                lastLine = physicalLast;
            }
        }
    }


    protected void text( Writer out, int first, int last ) throws IOException {
        long start = System.currentTimeMillis();
        this.paintLines( out, buffer, first, last );
        long end = System.currentTimeMillis();
        //Log.log( Log.DEBUG, this, "Time: " + ( end - start ) + " ms" );
    }


    protected void paintLines(
        Writer out, Buffer buffer, int first, int last
    ) throws IOException {
        Segment line = new Segment();
        Token tokens = null;
        for ( int i = first; i <= last; i++ ) {
            buffer.getLineText( i, line );
            GenericPainter painter_ = getPainter() ;
            painter_.setPos( 0 );
            tokens = getTokens( i, buffer ) ;
            painter_.paintSyntaxLine( out, i + 1, line, tokens );
            out.write( painter_.newLine() );
        }
    }

    private Token getTokens( int i , Buffer buffer ) {
        DefaultTokenHandler list = new DefaultTokenHandler() ;
        buffer.markTokens( i, list ) ;
        return list.getTokens() ;
    }

    private class SelectionStartLineComparator implements Comparator {
        public int compare( Object obj1, Object obj2 ) {
            Selection s1 = ( Selection ) obj1;
            Selection s2 = ( Selection ) obj2;

            int diff = s1.getStartLine() - s2.getStartLine();

            if ( diff == 0 ) {
                return 0;
            }
            else if ( diff > 0 ) {
                return 1;
            }
            else {
                return -1;
            }
        }
    }

}