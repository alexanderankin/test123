/*
 *  ${filename} -
 *  Copyright (C) 2002  Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jimporter.grouping;

import org.gjt.sp.jedit.jEdit;

/**
 * A package group item is currently used to represent either a single class or
 * group of classes that should be grouped together in the import list.
 *
 * @author Matthew Flower
 */
public class PackageGroupItem implements ImportGroupItem {
	private String packagePattern;

	/**
	 * Create a new instance of PackageGroupItem.
	 *
	 * @param packagePattern a <code>String</code> value containing the name of
	 * a class or a package in package.* format.
	 */
	public PackageGroupItem(String packagePattern) {
		this.setPackagePattern(packagePattern);
	}

	/**
	 * Get a string representation of this package that will be displayed in the
	 * Import group option list in the options dialog.
	 *
	 * @return a <code>String</code> representation of the package name.
	 */
	public String toString() {
		return this.packagePattern;
	}

	/**
	 * Store this grouping item in the jEdit properties so it can persist between
	 * sessions.
	 *
	 * @param itemNumber an <code>int</code> value indicating which item this is
	 * in order.  This is important because the group item needs to save this
	 * information as well -- there are potentially several items and order is
	 * of utmost importance.
	 */
	public void store(int itemNumber) {
		jEdit.setProperty(ImportGroupOption.IMPORT_GROUP_VALUE_PREFIX + ".list." + itemNumber + ".type", "package");
		jEdit.setProperty(ImportGroupOption.IMPORT_GROUP_VALUE_PREFIX + ".list." + itemNumber + ".value", packagePattern);
	}

	/**
	 * Set the string that will identify the package that we are trying to find
	 * in the list of import statements.
	 *
	 * @param packagePattern a <code>String</code> value that contains the package
	 * that we are going to try to identify.
	 * @see #getPackagePattern
	 */
	public void setPackagePattern(String packagePattern) {
		int lastDotIndex = packagePattern.lastIndexOf(".");
		if (lastDotIndex > 0)
		{
			String tail = packagePattern.substring(lastDotIndex+1).trim();

			if (tail.equals("*"))
			{
				this.packagePattern = packagePattern.substring(0, lastDotIndex);
			}
			else
			{
				this.packagePattern = packagePattern.trim();
			}
		}
		else
		{
			this.packagePattern = packagePattern.trim();
		}
	}

	/**
	 * Get the <code>String</code> that identifies the package that we are going
	 * to sort.
	 *
	 * @return a <code>String</code> containing the package we are going to sort.
	 * @see #setPackagePattern
	 */
	public String getPackagePattern() {
		return packagePattern;
	}
}
