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

import make.*;
import java.io.*;
import java.util.regex.*;
import java.util.HashMap;

public class Make extends Buildfile {
	public Make(String dir, String name) {
		super(dir, name);
		this.parseTargets();
	}
	
	public String getName() {
		return "Make";
	}
	
	protected boolean _parseTargets() {
		try {
			Process p = Runtime.getRuntime().exec(new String[] { "make", "-r", "-p", "-n", "-f", this.name}, null, this.dir);
			// apparently it hangs if you wait for it. Not sure why.
			//int exitCode = p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			// Start at the '# Files' line
			String line;
			while (!(line = reader.readLine()).equals("# Files")) {
				//System.out.println(line);
			}
			
			boolean notATarget = false;
			Pattern pat = Pattern.compile("^(\\w+?):");
			while ((line = reader.readLine()) != null) {
				if (notATarget) {
					// the previous line said 'not a target', so skip it
					notATarget = false;
					continue;
				} else if (line.equals("# Not a target:")) {
					// skip this line and the next one
					notATarget = true;
					continue;
				}
				
				Matcher mat = pat.matcher(line);
				if (mat.find()) {
					this.targets.add(new BuildTarget(mat.group(1), ""));
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected Process _runTarget(BuildTarget target, HashMap<String, String> params) throws IOException {
		return Runtime.getRuntime().exec(new String[] { "make", "-f", this.name, target.name }, null, this.dir);
	}
	
	protected void _processErrors(String line) {}
}
