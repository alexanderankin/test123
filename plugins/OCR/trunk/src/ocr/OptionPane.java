package ocr;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane
{
	public static final String OPTIONS_OCR_GOCR_PATH = "options.ocr.gocrPath";
	public static final String OPTIONS_OCR_SCREEN_CAPTURE_DELAY = "options.ocr.screenCaptureDelay";
	public static final String OPTIONS_OCR_CONVERTER_PATH = "options.ocr.converterPath";
	JTextField gocrPath;
	JSpinner screenCaptureDelay;
	JTextField converterPath;

	public OptionPane()
	{
		super("ocr");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		gocrPath = new JTextField(jEdit.getProperty(OPTIONS_OCR_GOCR_PATH));
		addComponent("GOCR path:", gocrPath);
		int delay = jEdit.getIntegerProperty(OPTIONS_OCR_SCREEN_CAPTURE_DELAY, 5);
		screenCaptureDelay = new JSpinner(new SpinnerNumberModel(delay, 1, 20, 1));
		addComponent("Screen capture delay:", screenCaptureDelay);
		converterPath = new JTextField(jEdit.getProperty(OPTIONS_OCR_CONVERTER_PATH));
		addComponent("Image converter path:", converterPath);
	}
	public void save()
	{
		jEdit.setProperty(OPTIONS_OCR_GOCR_PATH, gocrPath.getText());
		jEdit.setIntegerProperty(OPTIONS_OCR_SCREEN_CAPTURE_DELAY,
			((Integer) screenCaptureDelay.getValue()).intValue());
		jEdit.setProperty(OPTIONS_OCR_CONVERTER_PATH, converterPath.getText());
	}
}
