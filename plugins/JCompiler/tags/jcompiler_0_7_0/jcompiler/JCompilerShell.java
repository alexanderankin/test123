/*
 * JCompilerShell.java - ConsolePlugin shell for JCompiler
 * Copyright (C) 2000 Dirk Moebius
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

package jcompiler;

import java.io.*;
import gnu.regexp.RE;
import gnu.regexp.RESyntax;
import gnu.regexp.REException;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

/**
 * a JCompiler shell for the Console plugin.
 */
public class JCompilerShell implements Shell, EBComponent {

    public JCompilerShell() {
        errorSource = new DefaultErrorSource("JCompiler");
        EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST, errorSource);
        EditBus.addToBus(errorSource);
        EditBus.addToBus(this);
        propertiesChanged();
    }

    public void handleMessage(EBMessage msg) {
        if (msg instanceof PropertiesChanged) {
            propertiesChanged();
        }
    }

    
    /**
     * print an information message to Output.
     *
     * @param output the output; may be null, if this interface method is
     *               not invoked from ConsolePlugin.
     */
    public void printInfoMessage(Output _output) {
        if (_output != null) {
            output = _output; // remember the output for later use
            output.printInfo(jEdit.getProperty("jcompiler.msg.info"));
        }
    }

    
    /**
     * execute a command.
     *
     * @param view    the view that was open when the command was invoked.
     * @param command the command.
     * @param output  the output; may be null, if this interface method is
     *                not invoked from ConsolePlugin.
     */
    public void execute(View view, String command, Output _output) {
        if (_output != null) {
            output = _output; // remember the output for later use
        }
        if (output == null) {
            Log.log(Log.NOTICE, this, 
                "You should install and enable the Console plugin."); 
        }
        
        stop(); // stop last command
        errorSource.clear();
        
        String cmd = command.trim();
        if ("compile".equals(cmd)) {
            jthread = new CompilerThread(view, false, false);
        }
        else if ("compilepkg".equals(cmd)) {
            jthread = new CompilerThread(view, true, false);
        }
        else if ("rebuildpkg".equals(cmd)) {
            jthread = new CompilerThread(view, true, true);
        }
        else if ("help".equals(cmd)) {
            printInfoMessage(output);
        } 
        else {
            if (output != null) {
                output.printError("Unknown JCompiler command '" + cmd + "'");
            }
        }
    }

    
    public void stop() {
        if (jthread != null) {
            if (jthread.isAlive()) {
                jthread.stop();
                if (output != null) {
                    output.printError("JCompiler thread killed.");
                }
            }
            jthread = null;
        }
    }

    
    public boolean waitFor() {
        if (jthread != null) {
            try {
                jthread.wait();
                jthread = null;
            }
            catch (InterruptedException ie) {
                return false;
            }
        }
        return true;
    }


    private void propertiesChanged() {
        String rstr = jEdit.getProperty("jcompiler.regexp", "(.+):(\\d+):(.+)");
        rfilenamepos = jEdit.getProperty("jcompiler.regexp.filename", "$1");
        rlinenopos = jEdit.getProperty("jcompiler.regexp.lineno", "$2");
        rmessagepos = jEdit.getProperty("jcompiler.regexp.message", "$3");
        try {
            regexp = new RE(rstr, RE.REG_ICASE, RESyntax.RE_SYNTAX_PERL5);
        }
        catch (REException rex) {
            Log.log(Log.ERROR, this, "The regular expression "
                + rstr + " for compiler errors is invalid. Message is: "
                + rex.getMessage() + " at position " + rex.getPosition());
        }
    }
    

    /** 
     * parse the line for errors and send them to ErrorList and Console.
     */
    private void printLine(String line) {
        int type = -1;
        if (regexp != null && regexp.isMatch(line)) {
            String loLine = line.toLowerCase();
            if (loLine.indexOf("warning") != -1 || loLine.indexOf("caution") != -1) {
                type = ErrorSource.WARNING;
            } else {
                type = ErrorSource.ERROR;
            }
            String filename = regexp.substitute(line, rfilenamepos);
            String lineno = regexp.substitute(line, rlinenopos);
            String message = regexp.substitute(line, rmessagepos);
            errorSource.addError(type, filename, 
                Integer.parseInt(lineno) - 1, 0, 0, message);
        }
        if (output != null) {
            switch (type) {
                case ErrorSource.WARNING:
                    output.printWarning(line);
                    break;
                case ErrorSource.ERROR:
                    output.printError(line);
                    break;
                default:
                    output.printPlain(line);
                    break;
            }
        }
    }

    
    private DefaultErrorSource errorSource = null;
    private CompilerThread jthread = null;
    private Output output = null;
    private RE regexp = null;
    private String rfilenamepos;
    private String rlinenopos;
    private String rmessagepos;


    /// wraps the JCompiler run in a thread
    class CompilerThread extends Thread {
        CompilerThread(View view, boolean pkgCompile, boolean rebuild) {
            super();
            this.view = view;
            this.pkgCompile = pkgCompile;
            this.rebuild = rebuild;
            this.setPriority(Thread.MIN_PRIORITY);
            this.start();
        }
        
        public void run() {
            JCompiler jcompiler = new JCompiler();
            Thread outputThread = new OutputThread(jcompiler.getOutputPipe());
            jcompiler.compile(view, view.getBuffer(), pkgCompile, rebuild);
            Log.log(Log.DEBUG, this, "compile thread complete");
            jcompiler = null;
            view = null;
            outputThread = null;
        }
        
        private View view;
        private boolean pkgCompile;
        private boolean rebuild;

        /// this class monitors output created by JCompiler
        class OutputThread extends Thread {
            OutputThread(PipedOutputStream outpipe) {
                this.outpipe = outpipe;
                this.start();
            }
            public void run() {
                try {
                    PipedInputStream inpipe = new PipedInputStream(outpipe);
                    InputStreamReader in = new InputStreamReader(inpipe);
                    BufferedReader buf = new BufferedReader(in);
                    String line;
                    while ((line = buf.readLine()) != null) {
                        printLine(line);
                    }
                    Log.log(Log.DEBUG, this, "ends");
                }
                catch (IOException ioex) {
                    // ignore
                }
            }
            private PipedOutputStream outpipe;
        }                
    }
}
