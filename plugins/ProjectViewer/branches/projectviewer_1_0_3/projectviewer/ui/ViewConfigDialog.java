/*
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
package projectviewer.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.gjt.sp.jedit.View;


/**
 * A dialog for configuring a {@link FileView}.
 */
public class ViewConfigDialog extends JDialog implements ActionListener {

   private boolean ok;
   private Component configComponent;
   private JButton okButton, cancelButton;

   /**
    * Create a new <code>ViewConfigDialog</code>.
    */
   public ViewConfigDialog(Frame owner, String title, Component aConfigComponent) {
      super(owner, title, true);
      configComponent = aConfigComponent;
      ok = false;
      initComponents();
   }

   /**
    * Returns <code>true</code> if the user ok'd this dialog.
    */
   public boolean isOk() {
      return ok;
   }

   /**
    * Handle button actions.
    */
   public void actionPerformed(ActionEvent evt) {
      if (evt.getSource() == okButton) ok = true;
      dispose();
   }

   /**
    * Initialize components.
    */
   private void initComponents() {
      JPanel pane = new JPanel(new GridBagLayout());
      pane.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
      GridBagConstraints gbc = new GridBagConstraints();
      
      gbc.anchor = gbc.WEST;
      gbc.fill = gbc.BOTH;
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 1;
      gbc.weighty = .9;
      gbc.insets = new Insets(0, 0, 17, 0);
      pane.add(configComponent, gbc);

      okButton = new JButton("OK");
      cancelButton = new JButton("Close");
      okButton.addActionListener(this);
      cancelButton.addActionListener(this);
      JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
      buttons.add(okButton);
      buttons.add(Box.createHorizontalStrut(5));
      buttons.add(cancelButton);
      buttons.add(Box.createHorizontalStrut(5));

      gbc.anchor = gbc.EAST;
      gbc.fill = gbc.HORIZONTAL;
      gbc.gridy = 1;
      gbc.weighty = .1;
      gbc.insets = new Insets(0, 0, 0, 0);
      pane.add(buttons, gbc);

      setContentPane(pane);
      pack();
   }

}
