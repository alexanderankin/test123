/*
 * FormatterBeanInfo.java - bean info for the Formatter class
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


import java.beans.*;


public class FormatterBeanInfo extends astyle.ASFormatterBeanInfo {

	public FormatterBeanInfo() {
		super();

		addPropertyDescription(
			"formatOnSave",
			"Format On Save",
			"Whether code should be automatically be beautified when the " +
			"current buffer is saved.\n" +
			"This applies to buffers in 'c', 'c++' or 'java' mode only. " +
			"Buffers with other modes remain unaffected.",
			null,
			Formatter.class
		);
	}


	public BeanDescriptor getBeanDescriptor() {
		return new BeanDescriptor(Formatter.class);
	}

}
