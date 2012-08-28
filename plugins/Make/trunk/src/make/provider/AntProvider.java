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
import make.buildfile.Ant;

public class AntProvider implements BuildfileProvider {
	public boolean accept(String filename) {
		return "build.xml".equals(filename);
	}
	
	public Ant createFor(String dir, String filename) {
		return new Ant(dir, filename);
	}
}
