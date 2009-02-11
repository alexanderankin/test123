package superabbrevs.gui;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import superabbrevs.AbbrevsOptionPaneController;
import superabbrevs.SuperAbbrevsPlugin;

public class AbbrevsDialog extends EnhancedDialog {

    private AbbrevsManagerPane abbrevsOptionPane;

    public AbbrevsDialog(View view, boolean modal, AbbrevsOptionPaneController controller) {
        super(view, "Abbreviations", modal);

        initComponents();

        Properties p = loadWindowState();
        
        if (p != null) {
            int height = new Integer(p.getProperty("AbbrevsDialog.height")).intValue();
            int width = new Integer(p.getProperty("AbbrevsDialog.width")).intValue();
            int x = new Integer(p.getProperty("AbbrevsDialog.x")).intValue();
            int y = new Integer(p.getProperty("AbbrevsDialog.y")).intValue();
            
            this.setBounds(x, y, width, height); 
        } else {
            this.setLocationRelativeTo(view);
        }

        setEnterEnabled(false);
        abbrevsOptionPane = new AbbrevsManagerPane(controller);
        abbrevsOptionPane.setVisible(true);
        setContentPane(abbrevsOptionPane);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Abbreviation editor");
        setName("abbrevsDialog");

        pack();
    }
    
    public void ok() {
    }

    public void cancel() {
        try {
            abbrevsOptionPane.save();
            saveWindowState();
            dispose();
        } catch (ValidationException ex) {
        }
    }

    private Properties loadWindowState() {
        Properties p = new Properties();
        InputStream in = SuperAbbrevsPlugin.getResourceAsStream(
                SuperAbbrevsPlugin.class, "AbbrevsDialog.properties");
        try {    
            p.load(in);
            return p;
        } catch (Exception ex) {
            return null;
        } finally {
            try { in.close(); } catch (Exception ex) {}
        }
    }

    private void saveWindowState() {
        Properties p = new Properties();
        p.setProperty("AbbrevsDialog.height", "" + this.getHeight());
        p.setProperty("AbbrevsDialog.width", "" + this.getWidth());
        p.setProperty("AbbrevsDialog.x", "" + this.getX());
        p.setProperty("AbbrevsDialog.y", "" + this.getY());

        OutputStream out = SuperAbbrevsPlugin.getResourceAsOutputStream(
                SuperAbbrevsPlugin.class, "AbbrevsDialog.properties");

        try {
            p.store(out, "Saving the window state");
        } catch (Exception ex) {
            JOptionPane.showConfirmDialog(this, ex.getMessage(), "Exception",
                    JOptionPane.ERROR);
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }
}
