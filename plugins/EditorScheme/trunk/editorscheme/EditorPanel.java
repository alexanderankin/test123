package editorscheme;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.gjt.sp.jedit.jEdit;
// TODO: change this back when jEdit 4.5 is released
//import org.gjt.sp.jedit.gui.ColorWellButton;
import editorscheme.temporary.ColorWellButton;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.util.SyntaxUtilities;
import ise.java.awt.KappaLayout;

/**
 * Panel to edit the properties of a particular property group for
 * a given scheme.
 */
public class EditorPanel extends JPanel implements ActionListener {

    private EditorScheme scheme = null;
    private String propertyGroupName = "";
    private HashMap<String, JButton> buttons = new HashMap<String, JButton>();
    private JCheckBox usePropertyGroup;

    /**
     * @param propertyGroupName One of the names in the list returned by
     * <code>jEdit.getProperty("editor-scheme.property-groups")</code>.
     */
    public EditorPanel(EditorScheme scheme, String propertyGroupName) {
        if (scheme == null || propertyGroupName == null) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        this.scheme = scheme;
        this.propertyGroupName = propertyGroupName;
        installUI();
    }

    public void setScheme(EditorScheme es) {
        if (es == null) {
            return;
        }
        scheme = es;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (String name : buttons.keySet()) {
                    JButton button = buttons.get(name);
                    
                    ChangeListener[] listeners = button.getChangeListeners();
                    for (ChangeListener listener : listeners) {
                        if (listener instanceof ColorWellButtonListener) {
                            button.removeChangeListener(listener);   
                        }
                        if (listener instanceof StyleButtonListener) {
                            button.removeChangeListener(listener);   
                        }
                    }
                    
                    String colorString = scheme.getProperty(name);
                    
                    if (name.indexOf("style") > 0 && colorString != null) {
                        SyntaxStyle style = getStyle(colorString);
                        StyleButton sb = (StyleButton) button;
                        sb.setText(name);
                        sb.setStyle(style);
                        button.addChangeListener(new StyleButtonListener(name));
                    } else if (colorString != null && colorString.length() > 0) {
                        int colorValue = Integer.decode(colorString);
                        Color color = new Color(colorValue);
                        ColorWellButton cwb = (ColorWellButton) button;
                        cwb.setSelectedColor(color);
                        button.addChangeListener(new ColorWellButtonListener(name));
                    } else {
                        ColorWellButton cwb = (ColorWellButton) button;
                        cwb.setSelectedColor(jEdit.getColorProperty(name));
                        button.addChangeListener(new ColorWellButtonListener(name));
                    }
                }
                repaint();
            }
        } );
    }

    private void installUI() {
        boolean apply = jEdit.getBooleanProperty("editor-scheme." + propertyGroupName + ".apply", true);
        usePropertyGroup = new JCheckBox(jEdit.getProperty("editor-scheme.usegroup", "Use this group"), apply);
        usePropertyGroup.addActionListener( 
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    jEdit.setBooleanProperty("editor-scheme." + propertyGroupName + ".apply", usePropertyGroup.isSelected());
                    for (JButton button : buttons.values()) {
                        button.setEnabled(usePropertyGroup.isSelected());
                    }
                }
            }
        );
        
        // get the names and sort them
        String propNames = jEdit.getProperty("editor-scheme." + propertyGroupName + "-props");
        String[] names = propNames.split("\\s+");
        TreeMap<String, String> nameMap = new TreeMap<String, String>();
        for (String name : names ) {
            String displayName = jEdit.getProperty("editor-scheme." + name + ".name");
            nameMap.put(displayName, name);
        }
        
        KappaLayout layout = new KappaLayout();
        setLayout(layout);
        setBorder(BorderFactory.createEmptyBorder(11, 11, 12, 12));
        KappaLayout.Constraints con = KappaLayout.createConstraint();
        con.x = 0;
        con.w = 1;
        con.h = 1;
        con.s = "w";
        con.p = 3;
        con.y = 0;
        
        add(usePropertyGroup, con);

        int y = 1;  // row for the layout constraint
        for (String name : nameMap.values()) {
            if (name.toLowerCase().indexOf("font") == - 1) {
                String colorString = scheme.getProperty(name);
                int colorValue = 0;
                SyntaxStyle style = null;
                JButton button;
                String text = jEdit.getProperty("editor-scheme." + name + ".name");
                if (name.indexOf("style") > 0 && colorString != null) {
                    style = getStyle(colorString);
                    button = new StyleButton(text, style);
                    if (! scheme.getReadOnly()) {
                        button.addChangeListener(new StyleButtonListener(name));
                    }
                } else if (colorString != null && colorString.length() > 0) {
                    colorValue = Integer.decode(colorString);
                    Color color = new Color(colorValue);
                    button = new ColorWellButton(color);
                    if (! scheme.getReadOnly()) {
                        button.addChangeListener(new ColorWellButtonListener(name));
                    }
                } else {
                    button = new ColorWellButton(jEdit.getColorProperty(name));
                    if (! scheme.getReadOnly()) {
                        button.addChangeListener(new ColorWellButtonListener(name));
                    }
                }
                buttons.put(name, button);

                JLabel label = new JLabel(text);
                con.x = 0;
                con.y = y;
                add(label, con);
                con.x = 1;
                add(button, con);
            }
            ++y;
        }
    }

    class StyleButtonListener implements ChangeListener {
        private String name;
        public StyleButtonListener(String name) {
            this.name = name;
        }
        public void stateChanged(ChangeEvent ae) {
            StyleButton btn = (StyleButton) ae.getSource();
            SyntaxStyle ss = btn.getStyle();
            StringBuilder sb = new StringBuilder();
            if (ss.getForegroundColor() != null) {
                sb.append("color:").append(SyntaxUtilities.getColorHexString(ss.getForegroundColor())).append(' ');
            }
            if (ss.getBackgroundColor() != null) {
                sb.append("bgColor:").append(SyntaxUtilities.getColorHexString(ss.getBackgroundColor())).append(' ');
            }
            if (ss.getFont() != null) {
                Font font = ss.getFont();
                if (font.isBold()) {
                    sb.append("style:").append("b ");
                }
                if (font.isItalic()) {
                    sb.append("style:").append("i");
                }
            }
            scheme.setProperty(name, sb.toString());
        }
    }

    class ColorWellButtonListener implements ChangeListener {
        private String name;
        public ColorWellButtonListener(String name) {
            this.name = name;
        }
        public void stateChanged(ChangeEvent ae) {
            ColorWellButton btn = (ColorWellButton) ae.getSource();
            String colorHexString = SyntaxUtilities.getColorHexString(btn.getSelectedColor());
            scheme.setProperty(name, colorHexString);
        }
    }

    private SyntaxStyle getStyle(String value) {
        Font font = jEdit.getActiveView().getTextArea().getFont();
        return SyntaxUtilities.parseStyle(value, font.getFamily(), font.getSize(), true);
    }

    public String getName() {
        return propertyGroupName;
    }

    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source instanceof JComboBox) {
            Object selectedItem = ((JComboBox) source).getSelectedItem();
            if (selectedItem instanceof EditorScheme) {
                setScheme((EditorScheme) selectedItem);
            }
        }
    }
}
