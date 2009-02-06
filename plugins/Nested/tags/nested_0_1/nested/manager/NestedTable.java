package nested.manager ;

import javax.swing.JTable ;
import javax.swing.table.TableColumn ; 
import java.awt.Color ;
import javax.swing.table.TableCellEditor;

public class NestedTable extends JTable {
	
	public NestedTable( ){
		super( ) ;
		setModel( nested.Plugin.getModel( ) ) ;
		initWidth( ) ;
		setDefaultRenderer( Color.class , new ColorTableCellRenderer( ) ) ;
	}
	
	private void initWidth( ){
		TableColumn column ;
		column = getColumnModel().getColumn( 2 ) ;
		column.setPreferredWidth( 20 ) ;
		column.setMaxWidth( 20 ) ;
		TableCellEditor editor = new ColorEditor();
    column.setCellEditor(editor);
	}
	
}

