/*
 * AntFarmLogger.java - Ant build utility plugin for jEdit
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
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 * any, must include the following acknowlegement:
 * "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowlegement may appear in the software itself,
 * if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 * Foundation" must not be used to endorse or promote products derived
 * from this software without prior written permission. For written
 * permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 * nor may "Apache" appear in their names without prior written
 * permission of the Apache Group.
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

import java.io.*;
import java.util.*;
import org.apache.tools.ant.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 *  Description of the Class
 *
 *@author     steinbeck
 *@created    10. August 2001
 */
public class SimpleAntBridge
		 implements AntBridge
{

	private boolean debug = true;
	private Project project;
	private String buildFile;
	private AntFarmPlugin plugin;
	private AntFarmLogger logger;


	/**
	 *  Create a new <code>SimpleAntBridge</code> .
	 *
	 *@since
	 */
	public SimpleAntBridge() {
		project = null;
	}


	/**
	 *  Set the {@link AntFarmPlugin}.
	 *
	 *@param  aPlugin  The new Plugin value
	 *@since
	 */
	public void setPlugin(AntFarmPlugin aPlugin) {
		plugin = aPlugin;
		logger = new AntFarmLogger(plugin, plugin.getAntFarm());
		logger.setOutputPrintStream(getStdOut());
		logger.setErrorPrintStream(getStdErr());
	}


	/**
	 *  Returns the currently used build file.
	 *
	 *@return    The BuildFile value
	 *@since
	 */
	public String getBuildFile() {
		return buildFile;
	}


	/**
	 *  Returns an array of target names.
	 *
	 *@return    The Targets value
	 *@since
	 */
	public String[] getTargets() {
		if (project == null)
		{
			return new String[0];
		}
		Map targets = project.getTargets();

		List targetNames = new ArrayList(targets.size());

		for (Iterator i = targets.keySet().iterator(); i.hasNext(); )
		{
			targetNames.add(i.next());
		}

		Collections.sort(targetNames);
		String[] arr = new String[targetNames.size()];
		return (String[]) targetNames.toArray(arr);
	}

	/**
	 *  Gets the DefaultTarget attribute of the TargetParser object
	 *
	 *@return    The DefaultTarget value
	 */
	public String getDefaultTarget() {
		if (project == null) return null;
		return project.getDefaultTarget();
	}


	
	/**
	 *  Load the specified build file.
	 *
	 *@param  buildFile  Description of Parameter
	 *@since
	 */
	public void loadBuildFile(String buildFile) {
		this.buildFile = buildFile;
		File buildFileObject = new File(buildFile);
		if (!buildFileObject.exists() || buildFileObject.isDirectory())
		{
			// TODO: Log to AntFarmLogger.
			return;
		}

		project = new Project();

		try
		{
			project.addBuildListener(logger);
			project.init();

			project.setUserProperty("ant.file", buildFileObject.getAbsolutePath());
			//project.setProperty("ant.home", jEdit.getProperty("ant.home"));

			ProjectHelper.configureProject(project, buildFileObject);
			// TODO: Notify that build file was loaded successfully.

		}
		catch (Throwable t)
		{
			// TODO: Log to AntFarmLogger.
			Log.log(Log.WARNING, this, t);
			project = null;
		}

	}


	/**
	 *  Execute the specified target.
	 *
	 *@param  targetName  Description of Parameter
	 *@since
	 */
	public void executeTarget(String targetName) {
		PrintStream out = System.out;
		PrintStream err = System.err;

		if (!debug)
		{
			System.setOut(getStdOut());
			System.setErr(getStdErr());
		}

		Throwable error = null;

		try
		{
			fireBuildStarted();

			if (targetName == null || targetName.trim().length() == 0)
			{
				targetName = project.getDefaultTarget();
			}

			project.executeTarget(targetName);

		}
		catch (Throwable t)
		{
			error = t;
			Log.log(Log.WARNING, this, t);

		}
		finally
		{
			fireBuildFinished(error);
		}
		/*
		 *
		 * farm.handleBuildMessage( new BuildMessage( "\nBUILD FAILED: " +
		 * be.getMessage()), Color.red );
		 *
		 * } catch(Throwable exc) {
		 * error = exc;
		 * farm.handleBuildMessage( new BuildMessage( exc.getMessage() ));
		 *
		 * } finally {
		 * project.fireBuildFinished( error );
		 * System.setOut( out );
		 * System.setErr( err );
		 * }
		 */
	}


	/**
	 *  Fire the build started event to the {@link AntFarmLogger}. This is hack
	 *  since <code>Project</code> 's <code>fireBuildXXX</code> are declared as <b>
	 *  protected</b> .
	 *
	 *@since
	 */
	protected void fireBuildStarted() {
		logger.buildStarted(new BuildEvent(project));
	}


	/**
	 *  Returns a writer in which external classes can send <code>String</code> to
	 *  make them being displayed in the console as standard output.
	 *
	 *@return    The StdOut value
	 *@since
	 */
	private PrintStream getStdOut() {
		return new BuildStream(plugin, plugin.getAntFarm());
	}


	/**
	 *  Returns a writer in which external classes can send <code>String</code> to
	 *  make them being displayed in the console as error output.
	 *
	 *@return    The StdErr value
	 *@since
	 */
	private PrintStream getStdErr() {
		return new BuildStream(plugin, plugin.getAntFarm());
	}


	/**
	 *  Fire the build finished event to the {@link AntFarmLogger}. This is hack
	 *  since <code>Project</code> 's <code>fireBuildXXX</code> are declared as <b>
	 *  protected</b> .
	 *
	 *@param  exception  Description of Parameter
	 *@since
	 */
	private void fireBuildFinished(Throwable exception) {
		BuildEvent event = new BuildEvent(project);
		event.setException(exception);
		logger.buildFinished(event);
	}
}

