/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2005 Marcelo Vanzin
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
package p4plugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;

import common.threads.WorkerThreadPool;
import common.threads.WorkRequest;

import p4plugin.config.P4Config;
import p4plugin.config.P4GlobalConfig;

/**
 *  A wrapper around the perforce executable.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class Perforce {

    private String          cmd;
    private String[]        args;

    private Process         perforce;
    private StreamReader    stderr;
    private StreamReader    stdout;

    private WorkRequest     stderrReq;
    private WorkRequest     stdoutReq;

    /**
     *  Creates a new wrapper around the p4 command, that will
     *  execute the given command with the given arguments.
     */
    public Perforce(String cmd, String[] args) {
        if (cmd == null)
            throw new IllegalArgumentException("command is null");
        this.cmd    = cmd;
        this.args   = args;
    }

    /**
     *  Executes the P4 commmand, returning a process on success.
     *  If an error occurs, a message will be shown to the user
     *  and null will be returned.
     *
     *  <p>The view is also used to get the current active project
     *  and figuring out information about parameters to the p4
     *  executable, such as user, client and editor info.</p>
     *
     *  @since      P4P 0.1
     */
    public Perforce exec(View view) throws IOException {
        if (perforce != null)
            throw new IllegalStateException("can't reuse Perfoce objects");

        int size = 2;
        if (args != null) size += args.length;
        String[] p4cmd = new String[size];

        p4cmd[0] = P4GlobalConfig.getInstance().getPerforcePath();
        if (p4cmd[0] == null)
            throw new IllegalStateException(jEdit.getProperty("p4plugin.no_p4_config"));
        p4cmd[1] = cmd;
        if (args != null) {
            System.arraycopy(args, 0, p4cmd, 2, args.length);
        }

        // print the command to the shell instance
        StringBuffer cmdstr = new StringBuffer("> ");
        for (int i = 0; i < p4cmd.length; i++)
            cmdstr.append(p4cmd[i]).append(" ");
        cmdstr.setLength(cmdstr.length() - 1);
        P4Shell.writeToShell(cmdstr.toString());

        // Try to find the project for the view and see if it has
        // the configuration for perforce.
        String[] envp = null;
        P4Config usercfg = P4Config.getProjectConfig(view);
        if (usercfg != null) {
            envp = usercfg.getEnv();
        }

        // try to run the command.
        if (envp != null) {
            perforce = Runtime.getRuntime().exec(p4cmd, envp);
        } else {
            perforce = Runtime.getRuntime().exec(p4cmd);
        }
        stderr = new StreamReader(perforce.getErrorStream(), perforce.getOutputStream());
        stdout = new StreamReader(perforce.getInputStream(), perforce.getOutputStream());

        WorkRequest[] reqs =
            WorkerThreadPool.getSharedInstance().addRequests(
                new Runnable[] { stdout, stderr });
        stdoutReq = reqs[0];
        stderrReq = reqs[1];
        return this;
    }

    public void waitFor() throws InterruptedException {
        perforce.waitFor();
        stdoutReq.waitFor();
        stderrReq.waitFor();
    }

    public String getOutput() {
        if (stdout == null)
            throw new IllegalStateException("command not being executed");
        if (!stdoutReq.isDone())
            throw new IllegalStateException("still reading from process");
        return stdout.getData();
    }

    public String getError() {
        if (stderr == null)
            throw new IllegalStateException("command not being executed");
        if (!stderrReq.isDone())
            throw new IllegalStateException("still reading from process");
        return stderr.getData();
    }

    public Process getProcess() {
        return perforce;
    }

    public boolean isSuccess() {
        return (perforce.exitValue() == 0);
    }

    public void showError(View v) {
        String msg = "Exit status: " + perforce.exitValue() + "\n\n" + getError();
        showDialog(v, msg);
        Log.log(Log.ERROR, this, getError());
    }

    private void showDialog(View v, String msg) {
        JTextArea message = new JTextArea(24, 80);
        message.setText(msg);
        JOptionPane.showMessageDialog(v,
            new JScrollPane(message),
            jEdit.getProperty("p4plugin.p4_error.title"),
            JOptionPane.ERROR_MESSAGE);
    }

    public void processOutput(Visitor visitor) {
        // break the list into individual directories.
        try {
            BufferedReader br = new BufferedReader(new StringReader(getOutput()));
            String line;
            while ((line = br.readLine()) != null)
                if (!visitor.process(line))
                    break;
            br.close();
        } catch (IOException ioe) {
            // not gonna happen for StringReader.
        }
    }

    private class StreamReader implements Runnable {

        private boolean         foundError;
        private InputStream     input;
        private OutputStream    output;

        private StringBuffer    data;

        public StreamReader(InputStream in, OutputStream out) {
            this.input  = in;
            this.output = out;

            this.data       = new StringBuffer();
            this.foundError = false;
        }

        public void run() {
            String outData = null;
            try {
                byte[] buf = new byte[128];
                while (true) {
                    int read = input.read(buf);
                    if (read == -1)
                       break;

                    for (int i = 0; i < read; i++)
                        data.append((char)buf[i]);

                    if (!foundError && data.toString().indexOf("Hit return to continue") >= 0) {
                        foundError = true;
                        try {
                            SwingUtilities.invokeAndWait(
                                new Runnable() {
                                    public void run() {
                                        showDialog(jEdit.getActiveView(),
                                                   jEdit.getProperty("p4plugin.retry_msg"));
                                    }
                                }
                            );
                        } catch (Exception e) {
                            Log.log(Log.WARNING, this, e);
                        }
                        outData = "\n";
                    }
                    if (outData != null) {
                        output.write(outData.getBytes());
                        output.flush();
                        outData = null;
                    }
                }
            } catch (IOException ioe) {
                Log.log(Log.DEBUG, this, ioe);
            }

        }

        public String getData() {
            return data.toString();
        }

    }

    /**
     *  Interface to implement a "visitor" pattern and make it easier to
     *  process the output of the command (less "cookie cutter" code).
     */
    public static interface Visitor {

        /**
         *  Called once for every line in the output.
         *
         *  @return Whether to continue reading the output.
         */
        public boolean process(String line);

    }

}

