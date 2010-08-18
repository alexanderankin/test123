
package xml.gui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import xml.AntXmlParsedData;

import sidekick.SideKickParsedData;
import eclipseicons.EclipseIconsPlugin;

/**
 * This is a toolbar to be added to SideKick. It provides the ability to sort
 * elements in an Ant file.
 */
public class AntModeToolBar extends JPanel {

    private JComboBox choices;
    private JButton direction;
    private View view = null;

    private static ImageIcon upIcon = EclipseIconsPlugin.getIcon("up.gif");    
    private static ImageIcon downIcon = EclipseIconsPlugin.getIcon("down.gif");    
    private boolean down = true;

    public AntModeToolBar(View view) {
        this.view = view;
        installComponents();
        installListeners();
    }

    private void installComponents() {
        choices = new JComboBox();
        choices.setEditable(false);

        JLabel sortLabel = new JLabel(jEdit.getProperty("options.sidekick.xml.sortBy", "Sort by:"));

        // these are added in the same order as the values of SORT_BY_NAME, etc, in AntXmlParsedData.
        choices.addItem(jEdit.getProperty("options.sidekick.xml.sortByName", "Name"));
        choices.addItem(jEdit.getProperty("options.sidekick.xml.sortByLine", "Line"));
        choices.addItem(jEdit.getProperty("options.sidekick.xml.sortByType", "Type"));

        direction = new JButton();
        direction.setIcon(downIcon);
        direction.setToolTipText(jEdit.getProperty("options.sidekick.xml.sortDirection", "Sort direction"));

        add(sortLabel);
        add(choices);
        add(direction);

        if (view != null) {
            AntXmlParsedData data = (AntXmlParsedData) SideKickParsedData.getParsedData(view);
            int choice = data.getSortBy();
            choices.setSelectedIndex(choice);
        }
    }

    private void installListeners() {
        choices.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
                if (view != null) {
                    AntXmlParsedData data = (AntXmlParsedData) SideKickParsedData.getParsedData(view);
                    if (data != null) {
                        data.setSortBy(choices.getSelectedIndex());
                        data.sort(view);
                    }
                }
            }
        } );

        direction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (view != null) {
                    AntXmlParsedData data = (AntXmlParsedData) SideKickParsedData.getParsedData(view);
                    if (data != null) {
                        down = !down;
                        direction.setIcon(down ? downIcon : upIcon);
                        data.setSortDirection(down);
                        data.sort(view);
                    }
                }
            }
        } );
    }
}