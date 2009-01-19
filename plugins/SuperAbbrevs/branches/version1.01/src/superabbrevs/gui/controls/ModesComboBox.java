package superabbrevs.gui.controls;

import java.util.SortedSet;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import superabbrevs.ModeService;

public class ModesComboBox extends JComboBox {

	private final ModeService modeService;

	public ModesComboBox(ModeService modeService) {
		this.modeService = modeService;
	}

	public void bind() {
		SortedSet<String> modesNames = modeService.getModesNames();
		ComboBoxModel model = new DefaultComboBoxModel(modesNames.toArray());
		setModel(model);
        setSelectedItem(modeService.getCurrentModeName());	
	}

	@Override
	public String getSelectedItem() {
		return (String) super.getSelectedItem();
	}
}
