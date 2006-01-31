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
package p4plugin.action;

import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import common.threads.WorkerThreadPool;

/**
 *  P4 Action that spawns a new thread to wait for the p4 process and
 *	show an error message if something goes wrong. The idea here is
 *	that some action might spawn a long running external process, so
 *	it would be bad to block the AWT thread in these cases. Especially
 *	bad if this process happened to be jEdit connection to the current
 *	instance to edit a file.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public abstract class AsyncP4Action extends AbstractP4Action {

    protected AsyncP4Action() {
        // no-op.
    }

    protected AsyncP4Action(boolean askForChangeList) {
        super(askForChangeList);
    }

    protected AsyncP4Action(String actionName, boolean askForChangeList) {
        super(actionName, askForChangeList);
    }

    /**
     *  Show the change list chooser in the AWT thread if requested,
     *  and then call perforce in the current thread.
     */
    protected void run(ActionEvent ae) {
        if (askForChangeList) {
            // we need to reserve two extra threads in the worker pool,
            // since this method is run from a worker thread and
            // will cause another perforce process to run, which
            // will use two other worker threads.
            WorkerThreadPool.getSharedInstance().ensureCapacity(3);
            CListChooser chooser = new CListChooser(showDefaultCL);
            try {
                SwingUtilities.invokeAndWait(chooser);
            } catch (Exception e) {
                Log.log(Log.ERROR, this, e);
                return;
            }
            if (!chooser.cancelled)
                invokePerforce(chooser.change, ae);
        } else {
            invokePerforce(null, ae);
        }
    }

    /**
     *  Executes the p4 command according to the arguments defined
     *  by the implementation class.
     */
    public void actionPerformed(ActionEvent ae) {
        WorkerThreadPool.getSharedInstance().addRequest(new DeferredRunner(ae));
    }

    /** Runnable used to defer execution of the action. */
    private class DeferredRunner implements Runnable {

        public ActionEvent ae;

        public DeferredRunner(ActionEvent ae) {
            this.ae = ae;
        }

        public void run() {
            AsyncP4Action.this.run(ae);
        }

    }

}

