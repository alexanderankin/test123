/*
 *  AntFarm.java - Plugin for running Ant builds from jEdit.
 *  Copyright (C) 2001 Brian Knowles
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package antfarm;

import console.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.apache.tools.ant.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.browser.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.*;

public class AntFarm extends JPanel implements EBComponent
{

	public final static String NAME = AntFarmPlugin.NAME;

	JButton addAntFile;
	JButton removeAntFile;
	JButton runTarget;
	private Hashtable _antProjects = new Hashtable();

	private Panel _toolBar;
	private JScrollPane _projects;
	private AntTree _antTree;
	private View _view;

	static public final	String logLevels[] = {
			"Error",
			"Warning",
			"Info",
			"Verbose",
			"Debug"
	};

	private int logLevel;

	public AntFarm( View view )
	{
		super( new BorderLayout() );
		_view = view;
		initComponents();
		setDefaultMessageOutputLevel();
		EditBus.addToBus( this );
	}

	public void setDefaultMessageOutputLevel()
	{
		setMessageOutputLevel(jEdit.getProperty(
			AntFarmPlugin.OPTION_PREFIX + "logging-level", "Info"));
	}

	public void setMessageOutputLevel(String level)
	{
		if(level == null)
			return;
		for(int i = 0; i < logLevels.length; ++i)
		{
			if(level.equals(logLevels[i]))
			{
				logLevel = i;
				return;
			}
		}
		logLevel = Project.MSG_INFO;
	}

	public void setMessageOutputLevel(int level)
	{
		if(level < Project.MSG_ERR)
			logLevel = Project.MSG_ERR;
		else if(level > Project.MSG_DEBUG)
			logLevel = Project.MSG_DEBUG;
		else logLevel = level;
	}

	public int getMessageOutputLevel()
	{
		return logLevel;
	}


	static ImageIcon loadIcon( String propertyName )
	{
		String iconName = jEdit.getProperty( propertyName );
		return new ImageIcon( AntFarm.class.getResource( iconName ) );
	}


	public String getName()
	{
		return AntFarm.NAME;
	}


	public Component getComponent()
	{
		return this;
	}

	public void handleMessage( EBMessage msg )
	{
		if ( msg instanceof BufferUpdate ) {
			BufferUpdate updateMessage = (BufferUpdate) msg;
			if ( updateMessage.getWhat() == BufferUpdate.LOADED ) {
				File file = updateMessage.getBuffer().getFile();
				if ( file.getName().equals( "build.xml" ) )
					addAntBuildFile( file.getAbsolutePath() );
			}
		}

		else if ( msg instanceof VFSUpdate ) {
			VFSUpdate updateMessage = (VFSUpdate) msg;
			reloadAntFile( updateMessage.getPath() );
		}

		else if( msg instanceof PropertiesChanged ) {
			setDefaultMessageOutputLevel();
		}


	}


	public void addAntBuildFile( String buildFile )
	{
		if ( isAntFileKnown( buildFile ) )
			return;

		String currentProp = jEdit.getProperty( "antviewer.buildfiles" );
		if ( ( currentProp != null ) && ( currentProp.length() > 0 ) )
			currentProp += "," + buildFile;
		else
			currentProp = buildFile;

		jEdit.setProperty( "antviewer.buildfiles", currentProp );
		jEdit.saveSettings();

		_antTree.reload();
	}


	Project getProject( String buildFilePath ) throws Exception
	{
		Project project
			 = (Project) _antProjects.get( buildFilePath );
		if ( project != null )
			return project;
		project = parseBuildFile( buildFilePath );
		_antProjects.put( buildFilePath, project );
		return project;
	}


	Vector getAntBuildFiles()
	{
		String prop = jEdit.getProperty( "antviewer.buildfiles" );
		if ( prop == null )
			prop = "";
		StringTokenizer tok = new StringTokenizer( prop, "," );

		Vector v = new Vector();
		while ( tok.hasMoreElements() )
			v.addElement( tok.nextToken() );

		return v;
	}


	void loadBuildFileInShell( String filePath )
	{
		Vector buildFiles = getAntBuildFiles();
		for ( int i = 0; i < buildFiles.size(); i++ ) {
			String fileName = (String) buildFiles.elementAt( i );
			if ( fileName.equals( filePath ) ) {
				Console console = AntFarmPlugin.getConsole( _view, false );
				console.run( AntFarmPlugin.ANT_SHELL, console, "="
					 + ( i + 1 ) );
				break;
			}
		}
	}


	void promptForAntBuildFile()
	{
		String[] buildFile = GUIUtilities.showVFSFileDialog(
			_view,
			null,
			VFSBrowser.OPEN_DIALOG, false
			 );
		if ( null == buildFile )
			return;

		// assume we only got one back since we only allowed single
		// selection.
		addAntBuildFile( buildFile[0] );
	}


	void reloadAntFile( String path )
	{
		if ( !isAntFileKnown( path ) )
			return;
		AntFarmPlugin.getErrorSource().clear();
		try {
			_antProjects.put( path, parseBuildFile( path ) );
		}
		catch ( Exception e ) {
			// always force broken files to be re-parsed
			_antProjects.remove( path );
		}
		_antTree.reload();
		loadBuildFileInShell( path );
	}


	void removeAntBuildFile()
	{
		if ( _antTree == null )
			return;
		String buildFile = _antTree.getSelectedBuildFile();
		Vector buildFiles = getAntBuildFiles();
		Vector remainingBuildFiles = new Vector();

		String file = null;
		for ( int i = 0; i < buildFiles.size(); i++ ) {
			file = (String) buildFiles.elementAt( i );
			if ( !file.equals( buildFile ) )
				remainingBuildFiles.addElement( file );
		}

		saveAntFilesProperty( remainingBuildFiles );
		_antTree.removeBuildFileNode();
	}


	void displayProjectDir( File directory )
	{
		String window = "vfs.browser";
		_view.getDockableWindowManager().addDockableWindow( window );
		VFSBrowser browser = (VFSBrowser) _view.getDockableWindowManager().getDockable( window );
		browser.setDirectory( directory.getAbsolutePath() );
	}


	void addAntError( String exceptionString, String baseDir )
	{
		ConsolePlugin.parseLine( exceptionString, baseDir, AntFarmPlugin.getErrorSource() );
	}


	private boolean isAntFileKnown( String path )
	{
		Vector buildFiles = getAntBuildFiles();
		for ( int i = 0; i < buildFiles.size(); i++ ) {
			if ( path.equals( (String) buildFiles.elementAt( i ) ) )
				return true;
		}
		return false;
	}


	private Project parseBuildFile( String buildFilePath ) throws Exception
	{
		File buildFile = new File( buildFilePath );
		Project project = new Project();
		try {
			if ( buildFile.exists() ) {
				project.init();
				try {
					ProjectHelper.configureProject( project, buildFile );
				}
				catch(Exception e)
				{
					Log.log(Log.ERROR, this, "Call to ProjectHelper.configureProject() throw exception.");
					throw e;
				}
			}
			else {
				throw new Exception(
					jEdit.getProperty( AntFarmPlugin.NAME + ".project.missing" ) +
					buildFile.getAbsolutePath()
					 );
			}
		}
		catch ( BuildException be ) {
			addAntError(
				be.toString(),
				project.getBaseDir().toString()
				 );
			Log.log( Log.DEBUG, project, be );
			throw be;
		}
		catch ( Exception e ) {
			Log.log( Log.ERROR, project, e );
			throw e;
		}
		return project;
	}



	private void saveAntFilesProperty( Vector v )
	{
		String prop = "";
		for ( int i = 0; i < v.size(); i++ )
			prop += (String) v.elementAt( i ) + ",";
		jEdit.setProperty( "antviewer.buildfiles", prop );
		jEdit.saveSettings();
	}


	private void initComponents()
	{
		_projects = null;

		setLayout( new BorderLayout() );

		Box topBox = new Box( BoxLayout.Y_AXIS );
		topBox.add( createToolBar() );
		add( BorderLayout.NORTH, topBox );

		JScrollPane _projects = new JScrollPane();
		_projects.getViewport().add( _antTree = new AntTree( this, _view ) );

		add( BorderLayout.CENTER, _projects );
		setVisible( true );

	}


	private JToolBar createToolBar()
	{
		JToolBar _toolBar = new JToolBar();
		_toolBar.setFloatable( false );
		_toolBar.putClientProperty( "JToolBar.isRollover", Boolean.TRUE );

		_toolBar.add( addAntFile = createToolButton( "add" ) );
		_toolBar.add( removeAntFile = createToolButton( "remove" ) );

		_toolBar.addSeparator();
		_toolBar.add( runTarget = createToolButton( "run" ) );

		// default to enabled to false
		removeAntFile.setEnabled( false );
		runTarget.setEnabled( false );

		_toolBar.addSeparator();

		_toolBar.add( Box.createGlue() );

		return _toolBar;
	}


	private JButton createToolButton( String name )
	{
		String prefix = AntFarmPlugin.NAME;
		String buttonPrefix = prefix + "." + name + ".";
		String iconProp = buttonPrefix + "icon";

		JButton button = new JButton( AntFarm.loadIcon( iconProp ) );
		button.setToolTipText( jEdit.getProperty( buttonPrefix + "label" ) );

		button.setRequestFocusEnabled( false );
		button.setMargin( new Insets( 0, 0, 0, 0 ) );
		button.addActionListener( new ActionHandler() );

		return button;
	}


	class ActionHandler implements ActionListener
	{
		public void actionPerformed( ActionEvent evt )
		{
			Object source = evt.getSource();
			if ( source == addAntFile ) {
				promptForAntBuildFile();
			}
			if ( source == removeAntFile ) {
				removeAntBuildFile();
			}
			if ( source == runTarget ) {
				_antTree.executeCurrentTarget();
			}
		}
	}
}

