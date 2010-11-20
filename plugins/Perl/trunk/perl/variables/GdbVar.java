/*
Copyright (C) 2007  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package perl.variables;

import perl.core.CommandManager;
import perl.core.Debugger;
import perl.core.Parser.GdbHandler;
import perl.core.Parser.GdbResult;
import perl.core.Parser.ResultHandler;
import perl.options.GeneralOptionPane;

import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class GdbVar extends DefaultMutableTreeNode {
	static protected final TypeMacroMap tmm = TypeMacroMap.getInstance();

	protected String name;
	private String value = null;
	private String type = null;
	private String inferredType = null;
	private String gdbName = null;
	private int numChildren = 0;
	protected UpdateListener listener = null;
	private boolean created = false;
	private boolean requested = false;
	private boolean leaf = true;
	private static Pattern arrayPattern = Pattern.compile(".*\\s\\[\\d+\\]");
	private static Pattern charArrayPattern = Pattern.compile("char\\s\\[\\d+\\]");
	private boolean splitArray = false;
	
	private static Vector<ChangeListener> listeners =
		new Vector<ChangeListener>();
	
	public interface ChangeListener {
		void changed(GdbVar v);
	}
	public interface UpdateListener {
		void updated(GdbVar v);
	}
	
	public GdbVar(String name) {
		this.name = name;
		createGdbVar();
	}
	
	public GdbVar(String name, String gdbName, boolean leaf) {
		this.name = name;
		this.gdbName = gdbName;
		this.leaf = leaf;
		getValue();
	}
	public static void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}
	public static void removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}

	private CommandManager getCommandManager() {
		return Debugger.getInstance().getCommandManager();
	}
	public void done() {
		if (getCommandManager() == null)
			return;
		getCommandManager().add("-var-delete " + gdbName);
		if (splitArray) {
			Enumeration c = children();
			while (c.hasMoreElements()) {
				Object next = c.nextElement(); 
				if (next instanceof ArrayRangeVar)
					((ArrayRangeVar)next).done();
			}
		}
	}
	
	public void setChangeListener(UpdateListener l) {
		listener = l;
	}
	public void unsetChangeListener() {
		listener = null;
	}
	protected void notifyListener() {
		if (listener != null)
			listener.updated(this);
	}
	protected String getDisplayName() {
		return name;
	}
	public String toString() {
		String displayName = getDisplayName();
		if (value == null || value.equals(""))
			return displayName;
		return displayName + " = " + value;
	}
	private void getValue() {
		if (getCommandManager() == null)
			return;
		if (inferredType != null && tmm.containsKey(inferredType)) {
			getValueByMacro(tmm.get(inferredType));
			return;
		}
		getCommandManager().add("-var-evaluate-expression " + gdbName,
				new ResultHandler() {
				public void handle(String msg, GdbResult res) {
					if (! msg.equals("done"))
						return;
					value = res.getStringValue("value");
					if (value == null)
						value = "";
					notifyListener();
				}
		});
	}
	private void getValueByMacro(String macro) {
		Debugger.getInstance().getParser().addGdbHandler(new GdbHandler() {
			public void handle(String line) {
				value = line;
				if (value == null)
					value = "";
				notifyListener();
				Debugger.getInstance().getParser().removeGdbHandler(this);		
			}
		});
		getCommandManager().add(macro + " " + name);
	}

	private void createChildren() {
		created = true;
		doCreateChildren();
	}

	protected void doCreateChildren() {
		if (gdbName == null) {
			requested = true;
			return;
		}
		// Check for large arrays
		int arrayRangeSplitSize = jEdit.getIntegerProperty(
				GeneralOptionPane.ARRAY_RANGE_SPLIT_SIZE_PROP, 100);
		if ((arrayRangeSplitSize > 0) && (numChildren > arrayRangeSplitSize)) {
			Matcher m = arrayPattern.matcher(type);
			if (m.find()) {
				splitArray = true;
				createArrayRangeChildren(arrayRangeSplitSize);
				notifyListener();
				return;
			}
		}
		getCommandManager().add("-var-list-children " + gdbName,
			new ResultHandler() {
				public void handle(String msg, GdbResult res) {
					if (! msg.equals("done"))
						return;
					int nc = Integer.parseInt(res.getStringValue("numchild"));
					for (int i = 0; i < nc; i++) {
						String base = "children/" + i + "/child/";
						String cname = res.getStringValue(base + "name");
						String cexp = res.getStringValue(base + "exp");
						String cnc = res.getStringValue(base + "numchild");
						GdbVar child = new GdbVar(cexp, cname, Integer.parseInt(cnc) == 0);
						child.setChangeListener(listener);
						add(child);
					}
					notifyListener();
				}
			}
		);
	}
	private void createArrayRangeChildren(int arrayRangeSplitSize) {
		int from = 0;
		do {
			int to = from + arrayRangeSplitSize - 1;
			if (to > numChildren)
				to = numChildren;
			String name = "[" + String.valueOf(from) + ".." +
				String.valueOf(to) + "]";
			ArrayRangeVar child = new ArrayRangeVar(name, this, from, to);
			child.setChangeListener(listener);
			add(child);
			from = to + 1; 
		} while (from <= numChildren);
	}
	public void reset() {
		gdbName = null;
		value = "";
		numChildren = 0;
		removeAllChildren();
		notifyListener();
	}
	public void update() {
		if (gdbName == null)
			createGdbVar();
		else {
			if (getCommandManager() == null)
				return;
			getCommandManager().add("-var-update " + gdbName);
			getValue();
			updateChildren();
		}
	}
	@SuppressWarnings("unchecked")
	protected void updateChildren() {
		Enumeration<GdbVar> c = this.children();
		while (c.hasMoreElements()) {
			GdbVar child = c.nextElement();
			child.update();
		}
	}
	protected void createGdbVar() {
		if (getCommandManager() == null)
			return;
		getCommandManager().add("-var-create - * \"" + name + "\"",
			new ResultHandler() {
				public void handle(String msg, GdbResult res) {
					if (! msg.equals("done"))
						return;
					gdbName = res.getStringValue("name");
					type = res.getStringValue("type");
					inferredType = TypeMacroMap.getInstance().getInferredType(type);
					String nc = res.getStringValue("numchild");
					// Display char arrays as strings if requested
					if (jEdit.getBooleanProperty(GeneralOptionPane.CHAR_ARRAY_AS_STRING_PROP)) {
						Matcher m = charArrayPattern.matcher(type);
						if (m.find()) {
							numChildren = 0;
							leaf = true;
							getCharArrayValue();
							return;
						}
					}
					numChildren = Integer.parseInt(nc); 
					leaf = (numChildren == 0);
					if (requested)
						createChildren();
					getValue();
				}
			}
		);
	}

	protected void getCharArrayValue() {
		if (getCommandManager() == null)
			return;
		getCommandManager().add("-data-evaluate-expression " + name,
				new ResultHandler() {
				public void handle(String msg, GdbResult res) {
					if (! msg.equals("done"))
						return;
					value = res.getStringValue("value");
					if (value == null)
						value = "";
					notifyListener();
				}
		});
	}

	@Override
	public int getChildCount() {
		if (! created) {
			createChildren();
			return 0;
		}
		return super.getChildCount();
	}

	@Override
	public boolean isLeaf() {
		if (! created)
			return leaf ;
		return super.isLeaf();
	}

	public void contextRequested() {
		if (getCommandManager() == null)
			return;
		if (! isLeaf())
			return;
		String newValue =
			JOptionPane.showInputDialog("New value for '" + name + "':", value);
		getCommandManager().add(
				"-var-assign " + gdbName + " " + newValue);
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).changed(GdbVar.this);
		getValue();	// Update the value after the change
	}
}
