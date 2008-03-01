package superabbrevs;

import superabbrevs.installation.Installation;
import javax.swing.JDialog;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import superabbrevs.gui.AbbrevsDialog;

/**
 * @author sune
 * Created on 27. januar 2007, 21:58
 *
 */
public class SuperAbbrevsPlugin extends EditPlugin {
    
    public final static String NAME = "SuperAbbrevs";
        
    @Override
    public void start() {
        super.start();
        Installation.install();
    }
    
    @Override
    public void stop() {
        super.stop();
    }
    
    public static void shiftTab(View view, JEditTextArea textArea,
            Buffer buffer){
        InputHandler inputHandler = new InputHandler(view, textArea, buffer);        
        inputHandler.shiftTab();
    }
    
    public static void tab(View view, JEditTextArea textArea, Buffer buffer){
        InputHandler inputHandler = new InputHandler(view, textArea, buffer);        
        inputHandler.tab();
    }
    
    public static void showSearchDialog(View view, JEditTextArea textArea,
            Buffer buffer){
        InputHandler inputHandler = new InputHandler(view, textArea, buffer);        
        inputHandler.showSearchDialog();
    }
    
    public static void showOptionPane(View view, JEditTextArea textArea,
            Buffer buffer) {
        String mode = buffer.getMode().getName();
        
        AbbrevsOptionPaneController controller = new AbbrevsOptionPaneController(mode);
        JDialog dialog = new AbbrevsDialog(view, false, controller);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }
    
    public boolean usePluginHome() {
        return true;
    }
}
