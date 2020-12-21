package lookandfeel;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.gjt.sp.jedit.AbstractOptionPane;

public class SquarenessLnfInstaller  implements LookAndFeelInstaller {
	public String getName() {
        return "Squareness";		
	}
	
    public void install() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel( new net.beeger.squareness.SquarenessLookAndFeel() );
        UIManager.put( "ClassLoader", net.beeger.squareness.SquarenessLookAndFeel.class.getClassLoader() );
    }

	public AbstractOptionPane getOptionPane() {
		return null;	
	}
}
