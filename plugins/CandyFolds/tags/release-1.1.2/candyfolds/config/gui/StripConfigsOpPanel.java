/* % [{
% (C) Copyright 2008 Nicolas Carranza and individual contributors.
% See the CandyFolds-copyright.txt file in the CandyFolds distribution for a full
% listing of individual contributors.
%
% This file is part of CandyFolds.
%
% CandyFolds is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation, either version 3 of the License,
% or (at your option) any later version.
%
% CandyFolds is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License
% along with CandyFolds.  If not, see <http://www.gnu.org/licenses/>.
% }] */
package candyfolds.config.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import candyfolds.config.StripConfig;
import candyfolds.config.ModeConfig;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

class StripConfigsOpPanel {
	final JToolBar panel=new JToolBar();
	private final StripConfigsTable stripConfigsTable;
	final Action[] actions=new Action[]{
	      new AbstractAction("Add", GUIUtilities.loadIcon("Plus.png")) {
		      {
			      putValue(Action.SHORT_DESCRIPTION, jEdit.getProperty("common.add"));
		      }
		      @Override
		      public void actionPerformed(ActionEvent ev) {
			      if(getModeConfig()!=null) {
				      getModeConfig().addStripConfig();
				      stripConfigsTable.fireTableDataChanged();
			      }
		      }
	      },
	      new AbstractAction("Remove", GUIUtilities.loadIcon("Minus.png")) {
					{
			      putValue(Action.SHORT_DESCRIPTION, jEdit.getProperty("common.remove"));
		      }
		      @Override
		      public void actionPerformed(ActionEvent ev) {
			      int row=stripConfigsTable.getSelectedIndex();
			      if(row<0)
				      return;
			      if(getModeConfig()!=null) {
				      getModeConfig().removeStripConfig(row);
				      stripConfigsTable.fireTableDataChanged();
			      }
		      }
	      },
	      new AbstractAction("Move Up", GUIUtilities.loadIcon("ArrowU.png")) {
					{
			      putValue(Action.SHORT_DESCRIPTION, jEdit.getProperty("common.moveUp"));
		      }
		      @Override
		      public void actionPerformed(ActionEvent ev) {
			      int row=stripConfigsTable.getSelectedIndex();
			      if(row<0)
				      return;
			      if(getModeConfig()!=null) {
				      if(getModeConfig().moveUp(row)) {
					      stripConfigsTable.fireTableDataChanged();
					      stripConfigsTable.setSelectedRow(row-1);
				      }
			      }
		      }
	      },
	      new AbstractAction("Move Down", GUIUtilities.loadIcon("ArrowD.png")) {
					{
			      putValue(Action.SHORT_DESCRIPTION, jEdit.getProperty("common.moveDown"));
		      }
		      @Override
		      public void actionPerformed(ActionEvent ev) {
			      int row=stripConfigsTable.getSelectedIndex();
			      if(row<0)
				      return;
			      if(getModeConfig()!=null) {
				      if(getModeConfig().moveDown(row)) {
					      stripConfigsTable.fireTableDataChanged();
					      stripConfigsTable.setSelectedRow(row+1);
				      }
			      }
		      }
	      },
	    };

	{
		//BoxLayout l=new BoxLayout(panel, BoxLayout.X_AXIS);
		//panel.setLayout(l);
		panel.setRollover(true);
		panel.setBorderPainted(false);
		panel.setFloatable(false);

		for(int i=0; i<actions.length; i++){
			JButton b=panel.add(actions[i]);
			b.setText(null);
		}
	}

	StripConfigsOpPanel(StripConfigsTable stripConfigsTable) {
		this.stripConfigsTable=stripConfigsTable;
	}

	ModeConfig getModeConfig() {
		return stripConfigsTable.getModeConfig();
	}

	void setEnabled(boolean enabled) {
		for(int i=actions.length; --i>=0;)
			actions[i].setEnabled(enabled);
	}
}
