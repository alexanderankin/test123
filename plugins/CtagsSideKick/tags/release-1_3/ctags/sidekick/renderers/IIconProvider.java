package ctags.sidekick.renderers;

import javax.swing.ImageIcon;

import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.Tag;

public interface IIconProvider extends IObjectProcessor {
	ImageIcon getIcon(Tag tag);
}
