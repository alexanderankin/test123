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
package make.provider;

import make.BuildfileProvider;
import make.buildfile.MSBuild;
import org.gjt.sp.jedit.jEdit;

public class MSBuildProvider implements BuildfileProvider {
	public boolean accept(String filename) {
		return filename.matches(jEdit.getProperty("make.buildfile.msbuild", ".*\\.sln|.*\\.csproj"));
	}
	
	public MSBuild createFor(String dir, String filename) {
		return new MSBuild(dir, filename);
	}
}
