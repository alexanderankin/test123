package updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public class InstallLauncher {

	public static void main(String [] args)
	{
		JOptionPane.showMessageDialog(null, "Beginning installation");
		if (args.length < 2)
		{
			JOptionPane.showMessageDialog(null,
				"Incorrect command-line for InstallLauncher. Usage:\n" +
				"\tjava -jar InstallLauncher.jar <installer-file> <jEdit installation directory>");
			return;
		}
		String installerFile = args[0];
		String installDir = args[1];
		String [] installerArgs = new String[] { "java", "-jar",
			installerFile, "auto", installDir };
		try {
			Process p = Runtime.getRuntime().exec(installerArgs);
			StreamConsumer osc = new StreamConsumer(p.getInputStream());
			osc.start();
			StreamConsumer esc = new StreamConsumer(p.getErrorStream());
			esc.start();
			p.waitFor();
			JOptionPane.showMessageDialog(null, "Completed installation");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	private static class StreamConsumer extends Thread
	{
		private InputStream is;
		public StreamConsumer(InputStream is) {
			this.is = is;
		}
		public void run() {
			try {
				BufferedReader br = new BufferedReader(
					new InputStreamReader(is));
				String line;
				while ((line = br.readLine()) != null)
					System.err.println(line);
			} catch (IOException ioe) {
				ioe.printStackTrace();  
			}
		}
	}
}
