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

danson: added some code to the constructor to build an index of lines.
*/

package bigdoc;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FileMap {

    private static AtomicInteger ider = new AtomicInteger();
    private List<ByteBuffer> chunks = new ArrayList<ByteBuffer>();
    private static final long MAX_INT = Integer.MAX_VALUE;
    private long length;
    private File file;
    private RandomAccessFile fileAccessor;

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
                    line.lineLength = Math.max(0, offset - start);
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

    public byte[] get( long offSet, int size ) throws IndexOutOfBoundsException {
        // Quick and dirty but will go wrong for massive numbers
        double a = offSet;
        double b = MAX_INT;
        byte[] dst = new byte[size];
        long whichChunk = ( long ) Math.floor( a / b );
        long withinChunk = offSet - whichChunk * MAX_INT;

        // Data does not straddle two chunks
        if ( MAX_INT - withinChunk > dst.length ) {
            ByteBuffer chunk = chunks.get( ( int ) whichChunk );
            // Allows free threading
            ByteBuffer readBuffer = chunk.asReadOnlyBuffer();
            readBuffer.position( ( int ) withinChunk );
            readBuffer.get( dst, 0, dst.length );
        } else {
            int l1 = ( int ) ( MAX_INT - withinChunk );
            int l2 = ( int ) dst.length - l1;
            ByteBuffer chunk = chunks.get( ( int ) whichChunk );
            // Allows free threading
            ByteBuffer readBuffer = chunk.asReadOnlyBuffer();
            readBuffer.position( ( int ) withinChunk );
            readBuffer.get( dst, 0, l1 );

            chunk = chunks.get( ( int ) whichChunk + 1 );
            readBuffer = chunk.asReadOnlyBuffer();
            readBuffer.position( 0 );
            try {
                readBuffer.get( dst, l1, l2 );
            } catch ( java.nio.BufferUnderflowException e ) {
                throw e;
            }
        }
        return dst;
    }

    public void put( long offSet, byte[] src ) throws IndexOutOfBoundsException {
        // Quick and dirty but will go wrong for massive numbers
        double a = offSet;
        double b = MAX_INT;
        long whichChunk = ( long ) Math.floor( a / b );
        long withinChunk = offSet - whichChunk * MAX_INT;

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
}