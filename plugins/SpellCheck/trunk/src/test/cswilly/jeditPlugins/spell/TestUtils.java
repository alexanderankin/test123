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
