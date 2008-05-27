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
//}}}

///}}}

/**
 * provides methods to start jEdit and to dispose of the robot.
 */
public class TestUtils{

	public static final String ENV_JEDIT_SETTINGS = "test-jedit.settings";
	private static FrameFixture jeditFrame;
	private static RobotFixture robot;

	public static void setUpjEdit(){
		System.out.println("Setting jedit up");
		robot = RobotFixture.robotWithNewAwtHierarchy();
		String settings = System.getProperty(ENV_JEDIT_SETTINGS);
		assertTrue("Forgot to set env. variable '"+ENV_JEDIT_SETTINGS+"'",settings!=null);
		
		final String[] args = {"-settings="+settings,"-norestore","-noserver","-nobackground"};
		Thread runJeditThread = new Thread(){
			public void run(){
				jEdit.main(args);
			}
		};
		runJeditThread.start();
		jeditFrame = WindowFinder.findFrame(View.class).withTimeout(40000).using(robot);
		try{
			Class c = Class.forName(SpellCheckPlugin.class.getName());
		}catch(ClassNotFoundException cnfe){
			fail("Couldn't find plugin's class");
		}
	}

	public static Robot robot(){
		return robot;
	}

	public static FrameFixture jeditFrame(){
		return jeditFrame;
	}
	
	public static  void tearDownjEdit(){
		robot.cleanUp();
	}
}
