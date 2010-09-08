/*
 * PHPItem.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003-2010 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.phpparser.project.itemfinder;

import javax.swing.*;

/**
 * @author Matthieu Casanova
 */
public interface PHPItem
{
	int CLASS = 1;
	int METHOD = 2;
	int FIELD = 4;
	int INTERFACE = 8;
	int DOCUMENT = 16;// special type for document
	int DEFINE = 32;
	int GLOBAL = 64;
	int INCLUDE = 128;
	int VARIABLE = 256;
	int CLASS_CONSTANT = 512;

	int getItemType();

	String getName();

	/**
	 * Returns the name of the item in lower case.
	 * It will be used to quick find items
	 *
	 * @return the name in lower case
	 */
	String getNameLowerCase();

	int getSourceStart();

	int getBeginLine();

	int getBeginColumn();

	String getPath();

	Icon getIcon();

	String getNamespace();
}
