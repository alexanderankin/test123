package ctags.sidekick.renderers;

import java.net.URL;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import org.gjt.sp.jedit.jEdit;

import ctags.sidekick.AbstractObjectProcessor;
import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.Tag;
import ctags.sidekick.options.GeneralOptionPane;

public class KindIconProvider extends AbstractObjectProcessor implements
		IIconProvider {

	static final String NAME = "Tag kind icon";
	static final String DESCRIPTION = "Provides icons denoting tag kind";
	
	static Hashtable<String, ImageIcon> icons =
		new Hashtable<String, ImageIcon>();
	
	public KindIconProvider() {
		super(NAME, DESCRIPTION);
	}

	public ImageIcon getIcon(Tag tag) {
		String kind = tag.getKind();
		String iconName =
			jEdit.getProperty(GeneralOptionPane.ICONS + kind);
		if (iconName == null || iconName.length() == 0)
			iconName = "unknown.png";
		ImageIcon icon = (ImageIcon) icons.get(kind);
		if (icon == null)
		{
			URL url = Tag.class.getClassLoader().getResource(
					"icons/" + iconName);
	        try {
	            icon = new ImageIcon(url);
	        }
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
			if (icon != null)
				icons.put(kind, icon);
		}
		if (icon != null)
			return icon;
		return null;
	}

	public IObjectProcessor getClone() {
		return new KindIconProvider();
	}

}
