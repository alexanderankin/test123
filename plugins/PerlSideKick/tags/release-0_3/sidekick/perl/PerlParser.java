/*
 * PerlParser.java
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
package sidekick.perl;

import javax.swing.tree.*;
import javax.swing.text.Position;
import javax.swing.ImageIcon;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import errorlist.*;
import sidekick.*;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

/**
 * PerlParser: parses perl source and builds a sidekick structure tree
 * PerParser is based on regular expressions and will therefore 
 * not able to correctly parse very irregular perl scripts 
 *
 * @author     Martin Raspe
 * @created    March 3, 2005
 * @modified   $Date$ by $Author$
 * @version    $Revision$
 */
public class PerlParser extends SideKickParser {
	//{{{ Icons
	public static final ImageIcon SUB_ICON = new ImageIcon(
		PerlSideKickPlugin.class.getResource("/icons/perlsub.gif"));
	public static final ImageIcon PACKAGE_ICON = new ImageIcon(
		PerlSideKickPlugin.class.getResource("/icons/perlpackage.gif"));
	public static final ImageIcon USE_ICON = new ImageIcon(
		PerlSideKickPlugin.class.getResource("/icons/perluse.gif"));
	public static final ImageIcon SUB_INVALID_ICON = new ImageIcon(
		PerlSideKickPlugin.class.getResource("/icons/perlsub_invalid.gif"));
	public static final ImageIcon PACKAGE_INVALID_ICON = new ImageIcon(
		PerlSideKickPlugin.class.getResource("/icons/perlpackage_invalid.gif"));
	public static final ImageIcon USE_INVALID_ICON = new ImageIcon(
		PerlSideKickPlugin.class.getResource("/icons/perluse_invalid.gif"));
	public static final ImageIcon COMMENT_ICON = new ImageIcon(
		PerlSideKickPlugin.class.getResource("/icons/perl_comment.gif"));
	//}}}

	//{{{ private vars
	public static final char LINE_COMMENT = '#';
	public static final char ESCAPE_CHAR = '\\';

	private RE re;

	private PerlParsedData data;
	private PackageMap packages;
	private ArrayList podList;
	private PerlAsset _asset;
	private PerlAsset _pkgAsset;

	private Position _start;
	private Position _end;
	//}}}

/**	 * Constructs a new PerlParser object
	 *
	 * @param name See sidekick.SidekickParser.
	 */
	public PerlParser(String name) {
		super(name);
	}

/**	 * Parses the given text and returns a tree model.
	 *
	 * @param buffer The buffer to parse.
	 * @param errorSource An error source to add errors to.
	 *
	 * @return A new instance of the <code>SideKickParsedData</code> class.
	 */
	public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource) {
		data = new PerlParsedData(buffer.getName());
		packages = new PackageMap(new PackageComparator());
		podList = new ArrayList();
		String _line;
		String _name;
		String[] _names;
		String _package = "main";
		boolean _inComment = false;
		int buflen = buffer.getLength();
		int _lineNo;
		int _tmp;
		for (_lineNo = 0; _lineNo < buffer.getLineCount(); _lineNo++) {
			_start = buffer.createPosition(buffer.getLineStartOffset(_lineNo));
			_tmp = buffer.getLineEndOffset(_lineNo);
			if (_tmp > buflen) _tmp = buflen;
			_end = buffer.createPosition(_tmp);
			_line = buffer.getLineText(_lineNo).trim();
			// line comment or empty line
			if (_line.indexOf(LINE_COMMENT) == 0 || _line.length() == 0) continue;
			// POD: end
			if (_inComment && _line.startsWith("=cut")) { 
				_inComment = false;
				completeAsset(_end);
				continue;
				}
			// POD: headline 1
			_name = rematch(_line, "^=head1\\s+(.*)", 1);
			if (_name != null) {
				_inComment = true;
				addPodAsset(_name, _lineNo, _start);
				continue;
				}
			if (_inComment) continue;
			// POD: any other directive
			_name = rematch(_line, "^=(\\w+)", 1); 
			if (_name != null) {
				_inComment = true;
				continue;
				}
			// sub (fully qualified with package)
			_names = submatch(_line, "^sub\\s+([\\w:]+)::(\\w+)");
			if (_names != null) {
				addAsset("_sub", _names[0], _names[1], _lineNo, _start);
				continue;
				}
			// sub (simple)
			_name = rematch(_line, "^sub\\s+(\\w+)", 1);
			if (_name != null) {
				addAsset("_sub", _package, _name, _lineNo, _start);
				continue;
				}
			// use/require
			_name = rematch(_line, "^(use|require)\\s+([\\w:.]+)", 2);
			if (_name != null) {
				addLineAsset("_use", _package, _name, _lineNo, _start, _end);
				continue;
				}
			// explicit package
			_name = rematch(_line, "^package\\s+([\\w:]+)", 1);
			if (_name != null) {
				// remember the new package name
				_package = _name;
				addPackageAsset(_name, _lineNo, _start);
				continue;
				}
			}
		completeAsset(_end);
		Log.log(Log.DEBUG, this, "parsing completed");
		buildTrees();
		Log.log(Log.DEBUG, this, "tree built");
		return data;
	}

	// Sets the end position of the most recently created asset.
	private void completeAsset(Position end) {
		if (_asset != null) _asset.setEnd(end);
		_asset = null;
		if (_pkgAsset != null) _pkgAsset.setEnd(end);
		_pkgAsset = null;
		}

	// Adds a multi-line asset to the list of its package
	private void addAsset(String typ, String p, String name, int lineNo, Position start) {
		completeAsset(start);
		_asset = new PerlAsset(name, lineNo, start);
		packages.addToList(typ, p, _asset);
		}

	// Adds a one-line asset (don't complete the current _asset yet)
	private void addLineAsset(String typ, String p, String name, int lineNo, Position start, Position end) {
		PerlAsset a = new PerlAsset(name, lineNo, start);
		a.setEnd(end);
		packages.addToList(typ, p, a);
		}

	//Adds an asset to the pod list
	private void addPodAsset(String name, int lineNo, Position start) {
		completeAsset(start);
		_asset = new PerlAsset(name, lineNo, start);
		podList.add(_asset);
		}

	// Adds a new package to the "packages" TreeMap.
	private void addPackageAsset(String name, int lineNo, Position start) {
		completeAsset(start);
		packages.addPackage(name, new PerlAsset(name, lineNo, start));
		}

	// Builds the sidekick tree from the "packages" TreeMap
	private void buildTrees() {
		Set list = packages.keySet();
		Iterator i = list.iterator();
		while(i.hasNext()) {
			String name = (String) i.next();
			HashMap h = (HashMap) packages.get(name);
			PerlAsset a = (PerlAsset) h.get("_asset");
			a.setIcon(PACKAGE_ICON);
			DefaultMutableTreeNode t = new DefaultMutableTreeNode(a); 
			newTree(t, "use", (ArrayList) h.get("_use"), USE_ICON); 
			addList(t, (ArrayList) h.get("_sub"), SUB_ICON); 
			data.root.add(t);
			}
		newTree(data.root, "Pod", podList, COMMENT_ICON);
		}

	// Builds a tree from asset list "list" and adds it to tree node "n".
	private void newTree(DefaultMutableTreeNode n, String name, ArrayList list, ImageIcon icon) {
		if (list.size() == 0) return; 
		PerlAsset first	= (PerlAsset) list.get(0);
		PerlAsset branch = new PerlAsset(name, first.getLineNo(), first.start);
		branch.setIcon(icon);
		DefaultMutableTreeNode t = new DefaultMutableTreeNode(branch); 
		addList(t, list, null);
		n.add(t);
		}

	//Adds an Asset list "list" to a tree node "n"
	private void addList(DefaultMutableTreeNode t, ArrayList list, ImageIcon icon) {
		Collections.sort(list, new PerlAssetComparator());
		Iterator i = list.iterator();
		while(i.hasNext()) {
			PerlAsset a = (PerlAsset) i.next();
			a.setIcon(icon);
			t.add(new DefaultMutableTreeNode(a));
			}
		}
	
	// apply a regular expression to a line
	private REMatch regex (String line, String expr) {
		try { re = new RE(expr);
			}
		catch (REException e) {
			Log.log(Log.DEBUG, this, e.getMessage());
			};
		return re.getMatch(line);
		}

	// match a line and return group if found
	private String rematch(String line, String expr, int group) {
		String _result;
		REMatch match = regex(line, expr);
		if (match != null) _result = match.toString(group);
		else _result = null;
		return _result;
		}
		
	// match a line and return package and subname if found
	private String[] submatch(String line, String expr) {
		String[] _results = new String[2];
		REMatch match = regex(line, expr);
		if (match != null) {
			_results[0] = match.toString(1);
			_results[1] = match.toString(2);
			}
		else _results = null;	
		return _results;
		}

	// compare strings, ignore case
	private class PerlAssetComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			String s1 = ((PerlAsset) o1).toString();
			String s2 = ((PerlAsset) o2).toString();
			return s1.toLowerCase().compareTo(s2.toLowerCase());
			}
		}

	// sort package names
	private class PackageComparator implements Comparator{
		public int compare(Object o1, Object o2) {
			String s1 = (String)o1;
			String s2 = (String)o2;
			int comp = s1.toLowerCase().compareTo(s2.toLowerCase());
			if (comp == 0) return comp;
			// pull up module name "main"
			if ("main".equals(s1)) return -1;
			if ("main".equals(s2)) return 1;
			return comp;
			}
		}

	// reflects perl script structure
	private class PackageMap extends TreeMap {
		public PackageMap() {
			super();
			}

		public PackageMap(PackageComparator pc) {
			super(pc);
			}

		// add a HashMap for a new package name
		public HashMap addPackage(String p, PerlAsset a) {
			if (this.containsKey(p)) return (HashMap) this.get(p);
			HashMap h = new HashMap();
			h.put("_asset", a);
			h.put("_use", new ArrayList());
			h.put("_sub", new ArrayList());
			this.put(p, h);
			return h;
			}

		// add an Asset of type "typ" to the TreeMap for package "p"
		public void addToList(String typ, String p, PerlAsset a) {
			_pkgAsset = new PerlAsset(p, a.getLineNo(), a.start);
			HashMap h = addPackage(p, _pkgAsset);
			ArrayList list = (ArrayList) h.get(typ);
			list.add(a);
			}
		}
}
