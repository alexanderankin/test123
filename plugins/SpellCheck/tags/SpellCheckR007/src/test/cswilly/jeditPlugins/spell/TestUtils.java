/*
* $Revision$
* $Date$
* $Author$
*
* Copyright (C) 2008 Eric Le Lay
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

package cswilly.jeditPlugins.spell;


//{{{ Imports

import java.io.*;
import java.awt.Dialog;
import java.awt.Component;
import javax.swing.SwingUtilities;
import javax.swing.JButton;

import javax.swing.tree.*;
import javax.swing.JTree;
import java.util.Arrays;

//{{{ 	jEdit
import org.gjt.sp.jedit.*;

//}}}

//{{{	junit
import org.junit.*;
import static org.junit.Assert.*;
//}}}

//{{{	FEST...
import org.fest.swing.fixture.*;
import org.fest.swing.core.*;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.driver.BasicJTreeCellReader;
//}}}

///}}}

/**
 * common constants
 */
public class TestUtils{

	//common environment variables
	public static final String ENV_ASPELL_EXE	  = "test-jedit.aspell-exe";
	public static final String ENV_TESTS_DIR	  = "test-tests.dir";
}
