/*
 *  AntFarmLogger.java - Plugin for running Ant builds from jEdit.
 *  Copyright (C) 2001 John Gellene
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

import java.io.*;
import javax.swing.*;
import org.apache.tools.ant.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.*;

public class AntFarmLogger extends DefaultLogger
{
	public AntFarmLogger()
	{
		this(Project.MSG_INFO);
	}

	public AntFarmLogger(int level)
	{
		super();
		int logLevel = level;
		if(level < Project.MSG_ERR)
			logLevel = Project.MSG_ERR;
		else if(level > Project.MSG_DEBUG)
			logLevel = Project.MSG_DEBUG;
		setMessageOutputLevel(logLevel);
	}
}



