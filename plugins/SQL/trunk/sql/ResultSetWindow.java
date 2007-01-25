/**
 * ResultSetWindow.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 *
 * Copyright (C) 2001 Sergey V. Udaltsov
 * svu@users.sourceforge.net
 *
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

package sql;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.Element;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.*;

import common.gui.*;

import sql.*;

/**
 *  Description of the Class
 *
 * @author     svu
 */
public class ResultSetWindow extends JPanel
{

	protected View view;
	protected JLabel status;
	protected JTabbedPane notebook;

	protected int sortOrder = HelpfulJTable.SORT_OFF;
	protected int sortColumn = -1;

	protected final static String MAX_RECS_TO_SHOW_PROP = "sql.maxRecordsToShow";
	protected final static String AUTORESIZE = "sql.autoresizeResult";
	protected final static String CLOSE_WITH_BUFFER = "sql.closeWithBuffer";

	public final static String RESULT_SETS_BUF_PROPERTY = "sql.resultSets";


	/**
	 *  Constructor for the ResultSetWindow object
	 *
	 * @param  view  Description of Parameter
	 * @since
	 */
	public ResultSetWindow(View view)
	{
		this.view = view;

		setLayout(new BorderLayout());
//    final JPanel p = new JPanel( new BorderLayout() );

		notebook = new JTabbedPane();
		add(BorderLayout.CENTER, notebook);

		status = new JLabel();
		final JPanel p = new JPanel(new BorderLayout());
		p.add(BorderLayout.WEST, status);
		final JButton closeAll = new JButton(jEdit.getProperty("sql.resultSet.closeAll"));
		p.add(BorderLayout.EAST, closeAll);
		add(BorderLayout.SOUTH, p);
		closeAll.addActionListener(
		        new ActionListener()
		        {
			        public void actionPerformed(ActionEvent evt)
			        {
				        while (notebook.getComponentCount() != 0)
					        notebook.remove(notebook.getComponent(0));
			        }
		        });

		status.setText(jEdit.getProperty("sql.resultSet.status",
		                                 new Object[]{new Integer(SqlUtils.getThreadGroup().getNumberOfRequest())}));

		SqlUtils.getThreadGroup().addListener(
		        new SqlThreadGroup.Listener()
		        {
			        public void groupChanged(final int numberOfActiveThreads)
			        {
				        SwingUtilities.invokeLater(
				                new Runnable()
				                {
					                public void run()
					                {
						                final Object[] args = {new Integer(numberOfActiveThreads)};
						                status.setText(jEdit.getProperty("sql.resultSet.status", args));
					                }
				                });
			        }
		        });
	}


	/**
	 *  Gets the Name attribute of the ResultSetWindow object
	 *
	 * @return    The Name value
	 * @since
	 */
	public String getName()
	{
		return SqlPlugin.resultSetWinName;
	}


	/**
	 *  Gets the Component attribute of the ResultSetWindow object
	 *
	 * @return    The Component value
	 * @since
	 */
	public Component getComponent()
	{
		return this;
	}

	protected Pattern[] patterns = null;

	protected Pattern[] getPatterns()
	{
		if (patterns == null)
		{
			final String[] keywords = new String[]
			                          { "from", "select", "where", "and", "or", "order by", "group by", "like", "distinct", "like", "in", "as" };
			patterns = new Pattern[keywords.length];
			for (int i = patterns.length; --i >= 0;)
				patterns[i] = Pattern.compile("\\b(" + keywords[i] + ")\\b", Pattern.CASE_INSENSITIVE);
		}
		return patterns;
	}


	public String formatQuery(String query)
	{
		final String lq = query.length() > 128 ? query.substring(0, 127) : query;

		String lqf = lq.replaceAll("\n", "<p>");
		for (int i = patterns.length; --i >= 0;)
			lqf = patterns[i].matcher(lqf).replaceAll("<b>$1</b>");

System.out.println("formatted:[" + lqf + "]");

		return "<html>" + lqf + "</html>";
	}


	public void setRenderers(JTable table, final SqlServerType type)
	{
		table.setDefaultRenderer(Object.class, new Renderer(type));
	}

	/**
	 *  Description of the Method
	 *
	 * @param  data  The feature to be added to the DataSet attribute
	 * @since
	 */
	public void addDataSet(SqlServerRecord sqlServer, String query, Data data)
	{
		final Pattern[] patterns = getPatterns();
		final String formattedQuery = formatQuery(query);

		final JPanel p = new JPanel(new BorderLayout());

		final JPanel p1 = new JPanel(new BorderLayout());

		final JLabel serverLbl = new JLabel(sqlServer.getName(), SwingConstants.LEFT);
		serverLbl.setToolTipText(sqlServer.getServerType().getName());

		final JButton closeBtn = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/closebox.gif"))));
		closeBtn.addActionListener(
		        new ActionListener()
		        {
			        public void actionPerformed(ActionEvent evt)
			        {
				        notebook.remove(p);
			        }
		        });

		p1.add(BorderLayout.EAST, closeBtn);
		p1.add(BorderLayout.WEST, serverLbl);
		p.add(BorderLayout.NORTH, p1);

		final JComponent dataView = createDataView(data);
		p.add(BorderLayout.CENTER, dataView);

		final int recCount = data.recCount;
		final Object[] args = {new Integer(recCount)};
		final int maxRecs = getMaxRecordsToShow();
		if (recCount > maxRecs)
			args[0] = new String(" > " + maxRecs);

		final JLabel infoLbl = new JLabel(jEdit.getProperty("sql.resultSet.info", args), SwingConstants.LEFT);
		infoLbl.setToolTipText(formattedQuery);
		p.add(BorderLayout.SOUTH, infoLbl);

		final ImageIcon ii = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/ResultSetWindowTab.png")));
		notebook.addTab("", ii, p, formattedQuery);

		final JTable tbl = (JTable)((JScrollPane)dataView).getViewport().getView();

		setRenderers(tbl, sqlServer.getServerType());

		p.addMouseListener(new MouseHandler(p, tbl));
		notebook.setSelectedComponent(p);

		revalidate();

		final Buffer activeBuf = jEdit.getActiveView().getBuffer();
		if (activeBuf != null)
		{
			java.util.List resultSets = (java.util.List)activeBuf.getProperty(RESULT_SETS_BUF_PROPERTY);
			if (resultSets == null)
			{
				resultSets = new ArrayList();
				activeBuf.setProperty(RESULT_SETS_BUF_PROPERTY, resultSets);
			}
			resultSets.add(p);
		}
		notebook.addContainerListener(new ContainerAdapter()
		                              {
			                              public void componentRemoved(ContainerEvent evt)
			                              {
				                              final Component child = evt.getChild();
				                              final java.util.List resultSets = (java.util.List)activeBuf.getProperty(RESULT_SETS_BUF_PROPERTY);
				                              if (resultSets != null && child == p)
					                              try {
						                              Log.log(Log.DEBUG, ResultSetWindow.class,
						                                      "Removing page " + child + " from the list of resultSets");
						                              resultSets.remove(p);
					                              } catch (Exception ex)
					                              { /* anything can happen but it does not matter here */ }
			                              }
		                              });
	}


	/**
	 *  Sets the SortOrder attribute of the ResultSetWindow object
	 *
	 * @param  order  The new SortOrder value
	 * @param  table  The new SortOrder value
	 * @param  data   The new SortOrder value
	 */
	protected void setSortOrder(JTable table, Data data, int order)
	{
		sortOrder = order;
		resort(table, data);
	}


	/**
	 *  Sets the SortColumn attribute of the ResultSetWindow object
	 *
	 * @param  column  The new SortColumn value
	 * @param  table   The new SortColumn value
	 * @param  data    The new SortColumn value
	 */
	protected void setSortColumn(JTable table, Data data, int column)
	{
		sortColumn = column;
		resort(table, data);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  table  Description of Parameter
	 * @param  data   Description of Parameter
	 */
	protected void resort(JTable table, Data data)
	{
		if (data == null || data.rowData == null || data.columnNames == null)
			return;

		if (sortOrder == HelpfulJTable.SORT_OFF)
		{
			table.setModel(data);
			return;
		}

		if (sortColumn < 0 || sortColumn >= data.columnNames.length)
			return;

		if (!(sortOrder == HelpfulJTable.SORT_ASCENDING ||
		      sortOrder == HelpfulJTable.SORT_DESCENDING))
			return;

		final boolean isAscending = sortOrder == HelpfulJTable.SORT_ASCENDING;

		Arrays.sort(data.rowData, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				if (!(o1 instanceof Object[] &&
				      o2 instanceof Object[]))
					throw new ClassCastException();

				final Object [] row1 = (Object[]) o1;
				final Object [] row2 = (Object[]) o2;
				final Object do1 = row1[sortColumn];
				final Object do2 = row2[sortColumn];
				final int rv = (do1 instanceof Comparable) ? 
				               ((Comparable)do1).compareTo(do2) : 
				               ("" + do1).compareTo("" + do2);
				return isAscending ? rv : -rv;
			}


			public boolean equals(Object o)
			{
				return false;
			}
		});

		table.setModel(data);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  data  Description of Parameter
	 * @return       Description of the Returned Value
	 * @since
	 */
	protected JComponent createDataView(final Data data)
	{
		final HelpfulJTable tbl = new HelpfulJTable();
		tbl.addPropertyChangeListener("sortOrder",
		                              new PropertyChangeListener()
		                              {
			                              public void propertyChange(PropertyChangeEvent evt)
			                              {
				                              setSortOrder(tbl, data, ((Number) evt.getNewValue()).intValue());
			                              }
		                              });

		tbl.addPropertyChangeListener("sortColumn",
		                              new PropertyChangeListener()
		                              {
			                              public void propertyChange(PropertyChangeEvent evt)
			                              {
				                              setSortColumn(tbl, data, ((Number) evt.getNewValue()).intValue());
			                              }
		                              });

		if (getAutoResize())
		{
			tbl.setAutoResizeColumns(true);
			tbl.setAutoResizeWithHeaders(true);
			tbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
		else
		{
			tbl.setAutoResizeColumns(false);
			tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}

		setSortOrder(tbl, data, HelpfulJTable.SORT_OFF);

		tbl.addMouseListener(new MouseHandler(tbl, tbl));

		tbl.setTableHeader(new TableHeader(tbl, data.columnTypeNames));

		final JScrollPane scroller = new JScrollPane(tbl);

		return scroller;
	}


	/**
	 *  Sets the MaxRecordsToShow attribute of the ResultSetWindow class
	 *
	 * @param  maxRecs  The new MaxRecordsToShow value
	 * @since
	 */
	public final static void setMaxRecordsToShow(int maxRecs)
	{
		SqlPlugin.setGlobalProperty(MAX_RECS_TO_SHOW_PROP, "" + maxRecs);
	}


	/**
	 *  Sets the AutoResize attribute of the ResultSetWindow class
	 *
	 * @param  autoResize  The new AutoResize value
	 * @since
	 */
	public final static void setAutoResize(boolean autoResize)
	{
		SqlPlugin.setGlobalProperty(AUTORESIZE, "" + autoResize);
	}



	/**
	 *  Sets the CloseWithBuffer attribute of the ResultSetWindow class
	 *
	 * @param  closeWithBuffer  The new CloseWithBuffer value
	 * @since
	 */
	public final static void setCloseWithBuffer(boolean closeWithBuffer)
	{
		SqlPlugin.setGlobalProperty(CLOSE_WITH_BUFFER, "" + closeWithBuffer);
	}


	/**
	 *  Gets the MaxRecordsToShow attribute of the ResultSetWindow class
	 *
	 * @return    The MaxRecordsToShow value
	 * @since
	 */
	public final static int getMaxRecordsToShow()
	{
		try
		{
			return Integer.parseInt(SqlPlugin.getGlobalProperty(MAX_RECS_TO_SHOW_PROP));
		} catch (NumberFormatException ex)
		{
			return 10;
		} catch (NullPointerException ex)
		{
			return 10;
		}
	}


	/**
	 *  Gets the AutoResize attribute of the ResultSetWindow class
	 *
	 * @return    The AutoResize value
	 * @since
	 */
	public final static boolean getAutoResize()
	{
		try
		{
			return Boolean.valueOf(SqlPlugin.getGlobalProperty(AUTORESIZE)).booleanValue();
		} catch (NullPointerException ex)
		{
			return false;
		}
	}


	/**
	 *  Gets the CloseWithBuffer attribute of the ResultSetWindow class
	 *
	 * @return    The CloseWithBuffer value
	 * @since
	 */
	public final static boolean getCloseWithBuffer()
	{
		try
		{
			return Boolean.valueOf(SqlPlugin.getGlobalProperty(CLOSE_WITH_BUFFER)).booleanValue();
		} catch (NullPointerException ex)
		{
			return false;
		}
	}


	/**
	 *Description of the Method
	 *
	 * @since
	 */
	public final static void clearProperties()
	{
		SqlPlugin.unsetGlobalProperty(MAX_RECS_TO_SHOW_PROP);
		SqlPlugin.unsetGlobalProperty(AUTORESIZE);
		SqlPlugin.unsetGlobalProperty(CLOSE_WITH_BUFFER);
	}


	/**
	 *Description of the Method
	 *
	 * @param  rs                Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 * @since
	 */
	public static Data prepareModel(SqlServerRecord record, ResultSet rs)
	throws SQLException
	{
		int recCount = 0;

		if (rs == null)
			return null;

		final ResultSetMetaData rsmd = rs.getMetaData();
		final int colNumber = rsmd.getColumnCount();
		final String[] columnNames = new String[colNumber];
		final String[] columnTypeNames = new String[colNumber];
		final int[] columnTypes = new int[colNumber];
		for (int i = colNumber + 1; --i > 0;)
		{
			columnNames[i - 1] = rsmd.getColumnName(i);

			String type = rsmd.getColumnTypeName(i);

			columnTypes[i - 1] = rsmd.getColumnType(i);

			try
			{
				switch (rsmd.getColumnType(i))
				{
				case Types.CLOB:
				case Types.BLOB:
					break;
				default:
					final int precision = rsmd.getPrecision(i);
					if (precision != 0)
					{
						final int scale = rsmd.getScale(i);
						type += "[" + precision + ((scale == 0) ? "" : ("." + scale)) + "]";
					}
				}
			} catch (Exception ex)
			{
				//Log.log( Log.DEBUG, ResultSetWindow.class, ex );
				/*
				 *  SQLException - not supported?
				 *  Can also be NumberFormatException - for Oracle's CLOBs
				 */
			}

			if (rsmd.columnNoNulls == rsmd.isNullable(i))
				type += "/" + jEdit.getProperty("sql.resultSet.colHeaders.notNullable");

			columnTypeNames[i - 1] = type;
		}

		final java.util.List rowData = new ArrayList();
		final int maxRecs = getMaxRecordsToShow();

		while (rs.next())
		{
			if (++recCount > maxRecs)
				break;

			final Object[] aRow = new Object[colNumber];
			int j = 1;
			for (int i = colNumber; --i >= 0; j++)
				aRow[j - 1] = col2Object(record, rsmd, rs, j);

			rowData.add(aRow);
		}

		Log.log(Log.DEBUG, ResultSetWindow.class,
		        "Got " + rowData.size() + " records in " + columnNames.length + " columns");
		return new Data
		       (record,
		        (Object[][]) rowData.toArray(new Object[0][]),
		        columnNames,
		        columnTypeNames,
		        columnTypes,
		        recCount);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  rsmd              Description of Parameter
	 * @param  rs                Description of Parameter
	 * @param  idx               Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	protected static Object col2Object(SqlServerRecord record, ResultSetMetaData rsmd, ResultSet rs, int idx)
	throws SQLException
	{
		return record.getServerType().toObject(rs, rsmd.getColumnType(idx), idx);
	}


	protected class MouseHandler extends MouseAdapter
	{
		protected JTable table;
		protected Component comp;


		/**
		 *Constructor for the MouseHandler object
		 *
		 * @param  table  Description of Parameter
		 * @since
		 */
		public MouseHandler(Component comp, JTable table)
		{
			this.table = table;
			this.comp = comp;
		}


		public void mousePressed(MouseEvent evt)
		{
			final Point p = evt.getPoint();

			if ((evt.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
			{
				final ResultSetWindowPopup rswp = new ResultSetWindowPopup(table, evt.getPoint());
				rswp.show(comp, p.x + 1, p.y + 1);
				evt.consume();
				return;
			}

			if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
			{
				final String contents = table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()).toString();
				final JDialog d = new JDialog(view, "Cell Data");
				final JScrollPane sp = new JScrollPane(new JLabel(contents));
				d.getContentPane().add(sp);
				d.pack();
				d.setVisible(true);
			}
		}
	}


	protected static class TableHeader extends JTableHeader
	{
		protected String types[];


		/**
		 *Constructor for the TableHeader object
		 *
		 * @param  table  Description of Parameter
		 * @param  types  Description of Parameter
		 * @since
		 */
		public TableHeader(JTable table, String types[])
		{
			super(table.getColumnModel());
			this.types = types;
		}


		public String getToolTipText(MouseEvent evt)
		{
			final Point p = evt.getPoint();
			if (p == null)
				return null;
			final int colNo = columnAtPoint(p);
			if (colNo == -1)
				return null;
			return types[colNo];
		}
	}


	public static class BufferListener implements EBComponent
	{

		public void handleMessage(EBMessage msg)
		{
			if (!(msg instanceof BufferUpdate))
				return;

			final BufferUpdate umsg = (BufferUpdate) msg;
			if (umsg.getWhat() != umsg.CLOSED)
				return;

			if (!getCloseWithBuffer())
				return;

			final Buffer buffer = umsg.getBuffer();
			final java.util.List resultSets = (java.util.List)buffer.getProperty(RESULT_SETS_BUF_PROPERTY);
			if (resultSets != null)
			{
				for (Iterator i = resultSets.iterator(); i.hasNext();)
				{
					final Component page = (Component)i.next();
					Log.log(Log.DEBUG, ResultSetWindow.class,
					        "Removing page " + page + " from the notebook and the list of resultSets");
					i.remove();
					final Container notebook = page.getParent();
					if (notebook != null)   // probably the page was already removed from the notebook?
						notebook.remove(page);
				}
			}
		}
	}

	protected static class Data extends AbstractTableModel
	{
		public SqlServerRecord serverRecord;
		public Object rowData[][];
		public String columnNames[];
		public String columnTypeNames[];
		public int columnTypes[];
		public int recCount;


		/**
		 *Constructor for the Data object
		 *
		 * @param  rowData      Description of Parameter
		 * @param  recCount     Description of Parameter
		 * @param  columnNames  Description of Parameter
		 * @param  columnTypes  Description of Parameter
		 * @since
		 */
		public Data(SqlServerRecord serverRecord,
		            Object rowData[][],
		            String columnNames[],
		            String columnTypeNames[],
		            int columnTypes[],
		            int recCount)
		{
			this.serverRecord = serverRecord;
			this.rowData = rowData;
			this.columnNames = columnNames;
			this.columnTypeNames = columnTypeNames;
			this.columnTypes = columnTypes;
			this.recCount = recCount;
		}

		public String[] getColumnHeaders()
		{
			return columnNames;
		}


		public int[] getColumnTypes()
		{
			return columnTypes;
		}


		public int getRowCount()
		{
			return rowData.length;
		}


		public int getColumnCount()
		{
			return columnNames.length;
		}


		public Object getValueAt(int r, int c)
		{
			if (r >= rowData.length || r < 0)
				return null;
			if (c >= columnNames.length || c < 0)
				return null;

			return rowData[r][c];
		}


		public String getColumnName(int c)
		{
			return columnNames[c];
		}


		public boolean isCellEditable(int r, int c)
		{
			return false;
		}


		public Object[] getRowData(int r)
		{
			return rowData[r];
		}

		public SqlServerRecord getServerRecord()
		{
			return serverRecord;
		}
	}


	protected static class Renderer extends DefaultTableCellRenderer
	{
		protected SqlServerType serverType;

		public Renderer(SqlServerType serverType)
		{
			this.serverType = serverType;
		}

		public Component getTableCellRendererComponent(JTable table, 
		                                               Object value,
		                                               boolean isSelected,
		                                               boolean hasFocus,
		                                               int row, int column) {
			final String s = serverType.toString(value);
			return super.getTableCellRendererComponent(table, s, isSelected, hasFocus, row, column);
		}
	};
}

