/*
 * error_list.java
 * Copyright (C) 1999 Slava Pestov
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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.Component;
import org.gjt.sp.jedit.*;

public class error_list extends EditAction
{
	public error_list()
	{
		super("error-list");
	}

	public void actionPerformed(ActionEvent evt)
	{
		View view = getView(evt);
		if(ErrorListPlugin.isErrorListShowing(view))
			ErrorListPlugin.getErrorList(view).close();
		else
			ErrorListPlugin.getErrorList(view);
	}

	public boolean isToggle()
	{
		return true;
	}

	public boolean isSelected(Component comp)
	{
		return ErrorListPlugin.isErrorListShowing(getView(comp));
	}
}
