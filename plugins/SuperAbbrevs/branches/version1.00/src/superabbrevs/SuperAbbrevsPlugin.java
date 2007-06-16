package superabbrevs;

import java.util.Hashtable;
import javax.swing.JDialog;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import superabbrevs.gui.AbbrevsDialog;

/**
 * @author sune
 * Created on 27. januar 2007, 21:58
 *
 */
public class SuperAbbrevsPlugin extends EditPlugin {
    
    private final static String NAME = "SuperAbbrevs";
    
    private static Hashtable<Buffer, InputHandler> handlers = 
            new Hashtable<Buffer, InputHandler>();
    
    public void start() {
        super.start();
        // migrate old abbrevation folder to the new home
	System.out.println("----------------------------start");
	Persistence.createPluginsDir();
        Persistence.createAbbrevsDir();
        //Persistence.writeDefaultAbbrevs();
    }
    
    public void stop() {
        super.stop();
    }
    
    public static void shiftTab(View view, JEditTextArea textArea,
            Buffer buffer){
        
    }
    
    public static void tab(View view, JEditTextArea textArea, Buffer buffer){
        InputHandler inputHandler = handlers.get(buffer);
        if (inputHandler == null) {
            inputHandler = new InputHandler(view, textArea, buffer);
            handlers.put(buffer, inputHandler);
        } 
        
        inputHandler.tab();
    }
    
    public static void showDialog(View view, JEditTextArea textArea,
            Buffer buffer){
        
    }
    
    public static void showOptionPane(View view, JEditTextArea textArea,
            Buffer buffer) {
        String mode = buffer.getMode().getName();
        
        AbbrevsOptionPaneController controller = new AbbrevsOptionPaneController(mode);
        JDialog dialog = new AbbrevsDialog(view, false, controller);
        dialog.setVisible(true);
    }
    
    public boolean usePluginHome() {
        return true;
    }
}
