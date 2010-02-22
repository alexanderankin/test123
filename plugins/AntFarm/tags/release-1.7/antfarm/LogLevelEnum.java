/*
 *  LogLevelEnum.java - Enum to hold logging levels for Ant
 *  Copyright (C) 2001 Brian Knowles
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package antfarm;

import org.apache.tools.ant.*;

class LogLevelEnum {
	private final String _name;
	private final int _value;
	
	public static final LogLevelEnum ERROR =
		new LogLevelEnum("Error", Project.MSG_ERR);
	public static final LogLevelEnum WARNING =
		new LogLevelEnum("Warning", Project.MSG_WARN);
	public static final LogLevelEnum INFO =
		new LogLevelEnum("Information", Project.MSG_INFO);
	public static final LogLevelEnum VERBOSE =
		new LogLevelEnum("Verbose", Project.MSG_VERBOSE);
	public static final LogLevelEnum DEBUG =
		new LogLevelEnum("Debug", Project.MSG_DEBUG);
	
	public static LogLevelEnum[] getAll()
	{
		return new LogLevelEnum[]{ERROR, WARNING, INFO, VERBOSE, DEBUG};
	}
	
	public static LogLevelEnum getLogLevel(int level)
	{
		for(int i = 0; i < getAll().length; i++) {
			if (getAll()[i].getValue() == level)
				return getAll()[i];
		}
		return null;
	}

	private LogLevelEnum(String name, int value) 
	{
		_name = name;
		_value = value;
	}
	
	public String toString() 
	{
		return _name;
	}
	
	public int getValue()
	{
		return _value;
	}
	
	public boolean equals(Object o)
	{
		if (!(o instanceof LogLevelEnum))
			return false;
			
		if (((LogLevelEnum)o).getValue() == getValue())
			return true;
			
		return false;
	}

}

