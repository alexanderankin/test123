/*
 * $Revision: 1.2 $
 * $Date: 2002-06-07 14:53:28 $
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
  public static final String PLUGIN_NAME             = "SpellCheck";
  public static final String CHECK_SELECTION_ACTION  = "spell-check-selection";
  public static final String ASPELL_EXE_PROP         = "spell-check-aspell-exe";
  public static final String ASPELL_LANG_PROP        = "spell-check-aspell-lang";

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
    optionsDialog.addOptionPane( new SpellCheckOptionPane() );
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
    {
      textArea.selectAll();
      selectedText = textArea.getText();
    }

    String checkedText = checkBuffer( view, selectedText );

    if ( checkedText == null )
      return;

    if ( checkedText.equals(selectedText) )
      view.getStatus().setMessageAndClear("Spell check terminated with no error");
    else
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

      // Restore buffer correct line separator if any
      if ( buffer.endsWith("\n") )
        output.write( '\n' );

      input.close();
      output.close();

      if ( checkingNotCanceled )
        checkedBuffer = stringWriter.toString();
      else
        checkedBuffer = null;
    }
    catch( SpellException e )
    {
      Log.log(Log.ERROR, SpellCheckPlugin.class, "Error spell checking (Aspell).");
      Log.log(Log.ERROR, SpellCheckPlugin.class, e);
      Object[] args = { new String (e.getMessage()) };
      GUIUtilities.error( view, "spell-check-error", args);
      checkedBuffer = null;
    }
    catch( IOException e )
    {
      Log.log(Log.ERROR, SpellCheckPlugin.class, "Error spell checking (plugin).");
      Log.log(Log.ERROR, SpellCheckPlugin.class, e);
      Object[] args = { new String (e.getMessage()) };
      GUIUtilities.error( view, "spell-check-error", args);
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
    String  aspellMainLanguage = getAspellMainLanguage();

    if( _fileSpellChecker == null )
      _fileSpellChecker = new FileSpellChecker( aspellExeFilename, aspellMainLanguage );
    else
      if( !aspellExeFilename.equals( _fileSpellChecker.getAspellExeFilename() )
       || !aspellMainLanguage.equals( _fileSpellChecker.getAspellMainLanguage() ) )
      {
        _fileSpellChecker.stop();
        _fileSpellChecker = new FileSpellChecker( aspellExeFilename, aspellMainLanguage );
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

  private static
  String getAspellMainLanguage()
  {
    String  aspellMainLanguage = jEdit.getProperty( ASPELL_LANG_PROP );

    if( aspellMainLanguage == null ||
        aspellMainLanguage.equals("") )
    {
      aspellMainLanguage = "";
    }

    return aspellMainLanguage;
  }

}
