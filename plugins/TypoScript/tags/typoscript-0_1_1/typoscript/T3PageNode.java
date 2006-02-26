/***************************************************************
*  Copyright notice
*
*  (c) 2005,2006 Neil Bertram (neil@tasmanstudios.co.nz)
*  All rights reserved
*
*  This plugin is part of the Typo3 project. The Typo3 project is
*  free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*
*  The GNU General Public License can be found at
*  http://www.gnu.org/copyleft/gpl.html.
*  A copy is found in the textfile GPL.txt
*
*
*  This plugin is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  This copyright notice MUST APPEAR in all copies of the source!
***************************************************************/
/**
 * $Id$
 * 
 * This object represents a page on a remote TYPO3 site, and is also
 * the source of data for the JTree in TypoScriptSiteBrowser.
 *
 * @author      Neil Bertram <neil@tasmanstudios.co.nz>
 */

package typoscript;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.xmlrpc.XmlRpcException;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

/**
 * Represents a page node in the sitebrowser tree, but also contains the data linking a page
 * to its templates
 */
public class T3PageNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;
	private String title;
	private int uid;
	private int type;
	private boolean hasTemplate;
	private int setupLines;
	private int constantsLines;
	private int templateUID;
	private String templateTitle;
	private boolean loaded;
	private T3Site belongsTo;
	protected T3PageNodePopupMenu popupMenu;
	
	/**
	 * This constructor is used for generating site root nodes which will only load data
	 * upon being opened
	 * @param site parent site
	 */
	public T3PageNode(T3Site site) {
		super();
		parent = null;
		title = site.getName();
		type = -1; // root node
		uid = 0;
		hasTemplate = false;
		templateUID = -1;
		templateTitle = "";
		
		// Add a "Loading" node inside it, removed once data arrives
		add(new DefaultMutableTreeNode());

		loaded = false;
		belongsTo = site;
		popupMenu = null; // doesn't have one
	}
	
	/**
	 * The constructor for a normal page node, constructed from the
	 * XML-RPC result
	 * 
	 * @param data xmlrpc data for this node
	 * @param site parent site
	 */
	public T3PageNode(Hashtable data, T3Site site) {
		super();
	
		title = (String)data.get("title");
		uid = ((Integer)data.get("uid")).intValue();
		type = ((Integer)data.get("doktype")).intValue();
		Vector templates = (Vector)data.get("templates");
		if (templates.isEmpty()) {
			hasTemplate = false;
			setupLines = -1;
			constantsLines = -1;
			templateUID = -1;
			templateTitle = "";
		} else if (templates.size() == 1){
			// Only one template, display it directly in the tree
			hasTemplate = true;
			Hashtable templateInfo = (Hashtable)templates.get(0);
			setupLines = Integer.parseInt(templateInfo.get("setuplines").toString());
			constantsLines = Integer.parseInt(templateInfo.get("constantslines").toString());
			templateUID = Integer.parseInt(templateInfo.get("templateuid").toString());
			templateTitle = (String)templateInfo.get("templatetitle");
		} else {
			// More than one template, display a sublist of templates, but also make the top-level one
			// point to the first template
			hasTemplate = true;
			Hashtable templateInfo = (Hashtable)templates.get(0);
			setupLines = Integer.parseInt(templateInfo.get("setuplines").toString());
			constantsLines = Integer.parseInt(templateInfo.get("constantslines").toString());
			templateUID = Integer.parseInt(templateInfo.get("templateuid").toString());
			templateTitle = (String)templateInfo.get("templatetitle");
			
			for (int i = 0; i < templates.size(); i++) {
				templateInfo = (Hashtable)templates.get(i);
				this.add(new T3PageNode(site, data, templateInfo));
			}
		}
		
		// Look for children
		Vector ourChildren = (Vector)data.get("children");
		Iterator it = ourChildren.iterator();
		while (it.hasNext()) {
			Hashtable childNode = (Hashtable)it.next();
			this.add(new T3PageNode(childNode, site));
		}
		
		loaded = true;
		belongsTo = site;
		if (hasTemplate) {
			popupMenu = new T3PageNodePopupMenu();
		} else {
			popupMenu = null;
		}
	}
	
	/**
	 * This constructor is for template-only nodes
	 * @param site the parent site
	 * @param parentData the page data
	 * @param templateInfo contains information such as title, uid
	 */
	public T3PageNode(T3Site site, Hashtable parentData, Hashtable templateInfo) {
		super();
		
		title = (String)parentData.get("title");
		uid = ((Integer)parentData.get("uid")).intValue();
		
		type = -5; // special type = template only
		
		loaded = true;
		belongsTo = site;
		
		hasTemplate = true;
		setupLines = Integer.parseInt(templateInfo.get("setuplines").toString());
		constantsLines = Integer.parseInt(templateInfo.get("constantslines").toString());
		templateUID = Integer.parseInt(templateInfo.get("templateuid").toString());
		templateTitle = (String)templateInfo.get("templatetitle");
		
		if (hasTemplate) {
			popupMenu = new T3PageNodePopupMenu();
		} else {
			popupMenu = null;
		}
	}
	
	/**
	 * Only ever called on a site root node, this will convert the node
	 * into the root of a real site tree by fetching the data over XML-RPC
	 * 
	 * @param treeModel the model representing the tree to populate
	 */
	public void loadTree(DefaultTreeModel treeModel) {
		new TreeFetchWorker(treeModel, belongsTo, this).start();
		loaded = true;
	}
	
	public String toString() {
		if (type != -5) {
			return title; // return page title for anything except a template
		} else {
			return templateTitle;
		}
	}

	public int getConstantsLines() {
		return constantsLines;
	}

	public boolean hasTemplate() {
		return hasTemplate;
	}

	public int getSetupLines() {
		return setupLines;
	}

	public int getTemplateUID() {
		return templateUID;
	}

	public String getTemplateTitle() {
		return templateTitle;
	}
	
	public String getTitle() {
		return title;
	}

	public int getType() {
		return type;
	}

	public int getUid() {
		return uid;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public T3Site getSite() {
		return belongsTo;
	}
	
	/**
	 * Opens a page into a jedit buffer
	 * 
	 * @param tree the sitetree that the selection was made in (to calculate path)
	 * @param type either constants or setup
	 * @param view the view to load the buffer into
	 */
	public void open(JTree tree, String type, View view) {
		TreePath selectPath = tree.getSelectionPath();
		T3PageNode selected = (T3PageNode)selectPath.getLastPathComponent();
		String path = "typoscript:" + selected.getSite().getName() + "::";
		Object[] pathComponents = selectPath.getPath();
		for (int i = 2; i < pathComponents.length - 1; i++) {
			T3PageNode node = (T3PageNode)pathComponents[i];
			path += node.getTitle() + "/";
		}
		T3PageNode node = (T3PageNode)pathComponents[pathComponents.length - 1];
		path += node.getTitle() + "."  + node.getTemplateTitle() + "." + node.getUid() + "." + node.getTemplateUID() + "." + type + ".ts";
		
		jEdit.openFile(view, path);
	}
}

/**
 * This class processes the arrival of data in a new thread so as not to hold up the interface
 */
class TreeFetchWorker extends Thread {
	DefaultTreeModel treeModel;
	T3Site curSite;
	T3PageNode curNode;
	
	public TreeFetchWorker(DefaultTreeModel model, T3Site site, T3PageNode node) {
		treeModel = model;
		curSite = site;
		curNode = node;
	}
	public void run() {
		curNode.removeAllChildren();
		curNode.add(new DefaultMutableTreeNode("Retrieving site tree..."));
		treeModel.nodeStructureChanged(curNode);
		
		Hashtable data = null;
		try {
			data = curSite.getWorker().getPageTree();
		} catch (Exception e) {
			curNode.removeAllChildren();
			if (e instanceof XmlRpcException) {
				XmlRpcException xe = (XmlRpcException) e;
				if (xe.code == RemoteCallWorker.JEDITVFS_ERROR_AUTHFAIL) {
					curNode.add(new DefaultMutableTreeNode("Authentication failed - check settings"));
				} else {
					curNode.add(new DefaultMutableTreeNode("Unexpected error: " + e.getMessage()));
				}
			} else if (e instanceof IOException) {
				curNode.add(new DefaultMutableTreeNode("I/O error - Server down?"));
			}
			treeModel.nodeStructureChanged(curNode);
			e.printStackTrace();
			return;
		}
		
		curNode.removeAllChildren();		
		
		// Add all children of the data we loaded (which is the root node of this site)
		Vector ourChildren = (Vector)data.get("children");
		Iterator it = ourChildren.iterator();
		while (it.hasNext()) {
			Hashtable childNode = (Hashtable)it.next();
			curNode.add(new T3PageNode(childNode, curSite));
		}
		
		treeModel.nodeStructureChanged(curNode);
	}
}

/**
 * Represents the popup menu when a template is right clicked
 * Intended to be JDK1.4 compatible, 1.5 has a new way to attach popup menus in a cleaner manner
 * 
 * Only ever instantiated on nodes with templates
 */
class T3PageNodePopupMenu extends JPopupMenu {
	private JTree siteTree;
	private View bufferView;
	
	public T3PageNodePopupMenu() {
		JMenuItem mnuOpenConstants = new JMenuItem("Open constants");
		mnuOpenConstants.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				T3PageNode selected = (T3PageNode)siteTree.getLastSelectedPathComponent();
				selected.open(siteTree, "constants", bufferView);
			}
		});
		JMenuItem mnuOpenSetup = new JMenuItem("Open setup");
		mnuOpenSetup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				T3PageNode selected = (T3PageNode)siteTree.getLastSelectedPathComponent();
				selected.open(siteTree, "setup", bufferView);
			}
		});
		
		this.add(mnuOpenSetup);
		this.add(mnuOpenConstants);
	}
	
	/**
	 * Sets the calling tree and view then invokes the menu display
	 */
	public void show(Component invoker, int x, int y, JTree tree, View view) {
		bufferView = view;
		siteTree = tree;
		super.show(invoker, x, y);
	}
}