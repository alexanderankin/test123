
package xml.gui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import xml.TldXmlParsedData;
import sidekick.SideKickParsedData;

/**
 * This is a toolbar to be added to SideKick. It provides the ability to sort
 * elements in a TLD file.
 */
public class TldModeToolBar extends JPanel {

    private JComboBox choices;
    private View view = null;

    public TldModeToolBar(View view) {
        this.view = view;
        installComponents();
        installListeners();
    }

    private void installComponents() {
        choices = new JComboBox();
        choices.setEditable(false);

        JLabel sortLabel = new JLabel(jEdit.getProperty("options.sidekick.xml.sortBy", "Sort by:"));

        // these are added in the same order as the values of SORT_BY_NAME, etc, in TldXmlParsedData.
        choices.addItem(jEdit.getProperty("options.sidekick.xml.sortByName", "Name"));
        choices.addItem(jEdit.getProperty("options.sidekick.xml.sortByLine", "Line"));
        choices.addItem(jEdit.getProperty("options.sidekick.xml.sortByType", "Type"));

        add(sortLabel);
        add(choices);

        if (view != null) {
            TldXmlParsedData data = (TldXmlParsedData) SideKickParsedData.getParsedData(view);
            int choice = data.getSortBy();
            choices.setSelectedIndex(choice);
        }
    }

    private void installListeners() {
        choices.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
                if (view != null) {
                    TldXmlParsedData data = (TldXmlParsedData) SideKickParsedData.getParsedData(view);
                    if (data != null) {
                        data.setSortBy(choices.getSelectedIndex());
                        data.sort(view);
                    }
                }
            }
        } );
    }
}