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

import candyfolds.config.ModeConfig;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.gjt.sp.util.Log;

class ModeConfigPanel {

	private static final Logger L=Logger.getLogger(ModeConfigPanel.class.getName());
	//static { L.setLevel(Level.ALL); }

	final JPanel panel=new JPanel(new BorderLayout());
	private ModeConfig modeConfig;
	final JCheckBox enabledCB=new JCheckBox("Enable CandyFolds for this mode");
	final JCheckBox useIgnoreLineRegexCB=new JCheckBox("Ignore lines matching regex:");
	final JTextField ignoreLineRegexTF=new JTextField(12);
	final JCheckBox showStripOn0IndentCB=new JCheckBox("Show indentation-guide on left edge");
	final JCheckBox drawThinStripesCB=new JCheckBox("Thinner stripes by factor:");
	final JTextField drawThinStripesFactorTF=new JTextField(4);
	final JCheckBox useThinStripesPixelSizeCB=new JCheckBox("Use always 1 pixel");
	private final StripConfigsTable stripConfigsTable=new StripConfigsTable();
	private final JPanel tablePanel=new JPanel(new BorderLayout());
	private final StripConfigsOpPanel stripConfigsOpPanel=new StripConfigsOpPanel(stripConfigsTable);

	{
		JScrollPane sp=new JScrollPane(stripConfigsTable.table);
		sp.setPreferredSize(new Dimension(10, 10));
		sp.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		tablePanel.add(sp);
		tablePanel.add(stripConfigsOpPanel.panel, BorderLayout.SOUTH);
		//tablePanel.setBorder(BorderFactory.createTitledBorder("Candies"));
		panel.add(tablePanel);

		Box box=new Box(BoxLayout.Y_AXIS);
		enabledCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				if(modeConfig!=null)
					modeConfig.setEnabled(enabledCB.isSelected());
				updateView();
			}
		}
								   );
		box.add(enabledCB);
		Box p=new Box(BoxLayout.X_AXIS);
		p.setAlignmentX(0);
		useIgnoreLineRegexCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				boolean useIgnoreLineRegex=useIgnoreLineRegexCB.isSelected();
				if(modeConfig!=null)
					modeConfig.setUseIgnoreLineRegex(useIgnoreLineRegex);
				updateView();
				if(useIgnoreLineRegex) {
					if(ignoreLineRegexTF.getText().trim().length()==0) {
						ignoreLineRegexTF.setText("\\s*\\S\\s*$");
						ignoreLineRegexTF.selectAll();
					}
					ignoreLineRegexTF.requestFocus();
				}
			}
		});
		p.add(useIgnoreLineRegexCB);
		ignoreLineRegexTF.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent ev) {
				updateModeConfigIgnoreLineRegex();
				updateView();
			}
		});
		p.add(ignoreLineRegexTF);
		box.add(p);
		showStripOn0IndentCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				if(modeConfig!=null)
					modeConfig.setShowStripOn0Indent(showStripOn0IndentCB.isSelected());
				updateView();
			}
		});
		box.add(showStripOn0IndentCB);
		p=new Box(BoxLayout.X_AXIS);
		p.setAlignmentX(0);
		drawThinStripesCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				if(modeConfig!=null)
					modeConfig.setDrawThinStripes(drawThinStripesCB.isSelected());
				updateView();
			}
		});
		p.add(drawThinStripesCB);
		drawThinStripesFactorTF.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent ev) {
				updateModeConfigDrawThinStripesFactor();
				updateView();
			}
		});
		p.add(drawThinStripesFactorTF);
		useThinStripesPixelSizeCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				if(modeConfig!=null)
					modeConfig.setUseThinStripesPixelSize(useThinStripesPixelSizeCB.isSelected());
				updateView();
			}
		});
		p.add(useThinStripesPixelSizeCB);
		box.add(p);
		panel.add(box, BorderLayout.NORTH);

		updateView();
	}

	void setModeConfig(ModeConfig modeConfig) {
		this.modeConfig=modeConfig;
		stripConfigsTable.setModeConfig(modeConfig);
		updateView();
	}

	private void updateModeConfigIgnoreLineRegex() {
		if(modeConfig!=null)
			modeConfig.ignoreLineRegex.setValue(ignoreLineRegexTF.getText());
	}

	private void updateModeConfigDrawThinStripesFactor() {
		if(modeConfig!=null) {
			String factorS=drawThinStripesFactorTF.getText();
			try {
				float newFactor=Float.valueOf(factorS);
				modeConfig.setDrawThinStripesFactor(newFactor);
			} catch(NumberFormatException ex) {	}
		}
	}

	private void updateView() {
		if(modeConfig!=null) {
			enabledCB.setSelected(modeConfig.getEnabled());
			boolean isDefault=modeConfig!=modeConfig.config.defaultModeConfig;
			enabledCB.setVisible(isDefault);
			useIgnoreLineRegexCB.setSelected(modeConfig.getUseIgnoreLineRegex());
			ignoreLineRegexTF.setText(modeConfig.ignoreLineRegex.getValue());
			showStripOn0IndentCB.setSelected(modeConfig.getShowStripOn0Indent());
			drawThinStripesCB.setSelected(modeConfig.getDrawThinStripes());
			drawThinStripesFactorTF.setText(Float.toString(modeConfig.getDrawThinStripesFactor()));
			useThinStripesPixelSizeCB.setSelected(modeConfig.getUseThinStripesPixelSize());
			stripConfigsOpPanel.panel.setVisible(
				modeConfig!=modeConfig.config.defaultModeConfig);
		}
		useIgnoreLineRegexCB.setEnabled(enabledCB.isSelected());
		ignoreLineRegexTF.setEnabled(enabledCB.isSelected() && useIgnoreLineRegexCB.isSelected());
		showStripOn0IndentCB.setEnabled(enabledCB.isSelected());
		drawThinStripesCB.setEnabled(enabledCB.isSelected());
		drawThinStripesFactorTF.setEnabled(enabledCB.isSelected() && drawThinStripesCB.isSelected() && !useThinStripesPixelSizeCB.isSelected());
		useThinStripesPixelSizeCB.setEnabled(enabledCB.isSelected() && drawThinStripesCB.isSelected());
		setEnabledStripConfigs(enabledCB.isSelected());
	}

	private void setEnabledStripConfigs(boolean enabled) {
		stripConfigsTable.table.setEnabled(enabled);
		stripConfigsOpPanel.setEnabled(enabled);
	}

	void save() {
		updateModeConfigIgnoreLineRegex();
		stripConfigsTable.save();
	}
}