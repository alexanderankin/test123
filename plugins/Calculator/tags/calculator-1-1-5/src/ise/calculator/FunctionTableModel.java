package ise.calculator;

import javax.swing.table.*;
import java.util.*;

public class FunctionTableModel extends AbstractTableModel {
   
   private Vector data = null;
   private Vector col_names = null;
   
   public FunctionTableModel() {}
   
   public FunctionTableModel( Vector v ) {
      data = v;
   }
   
   public String getColumnName( int col ) {
      if ( col_names == null )
         return null;
      return col_names.elementAt( col ).toString();
   }
   
   public boolean isCellEditable( int row, int col ) {
      return col == 0;
   }
   
   public Object getValueAt( int row, int col ) {
      if ( data == null )
         return null;
      return ( ( Vector ) data.elementAt( row ) ).elementAt( col );
   }
   
   public void setValueAt( Object value, int row, int col ) {
      if ( data == null || value == null )
         return ;
      Vector row_data = ( Vector ) data.elementAt( row );
      if ( row_data == null )
         return ;
      row_data.set( col, value );
   }
   
   public void setDataVector( Vector v, Vector col_names ) {
      data = v;
      this.col_names = col_names;
      fireTableDataChanged();
   }
   
   public int getRowCount() {
      if ( data == null )
         return 0;
      return data.size();
   }
   
   public int getColumnCount() {
      if ( col_names == null )
         return 0;
      return col_names.size();
   }
   
   public Class getColumnClass( int col ) {
      if ( data == null )
         return null;
      return getValueAt( 0, col ).getClass();
   }
}

