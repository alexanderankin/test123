/*
 *  MiscellaneousOptionPane.java -   
 *  Copyright (C) 2002  Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jimporter.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import jimporter.searchmethod.SearchMethod;
import org.gjt.sp.jedit.jEdit;

/**
 * An option pane containing the JImporter options that don't fit into another
 * category.
 *
 * @author Matthew Flower
 */
public class MiscellaneousOptionPane extends JImporterOptionPane {
    private ButtonGroup searchMethodOptions;
    private JLabel useBruteForceMethodDescription;

    private String searchMethod = SearchMethod.getCurrent().getUniqueIdentifier();
  
    /**
     * Standard constructor.
     */
    public MiscellaneousOptionPane() {
        super("jimporter.miscellaneous");
    }

    /**
     * This method is called by jEdit (or my option dialog) to indicate that this
     * option pane should save itself.
     */     
    public void _init() {
        SearchMethodRadioListener smListener = new SearchMethodRadioListener();

        searchMethodOptions = new ButtonGroup();
        Iterator it = SearchMethod.getSearchMethods().iterator();

        addSeparator("options.jimporter.searchmethod.label");
        while (it.hasNext()) {
            SearchMethod sm = (SearchMethod) it.next();

            JRadioButton radioButton = new JRadioButton(sm.getName());
            radioButton.setActionCommand(sm.getUniqueIdentifier());
            radioButton.addActionListener(smListener);
            radioButton.setSelected(sm.equals(SearchMethod.getCurrent()));
            
            //Make sure all of the search methods are in the same search method grouping
            searchMethodOptions.add(radioButton);

            //Add the radio button to the option pane
            addComponent(radioButton);
        }
        
        //Popup dialog options
        addSeparator("options.jimporter.dialogboxbehavior.label");
        new AutoSearchAtPointOption().createVisualPresentation(this);
        new AutoImportOnOneMatchOption().createVisualPresentation(this);
    }
    
    /**
     * Save any options the user has changed.
     */
    public void saveChanges() {
        //Save the search method that the user has selected
        jEdit.setProperty("jimporter.searchmethod", searchMethod);
    }
    
    /**
     * Update the search method when the user clicks on a radio button.
     */
    class SearchMethodRadioListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            searchMethod = e.getActionCommand();
        }
    }
}

