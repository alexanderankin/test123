/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:
*/ 
package optional;

import java.awt.Frame;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.options.BufferOptionPane;

// {{{ class CombinedOptions
/**
 * 
 * An OptionDialog which combines all of jEdit's options into 3 tabs on a single
 * dialog.
 * 
 * @author ezust
 * 
 */

public class CombinedOptions extends TabbedOptionDialog
{
    
	// {{{ Members

	GlobalOptionGroup globalOptions;

	PluginOptionGroup pluginOptions;

	BufferOptionPane bufferOptions;

	int startingIndex = 0;

	// }}}
    
	// {{{ CombinedOptions Constructors
	public CombinedOptions(Frame parent, int tabIndex)
	{
		super(parent, "Combined Options");
		startingIndex = tabIndex;
		_init();

	}

	public CombinedOptions(Frame parent)
	{
		this(parent, 0);
	} // }}}

	// {{{ _init() method
	public void _init()
	{
		String title = jEdit.getProperty("options.title");
		setTitle(title);
		addOptionGroup(new GlobalOptionGroup());
		addOptionGroup(new PluginOptionGroup());
		addOptionPane(new BufferOptionPane());
		setSelectedIndex(startingIndex);
		setVisible(true);
	}// }}}

}// }}}

