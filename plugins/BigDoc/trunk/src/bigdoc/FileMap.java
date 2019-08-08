/*

Full source code to article on JAXenter.com:
http://jaxenter.com/high-speed-multi-threaded-virtual-memory-in-java.1-46188.html

===============

Copyright (c) 2013, Dr Alexander J Turner
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met: 

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer. 
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies, 
either expressed or implied, of Dr Turner.

danson: added some code to the constructor to build an index of lines, support
inserts, deletes, and saves, did some clean up to make this academic code more
readable and usable.
*/

package bigdoc;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileMap {

    private static AtomicInteger ider = new AtomicInteger();
    private List<ByteBuffer> chunks = new ArrayList<ByteBuffer>();
    private static final long MAX_INT = Integer.MAX_VALUE;
    private long length;
    private File file;
    private RandomAccessFile fileAccessor;
    private Set<Edit> edits = new TreeSet<Edit>();

    // <line number, line details>
    private Map<Long, LineDetails> lines = new HashMap<Long, LineDetails>();

    private static int getUniqueId() {
        return ider.incrementAndGet();
    }

    public FileMap( String filename ) throws IOException {
        file = new File( filename );
        long size = file.length();

        fileAccessor = new RandomAccessFile( file, "rw" );
        FileChannel channelMapper = fileAccessor.getChannel();

        // map file to byte buffers
        long nChunks = size / MAX_INT;
        if ( nChunks > Integer.MAX_VALUE ) {
            throw new ArithmeticException( "Requested File Size Too Large" );
        }
        length = size;
        long countDown = size;
        long from = 0;
        while ( countDown > 0 ) {
            long len = Math.min( MAX_INT, countDown );
            ByteBuffer chunk = channelMapper.map( FileChannel.MapMode.READ_WRITE, from, len );
            chunks.add( chunk );
            from += len;
            countDown -= len;
        }

        // map of lines
        long lineNumber = 0l;
        long offset = 0l;
        long start = 0l;
        for ( ByteBuffer chunk : chunks ) {
            while ( chunk.hasRemaining() ) {
                byte b = chunk.get();
                ++offset;
                if ( b == '\n' ) {
                    LineDetails line = new LineDetails();
                    line.lineStartOffset = start;
                    line.lineEndOffset = offset;
                    line.lineLength = Math.max( 0, offset - start );
                    lines.put( lineNumber, line );
                    start = offset;
                    ++lineNumber;
                }
            }
        }
    }

    public Map<Long, LineDetails> getLineDetails() {
        return lines;
    }

    public byte[] get( long offset, int size ) throws IndexOutOfBoundsException {
        //System.out.println( "+++++ get( " + offset + ", " + size + ')' );
        // check edits, adjust size as necessary, may need to asjust size to
        // account for inserts and deletes
        int editCount = 0;
        for ( Edit edit : edits ) {
            switch ( edit.type ) {
                case Edit.INSERT:
                    editCount += edit.count;
                    break;
                case Edit.DELETE:
                    editCount -= edit.count;
                    break;
            }
        }
        int readSize = Math.max( size, size - editCount );        // minus is correct, need to read less if there are inserts, more if there are deletes
        // int readSize = size;
        //System.out.println( "+++++ size = " + size + ", readSize = " + readSize + ", editCount = " + editCount );
        double a = offset;
        double b = MAX_INT;
        byte[] fileBytes = new byte[readSize];
        long whichChunk = ( long ) Math.floor( a / b );
        long withinChunk = offset - whichChunk * MAX_INT;

        // Data does not straddle two chunks
        if ( MAX_INT - withinChunk > fileBytes.length ) {
            ByteBuffer chunk = chunks.get( ( int ) whichChunk );
            // Allows free threading
            ByteBuffer readBuffer = chunk.asReadOnlyBuffer();
            readBuffer.position( ( int ) withinChunk );
            readBuffer.get( fileBytes, 0, fileBytes.length );
        } else {
            int bufferOffset = ( int ) ( MAX_INT - withinChunk );
            int readLength = ( int ) fileBytes.length - bufferOffset;
            ByteBuffer chunk = chunks.get( ( int ) whichChunk );
            // Allows free threading
            ByteBuffer readBuffer = chunk.asReadOnlyBuffer();
            readBuffer.position( ( int ) withinChunk );
            readBuffer.get( fileBytes, 0, bufferOffset );

            chunk = chunks.get( ( int ) whichChunk + 1 );
            readBuffer = chunk.asReadOnlyBuffer();
            readBuffer.position( 0 );
            try {
                readBuffer.get( fileBytes, bufferOffset, readLength );
            } catch ( java.nio.BufferUnderflowException e ) {
                throw e;
            }
        }

        if ( edits.isEmpty() ) {
            //System.out.println( "+++++ returning fileBytes" );
            return fileBytes;
        }

        // file bytes as byte buffer
        ByteBuffer fileBuffer = ByteBuffer.allocate( fileBytes.length );
        fileBuffer.put( fileBytes );
        fileBuffer.position( 0 );
        // new buffer with inserts/deletes added
        ByteBuffer returnBytes = ByteBuffer.allocate( size );

        Iterator<Edit> editIterator = edits.iterator();
        Edit currentEdit = editIterator.next();

        while ( fileBuffer.hasRemaining() && returnBytes.hasRemaining() ) {
            //System.out.println( "+++++ currentEdit: " + currentEdit );
            long editOffset;
            if ( currentEdit == null ) {
                editOffset = fileBuffer.capacity();
            } else {
                editOffset = currentEdit.offset - offset;
            }
            while ( fileBuffer.position() < editOffset && fileBuffer.hasRemaining() && returnBytes.hasRemaining() ) {
                returnBytes.put( fileBuffer.get() );
            }
            if ( currentEdit != null ) {
                if ( currentEdit.type == Edit.INSERT ) {
                    returnBytes.put( currentEdit.data );
                } else {
                    fileBuffer.position( fileBuffer.position() + currentEdit.count );
                }
            }
            if ( editIterator.hasNext() ) {
                currentEdit = editIterator.next();
            } else {
                currentEdit = null;
            }
        }
        //System.out.println( "+++++ returning returnBytes: " + returnBytes.array().length );
        return returnBytes.array();
    }

    // this overwrites content at the given offset
    public void put( long offset, byte[] src ) throws IndexOutOfBoundsException {
        // Quick and dirty but will go wrong for massive numbers
        double a = offset;
        double b = MAX_INT;
        long whichChunk = ( long ) Math.floor( a / b );
        long withinChunk = offset - whichChunk * MAX_INT;

        // Data does not straddle two chunks
        if ( MAX_INT - withinChunk > src.length ) {
            ByteBuffer chunk = chunks.get( ( int ) whichChunk );
            // Allows free threading
            ByteBuffer writeBuffer = chunk.duplicate();
            writeBuffer.position( ( int ) withinChunk );
            writeBuffer.put( src, 0, src.length );
        } else {
            int l1 = ( int ) ( MAX_INT - withinChunk );
            int l2 = ( int ) src.length - l1;
            ByteBuffer chunk = chunks.get( ( int ) whichChunk );
            // Allows free threading
            ByteBuffer writeBuffer = chunk.duplicate();
            writeBuffer.position( ( int ) withinChunk );
            writeBuffer.put( src, 0, l1 );

            chunk = chunks.get( ( int ) whichChunk + 1 );
            writeBuffer = chunk.duplicate();
            writeBuffer.position( 0 );
            writeBuffer.put( src, l1, l2 );
        }
    }

    // TODO: need method "insert(long offset, byte[] src)
    public void insert( long offset, byte[] src ) {
        Edit ed = new Edit();
        ed.type = Edit.INSERT;
        ed.offset = offset;
        ed.data = Arrays.copyOf( src, src.length );
        ed.count = src.length;
        edits.add( ed );
        //System.out.println( "+++++ " + ed );
    }

    public void insert( long offset, String str ) {
        insert( offset, str.getBytes() );
    }

    // delete a single byte
    public void delete( long offset ) {
        delete( offset, 1 );
    }

    public void delete( long offset, int count ) {
        Edit ed = new Edit();
        ed.type = Edit.DELETE;
        ed.offset = offset;
        ed.count = count;
        edits.add( ed );
    }

    public void save() {
        // TODO: finish this!
        for ( Edit edit : edits ) {

        }
    }

    public void purge() {
        if ( fileAccessor != null ) {
            try {
                fileAccessor.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            } finally {
                file.delete();
            }
        }
    }

    public long getSize() {
        return length;
    }

    public class LineDetails {
        public long lineStartOffset = 0;
        public long lineLength = 0;
        public long lineEndOffset = 0;

        public String toString() {
            return "LineDetails: " + lineStartOffset + ',' + lineLength + ',' + lineEndOffset;
        }
    }

    public class Range {
        public long start = 0;
        public long end = 0;

        public Range() {

        }

        public Range( int start, int end ) {
            this.start = start;
            this.end = end;
        }

        public boolean equals( Object other ) {
            if ( ! ( other instanceof Range ) ) {
                return false;
            }
            Range r = ( Range ) other;
            return r.start == this.start && r.end == this.end;
        }

        public int hashCode() {
            return ( int ) start * 100000 + ( int ) end;
        }

        public String toString() {
            return "Range: [" + start + ", " + end + ']';
        }
    }

    public class Edit implements Comparable<Edit> {
        public long offset = 0;
        public static final int INSERT = 1;
        public static final int DELETE = 2;
        public int type = INSERT;
        public byte[] data;
        public int count = 0;

        public boolean equals( Object other ) {
            if ( ! ( other instanceof Edit ) ) {
                return false;
            }
            return offset == ( ( Edit ) other ).offset;
        }

        public int hashCode() {
            return ( int ) offset;
        }

        public int compareTo( Edit other ) {
            return Long.compare( offset, other.offset );
        }

        public String toString() {
            return new StringBuilder( "Edit:[" ).append( "offset=" ).append( offset ).append( ",type=" ).append( type ).append( ",count=" ).append( count ).append( ']' ).toString();
        }
    }
}