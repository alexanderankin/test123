package bigdoc;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.swing.SwingWorker;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;


/**
 * A document that uses a memory map as a backing store. This document can
 * support files up to Integer.MAX_VALUE size, which for current Java 11 is
 * 2,147,483,647 bytes or 2 GB.
 */
public class BigTextDocument implements Document {

    private File file;
    private ArrayList<DocumentListener> documentListeners = null;
    private ArrayList<UndoableEditListener> editListeners = null;
    private HashMap<Object, Object> properties = null;
    private ArrayList<LineDetails> lines;
    private Element rootElement;
    private ByteBuffer chunk;

    /**
     * @param filename The name of a local file to open.
     * @param listener A PropertyChangeListener that will be notified when the file is 
     * loaded. The change event will be named "lineLoader" and the value will be "done"
     * when the document has finished loading.
     */
    public BigTextDocument( String filename, PropertyChangeListener listener ) {
        Objects.requireNonNull( filename );
        file = new File( filename );
        if ( !file.exists() || file.isDirectory() ) {
            throw new IllegalArgumentException( "Invalid file." );
        }
        if ( file.length() > Integer.MAX_VALUE ) {
            throw new IllegalArgumentException( "File is too large, it must be smaller than " + Integer.MAX_VALUE + " bytes." );
        }
        rootElement = new RootElement();
        LineLoader lineLoader = new LineLoader( file );
        lineLoader.addPropertyChangeListener( listener );
        lineLoader.execute();
    }

    /**
     * Required by Document interface.
     */
    public void addDocumentListener( DocumentListener listener ) {
        Objects.requireNonNull( listener );
        if ( documentListeners == null ) {
            documentListeners = new ArrayList<DocumentListener>();
        }
        documentListeners.add( listener );
    }

    public void addUndoableEditListener( UndoableEditListener listener ) {
        if ( editListeners != null ) {
            editListeners = new ArrayList<UndoableEditListener>();
        }
        editListeners.add( listener );
    }

    /**
     * Required by Document interface.
     */
    public Position createPosition( int offs ) {
        return new Position(){

            public int getOffset() {
                return offs;
            }
        };
    }

    public Element getDefaultRootElement() {
        return rootElement;
    }

    /**
     * Required by Document interface.
     */
    public Position getEndPosition() {
        return new Position(){

            public int getOffset() {
                return BigTextDocument.this.getLength();
            }
        };
    }

    public int getLength() {
        return ( int )file.length();
    }

    /**
     * Required by Document interface.
     */
    public Object getProperty( Object key ) {
        if ( properties == null ) {
            return null;
        }
        return properties.get( key );
    }

    /**
     * Required by Document interface, returns the single root element as
     * returned by getDefaultRootElement.
     */
    public Element[] getRootElements() {
        Element[] roots = new Element [1];
        roots[0] = getDefaultRootElement();
        return roots;
    }

    /**
     * Required by Document interface.
     */
    public Position getStartPosition() {
        return new Position(){

            public int getOffset() {
                return 0;
            }
        };
    }

    /**
     * @param offset offset in the file
     * @param length the number of bytes to read
     * @return some document text
     */
    public String getText( int offset, int length ) {
        if ( offset > chunk.capacity() || length <= 0 ) {
            return "";
        }
        if ( offset + length > chunk.capacity() ) {
            length = chunk.capacity() - offset;
        }
        chunk.rewind();
        chunk.position( offset );
        byte[] bytes = new byte [length];

        // ByteBuffer.get, the 0 and length are for the bytes array, not the file
        chunk.get( bytes, 0, length );
        return new String( bytes );
    }

    /**
     * @param offset offset in the file
     * @param length the number of bytes to read
     * @param txt a Segment in which to put the results
     */
    public void getText( int offset, int length, Segment txt ) {

        // Segment is char[] array, int count, int offset
        String s = getText( offset, length );
        txt.array = s.toCharArray();
        txt.count = s.length();
        txt.offset = 0;    // offset in the char array, not the file
    }

    public void insertString( int offset, String str, AttributeSet a ) {
        // not implemented, at the moment, this is read-only
    }

    /**
     * Required by Document interface.
     */
    public void putProperty( Object key, Object value ) {
        if ( properties == null ) {
            properties = new HashMap<Object, Object>();
        }
        properties.put( key, value );
    }

    /**
     * Required by Document interface.
     */
    public void remove( int offs, int len ) {
        // not implemented, at the moment, this is read-only
    }

    /**
     * Required by Document interface.
     */
    public void removeDocumentListener( DocumentListener listener ) {
        if ( documentListeners != null ) {
            documentListeners.remove( listener );
        }
        if ( documentListeners.isEmpty() ) {
            documentListeners = null;
        }
    }

    /**
     * Required by Document interface.
     */
    public void removeUndoableEditListener( UndoableEditListener listener ) {
        if ( editListeners != null ) {
            editListeners.remove( listener );
        }
        if ( editListeners.isEmpty() ) {
            editListeners = null;
        }
    }

    /**
     * Required by Document interface.
     */
    public void render( Runnable r ) {
        try {
            r.run();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    // root element, children are represented by LineDetails. Child elements are
    // created on the fly as needed.
    private class RootElement implements Element {

        // no attributes
        public AttributeSet getAttributes() {
            return null;
        }

        public Document getDocument() {
            return BigTextDocument.this;
        }

        // lazy creation of child elements as needed
        public Element getElement( int index ) {
            LineDetails line = lines.get( index );
            return new LineElement( this, line.getStartOffset(), line.getEndOffset() );
        }

        public int getElementCount() {
            return lines.size();
        }

        public int getElementIndex( int offset ) {

            // return the index of the first line if the offset is 0 or less
            if ( offset <= 0 ) {
                return 0;
            }

            // return the index of the last line if the offset is greater than the length of the document
            if ( offset >= BigTextDocument.this.getLength() ) {
                return getElementCount() - 1;
            }

            // use Newton's method to quickly find line containing offset. This works
            // well because the lines are ordered in the list. In testing, this usually
            // found the right line after checking just 10 - 12 entries out of 20000, so
            // it should be way faster than just iterating through the list one at a time.
            int searchStart = 0;
            int searchEnd = lines.size() - 1;
            int searchIndex = Math.floorDiv( lines.size(), 2 );
            while ( true ) {
                LineDetails line = lines.get( searchIndex );
                if ( line.start > offset ) {
                    searchEnd = searchIndex;
                    searchIndex = searchEnd - Math.floorDiv( searchEnd - searchStart, 2 ) - 1;
                }
                else if ( line.end < offset ) {
                    searchStart = searchIndex;
                    searchIndex = searchStart + Math.floorDiv( searchEnd - searchStart, 2 );
                    if ( searchIndex == searchStart ) {
                        searchIndex += 1;
                    }
                }
                else {
                    return searchIndex;
                }
            }
        }

        public int getEndOffset() {
            return BigTextDocument.this.getLength();
        }

        public String getName() {
            return "root";
        }

        public Element getParentElement() {
            return null;
        }

        public int getStartOffset() {
            return 0;
        }

        public boolean isLeaf() {
            return false;
        }
    }


    // LineElement represents a single line of a file, they have no children.
    private class LineElement implements Element {

        private Element parent;
        private int startOffset;
        private int endOffset;

        public LineElement( Element parent, int startOffset, int endOffset ) {
            this.parent = parent;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }

        public AttributeSet getAttributes() {
            return null;
        }

        public Document getDocument() {
            return BigTextDocument.this;
        }

        public Element getElement( int index ) {
            return null;
        }

        public int getElementCount() {
            return 0;
        }

        public int getElementIndex( int offset ) {
            return -1;
        }

        public int getEndOffset() {
            return endOffset;
        }

        public String getName() {
            return "line";
        }

        public Element getParentElement() {
            return parent;
        }

        public int getStartOffset() {
            return startOffset;
        }

        public boolean isLeaf() {
            return true;
        }
    }

    // LineDetails simply tracks the line number, start offset, and endOffset,
    // it does not store any text. One of these is create per line in the document
    // and stored in memory.
    private class LineDetails {

        private int lineNumber;
        private int start = 0;
        private int end = 0;

        public LineDetails( int lineNumber, int start, int end ) {
            this.lineNumber = lineNumber;
            this.start = start;
            this.end = end;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public int getStartOffset() {
            return start;
        }

        public int getEndOffset() {
            return end;
        }

        public int getLength() {
            return end - start + 1; // TODO: confirm the +1 is necessary
        }

        public String toString() {
            return "LineDetails: " + getStartOffset() + ',' + getLength() + ',' + getEndOffset();
        }
    }

    // Scans the files for line separators and creates a list of LineDetails objects.
    private class LineLoader extends SwingWorker <ArrayList, Object[]> {

        public LineLoader( File file ) {
            try {
                int length = ( int )file.length();
                RandomAccessFile fileAccessor = new RandomAccessFile( file, "r" );
                FileChannel channelMapper = fileAccessor.getChannel();
                chunk = channelMapper.map( FileChannel.MapMode.READ_ONLY, 0, length );
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        @Override
        protected ArrayList doInBackground() {
            lines = new ArrayList<LineDetails>();
            int lineNumber = 0;
            int offset = 0;
            int start = 0;
            byte[] temp = new byte [1024 * 32];
            while ( chunk.hasRemaining() ) {
                int length = Math.min( temp.length, chunk.remaining() );
                chunk.get( temp, 0, length );
                for ( int i = 0; i < length; i++ ) {
                    byte b = temp[i];
                    ++offset;
                    if ( b == '\r' ) {

                        // Windows file, read next byte for \n. I'm not worrying about
                        // old-style Mac files, Mac switched to Unix line separators
                        // years ago.
                        continue;
                    }
                    if ( b == '\n' || i == length - 1 ) {
                        LineDetails line = new LineDetails( lineNumber, start, offset );
                        lines.add( line );
                        start = offset;
                        ++lineNumber;
                    }
                }
            }
            return lines;
        }

        protected void done() {
            firePropertyChange( "lineLoader", "", "done" );
            chunk.rewind();
        }
    }
}
