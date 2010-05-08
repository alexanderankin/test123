package ctagsinterface.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import ctagsinterface.main.CtagsInterfacePlugin;

public class Query {
	private Vector<Object> conditions;
	private Vector<Object> tables;
	private Vector<Object> columns;
	private Vector<Object> order;
	private int limit = 0;
	
	public Query() {
		conditions = new Vector<Object>();
		tables = new Vector<Object>();
		columns = new Vector<Object>();
		order = new Vector<Object>();
	}
	public Query(Object column, Object table, Object condition) {
		this();
		setColumn(column);
		setTable(table);
		setCondition(condition);
	}
	public Query(Object [] columns, Object [] tables, Object [] conditions) {
		this();
		setColumns(columns);
		setTables(tables);
		setConditions(conditions);
	}

	public ResultSet run() throws SQLException {
		return CtagsInterfacePlugin.getDB().query(this.toString());
	}
	
	public void addTable(Object table) {
		tables.add(table);
	}
	public void setTables(Object [] tables) {
		this.tables.clear();
		for (int i = 0; i < tables.length; i++)
			addTable(tables[i]);
	}
	public void setTable(Object table) {
		setTables(new Object [] { table });
	}
	
	public void addCondition(Object condition) {
		conditions.add(condition);
	}
	public void setConditions(Object [] conditions) {
		this.conditions.clear();
		for (int i = 0; i < conditions.length; i++)
			addCondition(conditions[i]);
	}
	public void setCondition(Object condition) {
		setConditions(new Object [] { condition });
	}
	public void setConditions(Vector<Object> conditions) {
		this.conditions = conditions;
	}
	
	public Vector<Object> getConditions() {
		return conditions;
	}
	
	public void addColumn(Object column) {
		columns.add(column);
	}
	public void setColumns(Object [] columns) {
		this.columns.clear();
		for (int i = 0; i < columns.length; i++)
			addColumn(columns[i]);
	}
	public void setColumn(Object column) {
		setColumns(new Object [] { column });
	}
	public void setColumns(Vector<Object> columns) {
		this.columns = columns;
	}

	public void setOrder(Vector<Object> order) {
		this.order = order;
	}
	public void setOrder(Object order) {
		this.order.clear();
		this.order.add(order);
	}

	// Set the max number of rows to return.
	public void setLimit(int limit) {
		this.limit = limit; 
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("SELECT ");
		s.append(join(",", columns));
		s.append(" FROM ");
		s.append(join(",", tables));
		s.append(" WHERE ");
		s.append(join(" AND ", conditions));
		if (! order.isEmpty())
			s.append(" ORDER BY " + join(",", order));
		if (limit > 0)
			s.append(" LIMIT " + limit);
		return s.toString();
	}

	private String join(String separator, Vector<Object> list) {
		StringBuffer s = new StringBuffer();
		boolean first = true;
		for (int i = 0; i < list.size(); i++) {
			if (! first)
				s.append(separator);
			first = false;
			s.append(list.get(i).toString());
		}
		return s.toString();
	}
}
