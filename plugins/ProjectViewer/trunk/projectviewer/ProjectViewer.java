/* $Id$
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

import java.awt.*;
import java.awt.event.*;
//import java.net.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

import projectviewer.event.*;
import projectviewer.tree.*;

/** A Project Viewer plugin for jEdit.
 */
public final class ProjectViewer extends JPanel implements EBComponent {

	public final static String ALL_PROJECTS = "All Projects";

	private final static String FOLDERS_TAB_TITLE = "Folders";
	private final static String FILES_TAB_TITLE = "Files";
	private final static String WORKING_FILES_TAB_TITLE = "Working Files";

	private final static int FOLDERS_TAB = 0;
	private final static int FILES_TAB = 1;
	private final static int WORKING_FILES_TAB = 2;

    private boolean allProjectsLoaded;
    
	JButton removeFileBtn;
	JButton removeAllFilesBtn;
	JButton createProjectBtn;
	JButton addFileBtn;
	JButton importFilesBtn;
	JButton openAllBtn;
	JButton expandBtn;
	JButton contractBtn;
	JButton saveBtn;
	JButton launchBrowserBtn;  // this will eventually be on the context menu

	private ProjectView projectView;
	private JTabbedPane tabs = new JTabbedPane();

	private JTree folderTree;
	private JTree fileTree;
	private JTree workingFileTree;
	private List listeners;

	private JComboBox projectCombo;
	private JLabel status = new JLabel(" ");

	private View view;

	private ViewerListener vsl;
	private ProjectTreeSelectionListener tsl;
    private TreeContextMenuListener cml;
	private Launcher launcher;

    /** The "main" viewer. Actually, the first instance to be created. */
    private static ProjectViewer mainViewer;
    public static ProjectViewer getMainViewer() {
        return mainViewer;
    }
    
	/** Create a new <code>ProjectViewer</code>.
	 *
	 *@param  aView  Description of Parameter
	 */
	public ProjectViewer(View aView) {
        Log.log(Log.DEBUG, this, "New ProjectViewer instance!");
        allProjectsLoaded = false;
		view = aView;
		launcher = new Launcher(view, this);
		vsl = new ViewerListener(this, launcher);
		tsl = new ProjectTreeSelectionListener(this, launcher);
        cml = new TreeContextMenuListener(this);
		listeners = new ArrayList();
		loadGUI();
		enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
        
        if (mainViewer == null) {
            mainViewer = this;
    		setCurrentProject(getLastProject());
        } else {
            setCurrentProject(mainViewer.getCurrentProject());
        }
	}

	/** Set the project that the user is working with.
	 *
	 *@param  project  The new currentProject value
	 */
	public void setCurrentProject(Project project) {
		if (projectView != null) {
			if (!isAllProjects() && getCurrentProject().equals(project))
				return;
			if (isAllProjects() && project == null)
				return;
			projectView.deactivate();
			launcher.closeProject(getCurrentProject());
            launcher.openProject(project);
		}
        
		projectView = getProjectViewFor(project);
        if (isAllProjects()) {
            if (!allProjectsLoaded) {
                for (Iterator i = ProjectManager.getInstance().projects(); i.hasNext(); ) {
                    Project p = (Project) i.next();
                    if (!p.isLoaded()) {
                        p.load();
                    }
                }
                allProjectsLoaded = true;
            }

            // Only single selections in "All Projects View"            
            folderTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            workingFileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);            
            
            
        } else {
            
            // Allow multiple selections
            folderTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            workingFileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);            
            
            if (!project.isLoaded()) {
                project.load();
            }
        }

		projectView.activate();
		loadProject();
	}

	/** Set the status message.
	 *
	 *@param  msg  The new status value
	 */
	public void setStatus(Object msg) {
		status.setText(msg.toString());
	}

	/** Returns <code>true</code> if currently selected node of the displayed
	 * tree is a file.
	 *
	 *@return    The fileSelected value
	 */
	public boolean isFileSelected() {
		return getCurrentTree().getLastSelectedPathComponent() instanceof ProjectFile;
	}

	/** Returns the currently displayed tree, or <code>null</code> if none are
	 * displayed.
	 *
	 *@return    The currentTree value
	 */
	public JTree getCurrentTree() {
		switch (tabs.getSelectedIndex()) {
						case FOLDERS_TAB:
							return folderTree;
						case FILES_TAB:
							return this.fileTree;
						case WORKING_FILES_TAB:
							return this.workingFileTree;
						default:
							return null;
		}
	}

	/** Returns the current project.
	 *
	 *@return    The currentProject value
	 */
	public Project getCurrentProject() {
		return projectView.getCurrentProject();
	}

	/** Returns the selected node of the current tree.
	 *
	 *@return    The selectedNode value
	 */
	public Object getSelectedNode() {
		return getCurrentTree().getLastSelectedPathComponent();
	}

	/** Returns the currently selected file in the current tree, or <code>null</code>
	 * if no nodes are selected.
	 *
	 *@return    The selectedFile value
	 */
	public ProjectFile getSelectedFile() {
		return (ProjectFile) getSelectedNode();
	}

	/** Returns this component.
	 *
	 *@return    The component value
	 */
	public Component getComponent() {
		return this;
	}

	/** Returns the name of this component.
	 *
	 *@return    The name value
	 */
	public String getName() {
		return ProjectPlugin.NAME;
	}

	/** Return the view that this project viewer is working with
	 *
	 *@return    The view value
	 */
	public View getView() {
		return this.view;
	}

	/** Returns <code>true</code> if the current view is 'All Projects'.
	 *
	 *@return    The allProjects value
	 */
	public boolean isAllProjects() {
		return projectView instanceof AllProjectsView;
	}

	/** Collapses all nodes of the current tree. */
	public void collapseAll() {
		getCurrentTree().collapseRow(0);
	}

	/** Expands all nodes of the current tree. */
	public void expandAll() {
		expandAll(getCurrentTree());
	}

	/** Expands all nodes of the specified tree.
	 *
	 *@param  tree  Description of Parameter
	 */
	public void expandAll(JTree tree) {
		expand(new TreePath(tree.getModel().getRoot()), tree);
	}

	/** Expand the given sub tree.
	 *
	 *@param  path  Description of Parameter
	 *@param  tree  Description of Parameter
	 */
	public void expand(TreePath path, JTree tree) {
		TreeModel model = tree.getModel();
		Object node = path.getLastPathComponent();
		if (model.isLeaf(node))
			return;
		tree.expandPath(path);

		int count = model.getChildCount(node);
		for (int i = 0; i < count; i++) {
			expand(path.pathByAddingChild(model.getChild(node, i)), tree);
		}
	}

	/** Show the default cursor. */
	public void showDefaultCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/** Show the wait cursor. */
	public void showWaitCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/** Returns a file chooser that with a starting directory relative to the
	 * currently selected node of the current displayed tree.
	 *
	 *@return    Description of the Returned Value
	 */
	public JFileChooser createFileChooser() {
		Object node = getSelectedNode();

		String browsePath = "";
		if (node instanceof Project)
			browsePath = ((Project) node).getRoot().getPath();

		else if (node instanceof ProjectDirectory)
			browsePath = ((ProjectDirectory) node).getPath();

		else if (node instanceof ProjectFile)
			browsePath = ((ProjectFile) node).toFile().getParent();
		else if (getCurrentProject() != null)
			browsePath = getCurrentProject().getRoot().getPath();

		return new JFileChooser(browsePath);
	}

	/** Reload ProjectResources and then reparse the object tree. */
	public void refresh() {
		loadProjectCombo();
		loadProject();
	}

	/** Handle a message from the bus.
	 *
	 *@param  msg  Description of Parameter
	 */
	public void handleMessage(EBMessage msg) {
		if (isAllProjects() || !(msg instanceof BufferUpdate))
			return;

		BufferUpdate update = (BufferUpdate) msg;
		if (update.getWhat().equals(BufferUpdate.DIRTY_CHANGED) &&
				!update.getBuffer().isDirty() &&
				getCurrentProject().canAddInProject(update.getBuffer().getPath())
				) {
			int res = JOptionPane.showConfirmDialog(getView(),
					"Import " + update.getBuffer().getName() + " to " + getCurrentProject().getName() + "?",
					jEdit.getProperty(ProjectPlugin.NAME + ".title"),
					JOptionPane.YES_NO_OPTION);

			if (res != JOptionPane.YES_OPTION)
				return;
			getCurrentProject().importFile(new ProjectFile(update.getBuffer().getPath()));
		}
	}

	/** Add a {@link ProjectViewerListener}.
	 *
	 *@param  listener  The feature to be added to the ProjectViewerListener attribute
	 */
	public void addProjectViewerListener(ProjectViewerListener listener) {
		listeners.add(listener);
	}

	/** Remove a {@link ProjectViewerListener}.
	 *
	 *@param  listener  Description of Parameter
	 */
	public void removeProjectViewerListener(ProjectViewerListener listener) {
		listeners.remove(listener);
	}

	/** Enable buttons for the given node.
	 *
	 *@param  node  Description of Parameter
	 */
	public void enableButtonsForNode(Object node) {
		boolean isAllNode = node instanceof String;
		openAllBtn.setEnabled(node != null && !isAllNode);
		importFilesBtn.setEnabled(node != null && !isAllNode);
		addFileBtn.setEnabled(node != null && !isAllNode);
		//removeFileBtn    .setEnabled( node instanceof ProjectFile );
		removeFileBtn.setEnabled(node != null && !isAllNode);
		removeAllFilesBtn.setEnabled(node != null && !isAllNode);
	}

	/** Process component events.
	 *
	 *@param  evt  Description of Parameter
	 */
	protected void processComponentEvent(ComponentEvent evt) {
		if (evt.getID() == ComponentEvent.COMPONENT_HIDDEN)
			EditBus.removeFromBus(this);
		else if (evt.getID() == ComponentEvent.COMPONENT_SHOWN)
			EditBus.addToBus(this);
		super.processComponentEvent(evt);
	}

	/** Returns project view for the given project.
	 *
	 *@param  aProject  Description of Parameter
	 *@return           The projectViewFor value
	 */
	private ProjectView getProjectViewFor(Project aProject) {
		return (aProject instanceof Project) ? (ProjectView)
				new SimpleProjectView(aProject) : new AllProjectsView(this);
	}

	/** Returns the last project of the previous session.
	 *
	 *@return    The lastProject value
	 */
	private Project getLastProject() {
		return ProjectManager.getInstance().getProject(ProjectPlugin.getLastProject());
	}

	/** Fire the project loaded event.
	 *
	 *@param  project  Description of Parameter
	 */
	private void fireProjectLoaded(Project project) {
        ProjectViewerEvent evt = new ProjectViewerEvent(this, project);
        for (Iterator i = listeners.iterator(); i.hasNext(); ) {
            try {
                ((ProjectViewerListener) i.next()).projectLoaded(evt);
            }
            catch (Throwable t) {
                Log.log(Log.WARNING, this, t);
            }
        }
	}
 
	/** loads the GUI of Project Viewer */
	private void loadGUI() {
		setLayout(new BorderLayout());

		projectCombo = new JComboBox();

		folderTree = createTree();
	 	fileTree = createTree();
		workingFileTree = createTree();

		JPanel bar = new JPanel(new BorderLayout());

		/** @todo  support new toolbar from jedit 4.1 */
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);

		removeFileBtn = createButton("/projectviewer/icons/RemoveFile.gif", "Remove file or directory");
		removeAllFilesBtn = createButton("/projectviewer/icons/RemoveAllFiles.gif", "Remove all files");
		createProjectBtn = createButton("/projectviewer/icons/CreateProject.gif", "Create project");
		addFileBtn = createButton("/projectviewer/icons/AddFile.gif", "Add file to project");
		importFilesBtn = createButton("/projectviewer/icons/Import.gif", "Import files into this project");
		openAllBtn = createButton("/projectviewer/icons/OpenAll.gif", "Open all files in this project");
		expandBtn = createButton("/projectviewer/icons/Expand.gif", "Expand the file list");
		contractBtn = createButton("/projectviewer/icons/Contract.gif", "Contract the file list");
        saveBtn = createButton(GUIUtilities.loadIcon("Save24.gif"), "Save project(s)");
		launchBrowserBtn= createButton("/projectviewer/icons/web.gif", "Preview in Browser");	
		toolbar.add(createProjectBtn);
		toolbar.add(expandBtn);
		toolbar.add(contractBtn);
		toolbar.add(openAllBtn);
		toolbar.add(addFileBtn);
		toolbar.add(importFilesBtn);
		toolbar.add(removeFileBtn);
		toolbar.add(removeAllFilesBtn);
		toolbar.add(launchBrowserBtn);
		//  set the default state of the toggle buttons...
		removeFileBtn.setEnabled(false);
		removeAllFilesBtn.setEnabled(false);
		addFileBtn.setEnabled(false);
		importFilesBtn.setEnabled(false);
		openAllBtn.setEnabled(false);
		expandBtn.setEnabled(true);
		contractBtn.setEnabled(true);
		launchBrowserBtn.setEnabled(true);   
		
		bar.add(toolbar, BorderLayout.NORTH);
		bar.add(projectCombo, BorderLayout.SOUTH);

		// ok... now create a JPanel for placing the bar and the tree into and then
		// stick that into tabs...
		JPanel allComponents = new JPanel(new BorderLayout());

		allComponents.add(bar, BorderLayout.NORTH);

		tabs.addChangeListener(tsl);

		tabs.addTab(FOLDERS_TAB_TITLE, new JScrollPane(folderTree));
		tabs.addTab(FILES_TAB_TITLE, new JScrollPane(fileTree));
		tabs.addTab(WORKING_FILES_TAB_TITLE, new JScrollPane(workingFileTree));

		allComponents.add(tabs, BorderLayout.CENTER);

		// ok.. add the bar to the tab...
		//contentPanel.getContentPane().add(bar, BorderLayout.NORTH);
		add(allComponents, BorderLayout.CENTER);
		add(status, BorderLayout.SOUTH);

		loadProjectCombo();
		projectCombo.addItemListener(vsl);
		setVisible(true);
	}

	/** Create a tool bar button.
	 *
	 *@param  icon     Description of Parameter
	 *@param  tooltip  Description of Parameter
	 *@return          Description of the Returned Value
	 */
	private JButton createButton(String icon, String tooltip) {
		return createButton(new ImageIcon(getClass().getResource(icon)), tooltip);
	}

	/** Create a tool bar button.
	 *
	 *@param  icon     Description of Parameter
	 *@param  tooltip  Description of Parameter
	 *@return          Description of the Returned Value
	 */
	private JButton createButton(Icon icon, String tooltip) {
		JButton init = new JButton(icon);
		Insets zeroMargin = new Insets(0, 0, 0, 0);
		init.setMargin(zeroMargin);
		init.setToolTipText(tooltip);
		init.addActionListener(vsl);
		return init;
	}

	/** Create and initialize a tree widget.
	 *
	 *@return    Description of the Returned Value
	 */
	private JTree createTree() {
		JTree tree = new ProjectTree();
		//tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new TreeRenderer());
		tree.addTreeSelectionListener(tsl);
		tree.addMouseListener(tsl);
        tree.addMouseListener(cml);
		ToolTipManager.sharedInstance().registerComponent(tree);
		return tree;
	}

	/** Creates an initialzed project combo and adds all known projects */
	private void loadProjectCombo() {
		vsl.pause();
		if (projectCombo.getItemCount() != 0)
			projectCombo.removeAllItems();
		projectCombo.addItem(ALL_PROJECTS);

		Iterator i = ProjectManager.getInstance().projects();
		while (i.hasNext())
			projectCombo.addItem(i.next());
		vsl.resume();
	}

	/** Load a project. */
	private void loadProject() {
		showWaitCursor();

		folderTree.setModel(projectView.getFolderViewModel());
		fileTree.setModel(projectView.getFileViewModel());
		workingFileTree.setModel(projectView.getWorkingFileViewModel());

		expandAll(fileTree);
		expandAll(workingFileTree);

		removeFileBtn.setEnabled(false);
		removeAllFilesBtn.setEnabled(true);
		addFileBtn.setEnabled(true);
		importFilesBtn.setEnabled(true);
		openAllBtn.setEnabled(true);

		vsl.pause();
		projectCombo.setSelectedItem(isAllProjects() ? (Object) ALL_PROJECTS : getCurrentProject());
		vsl.resume();

		showDefaultCursor();

		if (getCurrentProject() != null)
			fireProjectLoaded(getCurrentProject());
	}

}

