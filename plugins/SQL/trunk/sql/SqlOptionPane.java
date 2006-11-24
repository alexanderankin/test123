/**
 * SqlOptionPane.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 * Copyright (C) 2001 Sergey V. Udaltsov
 * svu@users.sourceforge.net
 *
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
package sql;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import common.gui.pathbuilder.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;

import sql.preprocessors.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 ������ 2001 �.
 */
public abstract class SqlOptionPane extends AbstractOptionPane
{
	/**
	 *Constructor for the SqlOptionPane object
	 *
	 * @param  title  Description of Parameter
	 */
	protected SqlOptionPane(String title)
	{
		super(title);
	}


	/**
	 *  Description of the Method
	 */
	public void _init()
	{
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 5, 5, 5));
	}


	/**
	 *Description of the Method
	 *
	 * @param  titlePropertyName  Description of Parameter
	 * @return                    Description of the Returned Value
	 * @since
	 */
	public static Border createTitledBorder(String titlePropertyName)
	{
		return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
		                                        jEdit.getProperty(titlePropertyName));
	}

}

