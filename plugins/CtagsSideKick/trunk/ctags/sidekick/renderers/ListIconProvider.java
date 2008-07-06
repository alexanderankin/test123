package ctags.sidekick.renderers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.ImageIcon;

import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.ListObjectProcessor;
import ctags.sidekick.Tag;

public class ListIconProvider extends ListObjectProcessor implements
		IIconProvider {

	private static final String NAME = "Composite";
	private static final String DESCRIPTION =
		"A list of icon providers, each appending its own icon.";
	
	public ListIconProvider() {
		super(NAME, DESCRIPTION);
	}
	public ImageIcon getIcon(Tag tag) {
		Vector<IObjectProcessor> processors = getProcessors();
		int width = 0;
		int height = 0;
		Vector<ImageIcon> icons = new Vector<ImageIcon>();
		for (int i = 0; i < processors.size(); i++) {
			ImageIcon icon = ((IIconProvider)processors.get(i)).getIcon(tag);
			if (icon == null)
				continue;
			icons.add(icon);
			width += icon.getIconWidth();
			height = Math.max(height, icon.getIconHeight());
		}
		if (icons.size() == 0)
			return null;
		if (icons.size() == 1)
			return icons.get(0);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();
		int x = 0;
		for (int i = 0; i < icons.size(); i++) {
			ImageIcon icon = icons.get(i);
			icon.paintIcon(null, g, x, 0);
			x += icon.getIconWidth();
		}
		g.dispose();
		return new ImageIcon(image);		
	}

	public IObjectProcessor getClone() {
		return new ListIconProvider();
	}

}
