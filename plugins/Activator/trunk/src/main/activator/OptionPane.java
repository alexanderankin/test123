package activator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.gjt.sp.jedit.AbstractOptionPane;


public class OptionPane extends AbstractOptionPane {
    
    
    public OptionPane() {
        super("Activator");
    }

    protected void _init() {
        setBorder(BorderFactory.createEmptyBorder(11, 11, 11, 11));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Activator"), BorderLayout.NORTH);
        panel.add(new ActivationPanel(false));
        panel.setPreferredSize(new Dimension(600, 600));
        addComponent(panel);
    }
}