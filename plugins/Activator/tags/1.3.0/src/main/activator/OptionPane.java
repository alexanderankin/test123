package activator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class OptionPane extends AbstractOptionPane {


    public OptionPane() {
        super(jEdit.getProperty("activator.label", "Activator"));
    }

    protected void _init() {
        setBorder(BorderFactory.createEmptyBorder(11, 11, 11, 11));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(jEdit.getProperty("activator.optionpane.label", "Activator")), BorderLayout.NORTH);
        panel.add(new ActivationPanel(false));
        panel.setMinimumSize(new Dimension(400, 400));
        panel.setPreferredSize(new Dimension(400, 400));
        addComponent(panel);
    }
}