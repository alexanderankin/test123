package ocr;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane
{
	public static final String OPTIONS_OCR_GOCR_PATH = "options.ocr.gocrPath";
	public static final String OPTIONS_OCR_CONVERTER_PATH = "options.ocr.converterPath";
	JTextField gocrPath;
	JTextField converterPath;

	public OptionPane()
	{
		super("ocr");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		gocrPath = new JTextField(jEdit.getProperty(OPTIONS_OCR_GOCR_PATH));
		addComponent("GOCR path:", gocrPath);
		converterPath = new JTextField(jEdit.getProperty(OPTIONS_OCR_CONVERTER_PATH));
		addComponent("Image converter path:", converterPath);
	}
	public void _save()
	{
		jEdit.setProperty(OPTIONS_OCR_GOCR_PATH, gocrPath.getText());
		jEdit.setProperty(OPTIONS_OCR_CONVERTER_PATH, converterPath.getText());
	}
}
