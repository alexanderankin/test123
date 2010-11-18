/*
Copyright (C) 2010 Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package perl;

import java.util.Properties;

public class LaunchConfig {
	public String name;
	public String script;
	public String arguments;
	public LaunchConfig(String name, String script,
		String arguments) {
		set(name, script, arguments);
	}
	public LaunchConfig duplicate() {
		return new LaunchConfig(name, script, arguments);
	}
	public void set(String name, String script, String arguments) {
		this.name = name;
		this.script = script;
		this.arguments = arguments;
	}
	public String toString() {
		return name;
	}
	public static LaunchConfig load(String prefix, Properties p) {
		return new LaunchConfig(
			p.getProperty(prefix + "name"),
			p.getProperty(prefix + "script"),
			p.getProperty(prefix + "arguments"));
	}
	public void save(String prefix, Properties p) {
		p.setProperty(prefix + "name", name);
		p.setProperty(prefix + "script", script);
		p.setProperty(prefix + "arguments", arguments);
	}
}
