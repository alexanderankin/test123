/*
 * ErrorGutterIcon.java - Error list gutter icon
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011 Shlomy Reinstein
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package errorlist;

import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.ImageIcon;

import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;

/**
  * Gutter Icon to indicate an error.
  * @author Shlomy Reinstein
  */
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

		if (errMsg.length() > 0)
			return errMsg.toString();
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
