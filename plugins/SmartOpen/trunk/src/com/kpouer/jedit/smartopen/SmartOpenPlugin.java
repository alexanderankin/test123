/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011-2013 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.kpouer.jedit.smartopen;

//{{{ Imports
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import com.kpouer.jedit.smartopen.indexer.IndexFilesTask;
import com.kpouer.jedit.smartopen.indexer.IndexProjectTask;
import com.kpouer.jedit.smartopen.indexer.IndexProjectUpdateTask;
import common.gui.itemfinder.ItemFinder;
import common.gui.itemfinder.ItemFinderPanel;
import common.gui.itemfinder.ItemFinderWindow;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferChanging;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.Task;
import org.gjt.sp.util.ThreadUtilities;
import projectviewer.ProjectViewer;
import projectviewer.event.ProjectUpdate;
import projectviewer.event.ViewerUpdate;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
//}}}

/**
 * @author Matthieu Casanova
 */
public class SmartOpenPlugin extends EditPlugin
{
	private static JTextField extensionTextField;
	private final Map<View, SmartOpenToolbar> viewToolbar = new HashMap<>();
	private final Map<View, JComponent> topToolbars = new HashMap<>();

	public static FileIndex itemFinder;
	private Timer timer;

	private boolean toolbar;
	private static SmartOpenToolbar smartToolbar;

	//{{{ start() method
	@Override
	public void start()
	{
		propertiesChanged(null);
		itemFinder = new FileIndex(null);
		if (smartToolbar != null)
			smartToolbar.setFileIndex(itemFinder);

		EditBus.addToBus(this);
		timer = new Timer(60000, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				indexFiles(false);
			}
		});
		timer.start();
	} //}}}

	//{{{ addAncestorToolBar() method
	private void addToolbar(View view)
	{
		if (viewToolbar.containsKey(view))
			return;
		smartToolbar = new SmartOpenToolbar(view,itemFinder);
		JComponent toolBar = getViewToolbar(view);
		toolBar.add(smartToolbar);
		toolBar.revalidate();
		topToolbars.put(view, toolBar);
		viewToolbar.put(view, smartToolbar);
	} //}}}

	//{{{ getViewToolbar() method
	private JComponent getViewToolbar(View view)
	{
		try
		{
			Field topToolBarsField = view.getClass().getDeclaredField("topToolBars");
			topToolBarsField.setAccessible(true);
			JPanel topToolBars = (JPanel) topToolBarsField.get(view);
			Component[] components = topToolBars.getComponents();

			if (components.length > 1)
			{
				for (int i = 1; i < components.length; i++)
				{
					Component component = components[i];
					if (component instanceof JComponent)
					{
						JComponent toolBar = (JComponent) component;
						if (toolBar.getClientProperty("Ancestor-SmartOpen") == Boolean.TRUE)
							return toolBar;
					}
				}
			}
		}
		catch (NoSuchFieldException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		catch (IllegalAccessException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		JPanel customToolbar = new JPanel(new FlowLayout(FlowLayout.LEADING));
		customToolbar.putClientProperty("Ancestor-SmartOpen", Boolean.TRUE);
		view.addToolBar(customToolbar);
		return customToolbar;
	} //}}}

	//{{{ removeToolbar() method
	private void removeToolbar(View view)
	{
		SmartOpenToolbar toolBar = viewToolbar.get(view);
		if (toolBar == null)
			return;
		JComponent top = topToolbars.get(view);
		top.remove(toolBar);
		if (top.getComponentCount() == 0)
		{
			view.removeToolBar(top);
			topToolbars.remove(view);
		}
		else
		{
			top.revalidate();
		}
		viewToolbar.remove(view);
	} //}}}

	public static void resetFrequency()
	{
		if (itemFinder == null)
			return;
		itemFinder.resetFrequency();
	}

	//{{{ indexFiles() methods
	public static void indexFiles()
	{
		indexFiles(false);
	}

	/**
	 * Index files.
	 * @param force true if you want to force project reindexation
	 */
	private static void indexFiles(boolean force)
	{
		if (jEdit.getBooleanProperty("options.smartopen.projectindex"))
		{
			if (force)
			{
				VPTProject activeProject = ProjectViewer.getActiveProject(jEdit.getActiveView());
				indexProject(activeProject);
			}
		}
		else
		{
			if (itemFinder != null)
				itemFinder.close();
			itemFinder = new FileIndex(null);
			if (smartToolbar != null)
				smartToolbar.setFileIndex(itemFinder);
			Task task = new IndexFilesTask();
			ThreadUtilities.runInBackground(task);
		}
	} //}}}

	//{{{ indexProject() method
	private static void indexProject(VPTProject activeProject)
	{
		if (Objects.equals(itemFinder.getProject(), activeProject))
		{
			return;
		}

		if (activeProject != null)
		{
			if (itemFinder != null)
				itemFinder.close();
			itemFinder = new FileIndex(activeProject);
			if (smartToolbar != null)
				smartToolbar.setFileIndex(itemFinder);

			//reindex only for in-memory storage
			//if(jEdit.getBooleanProperty("options.smartopen.memoryindex")){
			IndexProjectTask task = new IndexProjectTask(itemFinder);
			ThreadUtilities.runInBackground(task);
			//}
		}
		else
		{
			Task task = new IndexFilesTask();
			ThreadUtilities.runInBackground(task);
		}
	} //}}}

	//{{{ stop() method
	@Override
	public void stop()
	{
		extensionTextField = null;
		timer.stop();
		EditBus.removeFromBus(this);
		itemFinder = null;
		removeToolbars();
		smartToolbar = null;
	} //}}}

	//{{{ handleViewUpdate() method
	@EditBus.EBHandler
	public void handleViewUpdate(ViewUpdate viewUpdate)
	{
		if (viewUpdate.getWhat() == ViewUpdate.ACTIVATED)
		{
			VPTProject project = ProjectViewer.getActiveProject(viewUpdate.getView());
			if (!Objects.equals(project, itemFinder.getProject()))
				indexProject(project);
		}
		if (toolbar)
		{
			if (viewUpdate.getWhat() == ViewUpdate.CREATED)
			{
				View view = viewUpdate.getView();
				addToolbar(view);
			}
			else if (viewUpdate.getWhat() == ViewUpdate.CLOSED)
			{
				viewToolbar.remove(viewUpdate.getView());
			}
		}
	} //}}}

	//{{{ propertiesChanged() method
	@EditBus.EBHandler
	public void propertiesChanged(PropertiesChanged propertiesChanged)
	{
		if (toolbar != jEdit.getBooleanProperty("options.smartopen.toolbar"))
		{
			toolbar = !toolbar;
			if (toolbar)
				addToolbars();
			else
				removeToolbars();
		}
		indexFiles();
	} //}}}

	//{{{ bufferChanging() method
	@EditBus.EBHandler
	public void bufferChanging(BufferChanging bc)
	{
		String path = bc.getBuffer().getPath();
		ThreadUtilities.runInBackground(new UpdateFrequencyTask(path));
	} //}}}

	//{{{ viewerUpdate() method
	@EditBus.EBHandler
	public void viewerUpdate(ViewerUpdate vu)
	{
		if (jEdit.getBooleanProperty("options.smartopen.projectindex"))
		{
			if (vu.getType() == ViewerUpdate.Type.PROJECT_LOADED)
			{
				VPTNode node = vu.getNode();
				VPTProject project = VPTNode.findProjectFor(node);
				if (!Objects.equals(project, itemFinder.getProject()))
					indexProject(project);
			}
		}
	} //}}}

	//{{{ projectModified() method
	@EditBus.EBHandler
	public void projectModified(ProjectUpdate pu)
	{
		if (pu.getProject() == itemFinder.getProject())
		{
			if (pu.getType() == ProjectUpdate.Type.FILES_CHANGED)
			{
				Collection<VPTFile> addedFiles = pu.getAddedFiles();
				Collection<VPTFile> removedFiles = pu.getRemovedFiles();
				IndexProjectUpdateTask task = new IndexProjectUpdateTask(addedFiles, removedFiles);
				ThreadUtilities.runInBackground(task);
			}
		}
	} //}}}

	//{{{ smartOpen() method
	public static void smartOpen(View view)
	{
		SmartOpenPlugin instance = (SmartOpenPlugin) jEdit.getPlugin(SmartOpenPlugin.class.getName());
		SmartOpenToolbar smartOpenToolbar = instance.viewToolbar.get(view);
		if (smartOpenToolbar != null)
		{
			ItemFinderPanel<String> itemFinderPanel = smartOpenToolbar.getItemFinderPanel();
			String wordAtCaret = getWordAtCaret(view);
			itemFinderPanel.setText(wordAtCaret);
			EventQueue.invokeLater(itemFinderPanel.requestFocusWorker);
		}
		else
		{
			smartOpenDialog(view);
		}
	} //}}}

	//{{{ smartOpenDialog() methods
	public static void smartOpenDialog(View view, final String fileName)
	{
		if (extensionTextField == null)
			extensionTextField = new JTextField(6);
		ItemFinder<String> filetItemFinder = new FileItemFinder(itemFinder, extensionTextField);

		final ItemFinderWindow<String> itemFinderWindow = new ItemFinderWindow<>(filetItemFinder);
		ItemFinderPanel<?> itemFinderPanel = (ItemFinderPanel<?>) itemFinderWindow.getContentPane();
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel extensionPanel = new JPanel();
		extensionPanel.add(new JLabel("extension:"));
		extensionPanel.add(extensionTextField);

		Component label = itemFinderPanel.getLabel();
		final Component searchField = itemFinderPanel.getSearchField();
		topPanel.add(label);
		topPanel.add(extensionPanel, BorderLayout.EAST);
		itemFinderPanel.removeAll();
		itemFinderPanel.add(topPanel, BorderLayout.NORTH);

		itemFinderPanel.add(searchField, BorderLayout.CENTER);
		itemFinderWindow.pack();
		itemFinderWindow.setLocationRelativeTo(view);
		itemFinderWindow.setVisible(true);
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				itemFinderWindow.getItemFinderPanel().setText(fileName);
				searchField.requestFocus();
			}
		});
	}

	public static void smartOpenDialog(View view)
	{
		String wordAtCaret = getWordAtCaret(view);
		smartOpenDialog(view, wordAtCaret);
	} //}}}

	//{{{ addToolbars() method
	private void addToolbars()
	{
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++)
		{
			addToolbar(views[i]);
		}
	} //}}}

	//{{{ removeToolbars() method
	private void removeToolbars()
	{
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++)
		{
			removeToolbar(views[i]);
		}
	} //}}}

	//{{{ getWordAtCaret() method
	public static String getWordAtCaret(View view)
	{
		JEditTextArea ta = view.getTextArea();
		String selected = ta.getSelectedText();
		if (selected != null && !selected.isEmpty())
		{
			if (selected.contains("\n"))
				return null;
			return selected;
		}
		int line = ta.getCaretLine();
		String text = ta.getLineText(line);
		Pattern pat = Pattern.compile("\\w+");
		Matcher m = pat.matcher(text);
		int end = -1;
		int start = -1;
		int offset = ta.getCaretPosition() - ta.getLineStartOffset(line);
		while (end <= offset)
		{
			if (! m.find())
				return null;
			end = m.end();
			start = m.start();
			selected = m.group();
		}
		if (start > offset || selected == null || selected.isEmpty())
			return null;
		return selected;
	} //}}}

	private static class UpdateFrequencyTask extends Task
	{
		private final String path;

		private UpdateFrequencyTask(String path)
		{
			this.path = path;
		}

		@Override
		public void _run()
		{
			itemFinder.updateFrequency(path);
		}
	}
}
