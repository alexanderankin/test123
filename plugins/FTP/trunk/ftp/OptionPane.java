/*
 * OptionPane.java - Plugin Options Pane for FTP Plugin
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=sidekick:collapseFolds=1:
 *
 * Copyright © 2013 Alan Ezust
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

package ftp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import org.gjt.sp.jedit.AbstractOptionPane;
import common.gui.FileTextField;
import org.gjt.sp.jedit.jEdit;


/** FTP plugin options pane
*/
@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane implements ActionListener {
	
	JCheckBox storePasswords;	
	JCheckBox useKeyFile;
	FileTextField keyFile;
	JCheckBox enableCompression;
	JCheckBox disableWeakCrypto;
	
	
	public OptionPane() {
		super("ftp");
	}
	
	
	protected void _init() {						
		storePasswords = new JCheckBox(jEdit.getProperty("options.ftp.savePasswords"), jEdit.getBooleanProperty("vfs.ftp.storePassword"));
		storePasswords.addActionListener(this);
		addComponent(storePasswords);
		
		useKeyFile = new JCheckBox(jEdit.getProperty("options.ftp.useKeyFile"), jEdit.getBooleanProperty("ftp.useKeyFile"));
		useKeyFile.setToolTipText(jEdit.getProperty("options.ftp.useKeyFile.tooltip"));
		useKeyFile.addActionListener(this);
				
		keyFile = new FileTextField(jEdit.getProperty("ftp.passKeyFile"), false);
		keyFile.setToolTipText(jEdit.getProperty("options.ftp.useKeyFile.tooltip"));
		addComponent(useKeyFile , keyFile);
		
		disableWeakCrypto = new JCheckBox(jEdit.getProperty("options.ftp.disableWeakCrypto"), jEdit.getBooleanProperty("ftp.disableWeakCrypto"));
		addComponent(disableWeakCrypto);
		
		actionPerformed(null);
		
		enableCompression = new JCheckBox(jEdit.getProperty("options.sftp.enableCompression"),
				jEdit.getBooleanProperty("vfs.sftp.compression"));
		addComponent(enableCompression);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		useKeyFile.setEnabled(storePasswords.isSelected());
		keyFile.setEnabled(useKeyFile.isSelected() && storePasswords.isSelected());		
	}
	
	protected void _save() {		

		jEdit.setBooleanProperty("ftp.useKeyFile", useKeyFile.isSelected());
		jEdit.setBooleanProperty("ftp.disableWeakCrypto", disableWeakCrypto.isSelected());
		
		if (useKeyFile.isSelected()) {
			jEdit.setProperty("ftp.passKeyFile", keyFile.getTextField().getText());
		}
		jEdit.setBooleanProperty("vfs.ftp.storePassword", storePasswords.isSelected());
		jEdit.setBooleanProperty("vfs.sftp.compression", enableCompression.isSelected());
	}
}

