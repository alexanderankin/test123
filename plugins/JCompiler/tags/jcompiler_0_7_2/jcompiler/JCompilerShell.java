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

// from Java:
import java.io.*;

// from jEdit:
import gnu.regexp.RE;
import gnu.regexp.RESyntax;
import gnu.regexp.REException;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

// from EditBus plugin:
import org.gjt.sp.jedit.DefaultErrorSource;
import org.gjt.sp.jedit.ErrorSource;

// from Console plugin:
import console.Shell;
import console.Console;

// from JCompiler plugin:
import JCompilerPlugin;


/**
 * a JCompiler shell for the Console plugin.
 */
public class JCompilerShell extends Shell implements EBComponent {

    public JCompilerShell() {
        super(JCompilerPlugin.NAME);
        errorSource = new DefaultErrorSource(JCompilerPlugin.NAME);
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
     * print an information message to the Console.
     *
     * @param console the Console
     */
    public void printInfoMessage(Console _console) {
        if (_console != null) {
            _console.printInfo(jEdit.getProperty("jcompiler.msg.info"));
        }
    }

    
    /**
     * execute a command.
     *
     * @param view    the view that was open when the command was invoked.
     * @param command the command.
     * @param console the Console where output should go to.
     */
    public void execute(View view, String command, Console console) {
        this.console = console; // remember console instance
        
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
            printInfoMessage(console);
        } 
        else {
            if (console != null) {
                console.printError("Unknown JCompiler command '" + cmd + "'");
            }
        }
    }

    
    public void stop() {
        if (jthread != null) {
            if (jthread.isAlive()) {
                jthread.stop();
                if (console != null) {
                    console.printError("JCompiler thread killed.");
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
            if (loLine.indexOf("warning:") != -1 
                || loLine.indexOf("caution:") != -1
                || loLine.indexOf("note:") != -1) {
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
        if (console != null) {
            switch (type) {
                case ErrorSource.WARNING:
                    console.printWarning(line);
                    break;
                case ErrorSource.ERROR:
                    console.printError(line);
                    break;
                default:
                    console.printPlain(line);
                    break;
            }
        }
    }

    
    private DefaultErrorSource errorSource = null;
    private CompilerThread jthread = null;
    private Console console = null;
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
                try {
                    PipedInputStream inpipe = new PipedInputStream(outpipe);
                    InputStreamReader in = new InputStreamReader(inpipe);
                    buf = new BufferedReader(in);
                    this.start();
                }
                catch (IOException ioex) {
                    // if there's an exception, the thread is never started.
                }
            }
            public void run() {
                String line;
                if (buf == null) return;
                try {
                    while ((line = buf.readLine()) != null) {
                        printLine(line);
                    }
                    Log.log(Log.DEBUG, this, "ends");
                }
                catch (IOException ioex) {
                    // ignore
                }
            }
            private BufferedReader buf = null;
        }                
    }
}
