package astyleplugin;

import org.gjt.sp.jedit.options.BeanOptionPane;

public class AStyleOptionPane extends BeanOptionPane
{
	public AStyleOptionPane() {
		super("astyleplugin", "astyleplugin.Formatter", AStylePlugin.class.getClassLoader());
	}



}
