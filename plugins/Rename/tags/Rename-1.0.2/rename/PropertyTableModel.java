package rename;

import javax.swing.table.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

/**
 *  Description of the Class
 *
 * @author     mace0031
 * @created    February 2, 2004
 * @version    $Revision$
 * @updated    $Date$ by $Author$
 */
public class PropertyTableModel extends AbstractTableModel {
	protected String type;
	protected ArrayList idList;
	protected String suffix;
	protected String[] variables;
	protected boolean[] changed;

	/**
	 * @param  idArray  The ids of the objects to examine
	 * @param  suffix   The suffix to append to get the variable property (ex: "label","title","name")
	 */
	public PropertyTableModel(String type,String[] idArray, String suffix) {
		this.type = type;
		this.suffix = suffix;
		idList = new ArrayList();
		for (int i = 0; i < idArray.length; i++) {
			idList.add(idArray[i]);
		}
		variables = new String[idList.size()];
		Collections.sort(idList, PropertyComparator.getInstance());
		changed = new boolean[idList.size()];
		for (int i = 0; i < idList.size(); i++) {
			variables[i] = jEdit.getProperty(idList.get(i) + "." + suffix);
			changed[i] = false;
		}
	}

	public int getRowCount() {
		return idList.size();
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int col) {
		if (col == 0) {
			return type+" ID";
		} else {
			return suffix;
		}
	}

	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return idList.get(row);
		} else {
			return variables[row];
		}
	}

	public void setValueAt(Object value, int row, int col) {
		if (isCellEditable(row, col)) {
			variables[row] = value.toString();
			changed[row] = true;
			Log.log(Log.DEBUG, this, idList.get(row) + " was changed");
		}
	}

	public boolean isCellEditable(int row, int col) {
		return col > 0;
	}

	public boolean rowChanged(int row) {
		return changed[row];
	}
}

