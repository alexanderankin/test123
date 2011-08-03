package org.jymc.jpydebug.jedit;

/**
* Copyright (C) 2003,2004,2005 Jean-Yves Mengant
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


import org.gjt.sp.jedit.*;

import org.jymc.jpydebug.PythonDebugParameters;
import org.jymc.jpydebug.swing.ui.PyLintConfigurationPanel;
import org.jymc.jpydebug.utils.FileChooserPane;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;


/**
 * @author jean-yves Option Pane used to modify JPython CPython Compiler's
 *         location
 */

public class PylintOptionPane extends AbstractOptionPane
{
  private final static String _EMPTY_ = "";

  private PyLintConfigurationPanel _pyLintConfPane =
    new PyLintConfigurationPanel();

  /**
   * Creates a new PylintOptionPane object.
   */
  public PylintOptionPane()
  {
    super( "pylint-options" );
  }

  /**
   * init option pane panel
   */
  protected void _init()
  {
    setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
    // setLayout( new BorderLayout() ) ;

    // populating functional properties both to Jedit and Generic parameter
    // container
    // jEdit.getBooleanProperty( USEPYLINT_PROPERTY, false )
    _pyLintConfPane.set_booleanValue(
                                     PyLintConfigurationPanel.USEPYLINT_PROPERTY,
                                     jEdit.getBooleanProperty( PyLintConfigurationPanel.USEPYLINT_PROPERTY )
                                  );
    _pyLintConfPane.set_label(
                              PyLintConfigurationPanel.USEPYLINT_PROPERTY_LABEL,
                              jEdit.getProperty( PyLintConfigurationPanel.USEPYLINT_PROPERTY_LABEL )
                           );
    PythonDebugParameters.set_usePyLint(
                                        jEdit.getBooleanProperty( PyLintConfigurationPanel.USEPYLINT_PROPERTY )
                                     );

    _pyLintConfPane.set_value(
                              PyLintConfigurationPanel.PYLINT_LOCATION_PROPERTY,
                              jEdit.getProperty( PyLintConfigurationPanel.PYLINT_LOCATION_PROPERTY )
                           );
    _pyLintConfPane.set_label(
                              PyLintConfigurationPanel.PYLINT_LOCATION_PROPERTY_LABEL,
                              jEdit.getProperty( PyLintConfigurationPanel.PYLINT_LOCATION_PROPERTY_LABEL )
                           );
    PythonDebugParameters.set_pyLintLocation(
                                             jEdit.getProperty( PyLintConfigurationPanel.PYLINT_LOCATION_PROPERTY )
                                          );

    _pyLintConfPane.set_value(
                              PyLintConfigurationPanel.PYLINT_ARGUMENT_PROPERTY,
                              jEdit.getProperty( PyLintConfigurationPanel.PYLINT_ARGUMENT_PROPERTY )
                           );
    _pyLintConfPane.set_label(
                              PyLintConfigurationPanel.PYLINT_ARGUMENT_PROPERTY_LABEL,
                              jEdit.getProperty( PyLintConfigurationPanel.PYLINT_ARGUMENT_PROPERTY_LABEL )
                           );
    PythonDebugParameters.set_pyLintArgs(
                                         jEdit.getProperty( PyLintConfigurationPanel.PYLINT_ARGUMENT_PROPERTY )
                                      );

    _pyLintConfPane.set_booleanValue(
                                     PyLintConfigurationPanel.PYLINT_FATAL_PROPERTY,
                                     jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_FATAL_PROPERTY )
                                  );
    _pyLintConfPane.set_label(
                              PyLintConfigurationPanel.PYLINT_FATAL_PROPERTY_LABEL,
                              jEdit.getProperty( PyLintConfigurationPanel.PYLINT_FATAL_PROPERTY_LABEL )
                           );
    PythonDebugParameters.set_pyLintFatal(
                                          jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_FATAL_PROPERTY )
                                       );

    _pyLintConfPane.set_booleanValue(
                                     PyLintConfigurationPanel.PYLINT_ERROR_PROPERTY,
                                     jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_ERROR_PROPERTY )
                                  );
    _pyLintConfPane.set_label(
                              PyLintConfigurationPanel.PYLINT_ERROR_PROPERTY_LABEL,
                              jEdit.getProperty( PyLintConfigurationPanel.PYLINT_ERROR_PROPERTY_LABEL )
                           );
    PythonDebugParameters.set_pyLintFatal(
                                          jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_ERROR_PROPERTY )
                                       );

    _pyLintConfPane.set_booleanValue(
                                     PyLintConfigurationPanel.PYLINT_WARNING_PROPERTY,
                                     jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_WARNING_PROPERTY )
                                  );
    _pyLintConfPane.set_label(
                              PyLintConfigurationPanel.PYLINT_WARNING_PROPERTY_LABEL,
                              jEdit.getProperty( PyLintConfigurationPanel.PYLINT_WARNING_PROPERTY_LABEL )
                           );
    PythonDebugParameters.set_pyLintWarning(
                                            jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_WARNING_PROPERTY )
                                         );

    _pyLintConfPane.set_booleanValue(
                                     PyLintConfigurationPanel.PYLINT_CONVENTION_PROPERTY,
                                     jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_CONVENTION_PROPERTY )
                                  );
    _pyLintConfPane.set_label(
                              PyLintConfigurationPanel.PYLINT_CONVENTION_PROPERTY_LABEL,
                              jEdit.getProperty( PyLintConfigurationPanel.PYLINT_CONVENTION_PROPERTY_LABEL )
                           );
    PythonDebugParameters.set_pyLintConvention(
                                               jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_CONVENTION_PROPERTY )
                                            );

    _pyLintConfPane.set_booleanValue(
                                     PyLintConfigurationPanel.PYLINT_REFACTOR_PROPERTY,
                                     jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_REFACTOR_PROPERTY )
                                  );
    _pyLintConfPane.set_label(
                              PyLintConfigurationPanel.PYLINT_REFACTOR_PROPERTY_LABEL,
                              jEdit.getProperty( PyLintConfigurationPanel.PYLINT_REFACTOR_PROPERTY_LABEL )
                           );
    PythonDebugParameters.set_pyLintRefactor(
                                             jEdit.getBooleanProperty( PyLintConfigurationPanel.PYLINT_REFACTOR_PROPERTY )
                                          );

    _pyLintConfPane.doMyLayout();
    add( _pyLintConfPane );
  }


  /**
   * save options properties
   */
  protected void _save()
  {

    // populate fields first
    _pyLintConfPane.populateFields();

    // functional properties
    jEdit.setProperty(
                      PyLintConfigurationPanel.PYLINT_LOCATION_PROPERTY,
                      PythonDebugParameters.get_pyLintLocation()
                   );
    jEdit.setProperty(
                      PyLintConfigurationPanel.PYLINT_ARGUMENT_PROPERTY,
                      PythonDebugParameters.get_pyLintArgs()
                   );

    jEdit.setBooleanProperty(
                             PyLintConfigurationPanel.PYLINT_FATAL_PROPERTY,
                             PythonDebugParameters.is_pyLintFatal()
                          );

    jEdit.setBooleanProperty(
                             PyLintConfigurationPanel.PYLINT_ERROR_PROPERTY,
                             PythonDebugParameters.is_pyLintError()
                          );

    jEdit.setBooleanProperty(
                             PyLintConfigurationPanel.PYLINT_WARNING_PROPERTY,
                             PythonDebugParameters.is_pyLintWarning()
                          );
    jEdit.setBooleanProperty(
                             PyLintConfigurationPanel.PYLINT_REFACTOR_PROPERTY,
                             PythonDebugParameters.is_pyLintRefactor()
                          );
    jEdit.setBooleanProperty(
                             PyLintConfigurationPanel.PYLINT_CONVENTION_PROPERTY,
                             PythonDebugParameters.is_pyLintConvention()
                          );
    jEdit.setBooleanProperty(
                             PyLintConfigurationPanel.USEPYLINT_PROPERTY,
                             PythonDebugParameters.is_usePyLint()
                          );
  }
}
