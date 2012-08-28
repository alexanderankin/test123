/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package make;

import java.io.*;
import java.util.LinkedList;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.StatusBar;
import org.gjt.sp.util.ThreadUtilities;

public abstract class Buildfile {
	protected File dir;
	protected String name;
	public LinkedList<BuildTarget> targets;
	
	public Buildfile(String dir, String name) {
		this.dir = new File(dir);
		this.name = name;
	}
	
	public void parseTargets() {
		this.targets = new LinkedList<BuildTarget>();
		this._parseTargets();
	}
	
	public void runTarget(final BuildTarget target) {
		MakePlugin.clearErrors();
		ThreadUtilities.runInBackground(new Thread() {
				public void run() {
					try {
						StatusBar status = jEdit.getActiveView().getStatus();
						Process p = _runTarget(target);
						// TODO: set up an error source so that ErrorList can catch them
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line;
						while ((line = reader.readLine()) != null) {
							System.out.println(line);
							status.setMessageAndClear(line);
							_processErrors(line);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		});
	}
	
	public abstract String getName();
	protected abstract void _parseTargets();
	protected abstract Process _runTarget(BuildTarget target) throws IOException;
	protected abstract void _processErrors(String line);
}
