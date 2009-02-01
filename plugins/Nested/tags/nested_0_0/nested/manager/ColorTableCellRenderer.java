package nested.manager ;

import javax.swing.JTable ;
import javax.swing.JLabel ;
import javax.swing.table.TableCellRenderer ;
import javax.swing.table.DefaultTableCellRenderer ;
import java.awt.Component ;
import java.awt.Color ;

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

