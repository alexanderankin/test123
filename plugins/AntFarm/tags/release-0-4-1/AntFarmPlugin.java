/*
 * AntFarmPlugin.java - Ant build utility plugin for jEdit
 * Copyright (C) 2000 Chris Scott
 * Other contributors: Rick Gibbs
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999, 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/**
  @author Chris Scott, Rick Gibbs
*/

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.util.Vector;
import java.io.*;

/**
 * The 'Plugin' class is the interface between jEdit and the plugin.
 * Plugins can either extend EditPlugin or EBPlugin. EBPlugins have
 * the additional property that they receive EditBus messages.
 */

public class AntFarmPlugin extends EBPlugin
{
  /**
   * The 'name' of our dockable window.
   */
  public static final String NAME = "antfarm";

  private static DefaultErrorSource errorSource;

  static PrintStream out = System.out;
  static PrintStream err = System.err;

  private View theView;
  /**
   * Method called by jEdit to initialize the plugin.
   */
  public void start()
  {
    errorSource = new DefaultErrorSource("antfarm");
    EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST,errorSource);
    EditBus.addToBus(errorSource);

    // add our dockable to the dockables 'named list'
    EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST,NAME);

    // save System.out and err in case we need them
    //out = System.out;
    //err = System.err;
  }

  /**
   * Method called by jEdit before exiting. Usually, nothing
   * needs to be done here.
   */
  //public void stop() {}

  /**
   * Method called every time a view is created to set up the
   * Plugins menu. Menus and menu items should be loaded using the
   * methods in the GUIUtilities class, and added to the list.
   * @param menuItems Add the menu item here
   */
  public void createMenuItems(Vector menuItems)
  {
    menuItems.addElement(GUIUtilities.loadMenuItem("antfarm"));
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
  //public void createOptionPanes(OptionsDialog optionsDialog) {}

  /**
   * Handles a message sent on the EditBus. The default
   * implementation ignores the message.
   */
  public void handleMessage(EBMessage message)
  {
    /* upon receiving a CreateDockableWindow, we check if
     * the name of the requested window is 'hello-dockable',
     * and create it if it is so.
     */

    if(message instanceof CreateDockableWindow)
    {
      CreateDockableWindow cmsg = (CreateDockableWindow)message;
      theView = cmsg.getView();

      if(cmsg.getDockableWindowName().equals(NAME))
        cmsg.setDockableWindow(new AntFarm(this, cmsg.getView()));
    }
  }

  /**
   * Handle all of ANT's build messages including System.out and System.err
   *
   * Each build message filename, line no, column, of the error that it is
   * reporting if it in fact is an Error.  You can use this info some how
   * to publish an event to the Error List
   *
   * TODO: handle System.err messages with a different color.
   */
  void handleBuildMessage( AntFarm antFarm, BuildMessage message )
  {
    handleBuildMessage( antFarm, message, null);
  }

  void handleBuildMessage( AntFarm antFarm, BuildMessage message, Color lineColor )
  {
    if( message.isError() )
    {
      // publish this message to the ErrorList
      addError( ErrorSource.ERROR, message.getAbsoluteFilename(),
                message.getLine(), message.getColumn(),
                message.toString() ); //message.getMessage() );
      // "error-list" should be ErrorListPlugin.NAME but the
      // jar could be in a couple of different places...
      // theView.getDockableWindowManager().addDockableWindow("error-list");
    }
    else if( message.isWarning() )
    {
      // publish this message to the ErrorList
      addError( ErrorSource.WARNING, message.getAbsoluteFilename(),
                message.getLine(), message.getColumn(),
                message.toString() ); //message.getMessage() );
      // "error-list" should be ErrorListPlugin.NAME but the
      // jar could be in a couple of different places...
      // theView.getDockableWindowManager().addDockableWindow("error-list");
    }
    else
    {
      // publish this message to the buildResults text area
      //out.println("[MESSAGE]: " + message.toString() );
      if (lineColor != null)
        antFarm.appendToTextArea(message.toString(), lineColor);
      else
        antFarm.appendToTextArea(message.toString());
    }
  }


  void addError(int type, String file, int line, int column, String message)
  {
    /** this method call is a hack.. the signature of the method call is:
    * addError(int type, String path, int lineIndex, int start, int end, String error)
    * I don't know what 'start' and 'end' do
    * The -1 is a hack to make the error point to the correct place
    */

    errorSource.addError(type,file,line - 1,0,0,message);
  }

  void clearErrors()
  {
    errorSource.clear();
  }

}
