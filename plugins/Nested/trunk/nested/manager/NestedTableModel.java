package nested.manager ;

import java.util.Vector ;
import java.util.TreeMap ;
import javax.swing.table.AbstractTableModel ;  
import java.awt.Color ;

import org.gjt.sp.jedit.textarea.JEditTextArea ;
import org.gjt.sp.jedit.jEdit ;
import org.gjt.sp.jedit.EditPlugin ;

import javax.swing.SwingUtilities ;
import java.io.File ;

public class NestedTableModel extends AbstractTableModel {
	
	private String[] columnNames = { "mode", "sub-mode" , "" } ;
	private TreeMap<String,NestedObject> data ; 
	private File home ; 
	
	public NestedTableModel( ){
		super( ) ;
		home = EditPlugin.getPluginHome( nested.Plugin.class ) ;
		NestedReader nr = new NestedReader( home ) ;
		data = nr.getMap( ) ;
		fireTableDataChanged( ) ;
	}
	
	@Override
	public int  getColumnCount( ){
		return columnNames.length ;
	}
	
	@Override
	public int getRowCount() {
		return data == null ? 0 : data.size() ;
  }
	
	@Override
	public String getColumnName(int col) {
      return columnNames[col];
  }
	
	@Override
	public Class getColumnClass(int c) {
    if( c == 2) return Color.class ;
		return String.class ;
  }
	
	@Override
	public Object getValueAt( int row, int col ){
		if( data == null || row > data.size() ) return null ;
		NestedObject o = data.get( data.keySet().toArray()[row] );
		if( col == 0 ) return o.getMode() ; 
		if( col == 1 ) return o.getSubMode() ; 
		if( col == 2 ) return o.getColor() ; 
		return null ;   
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 2; 
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int column){
		if( column != 2 ) return ;
		( (NestedObject) data.get( data.keySet().toArray()[row] ) ).setColor( (Color)aValue ) ;
		SwingUtilities.invokeLater( new Runnable(){
				public void run(){
					saveMap( ) ;
				}
		} ) ;
		JEditTextArea textArea = jEdit.getActiveView().getTextArea( ) ;
		int first = textArea.getFirstPhysicalLine() ;
		int last = textArea.getLastPhysicalLine() ;
		textArea.invalidateLineRange( first, last ) ;
		fireTableCellUpdated( row, column ) ;
	}
	
	private String getKey( String mode, String submode ){
		String out = mode + "-" + submode ;
		return out ; 
	}
	
	public Color getColor( String mode, String submode ){
		if( data == null ) return null ;
		String key = getKey( mode, submode ) ;
		if( ! data.containsKey( key ) ) {
			data.put( key, new NestedObject(mode, submode ) ) ;
			SwingUtilities.invokeLater( new Runnable(){
					public void run(){
						saveMap( ) ;
					}
			} ) ;
		fireTableDataChanged() ;
		}
		return data.get( key ).getColor() ;
	}
	
	public void saveMap( ){
		NestedWriter writer = new NestedWriter( home ) ;
		writer.saveMap( data ) ;
	}
	
}

