// $Id$
package ise.plugin.nav;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Iterator;

import javax.swing.JComponent;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PositionChanging;
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
	
	public final static String NAME = "Navigator";
	
	
	/** EditPane / Navigator map */
	private final static HashMap<EditPane, Navigator> map = new HashMap<EditPane, Navigator>();

	public void start()
	{
		for (View v: jEdit.getViews()) {
			createNavigators(v);
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
		map.clear();
		clearToolBars();
	}

	public static void clearToolBars()
	{
		if (jEdit.getBooleanProperty("navigator.showOnToolbar"))
		{
			String toolBarActions = jEdit.getProperty("view.toolbar");
			StringTokenizer st = new StringTokenizer(toolBarActions);
			LinkedList<String> ll = new LinkedList<String>();
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
				Iterator<String> itr = ll.iterator();
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
	 * Adds a Navigator. Navigators are tracked by view.
	 * 
	 * @param view
	 *                The view for the Navigator
	 * @param navigator
	 *                The Navigator
	 */
	public static void addNavigator(EditPane pane, Navigator navigator)
	{
		if (pane== null)
		{
			return;
		}
		if (map.containsKey(pane))
			return;
		map.put(pane, navigator);
	}

	/**
	 * Chooses an arbitrary editpane of the view (works fine if there is only 1) and 
	 * creates a GUI for that navigator.
	 * @param view
	 * @return
	 */
	public static JComponent getToolBar(View view)
	{
		Navigator nav = getNavigator(view.getEditPane());
		NavToolBar toolBar = new NavToolBar(nav);
		return toolBar;
	}

	public static void removeNavigator(View view)
	{
		if (view == null)
		{
			return;
		}
		for (EditPane pane : view.getEditPanes()) { 
			if (map.containsKey(pane))
				map.remove(pane);
		}
	}


	/**
	 * @return null if there is no Navigator for this pane
	 */
	public static Navigator getNavigator(EditPane pane)
	{
		return map.get(pane);
	}

	
	public static void createNavigators(View v) {
		for (EditPane p: v.getEditPanes()) {
			createNavigator(p);
		}
	}
	
	/**
	 * 
	 * @param pane
	 * @return a previously existing or a newly created Navigator for this EditPane.
	 */
	public static Navigator createNavigator(EditPane pane)
	{
		Navigator navigator = getNavigator(pane);
		if (navigator == null)
		{
			navigator = new Navigator(pane);
			addNavigator(pane, navigator);
		}
		return navigator;
	}

	/**
	 * Wrapper for the 'backList' method of the Navigator for the given view.
	 * 
	 * @param view
	 *                The view for the Navigator
	 */
	public static void backList(View view)
	{
		Navigator navigator = getNavigator(view.getEditPane());
		if (navigator != null)
		{
			navigator.backList();
		}
	}

	/**
	 * Wrapper for the 'goBack' method of the Navigator for the given view.
	 * 
	 * @param view
	 *                The view for the Navigator
	 */
	public static void goBack(View view)
	{
		Navigator navigator = getNavigator(view.getEditPane());
		if (navigator != null)
		{
			navigator.goBack();
		}
	}

	/**
	 * Wrapper for the 'forwardList' method of the Navigator for the given
	 * view.
	 * 
	 * @param view
	 *                The view for the Navigator
	 */
	public static void forwardList(View view)
	{
		
		Navigator navigator = getNavigator(view.getEditPane());
		if (navigator != null)
		{
			navigator.forwardList();
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
		
		Navigator navigator = getNavigator(view.getEditPane());
		if (navigator != null)
		{
			navigator.goForward();
		}
	}

	
	public void handleMessage(EBMessage message)
	{
		System.out.println(message.toString());
		/* When we create a new View, create a new navigator for it */
		if (message instanceof ViewUpdate)
		{
			ViewUpdate vu = (ViewUpdate) message;
			View v = vu.getView();
			Object what = vu.getWhat();
			if (what == ViewUpdate.CREATED || what == ViewUpdate.EDIT_PANE_CHANGED) 
				createNavigators(v);
			else if (what.equals(ViewUpdate.CLOSED))
				removeNavigator(v);
		}

		/* If the editpane changes its current position, we want to know
		     just before it happens.  */
		else if (message instanceof PositionChanging) 
		{
			PositionChanging cc = (PositionChanging) message;
			EditPane p = cc.getEditPane();
			Navigator n = getNavigator(p);
			if (n != null) n.update();
		}
		else if (message instanceof EditPaneUpdate) 
		{
			EditPaneUpdate epu = (EditPaneUpdate) message;
			if (epu.getWhat() == EditPaneUpdate.DESTROYED) {
				map.remove(epu.getEditPane());
			}
		}
	}
}
