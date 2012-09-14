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
package make.buildfile;

import errorlist.ErrorSource;
import make.*;
import java.io.*;
import java.util.regex.*;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashMap;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.MiscUtilities;

public class Rake extends Buildfile {
	private String rake;
	private Pattern error;
	
	private String errorPath;
	private int errorLine;
	private String errorMsg;
	private LinkedList<String> errorExtraMsgs;
	
	public Rake(String dir) {
		this(dir, null);
	}
	
	public Rake(String dir, String name) {
		super(dir, name);
		
		// find the rake executable
		this.rake = "rake" + (OperatingSystem.isWindows() ? ".bat" : "");
		
		// TODO: find the rake error pattern
		this.error = Pattern.compile("\\s*\\[.+\\]\\s+(.+?):(\\d+):(.+)");
		
		this.parseTargets();
	}
	
	public String getName() {
		return "Rake";
	}
	
	protected boolean _parseTargets() {
		try {
			Process p = Runtime.getRuntime().exec(new String[] { this.rake, "-f", this.name, "--tasks"}, null, this.dir);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			Pattern pat = Pattern.compile("^rake\\s(\\S+)\\s*?#\\s(.*)$");
			Pattern pat_params = Pattern.compile("(\\S+)\\[(\\S+)\\]");
			
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher mat = pat.matcher(line);
				if (mat.find()) {
					String target = mat.group(1);
					String params = null;
					
					Matcher mat_params = pat_params.matcher(target);
					if (mat_params.find()) {
						target = mat_params.group(1);
						params = mat_params.group(2);
					}
					
					BuildTarget ob = new BuildTarget(target, mat.group(2));
					if (params != null) {
						String[] paramlist = params.split(",");
						for (int i = 0; i<paramlist.length; i++) {
							ob.params.add(paramlist[i]);
						}
					}
					
					this.targets.add(ob);
				}
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected Process _runTarget(BuildTarget target, HashMap<String, String> params) throws IOException {
		String fullTarget = target.name;
		if (target.params.size() > 0) {
			StringBuilder paramBuilder = new StringBuilder();
			Iterator<String> iter = target.params.iterator();
			while (iter.hasNext()) {
				paramBuilder.append(iter.next());
				if (iter.hasNext())
					paramBuilder.append(",");
			}
			
			fullTarget = fullTarget + "[" + paramBuilder.toString() + "]";
		}
		
		return Runtime.getRuntime().exec(new String[] { this.rake, "-f", this.name, fullTarget }, null, this.dir);
	}
	
	protected void _processErrors(String line) {
		// TODO: implement
	}
}
