/*
 * WhiteSpaceOptionPane.java
 * Copyright (c) 2000 Andre Kaplan
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/


package whitespace;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;


public class WhiteSpaceOptionPane extends AbstractOptionPane
{
    private JCheckBox  showSpaceDefault;
    private JCheckBox  showTabDefault;
    private JCheckBox  showWhitespaceDefault;
    private JCheckBox  showBlockDefault;
    private JCheckBox  showFoldDefault;
    private JCheckBox  showFoldTooltipDefault;

    private JButton    spaceColor;
    private JButton    tabColor;

    private JCheckBox  displayControlChars;
    private JButton    whitespaceColor;

    private JCheckBox  indentBlock;
    private JButton    blockColor;

    private JButton    foldColor;

    private JCheckBox  removeTrailingWhiteSpace;
    private JTextField escapeChars;
    private JCheckBox  softTabifyLeadingWhiteSpace;
    private JCheckBox  tabifyLeadingWhiteSpace;
    private JCheckBox  untabifyLeadingWhiteSpace;


    public WhiteSpaceOptionPane() {
        super("white-space");
    }


    public void _init() {
        this.showSpaceDefault       = this.createCheckBox(
            "white-space.show-space-default", true
        );
        this.showTabDefault         = this.createCheckBox(
            "white-space.show-tab-default", true
        );
        this.showWhitespaceDefault  = this.createCheckBox(
            "white-space.show-whitespace-default", true
        );
        this.showBlockDefault       = this.createCheckBox(
            "white-space.show-block-default", false
        );
        this.showFoldDefault        = this.createCheckBox(
            "white-space.show-fold-default", false
        );
        this.showFoldTooltipDefault = this.createCheckBox(
            "white-space.show-fold-tooltip-default", false
        );

        this.displayControlChars    = this.createCheckBox(
            "white-space.display-control-chars", false
        );
        this.indentBlock            = this.createCheckBox(
            "white-space.indent-block", true
        );

        this.spaceColor      = this.createColorButton("white-space.space-color");
        this.tabColor        = this.createColorButton("white-space.tab-color");
        this.whitespaceColor = this.createColorButton("white-space.whitespace-color");
        this.blockColor      = this.createColorButton("white-space.block-color");
        this.foldColor       = this.createColorButton("white-space.fold-color");

        this.removeTrailingWhiteSpace =
            this.createCheckBox("white-space.remove-trailing-white-space", false);
        this.escapeChars =
            new JTextField(jEdit.getProperty("white-space.escape-chars", "\\"), 10);

        ActionListener tabifyLogic = new TabifyLogicHandler();
        this.softTabifyLeadingWhiteSpace = this.createCheckBox(
            "white-space.soft-tabify-leading-white-space", false
        );
        this.softTabifyLeadingWhiteSpace.addActionListener(tabifyLogic);

        this.tabifyLeadingWhiteSpace     = this.createCheckBox(
            "white-space.tabify-leading-white-space", false
        );
        this.tabifyLeadingWhiteSpace.addActionListener(tabifyLogic);

        this.untabifyLeadingWhiteSpace   = this.createCheckBox(
            "white-space.untabify-leading-white-space", false
        );
        this.untabifyLeadingWhiteSpace.addActionListener(tabifyLogic);

        addComponent(this.showSpaceDefault);
        addComponent(jEdit.getProperty("options.white-space.space-color"),
            this.spaceColor);

        addComponent(this.showTabDefault);
        addComponent(jEdit.getProperty("options.white-space.tab-color"),
            this.tabColor);

        addComponent(this.showWhitespaceDefault);
        addComponent(this.displayControlChars);
        addComponent(jEdit.getProperty("options.white-space.whitespace-color"),
            this.whitespaceColor);

        addComponent(this.showBlockDefault);
        addComponent(this.indentBlock);
        addComponent(jEdit.getProperty("options.white-space.block-color"),
            this.blockColor);

        addComponent(this.showFoldDefault);
        addComponent(this.showFoldTooltipDefault);
        addComponent(jEdit.getProperty("options.white-space.fold-color"),
            this.foldColor);

        addComponent(new JLabel(jEdit.getProperty("options.white-space.on-save")));

        addComponent(this.removeTrailingWhiteSpace);

        addComponent(jEdit.getProperty("options.white-space.escape-chars"),
            this.escapeChars);

        addComponent(this.softTabifyLeadingWhiteSpace);
        addComponent(this.tabifyLeadingWhiteSpace);
        addComponent(this.untabifyLeadingWhiteSpace);
    }


    public void _save() {
        jEdit.setBooleanProperty("white-space.show-space-default",
            this.showSpaceDefault.isSelected());
        jEdit.setBooleanProperty("white-space.show-tab-default",
            this.showTabDefault.isSelected());
        jEdit.setBooleanProperty("white-space.show-whitespace-default",
            this.showWhitespaceDefault.isSelected());
        jEdit.setBooleanProperty("white-space.show-block-default",
            this.showBlockDefault.isSelected());
        jEdit.setBooleanProperty("white-space.show-fold-default",
            this.showFoldDefault.isSelected());
        jEdit.setBooleanProperty("white-space.show-fold-tooltip-default",
            this.showFoldTooltipDefault.isSelected());

        jEdit.setBooleanProperty("white-space.display-control-chars",
            this.displayControlChars.isSelected());
        jEdit.setBooleanProperty("white-space.indent-block",
            this.indentBlock.isSelected());

        jEdit.setProperty("white-space.space-color",
            GUIUtilities.getColorHexString(this.spaceColor.getBackground())
        );
        jEdit.setProperty("white-space.tab-color",
            GUIUtilities.getColorHexString(this.tabColor.getBackground())
        );
        jEdit.setProperty("white-space.whitespace-color",
            GUIUtilities.getColorHexString(this.whitespaceColor.getBackground())
        );
        jEdit.setProperty("white-space.block-color",
            GUIUtilities.getColorHexString(this.blockColor.getBackground())
        );
        jEdit.setProperty("white-space.fold-color",
            GUIUtilities.getColorHexString(this.foldColor.getBackground())
        );

        jEdit.setBooleanProperty("white-space.remove-trailing-white-space",
            this.removeTrailingWhiteSpace.isSelected()
        );
        jEdit.setProperty("white-space.escape-chars",
            this.escapeChars.getText()
        );
        jEdit.setBooleanProperty("white-space.soft-tabify-leading-white-space",
            this.softTabifyLeadingWhiteSpace.isSelected()
        );
        jEdit.setBooleanProperty("white-space.tabify-leading-white-space",
            this.tabifyLeadingWhiteSpace.isSelected()
        );
        jEdit.setBooleanProperty("white-space.untabify-leading-white-space",
            this.untabifyLeadingWhiteSpace.isSelected()
        );
    }


    private JButton createColorButton(String property) {
        JButton b = new JButton(" ");
        b.setBackground(GUIUtilities.parseColor(jEdit.getProperty(property)));
        b.addActionListener(new ActionHandler(b));
        b.setRequestFocusEnabled(false);
        return b;
    }


    private JCheckBox createCheckBox(String property, boolean defaultValue) {
        JCheckBox cb = new JCheckBox(jEdit.getProperty("options." + property));
        cb.setSelected(jEdit.getBooleanProperty(property, defaultValue));
        return cb;
    }


    class ActionHandler implements ActionListener {
        private JButton button;

        ActionHandler(JButton button) {
            this.button = button;
        }

        public void actionPerformed(ActionEvent evt) {
            JButton button = (JButton)evt.getSource();
            Color c = JColorChooser.showDialog(WhiteSpaceOptionPane.this,
                jEdit.getProperty("colorChooser.title"),
                button.getBackground()
            );
            if (c != null) {
                button.setBackground(c);
            }
        }
    }


    private class TabifyLogicHandler implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JCheckBox softTabify = WhiteSpaceOptionPane.this.softTabifyLeadingWhiteSpace;
            JCheckBox tabify     = WhiteSpaceOptionPane.this.tabifyLeadingWhiteSpace;
            JCheckBox untabify   = WhiteSpaceOptionPane.this.untabifyLeadingWhiteSpace;

            if (evt.getSource() == softTabify) {
                tabify.setEnabled(!softTabify.isSelected());
                untabify.setEnabled(!softTabify.isSelected());
            } else {
                if (tabify.isSelected() && untabify.isSelected()) {
                    if (evt.getSource() == tabify) {
                        untabify.setSelected(false);
                    } else if (evt.getSource() == untabify) {
                        tabify.setSelected(false);
                    }
                }
            }
        }
    }
}
