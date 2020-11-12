/*
 *  AntTree.java - Plugin for running Ant builds from jEdit.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import console.Console;

public class AntTree extends JTree
{
	public final static Icon ICON_ROOT = AntFarm.loadIcon("antfarm.root.");

	public final static Icon ICON_PROJECT = AntFarm.loadIcon("antfarm.project.");

	// AntFarm.loadIcon( "antfarm.project.icon" );
	public final static Icon ICON_BROKEN_PROJECT = AntFarm.loadIcon("antfarm.brokenProject.");

	public final static Icon ICON_TARGET = AntFarm.loadIcon("antfarm.target.");

	public final static Icon ICON_DEFAULT_TARGET = AntFarm.loadIcon("antfarm.defaultTarget.");

	protected JPopupMenu _popup;

	protected TreePath _clickedPath;

	private DefaultMutableTreeNode _top;

	private RootNode _root;

	private AntFarm _antFarm;

	private View _view;

	public AntTree(AntFarm antFarm, View view)
	{
		// Enable tool tips.
		ToolTipManager.sharedInstance().registerComponent(this);
		putClientProperty("JTree.lineStyle", "Angled");

		_antFarm = antFarm;
		_view = view;
		populateTree();
	}

	String getSelectedBuildFile()
	{
		DefaultMutableTreeNode node = getCurrentlySelectedNode();
		ProjectNode pnode = getProjectNode(node);
		if (pnode != null)
			return pnode.getBuildFilePath();
		else
			return null;
	}

	DefaultMutableTreeNode getCurrentlySelectedNode()
	{
		return getTreeNode(_clickedPath);
	}

	DefaultMutableTreeNode getTreeNode(TreePath path)
	{
		return (DefaultMutableTreeNode) (path.getLastPathComponent());
	}

	ProjectNode getProjectNode(DefaultMutableTreeNode node)
	{
		if (node == null)
			return null;
		Object obj = node.getUserObject();
		if (obj instanceof IconData)
			obj = ((IconData) obj).getObject();
		if (obj instanceof ProjectNode)
			return (ProjectNode) obj;
		else
			return null;
	}

	AntNode getAntNode(DefaultMutableTreeNode node)
	{
		if (node == null)
			return null;
		Object obj = node.getUserObject();
		if (obj instanceof IconData)
			obj = ((IconData) obj).getObject();
		if (obj instanceof AntNode)
			return (AntNode) obj;
		else
			return null;
	}

	ExpandingNode getExpandingNode(DefaultMutableTreeNode node)
	{
		if (node == null)
			return null;
		Object obj = node.getUserObject();
		if (obj instanceof IconData)
			obj = ((IconData) obj).getObject();
		if (obj instanceof ExpandingNode)
			return (ExpandingNode) obj;
		else
			return null;
	}

	OpenableNode getOpenableNode(DefaultMutableTreeNode node)
	{
		if (node == null)
			return null;
		Object obj = node.getUserObject();
		if (obj instanceof IconData)
			obj = ((IconData) obj).getObject();
		if (obj instanceof OpenableNode)
			return (OpenableNode) obj;
		else
			return null;
	}

	ExecutingNode getExecutingNode(DefaultMutableTreeNode node)
	{
		if (node == null)
			return null;
		Object obj = node.getUserObject();
		if (obj instanceof IconData)
			obj = ((IconData) obj).getObject();
		if (obj instanceof ExecutingNode)
			return (ExecutingNode) obj;
		else
			return null;
	}

	void reload()
	{
		Thread runner = new Thread()
		{
			public void run()
			{
				Runnable runnable = new Runnable()
				{
					public void run()
					{
						_root.populate(_top);
						((DefaultTreeModel) getModel()).reload();
					}
				};
				SwingUtilities.invokeLater(runnable);
			}
		};
		runner.start();
	}

	public void executeCurrentTarget()
	{
		// Console use change
		AntNode antNode = getAntNode(getCurrentlySelectedNode());
		if (antNode != null)
		{
			_antFarm.loadBuildFileInShell(antNode.getBuildFilePath());
		}
		// End console use change.

		ExecutingNode node = getExecutingNode(getCurrentlySelectedNode());
		if (node != null)
			node.execute();
		else
			getToolkit().beep();
	}

	void removeBuildFileNode()
	{
		((DefaultTreeModel) getModel()).removeNodeFromParent(getCurrentlySelectedNode());
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
		if (!root.children().hasMoreElements())
			setPlaceholder(root);
		reload();
	}

	private void setPlaceholder(DefaultMutableTreeNode node)
	{
		node.add(new DefaultMutableTreeNode(new Boolean(true)));
	}

	private void populateTree()
	{
		_root = new RootNode();
		_top = new DefaultMutableTreeNode(new IconData(ICON_ROOT, _root));

		String loading = jEdit.getProperty(AntFarmPlugin.NAME + ".tree.loading");
		_top.add(new DefaultMutableTreeNode(loading));

		setModel(new DefaultTreeModel(_top));

		TreeCellRenderer renderer = new IconCellRenderer();
		setCellRenderer(renderer);

		addTreeExpansionListener(new NodeExpansionListener());
		addTreeSelectionListener(new ProjectSelectionListener());

		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setShowsRootHandles(true);
		setRootVisible(false);
		setEditable(false);

		addMouseListener(new MouseTrigger());

		reload();
	}

	class MouseTrigger extends MouseAdapter
	{

		public void mousePressed(MouseEvent e)
		{

			if (GUIUtilities.isPopupTrigger(e))
			{

				int x = e.getX();
				int y = e.getY();
				TreePath path = getPathForLocation(x, y);

				if (path != null)
				{

					_popup = new JPopupMenu();
					add(_popup);

					// add actions depending upon the type
					// of node we're on
					Action expandCollapseAction = getExpandCollapseAction(path);
					if (expandCollapseAction != null)
					{
						_popup.add(expandCollapseAction);
						_popup.addSeparator();
					}

					Action executeAction = getExecuteAction(path);
					if (executeAction != null)
						_popup.add(executeAction);

					Action openAction = getOpenAction(path);
					if (openAction != null)
						_popup.add(openAction);

					Action browseAction = getBrowseAction(path);
					if (browseAction != null)
						_popup.add(browseAction);

					_popup.show(AntTree.this, x, y);
					_clickedPath = path;
					setSelectionPath(_clickedPath);
				}
			}
		}

		public void mouseClicked(MouseEvent e)
		{
			
			TreePath selPath = getPathForLocation(e.getX(), e.getY());
			if (selPath == null)
				return;
			boolean openOnClick = jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "open-on-singleclick" );
			DefaultMutableTreeNode node = getTreeNode(selPath);
			AntNode antNode = getAntNode(node);
			if (antNode == null) return;
			if (e.getClickCount() == 1  && openOnClick) {
				OpenableNode n = getOpenableNode(node);
				if (n != null) n.open(jEdit.getActiveView());
			}
			
			if (e.getClickCount() == 2)
			{
				_antFarm.loadBuildFileInShell(antNode.getBuildFilePath());
				ExecutingNode executingNode = getExecutingNode(node);
				if (executingNode != null)
				{
					executingNode.execute();
				}
			}

		}

		private AbstractAction getExpandCollapseAction(TreePath path)
		{

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();

			if (getExpandingNode(node) == null)
				return null;

			AbstractAction action = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (_clickedPath == null)
						return;
					if (isExpanded(_clickedPath))
						collapsePath(_clickedPath);
					else
						expandPath(_clickedPath);
				}
			};

			if (isExpanded(path))
				action.putValue(Action.NAME, jEdit.getProperty(AntFarmPlugin.NAME
					+ ".action.collapse"));
			else
				action.putValue(Action.NAME, jEdit.getProperty(AntFarmPlugin.NAME
					+ ".action.expand"));
			return action;
		}

		private AbstractAction getExecuteAction(TreePath path)
		{
			DefaultMutableTreeNode node = getTreeNode(path);

			// Begin console change.
			final AntNode antNode = getAntNode(node);
			// End console change.

			final ExecutingNode en = getExecutingNode(node);

			if (en == null)
				return null;

			AbstractAction action = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					// Begin console change.
					if (antNode != null)
					{
						_antFarm.loadBuildFileInShell(antNode
							.getBuildFilePath());
					}
					// End console change.

					en.execute();
				}
			};

			String text = jEdit.getProperty(AntFarmPlugin.NAME
				+ ".action.execute.target");
			ProjectNode p = getProjectNode(node);
			if (p != null)
			{
				text = jEdit.getProperty(AntFarmPlugin.NAME
					+ ".action.execute.default-target")
					+ p.getProject().getDefaultTarget();
			}
			action.putValue(Action.NAME, text);

			return action;
		}

		private AbstractAction getOpenAction(TreePath path)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();

			final OpenableNode openableNode = getOpenableNode(node);
			if (openableNode != null)
			{

				AbstractAction action = new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (_clickedPath == null)
							return;
						openableNode.open(_view);
					}
				};

				action.putValue(Action.NAME, jEdit.getProperty(AntFarmPlugin.NAME
					+ ".action.edit"));

				return action;
			}
			else
			{
				return null;
			}
		}

		private AbstractAction getBrowseAction(TreePath path)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();

			final ProjectNode projectNode = getProjectNode(node);
			if (projectNode != null)
			{

				AbstractAction action = new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (_clickedPath == null)
							return;
						projectNode.browseBaseDirectory();
					}
				};
				action.putValue(Action.NAME, jEdit.getProperty(AntFarmPlugin.NAME
					+ ".action.browse"));
				return action;
			}
			return null;
		}
	}

	// Make sure expansion is threaded and updating the tree model
	// only occurs within the event dispatching thread.
	class NodeExpansionListener implements TreeExpansionListener
	{
		public void treeExpanded(TreeExpansionEvent event)
		{
			final DefaultMutableTreeNode node = getTreeNode(event.getPath());
			final ExpandingNode enode = getExpandingNode(node);

			Thread runner = new Thread()
			{
				public void run()
				{
					if (enode != null && enode.expand(node))
					{
						Runnable runnable = new Runnable()
						{
							public void run()
							{
								((DefaultTreeModel) getModel())
									.reload(node);
							}
						};
						SwingUtilities.invokeLater(runnable);
					}
				}
			};
			runner.start();
		}

		public void treeCollapsed(TreeExpansionEvent event)
		{
		}
	}

	class ProjectSelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent event)
		{
			_clickedPath = event.getPath();

			ProjectNode projectNode = getProjectNode(getTreeNode(_clickedPath));
			if (projectNode != null)
			{
				_antFarm.removeAntFile.setEnabled(true);
			}
			else
			{
				_antFarm.removeAntFile.setEnabled(false);
			}

			if (getExecutingNode(getTreeNode(_clickedPath)) != null)
			{
				_antFarm.runTarget.setEnabled(true);
			}
			else
			{
				_antFarm.runTarget.setEnabled(false);
			}

			// Console use change.
			// AntNode antNode = getAntNode( getTreeNode(
			// _clickedPath ) );
			// if ( antNode != null ) {
			// _antFarm.loadBuildFileInShell(
			// antNode.getBuildFilePath() );
			// }
			// End console use change.
		}
	}

	class IconCellRenderer extends JLabel implements TreeCellRenderer
	{
		protected Color _textSelectionColor;

		protected Color _textNonSelectionColor;

		protected Color _bkSelectionColor;

		protected Color _bkNonSelectionColor;

		protected Color _borderSelectionColor;

		protected boolean _selected;

		public IconCellRenderer()
		{
			super();
			_textSelectionColor = UIManager.getColor("Tree.selectionForeground");
			_textNonSelectionColor = UIManager.getColor("Tree.textForeground");
			_bkSelectionColor = UIManager.getColor("Tree.selectionBackground");
			_bkNonSelectionColor = UIManager.getColor("Tree.textBackground");
			_borderSelectionColor = UIManager.getColor("Tree.selectionBorderColor");
			this.setOpaque(true);
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();

			if (obj instanceof Boolean)
				setText(jEdit.getProperty(AntFarmPlugin.NAME + ".tree.loading"));
			else
				setText(obj.toString());

			if (obj instanceof IconData)
			{
				IconData idata = (IconData) obj;
				if (expanded)
					setIcon(idata.getExpandedIcon());
				else
					setIcon(idata.getIcon());
				setComponentToolTip(idata.getObject());
			}
			else
				setIcon(null);

			this.setFont(tree.getFont());
			this.setForeground(sel ? _textSelectionColor : _textNonSelectionColor);
			this.setBackground(sel ? _bkSelectionColor : _bkNonSelectionColor);
			_selected = sel;
			return this;
		}

		public void paintComponent(Graphics g)
		{
			Color bColor = this.getBackground();
			Icon icon = getIcon();

			g.setColor(bColor);
			int offset = 0;
			if (icon != null && getText() != null)
				offset = (icon.getIconWidth() + getIconTextGap());
			g.fillRect(offset, 0, this.getWidth() - 1 - offset, this.getHeight() - 1);

			if (_selected)
			{
				g.setColor(_borderSelectionColor);
				g.drawRect(offset, 0, this.getWidth() - 1 - offset, this
					.getHeight() - 1);
			}
			super.paintComponent(g);
		}

		private void setComponentToolTip(Object object)
		{
			if (object instanceof AntNode)
			{
				AntNode antNode = (AntNode) object;

				String toolTipText = antNode.getToolTipText();

				if (!toolTipText.equals(""))
					this.setToolTipText(toolTipText);
				else
					this.setToolTipText(null);
			}
		}
	}

	class IconData
	{
		protected Icon _icon;

		protected Icon _expandedIcon;

		protected Object _data;

		public IconData(Icon icon, Object data)
		{
			_icon = icon;
			_expandedIcon = null;
			_data = data;
		}

		public IconData(Icon icon, Icon expandedIcon, Object data)
		{
			_icon = icon;
			_expandedIcon = expandedIcon;
			_data = data;
		}

		public Icon getIcon()
		{
			return _icon;
		}

		public Icon getExpandedIcon()
		{
			return _expandedIcon != null ? _expandedIcon : _icon;
		}

		public Object getObject()
		{
			return _data;
		}

		public String toString()
		{
			return _data.toString();
		}
	}

	abstract class AntNode
	{
		public abstract String getToolTipText();

		public abstract Project getProject();

		public abstract Target getTarget();

		public abstract String getBuildFilePath();

		String promptForProperties()
		{
			if (jEdit.getBooleanProperty(AntFarmPlugin.OPTION_PREFIX
				+ "suppress-properties"))
				return "";

			PropertyDialog propertyDialog = new PropertyDialog(_view, "Run '"
				+ getTarget().getName() + "' with these properties.");
			propertyDialog.setVisible(true);

			if (propertyDialog.isCanceled())
				return null;

			Properties properties = propertyDialog.getProperties();

			if (properties == null)
			{
				return "";
			}
			StringBuffer command = new StringBuffer();
			Enumeration ee = properties.keys();
			String current;
			while (ee.hasMoreElements())
			{
				current = (String) ee.nextElement();
				if (current.equals(""))
					continue;
				command.append(current).append("=");
				command.append(properties.getProperty(current));
				command.append(" ");
			}
			return command.toString();
		}
	}

	class RootNode implements ExpandingNode
	{
		public File[] listBuildFiles()
		{

			Vector fileNames = _antFarm.getAntBuildFiles();

			Vector v = new Vector();

			for (int i = 0; i < fileNames.size(); i++)
			{
				v.addElement(new File((String) fileNames.elementAt(i)));
			}

			File[] files = new File[v.size()];
			v.copyInto(files);

			return files;
		}

		public boolean expand(DefaultMutableTreeNode parent)
		{
			return populate(parent);
		}

		public boolean populate(DefaultMutableTreeNode parent)
		{
			parent.removeAllChildren();

			File[] buildFiles = listBuildFiles();

			if ((buildFiles == null) || (buildFiles.length < 1))
			{
				String noBuildFiles = jEdit.getProperty(AntFarmPlugin.NAME
					+ ".root.noBuildFiles");
				parent.add(new DefaultMutableTreeNode(noBuildFiles));
				return false;
			}

			Vector projectNodes = createAndSortProjectNodes(buildFiles);

			addProjectNodesToParentNode(parent, projectNodes);

			return true;
		}

		private void addProjectNodesToParentNode(DefaultMutableTreeNode parent,
			Vector projectNodes)
		{
			for (int i = 0; i < projectNodes.size(); i++)
			{
				ProjectNode pnode = (ProjectNode) projectNodes.elementAt(i);

				IconData idata = null;
				DefaultMutableTreeNode node = null;

				if (pnode.isProjectBroken())
				{
					idata = new IconData(AntTree.ICON_BROKEN_PROJECT, pnode);
					node = new DefaultMutableTreeNode(idata);
				}
				else
				{
					idata = new IconData(AntTree.ICON_PROJECT, pnode);
					node = new DefaultMutableTreeNode(idata);
					node.add(new DefaultMutableTreeNode(new Boolean(true)));
				}
				parent.add(node);
			}
		}

		private Vector createAndSortProjectNodes(File[] buildFiles)
		{
			Vector projectNodes = new Vector();

			for (int i = 0; i < buildFiles.length; i++)
			{
				ProjectNode newProjectNode = new ProjectNode(buildFiles[i]
					.getAbsolutePath());
				projectNodes.addElement(newProjectNode);
			}
			Collections.sort(projectNodes, new Comparator()
			{
				public int compare(Object obj1, Object obj2)
				{
					return obj1.toString().compareToIgnoreCase(obj2.toString());
				}
			});
			return projectNodes;
		}

	}

	class TargetNode extends AntNode implements ExecutingNode, OpenableNode
	{
		private Target _target;

		private ProjectNode _parent;

		public TargetNode(Target target, ProjectNode parent)
		{
			_target = target;
			_parent = parent;
		}

		public String getToolTipText()
		{
			String description = "";
			if (_target.getDescription() != null)
				description += _target.getDescription();

			if (isDefaultTarget())
				return "[default] " + description;

			return description;
		}

		public Project getProject()
		{
			return _parent.getProject();
		}

		public Target getTarget()
		{
			return _target;
		}

		public String getBuildFilePath()
		{
			return _parent.getBuildFilePath();
		}

		public String toString()
		{
			return _target.getName();
		}

		public void execute()
		{
			String properties = promptForProperties();

			if (properties == null)
				return;

			Console console = AntFarmPlugin.getConsole(_view);
			console.run(AntFarmPlugin.ANT_SHELL, "!" + _target.getName() + " "
				+ properties);
		}

		public boolean isSubTarget()
		{
			return _target.getDescription() == null;
		}

		private boolean isDefaultTarget()
		{
			return _target.getName().equals(getProject().getDefaultTarget());
		}

		public void open(View view)
		{
			jEdit.openFile(view, getBuildFilePath());
			Location l = _target.getLocation();
			JEditTextArea ta = view.getTextArea();
			int newCaret = ta.getLineStartOffset(l.getLineNumber() - 1);
			ta.setCaretPosition(newCaret);

		}

	}

	class ProjectNode extends AntNode implements ExpandingNode, ExecutingNode, OpenableNode
	{
		private String _buildFilePath;

		private Project _project;

		private Exception _buildException;

		public ProjectNode(String buildFilePath)
		{
			_buildFilePath = buildFilePath;
			try
			{
				_project = _antFarm.getProject(_buildFilePath);
			}
			catch (Exception e)
			{
				_buildException = e;
			}
		}

		public String getToolTipText()
		{
			return _buildFilePath;
		}

		public boolean isProjectBroken()
		{
			return _buildException != null;
		}

		public Project getProject()
		{
			return _project;
		}

		public Target getTarget()
		{
			return (Target) _project.getTargets().get(_project.getDefaultTarget());
		}

		public String getBuildFilePath()
		{
			return _buildFilePath;
		}

		public String toString()
		{
			if (isProjectBroken())
			{
				return jEdit.getProperty(AntFarmPlugin.NAME + ".project.broken")
					+ _buildException.getMessage();
			}
			else
			{
				if (_project.getName() != null && _project.getName().length() > 0)
				{
					return _project.getName()
						+ jEdit.getProperty(AntFarm.NAME
							+ ".project.build-file");
				}
				return jEdit.getProperty(AntFarmPlugin.NAME + ".project.untitled");
			}
		}

		public void execute()
		{

			String properties = promptForProperties();
			if (properties == null)
				return;
			Console console = AntFarmPlugin.getConsole(_view);
			console.run(AntFarmPlugin.ANT_SHELL, "! " + properties);
		}

		public boolean expand(DefaultMutableTreeNode parent)
		{
			parent.removeAllChildren();
			// Remove Flag

			Hashtable targets = _project.getTargets();
			if (targets == null)
				return true;

			Vector targetNodes = createAndSortTargetNodes(targets);

			for (int i = 0; i < targetNodes.size(); i++)
			{
				TargetNode nd = (TargetNode) targetNodes.elementAt(i);

				// Skip sub-nodes...
				if (nd.isSubTarget() && AntFarmPlugin.supressSubTargets())
					continue;

				IconData idata = null;
				if (nd.isDefaultTarget())
				{
					idata = new IconData(AntTree.ICON_DEFAULT_TARGET, null, nd);
				}
				else
				{
					idata = new IconData(AntTree.ICON_TARGET, null, nd);
				}
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(idata);
				parent.add(node);
			}

			return true;
		}

		public void browseBaseDirectory()
		{
			File baseDir = _project.getBaseDir();
			_antFarm.displayProjectDir(baseDir);
		}

		public void open(View view)
		{
			jEdit.openFile(view, _buildFilePath);
		}

		private Vector createAndSortTargetNodes(Hashtable targets)
		{
			Vector targetNodes = new Vector();

			for (Enumeration e = targets.elements(); e.hasMoreElements();)
			{
				Target target = (Target) e.nextElement();

				TargetNode newTargetNode = new TargetNode(target, this);

				// sort each node
				boolean isAdded = false;
				for (int i = 0; i < targetNodes.size(); i++)
				{
					TargetNode tn = (TargetNode) targetNodes.elementAt(i);
					if (newTargetNode._target.getName().compareTo(
						tn._target.getName()) < 0)
					{
						targetNodes.insertElementAt(newTargetNode, i);
						isAdded = true;
						break;
					}
				}
				if (!isAdded)
					targetNodes.addElement(newTargetNode);
			}
			return targetNodes;
		}
	}

	interface ExpandingNode
	{
		public boolean expand(DefaultMutableTreeNode parent);
	}

	interface ExecutingNode
	{
		public void execute();
	}

	interface OpenableNode
	{
		public void open(View view);
	}

}
