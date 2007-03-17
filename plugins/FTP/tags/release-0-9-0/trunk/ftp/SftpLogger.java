/*
* SftpLogger.java
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2007 Nicholas O'Leary
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
import org.gjt.sp.util.Log;
import com.jcraft.jsch.*;

public class SftpLogger implements Logger
{
	public boolean isEnabled(int level)
	{
		return level!=Logger.DEBUG;
	}
	
	public void log(int level, java.lang.String message)
	{
		int jeditLog;
		switch(level) {
		case Logger.DEBUG:
			jeditLog = Log.DEBUG;
			break;
		case Logger.ERROR:
			jeditLog = Log.ERROR;
			break;
		case Logger.FATAL:
			jeditLog = Log.ERROR;
			break;
		case Logger.INFO:
			jeditLog = Log.MESSAGE;
			break;
		case Logger.WARN:
			jeditLog = Log.WARNING;
			break;
		default:
			jeditLog = Log.MESSAGE;
		}
		Log.log(jeditLog,this,message);
	}
}
