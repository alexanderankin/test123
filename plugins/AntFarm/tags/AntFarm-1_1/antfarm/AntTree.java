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

import console.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.apache.tools.ant.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

public class AntTree extends JTree
{
	public final static ImageIcon ICON_ROOT =
		AntFarm.loadIcon( "antfarm.root.icon" );
	public final static ImageIcon ICON_PROJECT =
		AntFarm.loadIcon( "antfarm.project.icon" );
	public final static ImageIcon ICON_BROKEN_PROJECT =
		AntFarm.loadIcon( "antfarm.brokenProject.icon" );
	public final static ImageIcon ICON_TARGET =
		AntFarm.loadIcon( "antfarm.target.icon" );

	protected JPopupMenu _popup;
	protected TreePath _clickedPath;

	private DefaultMutableTreeNode _top;
	private RootNode _root;
	private AntFarm _antBrowser;
	private View _view;


	public AntTree( AntFarm antBrowser, View view )
	{
		//Enable tool tips.
		ToolTipManager.sharedInstance().registerComponent( this );
		putClientProperty( "JTree.lineStyle", "Angled" );

		_antBrowser = antBrowser;
		_view = view;
		populateTree();
	}


	String getSelectedBuildFile()
	{
		DefaultMutableTreeNode node = getCurrentlySelectedNode();
		ProjectNode pnode = getProjectNode( node );
		if ( pnode != null )
			return pnode.getBuildFilePath();
		else
			return null;
	}


	DefaultMutableTreeNode getCurrentlySelectedNode()
	{
		return getTreeNode( _clickedPath );
	}


	DefaultMutableTreeNode getTreeNode( TreePath path )
	{
		return (DefaultMutableTreeNode) ( path.getLastPathComponent() );
	}


	ProjectNode getProjectNode( DefaultMutableTreeNode node )
	{
		if ( node == null )
			return null;
		Object obj = node.getUserObject();
		if ( obj instanceof IconData )
			obj = ( (IconData) obj ).getObject();
		if ( obj instanceof ProjectNode )
			return (ProjectNode) obj;
		else
			return null;
	}


	AntNode getAntNode( DefaultMutableTreeNode node )
	{
		if ( node == null )
			return null;
		Object obj = node.getUserObject();
		if ( obj instanceof IconData )
			obj = ( (IconData) obj ).getObject();
		if ( obj instanceof AntNode )
			return (AntNode) obj;
		else
			return null;
	}


	ExpandingNode getExpandingNode( DefaultMutableTreeNode node )
	{
		if ( node == null )
			return null;
		Object obj = node.getUserObject();
		if ( obj instanceof IconData )
			obj = ( (IconData) obj ).getObject();
		if ( obj instanceof ExpandingNode )
			return (ExpandingNode) obj;
		else
			return null;
	}


	OpenableNode getOpenableNode( DefaultMutableTreeNode node )
	{
		if ( node == null )
			return null;
		Object obj = node.getUserObject();
		if ( obj instanceof IconData )
			obj = ( (IconData) obj ).getObject();
		if ( obj instanceof OpenableNode )
			return (OpenableNode) obj;
		else
			return null;
	}


	ExecutingNode getExecutingNode( DefaultMutableTreeNode node )
	{
		if ( node == null )
			return null;
		Object obj = node.getUserObject();
		if ( obj instanceof IconData )
			obj = ( (IconData) obj ).getObject();
		if ( obj instanceof ExecutingNode )
			return (ExecutingNode) obj;
		else
			return null;
	}


	void reload()
	{
		_root.populate( _top );
		( (DefaultTreeModel) getModel() ).reload();
	}


	void executeCurrentTarget()
	{
		ExecutingNode node = getExecutingNode( getCurrentlySelectedNode() );
		if ( node != null )
			node.execute();
		else
			getToolkit().beep();
	}



	void removeBuildFileNode()
	{
		( (DefaultTreeModel) getModel() ).removeNodeFromParent( getCurrentlySelectedNode() );
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
		if ( !root.children().hasMoreElements() )
			setPlaceholder( root );
		reload();
	}


	private void setPlaceholder( DefaultMutableTreeNode node )
	{
		node.add( new DefaultMutableTreeNode( new Boolean( true ) ) );
	}


	private void populateTree()
	{
		_root = new RootNode();
		_top = new DefaultMutableTreeNode( new IconData( ICON_ROOT, _root ) );

		setModel( new DefaultTreeModel( _top ) );

		TreeCellRenderer renderer = new IconCellRenderer();
		setCellRenderer( renderer );

		addTreeExpansionListener( new NodeExpansionListener() );
		addTreeSelectionListener( new ProjectSelectionListener() );

		getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		setShowsRootHandles( true );
		setRootVisible( false );
		setEditable( false );

		addMouseListener( new MouseTrigger() );

		reload();
	}


	class MouseTrigger extends MouseAdapter
	{

		public void mouseReleased( MouseEvent e )
		{

			if ( GUIUtilities.isPopupTrigger( e ) ) {

				int x = e.getX();
				int y = e.getY();
				TreePath path = getPathForLocation( x, y );

				if ( path != null ) {

					_popup = new JPopupMenu();
					add( _popup );

					// add actions depending upon the type of node we're on
					Action expandCollapseAction = getExpandCollapseAction( path );
					if ( expandCollapseAction != null ) {
						_popup.add( expandCollapseAction );
						_popup.addSeparator();
					}

					Action executeAction = getExecuteAction( path );
					if ( executeAction != null )
						_popup.add( executeAction );

					Action openAction = getOpenAction( path );
					if ( openAction != null )
						_popup.add( openAction );

					Action browseAction = getBrowseAction( path );
					if ( browseAction != null )
						_popup.add( browseAction );

					_popup.show( AntTree.this, x, y );
					_clickedPath = path;
					setSelectionPath( _clickedPath );
				}
			}
		}


		public void mouseClicked( MouseEvent e )
		{

			if ( e.getClickCount() == 2 ) {
				TreePath selPath = getPathForLocation( e.getX(), e.getY() );
				if ( selPath == null )
					return;
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) ( selPath.getLastPathComponent() );
				ExecutingNode executingNode = getExecutingNode( node );

				if ( executingNode != null ) {
					executingNode.execute();
				}
			}

		}


		private AbstractAction getExpandCollapseAction( TreePath path )
		{

			DefaultMutableTreeNode node =
				(DefaultMutableTreeNode) path.getLastPathComponent();

			if ( getExpandingNode( node ) == null )
				return null;

			AbstractAction action =
				new AbstractAction()
				{
					public void actionPerformed( ActionEvent e )
					{
						if ( _clickedPath == null )
							return;
						if ( isExpanded( _clickedPath ) )
							collapsePath( _clickedPath );
						else
							expandPath( _clickedPath );
					}
				};

			if ( isExpanded( path ) )
				action.putValue(
					Action.NAME,
					jEdit.getProperty( AntFarmPlugin.NAME + ".action.collapse" )
					 );
			else
				action.putValue(
					Action.NAME,
					jEdit.getProperty( AntFarmPlugin.NAME + ".action.expand" )
					 );
			return action;
		}


		private AbstractAction getExecuteAction( TreePath path )
		{

			DefaultMutableTreeNode node =
				(DefaultMutableTreeNode) path.getLastPathComponent();

			final ExecutingNode en = getExecutingNode( node );

			if ( en == null )
				return null;

			AbstractAction action =
				new AbstractAction()
				{
					public void actionPerformed( ActionEvent e )
					{
						en.execute();
					}
				};

			String text = jEdit.getProperty( AntFarmPlugin.NAME + ".action.execute.target" );
			ProjectNode p = getProjectNode( node );
			if ( p != null ) {
				text = jEdit.getProperty( AntFarmPlugin.NAME + ".action.execute.default-target" )
					 + p.getProject().getDefaultTarget();
			}
			action.putValue( Action.NAME, text );

			return action;
		}


		private AbstractAction getOpenAction( TreePath path )
		{
			DefaultMutableTreeNode node =
				(DefaultMutableTreeNode) path.getLastPathComponent();

			final OpenableNode openableNode = getOpenableNode( node );
			if ( openableNode != null ) {

				AbstractAction action =
					new AbstractAction()
					{
						public void actionPerformed( ActionEvent e )
						{
							if ( _clickedPath == null )
								return;
							openableNode.open( _view );
						}
					};

				action.putValue(
					Action.NAME,
					jEdit.getProperty( AntFarmPlugin.NAME + ".action.edit" )
					 );

				return action;
			}
			else {
				return null;
			}
		}


		private AbstractAction getBrowseAction( TreePath path )
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				path.getLastPathComponent();

			final ProjectNode projectNode = getProjectNode( node );
			if ( projectNode != null ) {

				AbstractAction action =
					new AbstractAction()
					{
						public void actionPerformed( ActionEvent e )
						{
							if ( _clickedPath == null )
								return;
							projectNode.browseBaseDirectory();
						}
					};
				action.putValue(
					Action.NAME,
					jEdit.getProperty( AntFarmPlugin.NAME + ".action.browse" )
					 );
				return action;
			}
			return null;
		}
	}


	// Make sure expansion is threaded and updating the tree model
	// only occurs within the event dispatching thread.
	class NodeExpansionListener implements TreeExpansionListener
	{
		public void treeExpanded( TreeExpansionEvent event )
		{
			final DefaultMutableTreeNode node = getTreeNode( event.getPath() );
			final ExpandingNode enode = getExpandingNode( node );

			Thread runner =
				new Thread()
				{
					public void run()
					{
						if ( enode != null && enode.expand( node ) ) {
							Runnable runnable =
								new Runnable()
								{
									public void run()
									{
										( (DefaultTreeModel) getModel() ).reload( node );
									}
								};
							SwingUtilities.invokeLater( runnable );
						}
					}
				};
			runner.start();
		}


		public void treeCollapsed( TreeExpansionEvent event ) { }
	}


	class ProjectSelectionListener implements TreeSelectionListener
	{
		public void valueChanged( TreeSelectionEvent event )
		{
			_clickedPath = event.getPath();

			ProjectNode projectNode = getProjectNode( getTreeNode( _clickedPath ) );
			if ( projectNode != null ) {
				_antBrowser.removeAntFile.setEnabled( true );
			}
			else {
				_antBrowser.removeAntFile.setEnabled( false );
			}

			if ( getExecutingNode( getTreeNode( _clickedPath ) ) != null ) {
				_antBrowser.runTarget.setEnabled( true );
			}
			else {
				_antBrowser.runTarget.setEnabled( false );
			}

			AntNode antNode = getAntNode( getTreeNode( _clickedPath ) );
			if ( antNode != null ) {
				_antBrowser.loadBuildFileInShell( antNode.getBuildFilePath() );
			}
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
			_textSelectionColor = UIManager.getColor(
				"Tree.selectionForeground" );
			_textNonSelectionColor = UIManager.getColor(
				"Tree.textForeground" );
			_bkSelectionColor = UIManager.getColor(
				"Tree.selectionBackground" );
			_bkNonSelectionColor = UIManager.getColor(
				"Tree.textBackground" );
			_borderSelectionColor = UIManager.getColor(
				"Tree.selectionBorderColor" );
			this.setOpaque( true );
		}


		public Component getTreeCellRendererComponent( JTree tree,
			Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus )
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();

			if ( obj instanceof Boolean )
				setText( jEdit.getProperty( AntFarmPlugin.NAME + ".tree.loading" ) );
			else
				setText( obj.toString() );

			if ( obj instanceof IconData ) {
				IconData idata = (IconData) obj;
				if ( expanded )
					setIcon( idata.getExpandedIcon() );
				else
					setIcon( idata.getIcon() );
				setComponentToolTip( idata.getObject() );
			}
			else
				setIcon( null );

			this.setFont( tree.getFont() );
			this.setForeground( sel ? _textSelectionColor : _textNonSelectionColor );
			this.setBackground( sel ? _bkSelectionColor : _bkNonSelectionColor );
			_selected = sel;
			return this;
		}


		public void paintComponent( Graphics g )
		{
			Color bColor = this.getBackground();
			Icon icon = getIcon();

			g.setColor( bColor );
			int offset = 0;
			if ( icon != null && getText() != null )
				offset = ( icon.getIconWidth() + getIconTextGap() );
			g.fillRect( offset, 0, this.getWidth() - 1 - offset,
				this.getHeight() - 1 );

			if ( _selected ) {
				g.setColor( _borderSelectionColor );
				g.drawRect( offset, 0, this.getWidth() - 1 - offset, this.getHeight() - 1 );
			}
			super.paintComponent( g );
		}


		private void setComponentToolTip( Object object )
		{
			if ( object instanceof AntNode ) {
				AntNode antNode = (AntNode) object;
				this.setToolTipText( antNode.getToolTipText() );
			}
		}
	}


	class IconData
	{
		protected Icon _icon;
		protected Icon _expandedIcon;
		protected Object _data;


		public IconData( Icon icon, Object data )
		{
			_icon = icon;
			_expandedIcon = null;
			_data = data;
		}


		public IconData( Icon icon, Icon expandedIcon, Object data )
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
			if ( jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "suppress-properties" ) )
				return "";

			PropertyDialog propertyDialog = new PropertyDialog(
				_view, "Run '" + getTarget().getName() + "' with these properties."
				 );
			propertyDialog.show();

			Properties properties = propertyDialog.getProperties();

			if ( properties == null ) {
				return "";
			}
			StringBuffer command = new StringBuffer();
			Enumeration ee = properties.keys();
			String current;
			while ( ee.hasMoreElements() ) {
				current = (String) ee.nextElement();
				if ( current.equals( "" ) )
					continue;
				command.append( current ).append( "=" );
				command.append( properties.getProperty( current ) );
				command.append( " " );
			}
			return command.toString();
		}
	}


	class RootNode implements ExpandingNode
	{
		public File[] listBuildFiles()
		{

			Vector fileNames = _antBrowser.getAntBuildFiles();

			Vector v = new Vector();

			for ( int i = 0; i < fileNames.size(); i++ ) {
				v.addElement( new File( (String) fileNames.elementAt( i ) ) );
			}

			File[] files = new File[v.size()];
			v.copyInto( files );

			return files;
		}


		public boolean expand( DefaultMutableTreeNode parent )
		{
			return populate( parent );
		}


		public boolean populate( DefaultMutableTreeNode parent )
		{
			parent.removeAllChildren();

			File[] buildFiles = listBuildFiles();

			if ( ( buildFiles == null ) || ( buildFiles.length < 1 ) ) {
				String noBuildFiles =
					jEdit.getProperty( AntFarmPlugin.NAME + ".root.noBuildFiles" );
				parent.add( new DefaultMutableTreeNode( noBuildFiles ) );
				return false;
			}

			Vector projectNodes = createAndSortProjectNodes( buildFiles );

			addProjectNodesToParentNode( parent, projectNodes );

			return true;
		}


		private void addProjectNodesToParentNode( DefaultMutableTreeNode parent, Vector projectNodes )
		{
			for ( int i = 0; i < projectNodes.size(); i++ ) {
				ProjectNode pnode = (ProjectNode) projectNodes.elementAt( i );

				IconData idata = null;
				DefaultMutableTreeNode node = null;

				if ( pnode.isProjectBroken() ) {
					idata = new IconData( AntTree.ICON_BROKEN_PROJECT, pnode );
					node = new DefaultMutableTreeNode( idata );
				}
				else {
					idata = new IconData( AntTree.ICON_PROJECT, pnode );
					node = new DefaultMutableTreeNode( idata );
					node.add( new DefaultMutableTreeNode( new Boolean( true ) ) );
				}
				parent.add( node );
			}
		}


		private Vector createAndSortProjectNodes( File[] buildFiles )
		{
			Vector projectNodes = new Vector();

			for ( int i = 0; i < buildFiles.length; i++ ) {
				ProjectNode newProjectNode = new ProjectNode( buildFiles[i].getAbsolutePath() );
				projectNodes.addElement( newProjectNode );
			}
			MiscUtilities.quicksort(
				projectNodes,
				new MiscUtilities.Compare()
				{
					public int compare( Object obj1, Object obj2 )
					{
						return obj1.toString().compareToIgnoreCase( obj2.toString() );
					}
				} );
			return projectNodes;
		}

	}


	class TargetNode extends AntNode implements ExecutingNode
	{
		private Target _target;
		private ProjectNode _parent;


		public TargetNode( Target target, ProjectNode parent )
		{
			_target = target;
			_parent = parent;
		}


		public String getToolTipText()
		{
			return _target.getDescription();
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
			if ( isDefaultTarget() )
				return _target.getName() + " [default]";
			return _target.getName();
		}


		public void execute()
		{

			String properties = promptForProperties();

			Console console = AntFarmPlugin.getConsole( _view );
			console.run( AntFarmPlugin.ANT_SHELL, console, "!"
				 + _target.getName()
				 + " " + properties );
		}


		private boolean isDefaultTarget()
		{
			return _target.getName().equals( getProject().getDefaultTarget() );
		}
	}


	class ProjectNode extends AntNode implements ExpandingNode, ExecutingNode, OpenableNode
	{
		private String _buildFilePath;
		private Project _project;
		private Exception _buildException;


		public ProjectNode( String buildFilePath )
		{
			_buildFilePath = buildFilePath;
			try {
				_project = _antBrowser.getProject( _buildFilePath );
			}
			catch ( Exception e ) {
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
			return (Target) _project.getTargets().get( _project.getDefaultTarget() );
		}


		public String getBuildFilePath()
		{
			return _buildFilePath;
		}


		public String toString()
		{
			if ( isProjectBroken() ) {
				return jEdit.getProperty( AntFarmPlugin.NAME + ".project.broken" ) +
					_buildException.getMessage()
					;
			}
			else {
				if ( _project.getName() != null && _project.getName().length() > 0 ) {
					return _project.getName() + jEdit.getProperty( AntFarm.NAME
						 + ".project.build-file" );
				}
				return jEdit.getProperty( AntFarmPlugin.NAME + ".project.untitled" );
			}
		}


		public void execute()
		{

			String properties = promptForProperties();

			Console console = AntFarmPlugin.getConsole( _view );
			console.run(
				AntFarmPlugin.ANT_SHELL, console, "!"
				 + " " + properties
				 );
		}


		public boolean expand( DefaultMutableTreeNode parent )
		{
			parent.removeAllChildren();
			// Remove Flag

			Hashtable targets = _project.getTargets();
			if ( targets == null )
				return true;

			Vector targetNodes = createAndSortTargetNodes( targets );

			for ( int i = 0; i < targetNodes.size(); i++ ) {
				TargetNode nd = (TargetNode) targetNodes.elementAt( i );
				IconData idata = new IconData( AntTree.ICON_TARGET,
					null, nd );
				DefaultMutableTreeNode node = new DefaultMutableTreeNode( idata );
				parent.add( node );
			}

			return true;
		}


		public void browseBaseDirectory()
		{
			File baseDir = _project.getBaseDir();
			_antBrowser.displayProjectDir( baseDir );
		}


		public void open( View view )
		{
			jEdit.openFile( view, _buildFilePath );
		}


		private Vector createAndSortTargetNodes( Hashtable targets )
		{
			Vector targetNodes = new Vector();

			for ( Enumeration e = targets.elements(); e.hasMoreElements();  ) {
				Target target = (Target) e.nextElement();

				TargetNode newTargetNode = new TargetNode( target, this );

				// sort each node
				boolean isAdded = false;
				for ( int i = 0; i < targetNodes.size(); i++ ) {
					TargetNode tn = (TargetNode) targetNodes.elementAt( i );
					if ( newTargetNode._target.getName().compareTo( tn._target.getName() ) < 0 ) {
						targetNodes.insertElementAt( newTargetNode, i );
						isAdded = true;
						break;
					}
				}
				if ( !isAdded )
					targetNodes.addElement( newTargetNode );
			}
			return targetNodes;
		}
	}


	interface ExpandingNode
	{
		public boolean expand( DefaultMutableTreeNode parent );
	}


	interface ExecutingNode
	{
		public void execute();
	}


	interface OpenableNode
	{
		public void open( View view );
	}

}

