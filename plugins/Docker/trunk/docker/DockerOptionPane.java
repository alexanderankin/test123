/*
 * DockerOptionPane.java
 * :tabSize=3:indentSize=3:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2002 Calvin Yu
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

package docker;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.gui.DockableWindowManager;

/**
 * Option pane for the docker plugin.
 */
public class DockerOptionPane extends AbstractOptionPane
{

   private JCheckBox topEnabled, leftEnabled, bottomEnabled, rightEnabled;

   /**
    * Create a new <code>DockerOptionPane</code>
    */
   public DockerOptionPane()
   {
      super("docker");
   }

   /**
    * Initialize this option pane.
    */
   public void _init()
   {
      JPanel panel = new JPanel(new GridBagLayout());
      panel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createTitledBorder(DockerPlugin.getProperty("label.auto-hide-enabled")),
         BorderFactory.createEmptyBorder(12, 12, 11, 11)));

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;

      topEnabled = new JCheckBox(DockerPlugin.getProperty("label.top-dock"));
      topEnabled.setSelected(DockerPlugin.getPlugin().isEnabled(DockableWindowManager.TOP));
      gbc.gridy = 0;
      panel.add(topEnabled, gbc);

      leftEnabled = new JCheckBox(DockerPlugin.getProperty("label.left-dock"));
      leftEnabled.setSelected(DockerPlugin.getPlugin().isEnabled(DockableWindowManager.LEFT));
      gbc.gridy++;
      panel.add(leftEnabled, gbc);
      
      bottomEnabled = new JCheckBox(DockerPlugin.getProperty("label.bottom-dock"));
      bottomEnabled.setSelected(DockerPlugin.getPlugin().isEnabled(DockableWindowManager.BOTTOM));
      gbc.gridy++;
      panel.add(bottomEnabled, gbc);

      rightEnabled = new JCheckBox(DockerPlugin.getProperty("label.right-dock"));
      rightEnabled.setSelected(DockerPlugin.getPlugin().isEnabled(DockableWindowManager.RIGHT));
      gbc.gridy++;
      panel.add(rightEnabled, gbc);

      addComponent(panel);
   }

   /**
    * Save the option.
    */
   public void _save()
   {
      DockerPlugin.getPlugin().setEnabled(DockableWindowManager.TOP,
                                          topEnabled.isSelected());
      DockerPlugin.getPlugin().setEnabled(DockableWindowManager.LEFT,
                                          leftEnabled.isSelected());
      DockerPlugin.getPlugin().setEnabled(DockableWindowManager.BOTTOM,
                                          bottomEnabled.isSelected());
      DockerPlugin.getPlugin().setEnabled(DockableWindowManager.RIGHT,
                                          rightEnabled.isSelected());
   }

}

