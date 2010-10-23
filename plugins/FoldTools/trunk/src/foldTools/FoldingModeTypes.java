package foldTools;

import java.util.HashMap;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;

public class FoldingModeTypes
{
	public static final int Unknown = 0;
	public static final int Fixed = 1;
	public static final int NonFixed = 2;

	private static final HashMap<String, Integer> modeTypes;

	static
	{
		modeTypes = new HashMap<String, Integer>();
		//modeTypes.put("comment", Fixed);
		modeTypes.put("sidekick", Fixed);
		modeTypes.put("indent", Fixed);
		modeTypes.put("none", Fixed);
		modeTypes.put("custom", NonFixed);
		modeTypes.put("explicit", NonFixed);
	}
	public static int getModeType(String mode)
	{
		Integer t = modeTypes.get(mode);
		if (t == null)	// If not one of the hard-coded types, try properties
		{
			int i = jEdit.getIntegerProperty(OptionPane.PROP + "type." + mode);
			t = Integer.valueOf(i);
		}
		return t.intValue();
	}
	public static void setModeType(String mode, int type)
	{
		jEdit.setIntegerProperty(OptionPane.PROP + "type." + mode, type);
	}
	public static int askModeType(String mode)
	{
		String [] options = { "Unknown", "Fixed", "Non-fixed" };
		int sel = JOptionPane.showOptionDialog(jEdit.getActiveView(),
			"Type of folding mode '" + mode + "':", "Unknown folding mode type",
			JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
			options, options[0]);
		if (sel == JOptionPane.CLOSED_OPTION)
			return Unknown;
		return sel;
	}
}
