package errorlist;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.ImageIcon;

import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;

public class ErrorGutterIcon extends TextAreaExtension {

	private EditPane editPane;

	//{{{ ErrorGutterIcon constructor
	public ErrorGutterIcon(EditPane editPane)
	{
		this.editPane = editPane;
	} //}}}
	
	public String getToolTipText(int x, int y) {
		return null;
	}

	public void paintValidLine(Graphics2D gfx, int screenLine,
			int physicalLine, int start, int end, int y) {
		ErrorSource[] errorSources = ErrorSource.getErrorSources();
		if(errorSources == null)
			return;

		for (int i = 0; i < errorSources.length; i++)
		{
			ErrorSource.Error[] errors = errorSources[i].getLineErrors(
				editPane.getBuffer().getSymlinkPath(), physicalLine, physicalLine);
			if(errors == null)
				continue;
			boolean isError = false;
			for (int j = 0; j < errors.length; j++)
			{
				if (errors[j].getErrorType() == ErrorSource.ERROR)
				{
					isError = true;
					break;
				}
			}
			JEditTextArea textArea = editPane.getTextArea();
			Point p = textArea.offsetToXY(textArea.getLineStartOffset(physicalLine));
			ImageIcon icon = isError ? ErrorList.ERROR_ICON : ErrorList.WARNING_ICON;
			gfx.setColor(Color.blue);
			gfx.drawImage(icon.getImage(), p.x, p.y, null);
		}
	}

}
