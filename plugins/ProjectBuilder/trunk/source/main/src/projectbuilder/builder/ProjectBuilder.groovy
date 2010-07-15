/*
 *  Copyright (c) 2009, Eric Berry <elberry@gmail.com>
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  	* Redistributions of source code must retain the above copyright notice, this
 *  	  list of conditions and the following disclaimer.
 *  	* Redistributions in binary form must reproduce the above copyright notice,
 *  	  this list of conditions and the following disclaimer in the documentation
 *  	  and/or other materials provided with the distribution.
 *  	* Neither the name of the Organization (TellurianRing.com) nor the names of
 *  	  its contributors may be used to endorse or promote products derived from
 *  	  this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package projectbuilder.builder

import javax.swing.JComponent;
import console.Shell;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit as JEDIT
import groovy.swing.SwingBuilder;

/**
 *
 * @author eberry
 */
public class ProjectBuilder extends FactoryBuilderSupport {

    private projectDirectory
    
    private ProjectBuilder() {
        registerFactory("f", new FileFactory())
        registerFactory("file", new FileFactory())
        registerFactory("d", new DirectoryFactory())
        registerFactory("dir", new DirectoryFactory())
        registerFactory("directory", new DirectoryFactory())

        // ID to Object.
        addAttributeDelegate(SwingBuilder.&objectIDAttributeDelegate)
    }

    public static build(String projectName, String projectRoot, Closure closure) {
        println("Project Name: ${projectName} - root: ${projectRoot}")
        ProjectBuilder builder = new ProjectBuilder()

        closure.setDelegate(builder)

        // create our initial directories.
        // the project root
        builder.d(projectRoot, id:"project_root") {
            // and project directory
            d(projectName, id:"project_directory") {
                // handle the rest of the closure
                closure.call(builder)
            }
        }
        
        builder.projectDirectory = builder.project_directory
        
        return builder
    }

    public File getProjectDirectory() {
        return projectDirectory;
    }

    /**
     * Run an external command in the system shell
     * @param cmd the command to run
     * @param dir the directory to run it in
     * TODO: see if there's a method in system shell to change directory directly
     */
    public static void cmd(String cmd, String dir) {
    	View view = JEDIT.getActiveView()
    	JComponent console = view.getDockableWindowManager().getDockable("console")
    	Shell system = Shell.getShell("System")
    	console.run(system, "cd \""+dir+"\"")
    	system.waitFor(console)
    	console.run(system, cmd)
    }
    
    /**
     * Sets the contents between two marks in a file
     * @param file the file to replace contents in
     * @param markStart the start of the mark
     * @param markEnd the end of the mark
     * @param contents the contents to insert
     */
    public static void mark(String file, String markStart, String markEnd, String contents) {
    	def f = new File(file)
    	def start = f.text.indexOf(markStart)
    	def end = f.text.indexOf(markEnd)
    	if (start == -1 || end == -1 || start > end)
    		return
    	f.text = f.text.substring(0, start+markStart.length())+contents+
    		f.text.substring(end)
    }
    
}