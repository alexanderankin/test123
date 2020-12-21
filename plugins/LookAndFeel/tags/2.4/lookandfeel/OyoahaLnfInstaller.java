/*
 * OyoahaLnfInstaller.java - Look And Feel plugin
 * Copyright (C) 2002 Calvin Yu
 *
 * :mode=java:tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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
package lookandfeel;

import com.oyoaha.swing.plaf.oyoaha.*;
import java.io.File;
import javax.swing.UIManager;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Installs the Oyoaha look and feel. See <a href="http://www.l2fprod.com">
 * http://www.l2fprod.com</a> .
 */
public class OyoahaLnfInstaller implements LookAndFeelInstaller
{

	public final static String OYOAHA_THEME_PROP = "lookandfeel.oyoaha.oyoaha-theme";
	public final static String METAL_THEME_PROP = "lookandfeel.oyoaha.metal-theme";

	public String getName() {
		return "Oyoaha";		
	}
	
	/**
	 * Install a non standard look and feel.
	 */
	public void install() throws UnsupportedLookAndFeelException
	{
		String metalTheme = jEdit.getProperty(METAL_THEME_PROP);
		String oyoahaTheme = jEdit.getProperty(OYOAHA_THEME_PROP);
		if (!LookAndFeelPlugin.isEmpty(metalTheme)) {
			OyoahaLookAndFeel.setCurrentTheme(new File(metalTheme));
		}
		OyoahaLookAndFeel lnf = new OyoahaLookAndFeel();
		lnf.setOyoahaTheme(new File(oyoahaTheme));
		UIManager.setLookAndFeel(lnf);
	}

	/**
	 * Returns a component used to configure the look and feel.
	 */
	public AbstractOptionPane getOptionPane() {
		return new OptionComponent();	
	}

	/**
	 * The configuration component.
	 */
	class OptionComponent extends AbstractOptionPane
	{
		private PathComponent metalTheme;
		private PathComponent oyoahaTheme;

		/**
		 * Create a new <code>OptionComponent</code>.
		 */
		public OptionComponent()
		{
			super("Oyoaha");
			init();
		}

		/**
		 * Layout this component.
		 */
		public void _init()
		{
			metalTheme = new PathComponent(METAL_THEME_PROP, false);
			addComponent(jEdit.getProperty(METAL_THEME_PROP + ".label"), metalTheme);
			addComponent(GUIUtils.createLinkLabelComponent(
				jEdit.getProperty(METAL_THEME_PROP + ".link.label"),
				jEdit.getProperty(METAL_THEME_PROP + ".link.href")));

			oyoahaTheme = new PathComponent(OYOAHA_THEME_PROP, true);
			addComponent(jEdit.getProperty(OYOAHA_THEME_PROP + ".label"), oyoahaTheme);
			addComponent(GUIUtils.createLinkLabelComponent(
				jEdit.getProperty(OYOAHA_THEME_PROP + ".link.label"),
				jEdit.getProperty(OYOAHA_THEME_PROP + ".link.href")));
		}

		/**
		 * Save this configuration.
		 */
		public void _save()
		{
			if (metalTheme.assertPath(true)) {
				if (metalTheme.isPathEmpty()) {
					jEdit.unsetProperty(METAL_THEME_PROP);
				} else {
					jEdit.setProperty(METAL_THEME_PROP, metalTheme.getPath());
				}
			}
			if (oyoahaTheme.assertPath(false)) {
				jEdit.setProperty(OYOAHA_THEME_PROP, oyoahaTheme.getPath());
			}
		}
	}

}

