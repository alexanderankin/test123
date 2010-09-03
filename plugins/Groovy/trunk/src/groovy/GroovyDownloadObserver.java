package groovy;
/**
 * @author Damien Radtke
 * class GroovyDownloadObserver
 * TODO: comment
 */
//{{{ Imports
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.StatusBar;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.ProgressObserver;
//}}}
public class GroovyDownloadObserver extends JDialog implements ProgressObserver {
	
	private View view;
	private JLabel msg;
	
	private InputStream in;
	private OutputStream out;
	private JButton cancel;
	private boolean done;
	private boolean canceled;
	
	public GroovyDownloadObserver(View view, InputStream in,
		OutputStream out, String title)
	{
		super(view, title, false);
		this.view = view;
		this.in = in;
		this.out = out;
		done = false;
		canceled = false;
		
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		
		JPanel msgPanel = new JPanel();
		msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.X_AXIS));
		msgPanel.add(msg = new JLabel());
		msgPanel.add(Box.createHorizontalGlue());
		/*
		cards = new JPanel(new CardLayout());
		cards.add(progress, "PROGRESS");
		cards.add(msg = new JLabel(), "MESSAGE");
		*/
		
		JPanel cancelPanel = new JPanel();
		cancelPanel.setLayout(new BoxLayout(cancelPanel, BoxLayout.X_AXIS));
		cancelPanel.add(Box.createHorizontalGlue());
		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(new CancelListener());
		cancelPanel.add(cancel);
		cancelPanel.add(Box.createHorizontalGlue());
		
		setContentPane(content);
		content.add(msgPanel);
		content.add(cancelPanel);
		
		setSize(new Dimension(400, 75));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocation(100, 100);
		setVisible(true);
	}
	
	public void setMaximum(long value) {}
	public void setStatus(String status) {}
	
	public void setMessage(String message) {
		msg.setText(message);
	}
	
	public void setValue(long value) {
		//System.out.println(value);
		msg.setText("Downloading ... "+value);
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public void done() {
		done = true;
		cancel.setText("Close");
	}
	
	class CancelListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			if (!done) {
				int answer = JOptionPane.showConfirmDialog(
							jEdit.getActiveView(),
							"Are you sure you want to cancel?",
							"Cancel Confirmation",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
				if (answer == JOptionPane.YES_OPTION) {
					IOUtilities.closeQuietly(in);
					IOUtilities.closeQuietly(out);
					canceled = true;
					dispose();
				}
			} else {
				dispose();
			}
		}
	}
	
}
