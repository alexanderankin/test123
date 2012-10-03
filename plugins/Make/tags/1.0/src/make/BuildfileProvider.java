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


public interface BuildfileProvider {
	/**
	 * Returns true if the provided filename is a valid name
	 * for this buildfile. This is only a suggestion, though,
	 * and is only used in MakePlugin.getBuildfileForPath().
	 */
	public boolean accept(String filename);

	/**
	 * Should return a new instance of your buildfile, given
	 * a full path split by directory and filename.
	 */
	public Buildfile createFor(String dir, String filename);
}
