/*
 * NimRODLnfInstaller.java - Look And Feel plugin
 * Copyright (C) 2004 Nilo J. Gonzalez
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

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.gjt.sp.jedit.AbstractOptionPane;

/**
 * A class for installing the NimROD look and feel
 */
public class NimRODLnfInstaller implements LookAndFeelInstaller {

	public String getName() {
        return "NimROD";		
	}
	
    /**
     * Install a non standard look and feel.
     */
    public void install() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new com.nilo.plaf.nimrod.NimRODLookAndFeel());
        UIManager.put("ClassLoader", com.nilo.plaf.nimrod.NimRODLookAndFeel.class.getClassLoader());
    }

    public AbstractOptionPane getOptionPane() {
        return null;
    }
}

