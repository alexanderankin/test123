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

import cswilly.spell.FileSpellChecker;
import cswilly.spell.SpellException;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
import java.io.*;
import java.util.List;
import java.util.Vector;

public class SpellCheckPlugin
  extends EditPlugin
{
  public static final String PLUGIN_NAME             = "Spell Check";
  public static final String CHECK_SELECTION_ACTION  = "spell-check-selection";
  public static final String ASPELL_EXE_PROP         = "spell-check-aspell-exe";

  private static FileSpellChecker _fileSpellChecker = null;

  /**
  * Method called by jEdit to initialize the plugin.
  */
  //public void start() {}

  /**
  * Method called by jEdit before exiting. Usually, nothing
  * needs to be done here.
  */
  //public void stop() {}

  /**
  * Method called every time a view is created to set up the
  * Plugins menu. Menus and menu items should be loaded using the
  * methods in the GUIUtilities class, and added to the list.
  * @param menuItems Add menuitems here
  */
  public void createMenuItems(Vector menuItems)
  {
    menuItems.addElement( GUIUtilities.loadMenuItem( CHECK_SELECTION_ACTION ) );
  }

  /**
  * Method called every time the plugin options dialog box is
  * displayed. Any option panes created by the plugin should be
  * added here.
  * @param optionsDialog The plugin options dialog box
  *
  * @see OptionPane
  * @see OptionsDialog#addOptionPane(OptionPane)
  */
  public void createOptionPanes(OptionsDialog optionsDialog)
  {
    optionsDialog.addOptionPane( new JEditOptionPane() );
  }

  /**
  * Displays the 'hello world' dialog box. This method is called
  * by the hello-world action, defined in actions.xml.
  */
  public static void showSpellDialog(View view)
  {
    JEditTextArea textArea = view.getTextArea();
    String selectedText =  textArea.getSelectedText();
    if( selectedText == null )
      return;

    String checkedText = checkBuffer( view, selectedText );
    if( checkedText == null )
      return;

      textArea.setSelectedText( checkedText );
  }


  private static
  String checkBuffer( View view, String buffer )
  {
    String checkedBuffer;
    FileSpellChecker checker = null;

    try
    {
      BufferedReader input  = new BufferedReader( new StringReader( buffer ) );
      StringWriter stringWriter = new StringWriter( buffer.length() );
      BufferedWriter output = new BufferedWriter( stringWriter );

      checker = _getFileSpellChecker();

      boolean checkingNotCanceled =  checker.checkFile( input, output );

      input.close();
      output.close();

      if( checkingNotCanceled )
        checkedBuffer = stringWriter.toString();
      else
        checkedBuffer = null;
    }
    catch( SpellException e )
    {
      Log.log(Log.ERROR, SpellCheckPlugin.class, "Error spell checking (Aspell).");
      Log.log(Log.ERROR, SpellCheckPlugin.class, e);
      String msg = "Cannot check selection.\nError (Aspell) is: " + e.getMessage();
      GUIUtilities.message( view, msg, null);
      checkedBuffer = null;
    }
    catch( IOException e )
    {
      Log.log(Log.ERROR, SpellCheckPlugin.class, "Error spell checking (plugin).");
      Log.log(Log.ERROR, SpellCheckPlugin.class, e);
      String msg = "Cannot check selection.\nError (plugin) is: " + e.getMessage();
      GUIUtilities.message( view, msg, null);
      checkedBuffer = null;
    }

// ??? May this a configurable option
//    if( checker != null )
//      checker.stop();

    return checkedBuffer;
  }

  private static
  FileSpellChecker _getFileSpellChecker()
  {
    //String  aspellExeFilename = "O:\\local\\aspell\\aspell.exe";
    String  aspellExeFilename = getAspellExeFilename();

    if( _fileSpellChecker == null )
      _fileSpellChecker = new FileSpellChecker( aspellExeFilename );
    else
      if( !aspellExeFilename.equals( _fileSpellChecker.getAspellExeFilename() ) )
      {
        _fileSpellChecker.stop();
        _fileSpellChecker = new FileSpellChecker( aspellExeFilename );
      }

    return _fileSpellChecker;
  }

  private static
  String getAspellExeFilename()
  {
    String  aspellExeFilename = jEdit.getProperty( ASPELL_EXE_PROP );

    if( aspellExeFilename == null ||
        aspellExeFilename.equals("") )
    {
      aspellExeFilename = "aspell.exe";
    }

    return aspellExeFilename;
  }

}
