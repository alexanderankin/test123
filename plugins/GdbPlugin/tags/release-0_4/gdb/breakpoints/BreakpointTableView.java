package gdb.breakpoints;

import gdb.breakpoints.BreakpointList.BreakpointListListener;
import gdb.core.CommandManager;
import gdb.core.Debugger;
import gdb.core.GdbState;
import gdb.core.GdbView;
import gdb.core.Parser.GdbResult;
import gdb.core.Parser.ResultHandler;

import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class BreakpointTableView extends GdbView {

	@Override
	public void running() {
		createModel();
	}

	@Override
	public void sessionEnded() {
		// TODO Auto-generated method stub
		super.sessionEnded();
	}

	@Override
	public void update() {
		model.update();
	}

	public static class BreakpointTableModel extends AbstractTableModel {

		Vector<String> columns = new Vector<String>();
		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		
		public BreakpointTableModel() {
			update();
		}

		private void update() {
			columns.clear();
			rows.clear();
			CommandManager cmdMgr = Debugger.getInstance().getCommandManager();
			if (cmdMgr == null)
				return;
			cmdMgr.add("-break-list", new ResultHandler() {
				@SuppressWarnings("unchecked")
				public void handle(String msg, GdbResult res) {
					if ((! msg.equals("done")) || res == null)
						return;
					// Reading the headers only for the order
					Object hdr = res.getValue("BreakpointTable/hdr");
					if (hdr != null && hdr instanceof Vector) {
						Vector<Object> cols = (Vector<Object>)hdr;
						for (int i = 0; i < cols.size(); i++) {
							Object col = cols.get(i);
							if (col instanceof Hashtable) {
								Hashtable<String, Object> colHash =
									(Hashtable<String, Object>)col;
								String colname = (String)colHash.get("col_name");
								columns.add(colname);
							}
						}
					}
					Object body = res.getValue("BreakpointTable/body");
					if (body != null && body instanceof Vector) {
						Vector<Object> breakpoints = (Vector<Object>)body;
						for (int i = 0; i < breakpoints.size(); i++) {
							Object brk = breakpoints.get(i);
							if (brk instanceof Hashtable) {
								Hashtable<String, Object> brkHash =
									(Hashtable<String, Object>)brk;
								Object bkpt = brkHash.get("bkpt");
								if (bkpt instanceof Hashtable) {
									Hashtable<String, Object> bkptHash =
										(Hashtable<String, Object>)(bkpt);
									Vector<String> row = new Vector<String>();
									for (int j = 0; j < columns.size(); j++)
										row.add("");
									rows.add(row);
									Enumeration<String> k = bkptHash.keys();
									while (k.hasMoreElements()) {
										String key = k.nextElement();
										String val = (String) bkptHash.get(key);
										int colIndex = columns.indexOf(key);
										if (colIndex < 0) {
											columns.add(key);
											row.add(val);
										} else
											row.set(colIndex, val);
									}
								}
							}
						}
					}
					for (int i = 0; i < columns.size(); i++) {
						String name = columns.get(i);
						name = name.substring(0, 1).toUpperCase() +
							name.substring(1);
						columns.set(i, name);
					}
					synchronized(columns) {
						columns.notify();
					}
					BreakpointTableModel.this.fireTableStructureChanged();
					/*
^done,BreakpointTable={nr_rows="2",nr_cols="6",
hdr=[{width="3",alignment="-1",col_name="number",colhdr="Num"},
{width="14",alignment="-1",col_name="type",colhdr="Type"},
{width="4",alignment="-1",col_name="disp",colhdr="Disp"},
{width="3",alignment="-1",col_name="enabled",colhdr="Enb"},
{width="10",alignment="-1",col_name="addr",colhdr="Address"},
{width="40",alignment="2",col_name="what",colhdr="What"}],
body=[bkpt={number="1",type="breakpoint",disp="keep",enabled="y",
addr="0x000100d0",func="main",file="hello.c",line="5",times="0"},
bkpt={number="2",type="breakpoint",disp="keep",enabled="y",
addr="0x00010114",func="foo",file="hello.c",fullname="/home/foo/hello.c",
line="13",times="0"}]}
					 */
				}
			});
		}

		public Class<?> getColumnClass(int arg0) {
			return String.class;
		}

		public int getColumnCount() {
			synchronized(columns) {
				if (columns.size() == 0) {
					try {
						columns.wait(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return columns.size();
		}

		public String getColumnName(int i) {
			return columns.get(i);
		}

		public int getRowCount() {
			if (getColumnCount() == 0)
				return 0;
			return rows.size();
		}

		public Object getValueAt(int row, int col) {
			if (row >= rows.size())
				return null;
			Vector<String> r = rows.get(row);
			if (col >= r.size())
				return null;
			return r.get(col);
		}

		public boolean isCellEditable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		public void setValueAt(Object arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

	}

	private BreakpointTableModel model = null;
	private JTable table;
	
	public BreakpointTableView() {
		setLayout(new BorderLayout());
		table = new JTable();
		table.setAutoCreateRowSorter(true);
		add(new JScrollPane(table), BorderLayout.CENTER);
		if (GdbState.isRunning())
			createModel();
		BreakpointList.getInstance().addListListener(new BreakpointListListener() {
			private void update() {
				if (model != null)
					model.update();
			}
			public void breakpointAdded(Breakpoint bp) {
				update();
			}
			public void breakpointChanged(Breakpoint bp) {
				update();
			}
			public void breakpointRemoved(Breakpoint bp) {
				update();
			}
		});
	}

	private void createModel() {
		if (model != null)
			return;
		model = new BreakpointTableModel();
		table.setModel(model);
	}
}
