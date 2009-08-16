package updater;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class InstallLauncher {

	private static JTextArea text;

	public static void main(String [] args)
	{
		if (args.length < 2)
		{
			JOptionPane.showMessageDialog(null,
				"Incorrect command-line for InstallLauncher. Usage:\n" +
				"\tjava -jar InstallLauncher.jar <installer-file> <jEdit installation directory>");
			return;
		}
		final JDialog dialog = new JDialog((JDialog)null, false);
		dialog.setTitle("Updating jEdit");
		dialog.setLayout(new BorderLayout());
		text = new JTextArea(8, 80);
		dialog.add(text, BorderLayout.CENTER);
		text.append("Please wait while an update is being installed...\n");
		JButton ok = new JButton("Ok");
		dialog.add(ok, BorderLayout.SOUTH);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		ok.setEnabled(false);
		ok.setPreferredSize(new Dimension(80, 20));
		dialog.pack();
		dialog.setVisible(true);

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
			text.append("Installation completed. You can now start jEdit.");
			ok.setEnabled(true);
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
					text.append(line + "\n");
			} catch (IOException ioe) {
				ioe.printStackTrace();  
			}
		}
	}
}
