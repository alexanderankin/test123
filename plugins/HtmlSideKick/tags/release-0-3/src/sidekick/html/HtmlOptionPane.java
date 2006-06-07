
package sidekick.html;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import org.gjt.sp.jedit.*;

public class HtmlOptionPane extends AbstractOptionPane  /*implements ActionListener*/ {
    
    JCheckBox showTagAttributes;
    JCheckBox showCoreAttributes;
    JCheckBox showLangAttributes;
    JCheckBox showScriptAttributes;
    JCheckBox showBrackets;
    JRadioButton showAllElements;
    JRadioButton showBlockElements;
    JCheckBox showJspElements;
    
    public HtmlOptionPane() {
        super("sidekick.html");   
    }

    public void _init() {
        setBorder(new TitledBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(6, 6, 6, 6)), jEdit.getProperty("options.sidekick.html.panel_label")));
        
        // attribute display
        JLabel attributes_label = new JLabel(jEdit.getProperty("options.sidekick.html.showAttributes.label"));
        addComponent(attributes_label);
        
        showTagAttributes = new JCheckBox(jEdit.getProperty("options.sidekick.html.showTagAttributes.label"));
        showTagAttributes.setSelected(jEdit.getBooleanProperty("options.sidekick.html.showTagAttributes", true));
        addComponent(showTagAttributes);
        
        showCoreAttributes = new JCheckBox(jEdit.getProperty("options.sidekick.html.showCoreAttributes.label"));
        showCoreAttributes.setSelected(jEdit.getBooleanProperty("options.sidekick.html.showCoreAttributes", true));
        addComponent(showCoreAttributes);
        
        showLangAttributes = new JCheckBox(jEdit.getProperty("options.sidekick.html.showLangAttributes.label"));
        showLangAttributes.setSelected(jEdit.getBooleanProperty("options.sidekick.html.showLangAttributes", true));
        addComponent(showLangAttributes);

        showScriptAttributes = new JCheckBox(jEdit.getProperty("options.sidekick.html.showScriptAttributes.label"));
        showScriptAttributes.setSelected(jEdit.getBooleanProperty("options.sidekick.html.showScriptAttributes", true));
        addComponent(showScriptAttributes);
        
        addSeparator();
        
        // bracket display
        showBrackets = new JCheckBox(jEdit.getProperty("options.sidekick.html.showBrackets.label"));
        showBrackets.setSelected(jEdit.getBooleanProperty("options.sidekick.html.showBrackets", true));
        addComponent(showBrackets);

        addSeparator();
        
        // element display
        showAllElements = new JRadioButton(jEdit.getProperty("options.sidekick.html.showAllElements.label"));
        showAllElements.setSelected(jEdit.getBooleanProperty("options.sidekick.html.showAllElements", true));
        addComponent(showAllElements);

        showBlockElements = new JRadioButton(jEdit.getProperty("options.sidekick.html.showBlockElements.label"));
        showBlockElements.setSelected(jEdit.getBooleanProperty("options.sidekick.html.showBlockElements", false));
        addComponent(showBlockElements);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(showAllElements);
        bg.add(showBlockElements);

        showJspElements = new JCheckBox(jEdit.getProperty("options.sidekick.html.showJspElements.label"));
        showJspElements.setSelected(jEdit.getBooleanProperty("options.sidekick.html.showJspElements", true));
        addComponent(showJspElements);
    }

    public void _save() {
        jEdit.setBooleanProperty("options.sidekick.html.showTagAttributes", showTagAttributes.isSelected());
        jEdit.setBooleanProperty("options.sidekick.html.showCoreAttributes", showCoreAttributes.isSelected());
        jEdit.setBooleanProperty("options.sidekick.html.showLangAttributes", showLangAttributes.isSelected());
        jEdit.setBooleanProperty("options.sidekick.html.showScriptAttributes", showScriptAttributes.isSelected());
        jEdit.setBooleanProperty("options.sidekick.html.showBrackets", showBrackets.isSelected());
        jEdit.setBooleanProperty("options.sidekick.html.showAllElements", showAllElements.isSelected());
        jEdit.setBooleanProperty("options.sidekick.html.showBlockElements", showBlockElements.isSelected());
        jEdit.setBooleanProperty("options.sidekick.html.showJspElements", showJspElements.isSelected());
    }
}
