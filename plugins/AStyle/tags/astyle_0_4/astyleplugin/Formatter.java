/*
 * Formatter.java - small extension to the astyle.ASFormatter
 * Copyright (C) 2001 Dirk Moebius
 * Artistic Style (c) 1998-2001 Tal Davidson (davidsont@bigfoot.com)
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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


package astyleplugin;


/**
 * Small extension to the original <code>astyle.ASFormatter</code> that
 * has an additional property <code>formatOnSave</code>.
 *
 * @see astyle.ASFormatter
 */
public class Formatter extends astyle.ASFormatter {

	public boolean getFormatOnSave() {
		return formatOnSave;
	}


	public void setFormatOnSave(boolean state) {
		formatOnSave = state;
	}


	protected boolean formatOnSave = false;

}

