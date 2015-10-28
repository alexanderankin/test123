/**
 * CssSideKickOptionPane.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Jakub Roztocil
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

package sidekick.css;

//{{{ Imports
import javax.swing.JCheckBox;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
//}}}

public class CssSideKickOptionPane extends AbstractOptionPane  {

    // formatting options
    private JCheckBox quote;
    private JCheckBox colon;
    private JCheckBox spaceAfterColon;

    // display options
    private JCheckBox showProprietaryAsError;


    //{{{ CssSideKickOptionPane constructor
    public CssSideKickOptionPane() {
        super("sidekick.css");
    } //}}}

    //{{{ _init() method
    public void _init() {
        addSeparator("Formatting Options");

        quote = new JCheckBox(jEdit.getProperty(CssSideKickPlugin.OPTION_PREFIX + "quote.title"));
        quote.setSelected(jEdit.getProperty(CssSideKickPlugin.OPTION_PREFIX + "quote").equals("'"));
        addComponent(quote);

        colon = new JCheckBox(jEdit.getProperty(CssSideKickPlugin.OPTION_PREFIX + "colon.title"));
        colon.setSelected(jEdit.getBooleanProperty(CssSideKickPlugin.OPTION_PREFIX + "colon"));
        addComponent(colon);

        spaceAfterColon = new JCheckBox(jEdit.getProperty(CssSideKickPlugin.OPTION_PREFIX + "space-after-colon.title"));
        spaceAfterColon.setSelected(jEdit.getBooleanProperty(CssSideKickPlugin.OPTION_PREFIX + "space-after-colon"));
        addComponent(spaceAfterColon);

        addSeparator("Display Options");

        showProprietaryAsError = new JCheckBox(jEdit.getProperty(CssSideKickPlugin.OPTION_PREFIX + "showProprietaryAsError.title"));
        showProprietaryAsError.setSelected(jEdit.getBooleanProperty(CssSideKickPlugin.OPTION_PREFIX + "showProprietaryAsError"));
        addComponent(showProprietaryAsError);


    } //}}}

    //{{{ _save() method
    public void _save() {
        jEdit.setProperty(CssSideKickPlugin.OPTION_PREFIX + "quote", quote.isSelected()
                                        ? "'"
                                        : "\"");
        jEdit.setBooleanProperty(CssSideKickPlugin.OPTION_PREFIX + "colon", colon.isSelected());
        jEdit.setBooleanProperty(CssSideKickPlugin.OPTION_PREFIX + "space-after-colon", spaceAfterColon.isSelected());
        jEdit.setBooleanProperty(CssSideKickPlugin.OPTION_PREFIX + "showProprietaryAsError", showProprietaryAsError.isSelected());
        CssSideKickCompletion.readConfig();
    } //}}}

}
