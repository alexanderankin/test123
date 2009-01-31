package nested.manager ;

import java.util.Vector ;
import java.util.HashMap ;
import javax.swing.table.AbstractTableModel ;  
import java.awt.Color ;

public class NestedTableModel extends AbstractTableModel {
	
	private String[] columnNames = { "mode", "sub-mode" , "" } ;
	private Vector<NestedObject> data = null ;
	
	public NestedTableModel( ){
		super( ) ;
		init( ) ;
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
		if( data == null ) return null ;
		NestedObject o = data.get( row ) ;
		if( col == 0 ) return o.getMode() ; 
		if( col == 1 ) return o.getSubMode() ; 
		if( col == 2 ) return o.getColor() ; 
		return null ;   
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return false; 
	}
	
	// TODO: generate data reading file on the plugin dir
	// TODO: use one data structure( SortedMap ) instead of Vector+ HashMap
	public void init( ){
		data = new Vector<NestedObject>( ) ;
		data.add( new NestedObject( "jsp", "java", Color.YELLOW )  ) ;
		data.add( new NestedObject( "jaxx", "java", Color.YELLOW )  ) ;
		data.add( new NestedObject( "jaxx", "xml", null )  ) ;
		data.add( new NestedObject( "jaxx", "css", Color.CYAN )  ) ;
		data.add( new NestedObject( "actions", "xml", null )  ) ;
		generateMap( ) ;
	}
	
	private HashMap<String,NestedObject> map = null ; 
	
	public Color getColor( String mode, String submode ){
		if( data == null ) return null ;
		if( map == null ) generateMap( ) ;
		String key = mode + "--" + submode ;
		if( map.containsKey( key ) ) {
			return map.get( key ).getColor() ;
		}
		return null ;
	}
	
  private void generateMap( ){
		map = new HashMap<String,NestedObject>( ) ;
		for( int i=0; i<data.size(); i++){
			NestedObject o = data.get(i) ;
			map.put( o.getMode() + "--" + o.getSubMode(), o ) ;
		}
	}
}
