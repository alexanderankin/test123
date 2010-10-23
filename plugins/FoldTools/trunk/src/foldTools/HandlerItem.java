package foldTools;

import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;

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
	public void save(String propBase)
	{
		jEdit.setProperty(propBase + "name", name);
		jEdit.setIntegerProperty(propBase + "nModes", modes.length);
		for (int i = 0; i < modes.length; i++)
		{
			String mode = modes[i];
			jEdit.setProperty(propBase + "mode." + i, mode);
			if (FoldingModeTypes.getModeType(mode) == FoldingModeTypes.Unknown)
			{
				int type = FoldingModeTypes.askModeType(mode);
				if (type != FoldingModeTypes.Unknown)
					FoldingModeTypes.setModeType(mode, type);
			}
		}
	}
	public static HandlerItem load(String propBase)
	{
		String name = jEdit.getProperty(propBase + "name");
		if (name == null || name.length() == 0)
			return null;
		int nModes = jEdit.getIntegerProperty(propBase + "nModes");
		if (nModes == 0)
			return null;
		String [] modes = new String[nModes];
		for (int i = 0; i < nModes; i++)
		{
			modes[i] = jEdit.getProperty(propBase + "mode." + i);
			if (modes[i] == null || modes[i].length() == 0)
				return null;
		}
		return new HandlerItem(name, modes);
	}
	public void createService()
	{
		StringBuilder code = new StringBuilder();
		code.append("FoldHandler [] handlers = new FoldHandler[" + modes.length + "];\n");
		for (int i = 0; i < modes.length; i++)
		{
			code.append("handlers[" + i + "] = FoldHandler.getFoldHandler(\"" +
				modes[i] + "\");\n");
		}
		code.append("new foldTools.CompositeFoldHandler(handlers);\n");
		PluginJAR plugin = jEdit.getPlugin(Plugin.class.getCanonicalName()).getPluginJAR();
		ServiceManager.registerService(Plugin.FOLD_HANDLER_SERVICE, name,
			code.toString(), plugin);
	}
}