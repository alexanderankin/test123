/*
 * $Revision: 1.1 $
 * $Date: 2001-09-09 15:04:14 $
 * $Author: cswilly $
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
import java.io.File;

public class JEditOptionPane
  extends AbstractOptionPane
{
  private JTextField _aspellExeFilenameField;

  public JEditOptionPane()
  {
    super( SpellCheckPlugin.PLUGIN_NAME );
  }

  public void _init()
  {
    String  aspellExeFilename = jEdit.getProperty( SpellCheckPlugin.ASPELL_EXE_PROP );
    if( aspellExeFilename == null )
      aspellExeFilename = "";

    _aspellExeFilenameField = new JTextField( aspellExeFilename, 25 );
    this.addComponent("Aspell executable filename", _aspellExeFilenameField );
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
  }

}
