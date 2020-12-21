package lookandfeel;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.gjt.sp.jedit.AbstractOptionPane;

public class InfoNodeLnfInstaller  implements LookAndFeelInstaller {
	public String getName() {
        return "InfoNode";		
	}
	
    public void install() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel( new net.infonode.gui.laf.InfoNodeLookAndFeel() );
        UIManager.put( "ClassLoader", net.infonode.gui.laf.InfoNodeLookAndFeel.class.getClassLoader() );
    }

	public AbstractOptionPane getOptionPane() {
		return null;	
	}
}
