/**
* Copyright (C) 2003 Jean-Yves Mengant
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


package org.jymc.jpydebug.jedit;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;

import org.gjt.sp.jedit.*;
import org.jymc.jpydebug.* ; 
import org.jymc.jpydebug.swing.ui.* ; 
import org.jymc.jpydebug.utils.* ; 


/**
 * @author jean-yves
 *
 * Used to get/set JPY debugger options 
 *
 */
public class JPYJeditOptionPane
extends AbstractOptionPane
{
  private final static Dimension _FILLER_ = new Dimension(0,5) ;
   private JButton _btnBreakpointColor;
   private JPanel  _pane ;
   
  private JpyDbgConfigurationPanel _confPane = new JpyDbgConfigurationPanel() ;
  
	
  public JPYJeditOptionPane()
  {
    super("jpydebug-options");
  }
	
	
  class _COLOR_PANE_
  extends JPanel
  {
    public _COLOR_PANE_()	
    {
    JPYColorChooserHelper helper = new JPYColorChooserHelper("colorChooser.title",JPYJeditOptionPane.this) ;	
      setLayout(new BoxLayout( this , BoxLayout.Y_AXIS)  )  ;
      VerticalComponentPanel e1 = new VerticalComponentPanel( 
			 new JLabel( jEdit.getProperty( JpyDbgConfigurationPanel.CURRENT_DEBUG_LINE_LABEL), JLabel.CENTER ) ,
				     _pane = helper.createColorPanel( JpyDbgConfigurationPanel.CURRENT_LINE_COLOR,
	                                                              JpyDbgConfigurationPanel.CURRENT_LINE_BORDER_COLOR )
					   ) ;
      add(e1) ; 
      add( Box.createRigidArea( _FILLER_ )) ; // filler
      VerticalComponentPanel e2 = new VerticalComponentPanel(
			new JLabel( jEdit.getProperty( JpyDbgConfigurationPanel.BREAKPOINT_LINE_COLOR_LABEL), JLabel.CENTER ) ,
							  _btnBreakpointColor = helper.createColorButton(JpyDbgConfigurationPanel.BREAKPOINT_LINE_COLOR));
      add(e2) ; 
      setBorder( new TitledBorder("debugging colors") ) ;
    }
    
  }
	
  /**
  *  init option pane panel
  */
  protected void _init()
  {
    setLayout( new BorderLayout() ) ;
    
    // populating functional properties both to Jedit and Generic parameter container
    _confPane.set_value(JpyDbgConfigurationPanel.TEMPDIR_PROPERTY , jEdit.getProperty(JpyDbgConfigurationPanel.TEMPDIR_PROPERTY ));
    _confPane.set_label(JpyDbgConfigurationPanel.TEMPDIR_PROPERTY_LABEL, jEdit.getProperty(JpyDbgConfigurationPanel.TEMPDIR_PROPERTY_LABEL ));
    PythonDebugParameters.set_tempDir(jEdit.getProperty(JpyDbgConfigurationPanel.TEMPDIR_PROPERTY)) ; 
   
    _confPane.set_value( JpyDbgConfigurationPanel.DEBUGGINGHOST_PROPERTY	, jEdit.getProperty( JpyDbgConfigurationPanel.DEBUGGINGHOST_PROPERTY ));
    _confPane.set_label(JpyDbgConfigurationPanel.DEBUGGINGHOST_PROPERTY_LABEL, jEdit.getProperty(JpyDbgConfigurationPanel.DEBUGGINGHOST_PROPERTY_LABEL ));
    PythonDebugParameters.set_dbgHost(jEdit.getProperty(JpyDbgConfigurationPanel.DEBUGGINGHOST_PROPERTY)) ;
   
    //_confPane.set_value( JpyDbgConfigurationPanel.JPYLOCATION_PROPERTY , jEdit.getProperty(JpyDbgConfigurationPanel.JPYLOCATION_PROPERTY));
    //_confPane.set_label(JpyDbgConfigurationPanel.JPYLOCATION_PROPERTY_LABEL, jEdit.getProperty(JpyDbgConfigurationPanel.JPYLOCATION_PROPERTY_LABEL ));
    //PythonDebugParameters.set_jpydbgScript(jEdit.getProperty(JpyDbgConfigurationPanel.JPYLOCATION_PROPERTY )) ; 
   
    _confPane.set_integerValue(	JpyDbgConfigurationPanel.CONNECTINGPORT_PROPERTY, jEdit.getIntegerProperty(JpyDbgConfigurationPanel.CONNECTINGPORT_PROPERTY,-1 )) ;
    _confPane.set_label(JpyDbgConfigurationPanel.CONNECTINGPORT_PROPERTY_LABEL, jEdit.getProperty(JpyDbgConfigurationPanel.CONNECTINGPORT_PROPERTY_LABEL ));
    PythonDebugParameters.set_connectingPort(jEdit.getIntegerProperty(JpyDbgConfigurationPanel.CONNECTINGPORT_PROPERTY,-1 )) ;
	  
    _confPane.set_integerValue( JpyDbgConfigurationPanel.CALLBACKPORT_PROPERTY , jEdit.getIntegerProperty(JpyDbgConfigurationPanel.CALLBACKPORT_PROPERTY,-1 )) ;
    _confPane.set_label(JpyDbgConfigurationPanel.CALLBACKPORT_PROPERTY_LABEL, jEdit.getProperty(JpyDbgConfigurationPanel.CALLBACKPORT_PROPERTY_LABEL ));
    PythonDebugParameters.set_listeningPort(jEdit.getIntegerProperty(JpyDbgConfigurationPanel.CALLBACKPORT_PROPERTY,-1 )) ; 
	  
    _confPane.set_value(	JpyDbgConfigurationPanel.JPYARGS_PROPERTY , jEdit.getProperty(JpyDbgConfigurationPanel.JPYARGS_PROPERTY ) ) ;
    _confPane.set_label(JpyDbgConfigurationPanel.JPYARGS_PROPERTY_LABEL , jEdit.getProperty(JpyDbgConfigurationPanel.JPYARGS_PROPERTY_LABEL ));
    PythonDebugParameters.set_jpydbgScriptArgs( jEdit.getProperty( JpyDbgConfigurationPanel.JPYARGS_PROPERTY ) ) ;
	  
	  
    PythonDebugParameters.set_autocompletion( jEdit.getBooleanProperty(JpyDbgConfigurationPanel.AUTOCOMPLETION_PROPERTY)) ;
    _confPane.set_label(JpyDbgConfigurationPanel.AUTOCOMPLETION_PROPERTY_LABEL, jEdit.getProperty(JpyDbgConfigurationPanel.AUTOCOMPLETION_PROPERTY_LABEL ));
    _confPane.set_booleanValue(JpyDbgConfigurationPanel.AUTOCOMPLETION_PROPERTY , jEdit.getBooleanProperty(JpyDbgConfigurationPanel.AUTOCOMPLETION_PROPERTY)) ;
   
    PythonDebugParameters.set_autoCompletionDelay(jEdit.getIntegerProperty(JpyDbgConfigurationPanel.AUTOCOMPLETION_DELAY_PROPERTY,200)) ;
    _confPane.set_label(JpyDbgConfigurationPanel.AUTOCOMPLETION_DELAY_PROPERTY_LABEL, jEdit.getProperty(JpyDbgConfigurationPanel.AUTOCOMPLETION_DELAY_PROPERTY_LABEL ));
    _confPane.set_integerValue( JpyDbgConfigurationPanel.AUTOCOMPLETION_DELAY_PROPERTY , jEdit.getIntegerProperty(JpyDbgConfigurationPanel.AUTOCOMPLETION_DELAY_PROPERTY,200)) ;
	  
    PythonDebugParameters.set_debugTrace(jEdit.getBooleanProperty(JpyDbgConfigurationPanel.DEBUG_TRACE,true)) ;
    _confPane.set_label(JpyDbgConfigurationPanel.DEBUG_TRACE_LABEL, jEdit.getProperty(JpyDbgConfigurationPanel.DEBUG_TRACE_LABEL ));
    _confPane.set_booleanValue( JpyDbgConfigurationPanel.DEBUG_TRACE , jEdit.getBooleanProperty(JpyDbgConfigurationPanel.DEBUG_TRACE)) ;
	  
    PythonDebugParameters.set_debugDynamicEvaluation(jEdit.getBooleanProperty(JpyDbgConfigurationPanel.DEBUG_DYNAMIC_EVALUATION,true)) ;
    _confPane.set_label(JpyDbgConfigurationPanel.DEBUG_DYNAMIC_EVALUATION_LABEL, jEdit.getProperty(JpyDbgConfigurationPanel.DEBUG_DYNAMIC_EVALUATION_LABEL ));
    _confPane.set_booleanValue(JpyDbgConfigurationPanel.DEBUG_DYNAMIC_EVALUATION , jEdit.getBooleanProperty(JpyDbgConfigurationPanel.DEBUG_DYNAMIC_EVALUATION , true )) ;
	  
    _confPane.set_value( JpyDbgConfigurationPanel.CODEPAGE_PROPERTY, jEdit.getProperty(JpyDbgConfigurationPanel.CODEPAGE_PROPERTY,"" )) ; 
    _confPane.set_label(JpyDbgConfigurationPanel.CODEPAGE_PROPERTY_LABEL, jEdit.getProperty(JpyDbgConfigurationPanel.CODEPAGE_PROPERTY_LABEL ));
    PythonDebugParameters.set_codePage(jEdit.getProperty(JpyDbgConfigurationPanel.CODEPAGE_PROPERTY)) ;  
	  
    _confPane.doMyLayout( new _COLOR_PANE_() ) ;
    add( _confPane ) ;  

  }
	

  /**
   *  save options properties
   */
  protected void _save()
  {
    // coloring properties
    //if ( _confPane.isValid() )
    //{  
      // populate fields first 
      _confPane.populateFields() ; 
      jEdit.setProperty( JpyDbgConfigurationPanel.BREAKPOINT_LINE_COLOR, GUIUtilities.getColorHexString(_btnBreakpointColor.getBackground()));
      Color c= ((javax.swing.border.MatteBorder)_pane.getBorder()).getMatteColor();
      jEdit.setProperty(JpyDbgConfigurationPanel.CURRENT_LINE_BORDER_COLOR,GUIUtilities.getColorHexString(c));
      // functional properties
      jEdit.setProperty( JpyDbgConfigurationPanel.TEMPDIR_PROPERTY , PythonDebugParameters.get_workDir () ) ; 
      jEdit.setProperty( JpyDbgConfigurationPanel.DEBUGGINGHOST_PROPERTY , PythonDebugParameters.get_dbgHost () ) ; 
      // jEdit.setProperty( JpyDbgConfigurationPanel.JPYLOCATION_PROPERTY, PythonDebugParameters.get_jyPathLocation () ) ; 
      jEdit.setProperty( JpyDbgConfigurationPanel.JPYARGS_PROPERTY, PythonDebugParameters.get_jpydbgScriptArgs () ) ; 
      jEdit.setProperty( JpyDbgConfigurationPanel.CODEPAGE_PROPERTY, PythonDebugParameters.get_codePage () ) ; 
      jEdit.setIntegerProperty( JpyDbgConfigurationPanel.CONNECTINGPORT_PROPERTY, PythonDebugParameters.get_connectingPort () ) ; 
      jEdit.setIntegerProperty( JpyDbgConfigurationPanel.CALLBACKPORT_PROPERTY , PythonDebugParameters.get_listeningPort () ) ; 
      jEdit.setBooleanProperty(JpyDbgConfigurationPanel.AUTOCOMPLETION_PROPERTY, PythonDebugParameters.get_autocompletion () ) ;
      jEdit.setIntegerProperty(JpyDbgConfigurationPanel.AUTOCOMPLETION_DELAY_PROPERTY,PythonDebugParameters.get_autoCompletionDelay ()) ;
      jEdit.setBooleanProperty(JpyDbgConfigurationPanel.DEBUG_TRACE, PythonDebugParameters.get_debugTrace ()) ;
      jEdit.setBooleanProperty(JpyDbgConfigurationPanel.DEBUG_DYNAMIC_EVALUATION,PythonDebugParameters.get_debugDynamicEvaluation ()) ;
    //}

  }

}
