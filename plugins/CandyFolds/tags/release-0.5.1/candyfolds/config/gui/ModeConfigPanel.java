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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import candyfolds.config.ModeConfig;

class ModeConfigPanel {
	final JPanel panel=new JPanel(new BorderLayout());
	private ModeConfig modeConfig;
	final JCheckBox enabledCB=new JCheckBox("Enable CandyFolds for this mode");
	private final FoldConfigsTable foldConfigsTable=new FoldConfigsTable();
	private final JPanel tablePanel=new JPanel(new BorderLayout());
	private final FoldConfigsOpPanel foldConfigsOpPanel=new FoldConfigsOpPanel(foldConfigsTable);

	{
		JScrollPane sp=new JScrollPane(foldConfigsTable.table);
		sp.setPreferredSize(new Dimension(10, 10));
		sp.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		tablePanel.add(sp);
		tablePanel.add(foldConfigsOpPanel.panel, BorderLayout.SOUTH);
		//tablePanel.setBorder(BorderFactory.createTitledBorder("Candies"));

		panel.add(tablePanel);
		panel.add(enabledCB, BorderLayout.NORTH);

		enabledCB.addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent ev) {
				    if(modeConfig!=null)
					    modeConfig.setEnabled(enabledCB.isSelected());
				    updateView();
			    }
		    }
		                           );
		updateView();
	}

	void setModeConfig(ModeConfig modeConfig) {
		this.modeConfig=modeConfig;
		foldConfigsTable.setModeConfig(modeConfig);
		updateView();
	}

	private void updateView() {
		if(modeConfig!=null) {
			enabledCB.setSelected(modeConfig.getEnabled());
			enabledCB.setVisible(modeConfig!=modeConfig.config.defaultModeConfig);
			foldConfigsOpPanel.panel.setVisible(
			  modeConfig!=modeConfig.config.defaultModeConfig);
		}
		setEnabledFoldConfigs(enabledCB.isSelected());
	}

	private void setEnabledFoldConfigs(boolean enabled) {
		foldConfigsTable.table.setEnabled(enabled);
		foldConfigsOpPanel.setEnabled(enabled);
	}
}
