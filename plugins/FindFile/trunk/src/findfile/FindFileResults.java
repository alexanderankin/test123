/** * FindFileResults.java - The search results panel. * :tabSize=8:indentSize=8:noTabs=false: * :folding=explicit:collapseFolds=1: * * @author Nicholas O'Leary * @version $Id: FindFileResults.java 13890 Thu Oct 16 16:06:24 CDT 2008 keeleyt83 $*/package findfile;//{{{ importsimport java.awt.*;import java.awt.event.*;import java.util.ArrayList;import java.util.Collections;import java.util.Comparator;import java.util.Enumeration;import java.util.Iterator;import java.util.Vector;import java.util.regex.Matcher;import java.util.regex.Pattern;import javax.swing.*;import javax.swing.border.*;import javax.swing.tree.*;import org.gjt.sp.jedit.*;import org.gjt.sp.jedit.browser.VFSBrowser;import org.gjt.sp.jedit.gui.RolloverButton;import org.gjt.sp.jedit.io.VFSManager;import org.gjt.sp.jedit.search.DirectoryListSet;import org.gjt.sp.jedit.search.SearchAndReplace;import org.gjt.sp.jedit.search.SearchDialog;import org.gjt.sp.util.Log;import org.gjt.sp.util.StandardUtilities;//}}}//{{{ ResultCellRendererpublic class FindFileResults    extends JPanel {    //{{{ Private members    private View view;    private JTree tree;    private DefaultMutableTreeNode rootNode;    private DefaultTreeModel treeModel;    private JLabel status;    private static Pattern ArchivePattern;    private RolloverButton newSearchButton;    private RolloverButton removeAllSearchesButton;    private RolloverButton toggleMultipleSearchesButton;    private boolean isMultiResults = true;    //}}}    //{{{ Constructor    /**     * Constructor.     * @param v The active view.     */    public FindFileResults(View v) {	view = v;	setBorder(new EmptyBorder(0,0,0,0));	setLayout(new BorderLayout());	Box toolBar = Box.createHorizontalBox();	toolBar.setBorder(new EmptyBorder(0,0,0,0));	status = new JLabel();	status.setText(jEdit.getProperty("FindFilePlugin.status-message.blank"));	toolBar.add(status);	toolBar.add(Box.createGlue());	ActionHandler ah = new ActionHandler();	newSearchButton = new RolloverButton(GUIUtilities.loadIcon(jEdit.getProperty("vfs.browser.search-directory.icon")));	newSearchButton.setToolTipText(jEdit.getProperty("FindFilePlugin.action-labels.newSearch"));	newSearchButton.addActionListener(ah);	toolBar.add(newSearchButton);	removeAllSearchesButton = new RolloverButton(GUIUtilities.loadIcon(jEdit.getProperty("hypersearch-results.clear.icon")));	removeAllSearchesButton.setToolTipText(jEdit.getProperty("FindFilePlugin.action-labels.removeAllSearches"));	removeAllSearchesButton.addActionListener(ah);	toolBar.add(removeAllSearchesButton);	toggleMultipleSearchesButton = new RolloverButton(GUIUtilities.loadIcon(jEdit.getProperty("hypersearch-results.multi.multiple.icon")));	toggleMultipleSearchesButton.setToolTipText(jEdit.getProperty("FindFilePlugin.action-labels.toggleMultple"));	toggleMultipleSearchesButton.addActionListener(ah);	toolBar.add(toggleMultipleSearchesButton);	rootNode = new DefaultMutableTreeNode();	treeModel = new DefaultTreeModel(rootNode);	tree = new JTree(treeModel);	tree.setShowsRootHandles(true);	tree.setRootVisible(false);	tree.addMouseListener(new MouseHandler());	tree.addKeyListener(new KeyHandler());	tree.setCellRenderer(new ResultCellRenderer());	add(BorderLayout.NORTH, toolBar);	add(BorderLayout.CENTER, new JScrollPane(tree));    } //}}}    public class ActionHandler implements ActionListener {	public void actionPerformed(ActionEvent e) {	    Object source = e.getSource();	    if (source == newSearchButton) {		newSearch();	    } else if (source == removeAllSearchesButton) {		removeAllSearches();	    } else if (source == toggleMultipleSearchesButton) {		toggleMultipleSearches();	    } else {		// Shouldn't be here!	    }	}    }    public void removeAllSearches () {	status.setText(jEdit.getProperty("FindFilePlugin.status-message.blank"));	rootNode.removeAllChildren();	treeModel.reload();	hideDockable();    }    public void newSearch() {	FindFileDialog fsd = new FindFileDialog(view);	fsd.setVisible(true);    }    public void toggleMultipleSearches() {	isMultiResults = !isMultiResults;	if (!isMultiResults) {	    toggleMultipleSearchesButton.setIcon(GUIUtilities.loadIcon(jEdit.getProperty("hypersearch-results.multi.single.icon")));	    for(int i = rootNode.getChildCount() - 2; i >= 0; i--) {		treeModel.removeNodeFromParent((MutableTreeNode)rootNode.getChildAt(i));	    }	} else {	    toggleMultipleSearchesButton.setIcon(GUIUtilities.loadIcon(jEdit.getProperty("hypersearch-results.multi.multiple.icon")));	}    }    //{{{ searchStarted    /**     * Called when a search is started, to update the status.     */    public void searchStarted() {	status.setText(jEdit.getProperty("FindFilePlugin.status-message.started"));	this.validate();    } //}}}    //{{{ searchDone    /**     * Called after a search has been performed.     * @param node The result information     */    public void searchDone(DefaultMutableTreeNode node, String searchFilter, String directory) {	Log.log(Log.DEBUG, this, " search done: " + node);	String multipleResults = jEdit.getProperty("options.FindFilePlugin.multipleResults", "true");	if (multipleResults.equals("false")) {	    rootNode.removeAllChildren();	}	rootNode.add(node);	String sortByS = jEdit.getProperty(FindFileOptionPane.OPTIONS + "sortBy", "path");	int sortBy = (sortByS.equals("path") ? ResultComparator.SORT_BY_PATH_AZ : ResultComparator.SORT_BY_FILENAME_AZ);	this.sortResults(node, new ResultComparator(sortBy));	status.setText(jEdit.getProperty("FindFilePlugin.status-message.finished") + " " + "\"" + searchFilter + "\"" + " in " + "\"" + directory + "\"" + ":");	SearchOptions options = (SearchOptions)node.getUserObject();	if (options.openResults)	    openAllResults();	this.validate();    } //}}}    //{{{ getSelectedResultTreeNode    /**     * @return The selected result, or null if a result is not selected.     */    private ResultTreeNode getSelectedResultTreeNode() {	TreePath path = tree.getSelectionPath();	if (path == null)	    return null;	DefaultMutableTreeNode value = (DefaultMutableTreeNode)path.getLastPathComponent();	Object resultO = value.getUserObject();	if (resultO instanceof ResultTreeNode)	    return (ResultTreeNode)resultO;	return null;    } //}}}    //{{{ isArchive    // TODO: Is this the reason why it can't return archive file results?    public static boolean isArchive(String path) {	Matcher archiveMatcher;	if (ArchivePattern == null) {	    String filter = jEdit.getProperty(FindFileOptionPane.OPTIONS + "archiveFilter", jEdit.getProperty(FindFileOptionPane.OPTION_PANE_DEFAULTS + "archiveFilter"));	    try {		ArchivePattern = Pattern.compile(StandardUtilities.globToRE(filter));	    } catch (Exception e) {		Log.log(Log.DEBUG, FindFileResults.class, " ERROR: " + e.getMessage());		Log.log(Log.DEBUG, FindFileResults.class, " Check the Archive filter in the plugins options.");	    }	}	archiveMatcher = ArchivePattern.matcher(path);	return (ArchivePattern != null && archiveMatcher.matches());    } //}}}    //{{{ open    public boolean open(View view, String path, boolean multiple) {	if (isArchive(path)) {	    if (!multiple) {		openArchive(path);		return true;	    } else {		Log.log(Log.MESSAGE, this, "Ignoring archive during multiple open: " + path);		return false;	    }	}	boolean openable = false;	if (jEdit.getProperty(FindFileOptionPane.OPTIONS + "useModeDetection", "true")	     .equals("true")) {	    Mode[] modes = jEdit.getModes();	    for (int i = 0; i < modes.length; i++) {		String filenameGlob = (String)modes[i].getProperty("filenameGlob");		if (filenameGlob != null && filenameGlob.length() != 0) {		    try {			Pattern filenamePattern = Pattern.compile(StandardUtilities.globToRE(filenameGlob));			Matcher filenameMatcher = filenamePattern.matcher(path);			if (filenameMatcher.matches()) {			    openable = true;			    break;			}		    } catch (Exception e) {		    }		}	    }	} else {	    openable = true;	}	if (openable) {	    jEdit.openFile(view, path);	    return true;	} else {	    Log.log(Log.MESSAGE, this, "No edit mode found for file: " + path);	    return false;	}    } //}}}    //{{{ openArchive    public void openArchive(String path) {	if (VFSManager.getVFSForProtocol("archive") instanceof org.gjt.sp.jedit.io.UrlVFS) {	    GUIUtilities.error(view, "FindFilePlugin.archive-not-installed", new Object[] {  });	} else {	    VFSBrowser.browseDirectory(view, "archive:" + path + "!");	}    } //}}}    //{{{ openSelected    /**     * Opens the selected file.     */    private void openSelected() {	ResultTreeNode selected = getSelectedResultTreeNode();	if (selected == null)	    return;	Log.log(Log.DEBUG, this, " open: " + selected.toString());	if (!open(view, selected.fullname, false)) {	    GUIUtilities.error(this, "FindFilePlugin.failed-to-open", new Object[] {		selected.shortname	    });	}    } //}}}    //{{{ openAllResults    /**     * Opens all of the results under the selected search.     */    private void openAllResults() {	TreePath path = tree.getSelectionPath();	if (path == null)	    return;	DefaultMutableTreeNode value = (DefaultMutableTreeNode)path.getLastPathComponent();	Vector failed = new Vector();	Enumeration enumer = value.children();	while (enumer.hasMoreElements()) {	    ResultTreeNode child = (ResultTreeNode)((DefaultMutableTreeNode)enumer.nextElement()).getUserObject();	    if (!open(view, child.fullname, true))		failed.addElement(child);	}	if (failed.size() != 0) {	    //Log.log(Log.MESSAGE,this,"No suitable edit mode found for the following files:");	    //for (int i=0;i<failed.size();i++)	    //   Log.log(Log.MESSAGE,this," "+((ResultTreeNode)failed.elementAt(i)).fullname);	    GUIUtilities.error(this, "FindFilePlugin.failed-to-open-some", new Object[] {  });	}    } //}}}    //{{{ hideDockable() method    private void hideDockable() {	view.getDockableWindowManager().hideDockableWindow("FindFilePlugin");    } //}}}    //{{{ closeAllResults    /**     * Closes all of the results under the selected search.     */    private void closeAllResults() {	TreePath path = tree.getSelectionPath();	if (path == null)	    return;	DefaultMutableTreeNode value = (DefaultMutableTreeNode)path.getLastPathComponent();	Enumeration enumer = value.children();	while (enumer.hasMoreElements()) {	    ResultTreeNode child = (ResultTreeNode)((DefaultMutableTreeNode)enumer.nextElement()).getUserObject();	    Buffer b = jEdit.getBuffer(child.fullname);	    if (b != null)		jEdit.closeBuffer(view, b);	}    } //}}}    //{{{ closeSelected    /**     * Closes the selected file.     */    private void closeSelected() {	ResultTreeNode selected = getSelectedResultTreeNode();	if (selected == null)	    return;	Log.log(Log.DEBUG, this, " close: " + selected.toString());	Buffer b = jEdit.getBuffer(selected.fullname);	if (b != null)	    jEdit.closeBuffer(view, b);    } //}}}    //{{{ toggleSelected    /**     * Toggles to open/close state of the selected file.     */    private void toggleSelected() {	ResultTreeNode selected = getSelectedResultTreeNode();	if (selected == null)	    return;	Log.log(Log.DEBUG, this, " toggle: " + selected.toString());	if (selected.isOpen())	    closeSelected();	else	    openSelected();    } //}}}    //{{{ deleteSelected    /**     * Deletes the selected file - after confirmation.     */    private void deleteSelected() {	ResultTreeNode selected = getSelectedResultTreeNode();	if (selected == null)	    return;	Log.log(Log.DEBUG, this, " delete: " + selected.fullname);	GUIUtilities.error(this, "FindFilePlugin.not-implemented", new Object[] {	    selected.shortname	});	/*	GUIUtilities.confirm(this,			     "FindFilePlugin.delete-confirm",			     new Object[] {selected.shortname},			     JOptionPane.YES_NO_OPTION,			     JOptionPane.QUESTION_MESSAGE);	*/    } //}}}    //{{{ sortResults    private void sortResults(DefaultMutableTreeNode value, Comparator c) {	int length = value.getChildCount();	ArrayList items = new ArrayList(length);	for (int i = 0; i < length; i++)	    items.add(value.getChildAt(i));	value.removeAllChildren();	Collections.sort(items, c);	Iterator iter = items.iterator();	while (iter.hasNext())	    value.add((MutableTreeNode)iter.next());	treeModel.reload();	if (value.getChildCount() > 0)	    tree.makeVisible(new TreePath(((DefaultMutableTreeNode)value.getChildAt(0)).getPath()));	TreePath tp = new TreePath(new Object[] { rootNode, value });	tree.scrollPathToVisible(tp);	tree.setSelectionPath(tp);    } //}}}    //{{{ Inner Classes    //{{{ MouseHandler    class MouseHandler	extends MouseAdapter {	//{{{ Private Memebers	private JPopupMenu filePopupMenu;	private JPopupMenu searchPopupMenu;	private JPopupMenu archivePopupMenu;	//}}}	//{{{ mousePressed	@Override public void mousePressed(MouseEvent evt) {	    if (evt.isConsumed())		return;	    TreePath path = tree.getPathForLocation(evt.getX(), evt.getY());	    if (path == null)		return;	    tree.setSelectionPath(path);	    DefaultMutableTreeNode value = (DefaultMutableTreeNode)path.getLastPathComponent();	    if (value.getParent() != rootNode && value.isLeaf()) {		ResultTreeNode result = (ResultTreeNode)value.getUserObject();		if (GUIUtilities.isPopupTrigger(evt)) {		    if (FindFileResults.isArchive(result.fullname))			showArchivePopupMenu(evt);		    else			showFilePopupMenu(evt);		} else {		    if (evt.getClickCount() == 2) {			toggleSelected();		    } else {			if (result.isOpen())			    openSelected();		    }		}	    } else {		if (GUIUtilities.isPopupTrigger(evt))		    showSearchPopupMenu(evt);	    }	} //}}}	//{{{ showArchivePopupMenu	private void showArchivePopupMenu(MouseEvent evt) {	    if (archivePopupMenu == null) {		archivePopupMenu = new JPopupMenu();		archivePopupMenu.add(new BrowseArchiveAction());		//archivePopupMenu.add(new DeleteFileAction());		archivePopupMenu.addSeparator();		archivePopupMenu.add(new CopyResultAction());		archivePopupMenu.addSeparator();		archivePopupMenu.add(new BrowsePathAction());		archivePopupMenu.addSeparator();		archivePopupMenu.add(new SearchInArchiveAction());	    }	    GUIUtilities.showPopupMenu(archivePopupMenu, evt.getComponent(), evt.getX(), evt.getY());	    evt.consume();	} //}}}	//{{{ showFilePopupMenu	private void showFilePopupMenu(MouseEvent evt) {	    if (filePopupMenu == null) {		filePopupMenu = new JPopupMenu();		filePopupMenu.add(new OpenFileAction());		// Open in New View		// Open in New Plain View		filePopupMenu.add(new CloseFileAction());		//filePopupMenu.addSeparator();		// Remove Node		//filePopupMenu.add(new DeleteFileAction());		filePopupMenu.addSeparator();		filePopupMenu.add(new CopyResultAction());		filePopupMenu.add(new BrowsePathAction());		filePopupMenu.add(new SearchInFileAction());	    }	    GUIUtilities.showPopupMenu(filePopupMenu, evt.getComponent(), evt.getX(), evt.getY());	    evt.consume();	} //}}}	//{{{ showSearchPopupMenu	private void showSearchPopupMenu(MouseEvent evt) {	    if (searchPopupMenu == null) {		searchPopupMenu = new JPopupMenu();		searchPopupMenu.add(new OpenAllResultsAction());		searchPopupMenu.add(new CloseAllResultsAction());		searchPopupMenu.addSeparator();		searchPopupMenu.add(new CopyAllResultsAction());		searchPopupMenu.addSeparator();		searchPopupMenu.add(new RemoveSearchAction());		searchPopupMenu.addSeparator();		searchPopupMenu.add(new SearchInFilesAction());		searchPopupMenu.addSeparator();		JMenu sortBy = new JMenu("Sort results");		sortBy.add(new SortByFileNameAction());		sortBy.add(new SortByPathAction());		searchPopupMenu.add(sortBy);	    }	    GUIUtilities.showPopupMenu(searchPopupMenu, evt.getComponent(), evt.getX(), evt.getY());	    evt.consume();	} //}}}    } //}}}    //{{{ KeyHandler    class KeyHandler	extends KeyAdapter {	//{{{ keyPressed	@Override public void keyPressed(KeyEvent evt) {	    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {		toggleSelected();		SwingUtilities.invokeLater(new Runnable() {		    public void run() {			tree.requestFocus();		    }		});		evt.consume();	    }	} //}}}    } //}}}    //{{{ Actions    //{{{ Pop-up menu actions    //{{{ OpenFileAction    class OpenFileAction	extends AbstractAction {	public OpenFileAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.open"));	}	public void actionPerformed(ActionEvent e) {	    openSelected();	}    } //}}}    //{{{ CloseFileAction    class CloseFileAction	extends AbstractAction {	public CloseFileAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.close"));	}	public void actionPerformed(ActionEvent e) {	    closeSelected();	}    } //}}}    //{{{ DeleteFileAction    class DeleteFileAction	extends AbstractAction {	public DeleteFileAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.delete"));	}	public void actionPerformed(ActionEvent e) {	    deleteSelected();	}    } //}}}    //{{{ CopyResultAction    class CopyResultAction	extends AbstractAction {	public CopyResultAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.copyResult"));	}	public void actionPerformed(ActionEvent e) {	    ResultTreeNode result = getSelectedResultTreeNode();	    Registers.setRegister('$', result.fullname);	}    } //}}}    //{{{ BrowsePathAction    class BrowsePathAction	extends AbstractAction {	public BrowsePathAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.browsePath"));	}	public void actionPerformed(ActionEvent e) {	    ResultTreeNode result = getSelectedResultTreeNode();	    if (result == null)		return;	    String path = VFSManager.getVFSForPath(result.fullname).getParentOfPath(result.fullname);	    VFSBrowser.browseDirectory(view, path);	}    } //}}}    //{{{ BrowseArchiveAction    class BrowseArchiveAction	extends AbstractAction {	public BrowseArchiveAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.browseArchive"));	}	public void actionPerformed(ActionEvent e) {	    ResultTreeNode result = getSelectedResultTreeNode();	    if (result == null)		return;	    openArchive(result.fullname);	}	@Override public boolean isEnabled() {	    return !(VFSManager.getVFSForProtocol("archive") instanceof org.gjt.sp.jedit.io.UrlVFS);	}    } //}}}    //{{{ SearchInFileAction    class SearchInFileAction	extends AbstractAction {	public SearchInFileAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.searchInFile"));	}	public void actionPerformed(ActionEvent e) {	    ResultTreeNode result = getSelectedResultTreeNode();	    if (result == null)		return;	    String path = VFSManager.getVFSForPath(result.fullname).getParentOfPath(result.fullname);	    String filename = VFSManager.getVFSForPath(result.fullname).getFileName(result.fullname);	    DirectoryListSet file = new DirectoryListSet(path, filename, false);	    SearchAndReplace.setSearchFileSet(file);	    if (StandardUtilities.compareStrings(jEdit.getBuild(), "04.02.07.00", false) >= 0) {		//SearchDialog.preloadSearchDialog(view);		SearchDialog.showSearchDialog(view, "", SearchDialog.DIRECTORY);	    } else {		SearchAndReplace.setSearchString(GUIUtilities.input(view, "FindFilePlugin.search-in-file", ""));		SearchAndReplace.hyperSearch(view);	    }	}    } //}}}    //{{{ SearchInArchiveAction    class SearchInArchiveAction	extends AbstractAction {	public SearchInArchiveAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.searchInArchive"));	}	public void actionPerformed(ActionEvent e) {	    ResultTreeNode result = getSelectedResultTreeNode();	    if (result == null)		return;	    DirectoryListSet file = new DirectoryListSet("archive:" + result.fullname + "!", "*", true);	    SearchAndReplace.setSearchFileSet(file);	    if (StandardUtilities.compareStrings(jEdit.getBuild(), "04.02.07.00", false) >= 0) {		//SearchDialog.preloadSearchDialog(view);		SearchDialog.showSearchDialog(view, "", SearchDialog.DIRECTORY);	    } else {		SearchAndReplace.setSearchString(GUIUtilities.input(view, "FindFilePlugin.search-in-file", ""));		SearchAndReplace.hyperSearch(view);	    }	}    } //}}}    //{{{ RemoveSearchAction    class RemoveSearchAction	extends AbstractAction {	public RemoveSearchAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.removeSearch"));	}	public void actionPerformed(ActionEvent e) {	    status.setText(jEdit.getProperty("FindFilePlugin.status-message.blank"));	    TreePath path = tree.getSelectionPath();	    if (path == null)		return;	    treeModel.removeNodeFromParent((DefaultMutableTreeNode)path.getLastPathComponent());	}    } //}}}    //{{{ CopyAllResultsAction    class CopyAllResultsAction	extends AbstractAction {	public CopyAllResultsAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.copyAllResults"));	}	public void actionPerformed(ActionEvent e) {	    TreePath path = tree.getSelectionPath();	    if (path == null)		return;	    DefaultMutableTreeNode value = (DefaultMutableTreeNode)path.getLastPathComponent();	    String text = value.toString() + "\n";	    for (int i = 0; i < value.getChildCount(); i++) {		DefaultMutableTreeNode child = (DefaultMutableTreeNode)value.getChildAt(i);		text += ((ResultTreeNode)child.getUserObject()).toString() + "\n";	    }	    Registers.setRegister('$', text);	}    } //}}}    //{{{ OpenAllResultsAction    class OpenAllResultsAction	extends AbstractAction {	public OpenAllResultsAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.openAllResults"));	}	public void actionPerformed(ActionEvent e) {	    openAllResults();	}    } //}}}    //{{{ CloseAllResultsAction    class CloseAllResultsAction	extends AbstractAction {	public CloseAllResultsAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.closeAllResults"));	}	public void actionPerformed(ActionEvent e) {	    closeAllResults();	}    } //}}}    //{{{ SearchInFilesAction    class SearchInFilesAction	extends AbstractAction {	public SearchInFilesAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.searchInFiles"));	}	public void actionPerformed(ActionEvent e) {	    TreePath path = tree.getSelectionPath();	    if (path == null)		return;	    DefaultMutableTreeNode value = (DefaultMutableTreeNode)path.getLastPathComponent();	    SearchOptions searchOptions = (SearchOptions)value.getUserObject();	    DirectoryListSet file = new DirectoryListSet(searchOptions.path, searchOptions.filter, searchOptions.recursive);	    SearchAndReplace.setSearchFileSet(file);	    if (StandardUtilities.compareStrings(jEdit.getBuild(), "04.02.07.00", false) >= 0) {		//SearchDialog.preloadSearchDialog(view);		SearchDialog.showSearchDialog(view, "", SearchDialog.DIRECTORY);	    } else {		SearchAndReplace.setSearchString(GUIUtilities.input(view, "FindFilePlugin.search-in-file", ""));		SearchAndReplace.hyperSearch(view);	    }	}    } //}}}    //{{{ SortByFileNameAction    class SortByFileNameAction	extends AbstractAction {	public SortByFileNameAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.sortByFileName"));	}	public void actionPerformed(ActionEvent e) {	    TreePath path = tree.getSelectionPath();	    if (path == null)		return;	    DefaultMutableTreeNode value = (DefaultMutableTreeNode)path.getLastPathComponent();	    sortResults(value, new ResultComparator(ResultComparator.SORT_BY_FILENAME_AZ));	}    } //}}}    //{{{ SortByPathAction    class SortByPathAction	extends AbstractAction {	public SortByPathAction() {	    super(jEdit.getProperty("FindFilePlugin.action-labels.sortByPath"));	}	public void actionPerformed(ActionEvent e) {	    TreePath path = tree.getSelectionPath();	    if (path == null)		return;	    DefaultMutableTreeNode value = (DefaultMutableTreeNode)path.getLastPathComponent();	    sortResults(value, new ResultComparator(ResultComparator.SORT_BY_PATH_AZ));	}    } //}}}} //}}}class ResultCellRenderer    extends DefaultTreeCellRenderer {    private static Icon fileClosedIcon = GUIUtilities.loadIcon("File.png");    private static Icon fileOpenedIcon = GUIUtilities.loadIcon("OpenFile.png");    private static Icon archiveIcon = GUIUtilities.loadIcon("DriveSmall.png");    private boolean isopen;    Font plainFont, boldFont;    //{{{ ResultCellRenderer constructor    ResultCellRenderer(){	plainFont = UIManager.getFont("Tree.font");	if(plainFont == null)	    plainFont = jEdit.getFontProperty("metal.secondary.font");	boldFont = new Font(plainFont.getName(),Font.BOLD,	plainFont.getSize());    } //}}}    //{{{ getTreeCellRendererComponent    @Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {	super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);	isopen = false;	if (value instanceof DefaultMutableTreeNode) {	    setFont(boldFont);	    Object userO = ((DefaultMutableTreeNode)value).getUserObject();	    if (userO instanceof ResultTreeNode) {		setFont(plainFont);		ResultTreeNode result = (ResultTreeNode)userO;		if (FindFileResults.isArchive(result.fullname)) {		    this.setIcon(ResultCellRenderer.archiveIcon);		} else {		    if (result.isOpen()) {			this.setIcon(ResultCellRenderer.fileOpenedIcon);			isopen = true;		    } else {			this.setIcon(ResultCellRenderer.fileClosedIcon);		    }		}	    } else if (userO instanceof SearchOptions) {		this.setIcon(null);		// Would like to set custom icon.	    } else {		this.setIcon(null);	    }	}	return this;    } //}}}    //{{{ paintComponent(Graphics) method    @Override public void paintComponent(Graphics g) {	if (isopen) {	    FontMetrics fm = getFontMetrics(getFont());	    int x;	    int y;	    y = fm.getAscent() + 2;	    if (getIcon() == null) {		x = 0;	    } else {		x = getIcon().getIconWidth() + getIconTextGap();	    }	    g.setColor(getForeground());	    g.drawLine(x, y, x + fm.stringWidth(getText()), y);	}	super.paintComponent(g);    } //}}}} //}}}