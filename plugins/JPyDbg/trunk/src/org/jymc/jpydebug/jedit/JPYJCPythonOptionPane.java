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

package org.jymc.jpydebug.jedit;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;

import org.gjt.sp.jedit.*;
import org.jymc.jpydebug.PythonDebugParameters;
import org.jymc.jpydebug.utils.* ; 


/**
 * @author jean-yves
 *
 * Option Pane used to modify JPython CPython Compiler's location
 * 
 */
public class JPYJCPythonOptionPane
extends AbstractOptionPane
{

	private final static String _LOCALPYTHON_PROPERTY_LABEL_ = "options.jpydebug-dbgoptions.pythoninterpreter" ; 
	public final static String LOCALPYTHON_PROPERTY = "jpydebug-dbgoptions.pythoninterpreter" ; 

	private final static String _LOCALJYTHON_PROPERTY_LABEL_ = "options.jpydebug-dbgoptions.jythoninterpreter" ; 
	public final static String LOCALJYTHON_PROPERTY = "jpydebug-dbgoptions.jythoninterpreter" ; 

	private final static String _LOCALJYTHONJVM_PROPERTY_LABEL_ = "options.jpydebug-dbgoptions.jythonjvm" ; 
	public final static String LOCALJYTHONJVM_PROPERTY = "jpydebug-dbgoptions.jythonjvm" ; 

	public final static String JYTHON_ENVIRONMENT = "jpydebug-dbgoptions.jython" ; 
	private final static String _JYTHON_ENVIRONMENT_LABEL_ = "options.jpydebug-dbgoptions.jython" ;

	public final static String JYTHON_HOME = "jpydebug-dbgoptions.jythonhome" ; 
	private final static String _JYTHON_HOME_LABEL_ = "options.jpydebug-dbgoptions.jythonhome" ;

	private JTextField _localPython    ; 
	private JTextField _jythonHome ; 
	private JTextField _localJythonArgs    ; 
	private JTextField _localJythonJvm  ; 
	private JCheckBox _jythonEnvironment ;
	
	private final static Dimension _FILLER_ = new Dimension(0,5) ;
	
	public JPYJCPythonOptionPane()
	{ super("jcpython-options"); }
	
	class _PANE_VERTICAL_ELEMENT_
	extends JPanel 
	{
	  public _PANE_VERTICAL_ELEMENT_( JComponent label , JComponent field )
	  {
		setLayout ( new FlowLayout( FlowLayout.LEFT ,5,5) ) ; 	
		add(label) ; 
		add(field) ; 
	  }	
	}
	
	class _PYTHON_PANE_
	extends JPanel 
	{
	  public _PYTHON_PANE_()
	  {
	    setLayout( new GridLayout(1,1)) ;
	    Box b = Box.createVerticalBox() ;
	    
		b.add( Box.createVerticalGlue()) ;

		_localPython = new JTextField(20) ; 
		_localPython.setText( jEdit.getProperty( LOCALPYTHON_PROPERTY , "" ) ) ;
		JLabel pythonLabel = new JLabel( jEdit.getProperty(_LOCALPYTHON_PROPERTY_LABEL_) ) ; 
		b.add( new FileChooserPane( pythonLabel , _localPython , null ) ) ;
		b.add( Box.createRigidArea( _FILLER_ )) ; // filler

		b.add( Box.createVerticalGlue()) ;
		
		b.setBorder( new TitledBorder("C PYTHON context properties") ) ;
		
		add(b) ;
	  }	
	}
	
	class _JYTHON_PANE_
	extends JPanel
	{
	  public _JYTHON_PANE_()
	  {
	    setLayout( new GridLayout(1,1)) ;
	    Box b = Box.createVerticalBox() ;

	    b.add( Box.createVerticalGlue() ) ;
	    
		_localJythonJvm = new JTextField(20) ; 
		_localJythonJvm.setText( jEdit.getProperty( LOCALJYTHONJVM_PROPERTY , "" ) ) ;
		JLabel jJyLocationLabel = new JLabel( jEdit.getProperty(_LOCALJYTHONJVM_PROPERTY_LABEL_) ) ; 
		b.add( new FileChooserPane( jJyLocationLabel , _localJythonJvm , null ) ) ;
		b.add( Box.createRigidArea( _FILLER_ )) ; // filler

		_jythonHome = new JTextField(20) ; 
		_jythonHome.setText( jEdit.getProperty( JYTHON_HOME , "" ) ) ;
		JLabel jythonHomeLabel = new JLabel( jEdit.getProperty(_JYTHON_HOME_LABEL_) ) ; 
		b.add( new FileChooserPane( jythonHomeLabel , _jythonHome , null ) ) ;
		b.add( Box.createRigidArea( _FILLER_ )) ; // filler

		_localJythonArgs = new JTextField(50) ; 
		_localJythonArgs.setText( jEdit.getProperty( LOCALJYTHON_PROPERTY , "" ) ) ;
		JLabel localJythonArgsLabel = new JLabel( jEdit.getProperty(_LOCALJYTHON_PROPERTY_LABEL_) ) ; 
		_PANE_VERTICAL_ELEMENT_ e2 = new _PANE_VERTICAL_ELEMENT_( localJythonArgsLabel , _localJythonArgs ) ; 
		b.add(e2) ;
		b.add( Box.createRigidArea( _FILLER_ )) ; // filler
		
		_jythonEnvironment = new JCheckBox() ; 
		_jythonEnvironment.setSelected( jEdit.getBooleanProperty( JYTHON_ENVIRONMENT , false ) ) ;
		JLabel jythonLabel = new JLabel( jEdit.getProperty(_JYTHON_ENVIRONMENT_LABEL_) ) ; 
		_PANE_VERTICAL_ELEMENT_ e3 = new _PANE_VERTICAL_ELEMENT_( jythonLabel , _jythonEnvironment ) ; 
		b.add(e3) ;
		b.add( Box.createRigidArea( _FILLER_ )) ; // filler
		b.setBorder( new TitledBorder("JAVA PYTHON(Jython) context properties") ) ;
		
		add(b);
	  }	
	}
	
	
	/**
	 *  init option pane panel
	 */
	protected void _init()
	{
	  setLayout(new BoxLayout( this , BoxLayout.Y_AXIS)  )  ;
	  //setLayout( new BorderLayout() ) ;	
	  add( new _PYTHON_PANE_() ) ; 	
	  add( new _JYTHON_PANE_() ) ; 	
	  // populating functional properties both to Jedit and Generic parameter container
	  _localPython.setText(jEdit.getProperty( LOCALPYTHON_PROPERTY ));
	  PythonDebugParameters.set_pythonShellPath(jEdit.getProperty( LOCALPYTHON_PROPERTY )) ;
	  _jythonHome.setText(jEdit.getProperty( JYTHON_HOME ));
	  PythonDebugParameters.set_jythonHome(jEdit.getProperty( JYTHON_HOME )) ;
	  PythonDebugParameters.set_pythonShellPath(jEdit.getProperty( JYTHON_HOME )) ;
	  _localJythonJvm.setText(jEdit.getProperty( LOCALJYTHONJVM_PROPERTY ));
	  PythonDebugParameters.set_jythonShellJvm(jEdit.getProperty( LOCALJYTHONJVM_PROPERTY )) ;
	  _localJythonArgs.setText(jEdit.getProperty( LOCALJYTHON_PROPERTY ));
	  PythonDebugParameters.set_jythonShellArgs(jEdit.getProperty( LOCALJYTHON_PROPERTY )) ;
	  _jythonEnvironment.setSelected(jEdit.getBooleanProperty(JYTHON_ENVIRONMENT)) ;
	  PythonDebugParameters.set_jythonActivated(jEdit.getBooleanProperty(JYTHON_ENVIRONMENT)) ;
	}

	/**
	 *  save options properties
	 */
	protected void _save()
	{
      // functional properties
	  jEdit.setProperty( LOCALPYTHON_PROPERTY , _localPython.getText() ) ; 
	  jEdit.setProperty( LOCALJYTHONJVM_PROPERTY , _localJythonJvm.getText() ) ; 
	  jEdit.setProperty( JYTHON_HOME , _jythonHome.getText() ) ; 
	  jEdit.setProperty( LOCALJYTHON_PROPERTY , _localJythonArgs.getText() ) ; 
	  jEdit.setBooleanProperty(JYTHON_ENVIRONMENT,_jythonEnvironment.isSelected()) ;
	}



}
