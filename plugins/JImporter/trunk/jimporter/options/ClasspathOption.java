/*
 *  ClasspathOption.java -   
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
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import jimporter.classpath.Classpath;
import org.gjt.sp.jedit.jEdit;

/**
 * This option class encapsulates the different options that users can use to 
 * specify a classpath that should be used to find classes to import.
 *
 * @author Matthew Flower
 * @since 0.3.0
 */
public class ClasspathOption extends JImporterOption implements OptionSaveListener {
    private ButtonGroup classPathOptions;
    private JTextArea jImporterClasspath;
    private String classpathSource = Classpath.getCurrent().getUniqueIdentifier();

    /**
     * Standard constructor.
     */
    public ClasspathOption() {
        super("classpath");
    }
    
    /**
     * Populate the option pane with all the information needed to set up 
     * classpaths.
     *
     * @param jiop a <code>JImporterOptionPane</code> value that we are going
     * to populate with values.
     */
    public void createVisualPresentation(JImporterOptionPane jiop) {
        classPathOptions = new ButtonGroup();
        ClasspathRadioListener cpListener = new ClasspathRadioListener();

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
            jiop.addComponent(currentRadioButton);

            //Kludge (sorry) if the importer is the "Use this classpath"
            if (currentClasspathObject.equals(Classpath.USE_SPECIFIED_CLASSPATH)) {
                //Textarea to enter the "use this classpath" classpath
                jImporterClasspath = new JTextArea(4, 55);
                jImporterClasspath.setLineWrap(true);
                jImporterClasspath.setWrapStyleWord(false);
                jImporterClasspath.setText(jEdit.getProperty("jimporter.classpath.usespecified"));
                jiop.addComponent(jImporterClasspath);
            }
        }
        
        //Add option to append rt.jar to the classpath
        new AppendRTJarToClasspathOption().createVisualPresentation(jiop);
        
        //Make sure we are notified when we save changes
        jiop.addSaveListener(this);
    }
    
    /**
     * Save any modifications that user has done in the options dialog box.
     */
    public void saveChanges() {
        //Figure out which classpath type the user has selected
        Classpath selectedClasspathType = Classpath.getForID(classpathSource);
        
        //If the selected classpath type requires additional handling, do it.
        if (selectedClasspathType.equals(Classpath.USE_SPECIFIED_CLASSPATH)) {
            selectedClasspathType.setClasspath(jImporterClasspath.getText());
        }
        
        //Save the classpath that the user has selected
        Classpath.setCurrent(selectedClasspathType);
    }
    
    /**
     * Listener used to identify which classpath JImporter should used as
     * determined by when a user clicks on a radio button.
     */
    class ClasspathRadioListener implements ActionListener {
        /**
         * This method is called when someone changes the currently selected
         * classpath radio button.
         *
         * @param e an <code>ActionEvent</code> class that contains information 
         * about the change of radio button.
         */
        public void actionPerformed(ActionEvent e) {
            classpathSource = e.getActionCommand();
        }
    }
}

