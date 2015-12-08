package beauty.options;

import beauty.options.java.*;
import org.gjt.sp.jedit.*;
import javax.swing.JTabbedPane;
import ise.java.awt.*;

/**
 * An option pane to configure settings for the built-in Java beautifier.
 */
public class JavaOptionPane extends AbstractOptionPane {

    private beauty.options.java.JavaOptionPane javaOptionPane;
    private Java8OptionPane java8OptionPane;
    
    public JavaOptionPane() {
        super( "beauty.java" );
        setLayout(new KappaLayout());
        javaOptionPane = new beauty.options.java.JavaOptionPane();
        java8OptionPane = new Java8OptionPane();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab( jEdit.getProperty("beauty.java8.Brackets_and_Padding", "Brackets and Padding"), javaOptionPane );
        tabs.addTab( jEdit.getProperty("beauty.java8.Blank_Lines", "Blank Lines"), java8OptionPane );
        add("0, 0, 1, 1, 0, wh, 6", tabs);
    }

    public void _init() {
        javaOptionPane._init();
        java8OptionPane._init();
    }

    public void _save() {
        javaOptionPane._save();
        java8OptionPane._save();
    }
}
