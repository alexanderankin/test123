/*
 *  AntFarmShell.java - Plugin for running Ant builds from jEdit.
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
import java.awt.Color;
import java.io.*;
import java.util.*;
import org.apache.tools.ant.*;
import org.gjt.sp.jedit.*;

public class AntFarmShell extends Shell
{

	private Project _currentProject;
	private File _currentBuildFile;
	private String _currentTarget;
	private TargetRunner _targetRunner;
	private AntFarm _antBrowser;
    private Output _output;


	public AntFarmShell()
	{
		super( "Ant" );
	}


	public static AntFarm getAntFarm( View view )
	{
		return (AntFarm) view.getDockableWindowManager().getDockable( AntFarmPlugin.NAME );
	}


	public static void reloadCurrentProject( View view )
	{
		String projectPath = AntFarmPlugin.ANT_SHELL.getCurrentProjectPath();
		getAntFarm( view ).reloadAntBuildFile( projectPath );
	}


	static String getAntCommandFragment( Properties properties )
	{
		if ( properties == null ) {
			return "";
		}
		StringBuffer command = new StringBuffer();
		Enumeration ee = properties.keys();
		String current;
		while ( ee.hasMoreElements() ) {
			current = (String) ee.nextElement();
			command.append( " -D" ).append( current ).append( "=" );
			command.append( properties.getProperty( current ) );
		}
		return command.toString();
	}

	public String getCurrentProjectPath()
	{
		if (_currentBuildFile == null) return "";
		return FileUtils.getAbsolutePath( _currentBuildFile );
	}


	// ----- Begin Shell implementation -----

	public void printInfoMessage( Output output )
	{
		printUsage(
			jEdit.getProperty( AntFarmPlugin.NAME + ".shell.msg.info" ),
			null,
			output
			 );
		printCurrentProjectInfo( null, output );
	}


	public void execute( Console console, Output output, String command )
	{
		stop( console );
        
        _output = output;

		command = command.trim();

		if ( command.startsWith( "!" ) ) {
			if ( _currentProject == null ) {

				output.print(
					console.getErrorColor(),
					jEdit.getProperty( AntFarmPlugin.NAME + ".shell.msg.non-selected" )
					 );
				output.commandDone();
				return;
			}
			_currentTarget = command.indexOf( " " ) > 0 ?
				command.substring( 1, command.indexOf( " " ) ) :
				command.substring( 1 );

			if ( _currentTarget.equals( "" ) ) {
				_currentTarget = _currentProject.getDefaultTarget();
			}
			Target target = (Target)
				_currentProject.getTargets().get( _currentTarget );

			if ( target == null ) {
				printUsage( "Not a valid target: " + _currentTarget, console.getErrorColor(), output );
				output.commandDone();
				return;
			}

			_targetRunner = new TargetRunner(
				target,
				_currentBuildFile,
				console.getView(),
				output,
				AntCommandParser.parseAntCommandProperties( command )
				 );
		}
		else if ( command.equals( "?" ) ) {

			printUsage( "", null, output );

			Vector buildFiles = getAntFarm( console ).getAntBuildFiles();
			output.print(
				null,
				jEdit.getProperty( AntFarmPlugin.NAME + ".shell.label.available-files" )
				 );

			String fileList = "";
			for ( int i = 0; i < buildFiles.size(); i++ ) {
				fileList += "(" + ( i + 1 ) + ") " + buildFiles.elementAt( i ) + "\n";
			}
			output.print( console.getInfoColor(), fileList );

			printCurrentProjectInfo( console.getInfoColor(), output );

			output.commandDone();
		}
		else if ( command.startsWith( "=" ) ) {

			Vector buildFiles = getAntFarm( console ).getAntBuildFiles();

			try {
				int fileNumber = Integer.parseInt( command.substring( 1 ) );

				if ( fileNumber - 1 >= buildFiles.size() ) {
					printUsage(
						jEdit.getProperty( AntFarmPlugin.NAME + ".shell.msg.no-build-file" ),
						console.getErrorColor(),
						output
						 );
					output.commandDone();
					return;
				}
				String buildFilePath = (String) buildFiles.elementAt( fileNumber - 1 );
				File buildFile = new File( buildFilePath );
				Project project = null;
				try {
					project = getAntFarm( console ).getProject( buildFile.getAbsolutePath() );
					setCurrentProject( project, buildFile, console );
				}
				catch ( Exception e ) {
					setCurrentProject( null, buildFile, console );
					output.print(
						console.getErrorColor(),
						jEdit.getProperty( AntFarmPlugin.NAME + ".shell.msg.broken-file" )
						 + buildFile + "\n" + e
						 );
					output.commandDone();
					return;
				}
				output.commandDone();
			}
			catch ( NumberFormatException e ) {
				printUsage(
					jEdit.getProperty( AntFarmPlugin.NAME + ".shell.msg.invalid-usage" ),
					console.getErrorColor(),
					output
					 );
				output.commandDone();
				return;
			}
		}
		else if ( command.startsWith( "+" ) ) {

			String filePath = command.substring( 1 );
			try {

				// Get the provided path, or look up the tree.
				File file = getFile( filePath, console );
				filePath = FileUtils.getAbsolutePath( file );

				// try to reload the project
				getAntFarm( console ).reloadAntBuildFile( filePath );

				// initiate the project
				Project project = getAntFarm( console ).getProject( filePath );
				setCurrentProject( project, file, console );
				getAntFarm( console ).addAntBuildFile( filePath );
			}
			catch ( Exception e ) {
				output.print(
					console.getErrorColor(),
					jEdit.getProperty( AntFarmPlugin.NAME + ".shell.msg.broken-file" )
					 + filePath + "\n" + e
					 );
				output.commandDone();
				return;
			}
			output.commandDone();
		}
		else {
			printUsage(
				jEdit.getProperty( AntFarmPlugin.NAME + ".shell.msg.invalid-usage" ),
				console.getErrorColor(),
				output
				 );
			output.commandDone();
		}
	}


	public void stop( Console console )
	{
		if ( _targetRunner != null ) {
			if ( _targetRunner.isAlive() ) {
				_targetRunner.stop();
                
                if (_output != null)
                {
                    _output.print(console.getErrorColor(),
                                  jEdit.getProperty( AntFarmPlugin.NAME + ".shell.msg.killed" ));
                }
			}
			_targetRunner.resetLogging();
			_targetRunner = null;
		}
	}


	public boolean waitFor( Console console )
	{
		if ( _targetRunner != null ) {
			try {
				synchronized ( _targetRunner ) {
					_targetRunner.join();
					_targetRunner = null;
				}
			}
			catch ( InterruptedException ie ) {
				return false;
			}
		}
		return true;
	}


	// ----- End Shell implementation -----

	private void setCurrentProject( Project currentProject, File currentBuildFile, Console console )
	{
		boolean printMessage = false;
		if ( !currentBuildFile.equals( _currentBuildFile ) )
			printMessage = true;

		_currentBuildFile = currentBuildFile;
		_currentProject = currentProject;

		if ( console.getShell() == AntFarmPlugin.ANT_SHELL && printMessage ) {
			printCurrentProjectInfo( console.getInfoColor(), console );
		}
	}



	private AntFarm getAntFarm( Console console )
	{
		AntFarm antBrowser = (AntFarm) console.getView().getDockableWindowManager().getDockable( AntFarmPlugin.NAME );
		if ( antBrowser != null ) {
			return antBrowser;
		}
		if ( _antBrowser == null ) {
			_antBrowser = new AntFarm( console.getView() );
		}
		return _antBrowser;
	}


	private File getFile( String filePath, Console console )
	{
		if ( filePath == null || filePath.length() == 0 ) {
			filePath = "build.xml";
		}
		File file = new File( filePath );
		if ( file.isAbsolute() ) {
			return file;
		}
		File directory = new File( console.getView().getBuffer().getPath() );
		return FileUtils.findFile( directory, filePath );
	}


	private void printCurrentProjectInfo( Color color, Output output )
	{
		if ( _currentProject == null )
			return;
		output.print(
			null,
			jEdit.getProperty( AntFarmPlugin.NAME + ".shell.label.current-file" )
			 );
		String projectName = _currentProject.getName() != null ? _currentProject.getName() : "Untitled";

		output.print( color, projectName + " ("
			 + _currentBuildFile.getAbsolutePath() + ")\n" );

		output.print(
			null,
			jEdit.getProperty( AntFarmPlugin.NAME + ".shell.label.available-targets" )
			 );
		String info = "";
		Enumeration targets = _currentProject.getTargets().keys();
		while ( targets.hasMoreElements() ) {
			String target = (String) targets.nextElement();
			info += target + printDefaultLabel( _currentProject, target ) + "\t";
		}
		output.print( color, info );

	}


	private String printDefaultLabel( Project project, String target )
	{
		if ( project.getDefaultTarget().equals( target ) )
			return " [default]";
		return "";
	}


	private void printUsage( String additionalMessage, Color color, Output output )
	{
		output.print( color, additionalMessage );
		output.print(
			null,
			jEdit.getProperty( AntFarmPlugin.NAME + ".shell.msg.usage" )
			 );
	}

}

