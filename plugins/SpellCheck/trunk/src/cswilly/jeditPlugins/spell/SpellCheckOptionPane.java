/*
 * $Revision: 1.1 $
 * $Date: 2002-06-07 14:46:21 $
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

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.io.File;

public class SpellCheckOptionPane
  extends AbstractOptionPane
{
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

    _aspellExeFilenameField = new JTextField( aspellExeFilename, 25 );
    this.addComponent("Aspell executable filename (full path and filename)", _aspellExeFilenameField );

    String  aspellMainLanguage = jEdit.getProperty( SpellCheckPlugin.ASPELL_LANG_PROP );
    if( aspellMainLanguage == null )
      aspellMainLanguage = "";

    _aspellMainLanguageField = new JTextField( aspellMainLanguage, 25 );
    this.addComponent("lang dictionary (e.g. fr_FR or empty for default)", _aspellMainLanguageField );
  }

  public void _save()
  {
    String  aspellExeFilename = _aspellExeFilenameField.getText().trim();
    if( !aspellExeFilename.equals( "" ) )
    {
      File aspellExeFile = new File( aspellExeFilename );
      if ( aspellExeFile.exists() )
        jEdit.setProperty( SpellCheckPlugin.ASPELL_EXE_PROP, aspellExeFilename );
    }

    String  aspellMainLanguage = _aspellMainLanguageField.getText().trim();
    jEdit.setProperty( SpellCheckPlugin.ASPELL_LANG_PROP, aspellMainLanguage );
  }

}
