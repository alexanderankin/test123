
/*
 * SkinLnfInstaller.java - Look And Feel plugin
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


import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;

import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


/**
 * Installs the skin look and feel.  See <a href="http://www.l2fprod.com">http://www.l2fprod.com</a>.
 */
public class SkinLnfInstaller implements LookAndFeelInstaller {

	public static final String THEMEPACK_PROP = "lookandfeel.skin.themepack";
	public static final String XTRA_SCROLLBARS_PROP = "lookandfeel.skin.xtra-scrollbars";

	public String getName() {
		return "Skin";
	}

	/**
	 * Install a non standard look and feel.
	 */
	public void install() throws UnsupportedLookAndFeelException {
		try {
			Skin theme = SkinLookAndFeel.loadThemePack( jEdit.getProperty( THEMEPACK_PROP ) );
			SkinLookAndFeel.setSkin( theme );
			UIManager.setLookAndFeel( new SkinLookAndFeel() );
			UIManager.put( "ScrollBar.alternateLayout",
			jEdit.getBooleanProperty( XTRA_SCROLLBARS_PROP, false ) ? Boolean.TRUE : null );

			// this is a workaround for the SkinLF; it doesn't find the
			// the UI classes for overridden components
			UIManager.put( org.gjt.sp.jedit.menu.EnhancedMenu.class, new javax.swing.JMenu().getUI() );
			UIManager.put( org.gjt.sp.jedit.menu.EnhancedMenuItem.class, new javax.swing.JMenuItem().getUI() );
			UIManager.put( org.gjt.sp.jedit.menu.EnhancedCheckBoxMenuItem.class, new javax.swing.JCheckBoxMenuItem().getUI() );
		}
		catch ( Exception e ) {
			throw new UnsupportedLookAndFeelException( e.getMessage() );
		}
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
	class OptionComponent extends AbstractOptionPane {

		private PathComponent themePack;
		private JCheckBox xtraScrollbars;

		/**
		 * Create a new <code>OptionComponent</code>.
		 */
		public OptionComponent() {
			super( "Skin" );
			init();
		}

		/**
		 * Layout this component.
		 */
		public void _init() {
			themePack = new PathComponent( THEMEPACK_PROP, true );
			addComponent( jEdit.getProperty( THEMEPACK_PROP + ".label" ), themePack );
			addComponent( GUIUtils.createLinkLabelComponent(
			jEdit.getProperty( THEMEPACK_PROP + ".link.label" ),
			jEdit.getProperty( THEMEPACK_PROP + ".link.href" ) ) );
			xtraScrollbars = new JCheckBox( jEdit.getProperty( XTRA_SCROLLBARS_PROP + ".label" ) );
			if ( jEdit.getBooleanProperty( XTRA_SCROLLBARS_PROP, false ) ) {
				xtraScrollbars.setSelected( true );
			}
			addComponent( xtraScrollbars );
		}

		/**
		 * Save this configuration.
		 */
		public void _save() {
			if ( themePack.assertPath( false ) ) {
				jEdit.setProperty( THEMEPACK_PROP, themePack.getPath() );
			}
			jEdit.setBooleanProperty( XTRA_SCROLLBARS_PROP,
			xtraScrollbars.isSelected() );
		}
	}
}
