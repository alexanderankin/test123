/*
 *  JImporterOptionPane.java - Plugin for add java imports to the top of a java file.
 *  Copyright (C) 2002 Matthew Flower (MattFlower@yahoo.com)
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
package jimporter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;
import jimporter.classpath.Classpath;
import jimporter.options.OptionSaveListener;
import jimporter.searchmethod.SearchMethod;
import jimporter.sorting.SortCaseInsensitiveOption;
import jimporter.sorting.SortOnImportOption;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * This class constructs the option pane that is used to collect information
 * about how to set up JImporter. It also is responsible for saving that
 * information to the jEdit properties file.
 *
 * @author    Matthew Flower
 */
public class JImporterOptionPane extends AbstractOptionPane {
    private ButtonGroup classPathOptions;
    private JTextArea jImporterClasspath;
    private ButtonGroup searchMethodOptions;
    private JLabel useBruteForceMethodDescription;

    private String classpathSource = Classpath.getCurrent().getUniqueIdentifier();
    private String searchMethod = SearchMethod.getCurrent().getUniqueIdentifier();
    private boolean autoSearchAtPoint = false;
    private boolean appendRTJarToClasspath = true;
    private boolean autoImportOnOneMatch = false;
    

    /**
     * Public constructor.
     */
    public JImporterOptionPane() {
        super("jimporter");
    }

    /**
     * Used to initialize all of the components that will appear on the
     * JImporter options page.
     */
    public void _init() {
        //
        //Classpath Options
        //
        classPathOptions = new ButtonGroup();
        ClasspathRadioListener cpListener = new ClasspathRadioListener();
        SearchMethodRadioListener smListener = new SearchMethodRadioListener();
        AutoSearchAtPointListener asapListener = new AutoSearchAtPointListener();
        AddRTJarToClasspathListener artcListener = new AddRTJarToClasspathListener();
        AutoImportOnOneMatchListener aiomListener = new AutoImportOnOneMatchListener();

        //Add all of the possible classpath options to the dialog
        addSeparator("options.jimporter.classpath.label");
        Iterator it = Classpath.getClasspaths().iterator();
        while (it.hasNext()) {
            Classpath currentClasspathObject = (Classpath) it.next();

            //Construct the radio button based on the classpath object
            JRadioButton currentRadioButton = new JRadioButton(currentClasspathObject.getLabel());
            currentRadioButton.setActionCommand(currentClasspathObject.getUniqueIdentifier());
            currentRadioButton.setSelected(Classpath.getCurrent().equals(currentClasspathObject));
            currentRadioButton.addActionListener(cpListener);

            //Make sure all of the classpath buttons are in the same radio button grouping
            classPathOptions.add(currentRadioButton);

            //Add the current radio button to the dialog box
            addComponent(currentRadioButton);

            //Kludge (sorry) if the importer is the "Use this classpath"
            if (currentClasspathObject.equals(Classpath.USE_SPECIFIED_CLASSPATH)) {
                //Textarea to enter the "use this classpath" classpath
                jImporterClasspath = new JTextArea(4, 55);
                jImporterClasspath.setLineWrap(true);
                jImporterClasspath.setWrapStyleWord(false);
                jImporterClasspath.setText(jEdit.getProperty("jimporter.classpath.usespecified"));
                addComponent(jImporterClasspath);
            }
        }
        
        //Add option to append rt.jar to the classpath
        JCheckBox appendRTJarToClasspathCheckbox = new JCheckBox(jEdit.getProperty(
            Classpath.APPEND_RT_JAR_LABEL_PROPERTY));
        appendRTJarToClasspath = Classpath.isAppendRTJar();
        appendRTJarToClasspathCheckbox.setSelected(appendRTJarToClasspath);
        appendRTJarToClasspathCheckbox.addItemListener(artcListener);
        addComponent(appendRTJarToClasspathCheckbox);
        

        //Search method options
        addSeparator("options.jimporter.searchmethod.label");
        searchMethodOptions = new ButtonGroup();
        it = SearchMethod.getSearchMethods().iterator();

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
        
        JCheckBox autoSearchAtPointCheckbox = new JCheckBox(jEdit.getProperty(
            JEditClassImporter.AUTO_SEARCH_AT_POINT_LABEL));
        autoSearchAtPoint = JEditClassImporter.isAutoSearchAtPoint();
        autoSearchAtPointCheckbox.setSelected(autoSearchAtPoint);
        autoSearchAtPointCheckbox.addItemListener(asapListener);
        addComponent(autoSearchAtPointCheckbox);
        
        JCheckBox autoImportOnOneMatchCheckbox = new JCheckBox(jEdit.getProperty(
            JEditClassImporter.AUTO_IMPORT_ON_MATCH_LABEL));
        autoImportOnOneMatch = JEditClassImporter.isAutoImportOnOneMatch();
        autoImportOnOneMatchCheckbox.setSelected(autoImportOnOneMatch);
        autoImportOnOneMatchCheckbox.addItemListener(aiomListener);
        addComponent(autoImportOnOneMatchCheckbox);
        
        addSeparator("options.jimporter.sorting.label");
        new SortOnImportOption().createVisualPresentation(this);
        new SortCaseInsensitiveOption().createVisualPresentation(this);
    }

    /**
     * This method saves out any information modified in the JImporter option
     * page.
     */
    public void _save() {
        //Figure out which classpath type the user has selected
        Classpath selectedClasspathType = Classpath.getForID(classpathSource);
        
        //If the selected classpath type requires additional handling, do it.
        if (selectedClasspathType.equals(Classpath.USE_SPECIFIED_CLASSPATH)) {
            selectedClasspathType.setClasspath(jImporterClasspath.getText());
        }
        
        //Save the classpath that the user has selected
        Classpath.setCurrent(selectedClasspathType);
        
        //Save the search method that the user has selected
        jEdit.setProperty("jimporter.searchmethod", searchMethod);
        
        //Save whether user wants to automatically search when importing the
        //class at point.
        JEditClassImporter.setAutoSearchAtPoint(autoSearchAtPoint);
        
        //Save whether user wants to automatically append the rt.jar location to
        //his or her classpath.
        Classpath.setAppendRTJar(appendRTJarToClasspath);
        
        //Set whether user wants to import automatically if only one match exists
        JEditClassImporter.setAutoImportOnOneMatch(autoImportOnOneMatch);
        
        //Let all of the listeners know that a change has occurred
        fireSaveChanges();
    }
    
    EventListenerList saveListeners = new EventListenerList();
    
    public void addSaveListener(OptionSaveListener l) {
        saveListeners.add(OptionSaveListener.class, l);
    }
    
    public void removeSaveListener(OptionSaveListener l) {
        saveListeners.remove(OptionSaveListener.class, l);
    }
    
    public OptionSaveListener[] getSaveListeners() {
        return (OptionSaveListener[])saveListeners.getListeners(OptionSaveListener.class);
    }
    
    private void fireSaveChanges() {
        OptionSaveListener[] listeners = (OptionSaveListener[])saveListeners.getListeners(OptionSaveListener.class);

        for (int i = 0; i < listeners.length; i++) {
            listeners[i].saveChanges();
        }
    }
    

    /**
     * Listener used to identify which classpath JImporter should used as
     * determined by when a user clicks on a radio button.
     */
    class ClasspathRadioListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            classpathSource = e.getActionCommand();
        }
    }

    /**
     * Update the search method when the user clicks on a radio button.
     */
    class SearchMethodRadioListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            searchMethod = e.getActionCommand();
        }
    }
    
    class AutoSearchAtPointListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {                
            autoSearchAtPoint = (e.getStateChange() == ItemEvent.SELECTED);
        }
    }
    
    class AddRTJarToClasspathListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            appendRTJarToClasspath = (e.getStateChange() == ItemEvent.SELECTED);
        }
    }
    
    class AutoImportOnOneMatchListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            autoImportOnOneMatch = (e.getStateChange() == ItemEvent.SELECTED);
        }
    }
}

