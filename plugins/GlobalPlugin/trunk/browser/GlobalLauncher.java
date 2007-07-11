package browser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class GlobalLauncher {

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
