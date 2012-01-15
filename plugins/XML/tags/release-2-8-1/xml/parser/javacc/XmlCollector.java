/*
* XmlCollector.java -- structures an HTML document tree.  
* Copyright (C) 1999 Quiotix Corporation.  
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License, version 2, as 
* published by the Free Software Foundation.  
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License (http://www.gnu.org/copyleft/gpl.txt)
* for more details.
*/

package xml.parser.javacc;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * An XmlVisitor which modifies the structure of the document so that
 * begin tags are matched properly with end tags and placed in TagBlock
 * elements.  Typically, an XmlDocument is created by the parser, which
 * simply returns a flat list of elements.  The XmlCollector takes this
 * flat list and gives it the structure that is implied by the HTML content.
 *
 * @author Brian Goetz, Quiotix
 */

public class XmlCollector extends XmlVisitor {

    protected ElementStack tagStack = new ElementStack();
    protected ElementStack elements;
    protected boolean collected;

    private static class TagStackEntry {
        String tagName;
        int index;
    }

    private static class ElementStack extends Vector {
        ElementStack() {
            super();
        }

        ElementStack( int n ) {
            super( n );
        }

        public void popN( int n ) {
            elementCount -= n;
        }
    }

    protected int pushNode( XmlDocument.XmlElement e ) {
        if (e != null) {
            elements.addElement( e );
        }
        return elements.size() - 1;
    }
                                                             
    public void visit( XmlDocument.Comment c ) {
            pushNode( c );
    }

    public void visit( XmlDocument.Text t ) {
        pushNode( t );
    }

    public void visit( XmlDocument.Newline n ) {
        pushNode( n );
    }

    public void visit( XmlDocument.Tag t ) {
        if (t == null) {
            return;   
        }
        TagStackEntry ts = new TagStackEntry();
        int index;

        /* Push the tag onto the element stack, and push an entry on the tag
        stack if it's a tag we care about matching */
        index = pushNode( t );
        if ( !t.emptyTag ) {
            ts.tagName = t.tagName;
            ts.index = index;
            tagStack.addElement( ts );
        }
    }

    public void visit( XmlDocument.EndTag t ) {
        if (t == null)
            return;
        int i;
        for ( i = tagStack.size() - 1; i >= 0; i-- ) {
            TagStackEntry ts = ( TagStackEntry ) tagStack.elementAt( i );
            if ( t.tagName.equals( ts.tagName ) ) {
                XmlDocument.TagBlock block;
                XmlDocument.ElementSequence blockElements;
                XmlDocument.Tag tag;

                // Create a new ElementSequence and copy the elements to it
                blockElements = new XmlDocument.ElementSequence( elements.size() - ts.index - 1 );
                for ( int j = ts.index + 1; j < elements.size(); j++ ) {
                    blockElements.addElement( ( XmlDocument.XmlElement ) elements.elementAt( j ) );
                }
                tag = ( XmlDocument.Tag ) elements.elementAt( ts.index );
                block = new XmlDocument.TagBlock( tag, blockElements, t );
                block.setStartLocation( tag.getStartLocation() );
                block.setEndLocation( t.getEndLocation() );

                // Pop the elements off the stack, push the new block
                elements.popN( elements.size() - ts.index );
                elements.addElement( block );

                // Pop the matched tag and intervening unmatched tags
                tagStack.popN( tagStack.size() - i );

                collected = true;
                break;
            }
        }

        // If we didn't find a match, just push the end tag
        if ( i < 0 )
            pushNode( t );
    }

    public void visit( XmlDocument.TagBlock bl ) {
        if (bl == null)
            return;
        XmlCollector c = new XmlCollector();
        c.start();
        c.visit( bl.body );
        c.finish();
        pushNode( bl );
    }

    public void visit( XmlDocument.ElementSequence s ) {
        if ( s == null )
            return ;
        elements = new ElementStack( s.size() );
        collected = false;

        for ( Iterator iterator = s.iterator(); iterator.hasNext(); ) {
            XmlDocument.XmlElement htmlElement = ( XmlDocument.XmlElement ) iterator.next();
            if ( htmlElement != null )
                htmlElement.accept( this );
        }
        if ( collected )
            s.setElements( elements );
    }

    public static void main( String[] args ) throws Exception {
        InputStream r = new FileInputStream( args[ 0 ] );

        try {
            XmlDocument document = new XmlParser( r ).XmlDocument();
            document.accept( new XmlScrubber() );
            document.accept( new XmlCollector() );
            document.accept( new XmlDumper( System.out ) );
        }
        finally {
            r.close();
        }
    }
}
