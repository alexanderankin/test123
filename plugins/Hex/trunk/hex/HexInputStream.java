/*
 * HexInputStream.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
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


package hex;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

import org.gjt.sp.util.Log;


public class HexInputStream extends FilterInputStream
{
    private static byte[] hexBytes = {
          (byte) '0', (byte) '1', (byte) '2', (byte) '3'
        , (byte) '4', (byte) '5', (byte) '6', (byte) '7'
        , (byte) '8', (byte) '9', (byte) 'a', (byte) 'b'
        , (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
    };

    private static final char SEPARATOR_1 = ':';
    private static final char SEPARATOR_2 = ';';

    private final int inBytesPerLine  = 16;
    private int inPos = 0;
    private int offsetPartLen   = 8 + 1 /* offset + space */;
    private int hexPartLen      = (inBytesPerLine * (2 + 1)) + 2 /* middle, end space */;
    private int asciiPartLen    = inBytesPerLine + 1;
    private int outBytesPerLine = (
          offsetPartLen
        + hexPartLen
        + asciiPartLen
        + 1 /* newline */
    );
    private final byte[] outEmptyLine = new byte[outBytesPerLine];
    private final int buf_lines = 1024;
    private final int buf_max_len = outBytesPerLine * buf_lines;
    private int    buf_len = 0;
    private int    buf_off = 0;
    private byte[] out_buf = new byte[buf_max_len];

    private long fillBufTime  = 0;
    private int  fillBufCalls = 0;


    public HexInputStream(InputStream in) {
        super(in);

        byte[] line = this.outEmptyLine;
        int    i    = this.outBytesPerLine - 1;

        line[i--] = (byte) '\n';
        while (i >= 0) {
            line[i--] = (byte) ' ';
        }
    }


    public boolean markSupported() {
        return false;
    }


    public int read() throws IOException {
        if (buf_off >= buf_len) {
            if (fillBuf() == -1) {
                return -1;
            }
        }

        return (int) this.out_buf[buf_off++];
    }


    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }


    public int read(byte[] b, int off, int len) throws IOException {
        int numRead = 0;

        int    bufLen = this.buf_len;
        int    bufOff = this.buf_off;
        byte[] buf    = this.out_buf;

        for (; len > 0; len--) {
            if (bufOff >= bufLen) {
                if (this.fillBuf() == -1) {
                    if (numRead == 0) {
                        return -1;
                    } else {
                        return numRead;
                    }
                }

                bufLen = this.buf_len;
                bufOff = this.buf_off;
                buf    = this.out_buf;
            }

            b[off++] = buf[bufOff++];
            numRead++;
        }

        this.buf_off = bufOff;
        return numRead;
    }


    public void close() throws IOException {
        this.in.close();

        Log.log(Log.DEBUG, this,
              "**** fillBuf: "
            + this.fillBufCalls + " calls"
            + ", "
            + "total: " + this.fillBufTime + " ms"
        );
    }


    private int fillBuf() throws IOException {
        this.buf_off = 0;
        this.buf_len = 0;

        int in_buf_len = this.buf_lines * this.inBytesPerLine;
        byte[] in_buf = new byte[in_buf_len];
        int in_read = this.in.read(in_buf, 0, in_buf_len);

        if (in_read == -1) {
            return in_read;
        }

        long startTime = System.currentTimeMillis();

        byte[] hex   = hexBytes;
        int in_pos   = this.inPos;
        int in_idx   = 0;

        int line_len = this.outBytesPerLine;
        byte[] buf   = this.out_buf;
        int out_len  = 0;
        int out_off  = 0;
        int asciiOff = 0;

        int b;
        int linePos;
        for (; in_read > 0; in_read--) {
            linePos = in_pos % 16;
            if (linePos == 0) {
                out_off  = out_len;
                out_len += line_len;
                System.arraycopy(this.outEmptyLine, 0, buf, out_off, line_len);

                buf[out_off++] = hex[(in_pos & 0xF0000000) >> 28];
                buf[out_off++] = hex[(in_pos & 0x0F000000) >> 24];
                buf[out_off++] = hex[(in_pos & 0x00F00000) >> 20];
                buf[out_off++] = hex[(in_pos & 0x000F0000) >> 16];
                buf[out_off++] = hex[(in_pos & 0x0000F000) >> 12];
                buf[out_off++] = hex[(in_pos & 0x00000F00) >>  8];
                buf[out_off++] = hex[(in_pos & 0x000000F0) >>  4];
                buf[out_off++] = hex[(in_pos & 0x0000000F)      ];
                buf[out_off++] = SEPARATOR_1;

                asciiOff = out_off + hexPartLen;
                buf[asciiOff++] = SEPARATOR_2;
            }

            b = (int) in_buf[in_idx];
            out_off++;
            buf[out_off++] = hex[(b & 0xF0) >> 4];
            buf[out_off++] = hex[(b & 0x0F)     ];

            if ((linePos == 7) || (linePos == 15)) {
                out_off++;
            }

            if ((b >= 0x20) && (b < 0x7f)) {
                buf[asciiOff++] = (byte) b;
            } else {
                buf[asciiOff++] = (byte) '.';
            }

            in_idx++;
            in_pos++;
        }

        this.inPos   = in_pos;
        this.buf_len = out_len;

        this.fillBufTime += (System.currentTimeMillis() - startTime);
        this.fillBufCalls++;

        return this.buf_len;
    }
}

