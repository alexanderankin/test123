/*
Copyright (C) 2007  Shlomy Reinstein

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

package browser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class GlobalLauncher {

	static GlobalLauncher instance = new GlobalLauncher();
	
	static public GlobalLauncher instance() {
		return instance;
	}
	
	public Vector<GlobalRecord> run(String options, String workingDirectory) {
        Vector<GlobalRecord> records = new Vector<GlobalRecord>();
		try {
			String command = "global " + options;
			File dir = new File(workingDirectory);
			Process p = Runtime.getRuntime().exec(command, null, dir);
	        BufferedReader stdInput = new BufferedReader(new 
	                InputStreamReader(p.getInputStream()));
	        // read the output from the command
	        String s;
	        while ((s = stdInput.readLine()) != null)
	        	records.add(new GlobalRecord(s));
	        stdInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return records;
	}
	
}
