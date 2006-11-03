/*
 *  WhiteSpaceGroupItem.java -
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

import java.util.*;

import org.gjt.sp.jedit.jEdit;

import jimporter.importer.*;

/**
 * An empty line in the import statement list.
 *
 * @author Byron Hawkins
 * @since 0.6
 */

public class ImportGroupList extends ArrayList
{
	protected Map packageGroups;
	protected List others;

	public ImportGroupList()
	{
		super();

		this.packageGroups = new TreeMap();
		this.others = new ArrayList();
	}

	public void add(ImportGroupItem groupItem)
	{
		if (groupItem instanceof PackageGroupItem)
		{
			String packagePattern = ((PackageGroupItem)groupItem).getPackagePattern();

			if (packagePattern.length() == 0)
			{
				return;
			}

			this.packageGroups.put(packagePattern, new PackageGroup(packagePattern));
		}

		super.add(groupItem);
	}

	public synchronized List getDisplayList(List importList) // List<ImportItem>
	{
		Iterator packageGroupIterator = this.packageGroups.values().iterator();
		while (packageGroupIterator.hasNext())
		{
			((PackageGroup)packageGroupIterator.next()).clear();
		}
		this.others.clear();

		Iterator items = importList.iterator();
	items:
		while (items.hasNext())
		{
			ImportItem nextItem = (ImportItem)items.next();

			Iterator groupItems = super.iterator();
			while (groupItems.hasNext())
			{
				ImportGroupItem groupItem = (ImportGroupItem)groupItems.next();

				if (groupItem instanceof PackageGroupItem)
				{
					PackageGroup group = (PackageGroup)this.packageGroups.get(((PackageGroupItem)groupItem).getPackagePattern());
					if (group.add(nextItem))
					{
						continue items;
					}
				}
			}

			this.others.add(nextItem);
		}

		Iterator groupItems = super.iterator();
		List displayList = new ArrayList();

		while (groupItems.hasNext())
		{
			ImportGroupItem groupItem = (ImportGroupItem)groupItems.next();

			if (groupItem instanceof PackageGroupItem)
			{
				PackageGroup group = (PackageGroup)this.packageGroups.get(((PackageGroupItem)groupItem).getPackagePattern());

				displayList.addAll(group.items);
			}
			else if (groupItem instanceof AllOtherImportsItem)
			{
				displayList.addAll(this.others);
			}
			else if (groupItem instanceof WhiteSpaceGroupItem)
			{
				displayList.add(null);
			}
		}

		return displayList;
	}

	protected class PackageGroup
	{
		protected String packageName;

		protected List items;

		public PackageGroup(String packageName)
		{
			this.packageName = packageName;

			this.items = new ArrayList();
		}

		public void clear()
		{
			this.items.clear();
		}

		public boolean add(ImportItem item)
		{
			String itemPackageName = item.getPackageName();

			if (itemPackageName.startsWith(this.packageName))
			{
				this.items.add(item);
				return true;
			}
			else if (item.getShortClassName().equals(this.packageName)) // kinda shady here
			{
				this.items.add(item);
				return true;
			}

			return false;
		}
	}
}

