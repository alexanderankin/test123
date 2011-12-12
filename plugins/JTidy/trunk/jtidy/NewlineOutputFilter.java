/*
 * NewlineOutputFilter.java
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


package jtidy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;


/**
 * Converts all line separators to UNIX
 * When inserting a multiline String into a jEdit Buffer, the line separator
 * must be <code>\n</code> (Unix line separator).
 * This class replaces:
 * <ul>
 * <li>Mac OS <code>\r</code> by <code>\n</code>
 * <li>DOS <code>\r\n</code> by <code>\n</code>
 * </ul>
 *
 * @author Andre Kaplan
 * @version $Id: NewlineOutputFilter.java,v 1.3 2002/03/17 16:39:00 akaplan Exp $
 */
public class NewlineOutputFilter extends FilterOutputStream
{
    private boolean evilDOS = false;

    private final int buf_len = 4096;
    private int buf_idx = 0;
    private byte[] buf = new byte[this.buf_len];


    public NewlineOutputFilter(OutputStream out) {
        super(out);
    }


    public void close() throws IOException {
        if (this.buf_idx > 0) {
            this.out.write(this.buf, 0, this.buf_idx);
            this.buf_idx = 0;
        }
        this.out.close();
    }


    public void flush() throws IOException {
        if (this.buf_idx > 0) {
            this.out.write(this.buf, 0, this.buf_idx);
            this.buf_idx = 0;
        }
        this.out.flush();
    }


    public void write(int b) throws IOException {
        switch (b) {
        case '\n':
            if (this.evilDOS) {
                this.evilDOS = false;
            } else {
                this.buf[this.buf_idx] = (byte) '\n';
                this.buf_idx++;
            }
            break;

        case '\r':
            this.buf[this.buf_idx] = (byte) '\n';
            this.buf_idx++;

            this.evilDOS = true;
            break;

        default:
            this.buf[this.buf_idx] = (byte) b;
            this.buf_idx++;

            this.evilDOS = false;
            break;
        }

        if (this.buf_idx >= this.buf_len) {
            this.out.write(this.buf, 0, this.buf_len);
            this.buf_idx = 0;
        }
    }


    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }


    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = off, j = len - 1; j >= 0; i++, j--) {
            this.write(b[i]);
        }
    }
}

