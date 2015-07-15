/*
 * ChainedIOException.java
 * Copyright (c) 2007 Dirk Moebius
 *
 * jEdit edit mode settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
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

package javainsight.buildtools;

import java.io.IOException;

/**
 * Chained IOException (Java1.4+).
 *
 * @author Dirk Moebius
 * @version $Id$
 */
public class ChainedIOException extends IOException
{
    private final Throwable cause;

    public ChainedIOException(String message) {
        super(message);
        this.cause = null;
    }

    public ChainedIOException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public ChainedIOException(Throwable cause) {
        super();
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return cause;
    }

}
