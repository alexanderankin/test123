/**
  * TargetParser.java - Ant build utility plugin for jEdit
  * Copyright (C) 2000 Chris Scott
  * Other contributors: Rick Gibbs, Christoph Steinbeck
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

import java.awt.Color;
import java.io.*;
import java.util.*;
import org.apache.tools.ant.*;

/**
 *  Returns a list of targets defined in an ant build file.
 *
 *@author     steinbeck
 *@created    31. Juli 2001
 */
public class TargetParser {

	/**
	 *  Our current message output status. Follows Project.MSG_XXX
	 */
	private int msgOutputLevel = Project.MSG_INFO;

	/**
	 *  The build targets
	 */
	private Hashtable targets = new Hashtable(5);
	private String defaultTarget = null;
	private File buildFile;
	private AntFarmPlugin farm;
        private AntFarm window;
	private boolean debug = false;


	/**
	 *  Constructor for the TargetParser object
	 *
	 *@param  farm  A reference to the parent plugin
	 */
	public TargetParser(AntFarmPlugin farm, AntFarm window) {
		this(farm, window, null, false);
	}


	/**
	 *  Constructor for the TargetExecutor object
	 *
	 *@param  farm       A reference to the parent plugin
	 *@param  buildFile  The build file to be parsed for targets
	 *@param  debug      Should debug output be done?
	 */
	public TargetParser(AntFarmPlugin farm, AntFarm window, File buildFile, boolean debug) {
		this.buildFile = buildFile;
		this.farm = farm;
                this.window = window;
		this.debug = debug;
	}


	/**
	 *  Gets the Targets attribute of the TargetParser object
	 *
	 *@return    The Targets value
	 */
	public Hashtable getTargets() {
		return targets;
	}


	/**
	 *  Gets the DefaultTarget attribute of the TargetParser object
	 *
	 *@return    The DefaultTarget value
	 */
	public String getDefaultTarget() {
		return defaultTarget;
	}


	/**
	 *  Gets the Targets attribute of the TargetParser object
	 *
	 */
	public void parseProject() {

		Project project = new Project();

		Throwable error = null;

		try {
			project.init();
			project.setUserProperty("ant.file", buildFile.getAbsolutePath());

			// first use the ProjectHelper to create the project object
			// from the given build file.
			try {
				Class.forName("javax.xml.parsers.SAXParserFactory");
				ProjectHelper.configureProject(project, buildFile);
			}
			catch (NoClassDefFoundError ncdfe) {
				throw new BuildException("No JAXP compliant XML parser found. See http://java.sun.com/xml for the\nreference implementation.", ncdfe);
			}
			catch (ClassNotFoundException cnfe) {
				throw new BuildException("No JAXP compliant XML parser found. See http://java.sun.com/xml for the\nreference implementation.", cnfe);
			}
			catch (NullPointerException npe) {
				throw new BuildException("No JAXP compliant XML parser found. See http://java.sun.com/xml for the\nreference implementation.", npe);
			}
			targets = project.getTargets();
			defaultTarget = project.getDefaultTarget();
		}
		catch (BuildException be) {
			//System.err.println(be.getMessage());
			farm.handleBuildMessage(window,new BuildMessage("\nBUILD FAILED: " +
					be.getMessage()), Color.red);
		}
		catch (Throwable exc) {
			farm.handleBuildMessage(window,new BuildMessage(exc.getMessage()));
		}
	}

}

