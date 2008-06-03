package errorlist;

import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.ImageIcon;

import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;

public class ErrorGutterIcon extends TextAreaExtension {

	private EditPane editPane;
	private static final int FOLD_MARKER_SIZE = 12;
		// Taken from Gutter... Unfortunately it is private there
	
	//{{{ ErrorGutterIcon constructor
	public ErrorGutterIcon(EditPane editPane)
	{
		this.editPane = editPane;
	} //}}}
	
	public String getToolTipText(int x, int y) {
		ErrorSource[] errorSources = ErrorSource.getErrorSources();
		if(!editPane.getBuffer().isLoaded())
			return null;

		JEditTextArea textArea = editPane.getTextArea();

		int offset = textArea.xyToOffset(x,y);
		if(offset == -1)
			return null;

		int line = textArea.getLineOfOffset(offset);

		StringBuffer errMsg = new StringBuffer();
		for(int i = 0; i < errorSources.length; i++)
		{
			ErrorSource.Error[] lineErrors =
				errorSources[i].getLineErrors(
				editPane.getBuffer().getSymlinkPath(),
				line,line);

			if(lineErrors == null)
				continue;

			errMsg.append("<html>");
			for(int j = 0; j < lineErrors.length; j++)
			{
				ErrorSource.Error error = lineErrors[j];
				errMsg.append(error.getErrorMessage());
                errMsg.append("<br>");
                for(String extra : error.getExtraMessages())
                {
                    errMsg.append("__");
                    errMsg.append(extra);
                    errMsg.append("<br>");                    
                }
			}
			errMsg.append("</html>");
		}

		return errMsg.toString();
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
			ImageIcon icon = isError ? ErrorList.ERROR_ICON : ErrorList.WARNING_ICON;
			// Center the icon in the gutter line
			int lineHeight = textArea.getPainter().getFontMetrics().getHeight();
			Point iconPos = new Point(
					(FOLD_MARKER_SIZE - icon.getIconWidth()) / 2,
					y + (lineHeight - icon.getIconHeight()) / 2);
			gfx.drawImage(icon.getImage(), iconPos.x, iconPos.y, null);
		}
	}

}
