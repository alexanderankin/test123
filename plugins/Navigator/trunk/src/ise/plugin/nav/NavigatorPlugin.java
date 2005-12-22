// $Id$
package ise.plugin.nav;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;

import javax.swing.JComponent;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.ViewUpdate;

/**
 * NavigatorPlugin, mostly static methods, allows one Navigator per View.
 * 
 * @author Dale Anson, danson@germane-software.com
 * @version $Revision$
 * @since Oct 25, 2003
 */
public class NavigatorPlugin extends EBPlugin
{

	/** Description of the Field */
	public final static String NAME = "Navigator";

	/** Description of the Field */
	public final static String MENU = "Navigator.menu";

	/** View/Navigator map */
	private final static HashMap map = new HashMap();

	public void start()
	{
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; ++i)
		{
			createNavigator(views[i]);
		}
		setToolBars();
	}

	public static void showButtons()
	{
		jEdit.setBooleanProperty("navigator.showOnToolbar", true);
		setToolBars();
	}

	public static void hideButtons()
	{
		jEdit.setBooleanProperty("navigator.showOnToolbar", false);
		clearToolBars();
	}

	public static void revalidateViews()
	{
		View views[] = jEdit.getViews();
		for (int i = 0; i < views.length; ++i)
		{
			if (views[i] != null)
			{
				views[i].getRootPane().revalidate();
			}
		}
	}

	public static void setToolBars()
	{

		if (jEdit.getBooleanProperty("navigator.showOnToolbar"))
		{
			String toolBarActions = jEdit.getProperty("view.toolbar");
			StringTokenizer st = new StringTokenizer(toolBarActions);
			boolean found = false;
			while (st.hasMoreTokens())
			{
				String actionName = st.nextToken();
				if (actionName.equals("navigator.back")
					|| actionName.equals("navigator.forward"))
				{
					found = true;
					break;
				}
			}

			if (!found)
			{
				toolBarActions = "navigator.back navigator.forward - "
					+ toolBarActions;
				jEdit.setProperty("view.toolbar", toolBarActions);
			}
		}
		revalidateViews();
	}

	public void stop()
	{
		clearToolBars();
	}

	public static void clearToolBars()
	{
		if (jEdit.getBooleanProperty("navigator.showOnToolbar"))
		{
			String toolBarActions = jEdit.getProperty("view.toolbar");
			StringTokenizer st = new StringTokenizer(toolBarActions);
			LinkedList ll = new LinkedList();
			boolean found = false;
			while (st.hasMoreTokens())
			{
				String actionName = st.nextToken();
				if (actionName.equals("navigator.back")
					|| actionName.equals("navigator.forward"))
				{
					found = true;
				}
				else
					ll.add(actionName);
			}

			if (found)
			{
				StringBuffer sb = new StringBuffer();
				Iterator itr = ll.iterator();
				while (itr.hasNext())
				{
					sb.append(itr.next() + " ");
				}
				jEdit.setProperty("view.toolbar", sb.toString());
			}
		}
		revalidateViews();

	}

	/**
	 * create the menu items for the Plugins menu
	 * 
	 * @param menuItems
	 */
	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu(MENU));
	}

	/**
	 * Adds a Navigator. Navigators are tracked by view.
	 * 
	 * @param view
	 *                The view for the Navigator
	 * @param navigator
	 *                The Navigator
	 */
	public static void addNavigator(View view, Navigator navigator)
	{
		if (view == null)
		{
			return;
		}
		if (map.containsKey(view))
			return;
		map.put(view, navigator);
	}

	public static JComponent getToolBar(View view)
	{
		Navigator nav = getNavigator(view);
		NavToolBar toolBar = new NavToolBar(nav);
		return toolBar;
	}

	public static void removeNavigator(View view)
	{
		if (view == null)
		{
			return;
		}
		if (!map.containsKey(view))
			return;
		map.remove(view);
	}

	public static Navigator getNavigator(View view)
	{
		return (Navigator) map.get(view);
	}

	public static Navigator createNavigator(View view)
	{
		Navigator navigator = getNavigator(view);
		if (navigator == null)
		{
			navigator = new Navigator(view);
			addNavigator(view, navigator);
		}
		return navigator;
	}

	/**
	 * Wrapper for the 'goBack' method of the Navigator for the given view.
	 * 
	 * @param view
	 *                The view for the Navigator
	 */
	public static void goBack(View view)
	{
		Navigator navigator = (Navigator) map.get(view);
		if (navigator != null)
		{
			navigator.goBack();
		}
	}

	/**
	 * Wrapper for the 'goForward' method of the Navigator for the given
	 * view.
	 * 
	 * @param view
	 *                The view for the Navigator
	 */
	public static void goForward(View view)
	{
		Navigator navigator = (Navigator) map.get(view);
		if (navigator != null)
		{
			navigator.goForward();
		}
	}

	public void handleMessage(EBMessage message)
	{
		System.out.println(message.toString());
		if (message instanceof ViewUpdate)
		{
			ViewUpdate vu = (ViewUpdate) message;
			View v = vu.getView();
			if (vu.getWhat() == vu.CREATED)
			{
				createNavigator(v);
			}
		}
	}
}
