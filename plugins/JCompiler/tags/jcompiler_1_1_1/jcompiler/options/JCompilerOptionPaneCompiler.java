/*
 * JCompilerOptionPaneCompiler.java - plugin options pane for JCompiler - compiler options
 * (c) 1999, 2000 Kevin A. Burton and Aziz Sharif
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */

package jcompiler.options;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;


/**
 * This is the option pane that jEdit displays for JCompiler's
 * compiler plugin options.
 */
public class JCompilerOptionPaneCompiler
		 extends AbstractOptionPane
		 implements ActionListener
{

	public JCompilerOptionPaneCompiler() {
		super("jcompiler.compiler");
	}


	public void _init() {
		// "Generate debug info (-g)"
		genDebug = new JCheckBox(jEdit.getProperty("options.jcompiler.gendebug"));
		genDebug.setSelected(jEdit.getBooleanProperty("jcompiler.gendebug", true));
		addComponent(genDebug);

		// "Generate optimized code (-O)"
		genOptimized = new JCheckBox(jEdit.getProperty("options.jcompiler.genoptimized"));
		genOptimized.setSelected(jEdit.getBooleanProperty("jcompiler.genoptimized", false));
		addComponent(genOptimized);

		// "Warn about use of deprecated API (-deprecation)"
		showDeprecation = new JCheckBox(jEdit.getProperty("options.jcompiler.showdeprecated"));
		showDeprecation.setSelected(jEdit.getBooleanProperty("jcompiler.showdeprecated", true));
		addComponent(showDeprecation);

		addComponent(Box.createVerticalStrut(20));

		// "Set the base directory of your current project here (or leave it empty)"
		addComponent(new JLabel(jEdit.getProperty("options.jcompiler.basepath.description1")));

		// "$basepath"
		basePath = new JTextField();
		String basePathValue = jEdit.getProperty("jcompiler.basepath");
		basePath.setText(basePathValue == null ? "" : basePathValue);
		pickBasePathButton = new JButton(pickIcon);
		pickBasePathButton.setMargin(new Insets(0,0,0,0));
		pickBasePathButton.setToolTipText(jEdit.getProperty("options.jcompiler.pick.tooltip"));
		pickBasePathButton.addActionListener(this);
		JPanel basePathPanel = new JPanel(new BorderLayout());
		basePathPanel.add(basePath, BorderLayout.CENTER);
		basePathPanel.add(pickBasePathButton, BorderLayout.EAST);
		addComponent(jEdit.getProperty("options.jcompiler.basepath"), basePathPanel);

		addComponent(Box.createVerticalStrut(10));

		// "You may use the $basepath variable in the following paths:"
		addComponent(new JLabel(jEdit.getProperty("options.jcompiler.basepath.description2")));

		// "Source path" (only on JDK 1.2 or higher)
		if (!isOldJDK) {
			srcPath = new JTextField();
			String srcPathValue = jEdit.getProperty("jcompiler.sourcepath");
			srcPath.setText(srcPathValue == null ? "" : srcPathValue);
			srcPath.setPreferredSize(new Dimension(270, srcPath.getPreferredSize().height));
			addComponent(jEdit.getProperty("options.jcompiler.sourcepath"), srcPath);
		}

		// "Required library path"
		libPath = new JTextField();
		String libPathValue = jEdit.getProperty("jcompiler.libpath");
		libPath.setText(libPathValue == null ? "" : libPathValue);
		libPath.setPreferredSize(new Dimension(270, libPath.getPreferredSize().height));
		addComponent(jEdit.getProperty("options.jcompiler.libpath"), libPath);

		// "Class path"
		classPath = new JTextField();
		String classPathValue = jEdit.getProperty("jcompiler.classpath");
		classPath.setText(classPathValue == null ? "" : classPathValue);
		classPath.setPreferredSize(new Dimension(270, classPath.getPreferredSize().height));
		addComponent(jEdit.getProperty("options.jcompiler.classpath"), classPath);

		// Output directory text field (+ select button)
		String output = null;
		if (jEdit.getBooleanProperty("jcompiler.specifyoutputdirectory"))
			output = jEdit.getProperty("jcompiler.outputdirectory");

		outputDirectory = new JTextField();
		outputDirectory.setText(output == null ? "" : output);
		pickDirectory = new JButton(pickIcon);
		pickDirectory.setMargin(new Insets(0,0,0,0));
		pickDirectory.setToolTipText(jEdit.getProperty("options.jcompiler.pick.tooltip"));
		pickDirectory.addActionListener(this);
		JPanel outputPanel = new JPanel(new BorderLayout());
		outputPanel.add(outputDirectory, BorderLayout.CENTER);
		outputPanel.add(pickDirectory, BorderLayout.EAST);
		addComponent(jEdit.getProperty("options.jcompiler.outputDirectory"), outputPanel);

		// "Add package of current sourcefile to CLASSPATH"
		addPkg2CP = new JCheckBox(jEdit.getProperty("options.jcompiler.addpkg2cp"));
		addPkg2CP.setSelected(jEdit.getBooleanProperty("jcompiler.addpkg2cp", true));
		addComponent("", addPkg2CP);

		addComponent(Box.createVerticalStrut(20));

		// "Other options:"
		otherOptions = new JTextField();
		String options = jEdit.getProperty("jcompiler.otheroptions");
		otherOptions.setText(options == null ? "" : options);
		addComponent(jEdit.getProperty("options.jcompiler.otheroptions"), otherOptions);
	}


	public void _save() {
		jEdit.setBooleanProperty("jcompiler.genDebug", genDebug.isSelected());
		jEdit.setBooleanProperty("jcompiler.genOptimized", genOptimized.isSelected());
		jEdit.setBooleanProperty("jcompiler.showdeprecated", showDeprecation.isSelected());
		jEdit.setBooleanProperty("jcompiler.addpkg2cp", addPkg2CP.isSelected());

		jEdit.setProperty("jcompiler.basepath", basePath.getText().trim());
		jEdit.setProperty("jcompiler.libpath", libPath.getText().trim());
		jEdit.setProperty("jcompiler.classpath", classPath.getText().trim());
		jEdit.setProperty("jcompiler.otheroptions", otherOptions.getText().trim());

		if (!isOldJDK)
			jEdit.setProperty("jcompiler.sourcepath", srcPath.getText().trim());

		String outputDir = outputDirectory.getText().trim();
		jEdit.setBooleanProperty("jcompiler.specifyoutputdirectory", outputDir.length() > 0);
		jEdit.setProperty("jcompiler.outputdirectory", outputDir);
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == pickDirectory) {
			File file = chooseDirectory();
			setDirectoryText(file, outputDirectory);
		}
		else if (e.getSource() == pickBasePathButton) {
			File file = chooseDirectory();
			setDirectoryText(file, basePath);
		}
	}


	/**
	 * Display a file chooser dialog for directories only.
	 *
	 * @return the File if one was selected, or null if user clicked cancel.
	 */
	private File chooseDirectory() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int retVal = chooser.showOpenDialog(this);

		if (retVal == JFileChooser.APPROVE_OPTION)
			return chooser.getSelectedFile();
		else
			return null;
	}


	private void setDirectoryText(File file, JTextField textField) {
		if (file != null) {
			try {
				String dirName = file.getCanonicalPath();
				textField.setText(dirName);
			}
			catch(IOException e) {
				Log.log(Log.ERROR, this, "Something went wrong getting the canonical path for directory " + file);
                Log.log(Log.ERROR, this, e);
			}
		}
	}


	private JCheckBox genDebug;
	private JCheckBox genOptimized;
	private JCheckBox showDeprecation;
	private JCheckBox addPkg2CP;
	private JLabel cpLabel;
	private JTextField outputDirectory;
	private JTextField otherOptions;
	private JButton pickDirectory;
	private JButton pickBasePathButton;
	private JTextField basePath;
	private JTextField srcPath;
	private JTextField libPath;
	private JTextField classPath;


	/** true, if JDK version is less than 1.2. */
	private final static boolean isOldJDK = (MiscUtilities.compareVersions(System.getProperty("java.version"), "1.2") < 0);

	private static Icon pickIcon = null;


	static {
		URL url = JCompilerOptionPaneCompiler.class.getResource("DirOpen.gif");
		if (url != null)
			pickIcon = new ImageIcon(url);
		else
			Log.log(Log.ERROR, JCompilerOptionPaneCompiler.class, "Error fetching image DirOpen.gif");
	}


}

