package nested.manager; 

import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import org.gjt.sp.jedit.View ;
import java.awt.BorderLayout ;

public class NestedManagerPanel extends JPanel {

	private NestedTable table ;
	private View view ; 
	
	public NestedManagerPanel( View view) {
		super( new BorderLayout() ) ;
		this.view = view ;
		table = new NestedTable( ) ;
		add( new JScrollPane( table ), BorderLayout.CENTER ) ; 
	}
	
	public NestedTable getTable( ){
		return table ;
	}
	
}

