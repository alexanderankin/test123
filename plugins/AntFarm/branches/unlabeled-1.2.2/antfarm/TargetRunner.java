/*
 *  TargetRunner.java - Plugin for running Ant builds from jEdit.
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

import java.io.*;
import javax.swing.*;
import org.apache.tools.ant.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;

public class TargetRunner extends java.lang.Thread
{

	PrintStream _out = System.out;
	Project runner = new Project();
	Target runAnt = new Target();
	PrintStream _err = System.err;
	DefaultLogger _buildLogger = new AntFarmLogger();
	Throwable _error = null;

	Target _target;
	Project _project;
	File _buildFile;
	View _view;
	Output _output;

	PrintStream _consoleOut;
	PrintStream _consoleErr;


	public TargetRunner( Target target, File buildFile, View view, Output output, int logLevel )
	{
		init( target, buildFile, view, output, logLevel );
	}


	public TargetRunner( Project project, File buildFile, View view, Output output, int logLevel )
	{
		Target target = (Target) project.getTargets().get( project.getDefaultTarget() );
		init( target, buildFile, view, output, logLevel );
	}


	public void run()
	{
		AntFarmPlugin.getErrorSource().clear();

		boolean useSameJvm
			 = jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "use-same-jvm" );
		String antCommand = jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "command" );

		if ( !useSameJvm && antCommand == null )
			promptForAntCommand();

		useSameJvm = jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "use-same-jvm" );

		// assume we have the console open at this point since it called us.
		Console console =
			(Console) _view.getDockableWindowManager().getDockable( "console" );

		if ( useSameJvm ) {
			setOutputStreams();

			// set ant.file property
			_project.setUserProperty( "ant.file", _buildFile.getAbsolutePath() );

			try {
				_project.addBuildListener( _buildLogger );

				fireBuildStarted();
				_project.executeTarget( _target.getName() );
			}
			catch ( RuntimeException exc ) {
				_error = exc;
			}
			catch ( Error e ) {
				_error = e;
			}
			finally {
				fireBuildFinished();
				resetLogging();
			}
		}
		else {
			String command = " -buildfile ";
			command += "\"";
			command += _buildFile.getAbsolutePath();
			command += "\"";
			command += " " + _target.getName();
			runAntCommand( command );
		}
		_output.commandDone();
	}


	private void setOutputStreams()
	{
		synchronized(System.err)
		{
			System.setOut( _consoleOut );
			System.setErr( _consoleErr );
		}
	}

	private void init( Target target, File buildFile, View view, Output output, int logLevel )
	{
		_target = target;
		_project = _target.getProject();
		_buildFile = buildFile;
		_view = view;
		_output = output;

		_consoleErr = new AntPrintStream( System.err, _view );
		_consoleOut = new AntPrintStream( System.out, _view );

		// set so jikes prints emacs style errors
		_project.setProperty( "build.compiler.emacs", "true" );

		configureBuildLogger(logLevel);

		// fire it up
		this.start();
	}


	private void runAntCommand( String args )
	{
		String command = jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "command" );
		if ( command == null || command.equals( "" ) )
			Log.log( Log.WARNING, this, "Please set the path to the Ant script you wish to use." );

		if ( command != null ) {
			command = "\"" + command + "\"";
			if (
				jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "output-emacs" )
				 ) {
				command += " -emacs ";
			}
			command += args;
			Console console = AntFarmPlugin.getConsole( _view );
			console.run( ConsolePlugin.SYSTEM_SHELL, console, command );
		}
	}



	private void configureBuildLogger(int logLevel)
	{
		_buildLogger.setEmacsMode(
			jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "output-emacs" )
			 );
		_buildLogger.setOutputPrintStream( _consoleOut );
		_buildLogger.setErrorPrintStream( _consoleErr );

		_buildLogger.setMessageOutputLevel( logLevel );
	}


	private void resetLogging()
	{
		_consoleErr.flush();
		_consoleOut.flush();
		System.setOut( _out );
		System.setErr( _err );
		_project.removeBuildListener( _buildLogger );
	}


	private void fireBuildStarted()
	{
		BuildEvent event = new BuildEvent( _project );
		event.setMessage( "Running target: " + _target, Project.MSG_INFO );
		_buildLogger.buildStarted( event );
		_buildLogger.messageLogged( event );
	}

	private void fireBuildFinished()
	{
		//BuildEvent
		BuildEvent event = new BuildEvent( _project );
		event.setException( _error );
		_buildLogger.buildFinished( event );
	}


	private String promptForAntCommand()
	{
		Object[] options =
			{
			jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "select-path-button" ),
			jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "use-jvm-button" )
			};
		int yesOrNo = JOptionPane.showOptionDialog(
			_view,
			jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "prompt" ),
			jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX + "prompt-dialog-title" ),
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]
			 );

		if ( yesOrNo == JOptionPane.YES_OPTION ) {
			String scriptFilePath
				 = AntFarmOptionPane.promptForAntScript( _view );
			jEdit.setProperty(
				AntFarmPlugin.OPTION_PREFIX + "command",
				scriptFilePath
				 );
			jEdit.setBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "use-same-jvm", false );
			jEdit.propertiesChanged();
			return scriptFilePath;
		}
		else if ( yesOrNo == JOptionPane.NO_OPTION ) {
			jEdit.setBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "use-same-jvm", true );
			jEdit.propertiesChanged();
		}
		return null;
	}
}

