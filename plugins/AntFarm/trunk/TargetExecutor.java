/**
  * TargetExecutor.java - Ant build utility plugin for jEdit
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
import java.awt.Color;
import java.io.*;
import java.util.*;
import org.apache.tools.ant.*;

public class TargetExecutor
{

  /** Our current message output status. Follows Project.MSG_XXX */
  private int msgOutputLevel = Project.MSG_INFO;

  /** The build targets */
  private Vector targets = new Vector(5);

  private File buildFile;
  private AntFarmPlugin farm;
  private String targetName;
  private boolean debug = false;

  public TargetExecutor( AntFarmPlugin farm, File buildFile )
  {
    this( farm, buildFile, null, false );
  }

  public TargetExecutor( AntFarmPlugin farm, File buildFile, String targetName )
  {
    this(farm, buildFile, targetName, false );
  }

  public TargetExecutor( AntFarmPlugin farm, File buildFile, String targetName, boolean debug )
  {
    this.buildFile = buildFile;
    this.farm = farm;
    this.targetName = targetName;
    this.debug = debug;
  }

  public void execute() throws Exception
  {
    // Add Timer for the build process.
    Date startTime = new Date();
    farm.handleBuildMessage(
      new BuildMessage( "Building at " + startTime + " ..."), Color.blue
    );

    PrintStream out = System.out;
    PrintStream err = System.err;

    if ( !debug )
    {
      System.setOut( this.getStdOut() );
      System.setErr( this.getStdErr() );
    }


    if (msgOutputLevel >= Project.MSG_INFO)
    {
      farm.handleBuildMessage( new BuildMessage( "Buildfile: " + buildFile) );
    }

    Project project = new Project();

    Throwable error = null;
	AntFarmLogger logger = null;
    try
    {
      logger = new AntFarmLogger( farm );
      logger.setOutputPrintStream( this.getStdOut() );
      logger.setErrorPrintStream( this.getStdErr() );
      project.addBuildListener( logger );
      project.init();

      // set user-define properties
      //Enumeration e = definedProps.keys();
      //while (e.hasMoreElements()) {
      //    String arg = (String)e.nextElement();
      //    String value = (String)definedProps.get(arg);
      //    project.setUserProperty(arg, value);
      //}

      project.setUserProperty( "ant.file" , buildFile.getAbsolutePath() );

      // first use the ProjectHelper to create the project object
      // from the given build file.
      try
      {
        Class.forName("javax.xml.parsers.SAXParserFactory");
        ProjectHelper.configureProject(project, buildFile);
      }
      catch (NoClassDefFoundError ncdfe)
      {
        throw new BuildException("No JAXP compliant XML parser found. See http://java.sun.com/xml for the\nreference implementation.", ncdfe);
      }
      catch (ClassNotFoundException cnfe)
      {
        throw new BuildException("No JAXP compliant XML parser found. See http://java.sun.com/xml for the\nreference implementation.", cnfe);
      }
      catch (NullPointerException npe)
      {
        throw new BuildException("No JAXP compliant XML parser found. See http://java.sun.com/xml for the\nreference implementation.", npe);
      }

      // make sure that we have a target to execute
      if (targetName == null || targetName.length() == 0)
      {
        targetName = project.getDefaultTarget();
      }

      project.executeTarget(targetName);

      //assume that if we are here, then the build was successful

      Date finishTime = new Date();
      long difference = finishTime.getTime() - startTime.getTime();

      farm.handleBuildMessage( new BuildMessage("BUILD SUCCESSFUL: Completed in " +
                               formatTime(difference)), Color.blue);

    }
    catch (BuildException be)
    {
      System.err.println(be.getMessage());
      farm.handleBuildMessage( new BuildMessage( "\nBUILD FAILED: " +
                               be.toString()), Color.red );
    }
    catch (Throwable exc)
    {
      farm.handleBuildMessage( new BuildMessage( exc.toString() ));
      exc.printStackTrace(logger.getErrorPrintStream());
    }
    finally
    {
      System.setOut( out );
      System.setErr( err );
    }
  }


  /**
   * Returns a writer in which external classes can send
   * <code>String</code> to make them being displayed in the
   * console as standard output.
   */
  public PrintStream getStdOut()
  {
    return new BuildStream( farm );
  }

  /**
   * Returns a writer in which external classes can send
   * <code>String</code> to make them being displayed in the
   * console as error output.
   */
  public PrintStream getStdErr()
  {
    return new BuildStream( farm );
  }

  private static String formatTime(long millis)
  {
    long seconds = millis / 1000;
    long minutes = seconds / 60;

    if (minutes > 0)
    {
      return Long.toString(minutes) + " minute"
             + (minutes == 1 ? " " : "s ")
             + Long.toString(seconds % 60) + " second"
             + (seconds % 60 == 1 ? "" : "s");
    }
    else
    {
      return Long.toString(seconds) + " second"
             + (seconds % 60 == 1 ? "" : "s");
    }
  }
}
