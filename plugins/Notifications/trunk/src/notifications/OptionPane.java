package notifications;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane
{

	public static final String PREFIX = "balloon.notification.";
	private static final String TIME_MS_OPTION = PREFIX + "time.ms";
	private static final String TIME_MS_LABEL = TIME_MS_OPTION + ".label";
	private static final String COLOR_OPTION = PREFIX + "color";
	private static final String COLOR_LABEL = COLOR_OPTION + ".label";
	private static final String BALLOON_OPTIONS_LABEL = "balloon.notification.options.label";

	private JSpinner timeMs;
	private JColorChooser color;

	public OptionPane()
	{
		super("notifications");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel balloonPanel = new JPanel(new BorderLayout());
		balloonPanel.setBorder(BorderFactory.createTitledBorder(
			jEdit.getProperty(BALLOON_OPTIONS_LABEL)));
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p.add(new JLabel(jEdit.getProperty(TIME_MS_LABEL)));
		timeMs = new JSpinner(new SpinnerNumberModel(getTimeMs(), 1000, 15000, 100));
		p.add(timeMs);
		balloonPanel.add(p, BorderLayout.NORTH);
		p = new JPanel(new BorderLayout());
		p.add(new JLabel(jEdit.getProperty(COLOR_LABEL)), BorderLayout.NORTH);
		color = new JColorChooser(getColor());
		p.add(color, BorderLayout.CENTER);
		balloonPanel.add(p, BorderLayout.CENTER);
		addComponent(balloonPanel, GridBagConstraints.NONE);
	}

	@Override
	public void _save()
	{
		jEdit.setIntegerProperty(TIME_MS_OPTION, ((Integer)timeMs.getValue()).intValue());
		jEdit.setColorProperty(COLOR_OPTION, color.getColor());
	}

	public static int getTimeMs()
	{
		return jEdit.getIntegerProperty(TIME_MS_OPTION, 5000);
	}

	public static Color getColor()
	{
		return jEdit.getColorProperty(COLOR_OPTION);
	}

}
