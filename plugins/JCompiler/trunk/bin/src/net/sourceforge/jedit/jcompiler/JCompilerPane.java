/*
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

package net.sourceforge.jedit.jcompiler;
 
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.text.Element;
import java.lang.*;


/**

This is the option pane that jEdit displays for Plugin Options.

*/
public class JCompilerPane extends AbstractOptionPane implements ActionListener, ChangeListener {

    JPanel misc             = new JPanel();
    JPanel classpath        = new JPanel();
    JPanel compilerOptions  = new JPanel();
    
	private JCheckBox showErrorInSeparateWindow = new JCheckBox("Show errors in new window");
	private JCheckBox autoSaveBufferOnJavaCompile = new JCheckBox("Auto save current buffer on Java Compile");
	private JCheckBox autoSaveAllBuffersOnJavaCompile = new JCheckBox("Auto save all buffers on Java Compile");
	private JCheckBox autoSaveBufferOnJavaPkgCompile = new JCheckBox("Auto save current buffer on Java Pkg Compile");
	private JCheckBox autoSaveAllBuffersOnJavaPkgCompile = new JCheckBox("Auto save all buffers on Java Pkg Compile");

	private JCheckBox specifyOutputDirectory = new JCheckBox("Specify where to place generated class files");
	private JCheckBox showDeprecation = new JCheckBox("Output source locations where deprecated APIs are used");
    
	//private JCheckBox smartPkgCompile = new JCheckBox("Perform smart compilation for Java Pkg Compile");
    
	private JCheckBox useJavaCP = new JCheckBox("Use CLASSPATH defined when running jEdit");
	private JCheckBox addPkg2CP = new JCheckBox("Automatically add compiling src file's package to CLASSPATH");	
	private JTextArea newCP = new JTextArea();
	private JLabel cpLabel = new JLabel();

    private JTextField outputDirectory = new JTextField();
    private JButton pickDirectory = new JButton( "choose" );
   
	
	public JCompilerPane()
	{

		super("jcompiler");

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();


		setLayout( gridbag );
        
        //************************* MISC options

        misc.setBorder( BorderFactory.createTitledBorder("Misc:") );
        misc.setLayout(new GridLayout (5, 1));
		
		showErrorInSeparateWindow.setSelected( jEdit.getProperty("jcompiler.showerrorwindow").equals("T") );
		showErrorInSeparateWindow.setEnabled(false);
		
		autoSaveBufferOnJavaCompile.setSelected(jEdit.getProperty("jcompiler.javacompile.autosave").equals("T"));
		autoSaveAllBuffersOnJavaCompile.setSelected(jEdit.getProperty("jcompiler.javacompile.autosaveall").equals("T"));
		autoSaveBufferOnJavaPkgCompile.setSelected(jEdit.getProperty("jcompiler.javapkgcompile.autosave").equals("T"));
		autoSaveAllBuffersOnJavaPkgCompile.setSelected(jEdit.getProperty("jcompiler.javapkgcompile.autosaveall").equals("T"));
		//smartPkgCompile.setSelected(jEdit.getProperty("jcompiler.javapkgcompile.smartcompile").equals("T"));
		//smartPkgCompile.setToolTipText("Will compile only those sources which are newer than their class files");
        

		useJavaCP.setSelected(jEdit.getProperty("jcompiler.usejavacp").equals("T"));
		addPkg2CP.setSelected(jEdit.getProperty("jcompiler.addpkg2cp").equals("T"));
		
		newCP.setLineWrap(true);
		
		misc.add(showErrorInSeparateWindow);
		misc.add(autoSaveBufferOnJavaCompile);
		misc.add(autoSaveAllBuffersOnJavaCompile);
		misc.add(autoSaveBufferOnJavaPkgCompile);
		misc.add(autoSaveAllBuffersOnJavaPkgCompile);   
		//misc.add(smartPkgCompile);


        c.insets = new Insets(2,2,2,2);
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(misc, c);
        
        this.add(misc);

        //************************* Compiler Options


        showDeprecation.setSelected(jEdit.getProperty("jcompiler.showdeprecated").equals("T"));
        specifyOutputDirectory.setSelected(jEdit.getProperty( "jcompiler.specifyoutputdirectory").equals("T") );

        enableOutputDirectory( jEdit.getProperty( "jcompiler.specifyoutputdirectory").equals("T") );

        String output = jEdit.getProperty("jcompiler.outputdirectory");
        if (output != null) {
            outputDirectory.setText( output );
        }
        
        
        compilerOptions.setBorder( BorderFactory.createTitledBorder("Compiler options:") );        
        compilerOptions.setLayout( new GridLayout(4,1) );
        
        specifyOutputDirectory.addChangeListener(this);

        compilerOptions.add(showDeprecation);
        compilerOptions.add(specifyOutputDirectory);
        

        






        outputDirectory.setText( jEdit.getProperty( "jcompiler.outputdirectory" ) );
        JLabel label = new JLabel( "Output Directory: " );
        
       
        compilerOptions.add( label );

        
        JPanel directoryPanel = new JPanel(new BorderLayout());


        pickDirectory.addActionListener(this);
        directoryPanel.add(outputDirectory, BorderLayout.CENTER);
        directoryPanel.add(pickDirectory, BorderLayout.EAST);
        compilerOptions.add(directoryPanel, BorderLayout.CENTER);
        
        
        
        
        
        
        
        c.gridy = 1;
        gridbag.setConstraints(compilerOptions, c);
        
        this.add(compilerOptions);
        

        //*************************  Classpath Options
        
        classpath.setBorder( BorderFactory.createTitledBorder("Classpath:") );
        classpath.setLayout(new BorderLayout());
		
		JPanel c1 = new JPanel();
		c1.setLayout(new GridLayout(3,1));
		c1.add(addPkg2CP);
		c1.add(useJavaCP);
		c1.add(cpLabel);
		
		JPanel c2 = new JPanel();
		c2.setLayout(new BorderLayout());
		c2.add(c1, BorderLayout.NORTH);
	
		c2.add(new JScrollPane( newCP ), BorderLayout.CENTER);		
		
		enableVisibility();
		
		useJavaCP.addActionListener(this);
        
        classpath.add(c2);


        c.gridy = 2;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(classpath, c);


		this.add(classpath);
	}
	
	public void actionPerformed(ActionEvent e) {

        if (e.getSource() == pickDirectory) {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //chooser.setTitle( "Specify output directory" );
        int retVal = chooser.showDialog(this, "OK");
        if(retVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if(file != null) {
                try {
                    String dirName = file.getCanonicalPath();
                    outputDirectory.setText(dirName);
                } catch(IOException donothing) {
                    // shouldn't happen
                }
            }
        }


            
        } else {
    		enableVisibility();
            
        }
	}
	

    private void enableOutputDirectory(boolean enable) {
            outputDirectory.setEnabled(enable);
            pickDirectory.setEnabled(enable);
    }


    public void stateChanged(ChangeEvent e) {

        if ( specifyOutputDirectory.isSelected() ) {
            enableOutputDirectory(true);
        } else {
            enableOutputDirectory(false);
        }

    }

	private void enableVisibility()
	{
		if (useJavaCP.isSelected())
		{
			cpLabel.setText("Java System CLASSPATH:");
			newCP.setText(System.getProperty("java.class.path"));
			newCP.setEnabled(false);
		}
		else
		{
			cpLabel.setText("JCompiler CLASSPATH:");
			newCP.setText(jEdit.getProperty("jcompiler.classpath"));
			newCP.setEnabled(true);			
		}
	}
		
	public void save()
	{
		jEdit.setProperty("jcompiler.showerrorwindow", showErrorInSeparateWindow.isSelected() ? "T" : "F");
		jEdit.setProperty("jcompiler.javacompile.autosave", autoSaveBufferOnJavaCompile.isSelected() ? "T" : "F");
		jEdit.setProperty("jcompiler.javacompile.autosaveall", autoSaveAllBuffersOnJavaCompile.isSelected() ? "T" : "F");
		jEdit.setProperty("jcompiler.javapkgcompile.autosave", autoSaveBufferOnJavaPkgCompile.isSelected() ? "T" : "F");
		jEdit.setProperty("jcompiler.javapkgcompile.autosaveall", autoSaveAllBuffersOnJavaPkgCompile.isSelected() ? "T" : "F");		
		//jEdit.setProperty("jcompiler.javapkgcompile.smartcompile", smartPkgCompile.isSelected() ? "T" : "F");

		jEdit.setProperty("jcompiler.showdeprecated", showDeprecation.isSelected() ? "T" : "F");		
		jEdit.setProperty("jcompiler.specifyoutputdirectory", specifyOutputDirectory.isSelected() ? "T" : "F");		
        
        if ( specifyOutputDirectory.isSelected() ) {
    		jEdit.setProperty("jcompiler.outputdirectory", outputDirectory.getText() );
        }




		jEdit.setProperty("jcompiler.usejavacp", useJavaCP.isSelected() ? "T" : "F");
		jEdit.setProperty("jcompiler.addpkg2cp", addPkg2CP.isSelected() ? "T" : "F");	
		if (useJavaCP.isSelected() == false)
		{
			jEdit.setProperty("jcompiler.classpath", newCP.getText());
		}
	}
}
