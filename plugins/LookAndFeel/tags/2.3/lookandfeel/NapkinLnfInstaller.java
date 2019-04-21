package lookandfeel;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.gjt.sp.jedit.AbstractOptionPane;

public class NapkinLnfInstaller  implements LookAndFeelInstaller {
	public String getName() {
        return "Napkin";		
	}
	
    public void install() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel( new net.sourceforge.napkinlaf.NapkinLookAndFeel() );
        UIManager.put( "ClassLoader", net.sourceforge.napkinlaf.NapkinLookAndFeel.class.getClassLoader() );
    }

	public AbstractOptionPane getOptionPane() {
		return null;	
	}
}
