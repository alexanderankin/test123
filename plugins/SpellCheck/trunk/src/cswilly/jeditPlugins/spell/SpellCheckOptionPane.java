/*
 * $Revision: 1.2 $
 * $Date: 2002-06-11 16:04:53 $
 * $Author: lio-sand $
 *
 * Copyright (C) 2001 C. Scott Willy
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

package cswilly.jeditPlugins.spell;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.File;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


public class SpellCheckOptionPane
  extends AbstractOptionPane
{
  private JButton    _fileChooser;
  private JLabel     _aspellExeFilenameLabel;
  private JTextField _aspellExeFilenameField;
  private JTextField _aspellMainLanguageField;

  public SpellCheckOptionPane()
  {
    super( SpellCheckPlugin.PLUGIN_NAME );
  }

  public void _init()
  {
    String  aspellExeFilename = jEdit.getProperty( SpellCheckPlugin.ASPELL_EXE_PROP );
    if( aspellExeFilename == null )
      aspellExeFilename = "";

    this._aspellExeFilenameLabel = new JLabel();
    this._aspellExeFilenameLabel.setText( jEdit.getProperty( "options.SpellCheck.aspellExe" ) );

    addComponent( _aspellExeFilenameLabel );

    this._aspellExeFilenameField = new JTextField( 25 );
    this._aspellExeFilenameField.setText( aspellExeFilename );
    this._fileChooser = new JButton( jEdit.getProperty( "options.SpellCheck.fileChooser" ) );
    this._fileChooser.addActionListener( new FileActionHandler() );

    JPanel filePanel = new JPanel( new BorderLayout( 5, 0 ) );
    filePanel.add( this._aspellExeFilenameField,  BorderLayout.CENTER );
    filePanel.add( this._fileChooser, BorderLayout.EAST );

    addComponent( filePanel );

    addComponent( new JSeparator() );

    String  aspellMainLanguage = jEdit.getProperty( SpellCheckPlugin.ASPELL_LANG_PROP );
    if( aspellMainLanguage == null )
      aspellMainLanguage = "";

    this._aspellMainLanguageField = new JTextField();
    this._aspellMainLanguageField.setText( aspellMainLanguage );

    addComponent( jEdit.getProperty( "options.SpellCheck.aspellLang" ), _aspellMainLanguageField );
  }

  public void _save()
  {
    String  aspellExeFilename = _aspellExeFilenameField.getText().trim();
    jEdit.setProperty( SpellCheckPlugin.ASPELL_EXE_PROP, aspellExeFilename );

    String  aspellMainLanguage = _aspellMainLanguageField.getText().trim();
    jEdit.setProperty( SpellCheckPlugin.ASPELL_LANG_PROP, aspellMainLanguage );
  }

  private class FileActionHandler implements ActionListener
  {
    public void actionPerformed(ActionEvent evt) {
      String initialPath = jEdit.getProperty( SpellCheckPlugin.ASPELL_EXE_PROP );

      String[] paths = GUIUtilities.showVFSFileDialog( null, initialPath, JFileChooser.OPEN_DIALOG, false );

      if (paths != null)
        SpellCheckOptionPane.this._aspellExeFilenameField.setText(paths[0]);
    }
  }

}
