package lookandfeel;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class NapkinLnfInstaller extends LnfInstaller {
    public void install() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel( new net.sourceforge.napkinlaf.NapkinLookAndFeel() );
        UIManager.put( "ClassLoader", net.sourceforge.napkinlaf.NapkinLookAndFeel.class.getClassLoader() );
    }

}
