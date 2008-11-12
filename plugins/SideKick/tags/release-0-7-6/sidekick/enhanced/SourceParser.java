/*
 * SourceParser.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 by Martin Raspe
 * (hertzhaft@biblhertz.it)
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
package sidekick.enhanced;

import javax.swing.tree.*;
import javax.swing.text.Position;
import javax.swing.ImageIcon;
import java.util.*;
import java.util.regex.*;

import java.net.URL;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
import org.gjt.sp.util.Log;

import sidekick.SideKickParsedData;
import sidekick.SideKickParser;

import errorlist.*;


/**
 * SourceParser: parses source and builds a sidekick structure tree
 * Parsers are based on regular expressions and will therefore
 * not able to correctly parse irregular source
 *
 * @author     Martin Raspe
 * @created    Oct 15, 2005
 * @modified   $Id$
 * @version    $Revision$
 */
public class SourceParser extends SideKickParser implements PartialParser {
	//{{{ Icons
	public ImageIcon PACKAGE_ICON;
	public ImageIcon USE_ICON;
	public ImageIcon SUB_ICON;
	public ImageIcon PACKAGE_INVALID_ICON;
	public ImageIcon USE_INVALID_ICON;
	public ImageIcon SUB_INVALID_ICON;
	public ImageIcon COMMENT_ICON;
	//}}}

	//{{{ vars
	public String LINE_COMMENT;
	public String COMMENT;	// title for block comments
	public String MAIN;	// title for default package
	public String USE;	// title for import modules

	public String USE_KEY   = "---use---";
	public String SUB_KEY   = "---sub---";
	public String PKG_KEY   = "---pkg---";

	protected SideKickParsedData data;
	protected PackageMap packages;
	protected ArrayList commentList;
	protected SourceAsset _asset;
	protected SourceAsset _pkgAsset;

	protected Position _start;
	protected Position _end;
    protected int _lastLineNumber = 0;

    protected int startLine = 0;

	//}}}

/**	 * Constructs a new SourceParser object
	 *
	 * @param name See sidekick.SidekickParser.
	 */
	public SourceParser(String name) {
		super(name);
		loadIcons(name, getClass());
	}

	public SourceParser(String name, Class cls) {
		super(name);
		loadIcons(name, cls);
	}
/**	 * Parses the given text and returns a tree model.
	 *
	 * @param buffer The buffer to parse.
	 * @param errorSource An error source to add errors to.
	 *
	 * @return A new instance of the <code>SourceParsedData</code> class.
	 */
	public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource) {
		data = new SideKickParsedData(buffer.getName());
		packages = new PackageMap(new PackageComparator());
		commentList = new ArrayList();
		parseBuffer(buffer, errorSource);
		completePackageAsset(_end, _lastLineNumber);
		Log.log(Log.DEBUG, this, "parsing completed");
		buildTrees();
		Log.log(Log.DEBUG, this, "tree built");
		return data;
	}

    /**
     * Parse the contents of the given text.  This is the entry point to use when
     * only a portion of the buffer text is to be parsed.  Note that <code>setLineOffset</code>
     * should be called prior to calling this method, otherwise, tree node positions
     * may be off.
     * <p>
     * This default implementation simply delegates to <code>parse(Buffer, DefaultErrorSource)</code>.
     * Subclasses should override to actually parse appropriately.
     *
     * @param buffer       the buffer containing the text to parse
     * @param text         the text to parse
     * @param errorSource  where to send errors
     * @return             the parsed buffer as a tree
     */
     public SideKickParsedData parse( Buffer buffer, String text, DefaultErrorSource errorSource ) {
        return parse(buffer, errorSource);
     }

    /**
     * If called by another parser to parse part of a file (for example, to parse
     * a script tag in an html document), this can be set to the offset of the
     * text to be parsed so that the node locations can be set correctly.
     *
     * @param startLine the starting line in the buffer of the text that is to
     * be parsed.
     */
     public void setStartLine(int startLine) {
        this.startLine = startLine;
     }

	protected void parseBuffer(Buffer buffer, DefaultErrorSource errorSource) {
		}

	protected void loadIcons(String name, Class cls) {
		PACKAGE_ICON		= loadIcon(name, cls, "package-icon");
		USE_ICON		= loadIcon(name, cls, "use-icon");
		SUB_ICON		= loadIcon(name, cls, "sub-icon");
		PACKAGE_INVALID_ICON	= loadIcon(name, cls, "package_invalid-icon");
		USE_INVALID_ICON	= loadIcon(name, cls, "use-invalid-icon");
		SUB_INVALID_ICON	= loadIcon(name, cls, "sub-invalid-icon");
		COMMENT_ICON		= loadIcon(name, cls, "comment-icon");
	}

	protected ImageIcon loadIcon(String name, Class cls, String icon) {
		String iconfile = jEdit.getProperty("sidekick.parser." + name + "." + icon);
		URL res = cls.getResource("/icons/" + iconfile);
		if (res == null) return null;
		return new ImageIcon(res);
	}

	// Sets the end position of the most recently created asset.
	protected void completeAsset(Position end) {
		if (_asset != null) _asset.setEnd(end);
		_asset = null;
		}

	// Sets the end position of the most recently created asset.
	protected void completeAsset(Position end, String desc) {
		if (_asset != null)
			_asset.setLongDescription(desc);
		completeAsset(end);
		}

	// Sets the end position of the most recently created asset.
	protected void completeAsset(Position end, int lineNo) {
		if (_asset != null)
			_asset.setLongDescription(
                		_asset.getLongDescription()+ "-" + lineNo
				);
		_lastLineNumber = lineNo;
		completeAsset(end);
		}

	// Sets the end position of the most recently created package asset.
	protected void completePackageAsset(Position end, int lineNo) {
		completeAsset(end, lineNo);
		_asset = _pkgAsset;
		completeAsset(end, lineNo);
        }

    // Adds a multi-line asset to the list of its package
	protected void addAsset(String typ, String p, String name, int lineNo, Position start) {
		completeAsset(start, lineNo);
		_asset = new SourceAsset(name, lineNo, start);
		packages.addToList(typ, p, _asset);
		}

	// Adds a one-line asset (don't complete the current _asset yet)
	protected void addLineAsset(String typ, String p, String name, int lineNo, Position start, Position end) {
		SourceAsset a = new SourceAsset(name, lineNo, start);
		a.setEnd(end);
		packages.addToList(typ, p, a);
		}

	//Adds an asset to the pod list
	protected void addCommentAsset(String name, int lineNo, Position start) {
		completeAsset(start, lineNo);
		_asset = new SourceAsset(name, lineNo, start);
		commentList.add(_asset);
		}

	// Adds a new package to the "packages" TreeMap.
	protected void addPackageAsset(String name, int lineNo, Position start) {
		completePackageAsset(start, lineNo);
		SourceAsset a = new SourceAsset(name, lineNo, start);
		_pkgAsset = a;
        packages.addPackage(name, a);
		}

	// Builds the sidekick tree from the "packages" TreeMap
	protected void buildTrees() {
		Set list = packages.keySet();
		Iterator i = list.iterator();
		while(i.hasNext()) {
			String name = (String) i.next();
			HashMap h = (HashMap) packages.get(name);
			SourceAsset a = (SourceAsset) h.get(PKG_KEY);
			a.setIcon(PACKAGE_ICON);
			DefaultMutableTreeNode t = new DefaultMutableTreeNode(a);
			newTree(t, USE, (ArrayList) h.get(USE_KEY), USE_ICON);
			addList(t, (ArrayList) h.get(SUB_KEY), SUB_ICON);
			data.root.add(t);
			}
		newTree(data.root, COMMENT, commentList, COMMENT_ICON);
		}

	// Builds a tree from asset list "list" and adds it to tree node "n".
	protected void newTree(DefaultMutableTreeNode n, String name, ArrayList list, ImageIcon icon) {
		if (list.size() == 0) return;
		SourceAsset first = (SourceAsset) list.get(0);
		SourceAsset branch = new SourceAsset(name, first.getLineNo(), first.getStart());
		branch.setIcon(icon);
		DefaultMutableTreeNode t = new DefaultMutableTreeNode(branch);
		addList(t, list, null);
		n.add(t);
		}

	//Adds an Asset list "list" to a tree node "n"
	protected void addList(DefaultMutableTreeNode t, ArrayList list, ImageIcon icon) {
		Collections.sort(list, new AssetComparator());
		Iterator i = list.iterator();
		while(i.hasNext()) {
			SourceAsset a = (SourceAsset) i.next();
			a.setIcon(icon);
			t.add(new DefaultMutableTreeNode(a));
			}
		}

	// match a line and return the specified group if found
	protected String find(String line, Pattern p, int g) {
		Matcher m = p.matcher(line);
		return m.find() ? m.group(g) : null;
		}

	// match a line and return package and subname if found
	protected String[] find2(String line, Pattern p) {
		String[] res = new String[2];
		Matcher m = p.matcher(line);
		if (m.find()) {
			res[0] = m.group(1);
			res[1] = m.group(2);
			}
		else res = null;
		return res;
		}

	// compare strings, ignore case
	protected class AssetComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			String s1 = ((SourceAsset) o1).toString();
			String s2 = ((SourceAsset) o2).toString();
			return s1.toLowerCase().compareTo(s2.toLowerCase());
			}
		}

	// sort package names
	public class PackageComparator implements Comparator{
		public int compare(Object o1, Object o2) {
			String s1 = (String)o1;
			String s2 = (String)o2;
			int comp = s1.toLowerCase().compareTo(s2.toLowerCase());
			if (comp == 0) return comp;
			// pull up module MAIN
			if (s1.equals(MAIN)) return -1;
			if (s2.equals(MAIN)) return 1;
			return comp;
			}
		}

	// reflects perl script structure
	public class PackageMap extends TreeMap {
		public PackageMap() {
			super();
			}

		public PackageMap(PackageComparator pc) {
			super(pc);
			}

		// add a HashMap for a new package name
		public HashMap addPackage(String p, SourceAsset a) {
			if (this.containsKey(p))
                return (HashMap) this.get(p);
			HashMap h = new HashMap();
			h.put(PKG_KEY, a);
			h.put(USE_KEY, new ArrayList());
			h.put(SUB_KEY, new ArrayList());
			this.put(p, h);
			return h;
			}

		// get the package asset by name
		public SourceAsset getPackageAsset(String p) {
			if (! this.containsKey(p))
				return null;
			HashMap h = (HashMap) this.get(p);
			return (SourceAsset) h.get(PKG_KEY);
			}

		// set the end position for a package asset
		public void completePackageAsset(String p, Position end) {
			SourceAsset a = getPackageAsset(p);
			if (a != null) a.setEnd(end);
			}

		public void completePackageAsset(String p, Position end, String desc) {
			SourceAsset a = getPackageAsset(p);
			if (a == null)
			return;
			a.setLongDescription(desc);
			a.setEnd(end);
			}

		public void completePackageAsset(String p, Position end, int lineNo) {
			SourceAsset a = getPackageAsset(p);
			if (a == null)
			return;
			a.setLongDescription(a.getLongDescription() + "-" + lineNo);
			a.setEnd(end);
			}

		// add an Asset of type "typ" to the TreeMap for package "p"
		public void addToList(String typ, String p, SourceAsset a) {
			SourceAsset pkg = new SourceAsset(p, a.getLineNo(), a.getStart());
			HashMap h = addPackage(p, pkg);
			ArrayList list = (ArrayList) h.get(typ);
			if (list != null)
			list.add(a);
			else Log.log(Log.DEBUG, SourceParser.class,
			"Entry " + typ + " not in PackageMap"
			);
			}
		}
}
