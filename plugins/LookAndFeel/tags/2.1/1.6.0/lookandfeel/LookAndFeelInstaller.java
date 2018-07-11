package lookandfeel;

import javax.swing.UnsupportedLookAndFeelException;
import org.gjt.sp.jedit.AbstractOptionPane;

public interface LookAndFeelInstaller {
    
    public final static String SERVICE_NAME = "lookandfeel.LookAndFeelInstaller";
    
    /**
     * @return the name of this look and feel    
     */
    public String getName();
    
    /**
     * @return A configuration panel for this look and feel.  May return null
     * if there is nothing to configure for this look and feel.
     */
    public AbstractOptionPane getOptionPane();
    
    /**
     * Install this look and feel.        
     */
    public void install() throws UnsupportedLookAndFeelException;
}
