/*
 * FtpException.java - FTP error
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Slava Pestov
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

package ftp;

//{{{ Imports
import com.fooware.net.FtpResponse;
import java.io.IOException;
import org.gjt.sp.jedit.jEdit;
//}}}

public class FtpException extends IOException
{
	public FtpException(FtpResponse response)
	{
		super(jEdit.getProperty("ftperror",new String[] {
			response.toString() }));
	}
}
