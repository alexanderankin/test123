/*
 * DockFocusManager.java
 * :tabSize=3:indentSize=3:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Calvin Yu
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

package docker;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.PanelWindowContainer;
import org.gjt.sp.jedit.msg.ViewUpdate;

import org.gjt.sp.util.Log;

/**
 * Manages focus handling for docks and provides default docking facilities.
 */
class DockFocusManager implements EBComponent, ContainerListener
{

	// map of previous focus traversal policies (before we overrode them)
	Map<View, FocusTraversalPolicy> ftps = new HashMap<View, FocusTraversalPolicy>();

	// containers which have this as a ContainerListener
	Stack <Container> clist = new Stack<Container>();

	public void handleMessage(EBMessage msg)
	{
		if (msg instanceof ViewUpdate)
		{
			ViewUpdate vu = (ViewUpdate) msg;
			if (vu.getWhat() == ViewUpdate.CREATED)
			{
				installFocusTraversalPolicy(vu.getView());
				setFocusCycleRoots(vu.getView());
				installDockUpdateListeners(vu.getView());
			}
		}
	}

	/**
	 * Cleans up all listeners and other registered objects.
	 *
	 */
	public void destroy() 
	{     
		Iterator <View> itr = ftps.keySet().iterator();
		while (itr.hasNext()) {
			View v = itr.next();
			FocusTraversalPolicy ftp = ftps.get(v);
			v.setFocusTraversalPolicy(ftp);
		}
		ftps.clear();
		while (!clist.empty()) {
			Container c = clist.pop();
			c.removeContainerListener(this);
		}
		clist.clear();
	}
	
	public void componentAdded(ContainerEvent evt)
	{
		Log.log(Log.DEBUG, this, "added: " + evt.getComponent());
		View view = (View) SwingUtilities.getWindowAncestor(evt.getComponent());
		switch (indexOfContainer(view, evt.getContainer()))
		{
		case 4:
			setFocusCycleRoot(view, view.getDockableWindowManager().getTopDockingArea());
			break;
		case 5:
			setFocusCycleRoot(view, view.getDockableWindowManager()
				.getLeftDockingArea());
			break;
		case 6:
			setFocusCycleRoot(view, view.getDockableWindowManager()
				.getBottomDockingArea());
			break;
		case 7:
			setFocusCycleRoot(view, view.getDockableWindowManager()
				.getRightDockingArea());
			break;
		default:
			Log.log(Log.NOTICE, this, "Component not related to dock: "
				+ evt.getContainer());
		}
	}

	public void componentRemoved(ContainerEvent evt)
	{
	}

	private void installDockUpdateListeners(View view)
	{
		addContainerListener(view, 4); // Top dock
		addContainerListener(view, 5); // Left dock
		addContainerListener(view, 6); // Bottom dock
		addContainerListener(view, 7); // Right dock
	}

	private void installFocusTraversalPolicy(View view)
	{
		FocusTraversalPolicy oftp = view.getFocusTraversalPolicy();
		DockerFocusTraversalPolicy p = new DockerFocusTraversalPolicy(oftp);
		view.setFocusTraversalPolicy(p);
		ftps.put(view, oftp);
	}

	private int indexOfContainer(View view, Container c)
	{
		Component[] comps = view.getDockableWindowManager().getComponents();
		for (int i = 0; i < comps.length; i++)
		{
			if (comps[i] == c)
				return i;
		}
		return -1;
	}

	private void setFocusCycleRoots(View view)
	{
		setFocusCycleRoot(view, view.getDockableWindowManager().getTopDockingArea());
		setFocusCycleRoot(view, view.getDockableWindowManager().getLeftDockingArea());
		setFocusCycleRoot(view, view.getDockableWindowManager().getBottomDockingArea());
		setFocusCycleRoot(view, view.getDockableWindowManager().getRightDockingArea());
	}

	private void setFocusCycleRoot(View view, PanelWindowContainer dock)
	{
		String[] names = dock.getDockables();
		for (int i = 0; i < names.length; i++)
		{
			Component comp = view.getDockableWindowManager()
				.getDockableWindow(names[i]);
			if (comp instanceof Container)
			{
				Log.log(Log.DEBUG, this, "Setting focus cycle root: " + comp);
				((Container) comp).setFocusCycleRoot(true);
			}
		}
	}

	private void addContainerListener(View view, int compIdx)
	{
		Container c = (Container)view.getDockableWindowManager().getComponent(compIdx); 
		c.addContainerListener(this);
		clist.push(c);
		
	}

}
