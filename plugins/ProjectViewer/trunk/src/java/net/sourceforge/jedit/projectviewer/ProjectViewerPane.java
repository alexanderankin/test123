package net.sourceforge.jedit.projectviewer;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.lang.*;


/**

This is the option pane that jEdit displays for Plugin Options.

*/
public class ProjectViewerPane extends AbstractOptionPane implements ActionListener {
	

    JComboBox projectCombo                      = new JComboBox();
    JPanel    options                           = new JPanel();
    
    
	public ProjectViewerPane() {
		super("projectviewer");
        
        this.setLayout(new BorderLayout() );
        this.options.setLayout( new BorderLayout() );
        
        ProjectViewer.getProjectCombo(this.projectCombo);

        this.add(projectCombo, BorderLayout.NORTH);
        this.add(options, BorderLayout.CENTER);

        options.add( this.getOptionPanel( null ) );

        projectCombo.addActionListener ( this );

	}



	public void actionPerformed(ActionEvent evt) {

        if (evt.getSource() == this.projectCombo) {

            
            Logger.log("...........", 9);
            
            this.options.setVisible(false);
            
            //this.options.removeAll();
            this.options = this.getOptionPanel( 
               ProjectResources.getProject( (String)projectCombo.getSelectedItem() ) );


            this.remove(this.options);
            this.add(options, BorderLayout.CENTER);

            this.options.setVisible(true);
             
            this.repaint();
             
         }
        
        
    }
    
    

	public void save() {

	}

    /**
    Returns a JPanel with information about this Projects options...  if project
    is null it assumes you mean all projects...
    
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    JPanel getOptionPanel(Project project) {
        
        JPanel options = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        options.setLayout(gridbag);        
        

        
        
        //------------ MISC Panel

        JPanel misc = new JPanel();
        misc.setBorder( BorderFactory.createTitledBorder("Misc:"));


        c.insets = new Insets(2,2,2,2);
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(misc, c);

        if (project != null) {
            misc.add(new JLabel("Project root:  "));
            
            JTextField rootfield = new JTextField(project.getRoot().get());
            //rootfield.setEnabled(false);
            misc.add(rootfield);
            
        } else {
            Project[] projects = ProjectResources.getProjects();

            misc.add(new JLabel("Number of Projects: " + projects.length) );

            /*
            
            for(int i = 0; i < projects.length;++i) {
                
                
                misc.add(new JLabel( projects[i].get() + 
                                     ": " + 
                                     ProjectResources.getFiles(projects[i]).length + 
                                     " files"));
            }

            */
            
        }

        
        options.add(misc);


        //------------ Compiler Panel        
        




        if (project != null) {

            JPanel compiler = new JPanel();
            compiler.setBorder( BorderFactory.createTitledBorder("Build Options:"));

            GridBagLayout compilerLayout = new GridBagLayout();

            GridBagConstraints compilerConstraints = new GridBagConstraints();

        
            compiler.setLayout(compilerLayout);


            JCheckBox compile = new JCheckBox("Build \"" + project.get() + "\"");


            //JComboBox combo = new JComboBox(ProjectResources.getFiles(project));
            
            compilerConstraints.gridy = 0;
            compilerConstraints.insets = new Insets(2,2,2,2);
            compilerLayout.setConstraints(compile, compilerConstraints);

            //compilerConstraints.gridy = 1;
            //compilerLayout.setConstraints(combo, compilerConstraints);
            
            compiler.add(compile);
            //compiler.add( combo );

            c.gridy = 1;
            gridbag.setConstraints(compiler, c);

            options.add(compiler);
            
            
        }    
        





        /*
        JButton save = new JButton("Save");
        save.setSize(new Dimension(25, 15));


        c.weightx = 0.0;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.SOUTHEAST;
        //c.gridheight = GridBagConstraints.REMAINDER;
        c.weightx = 2.0;
        c.weighty = 3.0;
        
        gridbag.setConstraints(save, c);
        options.add(save);

        */

        return options;
        
        
    }



    public static void main(String args[]) {

        ProjectViewerPane pvp = new ProjectViewerPane();



        JFrame frame = new JFrame();
        frame.getContentPane().add(pvp);
        frame.setSize(new Dimension(350, 600) );
        frame.setEnabled(true);
        frame.setVisible(true);
        frame.show();
        frame.toFront();        
    

    }
    
    

}
