/*
 * RFC.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Matthieu Casanova
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
package gatchan.jedit.rfcreader;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class RFC
{
	private int number;
	private String title;
	private List<Integer> obsoletes;
	private List<Integer> obsoletedBy;
	private List<Integer> updates;
	private List<Integer> updatedBy;
	private String also;
	private String status;
	private String date;

	public int getNumber()
	{
		return number;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public List<Integer> getObsoletes()
	{
		return obsoletes;
	}

	public void setObsoletes(List<Integer> obsoletes)
	{
		this.obsoletes = obsoletes;
	}

	public List<Integer> getObsoletedBy()
	{
		return obsoletedBy;
	}

	public void setObsoletedBy(List<Integer> obsoletedBy)
	{
		this.obsoletedBy = obsoletedBy;
	}

	public List<Integer> getUpdates()
	{
		return updates;
	}

	public void setUpdates(List<Integer> updates)
	{
		this.updates = updates;
	}

	public List<Integer> getUpdatedBy()
	{
		return updatedBy;
	}

	public void setUpdatedBy(List<Integer> updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	public String getAlso()
	{
		return also;
	}

	public void setAlso(String also)
	{
		this.also = also;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String toString()
	{
		return number + " " + title;
	}

	@Override
	public int hashCode()
	{
		return number;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj instanceof RFC)
		{
			RFC rfc = (RFC) obj;
			return rfc.number == number;
		}
		return false;
	}
}
