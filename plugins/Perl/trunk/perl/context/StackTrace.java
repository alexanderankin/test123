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

package perl.context;

import perl.core.Debugger;
import perl.core.GdbState;
import perl.core.GdbView;
import perl.core.Parser.GdbHandler;
import perl.core.Parser.GdbResult;
import perl.core.Parser.ResultHandler;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.MiscUtilities;

@SuppressWarnings("serial")
public class StackTrace extends GdbView {
	static private TreeModel emptyTreeModel = new DefaultTreeModel(null);
	private JTree tree;
	private DefaultMutableTreeNode root;

	private static Vector<FrameChangeListener> listeners =
		new Vector<FrameChangeListener>();
	
	public interface FrameChangeListener {
		void frameChanged(int newFrame);
	}
	
	public StackTrace() {
		super();
		setLayout(new BorderLayout());
		tree = new JTree();
		root = new DefaultMutableTreeNode("Stack trace:");
		tree.setModel(emptyTreeModel);
		tree.setRootVisible(false);
		tree.addMouseListener(new StackTraceListener());
		add(new JScrollPane(tree));
		if (GdbState.isStopped())
			update();
	}

	public static void addFrameChangeListener(FrameChangeListener l) {
		listeners.add(l);
	}
	public static void removeFrameChangeListener(FrameChangeListener l) {
		listeners.remove(l);
	}

	public void update() {
		root.removeAllChildren();
		getCommandManager().add("-stack-list-frames", new StackTraceResultHandler());
		getCommandManager().add("-stack-list-arguments 1", new StackArgumentsResultHandler());
	}
	public void sessionEnded() {
		tree.setModel(emptyTreeModel);
	}

	private class StackArgumentsResultHandler implements ResultHandler {
		@SuppressWarnings("unchecked")
		public void handle(String msg, GdbResult res) {
			if (msg.equals("done")) {
				Object stack = res.getValue("stack-args");
				if (stack == null)
					return;
				if (stack instanceof Vector) {
					Vector<Object> frames = (Vector<Object>)stack;
					for (int i = 0; i < frames.size(); i++) {
						Object frame = frames.get(i);
						if (frame instanceof Hashtable) {
							Hashtable<String, Object> frameHash =
								(Hashtable<String, Object>)
								((Hashtable<String, Object>)frame).get("frame");
							Object frameArgs = frameHash.get("args");
							Vector<String> names = new Vector<String>();
							Vector<String> values = new Vector<String>();
							if (frameArgs instanceof Vector) {
								Vector<Object> frameArgsVec =
									(Vector<Object>)frameArgs;
								for (int j = 0; j < frameArgsVec.size(); j++) {
									Hashtable<String, Object> argsHash =
										(Hashtable<String, Object>)frameArgsVec.get(j);
									String name = argsHash.get("name").toString();
									names.add(name);
									String value = argsHash.get("value").toString();
									values.add(value);
								}
								DefaultMutableTreeNode node =
									(DefaultMutableTreeNode) root.getChildAt(i);
								StackTraceNode frameNode = (StackTraceNode) node.getUserObject();
								frameNode.setArguments(names, values);
							}
						}
					}
				}
			}
			updateTree();
			tree.setSelectionRow(0);
		}
	}
	
	private class StackTraceResultHandler implements ResultHandler {
		@SuppressWarnings("unchecked")
		public void handle(String msg, GdbResult res) {
			//System.err.println("StackTraceResultHandler called with " + msg);
			if (msg.equals("done")) {
				Object stack = res.getValue("stack");
				if (stack == null)
					return;
				if (stack instanceof Vector) {
					Vector<Object> frames = (Vector<Object>)stack;
					for (int i = 0; i < frames.size(); i++) {
						Object frame = frames.get(i);
						if (frame instanceof Hashtable) {
							Hashtable<String, Object> frameHash =
								(Hashtable<String, Object>)
								((Hashtable<String, Object>)frame).get("frame");
							String level = frameHash.get("level").toString();
							String func = frameHash.get("func").toString();
							String file = frameHash.containsKey("file") ?
								frameHash.get("file").toString() : null;
							String line = frameHash.containsKey("line") ?
								frameHash.get("line").toString() : null;
							String from = frameHash.containsKey("from") ?
								frameHash.get("from").toString() : null;
							StackTraceNode frameNode = new StackTraceNode(
									level,
									func,
									file,
									line,
									from);
							root.add(new DefaultMutableTreeNode(frameNode));
						}
					}
				}
			}
			updateTree();
		}
	}
	
	public class StackTraceNode {
		String file;
		String base = null;
		int line = 0;
		int level = 0;
		String func;
		String from;
		Vector<String> args = null;
		Vector<String> values = null;
		StackTraceNode(String level, String func, String file, String line, String from)
		{
			if (level != null)
				this.level = Integer.parseInt(level);
			this.func = func;
			this.file = file;
			if (line != null)
				this.line = Integer.parseInt(line);
			this.from = from;
		}
		public void setArguments(Vector<String> arguments, Vector<String>argValues) {
			args = arguments;
			values = argValues;
		}
		public String toString() {
			String location;
			if (file != null)
				location = "at " + file + ":" + line;
			else
				location = "from " + from;
			StringBuffer arguments = new StringBuffer();
			if (args != null) {
				arguments.append("(");
				for (int i = 0; i < args.size(); i++) {
					if (i > 0)
						arguments.append(", ");
					arguments.append(args.get(i));
				}
				arguments.append(")");
			}
			return level + " " + func + arguments + " " + location;
		}
		public void selected() {
			getCommandManager().add("frame " + level);
			if (base == null) {
				Debugger.getInstance().getParser().addGdbHandler(new InfoSourceHandler(this));
				getCommandManager().add("info source");
			}
			else
				jump();
			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).frameChanged(level);
		}
		public int getLevel() {
			return level;
		}
		public void setBase(String path) {
			base = path;
		}
		public void jump() {
			if (file == null)
				return;
			String path = null;
			if ((base == null) || MiscUtilities.isAbsolutePath(file))
				path = file;
			else
				path = base + "/" + file;
			Debugger.getInstance().getFrontEnd().goTo(path, line);
		}
		public Vector<String> getArguments() {
			return args;
		}
		public Vector<String> getValues() {
			return values;
		}
	}

	private static class InfoSourceHandler implements GdbHandler {
		StackTraceNode frame;
		public InfoSourceHandler(StackTraceNode frame) {
			this.frame = frame; 
		}
		public void handle(String line) {
			final String prefix = "Compilation directory is ";
			int i = line.indexOf(prefix);
			if (i >= 0) {
				String path = line.substring(i + prefix.length()).trim();
				frame.setBase(path);
				frame.jump();
				Debugger.getInstance().getParser().removeGdbHandler(this);
			}
		}
	}
	
	private class StackTraceListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			TreePath path = tree.getSelectionPath();
			Object obj = ((DefaultMutableTreeNode) path
					.getLastPathComponent()).getUserObject();
			if (obj instanceof StackTraceNode) {
				StackTraceNode node = (StackTraceNode)obj;
				node.selected();
			}
		}
	}

	public void updateTree() {
		tree.setModel(new DefaultTreeModel(root));
	}

}
