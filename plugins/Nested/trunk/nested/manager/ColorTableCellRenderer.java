package nested.manager ;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class ColorTableCellRenderer extends DefaultTableCellRenderer {
 
  JLabel label = new JLabel();
 
  public ColorTableCellRenderer() { 
		super( ) ;
	}
 
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus, int row,
                                                 int column) {
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() ;
		Component c =
        renderer.getTableCellRendererComponent( table, "", isSelected, hasFocus, row, column);
		c.setBackground( (Color)value );
    return c ;
 }
}

