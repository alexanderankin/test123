package browser;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class GlobalTableModel extends AbstractTableModel {

	String [] COLUMNS = { "File", "Line", "Name", "Text" };
	Vector<GlobalReference> refs = new Vector<GlobalReference>();
	
	
	@Override
	public String getColumnName(int col) {
		return COLUMNS[col];
	}

	public int getColumnCount() {
		return COLUMNS.length;
	}

	
	public int getRowCount() {
		return refs.size();
	}

	public Object getValueAt(int row, int col) {
		GlobalRecord rec = ((GlobalReference)refs.get(row)).getRec();
		switch (col) {
		case 0:
			return rec.getFile();
		case 1:
			return rec.getLine();
		case 2:
			return rec.getName();
		case 3:
			return rec.getText();
		}
		return null;
	}

	public void clear()	{
		int nRows = refs.size();
		refs.clear();
		this.fireTableRowsDeleted(0, nRows);
	}
	public void add(GlobalReference ref) {
		refs.add(ref);
		fireTableRowsInserted(refs.size() - 1, refs.size() - 1);
	}

	public GlobalReference getRef(int index) {
		if (index < 0 || index >= refs.size())
			return null;
		return refs.get(index);
	}
}
