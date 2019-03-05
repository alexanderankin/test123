package editorscheme;

import java.awt.event.*;
import javax.swing.*;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.gui.StyleEditor;
import org.gjt.sp.jedit.jEdit;

public class StyleButton extends JButton {
    private SyntaxStyle style;
    public StyleButton(String text, SyntaxStyle ss) {
        super(text);
        setStyle(ss);
        addActionListener (new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                style = new StyleEditor(jEdit.getActiveView(), style, StyleButton.this.getText()).getStyle();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        setStyle(style);
                    }
                } );
            }
        }
       );
    }
    
    public void setStyle(SyntaxStyle ss) {
        this.style = ss;
        setBackground(style.getBackgroundColor());
        setForeground(style.getForegroundColor());
        setFont(style.getFont());
        fireStateChanged();
    }

    public SyntaxStyle getStyle() {
        return style;
    }
}