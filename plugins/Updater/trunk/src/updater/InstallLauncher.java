package updater;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class InstallLauncher
{
	// Commands provided via standard input
	public static final String LAUNCH_INSTALLER_NOW = "Launch Installer Now";
	public static final String END_EXECUTION = "Exit Now";
	public static final String PROGRESS_INDICATOR = "*** Progress: ";
	private static JTextArea text;
	private static JButton ok;
	private static int pos;
	private static Properties props;

	public static void main(String [] args)
	{
		props = new Properties();
		try
		{
			InputStream in = InstallLauncher.class.getResourceAsStream(
				"/Updater.props");
			props.load(in);
			in.close();
		}
		catch(IOException io)
		{
			System.err.println("Error reading 'Updater.props':");
			io.printStackTrace();
		}
		
		final JDialog dialog = new JDialog((JDialog)null, false);
		dialog.setTitle(props.getProperty("updater.msg.updateDialogTitle"));
		dialog.setLayout(new BorderLayout());
		text = new JTextArea(8, 80);
		dialog.add(new JScrollPane(text), BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		ok = new JButton(props.getProperty("updater.msg.updateDialogCloseButton"));
		buttonPanel.add(ok);
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		ok.setEnabled(false);
		dialog.pack();
		dialog.setVisible(true);

		// Show messages from jEdit until told to launch installer
		String line;
		InputStreamReader in = new InputStreamReader(System.in);
		String installerFile = null;
		String installDir = null;
		try
		{
			while ((line = readLine(in)) != null)
			{
				if (line.equals(END_EXECUTION))
				{
					ok.setEnabled(true);
					return;
				}
				if (line.startsWith(PROGRESS_INDICATOR))
					appendProgress(line.substring(PROGRESS_INDICATOR.length()));
				else if (line.equals(LAUNCH_INSTALLER_NOW))
					break;
				else
					appendText(line);
			}
			if (checkNullInput(line))
				return;
			// Now get the installer file and the install directory
			installerFile = readLine(in);
			if (checkNullInput(installerFile))
				return;
			installDir = readLine(in);
			if (checkNullInput(installDir))
				return;
			in.close();
		}
		catch (IOException e1)
		{
		}
		appendText(props.getProperty("updater.msg.waitForInstall"));
		String [] installerArgs = new String[] { "java", "-jar",
			installerFile, "auto", installDir };
		try {
			Process p = Runtime.getRuntime().exec(installerArgs);
			StreamConsumer osc = new StreamConsumer(p.getInputStream());
			osc.start();
			StreamConsumer esc = new StreamConsumer(p.getErrorStream());
			esc.start();
			p.waitFor();
			text.append(props.getProperty("updater.msg.installationComplete"));
			ok.setEnabled(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static String readLine(InputStreamReader in)
	{
		StringBuilder sb = new StringBuilder();
		int i;
		try
		{
			while ((i = in.read()) != -1)
			{
				if (i == '\n')
					break;
				sb.append((char) i);
			}
		}
		catch (IOException e)
		{
			return null;
		}
		return (i == -1) ? null : sb.toString();
	}

	private static boolean checkNullInput(String line)
	{
		if (line != null)
			return false;
		appendText("\n" + props.getProperty("updater.msg.updateErrorAbort"));
		ok.setEnabled(true);
		return true;
	}

	private static void appendProgress(final String s)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				text.replaceRange(s, pos, text.getText().length());
			}
		});
	}

	private static void appendText(final String s)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				text.append(s);
				if (! s.endsWith("\n"))
					text.append("\n");
				text.setCaretPosition(text.getText().length());
				pos = text.getCaretPosition();
			}
		});
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
					appendText(line + "\n");
			} catch (IOException ioe) {
				ioe.printStackTrace();  
			}
		}
	}
}
