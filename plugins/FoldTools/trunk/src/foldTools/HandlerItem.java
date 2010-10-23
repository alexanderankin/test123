package foldTools;

class HandlerItem
{
	public String name;
	public String [] modes;
	public String desc;
	public HandlerItem(String name, String [] modes)
	{
		this.name = name;
		this.modes = modes;
		StringBuilder sb = new StringBuilder(name + " - ");
		for (int i = 0; i < modes.length; i++)
		{
			if (i > 0)
				sb.append(",");
			sb.append(modes[i]);
		}
		desc = sb.toString();
	}
	public String toString()
	{
		return desc;
	}
}