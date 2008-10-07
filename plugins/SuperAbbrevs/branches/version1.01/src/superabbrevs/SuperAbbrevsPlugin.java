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
    
    public static void shiftTab(View view, JEditTextArea textArea, Buffer buffer){
        JEditInterface jedit = new JEditInterface(view, textArea, buffer);
        InputHandler inputHandler = new InputHandler(jedit);        
        inputHandler.shiftTab();
    }
    
    public static void tab(View view, JEditTextArea textArea, Buffer buffer){
        JEditInterface jedit = new JEditInterface(view, textArea, buffer);
        InputHandler inputHandler = new InputHandler(jedit);        
        inputHandler.tab();
    }
    
    public static void esc(View view, JEditTextArea textArea, Buffer buffer) {
        JEditInterface jedit = new JEditInterface(view, textArea, buffer);
        InputHandler inputHandler = new InputHandler(jedit);        
        inputHandler.esc();
    }
    
    public static void showSearchDialog(View view, JEditTextArea textArea, 
            Buffer buffer){
        JEditInterface jedit = new JEditInterface(view, textArea, buffer);
        InputHandler inputHandler = new InputHandler(jedit);        
        inputHandler.showSearchDialog();
    }
    
    public static void showOptionPane(View view, JEditTextArea textArea, 
            Buffer buffer) {
        JEditInterface jedit = new JEditInterface(view, textArea, buffer);
        String mode = jedit.getMode().getName();
        
        AbbrevsOptionPaneController controller = 
                new AbbrevsOptionPaneController(mode);
        JDialog dialog = new AbbrevsDialog(jedit.getView(), false, controller);
        dialog.setVisible(true);
    }
    
    public boolean usePluginHome() {
        return true;
    }
}
