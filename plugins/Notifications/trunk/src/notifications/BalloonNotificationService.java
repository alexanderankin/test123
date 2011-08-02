package notifications;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.notification.DefaultNotificationService;
import org.gjt.sp.jedit.gui.notification.NotificationService;

public class BalloonNotificationService extends NotificationService
{
	static private final String BALLOON_TIME_MS_PROP = "balloon.notification.time.ms";
	static private final int BALLOON_TIME_MS_DEFAULT = 5000;
	static private final String BALLOON_COLOR_PROP = "balloon.notification.color";
	static private final Color BALLOON_COLOR_DEFAULT = Color.yellow;
	private Object errorLock = new Object();
	private boolean error = false;
	private BalloonFrame frame;
	private final Vector<ErrorParameters> errors = new Vector<ErrorParameters>();
	
	@SuppressWarnings("serial")
	public class BalloonFrame extends JFrame
	{
		private int num = 0;
		private JPanel p;
		private final static int BalloonWidth = 500;
		private final static int BalloonHeight = 80;
		private final static int MaxHeight = 400;
		private final Dimension size = new Dimension(BalloonWidth, 0);
		private Point bottomRight = new Point();

		public class Balloon extends JPanel
		{
			private Timer timer;
			private static final int MaxMessageLines = 2;
			Balloon(final ErrorParameters entry)
			{
				setBorder(BorderFactory.createEtchedBorder());
				setLayout(new BorderLayout());
				Color c = jEdit.getColorProperty(BALLOON_COLOR_PROP, BALLOON_COLOR_DEFAULT);
				JPanel top = new JPanel(new BorderLayout());
				add(top, BorderLayout.NORTH);
				JToolBar tb = new JToolBar();
				top.add(tb, BorderLayout.WEST);
				JButton extend = new JButton("+");
				tb.add(extend);
				extend.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						DefaultNotificationService.instance().notifyError(
							entry.comp, entry.path, entry.messageProp, entry.args);
					}
				});
				JLabel path = new JLabel("<html><body><b>" + entry.path + "</b></html>");
				path.setBorder(BorderFactory.createLineBorder(Color.black));
				top.add(path, BorderLayout.CENTER);
				JTextArea ta = new JTextArea();
				ta.setBackground(c);
				for (int i = 0; i < MaxMessageLines; i++)
				{
					ta.append(entry.args[i].toString());
					if (i < MaxMessageLines - 1)
						ta.append("\n");
				}
				ta.setEditable(false);
				add(ta, BorderLayout.CENTER);
				int timeMs = jEdit.getIntegerProperty(BALLOON_TIME_MS_PROP,
					BALLOON_TIME_MS_DEFAULT);
				timer = new Timer(timeMs, new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								removeBalloon(Balloon.this);
							}
						});
					}
				});
				timer.setRepeats(false);
				timer.restart();
			}
		}

		public BalloonFrame()
		{
			setAlwaysOnTop(true);
			setUndecorated(true);
			setLayout(new BorderLayout());
			p = new JPanel();
			add(new JScrollPane(p, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
			p.setLayout(new GridLayout(0, 1));
		}

		public void setOwner(Frame owner)
		{
			bottomRight.x = owner.getX() + owner.getWidth();
			bottomRight.y = owner.getY() + owner.getHeight();
		}

		private void adjustSize() {
			size.height = num * BalloonHeight;
			if (size.height > MaxHeight)
				size.height = MaxHeight;
			setSize(size);
			setLocation(bottomRight.x - size.width, bottomRight.y - size.height);
		}

		public void addBalloons(Vector<ErrorParameters> entries)
		{
			num += entries.size();
			for (ErrorParameters entry: entries)
				p.add(new Balloon(entry));
			ErrorParameters lastError = entries.lastElement();
			if (lastError != null)
			{
				Frame f = JOptionPane.getFrameForComponent(lastError.comp);
				if (f != null)
					setOwner(f);
			}
			adjustSize();
			setVisible(true);
		}

		public void removeBalloon(Balloon b)
		{
			p.remove(b);
			if (num > 0)
				num--;
			if (num == 0)
			{
				dispose();
				setVisible(false);
				clear();
			}
			else
				adjustSize();
		}
	}

	private void clear()
	{
		synchronized (errorLock)
		{
			error = false;
		}
	}

	public boolean unnotifiedErrors()
	{
		return error;
	}

	public void notifyError(Component comp, String path, String messageProp,
		Object[] args)
	{
		synchronized(errorLock)
		{
			error = true;
			errors.add(new ErrorParameters(comp,path,messageProp,args));
			if(errors.size() == 1)
			{
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run()
					{
						if (frame == null)
							frame = new BalloonFrame();
						synchronized(errorLock)
						{
							frame.addBalloons(errors);
							errors.clear();
						}
					}
				});
			}
		}
	}

}
